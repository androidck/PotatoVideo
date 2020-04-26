package com.mibai.phonelive.activity;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.alibaba.fastjson.JSON;
import com.mibai.phonelive.Constants;
import com.mibai.phonelive.R;
import com.mibai.phonelive.bean.ReportBean;
import com.mibai.phonelive.http.HttpCallback;
import com.mibai.phonelive.http.HttpUtil;
import com.mibai.phonelive.utils.DpUtil;
import com.mibai.phonelive.utils.ToastUtil;
import com.mibai.phonelive.utils.WordUtil;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2018/6/13.
 */

public class ReportActivity extends AbsActivity implements View.OnClickListener {

    private EditText mEditText;
    private RadioGroup mRadioGroup;
    private LayoutInflater mInflater;
    private ReportBean mCurReportBean;
    private static final int LAST_ITEM_ID = 11;
    private String mVideoId;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_report;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.report));
        mVideoId = getIntent().getStringExtra(Constants.VIDEO_ID);
        mInflater = LayoutInflater.from(mContext);
        mEditText = (EditText) findViewById(R.id.edit);
        findViewById(R.id.btn_submit).setOnClickListener(this);
        mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        HttpUtil.getReportList(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    List<ReportBean> list = JSON.parseArray(Arrays.toString(info), ReportBean.class);
                    if (list.size() > 0) {
                        int dp1 = DpUtil.dp2px(1);
                        int dp15 = DpUtil.dp2px(15);
                        for (int i = 0, size = list.size(); i < size; i++) {
                            ReportBean bean = list.get(i);
                            RadioButton radioButton = (RadioButton) mInflater.inflate(R.layout.item_list_report, mRadioGroup, false);
                            radioButton.setId(bean.getId());
                            radioButton.setText(bean.getName());
                            radioButton.setTag(bean);
                            radioButton.setOnClickListener(ReportActivity.this);
                            if (i == 0) {
                                mCurReportBean = bean;
                                radioButton.setChecked(true);
                            }
                            mRadioGroup.addView(radioButton);
                            if (i < size - 1) {
                                View view = new View(mContext);
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp1);
                                params.setMargins(dp15, 0, dp15, 0);
                                view.setLayoutParams(params);
                                view.setBackgroundColor(0x0fffffff);
                                mRadioGroup.addView(view);
                            }
                        }
                    }
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        if (v instanceof RadioButton) {
            ((RadioButton) v).setChecked(true);
            mCurReportBean = (ReportBean) v.getTag();
            if (mCurReportBean.getId() == LAST_ITEM_ID) {
                if (mEditText.getVisibility() != View.VISIBLE) {
                    mEditText.setVisibility(View.VISIBLE);
                }
            } else {
                if (mEditText.getVisibility() == View.VISIBLE) {
                    mEditText.setVisibility(View.GONE);
                }
            }
        } else {
            if (v.getId() == R.id.btn_submit) {
                report();
            }
        }
    }

    private void report() {
        if (mCurReportBean!=null){
            String content = mCurReportBean.getName();
            if (TextUtils.isEmpty(content)) {
                ToastUtil.show("举报理由不能为空");
            }
            if (mCurReportBean.getId() == LAST_ITEM_ID) {
                String s = mEditText.getText().toString();
                if (!TextUtils.isEmpty(s)) {
                    content = s;
                }
            }
            HttpUtil.reportVideo(mVideoId, content, mCurReportBean.getId(), new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0) {
                        ToastUtil.show(msg);
                        finish();
                    }
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        HttpUtil.cancel(HttpUtil.GET_REPORT_LIST);
        HttpUtil.cancel(HttpUtil.REPORT_VIDEO);
        super.onDestroy();
    }
}
