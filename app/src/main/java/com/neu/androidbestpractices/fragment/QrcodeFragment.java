package com.neu.androidbestpractices.fragment;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.neu.androidbestpractices.R;
import com.neu.androidbestpractices.fragment.base.BaseFragment;
import com.neu.qrcode.Qrcode;
import com.neu.qrcode.QrcodeFactory;
import com.neu.qrcode.callback.QrcodeCallback;
import com.neu.qrcode.qrcode.QrcodeInfo;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by neu on 16/2/23.
 */
public class QrcodeFragment extends BaseFragment {

    @Bind(R.id.qrCode)
    Button mQrCodeButton;
    @Bind(R.id.text)
    TextView mText;
    @Bind(R.id.image)
    ImageView mImageView;
    @OnClick(R.id.qrCode)
    void onClick(){
        mQrcode = QrcodeFactory.newQrcode(this);
        mQrcode.start();
    }
    private Qrcode mQrcode;

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_qrcode;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        mQrcode.onActivityResult(requestCode, resultCode, data, new QrcodeCallback() {
            @Override
            public void onSuccess(@NonNull QrcodeInfo info) {
                String textInfo = "二维码信息" + info.getResult() + "图片高度" + info.getHeight() + "图片宽度" + info.getWidth();
                mText.setText(textInfo);
                mImageView.setImageBitmap(info.getQrCodeImage());
            }

            @Override
            public void onFailed(@NonNull String errMsg) {
                mText.setText(errMsg);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        mQrcode.onRequestPermissionsResult(requestCode, permissions, grantResults, new com.neu.qrcode.callback.PermissionResultCallback() {
            @Override
            public void denyPermission() {
                mText.setText("App无权限");
            }
        });
    }
}
