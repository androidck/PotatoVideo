package com.mibai.phonelive.activity;


import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSON;
import com.mibai.phonelive.R;
import com.mibai.phonelive.adapter.FansMsgAdapter;
import com.mibai.phonelive.bean.FansMsgBean;
import com.mibai.phonelive.custom.RefreshAdapter;
import com.mibai.phonelive.custom.RefreshView;
import com.mibai.phonelive.http.HttpCallback;
import com.mibai.phonelive.http.HttpUtil;
import com.mibai.phonelive.utils.WordUtil;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2018/7/21.
 */

public class FansMsgActivity extends AbsActivity {

    private RefreshView mRefreshView;
    private FansMsgAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_fans_msg;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.fans));
        mRefreshView = (RefreshView) findViewById(R.id.refreshView);
        mRefreshView.setNoDataLayoutId(R.layout.view_no_data_fans);
        mRefreshView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRefreshView.setDataHelper(new RefreshView.DataHelper<FansMsgBean>() {
            @Override
            public RefreshAdapter<FansMsgBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new FansMsgAdapter(mContext);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                HttpUtil.getFansMessages(p, callback);
            }

            @Override
            public List<FansMsgBean> processData(String[] info) {
                return JSON.parseArray(Arrays.toString(info), FansMsgBean.class);
            }

            @Override
            public void onRefresh(List<FansMsgBean> list) {

            }
            @Override
            public void onNoData(boolean noData) {

            }

            @Override
            public void onLoadDataCompleted(int dataCount) {

            }
        });
        mRefreshView.initData();
    }

    @Override
    protected void onDestroy() {
        HttpUtil.cancel(HttpUtil.GET_FANS_MESSAGES);
        super.onDestroy();
    }
}
