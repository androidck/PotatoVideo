package com.mibai.phonelive.custom;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mibai.phonelive.AppConfig;
import com.mibai.phonelive.R;
import com.mibai.phonelive.activity.PopularizeActivity;
import com.mibai.phonelive.activity.TypeActivity;
import com.mibai.phonelive.activity.VideoDetailsActivity;
import com.mibai.phonelive.bean.MusicBean;
import com.mibai.phonelive.bean.StatusBean;
import com.mibai.phonelive.bean.UserBean;
import com.mibai.phonelive.bean.VideoBean;
import com.mibai.phonelive.bean.VideoBuyBean;
import com.mibai.phonelive.dialog.BuyDialog;
import com.mibai.phonelive.dialog.BuyNewDialog;
import com.mibai.phonelive.dialog.FrequencyDialog;
import com.mibai.phonelive.event.NeedRefreshBuyEvent;
import com.mibai.phonelive.event.NeedRefreshEvent;
import com.mibai.phonelive.flowlayout.FlowLayout;
import com.mibai.phonelive.flowlayout.TagAdapter;
import com.mibai.phonelive.flowlayout.TagFlowLayout;
import com.mibai.phonelive.glide.ImgLoader;
import com.mibai.phonelive.http.HttpCallback;
import com.mibai.phonelive.http.HttpUtil;
import com.mibai.phonelive.network.OkHttp;
import com.mibai.phonelive.purse.VipPayActivity;
import com.mibai.phonelive.utils.FrameAnimUtil;
import com.mibai.phonelive.utils.HawkUtil;
import com.mibai.phonelive.utils.ScreenDimenUtil;
import com.mibai.phonelive.utils.WordUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Request;


/**
 * Created by cxf on 2018/6/5.
 */

public class VideoPlayWrap extends FrameLayout implements View.OnClickListener {

    private String mTag;
    private Context mContext;
    private VideoBean mVideoBean;
    private FrameLayout mContainer;
    private View mCover;
    private ImageView mCoverImg;
    private VideoPlayView mPlayView;
    private TextView mTvAdv;//广告
    private int mScreenWidth;
    private ImageView mAvatar;//头像
    private ImageView mBtnFollow;//关注按钮
    private FrameAnimImageView mBtnZan;//点赞按钮
    private TextView mZanNum;//点赞数
    private TextView mCommentNum;//评论数
    private TextView mShareNum;//分享数
    private TextView mTitle;//标题
    private TextView mName;//昵称
    private TextView mMusicTitle;//音乐标题
    private TextView mVideoDetails;
    private MusicAnimLayout mMusicAnimLayout;
    private boolean mUsing;//是否在使用中
    private ActionListener mActionListener;
    private String mMusicSuffix;
    private static final String SPACE = "            ";
    private static int sFollowAnimHashCode;
    private ValueAnimator mFollowAnimator;

    private TagFlowLayout flowLayout;
    private TagAdapter<String> adapter;
    public static OnCodeListener listener;
    public static OnVideoCodeListener videoCodeListener;
    private FrequencyDialog frequencyDialog;

    private ArrayList<String> list;
    private UserBean u;

    private int isShow = 0;
    private boolean isKou = false;

    private String videoId;

    private BuyNewDialog buyNewDialog;
    private BuyDialog buyDialog;

    public static OnBuyTipListener onBuyTipListener;

    public VideoPlayWrap(Context context) {
        this(context, null);
    }

