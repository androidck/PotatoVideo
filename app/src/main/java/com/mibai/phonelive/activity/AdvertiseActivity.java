package com.mibai.phonelive.activity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.mibai.phonelive.Constants;
import com.mibai.phonelive.R;
import com.mibai.phonelive.custom.CustomCountDownTimer;
import com.mibai.phonelive.utils.AdvertiseUtils;

import butterknife.BindView;
import butterknife.OnClick;

public class AdvertiseActivity extends AbsActivity {

    @BindView(R.id.iv_advertise)
    ImageView mIvImage;
    @BindView(R.id.btn_jump)
    Button mBtnJump;
    private CustomCountDownTimer mCountDownTimer;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_advertise;
    }

    @Override
    protected void main() {
        super.main();
        String path = AdvertiseUtils.getAdvLocPath(this);
        Glide.with(this).load(path).into(mIvImage);

        mCountDownTimer = new CustomCountDownTimer(5 * 1000 , 1000, this, mBtnJump);
        mCountDownTimer.start();
        mBtnJump.setEnabled(false);
    }


    @OnClick({R.id.btn_jump, R.id.iv_advertise})
    public void onClickListener(View v) {
        switch (v.getId()) {
            case R.id.btn_jump:
                startActivity(new Intent(AdvertiseActivity.this, MainActivity.class));
                finish();
                break;

            case R.id.iv_advertise:
                Intent intent = new Intent(AdvertiseActivity.this, WebActivity.class);
                intent.putExtra(Constants.URL, AdvertiseUtils.getAdvInfo(this).url);
                startActivity(intent);
                break;
        }
    }
}
