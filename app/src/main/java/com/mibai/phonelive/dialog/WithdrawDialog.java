package com.mibai.phonelive.dialog;

import android.content.Context;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.mibai.phonelive.R;
import com.mibai.phonelive.utils.ToastUtil;

// 提现dialog
public class WithdrawDialog extends BaseCustomDialog implements View.OnClickListener {

    private Context mContext;
    private int count;

    private EditText ed_number, ec_account;
    private TextView tv_esc, tv_confirm;

    private OnSuccessListener onSuccessListener;

    public WithdrawDialog(@NonNull Context context, int count, OnSuccessListener onSuccessListener) {
        super(context);
        this.mContext = context;
        this.count = count;
        this.onSuccessListener = onSuccessListener;
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_withdraw;
    }

    @Override
    public void initView() {
        ed_number = findViewById(R.id.ed_number);
        tv_esc = findViewById(R.id.tv_esc);
        tv_confirm = findViewById(R.id.tv_confirm);
        ec_account = findViewById(R.id.ed_account);
        tv_esc.setOnClickListener(this);
        tv_confirm.setOnClickListener(this);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_esc:
                dismiss();
                break;
            case R.id.tv_confirm:

                if (TextUtils.isEmpty(ec_account.getText().toString().trim())) {
                    ToastUtil.show("请输入提现的金币");
                    return;
                }
                if (TextUtils.isEmpty(ec_account.getText().toString().trim())) {
                    ToastUtil.show("支付宝账号不能为空");
                    return;
                }
                long str = Long.parseLong(ed_number.getText().toString().trim());
                if (count <= 0) {
                    ToastUtil.show("可提现金币不足");
                } else if (count < str) {
                    ToastUtil.show("可提现金币不足");
                } else if (TextUtils.isEmpty(ec_account.getText().toString().trim())) {
                    ToastUtil.show("支付宝账号不能为空");
                } else {
                    dismiss();
                    onSuccessListener.onSuccess(ed_number.getText().toString().trim(), ec_account.getText().toString().trim());
                }
                break;
        }
    }

    public interface OnSuccessListener {
        void onSuccess(String money, String account);
    }
}
