package com.mibai.phonelive.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mibai.phonelive.AppConfig;
import com.mibai.phonelive.R;
import com.mibai.phonelive.activity.PopularizeActivity;
import com.mibai.phonelive.activity.VideoPlayActivity;
import com.mibai.phonelive.bean.VideoBean;
import com.mibai.phonelive.bean.VideoBuyBean;
import com.mibai.phonelive.custom.VerticalViewPager;
import com.mibai.phonelive.custom.VideoPlayView;
import com.mibai.phonelive.custom.VideoPlayWrap;
import com.mibai.phonelive.dialog.BuyDialog;
import com.mibai.phonelive.dialog.BuyNewDialog;
import com.mibai.phonelive.dialog.FrequencyDialog;
import com.mibai.phonelive.event.NeedRefreshBuyEvent;
import com.mibai.phonelive.http.HttpUtil;
import com.mibai.phonelive.network.OkHttp;
import com.mibai.phonelive.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import okhttp3.Request;


/**
 * Created by cxf on 2017/12/20.
 */

public class VideoPlayAdapter extends PagerAdapter implements VideoPlayView.OnDoubleClickListener {

    private Context mContext;
    private LayoutInflater mInflater;
    private LinkedList<VideoPlayWrap> mViewList;
    private LinkedList<VideoPlayWrap> mAllList;
    private VideoPlayView mPlayView;
    private List<VideoBean> mVideoList;
    private VideoPlayWrap mCurWrap;
    private OnPlayVideoListener mOnPlayVideoListener;
    private VideoPlayWrap.ActionListener mActionListener;
    private VerticalViewPager mViewPager;
    private int code;
    private String message;
    private FrequencyDialog frequencyDialog;
    private int count;

    private BuyNewDialog buyNewDialog;
    private BuyDialog buyDialog;


    public VideoPlayAdapter(Context context, List<VideoBean> videoList) {
        mContext = context;
        mVideoList = videoList;
        mViewList = new LinkedList<>();
        mAllList = new LinkedList<>();
        mInflater = LayoutInflater.from(context);
        mPlayView = (VideoPlayView) mInflater.inflate(R.layout.view_video_layout_play, null, false);
        mPlayView.setOnDoubleClickListener(this);
    }

    public void setCodeIng(int code, String msg) {
        this.code = code;
        this.message = msg;
    }

    public void isAdd(int count) {
        this.count = count;
    }

