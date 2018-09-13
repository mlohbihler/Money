package com.serotonin.money.util;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import com.serotonin.money.vo.AssetInfo;
import com.serotonin.util.StringUtils;
import com.serotonin.web.http.HttpUtils4;

import lohbihler.atomicjson.JMap;
import lohbihler.atomicjson.JsonReader;

public class Market {
    static final Log LOG = LogFactory.getLog(Market.class);

    //http://www.google.com/finance?q=TSE%3ADR%2CTSE%3Apdv%2Ctse%3Aerf%2Ctse%3Aarx%2Ctse%3Ahej%2Ctse%3Ahep
    static String pattern1 = "\"${symbol}\",\".*?\",\"([\\d\\.]+?)\",\".*?\",\".*?\",\".*?\",\".*?\",\".*?\",\"${exchange}\"";
    static String pattern2 = "\\{u:\".*?${symbol}\".*?,p:\"([\\d\\.]+?)\"";
    static String pattern3a = "\\[\"${symbol}\",(.*?),\"${exchange}\",.*?,\"${symbol}\"";
    static String pattern3b = "\".*?\",\"([\\d\\.]+?)\",";
    //https://ca.finance.yahoo.com/quotes/GOOG,F00000TXOD.TO
    static String ysfPattern1 = "<span class=\"streaming-datum\" id=\"yfs_l84_${symbol}\">(.*?)</span>";
    static int maxSymbols = 14;

    static String alphaVantageApiKey = "9I8UTJYRY4757Z3R";

    public static void populateMarketValue(final AssetInfo asset) throws Exception {
        final List<AssetInfo> assets = new ArrayList<>();
        assets.add(asset);
        populateMarketValues(assets);
    }

    public static void populateMarketValues(final List<AssetInfo> assets) throws Exception {
        populateMarketValuesAlphaVantage(assets);

        //        // Just do yahoo
        //        final List<AssetInfo> fails = populateMarketValuesYahoo(assets);
        //        if (!fails.isEmpty()) {
        //            final List<AssetInfo> oneAtATime = new ArrayList<>();
        //            for (final AssetInfo a : fails) {
        //                oneAtATime.add(a);
        //                populateMarketValuesYahoo(oneAtATime);
        //                oneAtATime.clear();
        //            }
        //        }

        //        //assets = getMarketValid(assets);
        //
        //        // Try Google
        //        List<AssetInfo> fails = new ArrayList<AssetInfo>();
        //        while (!assets.isEmpty()) {
        //            if (assets.size() <= maxSymbols) {
        //                fails.addAll(populateMarketValuesGoogle(assets));
        //                break;
        //            }
        //
        //            List<AssetInfo> group = new ArrayList<AssetInfo>();
        //            for (int i = 0; i < maxSymbols; i++)
        //                group.add(assets.remove(0));
        //            fails.addAll(populateMarketValuesGoogle(group));
        //        }
        //
        //        if (!fails.isEmpty())
        //            // Try Yahoo
        //            populateMarketValuesYahoo(fails);
    }

