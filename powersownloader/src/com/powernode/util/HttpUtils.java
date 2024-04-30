package com.powernode.util;

/*
http tools
 */

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/*
    get http url connection
    @param url
    @return
 */
public class HttpUtils {

    public static long getHttpFileContentLength(String url) throws IOException {
        HttpURLConnection httpURLConnection = null;
        int contentLength;
        try {
            httpURLConnection = getHttpURLConnection(url);
            contentLength = httpURLConnection.getContentLength();
        } finally {
            if (httpURLConnection != null){
                httpURLConnection.disconnect();
            }
        }
        return contentLength;
    }

    public static HttpURLConnection getHttpURLConnection(String url, long startPos, long endPos) throws IOException {
        HttpURLConnection httpURLConnection = getHttpURLConnection(url);
        LogUtils.info("The range to download is: {} - {}", startPos, endPos);

        if (endPos != 0){
            httpURLConnection.setRequestProperty("RANGE","bytes="+startPos+"-"+endPos);
        } else {
            httpURLConnection.setRequestProperty("RANGE","bytes="+startPos+"-");
        }

        return httpURLConnection;
    }

    public static HttpURLConnection getHttpURLConnection(String url) throws IOException {
        URL httpUrl = new URL(url);
        HttpURLConnection httpURLConnection = (HttpURLConnection)httpUrl.openConnection();
        //send sign of this computer
        httpURLConnection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/14.0.835.163 Safari/535.1");
        return httpURLConnection;
    }

    public static String getHttpFileName(String url){
        int index = url.lastIndexOf("/");
        return url.substring(index + 1);
    }
}
