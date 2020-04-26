package com.mibai.phonelive.custom;

import android.content.Context;
import android.os.CountDownTimer;
import android.widget.Button;

/**
 * Description:倒计时
 */

public class CustomCountDownTimer extends CountDownTimer {
    private Button mBtnGetVerificationCode;
    private Context mContext;

    public CustomCountDownTimer(long millisInFuture, long countDownInterval, Context context, Button btnGetVerificationCode) {
        super(millisInFuture, countDownInterval);
        this.mBtnGetVerificationCode = btnGetVerificationCode;
        this.mContext = context;
    }

    @Override
    public void onFinish() {
        mBtnGetVerificationCode.setText("关闭");
        mBtnGetVerificationCode.setEnabled(true);
    }

    @Override
    public void onTick(long millisUntilFinished) {
        mBtnGetVerificationCode.setText(((millisUntilFinished / 1000) + 1) + "s");
        mBtnGetVerificationCode.setEnabled(false);
    }
}
