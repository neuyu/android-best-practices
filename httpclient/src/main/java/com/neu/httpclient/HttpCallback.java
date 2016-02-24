package com.neu.httpclient;

/**
 *Created by neu on 16/2/9.
 */
public abstract class HttpCallback<T> extends TType<T> implements ProgressListener {

    /**
     * 请求失败回调的函数，请求失败包括服务器未返回，或服务器返回的HTTP STATUS CODE标示失败
     *
     * @param httpStatusCode
     * @param message
     * @param throwable      如果httpStatusCode为-1的话，由该参数标识发生的异常
     */
    public abstract void onFailure(int httpStatusCode, String message, Throwable throwable);

    /**
     * 请求成功回调的函数，请求成功仅表示服务器正常处理请求了，不代表业务处理成功
     *
     * @param httpStatusCode
     * @param responseObject
     */
    public abstract void onSuccess(int httpStatusCode, T responseObject);

    @Override
    public void onProgress(long bytesCount, long contentLength, boolean done) {
    }

}
