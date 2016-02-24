package com.neu.httpclient;

/**
 * Created by siva on 15/12/23.
 */
public class HttpRequestFactory {

    private String TAG = HttpRequestFactory.class.getSimpleName();

    /**
     * 创建一个HttpRequest实例
     *
     * @return
     */
    public static HttpRequest newInstance() {

        return new OkHttpRequestImpl();
    }
}
