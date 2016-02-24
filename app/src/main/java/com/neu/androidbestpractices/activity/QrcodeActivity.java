package com.neu.androidbestpractices.activity;

import android.app.Activity;
import android.app.Fragment;

import com.neu.androidbestpractices.activity.base.BaseActivity;
import com.neu.androidbestpractices.fragment.QrcodeFragment;

/**
 * Created by neu on 16/2/23.
 */
public class QrcodeActivity extends BaseActivity{
    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected Fragment getFragment() {
        return new QrcodeFragment();
    }

    @Override
    protected CharSequence getToolbarTitle() {
        return "扫描二维码测试";
    }
}
