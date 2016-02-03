package com.neu.androidbestpractices.activity.base;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.neu.androidbestpractices.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * activity的基类
 * Created by neu on 16/2/3.
 */
public abstract class BaseActivity extends AppCompatActivity {
    @Bind(R.id.toolBar)
    Toolbar mToolBar;
    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = View.inflate(this, R.layout.common_activity, null);
        setContentView(view);
        ButterKnife.bind(this);

        //设置工具栏
        setSupportActionBar(mToolBar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mCollapsingToolbar.setTitle(getToolbarTitle());
        getFragmentManager().beginTransaction().replace(R.id.content_frame,getFragment()).commit();

        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

    }

    protected abstract Activity getActivity();

    protected abstract Fragment getFragment();

    protected abstract CharSequence getToolbarTitle();
}
