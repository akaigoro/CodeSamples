package so.completableFuture;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.junit.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.*;
import java.util.stream.Stream;

public class AsyncSync {
    String uri = "http://localhost:8765";
    //String uri = "http://bing.com/";
    int size = 1000;

    @Test
    public void sync() throws MalformedURLException, InterruptedException {

        long start = System.currentTimeMillis();
        URL url = new URL(uri);
        ExecutorService executor = Executors.newCachedThreadPool();

        Stream.generate(() -> url).limit(size).forEach(requestUrl -> {
            executor.submit(() -> {
                try {
                    HttpURLConnection conn = (HttpURLConnection) requestUrl.openConnection();
                    int responseCode = conn.getResponseCode();
                //    System.out.println("Completed with: " + responseCode);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
 //           System.out.println("Started request");
        });

        executor.shutdown();
        executor.awaitTermination(100, TimeUnit.SECONDS);
        long end = System.currentTimeMillis();
        System.out.println("sync:"+(end-start));

    }

    //@Test
    public void async() {

        try (CloseableHttpAsyncClient httpclient = HttpAsyncClients.custom()
                .setMaxConnPerRoute(2*size).setMaxConnTotal(2*size)
                .setUserAgent("Mozilla/4.0")
                .build()) {
            long start = System.currentTimeMillis();
            httpclient.start();
            HttpGet request = new HttpGet(uri);
            CompletableFuture.allOf(
                    Stream.generate(() -> request).limit(size).map(req -> {
                        CompletableFuture<Void> future = new CompletableFuture<>();
                        httpclient.execute(req, new FutureCallback<HttpResponse>() {
                            @Override
                            public void completed(final HttpResponse response) {
                              //  System.out.println("Completed with: " + response.getStatusLine().getStatusCode());
                                future.complete(null);
                            }

                            @Override
                            public void failed(Exception ex) {

                            }

                            @Override
                            public void cancelled() {

                            }
                       });
               //         System.out.println("Started request");
                        return future;
                    }).toArray(CompletableFuture[]::new)
            ).get();
            long end = System.currentTimeMillis();
            System.out.println("async:"+(end-start));
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

    }

    public void  test() throws MalformedURLException, InterruptedException {

        async();
        sync();
        sync();
        async();

        sync();
        async();
        async();
        sync();

    }

    public static void  main(String[] args) throws MalformedURLException, InterruptedException {//throws MalformedURLException, InterruptedException {
        new AsyncSync().test();
    }

}