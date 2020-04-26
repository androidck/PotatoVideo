package com.mibai.phonelive.dialog;

import android.content.Context;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mibai.phonelive.R;

// 次数用完dialog
public class FrequencyDialog extends BaseCustomDialog {

    private Context mContext;
    private OnItemClickListener onItemClickListener;
    private Button btnShare;
    private String content;
    private TextView textView;

    public FrequencyDialog(@NonNull Context context, String content, OnItemClickListener onItemClickListener) {
        super(context);
        this.mContext = context;
        this.content = content;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_frequency;
    }

    @Override
    public void initView() {
        btnShare = findViewById(R.id.btn_share);
        textView = findViewById(R.id.tv_content);
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onClick();
            }
        });
        if (TextUtils.isEmpty(content)){
            textView.setText("~ 成功推广1人，送3天无限观看，可无限叠加 ~");
        }else {
            textView.setText(content);
        }

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
