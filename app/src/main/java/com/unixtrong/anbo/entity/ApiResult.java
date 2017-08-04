package com.unixtrong.anbo.entity;

import org.json.JSONObject;

/**
 * Created by danyun on 2017/8/5
 */
public class ApiResult<T> {
    private String data;
    private String errorCode;
    private String errorMessage;
    private T body;

    public static <B> ApiResult<B> fill(JSONObject json, Parser<B> parser) {
        ApiResult<B> result = new ApiResult<>();
        result.data = json.toString();
        if (json.has("error_code")) {
            result.errorCode = json.optString("error_code");
        }
        if (json.has("error")) {
            result.errorMessage = json.optString("error");
        }
        if (result.isSuccess()) {
            result.body = parser.parse(result.data);
        }
        return result;
    }

    public String getData() {
        return data;
    }

    public ApiResult setData(String data) {
        this.data = data;
        return this;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public ApiResult setErrorCode(String errorCode) {
        this.errorCode = errorCode;
        return this;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public ApiResult setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }

    public T getBody() {
        return body;
    }

    public ApiResult setBody(T body) {
        this.body = body;
        return this;
    }

    public boolean isSuccess() {
        return errorCode == null;
    }

    public interface Parser<T> {
        T parse(String jsonStr);
    }
}
