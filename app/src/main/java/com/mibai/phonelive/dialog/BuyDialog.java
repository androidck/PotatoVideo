package com.mibai.phonelive.dialog;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.mibai.phonelive.R;
import com.mibai.phonelive.purse.MyPurseActivity;

// 购买dialog
public class BuyDialog extends BaseCustomDialog implements View.OnClickListener {

    private TextView tv_tip, tv_esc, tv_buy_jb;
    private Context mContext;
    private String nickName;

    public BuyDialog(@NonNull Context context, String nickName) {
        super(context);
        this.mContext = context;
        this.nickName = nickName;
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_buy;
    }

    @Override
    public void initView() {
        tv_tip = findViewById(R.id.tv_tip);
        tv_esc = findViewById(R.id.tv_esc);
        tv_buy_jb = findViewById(R.id.tv_buy_jb);
        tv_esc.setOnClickListener(this);
        tv_buy_jb.setOnClickListener(this);
    }

    @Override
    public void initData() {
        tv_tip.setText("此视频为@" + nickName + " 上传发布，首次观看需要消耗10金币，您的金币余额不足此视频观看消费");
    }

    @Override
    public boolean isCancelable() {
        return false;
    }

    @Override
    public int showGravity() {
        return Gravity.BOTTOM;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_esc:
                dismiss();
                break;
            case R.id.tv_buy_jb:
                dismiss();
                mContext.startActivity(new Intent(mContext, MyPurseActivity.class));
                break;
        }
    }
}
