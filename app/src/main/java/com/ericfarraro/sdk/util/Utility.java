package com.ericfarraro.sdk.util;

import org.apache.http.HttpConnection;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Eric on 10/7/2014.
 */
public class Utility {

    /**
     * Helper method to get the bytes at a specified url
     * @param u A representation of the URL, as a string
     * @return The bytes at the given URL
     * @throws IOException
     */
    public static byte[] getBytesForUrl(String u) throws IOException {

        URL url = new URL(u);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            // check to make sure the resource was found successfully
            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK)
                return null;

            int bytesRead = 0;
            byte[] buffer = new byte[1024];

            while((bytesRead = in.read(buffer)) > 0 ) {
                out.write(buffer, 0, bytesRead);
            }

            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }
}
