package com.mibai.phonelive.custom;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mibai.phonelive.AppConfig;
import com.mibai.phonelive.activity.MainActivity;
import com.mibai.phonelive.activity.VideoPlayActivity;
import com.mibai.phonelive.bean.StatusBean;
import com.mibai.phonelive.bean.VideoBean;
import com.mibai.phonelive.event.DoubleEvent;
import com.mibai.phonelive.http.HttpUtil;
import com.mibai.phonelive.network.OkHttp;
import com.mibai.phonelive.view.DoubleClickListener;
import com.mibai.phonelive.wrap.PlayHomeProgressWrap;
import com.mibai.phonelive.wrap.PlayProgressEntry;
import com.mibai.phonelive.wrap.PlayViewProgressWrap;
import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.ITXVodPlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXVodPlayConfig;
import com.tencent.rtmp.TXVodPlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.mibai.phonelive.R;
import com.mibai.phonelive.utils.L;
import com.mibai.phonelive.utils.ScreenDimenUtil;
import com.mibai.phonelive.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.Formatter;
import java.util.Locale;

import okhttp3.Request;

/**
 * Created by cxf on 2018/6/5.
 */

public class VideoPlayView extends FrameLayout implements View.OnClickListener, VideoPlayWrap.OnVideoCodeListener {

    private static final String TAG = "VideoPlayView";
    private TXCloudVideoView mVideoView;
    private TXVodPlayer mPlayer;
    private View mPlayBtn;
    private String mUrl;
    private PlayEventListener mPlayEventListener;
    private Context mContext;
    private boolean mStarted;
    private boolean mPaused;//是否切后台了
    private boolean mDestoryed;
    private boolean mClickPausePlay;//是否手动暂停了播放
    private boolean mPausePlay;//是否被动暂停了播放
    private boolean mFirstFrame;
    private int mScreenWidth;
    private ObjectAnimator mAnimator;
    private TXVodPlayConfig mTXVodPlayConfig;

    private RelativeLayout relativeLayout;

    private VideoPlayWrap mPlayWrap;

    private VideoBean videoBean;

    private int newProgress;

    public static OnDoubleClickListener listeners;

    private long maxPlayLength = 15;// 非会员 最大播放时长

    // 记录播放时长
    private long startPlayLength = 0;

