package com.neu.contact.contact;

import android.support.annotation.NonNull;

/**
 * Created by neuyuandaima on 2015/12/31.
 */
public interface ContactCallback {
    //成功回调 contactNumber为选择的手机号码，contactName为选择的姓名
    void onSuccess(@NonNull String contactNumber, @NonNull String contactName);

    //失败回调 message为失败的原因
    void onFailed(@NonNull int errCode, @NonNull String message);
}
