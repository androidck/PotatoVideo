package com.mibai.phonelive.activity;

import android.content.Intent;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSON;
import com.mibai.phonelive.Constants;
import com.mibai.phonelive.R;
import com.mibai.phonelive.adapter.AdminMsgAdapter;
import com.mibai.phonelive.bean.AdminMsgBean;
import com.mibai.phonelive.custom.RefreshAdapter;
import com.mibai.phonelive.custom.RefreshView;
import com.mibai.phonelive.http.HttpCallback;
import com.mibai.phonelive.http.HttpUtil;
import com.mibai.phonelive.interfaces.OnItemClickListener;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2018/7/27.
 */

public class AdminMsgActivity extends AbsActivity implements OnItemClickListener<AdminMsgBean> {

    private RefreshView mRefreshView;
    private AdminMsgAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_admin_msg;
    }

    @Override
    protected void main() {
        setTitle(Constants.YB_NAME_1);
        mRefreshView = (RefreshView) findViewById(R.id.refreshView);
        mRefreshView.setNoDataLayoutId(R.layout.view_no_data_default);
        mRefreshView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRefreshView.setDataHelper(new RefreshView.DataHelper<AdminMsgBean>() {
            @Override
            public RefreshAdapter<AdminMsgBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new AdminMsgAdapter(mContext);
                    mAdapter.setOnItemClickListener(AdminMsgActivity.this);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                HttpUtil.getAdminMsgList(p, callback);
            }

            @Override
            public List<AdminMsgBean> processData(String[] info) {
                return JSON.parseArray(Arrays.toString(info), AdminMsgBean.class);
            }

            @Override
            public void onRefresh(List<AdminMsgBean> list) {

            }

            @Override
            public void onNoData(boolean noData) {

            }

            @Override
            public void onLoadDataCompleted(int dataCount) {
                if (dataCount < 20) {
                    mRefreshView.setLoadMoreEnable(false);
                } else {
                    mRefreshView.setLoadMoreEnable(true);
                }
            }
        });
        mRefreshView.initData();
    }

    @Override
    protected void onDestroy() {
        HttpUtil.cancel(HttpUtil.GET_ADMIN_MSG_LIST);
        super.onDestroy();
    }

    @Override
    public void onItemClick(AdminMsgBean bean, int position) {
        Intent intent=new Intent(mContext,WebActivity.class);
        intent.putExtra(Constants.URL,bean.getUrl());
        startActivity(intent);
    }
}
