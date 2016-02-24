package com.neu.qrcode.impl;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.neu.qrcode.Qrcode;
import com.neu.qrcode.callback.PermissionResultCallback;
import com.neu.qrcode.callback.QrcodeCallback;
import com.neu.qrcode.qrcode.QrcodeInfo;
import com.neu.qrcode.skill.activity.CaptureActivity;
import com.neu.qrcode.skill.constants.Constants;
import com.neu.qrcode.skill.decode.DecodeThread;
import com.neu.qrcode.util.Utils;

/**
 * Created by neu on 16/2/23.
 */
public class QrcodeImpl implements Qrcode {
    /**
     * Id to identify a activity startActivityForResult
     */
    private static final int REQUEST_CODE = 101;
    /**
     * Id to identify a CAMERA permission request.
     */
    private static final int REQUEST_CAMERA = 201;
    /**
     * Permissions required to read and write contacts. Used by the {@link QrcodeImpl}.
     */
    private static String[] PERMISSIONS_CAMERA = {Manifest.permission.CAMERA};


    //持有activity
    private Activity mActivity;
    //持有fragment
    private android.support.v4.app.Fragment mSupportFragment;
    //持有fragment
    private Fragment mFragment;
    //扫描后回调
    private QrcodeCallback mCallback;


    public QrcodeImpl(Activity activity) {
        mActivity = activity;
    }

    public QrcodeImpl(Fragment fragment) {
        mFragment = fragment;
        mActivity = mFragment.getActivity();
    }

    public QrcodeImpl(android.support.v4.app.Fragment supportFragment) {
        mSupportFragment = supportFragment;
        mActivity = mSupportFragment.getActivity();
    }

    @Override
    public void start() {
        //检查是否具有该权限
        checkPremission();
    }

    /**
     * 检查是否具有权限
     */
    private void checkPremission() {
        //当Android6.0以下，申明了权限，直接startIntent()
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED){
            // Contacts permissions have not been granted.
            requesQrCodePermissions();

        } else {
            // Contact permissions have been granted. Show the contacts fragment.
            startQrCode();
        }
    }

    /**
     * 开启扫码
     */
    private void startQrCode() {
        if (mFragment != null) {
            Intent intent = new Intent(mActivity, CaptureActivity.class);
            mFragment.startActivityForResult(intent, REQUEST_CODE);
        } else if (mSupportFragment != null) {
            Intent intent = new Intent(mActivity, CaptureActivity.class);
            mSupportFragment.startActivityForResult(intent, REQUEST_CODE);
        } else {
            Intent intent = new Intent(mActivity, CaptureActivity.class);
            mActivity.startActivityForResult(intent, REQUEST_CODE);
        }
    }

    /**
     * Android6.0需要权限
     */
    private void requesQrCodePermissions() {
        //判断该权限是否需要
        if (mFragment != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mFragment.requestPermissions(PERMISSIONS_CAMERA, REQUEST_CAMERA);
        } else if (mSupportFragment != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mSupportFragment.requestPermissions(PERMISSIONS_CAMERA, REQUEST_CAMERA);
        } else {
            //Android6.0以上的手机启动contact,且是activity启动的
            //Android6.0以下的手机，具有权限管理，且关闭了app的联系人权限
            // TODO: 16/2/17 当Android6.0以下手机调用方必须是在其activity中实现OnRequestPermissionsResultCallback，且调用contact的onRequestPermissionsResult
            //不实现OnRequestPermissionsResultCallback的话，无法接收消息，fragmentactivity实现了该接口
            //所以当你的activity是fragmentactivity的子类或者appcompatActivity的子类，可以不实现其接口
            ActivityCompat.requestPermissions(mActivity, PERMISSIONS_CAMERA, REQUEST_CAMERA);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults, PermissionResultCallback callback) {
        if (requestCode == REQUEST_CAMERA) {
            // We have requested multiple permissions for contacts, so all of them need to be
            // checked.
            if (Utils.verifyPermissions(grantResults)) {
                // All required permissions have been granted, display contacts fragment.
                startQrCode();
            } else {
                callback.denyPermission();
            }

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data, QrcodeCallback callback) {
        this.mCallback = callback;
        if (requestCode == REQUEST_CODE) {
            handleActivityResult(resultCode, data, callback);
        }
    }

    /**
     * 处理扫描结果
     *
     * @param resultCode 结果码
     * @param data       数据
     * @param callback   回调
     */
    private void handleActivityResult(int resultCode, Intent data, QrcodeCallback callback) {
        switch (resultCode) {
            case Activity.RESULT_OK:
                handleSuccess(data);
                break;
            case Activity.RESULT_CANCELED:
                sendBackResult(false, "用户已取消", null);
                break;
            case Constants.PERMISSION_DENY:
                sendBackResult(false, "打开摄像头无权限", null);
                break;
        }
    }

    /**
     * 返回用户数据
     *
     * @param isSuccess 是否成功
     * @param message   信息
     * @param info      二维码信息
     */
    private void sendBackResult(boolean isSuccess, String message, QrcodeInfo info) {
        if (!isSuccess) {
            mCallback.onFailed(message);
        } else {
            mCallback.onSuccess(info);
        }
    }

    /**
     * 成功扫描
     *
     * @param data 数据
     */
    private void handleSuccess(Intent data) {
        Bundle bundle = data.getExtras();
        if (bundle != null) {
            String result = bundle.getString("result");
            byte[] compressedBitmap = bundle.getByteArray(DecodeThread.BARCODE_BITMAP);
            int width = bundle.getInt("width");
            int height = bundle.getInt("height");
            Bitmap bitmap = getBitmap(compressedBitmap);
            sendBackResult(true, null, new QrcodeInfo(result, bitmap, width, height));
        } else {
            sendBackResult(false, "扫码失败", null);
        }
    }

    /**
     * 将byte数组decode为bitmap
     *
     * @param compressedBitmap byte数组
     * @return bitmap对象
     */
    private Bitmap getBitmap(byte[] compressedBitmap) {
        Bitmap barcode = null;
        if (compressedBitmap != null) {
            barcode = BitmapFactory.decodeByteArray(compressedBitmap, 0, compressedBitmap.length, null);
            // Mutable copy:
            barcode = barcode.copy(Bitmap.Config.RGB_565, true);
        }
        return barcode;
    }


}
