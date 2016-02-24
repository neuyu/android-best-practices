package com.neu.httpclient;

import java.util.Map;

/**
 *
 * Created by neu on 16/2/9.
 */
public interface HttpRequest {

    /**
     * 发起HTTP GET同步请求
     *
     * @param ttype
     * @param url
     * @param parameters 请求的参数，参数将拼接到url中，允许为null
     * @param <T>        支持返回实体对像，当指定为String.class时，返回的是字符串
     * @return
     */
    <T> T get(TType<T> ttype, String url, Map<String, String> parameters);

    /**
     * 发起HTTP GET同步请求
     *
     * @param url
     * @param parameters
     * @return
     */
    String get(String url, Map<String, String> parameters);

    /**
     * 发起HTTP异步请求
     *
     * @param url
     * @param parameters   请求的参数，如果是GET请求，则会将参数拼接到url中，允许为null
     * @param httpCallback
     * @param <T>          支持返回实体对像，当指定为String.class时，返回的是字符串
     */
    <T> void get(String url, Map<String, String> parameters, HttpCallback<T> httpCallback);

    /**
     * 发起HTTP POST 同步请求
     *
     * @param ttType
     * @param url
     * @param parameters
     * @param <T>        支持返回实体对像，当指定为String.class时，返回的是字符串
     * @return
     */
    <T> T post(TType<T> ttType, String url, Map<String, String> parameters);

    /**
     * 发起HTTP POST 同步请求
     *
     * @param url
     * @param parameters
     * @return
     */
    String post(String url, Map<String, String> parameters);

    /**
     * 发起HTTP POST 异步请求
     *
     * @param url
     * @param parameters
     * @param httpCallback
     * @param <T>          支持返回实体对像，当指定为String.class时，返回的是字符串
     */
    <T> void post(String url, Map<String, String> parameters, HttpCallback<T> httpCallback);


}
