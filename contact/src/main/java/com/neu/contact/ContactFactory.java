package com.neu.contact;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.neu.contact.contact.Contact;
import com.neu.contact.contact.ContactImpl;
import com.neu.contact.util.Utils;


/**
 * Created by neuyuandaima on 2015/12/31.
 * 联系人构建工厂
 */
public class ContactFactory {
    /**
     * 传入activity的Contact
     * @param activity  传入参数
     * @return  返回Contact接口对象
     */
    public static Contact newContact(Activity activity) {
        Utils.checkNotNull(activity, "Activity could not null or other object");
        return new ContactImpl(activity);
    }

    /**
     * 传入fragment的Contact
     * @param fragment  传入参数
     * @return  返回Contact接口对象
     */
    public static Contact newContact(Fragment fragment) {
        Utils.checkNotNull(fragment,"Fragment could not null or other object");
        return new ContactImpl(fragment);
    }

    /**
     * 传入fragment的Contact
     *
     * @param fragment 传入参数
     * @return 返回Contact接口对象
     */
    public static Contact newContact(android.app.Fragment fragment) {
        Utils.checkNotNull(fragment, "Fragment could not null or other object");
        return new ContactImpl(fragment);
    }
}