    @Override
    public int getCount() {
        return mVideoList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        VideoPlayWrap wrap = null;
        if (mViewList.size() > 0) {
            wrap = mViewList.getFirst();
            mViewList.removeFirst();
        } else {
            wrap = (VideoPlayWrap) mInflater.inflate(R.layout.view_video_layout_wrap, container, false);
            wrap.setActionListener(mActionListener);
            mAllList.add(wrap);
        }

        wrap.loadData(mVideoList.get(position));
        // 传递ID
        wrap.loadVideoId(mVideoList.get(position).getId());
        container.addView(wrap);

        return wrap;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (object != null) {
            VideoPlayWrap videoWrap = (VideoPlayWrap) object;
            videoWrap.clearData();
            container.removeView(videoWrap);
            mViewList.addLast(videoWrap);
        }
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        VideoBean videoBean = mVideoList.get(position);
        if (mCurWrap != object) {
            if (mCurWrap != null) {
                mCurWrap.removePlayView();
            }
            mCurWrap = (VideoPlayWrap) object;
            mCurWrap.addPlayView(mPlayView);
            if (AppConfig.getInstance().isLogin() && code != 10030) {
                if (videoBean != null) {
                    if (videoBean.getIsbuy() == 2 || videoBean.getIsbuy() == 1) {
                        mCurWrap.play();
                    } else {
                        buyNewDialog(position, videoBean.getId(), String.valueOf(videoBean.getMoney()));
                        mCurWrap.stop();
                    }

                }
            } else {
                shareDialog();
            }
            if (mOnPlayVideoListener != null) {
                mOnPlayVideoListener.onPlayVideo(mCurWrap.getVideoBean());
            }
        }
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    public VideoPlayView getVideoPlayView() {
        return mPlayView;
    }


    public void insertList(List<VideoBean> list) {
        if (mVideoList != null) {
            mVideoList.addAll(list);
        }
        notifyDataSetChanged();
    }

    public void removeItem(VideoBean bean) {
        int size = mVideoList.size();
        if (size > 1) {
            int videoPosition = -1;
            for (int i = 0; i < size; i++) {
                if (mVideoList.get(i).getId().equals(bean.getId())) {
                    videoPosition = i;
                    break;
                }
            }
            if (videoPosition >= 0 && videoPosition < mVideoList.size()) {
                mVideoList.remove(videoPosition);
                notifyDataSetChanged();
                mCurWrap = (VideoPlayWrap) mViewPager.getChildAt(mViewPager.getCurrentItem());
                mCurWrap.showBg();
                if (AppConfig.getInstance().isLogin() && code != 10030) {
                    if (bean != null) {
                        if (bean.getIsbuy() == 2 || bean.getIsbuy() == 1) {
                            mCurWrap.play();
                        } else {
                            buyNewDialog(videoPosition, bean.getId(), String.valueOf(bean.getMoney()));
                            mCurWrap.stop();
                        }
                    }

                } else {
                    shareDialog();
                }
                if (mOnPlayVideoListener != null) {
                    mOnPlayVideoListener.onPlayVideo(mCurWrap.getVideoBean());
                }
            }
        } else {
            if (mVideoList.get(0).getId().equals(bean.getId())) {
                if (mContext instanceof VideoPlayActivity) {
                    ((VideoPlayActivity) mContext).onBackPressed();
                }
            }
        }
    }

    /**
     * 刷新当前播放页面的关注点赞等信息
     */
    public void refreshCurrentVideoInfo() {
        if (mAllList != null) {
            for (VideoPlayWrap wrap : mAllList) {
                if (wrap != null) {
                    wrap.getVideoInfo();
                }
            }
        }
    }

    /**
     * 刷新视频的关注信息
     */
    public void refreshVideoAttention(String uid, int isAttetion) {
        if (mAllList != null) {
            for (VideoPlayWrap wrap : mAllList) {
                if (wrap != null) {
                    VideoBean bean = wrap.getVideoBean();
                    if (bean != null && uid.equals(bean.getUid())) {
                        wrap.setIsAttent(isAttetion);
                    }
                }
            }
        }
    }

    public void release() {
        if (mAllList != null) {
            for (VideoPlayWrap wrap : mAllList) {
                wrap.release();
            }
        }
        mActionListener = null;
        mOnPlayVideoListener = null;
    }

    @Override
    public void onDoubleClick(VideoBean v) {
        ToastUtil.show("视频id" + v.getId());
    }

    /**
     * 回调出去可以让外面知道播放的是哪个视频
     */
    public interface OnPlayVideoListener {
        void onPlayVideo(VideoBean bean);
    }


    public void setOnPlayVideoListener(OnPlayVideoListener listener) {
        mOnPlayVideoListener = listener;
    }

    public void setActionListener(VideoPlayWrap.ActionListener listener) {
        mActionListener = listener;
    }

    public VideoPlayWrap getCurWrap() {
        return mCurWrap;
    }

    public VideoBean getCurVideoBean() {
        if (mCurWrap != null) {
            return mCurWrap.getVideoBean();
        }
        return null;
    }

    public void setViewPager(VerticalViewPager viewPager) {
        mViewPager = viewPager;
    }

    public void shareDialog() {
        frequencyDialog = new FrequencyDialog(mContext, message, new FrequencyDialog.OnItemClickListener() {
            @Override
            public void onClick() {
                Intent intent = new Intent(mContext, PopularizeActivity.class);
                intent.putExtra("uid", AppConfig.getInstance().getUid());
                mContext.startActivity(intent);
                frequencyDialog.dismiss();
            }
        });
        Context context = frequencyDialog.getContext();
        int divierId = context.getResources().getIdentifier("android:id/titleDivider", null, null);
        View divider = frequencyDialog.findViewById(divierId);
        if (divider != null) {
            divider.setBackgroundColor(Color.TRANSPARENT);
        }
        frequencyDialog.show();
    }


    // 购买dialog
    public void buyNewDialog(final int position, final String videoId, String money) {
        buyNewDialog = new BuyNewDialog(mContext, money, new BuyNewDialog.OnSuccessListener() {
            @Override
            public void onClick(int state) {
                if (state == 1) {
                    videoPay(position, videoId);
                }
            }
        });
        Context context = buyNewDialog.getContext();
        int divierId = context.getResources().getIdentifier("android:id/titleDivider", null, null);
        View divider = buyNewDialog.findViewById(divierId);
        if (divider != null) {
            divider.setBackgroundColor(Color.TRANSPARENT);
        }
        buyNewDialog.show();
    }

    // 视频支付
    public void videoPay(final int position, String vid) {
        String url = HttpUtil.HTTP_URL + "service=Video.buyVideo&uid=" + AppConfig.getInstance().getUid() + "&vid=" + vid;
        OkHttp.getAsync(url, new OkHttp.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                Log.d("videoPayResult", result);
                // {"ret":200,"data":{"code":200,"msg":"\u4f60\u6709\u94b1\u4f60\u725b\u903c","info":{"uid":298466,"vid":42,"addtime":1583257455,"id":"16"}},"msg":""}
                // {"ret":200,"data":{"code":400,"msg":"\u4f60\u6ca1\u94b1","info":[]},"msg":""}
                JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                JsonObject object = jsonObject.getAsJsonObject("data");

                VideoBuyBean bean = new Gson().fromJson(object.toString(), VideoBuyBean.class);
                if (bean.getCode() == 200) {
                    ToastUtil.show(bean.getMsg());
                    mCurWrap.newPlay(mVideoList.get(position).getHref());
                    mVideoList.get(position).setIsbuy(1);
                    notifyDataSetChanged();
                    EventBus.getDefault().post(new NeedRefreshBuyEvent());
                } else if (bean.getCode() == 400 || bean.getCode() == 500) {
                    // 提示金额不足，需要去充值金币
                    ToastUtil.show(bean.getMsg());
                    buyDialog(position);
                }
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                e.printStackTrace();
            }
        });
    }

    // 购买dialog
    public void buyDialog(int position) {
        buyDialog = new BuyDialog(mContext, mVideoList.get(position).getUserinfo().getUser_nicename());
        Context context = buyDialog.getContext();
        int divierId = context.getResources().getIdentifier("android:id/titleDivider", null, null);
        View divider = buyDialog.findViewById(divierId);
        if (divider != null) {
            divider.setBackgroundColor(Color.TRANSPARENT);
        }
        buyDialog.show();
    }
}
