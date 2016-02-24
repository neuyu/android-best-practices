package com.neu.qrcode.callback;

import android.support.annotation.NonNull;

import com.neu.qrcode.qrcode.QrcodeInfo;

/**
 * Created by neu on 16/2/23.
 */
public interface QrcodeCallback {
    /**
     * 扫描二维码成功
     *
     * @param info 二维码信息
     */
    void onSuccess(@NonNull QrcodeInfo info);

    /**
     * 扫描二维码失败
     * @param errMsg 错误信息
     */
    void onFailed(@NonNull String errMsg);
}