    long mLastTime = 0;
    long mCurTime = 0;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if (mClickPausePlay) {
                        clickResumePlay();
                    } else {
                        clickPausePlay();
                        String url = videoBean.getAd_url();
                        if (!TextUtils.isEmpty(url)) {
                            if (url.startsWith("http") || url.startsWith("https")) {
                                Intent intent = new Intent();
                                intent.setAction("android.intent.action.VIEW");
                                Uri content_url = Uri.parse(url);
                                intent.setData(content_url);
                                mContext.startActivity(intent);
                            }
                        }
                    }
                    break;
                case 2:
                    EventBus.getDefault().post(DoubleEvent.getInstance(mPlayWrap, videoBean));
                    break;
            }
        }
    };


    public static OnProgressMainListener progressMainListener;

    private static OnProgressVideoListener progressVideoListener;

    public VideoPlayView(Context context) {
        this(context, null);
    }

    public VideoPlayView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoPlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mScreenWidth = ScreenDimenUtil.getInstance().getScreenWdith();

    }

    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_video_play, this, false);
        addView(view);
        mPlayBtn = view.findViewById(R.id.btn_play);
        relativeLayout = view.findViewById(R.id.root);
        relativeLayout.setOnClickListener(this);
        mVideoView = (TXCloudVideoView) view.findViewById(R.id.player);
        mVideoView.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);
        mPlayer = new TXVodPlayer(mContext);
        mPlayer.setPlayerView(mVideoView);
        mPlayer.setAutoPlay(true);
        mPlayer.setPlayListener(mPlayListener);
        mPlayer.setVodListener(listener);
        //mPlayer.seek();


        mAnimator = ObjectAnimator.ofPropertyValuesHolder(mPlayBtn,
                PropertyValuesHolder.ofFloat("scaleX", 3f, 1f),
                PropertyValuesHolder.ofFloat("scaleY", 3f, 1f),
                PropertyValuesHolder.ofFloat("alpha", 0f, 1f));
        mAnimator.setDuration(120);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mPlayer.pause();


    }


    private ITXLivePlayListener mPlayListener = new ITXLivePlayListener() {
        @Override
        public void onPlayEvent(int e, Bundle bundle) {
            switch (e) {
                case TXLiveConstants.PLAY_EVT_PLAY_BEGIN:
                    if (!mDestoryed) {
                        L.e(TAG, "VideoPlayView------>播放开始");
                        if (mPlayEventListener != null) {
                            mPlayEventListener.onPlay();
                        }
                        if (mPlayWrap != null) {
                            mPlayWrap.startMusicAnim();
                        }
                    } else {
                        doDestroy();
                    }
                    break;
                case TXLiveConstants.PLAY_ERR_NET_DISCONNECT:
                case TXLiveConstants.PLAY_ERR_FILE_NOT_FOUND:
                    ToastUtil.show(mContext.getString(R.string.mp4_error));
                    if (mPlayEventListener != null) {
                        mPlayEventListener.onError();
                    }
                    break;
                case TXLiveConstants.PLAY_EVT_PLAY_LOADING:
                    if (mPlayEventListener != null) {
                        mPlayEventListener.onLoading();
                    }
                    break;
                case TXLiveConstants.PLAY_EVT_RCV_FIRST_I_FRAME:
                    if (!mDestoryed) {
                        mFirstFrame = true;
                        L.e(TAG, "VideoPlayView------>第一帧");
                        if (mPaused) {
                            mPlayer.pause();
                        }
                        if (mPlayWrap != null) {
                            mPlayWrap.hideBg();
                        }
                        if (mPlayEventListener != null) {
                            mPlayEventListener.onFirstFrame();
                        }
                    } else {
                        doDestroy();
                    }
                    break;
                case TXLiveConstants.PLAY_EVT_PLAY_END:
                    onReplay();
                    if (mPlayEventListener != null) {
                        mPlayEventListener.onPlayEnd();
                    }
                    break;
                case TXLiveConstants.PLAY_EVT_CHANGE_RESOLUTION:
                    int width = bundle.getInt("EVT_PARAM1", 0);
                    int height = bundle.getInt("EVT_PARAM2", 0);
                    if (!mDestoryed && width > 0 && height > 0) {
                        videoSizeChanged(width, height);
                        if (mPlayEventListener != null) {
                            mPlayEventListener.onVideoSizeChanged(width, height);
                        }
                    }
                    break;

            }
        }

        @Override
        public void onNetStatus(Bundle bundle) {

        }
    };

    private ITXVodPlayListener listener = new ITXVodPlayListener() {
        @Override
        public void onPlayEvent(TXVodPlayer txVodPlayer, int i, Bundle bundle) {
            switch (i) {
                case TXLiveConstants.PLAY_EVT_PLAY_PROGRESS:
                    // 播放进度, 单位是秒
                    if (mContext instanceof VideoPlayActivity) {

                        if (mClickPausePlay != true) {
                            int progress = bundle.getInt(TXLiveConstants.EVT_PLAY_PROGRESS);
                            int duration = bundle.getInt(TXLiveConstants.EVT_PLAY_DURATION);
                            EventBus.getDefault().post(PlayHomeProgressWrap.getInstance(1, duration));
                            progressVideoListener.onProgressVideo(progress);
                        }
                    }

                    if (mContext instanceof MainActivity) {
                        if (mClickPausePlay != true && mPausePlay != true) {
                            int progress = bundle.getInt(TXLiveConstants.EVT_PLAY_PROGRESS);
                            int duration = bundle.getInt(TXLiveConstants.EVT_PLAY_DURATION);
                            EventBus.getDefault().post(PlayHomeProgressWrap.getInstance(2, duration));
                            progressMainListener.onProgressMain(progress);
                        }
                    }

                    int duration = bundle.getInt(TXLiveConstants.EVT_PLAY_PROGRESS);
                    if (duration == maxPlayLength) {
                        onReplay();
                    }
                    break;

            }
        }

        @Override
        public void onNetStatus(TXVodPlayer txVodPlayer, Bundle bundle) {

        }
    };


    /**
     * 循环播放
     */
    private void onReplay() {
        if (mStarted && mPlayer != null) {
            mPlayer.seek(0);
            mPlayer.resume();
        }
    }


    /**
     * 加载视频成功后调用
     */
    private void videoSizeChanged(int width, int height) {
        L.e(TAG, "videoSizeChanged---width--->" + width);
        L.e(TAG, "videoSizeChanged---height-->" + height);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mVideoView.getLayoutParams();
        if (width >= height) {//横屏视频
            float rate = ((float) width) / height;
            params.height = (int) (mScreenWidth / rate);
        } else {
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        }
        mVideoView.setLayoutParams(params);
        mVideoView.requestLayout();
    }

    // 设置videoBean
    public void setVideoBean(VideoBean bean) {
        this.videoBean = bean;
    }

    public void play(final String url) {
        OkHttp.getAsync(HttpUtil.HTTP_URL + "service=User.ifree&uid=" + AppConfig.getInstance().getUid(), new OkHttp.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                JsonObject jsonParser = new JsonParser().parse(result).getAsJsonObject();
                JsonObject object = jsonParser.getAsJsonObject("data");
                JsonObject jsonObject = object.getAsJsonObject("info");
                StatusBean beans = new Gson().fromJson(jsonObject.toString(), StatusBean.class);
                if (beans.getStatus() == 400) {
                    return;
                } else {
                    mFirstFrame = false;
                    mPausePlay = false;
                    mClickPausePlay = false;
                    if (mPlayBtn != null && mPlayBtn.getVisibility() == VISIBLE) {
                        mPlayBtn.setVisibility(INVISIBLE);
                    }
                    if (!mDestoryed && mPlayer != null) {
                        if (TextUtils.isEmpty(url) || url.equals(mUrl)) {
                            return;
                        }
                        mUrl = url;
                        if (mStarted) {
                            mPlayer.stopPlay(false);
                        }
                        mPlayer.startPlay(mUrl);

                        mStarted = true;
                        L.e(TAG, "play------->" + mUrl);
                    }
                    if (mPlayEventListener != null) {
                        mPlayEventListener.onReadyPlay();
                    }
                }
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                e.printStackTrace();
            }
        });

    }

    public void destroy() {
        if (!mDestoryed) {
            doDestroy();
        }
    }

    private void doDestroy() {
        mDestoryed = true;
        if (mPlayer != null) {
            mPlayer.stopPlay(true);
        }
        if (mVideoView != null) {
            mVideoView.onDestroy();
        }
        L.e(TAG, "destroy------->");
    }

    public void setPlayWrap(VideoPlayWrap wrap) {
        mPlayWrap = wrap;
        L.e(TAG, "setPlayWrap------->" + wrap.hashCode());
    }

    // 设置播放进度
    public void setProgress(int progress) {
        if (mPlayer != null) {
            mPlayer.seek(progress);
        }
    }

    /**
     * 取消切后台，返回前台
     */
    public void onResume() {
        if (!mPausePlay && !mClickPausePlay && mPaused && !mDestoryed && mPlayer != null) {
            mPaused = false;
            mPlayer.resume();
            if (mPlayWrap != null) {
                mPlayWrap.startMusicAnim();
            }
        }
    }


    /**
     * 切后台
     */
    public void onPause() {
        if (!mPausePlay && !mClickPausePlay && !mPaused && !mDestoryed && mPlayer != null) {
            mPaused = true;
            mPlayer.pause();
//            if (mPlayWrap != null) {
//                mPlayWrap.pauseMusicAnim();
//            }
        }
    }


    /**
     * 手动暂停播放
     */
    public void clickPausePlay() {
        if (mFirstFrame && !mPausePlay && !mClickPausePlay) {
            mClickPausePlay = true;
            if (mPlayBtn != null && mPlayBtn.getVisibility() != VISIBLE) {
                mPlayBtn.setVisibility(VISIBLE);
                mAnimator.start();
            }
            mPlayer.pause();
        }
    }


    /**
     * 手动恢复播放
     */
    private void clickResumePlay() {
        if (mFirstFrame && !mPausePlay && mClickPausePlay) {
            mClickPausePlay = false;
            if (mPlayBtn != null && mPlayBtn.getVisibility() == VISIBLE) {
                mPlayBtn.setVisibility(INVISIBLE);
            }
            mPlayer.resume();
        }
    }

    /**
     * 被动暂停播放
     */
    public void pausePlay() {
        if (mFirstFrame && !mClickPausePlay && !mPausePlay) {
            mPausePlay = true;
            mPlayer.pause();
        }
    }

    public void pausePlays() {
        mPausePlay = true;
        mPlayer.pause();
    }


    /**
     * 被动恢复播放
     */
    public void resumePlay() {
        if (mFirstFrame && !mClickPausePlay && mPausePlay) {
            mPausePlay = false;
            mPlayer.resume();
            if (mPlayWrap != null) {
                mPlayWrap.startMusicAnim();
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.root:
                mLastTime = mCurTime;
                mCurTime = System.currentTimeMillis();
                if (mCurTime - mLastTime < 300) {//双击事件
                    mCurTime = 0;
                    mLastTime = 0;
                    handler.removeMessages(1);
                    handler.sendEmptyMessage(2);
                } else {//单击事件
                    handler.sendEmptyMessageDelayed(1, 310);
                }

                break;
        }
    }

    public void setPlayEventListener(PlayEventListener playEventListener) {
        mPlayEventListener = playEventListener;
    }

    @Override
    public void onVideoCode(int code, String msg, String id) {

    }

    public interface PlayEventListener {

        void onReadyPlay();

        void onVideoSizeChanged(int width, int height);

        void onError();

        void onLoading();

        void onPlay();

        void onFirstFrame();

        void onPlayEnd();

    }

    // 首页回调监听
    public interface OnProgressMainListener {
        void onProgressMain(int progress);
    }

    public interface OnProgressVideoListener {
        void onProgressVideo(int progress);
    }

    public static void setOnProgressMainListener(OnProgressMainListener onProgressMainListener) {
        progressMainListener = onProgressMainListener;
    }

    public static void setOnProgressVideoListener(OnProgressVideoListener onProgressVideoListener) {
        progressVideoListener = onProgressVideoListener;
    }

    public interface OnDoubleClickListener {
        void onDoubleClick(VideoBean v);
    }

    public static void setOnDoubleClickListener(OnDoubleClickListener onDoubleClickListener) {
        listeners = onDoubleClickListener;
    }

}
