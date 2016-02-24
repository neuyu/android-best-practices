package com.neu.httpclient;

import java.util.Map;

/**
 * Created by neu on 16/2/9.
 */
public class OkHttpRequestImpl implements HttpRequest{
    @Override
    public <T> T get(TType<T> ttype, String url, Map<String, String> parameters) {
        return null;
    }

    @Override
    public String get(String url, Map<String, String> parameters) {
        return null;
    }

    @Override
    public <T> void get(String url, Map<String, String> parameters, HttpCallback<T> httpCallback) {

    }

    @Override
    public <T> T post(TType<T> ttType, String url, Map<String, String> parameters) {
        return null;
    }

    @Override
    public String post(String url, Map<String, String> parameters) {
        return null;
    }

    @Override
    public <T> void post(String url, Map<String, String> parameters, HttpCallback<T> httpCallback) {

    }
}
