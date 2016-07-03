package com.deerhunter.developextesttask.network;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

public class NetworkCommunicator {
    private static final String TAG = NetworkCommunicator.class.getSimpleName();
    private static final int NETWORK_CONNECT_TIMEOUT = 10000;
    private static final int NETWORK_READ_TIMEOUT = 30000;

    public boolean isHtmlPage(String pageUrl) {
        try {
            URL url = new URL(pageUrl);
            HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
            urlc.setAllowUserInteraction(false);
            urlc.setDoInput(true);
            urlc.setDoOutput(false);
            urlc.setUseCaches(true);
            urlc.setRequestMethod("HEAD");
            urlc.connect();
            String mime = urlc.getContentType();
            if (mime != null && mime.contains("text/html")) {
                return true;
            }
        } catch (Exception ex) {
            Log.d(TAG, ex.toString());
        }
        return false;
    }

    public Response loadWebPage(String pageUrl) throws IOException {
        Response response = new Response();
        HttpURLConnection connection = null;
        InputStream inputStream = null;

        try {
            connection = obtainConnection(pageUrl);
            inputStream = getInputStream(connection);
            response = getResponse(connection, inputStream);
            return response;
        } catch (Exception e) {
            return response;
        } finally {
            releaseConnection(connection, inputStream);
        }
    }

    private InputStream getInputStream(HttpURLConnection connection){
        InputStream inputStream = null;
        try{
            inputStream = connection.getInputStream();
        } catch(Exception ex){
            Log.d(TAG, "Problem to get inputStream: " + ex.getMessage());
        }
        if (inputStream == null)
            inputStream = connection.getErrorStream();
        return inputStream;
    }

    private Response getResponse(HttpURLConnection connection, InputStream inputStream) throws IOException {
        Response response = new Response();
        BufferedInputStream bis = new BufferedInputStream(inputStream);
        int responseCode = connection.getResponseCode();
        response.setStatus(responseCode);
        String contentEncoding = getContentEncoding(connection);
        response.setContentEncoding(contentEncoding);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int readBytesCount;

        while ((readBytesCount = bis.read(buffer)) > -1) {
            baos.write(buffer, 0, readBytesCount);
        }
        response.setResponse(baos.toByteArray());

        return response;
    }

    private String getContentEncoding(HttpURLConnection connection) {
        String contentType = connection.getContentType();
        String[] values = contentType.split(";");
        String charset = "";

        for (String value : values) {
            value = value.trim();

            if (value.toLowerCase().startsWith("charset=")) {
                charset = value.substring("charset=".length());
            }
        }

        if ("".equals(charset)) {
            charset = "UTF-8"; //default
        }
        return charset;
    }

    private HttpURLConnection obtainConnection(String url) throws IOException {
        URL dataUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) dataUrl.openConnection();
        setupConnection(connection);
        return connection;
    }

    private void setupConnection(HttpURLConnection connection) {
        connection.setUseCaches(false);
        connection.setInstanceFollowRedirects(true);
        connection.setConnectTimeout(NETWORK_CONNECT_TIMEOUT);
        connection.setReadTimeout(NETWORK_READ_TIMEOUT);
        try {
            connection.setRequestMethod("GET");
        } catch (ProtocolException e) {
            Log.d(TAG, Log.getStackTraceString(e));
        }
    }

    private void releaseConnection(HttpURLConnection connection, InputStream inputStream){
        if (inputStream != null)
            try {
                inputStream.close();
            } catch (IOException e) {
                Log.d(TAG, "Can't close inputStream: " + Log.getStackTraceString(e));
            }
        if (connection != null)
            connection.disconnect();
    }
}