    static void populateMarketValuesAlphaVantage(final List<AssetInfo> assets) throws Exception {
        final List<HttpHost> proxies = new LinkedList<>();
        proxies.add(null);

        try (CloseableHttpClient client = createHttpClient(null)) {
            final String uri = "https://www.sslproxies.org/";
            final String content = HttpUtils4.getTextContent(client, uri);

            final Pattern pattern = Pattern.compile("<tr><td>(\\d+.\\d+.\\d+.\\d+)<\\/td><td>(\\d+)<\\/td>.*?<\\/tr>");
            final Matcher matcher = pattern.matcher(content);
            while (matcher.find()) {
                proxies.add(new HttpHost(matcher.group(1), Integer.parseInt(matcher.group(2))));
            }
        }

        final List<Future<Void>> futures = new ArrayList<>();
        final ExecutorService executor = Executors.newFixedThreadPool(20);
        final ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(0);
        for (final AssetInfo asset : assets) {
            futures.add(executor.submit(() -> {
                final String symbol = getSymbolToUse(asset);
                final StringBuilder sb = new StringBuilder();
                sb.append("https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=");
                sb.append(symbol);
                sb.append("&apikey=").append("alphaVantageApiKey");

                while (true) {
                    boolean empty = false;
                    HttpHost proxy = null;
                    synchronized (proxies) {
                        if (proxies.isEmpty()) {
                            empty = true;
                        } else {
                            proxy = proxies.remove(0);
                        }
                    }

                    if (empty) {
                        LOG.warn(symbol + ": Waiting for proxy");
                        Thread.sleep(500);
                    } else {
                        try (CloseableHttpClient client = createHttpClient(proxy)) {
                            final HttpGet get = new HttpGet(sb.toString());
                            final ScheduledFuture<?> aborter = scheduledExecutor.schedule(() -> get.abort(), 11,
                                    TimeUnit.SECONDS);
                            try (CloseableHttpResponse res = client.execute(get)) {
                                if (res.getStatusLine().getStatusCode() != 200) {
                                    throw new IOException("Bad response code: " + res.getStatusLine().getStatusCode());
                                }
                                final String content = IOUtils.toString(res.getEntity().getContent());
                                final JMap quote = new JsonReader(content).read();

                                if (!quote.containsKey("Global Quote")) {
                                    LOG.warn(symbol + ": No quote, content=" + content);
                                } else {
                                    final String price = quote.getStringByPath("Global Quote", "05. price");
                                    if (price != null) {
                                        asset.setMarketPrice(Double.parseDouble(price));
                                        asset.setMarketTime(System.currentTimeMillis());
                                    } else {
                                        LOG.warn("Unable to parse market price for " + symbol + ". content=" + content);
                                    }
                                    synchronized (proxies) {
                                        proxies.add(proxy);
                                    }
                                    break;
                                }
                                break;
                            } finally {
                                aborter.cancel(false);
                            }
                        } catch (final Exception e) {
                            LOG.warn(symbol + ": " + e.getMessage());
                        }
                    }
                }

                return null;
            }));
        }

        for (final Future<Void> future : futures) {
            future.get();
        }

        executor.shutdown();
        scheduledExecutor.shutdown();
    }

    static CloseableHttpClient createHttpClient(final HttpHost proxy) {
        final RequestConfig config = RequestConfig.copy(RequestConfig.DEFAULT) //
                .setConnectTimeout(10_000) //
                .setSocketTimeout(10_000) //
                .setConnectionRequestTimeout(10_000) //
                .build();
        final HttpClientBuilder builder = HttpClientBuilder.create() //
                .setDefaultRequestConfig(config);
        if (proxy != null) {
            builder.setProxy(proxy);
        }
        return builder.build();
    }

    static List<AssetInfo> populateMarketValuesGoogle(final List<AssetInfo> assets) throws Exception {
        final StringBuilder sb = new StringBuilder();
        sb.append("http://www.google.com/finance?q=");
        boolean first = true;
        for (final AssetInfo asset : assets) {
            if (first)
                first = false;
            else
                sb.append(URLEncoder.encode(",", "ASCII"));
            sb.append(URLEncoder.encode(getSymbolToUse(asset), "ASCII"));
        }

        final HttpClient httpClient = HttpUtils4.getHttpClient(10000);
        final String content = HttpUtils4.getTextContent(httpClient, sb.toString());

        final List<AssetInfo> fails = new ArrayList<>();
        for (final AssetInfo asset : assets) {
            if (!findPriceGoogle(asset, content))
                fails.add(asset);
        }

        return fails;
    }

    //    private static List<AssetInfo> getMarketValid(List<AssetInfo> assets) {
    //        List<AssetInfo> valid = new ArrayList<AssetInfo>();
    //        for (AssetInfo asset : assets) {
    //            if (Utils.getExchange(getSymbolToUse(asset)) != null)
    //                valid.add(asset);
    //        }
    //        return valid;
    //    }

