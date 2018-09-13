package adhoc;

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

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import com.serotonin.web.http.HttpUtils4;

public class Adhoc {
    public static void main(final String[] args) throws Exception {
        //        test1();
        test2();
    }

    static void test2() throws Exception {
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

        final String[] symbols = { "NASDAQ:AGNC", "NASDAQ:MCD", "NASDAQ:PG", "TSE:CBD", "TSE:CWX", "TSE:FAP",
                "TSE:GCL" };
        final List<Future<Void>> futures = new ArrayList<>();
        final ExecutorService executor = Executors.newFixedThreadPool(20);
        final ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(0);
        for (final String symbol : symbols) {
            futures.add(executor.submit(() -> {
                final StringBuilder sb = new StringBuilder();
                sb.append("https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=");
                sb.append(symbol);
                sb.append("&apikey=9I8UTJYRY4757Z3R");

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
                        System.out.println(symbol + ": Waiting for proxy");
                        Thread.sleep(500);
                    } else {
                        try (CloseableHttpClient client = createHttpClient(proxy)) {
                            final HttpGet get = new HttpGet(sb.toString());
                            final ScheduledFuture<?> aborter = scheduledExecutor.schedule(() -> {
                                System.out.println(symbol + ": Aborting");
                                get.abort();
                            }, 11, TimeUnit.SECONDS);
                            try (CloseableHttpResponse res = client.execute(get)) {
                                if (res.getStatusLine().getStatusCode() != 200) {
                                    throw new IOException("Bad response code: " + res.getStatusLine().getStatusCode());
                                }
                                final String content = IOUtils.toString(res.getEntity().getContent());
                                System.out.println(content);
                                synchronized (proxies) {
                                    proxies.add(proxy);
                                }
                                break;
                            } finally {
                                aborter.cancel(false);
                            }
                        } catch (final Exception e) {
                            System.out.println(symbol + ": " + e.getMessage());
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

    static void test1() throws Exception {
        final HttpClient httpClient = HttpUtils4.getHttpClient(10000);
        //        final String uri = "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=NASDAQ:AGNC&apikey=9I8UTJYRY4757Z3R";
        final String uri = "https://www.sslproxies.org/";
        final String content = HttpUtils4.getTextContent(httpClient, uri);

        final Pattern pattern = Pattern.compile("<tr><td>(\\d+.\\d+.\\d+.\\d+)<\\/td><td>(.*?)<\\/td>.*?<\\/tr>");
        //        final Pattern pattern = Pattern.compile("<td>(.*?)<\\/td>");
        final Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            //            System.out.println(matcher.groupCount());
            //            System.out.println(matcher.group(0));
            System.out.println(matcher.group(1) + ":" + matcher.group(2));
            //            System.out.println(matcher.group(2));
        }

        //        System.out.println(content);
    }
}
