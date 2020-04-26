package com.mibai.phonelive.fragment;

import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

import androidx.viewpager.widget.ViewPager;

import com.alibaba.fastjson.JSON;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mibai.phonelive.AppConfig;
import com.mibai.phonelive.Constants;
import com.mibai.phonelive.R;
import com.mibai.phonelive.activity.MainActivity;
import com.mibai.phonelive.activity.VideoPlayActivity;
import com.mibai.phonelive.adapter.VideoPlayAdapter;
import com.mibai.phonelive.bean.VideoBean;
import com.mibai.phonelive.custom.LoadingBar;
import com.mibai.phonelive.custom.VerticalViewPager;
import com.mibai.phonelive.custom.VideoPlayView;
import com.mibai.phonelive.custom.VideoPlayWrap;
import com.mibai.phonelive.event.NeedRefreshEvent;
import com.mibai.phonelive.event.VideoDeleteEvent;
import com.mibai.phonelive.http.HttpCallback;
import com.mibai.phonelive.http.HttpUtil;
import com.mibai.phonelive.interfaces.VideoChangeListener;
import com.mibai.phonelive.network.OkHttp;
import com.mibai.phonelive.utils.HawkUtil;
import com.mibai.phonelive.utils.L;
import com.mibai.phonelive.wrap.PlayHomeProgressWrap;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import okhttp3.Request;


/**
 * Created by cxf on 2018/6/5.
 * 短视频播放的fragment 可以上下滑动
 */

public class VideoPlayFragment extends AbsFragment implements ViewPager.OnPageChangeListener, VideoPlayWrap.OnVideoCodeListener, VideoPlayWrap.OnBuyTipListener, VideoPlayView.OnProgressVideoListener, VideoPlayView.OnProgressMainListener {

    private VerticalViewPager mViewPager;
    private VideoPlayView mPlayView;
    private LoadingBar mLoading;
    private VideoPlayAdapter mAdapter;
    private int mOuterViewPagerPosition;//外层ViewPager的position
    private boolean mHidden;//是否hidden
    private int mPage = 1;//分页加载的页数
    private DataHelper mDataHelper;
    private VideoPlayWrap.ActionListener mActionListener;
    private boolean mPaused;
    private boolean mNeedRefresh;
    private VideoBean mNeedDeleteVideoBean;
    private OnInitDataCallback mOnInitDataCallback;
    private int mLastPosition;
    private boolean mStartWatch;
    private boolean mEndWatch;
    private int codeIng;
    private int count = 0;

    private String videoId;

    private String moneyStr;// 价值金币
    private String isBuyStr;// 是否购买了

