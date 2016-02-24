package com.neu.androidbestpractices.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.neu.androidbestpractices.R;
import com.neu.androidbestpractices.adapter.CardViewAdapter;
import com.neu.androidbestpractices.pojo.ItemTest;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        //设置layoutmanager
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);

        //设置adapter
        CardViewAdapter adapter = new CardViewAdapter(getList(), this);
        recyclerView.setAdapter(adapter);


        Activity activity = this;
        activity.findViewById(R.id.collapsing_toolbar);
    }

    /**
     * 数据
     * @return
     */
    private List<ItemTest> getList() {
        List<ItemTest> list = new ArrayList<>();
        list.add(new ItemTest("联系人模块",ContactActivity.class.getCanonicalName()));
        list.add(new ItemTest("二维码模块",QrcodeActivity.class.getCanonicalName()));

        return list;

    }
}
