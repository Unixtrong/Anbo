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
     * 调用微博 API 获取用户关注的最新 count 条数据
     * 访问路径为 https://api.weibo.com/2/statuses/home_timeline.json
     *
     * @param token 用户的微博访问 token
     * @param count 需要获取的 feed 数量
     */
    public static ApiResult<List<Feed>> timeLine(String token, int count) {
        String url = BASE_URL + "statuses/home_timeline.json";
        // 将参数以键值对的形式填入 bundle 中
        Bundle params = new Bundle();
        params.putString("access_token", token);
        params.putString("count", String.valueOf(count));
        try {
            // 通过 url 和参数获取服务器返回的数据流
            InputStream resStream = HttpUtils.doGetRequest(url, params);
            // 将服务器返回的数据流转化为文本
            String res = CodeUtils.readText(resStream);
            // 将文本返回结果，解析到 ApiResult 对象中
            return ApiResult.fill(new JSONObject(res), new ApiResult.Parser<List<Feed>>() {
                @Override
                public List<Feed> parse(JSONObject json) {
                    // 将返回结果中 feed 列表对应的 JSON 对象，解析到 Feed 对象中
                    return Feed.fillList(json.optJSONArray("statuses"));
                }
            });
        } catch (IOException | JSONException e) {
            Lg.warn(e);
        }
        return null;
    }
}