    private SeekBar seekBar;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_video_play;
    }

    @Override
    protected void main() {
        if (mContext instanceof VideoPlayActivity) {
            View btnBack = mRootView.findViewById(R.id.btn_back);
            View commentGroup = mRootView.findViewById(R.id.comment_group);
            btnBack.setVisibility(View.VISIBLE);
            commentGroup.setVisibility(View.VISIBLE);
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.btn_back:
                            ((VideoPlayActivity) mContext).onBackPressed();
                            break;
                        case R.id.comment_group:
                            ((VideoPlayActivity) mContext).openCommentWindow();
                            break;
                    }

                }
            };
            btnBack.setOnClickListener(listener);
            commentGroup.setOnClickListener(listener);
            VideoPlayView.setOnProgressVideoListener(this);
        }
        seekBar = mRootView.findViewById(R.id.view_seekbar);
        mLoading = (LoadingBar) mRootView.findViewById(R.id.loading);
        mLoading.setLoading(true);
        mViewPager = (VerticalViewPager) mRootView.findViewById(R.id.viewPager);
        mViewPager.setOffscreenPageLimit(0);
        mViewPager.setOnPageChangeListener(this);
        if (mDataHelper != null) {
            List<VideoBean> list = mDataHelper.getInitVideoList();
            if (list != null && list.size() > 0) {
                initAdapter(list);
                int initPosition = mDataHelper.getInitPosition();
                if (initPosition >= 0 && initPosition < list.size()) {
                    mLastPosition = initPosition;
                    mViewPager.setCurrentItem(initPosition);
                }
            } else {
                mDataHelper.initData(mInitCallback);
            }
            int initPage = mDataHelper.getInitPage();
            if (initPage > 0) {
                mPage = initPage;
            }
        }
        EventBus.getDefault().register(this);
        VideoPlayWrap.setOnVideoCodeListener(this);
        VideoPlayWrap.setOnBuyTipListener(this);
        VideoPlayView.setOnProgressMainListener(this);
        Detionfre();
        // 进度条监听事件
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // 拖动开始
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // 拖动结束
                mPlayView.setProgress(seekBar.getProgress());
            }
        });

        Log.d("当前类名：", this.getClass().getName());

    }


    private void initAdapter(List<VideoBean> list) {
        mAdapter = new VideoPlayAdapter(mContext, list);
        mAdapter.setOnPlayVideoListener(new VideoPlayAdapter.OnPlayVideoListener() {
            @Override
            public void onPlayVideo(VideoBean bean) {
                if (mContext instanceof VideoChangeListener) {
                    ((VideoChangeListener) mContext).changeVideo(bean);
                }
                mStartWatch = false;
                mEndWatch = false;
            }
        });
        mAdapter.setActionListener(mActionListener);
        mPlayView = mAdapter.getVideoPlayView();
        mPlayView.setPlayEventListener(mPlayEventListener);
        mPlayView.setType(Constants.FOLLOW_TYPE);
        mViewPager.setAdapter(mAdapter);
        mAdapter.setViewPager(mViewPager);

       /* mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });*/

    }

    public void setDataHelper(DataHelper helper) {
        mDataHelper = helper;
    }

    private HttpCallback mInitCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0 && info.length > 0) {
                List<VideoBean> list = JSON.parseArray(Arrays.toString(info), VideoBean.class);
                if (list.size() > 0) {
                    if (mAdapter == null) {
                        initAdapter(list);
                    }
                }
                if (mOnInitDataCallback != null) {
                    mOnInitDataCallback.onInitSuccess();
                }
            }
        }
    };


    private HttpCallback mLoadMoreCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0 && info.length > 0) {
                List<VideoBean> list = JSON.parseArray(Arrays.toString(info), VideoBean.class);
                if (list.size() > 0) {
                    if (mAdapter != null) {
                        mAdapter.insertList(list);
                    }
                } else {
                    mPage--;
                }
            } else {
                mPage--;
            }
        }
    };

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }


    @Override
    public void onPageSelected(int position) {
        if (codeIng != 10030) {
            Detionfre();
        }
        if (mLastPosition != position) {
            if (mLastPosition < position) {
                if (mAdapter != null) {
                    int count = mAdapter.getCount();
                    if (count > 2 && position == count - 2) {
                        L.e("VideoPlayFragment-------->分页加载数据");
                        mPage++;
                        if (mDataHelper != null) {
                            mDataHelper.loadMoreData(mPage, mLoadMoreCallback);
                        }
                    }
                }
            }
            mLastPosition = position;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    private VideoPlayView.PlayEventListener mPlayEventListener = new VideoPlayView.PlayEventListener() {
        @Override
        public void onReadyPlay() {
            if (mLoading != null) {
                mLoading.setLoading(true);
            }
        }

        @Override
        public void onVideoSizeChanged(int width, int height) {

        }

        @Override
        public void onError() {

        }

        @Override
        public void onLoading() {
            if (mLoading != null) {
                mLoading.setLoading(true);
            }
        }

        @Override
        public void onPlay() {
            if (mLoading != null) {
                mLoading.setLoading(false);
            }
        }

        @Override
        public void onFirstFrame() {
            if (mOuterViewPagerPosition != 0 || mHidden) {
                if (mPlayView != null) {
                    mPlayView.pausePlay();
                }
            }
            if (!mStartWatch) {
                mStartWatch = true;
                VideoBean videoBean = mAdapter.getCurWrap().getVideoBean();
                if (videoBean != null) {
                    if (!AppConfig.getInstance().isLogin() ||
                            !AppConfig.getInstance().getUid().equals(videoBean.getUid())) {
                        HttpUtil.startWatchVideo(videoBean.getId());
                    }
                }
            }
        }

        @Override
        public void onPlayEnd() {
            if (!mEndWatch) {
                mEndWatch = true;
                VideoBean videoBean = mAdapter.getCurWrap().getVideoBean();
                if (videoBean != null) {
                    HttpUtil.endWatchVideo(videoBean.getId());
                }
            }
        }

    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNeedRefreshEvent(NeedRefreshEvent e) {
        mNeedRefresh = true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVideoDeleteEvent(VideoDeleteEvent e) {
        VideoBean bean = e.getVideoBean();
        if (bean != null) {
            if (mPaused) {
                mNeedDeleteVideoBean = bean;
            } else {
                if (mAdapter != null) {
                    mAdapter.removeItem(bean);
                }
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPlayHomeProgressWrap(PlayHomeProgressWrap wrap) {
        int progressAll = wrap.progressAll;
        int type = wrap.progressAll;
        if (type == 1) {
            seekBar.setMax(progressAll);
        } else {
            seekBar.setMax(progressAll);
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        mPaused = true;
        if (mPlayView != null) {
            mPlayView.onPause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPlayView != null) {
            mPlayView.onResume();
        }
        if (mPaused) {
            mPaused = false;
            if (mNeedDeleteVideoBean == null) {
                if (mNeedRefresh && mAdapter != null) {
                    mAdapter.refreshCurrentVideoInfo();
                    mNeedRefresh = false;
                }
            } else {
                if (mAdapter != null) {
                    mAdapter.removeItem(mNeedDeleteVideoBean);
                }
                mNeedDeleteVideoBean = null;
            }
        }
    }

    /**
     * back键返回的时候销毁playView
     */
    public void backDestroyPlayView() {
        if (mPlayView != null) {
            mPlayView.destroy();
            mPlayView = null;
        }
    }

    @Override
    public void onDestroy() {
        HttpUtil.cancel(HttpUtil.START_WATCH_VIDEO);
        HttpUtil.cancel(HttpUtil.END_WATCH_VIDEO);
        if (mLoading != null) {
            mLoading.endLoading();
        }
        if (mPlayView != null) {
            mPlayView.destroy();
            mPlayView = null;
        }
        if (mAdapter != null) {
            mAdapter.release();
        }
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    public void hiddenChanged(boolean hidden) {
        if (mHidden == hidden) {
            return;
        }
        mHidden = hidden;
        if (mOuterViewPagerPosition == 0 && mPlayView != null) {
            if (hidden) {
                mPlayView.pausePlay();
            } else {
                mPlayView.resumePlay();
            }
        }
    }

    /**
     * 外层ViewPager滑动事件
     */
    public void onOuterPageSelected(int position) {
        mOuterViewPagerPosition = position;
        if (mPlayView != null) {
            if (position == 0) {
                mPlayView.resumePlay();
            } else {
                mPlayView.pausePlay();
            }
        }
    }

    public void refreshVideoAttention(String uid, int isAttetion) {
        if (mAdapter != null) {
            mAdapter.refreshVideoAttention(uid, isAttetion);
        }
    }

    public VideoBean getCurVideoBean() {
        if (mAdapter != null) {
            return mAdapter.getCurVideoBean();
        }
        return null;
    }

    public VideoPlayWrap getCurWrap() {
        if (mAdapter != null) {
            return mAdapter.getCurWrap();
        }
        return null;
    }

    @Override
    public void onVideoCode(int code, String msg, String id) {
        codeIng = code;
        videoId = id;
        mAdapter.setCodeIng(codeIng, msg);

    }

    @Override
    public void onTip(String money, String isbuy) {
        //  ToastUtil.show("money：" + money + "---- isbuy：" + isbuy);
    }

    @Override
    public void onProgressVideo(int progress) {
        if (mContext instanceof VideoPlayActivity) {
            seekBar.setProgress(progress);
        }
    }

    @Override
    public void onProgressMain(int progress) {
        if (mContext instanceof MainActivity) {
            seekBar.setProgress(progress);
        }
    }

    public interface DataHelper {
        //初始化数据
        void initData(HttpCallback callback);

        //加载更多
        void loadMoreData(int p, HttpCallback callback);

        //初始化的position
        int getInitPosition();

        //初始化的视频列表
        List<VideoBean> getInitVideoList();

        //获取初始的页数
        int getInitPage();

        void setCode(int code);
    }

    public void setActionListener(VideoPlayWrap.ActionListener listener) {
        mActionListener = listener;
    }

    public interface OnInitDataCallback {
        void onInitSuccess();
    }

    public void setOnInitDataCallback(OnInitDataCallback onInitDataCallback) {
        mOnInitDataCallback = onInitDataCallback;
    }


    // 扣除次数
    public void Detionfre() {
        String url = HttpUtil.HTTP_URL + "service=Video.detionfre&uid=" + AppConfig.getInstance().getUid() + "&videoid=" + HawkUtil.getInstance().getSaveData(HawkUtil.VIDEO_ID);
        OkHttp.getAsync(url, new OkHttp.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                JsonObject object = new JsonParser().parse(result).getAsJsonObject();
                JsonObject jsonObject = object.getAsJsonObject("data");
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                e.printStackTrace();
            }
        });
    }

}
