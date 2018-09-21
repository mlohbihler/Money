package com.serotonin.money.util;

import java.io.File;
import java.io.IOException;
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
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import com.serotonin.money.vo.AssetInfo;
import com.serotonin.money.vo.Country;
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
        final ExecutorService executor = Executors.newFixedThreadPool(20);
        final ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(0);

        try {
            // Get the FX
            final double fx = getUSExchange(scheduledExecutor);

            populateMarketValuesAlphaVantage(executor, scheduledExecutor, assets, fx);
        } finally {
            executor.shutdown();
            scheduledExecutor.shutdown();
        }
    }

    static double getUSExchange(final ScheduledExecutorService scheduledExecutor) throws Exception {
        try (CloseableHttpClient client = createHttpClient(null)) {
            final StringBuilder sb = new StringBuilder();
            sb.append(
                    "https://www.alphavantage.co/query?function=CURRENCY_EXCHANGE_RATE&from_currency=USD&to_currency=CAD&apikey=");
            sb.append(alphaVantageApiKey);
            final HttpGet get = new HttpGet(sb.toString());
            final ScheduledFuture<?> aborter = scheduledExecutor.schedule(() -> get.abort(), 11, TimeUnit.SECONDS);
            try (CloseableHttpResponse res = client.execute(get)) {
                if (res.getStatusLine().getStatusCode() != 200) {
                    throw new IOException("Bad response code: " + res.getStatusLine().getStatusCode());
                }
                final String content = IOUtils.toString(res.getEntity().getContent());
                final JMap quote = new JsonReader(content).read();

                if (!quote.containsKey("Realtime Currency Exchange Rate")) {
                    throw new Exception("FX: No quote, content=" + content);
                }

                final String price = quote.getStringByPath("Realtime Currency Exchange Rate", "5. Exchange Rate");
                if (price != null) {
                    return Double.parseDouble(price);
                }

                throw new Exception("Unable to parse exchange rate for FX. content=" + content);
            } finally {
                aborter.cancel(false);
            }
        } catch (final Exception e) {
            throw new Exception("FX: " + e.getMessage());
        }
    }

    static void populateMarketValuesAlphaVantage(final ExecutorService executor,
            final ScheduledExecutorService scheduledExecutor, final List<AssetInfo> assets, final double fx)
            throws Exception {
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
                                    final String priceStr = quote.getStringByPath("Global Quote", "05. price");
                                    if (priceStr != null) {
                                        double price = Double.parseDouble(priceStr);
                                        if (asset.getDivCountry() == Country.US) {
                                            price *= fx;
                                        }
                                        asset.setMarketPrice(price);
                                        asset.setMarketTime(System.currentTimeMillis());
                                    } else {
                                        LOG.warn("Unable to parse market price for " + symbol + ". content=" + content);
                                    }
                                    synchronized (proxies) {
                                        proxies.add(proxy);
                                    }
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
