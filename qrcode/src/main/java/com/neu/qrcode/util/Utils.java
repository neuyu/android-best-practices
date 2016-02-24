package com.neu.qrcode.util;

import android.app.Activity;
import android.content.pm.PackageManager;

/**
 * Created by neu on 16/2/23.
 */
public class Utils {
    /**
     * 校验给定对象是否为空及其属性是否为空
     *
     * @param object  给定对象
     * @param message 抛出错误信息
     * @param <T>     返回类型
     * @return 返回对象
     */
    public static <T> T checkNotNull(T object, String message) {
        if (object == null) {
            throw new NullPointerException(message);
        }

        return object;
    }

    /**
     * Check that all given permissions have been granted by verifying that each entry in the
     * given array is of the value {@link PackageManager#PERMISSION_GRANTED}.
     *
     * @see Activity#onRequestPermissionsResult(int, String[], int[])
     */
    public static boolean verifyPermissions(int[] grantResults) {
        // At least one result must be checked.
        if(grantResults.length < 1){
            return false;
        }

        // Verify that each required permission has been granted, otherwise return false.
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

}
