package com.mibai.phonelive.dialog;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mibai.phonelive.R;

// 提示购买dialog
public class BuyNewDialog extends BaseCustomDialog implements View.OnClickListener {

    private TextView tv_content;
    private Button btn_esc, btn_confirm;

    private OnSuccessListener onSuccessListener;

    private Context mContext;
    private String money;

    public BuyNewDialog(@NonNull Context context, String money, OnSuccessListener onSuccessListener) {
        super(context);
        this.mContext = context;
        this.money = money;
        this.onSuccessListener = onSuccessListener;
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_buy_new;
    }

    @Override
    public void initView() {
        tv_content = findViewById(R.id.tv_content);
        btn_esc = findViewById(R.id.btn_esc);
        btn_confirm = findViewById(R.id.btn_confirm);
        btn_esc.setOnClickListener(this);
        btn_confirm.setOnClickListener(this);
    }

    @Override
    public void initData() {
        tv_content.setText("该视频是收费视频，您需要支付" + money + "金币才能观看");
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
            case R.id.btn_esc:
                dismiss();
                break;
            case R.id.btn_confirm:
                onSuccessListener.onClick(1);
                dismiss();
                break;
        }
    }

    public interface OnSuccessListener {
        void onClick(int state);
    }


}
