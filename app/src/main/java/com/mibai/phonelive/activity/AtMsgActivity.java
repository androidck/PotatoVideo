package com.mibai.phonelive.activity;

import android.app.Dialog;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSON;
import com.mibai.phonelive.Constants;
import com.mibai.phonelive.R;
import com.mibai.phonelive.adapter.AtMsgAdapter;
import com.mibai.phonelive.bean.AtMsgBean;
import com.mibai.phonelive.bean.VideoBean;
import com.mibai.phonelive.custom.RefreshAdapter;
import com.mibai.phonelive.custom.RefreshView;
import com.mibai.phonelive.fragment.VideoCommentFragment;
import com.mibai.phonelive.http.HttpCallback;
import com.mibai.phonelive.http.HttpUtil;
import com.mibai.phonelive.interfaces.GlobalLayoutChangedListener;
import com.mibai.phonelive.presenter.GlobalLayoutPresenter;
import com.mibai.phonelive.utils.DialogUitl;
import com.mibai.phonelive.utils.WordUtil;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2018/7/21.
 * "@"消息Activity
 */

public class AtMsgActivity extends AbsActivity implements AtMsgAdapter.ActionListener, GlobalLayoutChangedListener {

    private RefreshView mRefreshView;
    private AtMsgAdapter mAdapter;
    private String mGetVideoInfoTag;
    private GlobalLayoutPresenter mPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_zan_msg;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.at_me));
        mRefreshView = (RefreshView) findViewById(R.id.refreshView);
        mRefreshView.setNoDataLayoutId(R.layout.view_no_data_at);
        mRefreshView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRefreshView.setDataHelper(new RefreshView.DataHelper<AtMsgBean>() {
            @Override
            public RefreshAdapter<AtMsgBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new AtMsgAdapter(mContext);
                    mAdapter.setActionListener(AtMsgActivity.this);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                HttpUtil.getAtMessages(p, callback);
            }

            @Override
            public List<AtMsgBean> processData(String[] info) {
                return JSON.parseArray(Arrays.toString(info), AtMsgBean.class);
            }

            @Override
            public void onRefresh(List<AtMsgBean> list) {

            }

            @Override
            public void onNoData(boolean noData) {

            }

            @Override
            public void onLoadDataCompleted(int dataCount) {

            }
        });
        mRefreshView.initData();
        mGetVideoInfoTag = String.valueOf(this.hashCode());
        mPresenter = new GlobalLayoutPresenter(this, findViewById(R.id.root));
    }


    @Override
    public void onAvatarClick(AtMsgBean bean) {
        if (bean != null) {
            OtherUserActivity.forwardOtherUser(mContext, bean.getUid());
        }
    }

    @Override
    public void onItemClick(AtMsgBean bean) {
        if (bean != null) {
            openCommentWindow(bean.getVideoid(), bean.getVideouid());
        }
    }

    @Override
    public void onVideoClick(AtMsgBean bean) {
        if (bean != null) {
            forwardVideoPlayActivity(bean.getVideoid());
        }
    }

    private void forwardVideoPlayActivity(String videoId) {
        HttpUtil.getVideoInfo(videoId, mGetVideoInfoTag, mGetVideoInfoCallback);
    }

    private HttpCallback mGetVideoInfoCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0 && info.length > 0) {
                VideoBean videoBean = JSON.parseObject(info[0], VideoBean.class);
                if (videoBean != null) {
                    VideoPlayActivity.forwardSingleVideoPlay(mContext, videoBean);
                }
            }
        }

        @Override
        public boolean showLoadingDialog() {
            return true;
        }

        @Override
        public Dialog createLoadingDialog() {
            return DialogUitl.loadingDialog(mContext);
        }
    };


    private void openCommentWindow(String videoId, String uid) {
        addLayoutListener();
        VideoCommentFragment fragment = new VideoCommentFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.VIDEO_ID, videoId);
        bundle.putString(Constants.UID, uid);
        bundle.putBoolean(Constants.FULL_SCREEN, true);
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "VideoCommentFragment");
    }

    @Override
    protected void onDestroy() {
        removeLayoutListener();
        HttpUtil.cancel(HttpUtil.GET_AT_MESSAGES);
        HttpUtil.cancel(mGetVideoInfoTag);
        super.onDestroy();
    }

    @Override
    public void addLayoutListener() {
        if (mPresenter != null) {
            mPresenter.addLayoutListener();
        }
    }

    @Override
    public void removeLayoutListener() {
        if (mPresenter != null) {
            mPresenter.removeLayoutListener();
        }
    }
}
