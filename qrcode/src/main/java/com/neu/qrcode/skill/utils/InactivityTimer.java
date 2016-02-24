/*
 * Copyright (C) 2010 ZXing authors
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

package com.neu.qrcode.skill.utils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.util.Log;

/**
 * Finishes an activity after a period of inactivity if the device is on battery
 * power.
 */
public class InactivityTimer {

    private static final String TAG = InactivityTimer.class.getSimpleName();
    //5分钟后，该activity自动关闭
    private static final long INACTIVITY_DELAY_MS = 5 * 60 * 1000L;

    private Activity activity;
    private BroadcastReceiver powerStatusReceiver;
    //是否注册广播接收器
    private boolean registered;
    //异步内部类
    private AsyncTask<Object, Object, Object> inactivityTask;

    /**
     * 构造器
     *
     * @param activity
     */
    public InactivityTimer(Activity activity) {
        this.activity = activity;
        powerStatusReceiver = new PowerStatusReceiver();
        registered = false;
        onActivity();
    }

    /**
     * 关闭当前线程，开启线程执行
     */
    public synchronized void onActivity() {
        cancel();
        inactivityTask = new InactivityAsyncTask();
        inactivityTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * 取消异步线程
     */
    private synchronized void cancel() {
        AsyncTask<?, ?, ?> task = inactivityTask;
        if (task != null) {
            task.cancel(true);
            inactivityTask = null;
        }
    }

    /**
     * cancel线程，注销监听
     */
    public synchronized void onPause() {
        cancel();
        if (registered) {
            activity.unregisterReceiver(powerStatusReceiver);
            registered = false;
        } else {
            Log.w(TAG, "PowerStatusReceiver was never registered?");
        }
    }

    /**
     * 注册监听
     */
    public synchronized void onResume() {
        if (registered) {
            Log.w(TAG, "PowerStatusReceiver was already registered?");
        } else {
            activity.registerReceiver(powerStatusReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            registered = true;
        }
        onActivity();
    }

    /**
     * activity被destory
     */
    public void shutdown() {
        cancel();
    }

    private class PowerStatusReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                // 0 indicates that we're on battery 判断手机是否充电
                boolean onBatteryNow = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) <= 0;
                if (onBatteryNow) {
                    InactivityTimer.this.onActivity();
                } else {
                    InactivityTimer.this.cancel();
                }
            }
        }
    }

    private class InactivityAsyncTask extends AsyncTask<Object, Object, Object> {
        @Override
        protected Object doInBackground(Object... objects) {
            try {
                Thread.sleep(INACTIVITY_DELAY_MS);
                Log.i(TAG, "Finishing activity due to inactivity");
                activity.finish();
            } catch (InterruptedException e) {
                // continue without killing
            }
            return null;
        }
    }

}
