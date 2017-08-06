package com.unixtrong.anbo.entity;

import org.json.JSONObject;

/**
 * Created by danyun on 2017/8/5
 */
public class ApiResult<T> {
    private JSONObject mJson;
    private String mErrorCode;
    private String mErrorMessage;
    private T mBody;

    public static <B> ApiResult<B> fill(JSONObject json, Parser<B> parser) {
        ApiResult<B> result = new ApiResult<>();
        result.mJson = json;
        if (json.has("error_code")) {
            result.mErrorCode = json.optString("error_code");
        }
        if (json.has("error")) {
            result.mErrorMessage = json.optString("error");
        }
        if (result.isSuccess()) {
            result.mBody = parser.parse(result.mJson);
        }
        return result;
    }

    public JSONObject getJson() {
        return mJson;
    }

    public ApiResult setJson(JSONObject json) {
        this.mJson = json;
        return this;
    }

    public String getErrorCode() {
        return mErrorCode;
    }

    public ApiResult setErrorCode(String errorCode) {
        this.mErrorCode = errorCode;
        return this;
    }

    public String getErrorMessage() {
        return mErrorMessage;
    }

    public ApiResult setErrorMessage(String errorMessage) {
        this.mErrorMessage = errorMessage;
        return this;
    }

    public T getBody() {
        return mBody;
    }

    public ApiResult setBody(T body) {
        this.mBody = body;
        return this;
    }

    public boolean isSuccess() {
        return mErrorCode == null;
    }

    public interface Parser<T> {
        T parse(JSONObject json);
    }
}
