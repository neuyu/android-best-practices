package com.neu.httpclient;

/**
 * Created by neu on 16/2/09.
 */
public interface ProgressListener {

    /**
     * 上传或下载的进度反馈
     *
     * @param bytesCount    已上传或下载的字节数
     * @param contentLength 需要上传或下载的总字节数，下载的长度获取不到时可能返回-1
     * @param done          是否已经成功完成，注意异常情况下，该值不会返回true
     */
    void onProgress(long bytesCount, long contentLength, boolean done);
}
