package com.mibai.phonelive.community.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.mibai.phonelive.R;
import com.mibai.phonelive.dialog.BaseCustomDialog;

// 照片类型选择
public class MenuDialog extends BaseCustomDialog implements View.OnClickListener {

    private TextView tv_video, tv_photo, tv_esc;

    private OnItemClickListener onItemClickListener;
    private Context mContext;

    public MenuDialog(@NonNull Context context, OnItemClickListener onItemClickListener) {
        super(context);
        this.mContext = context;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_menu;
    }

    @Override
    public void initView() {
        tv_video = findViewById(R.id.tv_video);
        tv_photo = findViewById(R.id.tv_photo);
        tv_esc = findViewById(R.id.tv_esc);

        tv_video.setOnClickListener(this);
        tv_photo.setOnClickListener(this);
        tv_esc.setOnClickListener(this);
    }

    @Override
    public void initData() {

    }

    @Override
    public boolean isCancelable() {
        return true;
    }

    @Override
    public int showGravity() {
        return Gravity.BOTTOM;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_video:
                onItemClickListener.onClick(1);
                dismiss();
                break;
            case R.id.tv_photo:
                onItemClickListener.onClick(0);
                dismiss();
                break;
            case R.id.tv_esc:
                dismiss();
                break;
        }
    }

    public interface OnItemClickListener {
        void onClick(int position);
    }
}
