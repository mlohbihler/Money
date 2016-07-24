package com.serotonin.money.util;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.HttpClient;

import com.serotonin.money.vo.AssetInfo;
import com.serotonin.util.StringUtils;
import com.serotonin.web.http.HttpUtils4;

public class Market {
    //http://www.google.com/finance?q=TSE%3ADR%2CTSE%3Apdv%2Ctse%3Aerf%2Ctse%3Aarx%2Ctse%3Ahej%2Ctse%3Ahep
    static String pattern1 = "\"${symbol}\",\".*?\",\"([\\d\\.]+?)\",\".*?\",\".*?\",\".*?\",\".*?\",\".*?\",\"${exchange}\"";
    static String pattern2 = "\\{u:\".*?${symbol}\".*?,p:\"([\\d\\.]+?)\"";
    static String pattern3a = "\\[\"${symbol}\",(.*?),\"${exchange}\",.*?,\"${symbol}\"";
    static String pattern3b = "\".*?\",\"([\\d\\.]+?)\",";
    //https://ca.finance.yahoo.com/quotes/GOOG,F00000TXOD.TO
    static String ysfPattern1 = "<span class=\"streaming-datum\" id=\"yfs_l84_${symbol}\">(.*?)</span>";

    static int maxSymbols = 14;

    public static void populateMarketValue(AssetInfo asset) throws Exception {
        final List<AssetInfo> assets = new ArrayList<AssetInfo>();
        assets.add(asset);
        populateMarketValues(assets);
    }

    public static void populateMarketValues(List<AssetInfo> assets) throws Exception {
        // Just do yahoo
        final List<AssetInfo> fails = populateMarketValuesYahoo(assets);
        if (!fails.isEmpty()) {
            final List<AssetInfo> oneAtATime = new ArrayList<AssetInfo>();
            for (final AssetInfo a : fails) {
                oneAtATime.add(a);
                populateMarketValuesYahoo(oneAtATime);
                oneAtATime.clear();
            }
        }

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

    static List<AssetInfo> populateMarketValuesGoogle(List<AssetInfo> assets) throws Exception {
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

        final List<AssetInfo> fails = new ArrayList<AssetInfo>();
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

    private static boolean findPriceGoogle(AssetInfo asset, String content) {
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

    private static List<AssetInfo> populateMarketValuesYahoo(List<AssetInfo> assets) throws Exception {
        final StringBuilder sb = new StringBuilder();
        sb.append("https://ca.finance.yahoo.com/quotes/");

        final List<AssetInfo> attempts = new ArrayList<AssetInfo>();
        final List<AssetInfo> fails = new ArrayList<AssetInfo>();

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
            }
            catch (final Exception e) {
                fails.addAll(attempts);
                throw new Exception("Failed on url " + sb, e);
            }
        }

        return fails;
    }

    private static boolean findPriceYahoo(AssetInfo asset, String content) {
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
            }
            catch (final NumberFormatException e) {
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

    private static String getSymbolToUseYahoo(AssetInfo asset) {
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

    private static String getSymbolToUse(AssetInfo asset) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(asset.getMarketSymbol()))
            return asset.getSymbol();
        return asset.getMarketSymbol();
    }

    public static void main(String[] args) throws Exception {
        final String symbolToUse = "NASDAQ:AGNC";
        String p = StringUtils.replaceMacro(pattern3a, "symbol", Utils.getSymbolSansExchange(symbolToUse));
        p = StringUtils.replaceMacro(p, "exchange", Utils.getExchange(symbolToUse));
        final Pattern patt = Pattern.compile(p);

        final String content = FileUtils.readFileToString(new File("src-test", "content.txt"));

        for (final String s : StringUtils.findAllGroup(patt, content))
            System.out.println(s);
    }
}