    private static boolean findPriceGoogle(final AssetInfo asset, final String content) {
        String market = null;

        final String symbolToUse = getSymbolToUse(asset);
        if (Utils.getExchange(symbolToUse) == null)
            return false;

        String p = StringUtils.replaceMacro(pattern3a, "symbol", Utils.getSymbolSansExchange(symbolToUse));
        p = StringUtils.replaceMacro(p, "exchange", Utils.getExchange(symbolToUse));
        Pattern patt = Pattern.compile(p);
        final String[] innerContents = StringUtils.findAllGroup(patt, content);
        //        String innerContent = StringUtils.findGroup(patt, content);
        for (final String innerContent : innerContents) {
            patt = Pattern.compile(pattern3b);
            market = StringUtils.findGroup(patt, innerContent);
            if (market != null) {
                final double price = Double.parseDouble(market);
                if (price > 0) {
                    asset.setMarketPrice(Double.parseDouble(market));
                    asset.setMarketTime(System.currentTimeMillis());
                    break;
                }
            }
        }

        if (market == null) {
            p = StringUtils.replaceMacro(pattern2, "symbol", symbolToUse);
            patt = Pattern.compile(p);

            market = StringUtils.findGroup(patt, content);
            if (market != null) {
                final double price = Double.parseDouble(market);
                if (price > 0) {
                    asset.setMarketPrice(Double.parseDouble(market));
                    asset.setMarketTime(System.currentTimeMillis());
                }
            }
        }

        if (market == null) {
            p = StringUtils.replaceMacro(pattern1, "symbol", Utils.getSymbolSansExchange(symbolToUse));
            p = StringUtils.replaceMacro(p, "exchange", Utils.getExchange(symbolToUse));
            patt = Pattern.compile(p);

            market = StringUtils.findGroup(patt, content);
            if (market != null) {
                final double price = Double.parseDouble(market);
                if (price > 0) {
                    asset.setMarketPrice(Double.parseDouble(market));
                    asset.setMarketTime(System.currentTimeMillis());
                }
            }
        }

        return market != null;
    }

    private static List<AssetInfo> populateMarketValuesYahoo(final List<AssetInfo> assets) throws Exception {
        final StringBuilder sb = new StringBuilder();
        sb.append("https://ca.finance.yahoo.com/quotes/");

        final List<AssetInfo> attempts = new ArrayList<>();
        final List<AssetInfo> fails = new ArrayList<>();

        boolean first = true;
        for (final AssetInfo asset : assets) {
            final String symbol = getSymbolToUseYahoo(asset);
            if (symbol == null)
                fails.add(asset);
            else {
                if (first)
                    first = false;
                else
                    sb.append(URLEncoder.encode(",", "ASCII"));
                sb.append(URLEncoder.encode(symbol, "ASCII"));
                attempts.add(asset);
            }
        }

        if (!first) {
            final HttpClient httpClient = HttpUtils4.getHttpClient(60000);
            try {
                final String content = HttpUtils4.getTextContent(httpClient, sb.toString());
                for (final AssetInfo asset : assets) {
                    if (!findPriceYahoo(asset, content))
                        fails.add(asset);
                }
            } catch (final Exception e) {
                fails.addAll(attempts);
                throw new Exception("Failed on url " + sb, e);
            }
        }

        return fails;
    }

    private static boolean findPriceYahoo(final AssetInfo asset, final String content) {
        String market = null;

        final String symbolToUse = getSymbolToUseYahoo(asset);
        if (symbolToUse == null)
            return false;

        final String p = StringUtils.replaceMacro(ysfPattern1, "symbol", symbolToUse);
        final Pattern patt = Pattern.compile(p, Pattern.CASE_INSENSITIVE);
        market = StringUtils.findGroup(patt, content);
        if (market != null) {
            double price;
            try {
                price = Double.parseDouble(market);
            } catch (final NumberFormatException e) {
                price = 0;
                market = null;
            }
            if (price > 0) {
                asset.setMarketPrice(price);
                asset.setMarketTime(System.currentTimeMillis());
            }
        }

        return market != null;
    }

    private static String getSymbolToUseYahoo(final AssetInfo asset) {
        if (!org.apache.commons.lang3.StringUtils.isEmpty(asset.getMarketSymbol()))
            return asset.getMarketSymbol();

        final String exch = asset.getExchange();
        if (exch == null)
            return null;

        if (exch.equals("NASDAQ") || exch.equals("NYSE") || exch.equals("NYSEARCA"))
            return asset.getSymbolSansExchange();

        if (exch.equals("TSE"))
            return asset.getSymbolSansExchange() + ".TO";

        return asset.getSymbol();
    }

    private static String getSymbolToUse(final AssetInfo asset) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(asset.getMarketSymbol()))
            return asset.getSymbol();
        return asset.getMarketSymbol();
    }

    public static void main(final String[] args) throws Exception {
        final String symbolToUse = "NASDAQ:AGNC";
        String p = StringUtils.replaceMacro(pattern3a, "symbol", Utils.getSymbolSansExchange(symbolToUse));
        p = StringUtils.replaceMacro(p, "exchange", Utils.getExchange(symbolToUse));
        final Pattern patt = Pattern.compile(p);

        final String content = FileUtils.readFileToString(new File("src-test", "content.txt"));

        for (final String s : StringUtils.findAllGroup(patt, content))
            System.out.println(s);
    }
}
