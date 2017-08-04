package com.unixtrong.anbo.handler;

import android.os.Bundle;

import com.unixtrong.anbo.entity.ApiResult;
import com.unixtrong.anbo.entity.Feed;
import com.unixtrong.anbo.tools.CodeUtils;
import com.unixtrong.anbo.tools.HttpUtils;
import com.unixtrong.anbo.tools.Lg;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by danyun on 2017/8/5
 */

public class WeiboApi {

    private static final String BASE_URL = "https://api.weibo.com/2/";

    /**
     * https://api.weibo.com/2/statuses/home_timeline.json
     */
    public static ApiResult<List<Feed>> timeLine(String token, int count) {
        String url = BASE_URL + "statuses/home_timeline.json";
        Bundle params = new Bundle();
        params.putString("access_token", token);
        params.putString("count", String.valueOf(count));
        try {
            InputStream resStream = HttpUtils.doGetRequest(url, params);
            String res = CodeUtils.readText(resStream);
            return ApiResult.fill(new JSONObject(res), new ApiResult.Parser<List<Feed>>() {
                @Override
                public List<Feed> parse(String jsonStr) {
                    try {
                        return Feed.fillList(new JSONObject(jsonStr).optJSONArray("statuses"));
                    } catch (JSONException e) {
                        Lg.warn(e);
                        return null;
                    }
                }
            });
        } catch (IOException | JSONException e) {
            Lg.warn(e);
        }
        return null;
    }
}
