/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.neu.qrcode.skill.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder
 * rectangle and partial transparency outside it, as well as the laser scanner
 * animation and result points.
 */
public final class ViewfinderView extends View {
    private static final String TAG = "ViewfinderView";
    /**
     * 非扫描区域的背景颜色。
     */
    private final int SHADOW_COLOR = Color.parseColor("#7d000000");

    /**
     * 刷新界面的时间
     */
    private static final long ANIMATION_DELAY = 30L;

    /**
     * 四个绿色边角对应的长度
     */
    private static final int CORNER_LENGTH = 20;

    /**
     * 四个绿色边角对应的宽度
     */
    private static final int CORNER_WIDTH = 5;
    /**
     * 扫描框中的中间线的宽度
     */
    private static final int MIDDLE_LINE_WIDTH = 6;

    /**
     * 扫描框中的中间线的与扫描框左右的间隙
     */
    private static final int MIDDLE_LINE_PADDING = 5;

    /**
     * 中间那条线每次刷新移动的距离
     */
    private static final int SPEEN_DISTANCE = 5;

    /**
     * 手机的屏幕密度
     */
    private static float density;

    /**
     * 画笔对象的引用
     */
    private Paint mPaint;

    /**
     * 中间滑动线的最顶端位置
     */
    private int mSlidePos;

    boolean mIsFirst;

    private Rect mGuideRect;

    public ViewfinderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.d(TAG, "Construct ViewFinderView");
        density = context.getResources()
                .getDisplayMetrics().density;
        mPaint = new Paint();
    }

    @Override
    public void onDraw(Canvas canvas) {
        Rect frame = mGuideRect;
        if (frame == null) {
            return;
        }
        // Log.d("finder", frame + "");

        // 初始化中间线滑动的最上边和最下边
        if (!mIsFirst) {
            mIsFirst = true;
            mSlidePos = frame.top;
        }

        // 获取屏幕的宽和高
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        // 画出扫描框外面的阴影部分，共四个部分，扫描框的上面到屏幕上面，扫描框的下面到屏幕下面
        // 扫描框的左边面到屏幕左边，扫描框的右边到屏幕右边
        mPaint.setColor(SHADOW_COLOR);
        canvas.drawRect(0, 0, width, frame.top, mPaint);
        canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, mPaint);
        canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, mPaint);
        canvas.drawRect(0, frame.bottom + 1, width, height, mPaint);

        // 画扫描框边上的角，总共8个部分
        int cornerWidth = (int) (density * CORNER_WIDTH);
        int cornerLength = (int) (density * CORNER_LENGTH);
        mPaint.setColor(Color.GREEN);
        canvas.drawRect(frame.left, frame.top, frame.left + cornerLength, frame.top + cornerWidth, mPaint);
        canvas.drawRect(frame.left, frame.top, frame.left + cornerWidth, frame.top + cornerLength, mPaint);
        canvas.drawRect(frame.right - cornerLength, frame.top, frame.right, frame.top + cornerWidth, mPaint);
        canvas.drawRect(frame.right - cornerWidth, frame.top, frame.right, frame.top + cornerLength, mPaint);
        canvas.drawRect(frame.left, frame.bottom - cornerWidth, frame.left + cornerLength, frame.bottom, mPaint);
        canvas.drawRect(frame.left, frame.bottom - cornerLength, frame.left + cornerWidth, frame.bottom, mPaint);
        canvas.drawRect(frame.right - cornerLength, frame.bottom - cornerWidth, frame.right, frame.bottom, mPaint);
        canvas.drawRect(frame.right - cornerWidth, frame.bottom - cornerLength, frame.right, frame.bottom, mPaint);

        // 绘制中间的线,每次刷新界面，中间的线往下移动SPEEN_DISTANCE
        mSlidePos += SPEEN_DISTANCE;
        if (mSlidePos >= frame.bottom) {
            mSlidePos = frame.top;
        }
        canvas.drawRect(frame.left + MIDDLE_LINE_PADDING, mSlidePos - MIDDLE_LINE_WIDTH / 2, frame.right
                - MIDDLE_LINE_PADDING, mSlidePos + MIDDLE_LINE_WIDTH / 2, mPaint);

        // 只刷新扫描框的内容，其他地方不刷新
        postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top, frame.right, frame.bottom);
    }

    public void setGuideFrame(Rect rect) {
        mGuideRect = rect;
    }

}
