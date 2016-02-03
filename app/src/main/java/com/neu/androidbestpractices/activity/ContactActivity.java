package com.neu.androidbestpractices.activity;


import android.app.Activity;
import android.app.Fragment;

import com.neu.androidbestpractices.activity.base.BaseActivity;
import com.neu.androidbestpractices.fragment.ContactFragment;

/**
 * 联系人界面
 * Created by neu on 16/2/3.
 */
public class ContactActivity extends BaseActivity{
    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected Fragment getFragment() {
        return new ContactFragment();
    }

    @Override
    protected CharSequence getToolbarTitle() {
        return "联系人测试";
    }
}
