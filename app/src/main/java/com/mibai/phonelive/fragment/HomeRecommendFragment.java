package com.mibai.phonelive.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mibai.phonelive.AppConfig;
import com.mibai.phonelive.Constants;
import com.mibai.phonelive.R;
import com.mibai.phonelive.activity.LoginActivity;
import com.mibai.phonelive.activity.MainActivity;
import com.mibai.phonelive.activity.WebActivity;
import com.mibai.phonelive.bean.VideoBean;
import com.mibai.phonelive.custom.VideoPlayWrap;
import com.mibai.phonelive.event.DoubleEvent;
import com.mibai.phonelive.event.FollowEvent;
import com.mibai.phonelive.event.NeedRefreshLikeEvent;
import com.mibai.phonelive.http.HttpCallback;
import com.mibai.phonelive.http.HttpUtil;
import com.mibai.phonelive.interfaces.GlobalLayoutChangedListener;
import com.mibai.phonelive.utils.ToastUtil;
import com.mibai.phonelive.utils.WordUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * Created by cxf on 2018/6/9.
 * 首页推荐
 */

public class HomeRecommendFragment extends AbsFragment {

    private VideoPlayFragment mVideoPlayFragment;
    // 评论
    private VideoCommentFragment mCommentFragment;
    // 分享
    private VideoShareFragment mShareFragment;
    private long mLastClickTime;
    private View mLoadingGroup;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home_recommend;
    }

    @Override
    protected void main() {
        mLoadingGroup = mRootView.findViewById(R.id.loading_group);
        mVideoPlayFragment = new VideoPlayFragment();
        mVideoPlayFragment.setDataHelper(new VideoPlayFragment.DataHelper() {
            @Override
            public void initData(HttpCallback callback) {
                HttpUtil.getRecommendVideos(1, callback);
            }

            @Override
            public void loadMoreData(int p, HttpCallback callback) {
                HttpUtil.getRecommendVideos(p, callback);
            }

            @Override
            public int getInitPosition() {
                return 0;
            }

            @Override
            public List<VideoBean> getInitVideoList() {
                return null;
            }

            @Override
            public int getInitPage() {
                return 0;
            }

            @Override
            public void setCode(int code) {

            }

        });
        mVideoPlayFragment.setActionListener(mActionListener);
        mVideoPlayFragment.setOnInitDataCallback(mOnInitDataCallback);
        FragmentManager fragmentManager = getChildFragmentManager();
        if (fragmentManager != null) {
            FragmentTransaction tx = fragmentManager.beginTransaction();
            tx.replace(R.id.replaced, mVideoPlayFragment);
            tx.commit();
        }
        EventBus.getDefault().register(this);
    }

    public void hiddenChanged(boolean hidden) {
        if (mVideoPlayFragment != null) {
            mVideoPlayFragment.hiddenChanged(hidden);
        }
    }

    private VideoPlayFragment.OnInitDataCallback mOnInitDataCallback = new VideoPlayFragment.OnInitDataCallback() {
        @Override
        public void onInitSuccess() {
            if (mLoadingGroup != null && mLoadingGroup.getVisibility() == View.VISIBLE) {
                mLoadingGroup.setVisibility(View.INVISIBLE);
            }
        }
    };

    private VideoPlayWrap.ActionListener mActionListener = new VideoPlayWrap.ActionListener() {
        @Override
        public void onZanClick(final VideoPlayWrap wrap, VideoBean bean) {
            if (!canClick()) {
                return;
            }
            if (AppConfig.getInstance().isLogin()) {
                if (AppConfig.getInstance().getUid().equals(bean.getUid())) {
                    ToastUtil.show(WordUtil.getString(R.string.cannot_zan_self));
                    return;
                }
                String videoId = bean.getId();
                if (!TextUtils.isEmpty(videoId)) {
                    HttpUtil.setVideoLike(videoId, new HttpCallback() {
                        @Override
                        public void onSuccess(int code, String msg, String[] info) {
                            if (code == 0 && info.length > 0) {
                                JSONObject obj = JSON.parseObject(info[0]);
                                int islike = obj.getIntValue("islike");
                                wrap.setLikes(islike, obj.getString("likes"));
                                EventBus.getDefault().post(new NeedRefreshLikeEvent());
                            }
                        }
                    });
                }
            } else {
                LoginActivity.forwardLogin(mContext);
            }
        }

        @Override
        public void onCommentClick(VideoPlayWrap wrap, VideoBean bean) {
            if (!canClick()) {
                return;
            }
            ((GlobalLayoutChangedListener) mContext).addLayoutListener();
            mCommentFragment = new VideoCommentFragment();
            mCommentFragment.setVideoPlayWrap(wrap);
            Bundle bundle = new Bundle();
            bundle.putString(Constants.VIDEO_ID, bean.getId());
            bundle.putString(Constants.UID, bean.getUid());
            bundle.putBoolean(Constants.FULL_SCREEN, false);
            mCommentFragment.setArguments(bundle);
            if (!mCommentFragment.isAdded()) {
                mCommentFragment.show(((MainActivity) mContext).getSupportFragmentManager(), "VideoShareFragment");
            }
        }

        @Override
        public void onFollowClick(final VideoPlayWrap wrap, VideoBean bean) {
            if (!canClick()) {
                return;
            }
            if (AppConfig.getInstance().isLogin()) {
                if (AppConfig.getInstance().getUid().equals(bean.getUid())) {
                    ToastUtil.show(WordUtil.getString(R.string.cannot_follow_self));
                    return;
                }
                final String touid = bean.getUid();
                if (!TextUtils.isEmpty(touid)) {
                    HttpUtil.setAttention(touid, null);
                }
            } else {
                LoginActivity.forwardLogin(mContext);
            }
        }

        @Override
        public void onAvatarClick(VideoPlayWrap wrap, VideoBean bean) {
            if (!canClick()) {
                return;
            }
            ((MainActivity) mContext).showUserInfo();
        }

        @Override
        public void onShareClick(final VideoPlayWrap wrap, final VideoBean bean) {
            if (!canClick()) {
                return;
            }
            mShareFragment = new VideoShareFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable(Constants.VIDEO_BEAN, bean);
            mShareFragment.setArguments(bundle);
            mShareFragment.setActionListener(new VideoShareFragment.ActionListener() {
                @Override
                public void onShareSuccess() {
                    HttpUtil.setVideoShare(bean.getId(), new HttpCallback() {
                        @Override
                        public void onSuccess(int code, String msg, String[] info) {
                            if (code == 0 && info.length > 0) {
                                JSONObject obj = JSON.parseObject(info[0]);
                                wrap.setShares(obj.getString("shares"));
                            }
                        }
                    });
                }
            });
            if (!mShareFragment.isAdded()) {
                mShareFragment.show(((MainActivity) mContext).getSupportFragmentManager(), "VideoShareFragment");
            }
        }

        @Override
        public void onAdvClick(VideoPlayWrap wrap, VideoBean bean) {
            if (!canClick()) {
                return;
            }
            Intent intent = new Intent(getActivity(), WebActivity.class);
            intent.putExtra(Constants.URL, bean.getAd_url());
            intent.putExtra(Constants.TITLE, bean.getAd_title());
            startActivity(intent);
        }

    };

    @Override
    public void onDestroyView() {
        HttpUtil.cancel(HttpUtil.GET_RECOMMEND_VIDEOS);
        HttpUtil.cancel(HttpUtil.SET_VIDEO_SHARE);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        if (mVideoPlayFragment != null) {
            mVideoPlayFragment.setDataHelper(null);
            mVideoPlayFragment.setActionListener(null);
        }
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFollowEvent(FollowEvent e) {
        if (mVideoPlayFragment != null) {
            mVideoPlayFragment.refreshVideoAttention(e.getTouid(), e.getIsAttention());
        }
    }

    private boolean canClick() {
        long timeStamp = System.currentTimeMillis();
        if (timeStamp - mLastClickTime < 1000) {
            return false;
        } else {
            mLastClickTime = timeStamp;
            return true;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDoubleEvent(DoubleEvent e) {
        VideoBean bean = e.bean;
        VideoPlayWrap wrap = e.wrap;
        if (bean != null) {
            if (!canClick()) {
                return;
            }
            if (AppConfig.getInstance().isLogin()) {
                if (AppConfig.getInstance().getUid().equals(bean.getUid())) {
                    ToastUtil.show(WordUtil.getString(R.string.cannot_zan_self));
                    return;
                }
                String videoId = bean.getId();
                if (!TextUtils.isEmpty(videoId)) {
                    HttpUtil.setVideoLike(videoId, new HttpCallback() {
                        @Override
                        public void onSuccess(int code, String msg, String[] info) {
                            if (code == 0 && info.length > 0) {
                                JSONObject obj = JSON.parseObject(info[0]);
                                int islike = obj.getIntValue("islike");
                                wrap.setLikes(islike, obj.getString("likes"));
                                EventBus.getDefault().post(new NeedRefreshLikeEvent());
                            }
                        }
                    });
                }
            } else {
                LoginActivity.forwardLogin(mContext);
            }
        }
    }
}
