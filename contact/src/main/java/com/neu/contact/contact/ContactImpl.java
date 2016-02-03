package com.neu.contact.contact;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.neu.contact.constants.Constants;
import com.neu.contact.contactui.ContactsPickerActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by neuyuandaima on 2015/12/31.
 * Contact接口的实现类
 */
public final class ContactImpl implements Contact {
    //号码类型
    private static final int PHONE_TYPE = 2;
    //持有activity
    private Activity mActivity;
    //持有fragment
    private Fragment mSupportFragment;
    //持有fragment
    private android.app.Fragment mFragment;
    //回调
    private ContactCallback mContactCallback;
    //选中的姓名
    private String mPhoneName;
    //选中的手机号码
    private String mPhoneNum;
    //选中的姓名的多手机号码
    private List<String> mPhoneNumList = new ArrayList<>();

    //构造器
    public ContactImpl(Activity activity) {
        mActivity = activity;
    }

    public ContactImpl(Fragment fragment) {
        mSupportFragment = fragment;
    }

    public ContactImpl(android.app.Fragment fragment) {
        mFragment = fragment;
    }

    @Override
    public void getContacts() {
        //判断传入的activity还是fragment
        if (mActivity != null) {
            Intent intent = new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);
            mActivity.startActivityForResult(intent, 2);
        } else if (mFragment != null) {
            Intent intent = new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);
            mFragment.startActivityForResult(intent, 2);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);
            mSupportFragment.startActivityForResult(intent, 2);
        }
    }

    @Override
    public void getContactsUI() {
        //判断传入的是activity还是fragment还是v4 fragment
        if (mActivity != null) {
            Intent intent = new Intent(mActivity, ContactsPickerActivity.class);
            mActivity.startActivityForResult(intent, 1);
        } else if (mSupportFragment != null) {
            Intent intent = new Intent(mSupportFragment.getActivity(), ContactsPickerActivity.class);
            mSupportFragment.startActivityForResult(intent, 1);
        } else {
            Intent intent = new Intent(mFragment.getActivity(), ContactsPickerActivity.class);
            mFragment.startActivityForResult(intent, 1);
        }
    }
    // TODO: 2015/12/31 fragment和activity 启动另外一个activity 的效果是不一样的

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data, ContactCallback callback) {
        //判断用户选择是的哪一种打开方式
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {//自定义UI
            mContactCallback = callback;
            String number = data.getStringExtra(ContactsPickerActivity.KEY_PHONE_NUMBER);
            String name = data.getStringExtra(ContactsPickerActivity.KEY_CONTACT_NAME);
            mContactCallback.onSuccess(number, name);
        } else if (resultCode == Activity.RESULT_OK) {//默认ui
            mContactCallback = callback;
            getContactPhone(data);
        }
    }

    /**
     * 获取联系人信息
     *
     * @param data 点击联系人返回的结果
     */
    private void getContactPhone(Intent data) {
        Uri contactData = data.getData();
        Cursor cursor;
        if (mFragment != null) {
            mActivity = mFragment.getActivity();
        } else if (mSupportFragment != null) {
            mActivity = mSupportFragment.getActivity();
        }
        cursor = mActivity
                .getContentResolver()
                .query(contactData, null, null, null, null);
        if (cursor == null || cursor.getCount() < 1) {
            sendBackResult(false, Constants.PERMISSION_DENY, "获取联系人失败");
        } else {
            handleCursor(cursor);
        }
    }

    private void sendBackResult(boolean result, int errCode, String errMsg) {
        if (!result) {
            mContactCallback.onFailed(errCode, errMsg);
        } else if (mPhoneNumList.size() < 2) {
            mContactCallback.onSuccess(mPhoneNum, mPhoneName);
        } else {
            showDialog();
        }
    }

    private void handleCursor(Cursor cursor) {
        cursor.moveToFirst();
        int phoneCount = cursor.getInt(cursor.getColumnIndex(Contacts.HAS_PHONE_NUMBER));
        if (phoneCount > 0) {
            // 获得联系人的ID号
            String contactId = cursor.getString(cursor.getColumnIndex(Contacts._ID));
            Cursor phone = mActivity.getContentResolver()
                    .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId, null, null);
            try {
                while (null != phone && phone.moveToNext()) {
                    mPhoneName = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    mPhoneNum = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    mPhoneNumList.add(mPhoneNum);
                }
            } finally {
                //当用户点击的该联系人无手机号码,但有其他联系方式
                if (mPhoneName == null || mPhoneNum == null || mPhoneNumList.size() == 0) {
                    sendBackResult(false, Constants.SELECTED_NO_MOBILE_PHONE, "该联系人无手机号码");
                } else {//成功
                    sendBackResult(true, 0, null);
                }
                //关闭cursor
                if (phone != null) {
                    phone.close();
                }
            }
        } else {
            //用户点击的联系人无联系方式
            sendBackResult(false, Constants.SELECTED_NO_PHONE, "该联系人无联系方式");
        }
        cursor.close();
    }

    /**
     * 当联系人有多个联系方式
     */
    private void showDialog() {
        ListView listView = new ListView(mActivity);
        final Dialog mDialog = new AlertDialog.Builder(mActivity, AlertDialog.THEME_DEVICE_DEFAULT_DARK).setView(listView)
                .create();
        mDialog.setTitle("请选择手机号码");
        listView.setAdapter(new ArrayAdapter<String>(mActivity, android.R.layout.simple_expandable_list_item_1, mPhoneNumList));
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (mDialog.isShowing()) {
                    mDialog.dismiss();
                }
                mPhoneNum = mPhoneNumList.get(position);
                mContactCallback.onSuccess(mPhoneNum, mPhoneName);
            }
        });

        mDialog.show();
    }
}
