package com.unixtrong.anbo.tools;

import android.os.Bundle;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by danyun on 2017/8/5
 */

public class HttpUtils {

    public static InputStream doGetRequest(String path, Bundle params) throws IOException {
        String query = "";
        for (String key : params.keySet()) {
            query += "&" + key + "=" + params.getString(key);
        }
        path += query.replaceFirst("&", "?");
        Lg.debug("url: " + path);
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(10 * 1000);
        conn.setReadTimeout(10 * 1000);
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            return conn.getInputStream();
        }
        return null;
    }

}
