package util;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SimpleHttpServerTest {
    static String uri = "http://localhost:"+SimpleHttpServer.port;

    @Test
    public void smokeTest() throws IOException {
        URL url = new URL(uri);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        int responseCode = conn.getResponseCode();
        Assert.assertEquals(200, responseCode);
    }

    @Test
    public void pathTest() throws IOException {
        URL url = new URL(uri+"/accounts/filter/");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        int responseCode = conn.getResponseCode();
        Assert.assertEquals(200, responseCode);
        String reply = conn.getResponseMessage();
        Assert.assertNotNull(reply);
    }
}
