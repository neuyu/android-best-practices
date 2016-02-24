package com.neu.qrcode.skill.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.google.zxing.Result;
import com.neu.qrcode.R;
import com.neu.qrcode.skill.camera.CameraManager;
import com.neu.qrcode.skill.constants.Constants;
import com.neu.qrcode.skill.decode.DecodeThread;
import com.neu.qrcode.skill.utils.BeepManager;
import com.neu.qrcode.skill.utils.CaptureActivityHandler;
import com.neu.qrcode.skill.utils.InactivityTimer;
import com.neu.qrcode.skill.view.ViewfinderView;

import java.io.IOException;

/**
 * 扫描二维码实现类
 */
public final class CaptureActivity extends Activity implements SurfaceHolder.Callback {

    private static final String TAG = CaptureActivity.class.getSimpleName();
    //摄像头管理类
    private CameraManager cameraManager;
    //handler
    private CaptureActivityHandler handler;
    //activity计时类
    private InactivityTimer inactivityTimer;
    //震动类
    private BeepManager beepManager;
    //layout中的组件
    private SurfaceView scanPreview = null;

    private ViewfinderView mViewfinderView;
    //截取正方形
    private Rect mCropRect = null;

    private Rect mScanRect = null;
    //是否有surface
    private boolean isHasSurface = false;
    //view延迟加载
    private ViewStub mViewStub;
    //是否被inflate
    private boolean isInflate = false;

    public Handler getHandler() {
        return handler;
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置屏幕常亮
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.library_activity_capture);

        scanPreview = (SurfaceView) findViewById(R.id.capture_preview);
        //在初始化相机的时候截取正方形
        mViewStub = (ViewStub) findViewById(R.id.viewStub);
        //给activity生命周期计时 当手机不处于充电状态，5分钟关闭该activity
        inactivityTimer = new InactivityTimer(this);
        //扫码成功后震动
        beepManager = new BeepManager(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //处理相机事务
        cameraManager = new CameraManager(getApplication());
        //将handler设置为空
        handler = null;
        //判断是否添加了callback
        if (isHasSurface) {
            initCamera(scanPreview.getHolder());
        } else {

            scanPreview.getHolder()
                    .addCallback(this);
        }
        //注册是否充电监听
        inactivityTimer.onResume();
    }

    @Override
    protected void onPause() {
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        inactivityTimer.onPause();
        beepManager.close();
        cameraManager.closeDriver();
        if (!isHasSurface) {
            scanPreview.getHolder()
                    .removeCallback(this);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    /**
     * 初始化相机
     *
     * @param surfaceHolder surfaceholder
     */
    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (cameraManager.isOpen()) {
            Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
            return;
        }
        try {
            cameraManager.openDriver(surfaceHolder);
            // Creating the handler starts the preview, which can also throw a
            // RuntimeException.
            if (handler == null) {
                handler = new CaptureActivityHandler(this, cameraManager, DecodeThread.ALL_MODE);
            }

            if (!isInflate) {
                FrameLayout layout = (FrameLayout) mViewStub.inflate();
                mViewfinderView = (ViewfinderView) layout.findViewById(R.id.viewfinder_view);
                initCrop();
                mViewfinderView.setGuideFrame(getScanRect());
                isInflate = true;
            }

        } catch (IOException ioe) {
            Log.w(TAG, ioe);
            displayFrameworkBugMessageAndExit();
        } catch (RuntimeException e) {
            // Barcode Scanner has seen crashes in the wild of this variety:
            // java.?lang.?RuntimeException: Fail to connect to camera service
            Log.w(TAG, "Unexpected error initializing camera", e);
            displayFrameworkBugMessageAndExit();
        }
    }

    /**
     * 初始化截取的矩形区域
     */
    private void initCrop() {
        int cameraWidth = cameraManager.getCameraResolution().y;
        int cameraHeight = cameraManager.getCameraResolution().x;

        // 扫描框的为正方形，边长为相机最短边长的1/2
        int cropLength = Math.min(cameraWidth, cameraHeight) / 2;

        /** 获取布局中扫描框的位置信息 */
        int[] location = new int[2];
        mViewfinderView.getLocationInWindow(location);

        int cropLeft = (cameraWidth - cropLength) / 2;
        int cropTop = (cameraHeight - cropLength) / 3;

        /** 生成最终的截取的矩形 */
        mCropRect = new Rect(cropLeft, cropTop, cropLeft + cropLength, cropTop + cropLength);

        /** 获取布局容器的宽高 */
        int containerWidth = scanPreview.getWidth();
        int containerHeight = scanPreview.getHeight();

        /** 计算屏幕显示最终截取的矩形的左上角顶点x坐标 */
        int x = cropLeft * containerWidth / cameraWidth;
        /** 计算屏幕显示最终截取的矩形的左上角顶点y坐标 */
        int y = cropTop * containerHeight / cameraHeight;

        /** 计算屏幕显示最终截取的矩形的宽度 */
        int width = cropLength * containerWidth / cameraWidth;
        /** 计算屏幕显示最终截取的矩形的高度 */
        int height = cropLength * containerHeight / cameraHeight;

        mScanRect = new Rect(x, y, width + x, height + y);
    }

    public Rect getScanRect() {
        return mScanRect;
    }

    public Rect getCropRect() {
        return mCropRect;
    }

    private void displayFrameworkBugMessageAndExit() {
        // camera error
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage("相机打开出错，请稍后重试");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                setResult(Constants.PERMISSION_DENY);
                finish();
            }

        });
        builder.show();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (holder == null) {
            Log.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
        }
        //初始化相机
        if (!isHasSurface) {
            isHasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isHasSurface = false;
    }

    /**
     * A valid barcode has been found, so give an indication of success and show
     * the results.
     *
     * @param rawResult The contents of the barcode.
     * @param bundle    The extras
     */
    public void handleDecode(Result rawResult, Bundle bundle) {
        inactivityTimer.onActivity();
        beepManager.playBeepSoundAndVibrate();

        bundle.putInt("width", mCropRect.width());
        bundle.putInt("height", mCropRect.height());
        bundle.putString("result", rawResult.getText());

        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }
}