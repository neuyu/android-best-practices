package com.neu.qrcode;

import android.content.Intent;

import com.neu.qrcode.callback.PermissionResultCallback;
import com.neu.qrcode.callback.QrcodeCallback;

/**
 * Created by neu on 16/2/23.
 */
public interface Qrcode {
    /**
     * 开启扫描界面
     */
    void start();

    /**
     * 重写onActivityResult
     *
     * @param callback 扫描回调
     */
    void onActivityResult(int requestCode, int resultCode, Intent data, QrcodeCallback callback);

    //android 6.0的动态权限
    void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults,PermissionResultCallback callback);

}