    public VideoPlayWrap(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoPlayWrap(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTag = String.valueOf(this.hashCode()) + HttpUtil.GET_VIDEO_INFO;
        mContext = context;
        mScreenWidth = ScreenDimenUtil.getInstance().getScreenWdith();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_video_wrap, this, false);
        addView(view);
        mContainer = (FrameLayout) view.findViewById(R.id.container);
        mCover = view.findViewById(R.id.cover);
        mCoverImg = (ImageView) view.findViewById(R.id.coverImg);
        mAvatar = (ImageView) view.findViewById(R.id.avatar);
        mBtnFollow = (ImageView) view.findViewById(R.id.btn_follow);
        mBtnZan = (FrameAnimImageView) view.findViewById(R.id.btn_zan);
        mZanNum = (TextView) view.findViewById(R.id.zan);
        mCommentNum = (TextView) view.findViewById(R.id.comment);
        mShareNum = (TextView) view.findViewById(R.id.share);
        mTitle = (TextView) view.findViewById(R.id.title);
        mName = (TextView) view.findViewById(R.id.name);
        mMusicTitle = (TextView) view.findViewById(R.id.music_title);
        mMusicAnimLayout = (MusicAnimLayout) view.findViewById(R.id.music_anim);
        mVideoDetails = findViewById(R.id.tv_video_details);
        flowLayout = findViewById(R.id.flow_layout);
        mTvAdv = view.findViewById(R.id.adv);
        mAvatar.setOnClickListener(this);
        mBtnFollow.setOnClickListener(this);
        mVideoDetails.setOnClickListener(this);
        view.findViewById(R.id.btn_zan).setOnClickListener(this);
        view.findViewById(R.id.btn_comment).setOnClickListener(this);
        view.findViewById(R.id.btn_share).setOnClickListener(this);
        mMusicSuffix = WordUtil.getString(R.string.music_suffix);
        mFollowAnimator = ValueAnimator.ofFloat(1f, 1.4f, 0.2f);
        mFollowAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float v = (float) animation.getAnimatedValue();
                mBtnFollow.setScaleX(v);
                mBtnFollow.setScaleY(v);
            }
        });
        mFollowAnimator.setDuration(1000);
        mFollowAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mFollowAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mBtnFollow.setVisibility(INVISIBLE);
            }
        });

        getMyInfo();


    }

    /**
     * 加载数据
     */
    public void loadData(VideoBean bean) {
        mUsing = true;
        if (bean == null) {
            return;
        }
        mVideoBean = bean;
        ImgLoader.displayBitmap(bean.getThumb(), new ImgLoader.BitmapCallback() {
            @Override
            public void callback(Bitmap bitmap) {
                if (mCoverImg != null && mCover != null && mCover.getVisibility() == View.VISIBLE) {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mCoverImg.getLayoutParams();
                    float width = bitmap.getWidth();
                    float height = bitmap.getHeight();
                    if (width >= height) {
                        params.height = (int) (mScreenWidth * height / width);
                    } else {
                        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    }
                    mCoverImg.setLayoutParams(params);
                    mCoverImg.requestLayout();
                    mCoverImg.setImageBitmap(bitmap);
                } else {
                    bitmap.recycle();
                }
            }
        });


        if (TextUtils.isEmpty(bean.getAd_url())) {
            mTvAdv.setVisibility(View.GONE);
            mTvAdv.setOnClickListener(null);
            mTitle.setOnClickListener(null);
        } else {
            mTvAdv.setVisibility(View.VISIBLE);
            mTvAdv.setOnClickListener(this);
            mTitle.setOnClickListener(this);
        }

        mZanNum.setText(bean.getLikes());
        mCommentNum.setText(bean.getComments());
        mShareNum.setText(bean.getShares());
        int isAttent = bean.getIsattent();
        if (isAttent == 1 || AppConfig.getInstance().getUid().equals(mVideoBean.getUid())) {
            if (mBtnFollow.getVisibility() == VISIBLE) {
                mBtnFollow.setVisibility(INVISIBLE);
            }
        } else {
            if (mBtnFollow.getVisibility() != VISIBLE) {
                mBtnFollow.setVisibility(VISIBLE);
            }
            mBtnFollow.setScaleX(1f);
            mBtnFollow.setScaleY(1f);
            mBtnFollow.setImageResource(R.mipmap.icon_video_unfollow);
        }
        int islike = bean.getIslike();
        if (islike == 1) {
            mBtnZan.setImageResource(R.mipmap.icon_video_zan_12);
        } else {
            mBtnZan.setImageResource(R.mipmap.icon_video_zan_01);
        }

        mTitle.setText(bean.getTitle());
        u = bean.getUserinfo();
        if (u != null) {
            ImgLoader.displayAvatar(u.getAvatar(), mAvatar);
            mName.setText("@" + u.getUser_nicename());
            if (mVideoBean.getMusic_id() != 0) {
                MusicBean musicBean = mVideoBean.getMusicinfo();
                if (musicBean != null) {
                    mMusicAnimLayout.setImageUrl(musicBean.getImg_url());
                    String title = musicBean.getTitle();
                    mMusicTitle.setText(title + SPACE + title + SPACE + title);
                }
            } else {
                mMusicAnimLayout.setImageUrl(u.getAvatar());
                String title = "@" + u.getUser_nicename() + mMusicSuffix;
                mMusicTitle.setText(title + SPACE + title + SPACE + title);
            }
        }
        getVideoInfo();
        mMusicAnimLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, VipPayActivity.class));
            }
        });
    }

    public void loadVideoId(String id) {
        list = new ArrayList();
        if (!TextUtils.isEmpty(mVideoBean.getCate()) && mVideoBean.getId().equals(id) && mUsing == true) {
            String[] str = mVideoBean.getCate().split(",");
            for (int i = 0; i < str.length; i++) {
                list.add(str[i]);
            }
            adapter = new TagAdapter<String>(list) {
                @Override
                public View getView(FlowLayout parent, int position, String s) {
                    TextView mTag = (TextView) LayoutInflater.from(mContext).inflate(R.layout.item_lable, parent, false);
                    mTag.setText("#" + s);
                    return mTag;
                }
            };
            flowLayout.setAdapter(adapter);
            adapter.refreshTags(list);
            flowLayout.setmItemClickListener(new TagFlowLayout.onTagItemClickListener() {
                @Override
                public void onItemClick(int pos, Object data) {
                    String str = list.get(pos);
                    Intent intent = new Intent(mContext, TypeActivity.class);
                    intent.putExtra("type_name", str.substring(0));
                    mContext.startActivity(intent);
                }
            });

        }
    }

    /**
     * 暂停音乐播放的动画
     */
    public void pauseMusicAnim() {
        if (mMusicAnimLayout != null) {
            mMusicAnimLayout.pauseAnim();
        }
    }

    /**
     * 恢复音乐播放的动画
     */
    public void startMusicAnim() {
        if (mMusicAnimLayout != null) {
          //  mMusicAnimLayout.startAnim();
        }
    }

    /**
     * 显示背景图
     */
    public void showBg() {
        if (mCover.getVisibility() != VISIBLE) {
            mCover.setVisibility(VISIBLE);
        }
    }

    /**
     * 隐藏背景图
     */
    public void hideBg() {
        if (mCover.getVisibility() == VISIBLE) {
            mCover.setVisibility(INVISIBLE);
        }
    }

    public void removePlayView() {
        if (mContainer.getChildCount() > 0) {
            if (mPlayView != null) {
                mContainer.removeView(mPlayView);
                mPlayView = null;
            }
        }
        showBg();
        if (mMusicAnimLayout != null) {
            mMusicAnimLayout.cancelAnim();
        }
    }

    public void addPlayView(VideoPlayView playView) {
        mPlayView = playView;
        playView.setPlayWrap(this);
        ViewGroup parent = (ViewGroup) playView.getParent();
        if (parent != null) {
            parent.removeView(playView);
        }
        mContainer.addView(playView);
    }

    public void play() {
        if (mPlayView != null) {
            mPlayView.play(mVideoBean.getHref());
            mPlayView.setVideoBean(mVideoBean);
        }
    }

    public void newPlay(String href) {
        if (mPlayView != null) {
            mPlayView.play(href);
        }
    }

    public void stop() {
        if (mPlayView != null) {
            mPlayView.pausePlay();
        }
    }

    public void clearData() {
        mUsing = false;
        HttpUtil.cancel(mTag);
        if (mCoverImg != null) {
            mCoverImg.setImageDrawable(null);
        }
        if (mAvatar != null) {
            mAvatar.setImageDrawable(null);
        }
    }

    /**
     * 获取单个视频信息，主要是该视频关于自己的信息 ，如是否关注，是否点赞等
     */
    public void getVideoInfo() {
        HttpUtil.getVideoInfo(mVideoBean.getId(), mTag, mGetVideoInfoCallback);
        mVideoDetails.setVisibility(VISIBLE);
    }


    private HttpCallback mGetVideoInfoCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            listener.onCode(code, msg);
            videoCodeListener.onVideoCode(code, msg, videoId);
            if (code == 0 && info.length > 0) {
                JSONObject obj = JSON.parseObject(info[0]);
                String likes = obj.getString("likes");
                String comments = obj.getString("comments");
                String shares = obj.getString("shares");
                int isattent = obj.getIntValue("isattent");
                int islike = obj.getIntValue("islike");
                String ad_url = obj.getString("ad_url");
                String ad_title = obj.getString("ad_title");
                int money = obj.getIntValue("money");
                int isBuy = obj.getIntValue("isbuy");
                HawkUtil.getInstance().saveData(HawkUtil.VIDEO_ID, obj.getString("id"));
                onBuyTipListener.onTip(String.valueOf(money), String.valueOf(isBuy));
                if (mVideoBean != null) {
                    mVideoBean.setLikes(likes);
                    mVideoBean.setComments(comments);
                    mVideoBean.setShares(shares);
                    mVideoBean.setIsattent(isattent);
                    mVideoBean.setIslike(islike);
                    mVideoBean.setAd_url(ad_url);
                    mVideoBean.setAd_title(ad_title);
                    mVideoBean.setIsbuy(isBuy);
                    mVideoBean.setMoney(money);
                }

                if (mZanNum != null) {
                    mZanNum.setText(likes);
                }
                if (mCommentNum != null) {
                    mCommentNum.setText(comments);
                }
                if (mShareNum != null) {
                    mShareNum.setText(shares);
                }
                if (isattent == 1 || AppConfig.getInstance().getUid().equals(mVideoBean.getUid())) {
                    if (mBtnFollow.getVisibility() == VISIBLE) {
                        mBtnFollow.setVisibility(INVISIBLE);
                    }
                } else {
                    if (mBtnFollow.getVisibility() != VISIBLE) {
                        mBtnFollow.setVisibility(VISIBLE);
                    }
                    mBtnFollow.setScaleX(1f);
                    mBtnFollow.setScaleY(1f);
                    mBtnFollow.setImageResource(R.mipmap.icon_video_unfollow);
                }
                if (islike == 1) {
                    mBtnZan.setImageResource(R.mipmap.icon_video_zan_12);
                } else {
                    mBtnZan.setImageResource(R.mipmap.icon_video_zan_01);
                }

                if (TextUtils.isEmpty(ad_url)) {
                    mTvAdv.setVisibility(View.GONE);
                    mTvAdv.setOnClickListener(null);
                    mTitle.setOnClickListener(null);
                    mTitle.setText(mVideoBean.getTitle());
                } else {
                    mTvAdv.setVisibility(View.VISIBLE);
                    mTvAdv.setOnClickListener(VideoPlayWrap.this);
                    mTitle.setOnClickListener(VideoPlayWrap.this);

                    SpannableString spannableString = new SpannableString(mVideoBean.getTitle() + " 详情");
                    ImageSpan imageSpanManager = new ImageSpan(mContext, R.mipmap.ic_detail_link);
                    spannableString.setSpan(imageSpanManager, spannableString.length() - 2, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

                    mTitle.setMovementMethod(LinkMovementMethod.getInstance());
                    mTitle.setText(spannableString);
                }
            }
        }
    };


    /**
     * 修改评论数
     *
     * @param comments
     */
    public void setCommentNum(String comments) {
        if (mVideoBean != null) {
            mVideoBean.setComments(comments);
        }
        if (mCommentNum != null) {
            mCommentNum.setText(comments);
        }
        EventBus.getDefault().post(new NeedRefreshEvent());
    }

    /**
     * 修改点赞数
     *
     * @param isLike 自己是否点赞
     * @param likes  点赞数
     */
    public void setLikes(int isLike, String likes) {
        if (mVideoBean != null) {
            mVideoBean.setIslike(isLike);
            mVideoBean.setLikes(likes);
        }
        if (isLike == 1) {
            mBtnZan.setSource(FrameAnimUtil.getVideoZanAnim())
                    .setFrameScaleType(FrameAnimImageView.FIT_WIDTH)
                    .setDuration(30).startAnim();
        } else {
            mBtnZan.setSource(FrameAnimUtil.getVideoCancelZanAnim())
                    .setFrameScaleType(FrameAnimImageView.FIT_WIDTH)
                    .setDuration(30).startAnim();
        }
        if (mZanNum != null) {
            mZanNum.setText(likes);
        }
        EventBus.getDefault().post(new NeedRefreshEvent());
    }

    /**
     * 修改分享数
     *
     * @param shares
     */
    public void setShares(String shares) {
        if (mVideoBean != null) {
            mVideoBean.setShares(shares);
        }
        if (mShareNum != null) {
            mShareNum.setText(shares);
        }
        EventBus.getDefault().post(new NeedRefreshEvent());
    }


    /**
     * 修改是否关注
     *
     * @param isAttent
     */
    public void setIsAttent(int isAttent) {
        if (mVideoBean != null && mBtnFollow != null) {
            mVideoBean.setIsattent(isAttent);
            if (isAttent == 1) {
                if (sFollowAnimHashCode == this.hashCode()) {
                    sFollowAnimHashCode = 0;
                    //执行关注动画
                    mBtnFollow.setImageResource(R.mipmap.icon_video_follow);
                    mFollowAnimator.start();
                } else {
                    if (mBtnFollow.getVisibility() == VISIBLE) {
                        mBtnFollow.setVisibility(INVISIBLE);
                    }
                }
            } else {
                if (mBtnFollow.getVisibility() != VISIBLE) {
                    mBtnFollow.setVisibility(VISIBLE);
                }
                mBtnFollow.setScaleX(1f);
                mBtnFollow.setScaleY(1f);
                mBtnFollow.setImageResource(R.mipmap.icon_video_unfollow);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (mActionListener != null && mVideoBean != null) {
            switch (v.getId()) {
                case R.id.btn_zan:
                    if (mVideoBean != null && !mBtnZan.isAnimating()) {
                        mActionListener.onZanClick(this, mVideoBean);
                    }
                    break;
                case R.id.btn_comment:
                    mActionListener.onCommentClick(this, mVideoBean);
                    break;
                case R.id.btn_share:
                    mActionListener.onShareClick(this, mVideoBean);
                  /*  Intent intent = new Intent(mContext, PopularizeActivity.class);
                    intent.putExtra("uid", AppConfig.getInstance().getUid());
                    mContext.startActivity(intent);*/
                    break;
                case R.id.btn_follow:
                    sFollowAnimHashCode = this.hashCode();
                    mActionListener.onFollowClick(this, mVideoBean);
                    break;
                case R.id.avatar:
                    mActionListener.onAvatarClick(this, mVideoBean);
                    break;
                case R.id.adv:
                case R.id.title:
                    mActionListener.onAdvClick(this, mVideoBean);
                    break;
                case R.id.tv_video_details:
                    getShare();
                    break;
            }
        }
    }

    public VideoBean getVideoBean() {
        return mVideoBean;
    }

    @Override
    protected void onDetachedFromWindow() {
        HttpUtil.cancel(mTag);
        super.onDetachedFromWindow();
    }

    public void release() {
        if (mAvatar != null) {
            mAvatar.setImageDrawable(null);
        }
        if (mCoverImg != null) {
            mCoverImg.setImageDrawable(null);
        }
        if (mBtnZan != null) {
            mBtnZan.release();
        }
        if (mFollowAnimator != null) {
            mFollowAnimator.cancel();
        }
    }

    public void getShare() {
        OkHttp.getAsync(HttpUtil.HTTP_URL + "service=User.ifree&uid=" + AppConfig.getInstance().getUid(), new OkHttp.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                JsonObject jsonParser = new JsonParser().parse(result).getAsJsonObject();
                JsonObject object = jsonParser.getAsJsonObject("data");
                JsonObject jsonObject = object.getAsJsonObject("info");
                StatusBean beans = new Gson().fromJson(jsonObject.toString(), StatusBean.class);
                if (beans.getStatus() == 400) {
                    shareDialog(beans.getMsg());
                } else {
                    if (mVideoBean.getIsbuy() == 2 || mVideoBean.getIsbuy() == 1) {
                        Intent intent1 = new Intent(mContext, VideoDetailsActivity.class);
                        intent1.putExtra("title", mVideoBean.getTitle());
                        intent1.putExtra("video_url", mVideoBean.getHref());
                        mContext.startActivity(intent1);
                    } else {
                        buyNewDialog(mVideoBean.getId(), String.valueOf(mVideoBean.getMoney()));
                    }

                }
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                e.printStackTrace();
            }
        });
    }

    // 购买dialog
    public void buyNewDialog(final String videoId, String money) {
        buyNewDialog = new BuyNewDialog(mContext, money, new BuyNewDialog.OnSuccessListener() {
            @Override
            public void onClick(int state) {
                if (state == 1) {
                    videoPay(videoId);
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
    public void videoPay(String vid) {
        String url = HttpUtil.HTTP_URL + "service=Video.buyVideo&uid=" + AppConfig.getInstance().getUid() + "&vid=" + vid;
        OkHttp.getAsync(url, new OkHttp.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                JsonObject object = jsonObject.getAsJsonObject("data");
                VideoBuyBean bean = new Gson().fromJson(object.toString(), VideoBuyBean.class);
                if (bean.getCode() == 200) {
                    Intent intent1 = new Intent(mContext, VideoDetailsActivity.class);
                    intent1.putExtra("title", mVideoBean.getTitle());
                    intent1.putExtra("video_url", mVideoBean.getHref());
                    mContext.startActivity(intent1);
                    EventBus.getDefault().post(new NeedRefreshBuyEvent());
                } else if (bean.getCode() == 400 || bean.getCode() == 500) {
                    // 提示金额不足，需要去充值金币
                    buyDialog();
                }
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                e.printStackTrace();
            }
        });
    }

    // 购买dialog
    public void buyDialog() {
        buyDialog = new BuyDialog(mContext, u.getUser_nicename());
        Context context = buyDialog.getContext();
        int divierId = context.getResources().getIdentifier("android:id/titleDivider", null, null);
        View divider = buyDialog.findViewById(divierId);
        if (divider != null) {
            divider.setBackgroundColor(Color.TRANSPARENT);
        }
        buyDialog.show();
    }


    public interface ActionListener {
        //点击点赞
        void onZanClick(VideoPlayWrap wrap, VideoBean bean);

        //点击评论
        void onCommentClick(VideoPlayWrap wrap, VideoBean bean);

        //点击关注
        void onFollowClick(VideoPlayWrap wrap, VideoBean bean);

        //点击头像
        void onAvatarClick(VideoPlayWrap wrap, VideoBean bean);

        //点击分享
        void onShareClick(VideoPlayWrap wrap, VideoBean bean);

        //点击广告
        void onAdvClick(VideoPlayWrap wrap, VideoBean bean);

    }

    public void setActionListener(ActionListener listener) {
        mActionListener = listener;
    }


    public interface OnCodeListener {
        void onCode(int code, String msg);
    }

    public static void setOnCodeListener(OnCodeListener onCodeListener) {
        listener = onCodeListener;
    }

    public interface OnVideoCodeListener {
        void onVideoCode(int code, String msg, String id);
    }

    public static void setOnVideoCodeListener(OnVideoCodeListener onVideoCodeListener) {
        videoCodeListener = onVideoCodeListener;
    }


    public void getMyInfo() {
        HttpUtil.getUserHome(AppConfig.getInstance().getUid(), mGetUserHomeCallback);
    }

    private HttpCallback mGetUserHomeCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0 && info.length > 0) {
                JSONObject obj = JSON.parseObject(info[0]);
                UserBean user = JSON.toJavaObject(obj, UserBean.class);

            }
        }
    };


    // 分享dialog
    public void shareDialog(String content) {
        frequencyDialog = new FrequencyDialog(mContext, content, new FrequencyDialog.OnItemClickListener() {
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

    // 监听事件
    public interface OnBuyTipListener {
        void onTip(String money, String isbuy);
    }


    public static void setOnBuyTipListener(OnBuyTipListener listener) {
        onBuyTipListener = listener;
    }


}
