package com.neu.qrcode.qrcode;

import android.graphics.Bitmap;

/**
 * Created by neu on 16/2/23.
 * 二维码信息类
 */
public final class QrcodeInfo {
    /**
     * 二维码信息
     */
    private final String result;
    /**
     * 二维码图像
     */
    private final Bitmap qrCodeImage;
    /**
     * 二维码图像宽度
     */
    private final int width;
    /**
     * 二维码高度
     */
    private final int height;

    public QrcodeInfo(String result, Bitmap qiCodeImage, int width, int height) {
        this.result = result;
        this.qrCodeImage = qiCodeImage;
        this.width = width;
        this.height = height;
    }

    public Bitmap getQrCodeImage() {
        return qrCodeImage;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public String getResult() {
        return result;
    }

    @Override
    public String toString() {
        return result;
    }
}
