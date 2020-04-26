package com.mibai.phonelive.dialog;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;

import com.mibai.phonelive.R;

public class LoginDialog extends BaseCustomDialog {

    private Context mContext;
    private Button btnLogin;
    private OnClickListener onClickListener;

    public LoginDialog(@NonNull Context context,OnClickListener onClickListener) {
        super(context);
        this.mContext = context;
        this.onClickListener=onClickListener;
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_login_prompt;
    }

    @Override
    public void initView() {
        btnLogin = findViewById(R.id.btn_confirm);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                onClickListener.onClick();
            }
        });
    }

    @Override
    public void initData() {

    }

    @Override
    public boolean isCancelable() {
        return false;
    }

    @Override
    public int showGravity() {
        return Gravity.CENTER;
    }

    public interface OnClickListener{
        void onClick();
    }
}
