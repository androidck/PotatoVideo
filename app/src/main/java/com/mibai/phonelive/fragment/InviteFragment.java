package com.mibai.phonelive.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.mibai.phonelive.AppConfig;
import com.mibai.phonelive.Constants;
import com.mibai.phonelive.R;
import com.mibai.phonelive.bean.ConfigBean;
import com.mibai.phonelive.http.HttpCallback;
import com.mibai.phonelive.http.HttpUtil;
import com.mibai.phonelive.interfaces.CommonCallback;
import com.mibai.phonelive.utils.DialogUitl;
import com.mibai.phonelive.utils.DpUtil;
import com.mibai.phonelive.utils.MD5Util;
import com.mibai.phonelive.utils.ToastUtil;

/**
 * Created by cxf on 2017/9/30.
 * 邀请码输入框
 */

public class InviteFragment extends DialogFragment implements View.OnClickListener {

    private Context mContext;
    private View mRootView;
    private EditText mPwdEditText;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mContext = getContext();
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.fragment_invite, null);
        Dialog dialog = new Dialog(mContext, R.style.dialog2);
        dialog.setContentView(mRootView);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = DpUtil.dp2px(300);
        params.height = DpUtil.dp2px(180);
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPwdEditText = (EditText) mRootView.findViewById(R.id.pwd_text);
        mPwdEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 6) {
                    onInput(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mRootView.findViewById(R.id.btn_close).setOnClickListener(this);
    }


    private void onInput(final String content) {
        final Dialog dialog = DialogUitl.loadingDialog(mContext);
        dialog.show();
        HttpUtil.getConfig(new CommonCallback<ConfigBean>() {
            @Override
            public void callback(ConfigBean configBean) {
                String s = MD5Util.getMD5(content) + Constants.SIGN_1 + AppConfig.getInstance().getUid() + AppConfig.getInstance().getToken() + Constants.SIGN_2 + configBean.getDecryptSign() + Constants.SIGN_3;
                s = MD5Util.getMD5(s);
                HttpUtil.setDistribut(content, s, new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0) {
                            dismiss();
                            ToastUtil.show("设置成功");
                        } else {
                            ToastUtil.show(msg);
                        }
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }


    @Override
    public void onDestroy() {
        HttpUtil.cancel(HttpUtil.GET_CONFIG);
        HttpUtil.cancel(HttpUtil.SET_DISTRIBUT);
        super.onDestroy();
    }
}
