package com.neu.contact.contact;

import android.content.Intent;

/**
 * Created by neuyuandaima on 2015/12/31.
 */
public interface Contact {
    //通过自有页面展示联系人
    void getContacts();
    //通过手机app页面展示
    void getContactsUI();
    //展示结果
    void onActivityResult(int requestCode, int resultCode, Intent data, ContactCallback callback);
    //android 6.0的动态权限
    void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults,PermissionResultCallback callback);

}
