package com.neu.qrcode;

import android.app.Activity;
import android.app.Fragment;

import com.neu.qrcode.impl.QrcodeImpl;
import com.neu.qrcode.util.Utils;

/**
 * Created by neu on 16/2/23.
 * 二维码生产工厂类
 */
public class QrcodeFactory {
    /**
     * 利用activity生产二维码对象
     *
     * @param activity activity对象
     * @return
     */
    public static Qrcode newQrcode(Activity activity) {
        Utils.checkNotNull(activity, "Activity could not null or other object");
        return new QrcodeImpl(activity);
    }

    /**
     * 利用v4.fragement生成二维码对象
     *
     * @param fragment fragment对象
     * @return
     */
    public static Qrcode newQrcode(Fragment fragment) {
        Utils.checkNotNull(fragment, "Fragment could not null or other object");
        return new QrcodeImpl(fragment);
    }


    /**
     * 利用v4.fragement生成二维码对象
     *
     * @param fragment fragment对象
     * @return
     */
    public static Qrcode newQrcode(android.support.v4.app.Fragment fragment) {
        Utils.checkNotNull(fragment, "Fragment could not null or other object");
        return new QrcodeImpl(fragment);
    }
}
