package com.neu.androidbestpractices.fragment;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.Button;
import android.widget.TextView;

import com.neu.androidbestpractices.R;
import com.neu.androidbestpractices.fragment.base.BaseFragment;
import com.neu.contact.ContactFactory;
import com.neu.contact.contact.ContactCallback;
import com.neu.contact.contact.PermissionResultCallback;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by neu on 16/2/3.
 */
public class ContactFragment extends BaseFragment{
    @Bind(R.id.contact)
    Button mContact;
    @Bind(R.id.text)
    TextView mText;
    @Bind(R.id.contactUI)
    Button mContactUI;
    @OnClick({R.id.contact,R.id.contactUI})
    void testContactModule(Button button){
        switch (button.getId()){
            case R.id.contact:

                ContactFactory.newContact(this).getContacts();
                break;
            case R.id.contactUI:
                ContactFactory.newContact(this).getContactsUI();
                break;
        }
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_contact;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        ContactFactory.newContact(this).onActivityResult(requestCode, resultCode, data, new ContactCallback() {
            @Override
            public void onSuccess(@NonNull String contactNumber, @NonNull String contactName) {
                mText.setText(contactName + contactNumber);
            }

            @Override
            public void onFailed(@NonNull int errCode, @NonNull String message) {
                mText.setText(errCode + message);
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        ContactFactory.newContact(this).onRequestPermissionsResult(requestCode, permissions, grantResults, new PermissionResultCallback() {

            @Override
            public void denyPermission() {
                mText.setText("用户已经拒绝");
            }
        });
    }
}
