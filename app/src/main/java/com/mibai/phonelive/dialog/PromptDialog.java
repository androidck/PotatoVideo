package com.mibai.phonelive.dialog;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mibai.phonelive.R;

// 自定义提示dialog
public class PromptDialog extends BaseCustomDialog {

    private Context mContext;
    private String mContent;

    private TextView tv_content;
    private Button btn_confirm;

    private OnItemClickListener onItemClickListener;


    public PromptDialog(@NonNull Context context, String content, OnItemClickListener onItemClickListener) {
        super(context);
        this.mContext = context;
        this.mContent = content;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_prompt;
    }

    @Override
    public void initView() {
        tv_content = findViewById(R.id.tv_content);
        btn_confirm = findViewById(R.id.btn_confirm);

        tv_content.setText(mContent);

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onClick();
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

    public interface OnItemClickListener {
        void onClick();
    }


}
