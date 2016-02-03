package com.neu.androidbestpractices.pojo;

/**
 * 每个item对应的pojo
 * Created by neu on 16/2/2.
 */
public class ItemTest {
    //模块名称
    public String itemName;
    //模块测试入口
    public String className;

    public ItemTest(String itemName, String className) {
        this.itemName = itemName;
        this.className = className;
    }
}
