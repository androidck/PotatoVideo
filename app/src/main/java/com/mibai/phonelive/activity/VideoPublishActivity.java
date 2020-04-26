package com.mibai.phonelive.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.amap.api.location.AMapLocation;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mibai.phonelive.bean.LabelEntry;
import com.mibai.phonelive.event.SuccessEvent;
import com.mibai.phonelive.flowlayout.FlowLayout;
import com.mibai.phonelive.flowlayout.TagAdapter;
import com.mibai.phonelive.flowlayout.TagFlowLayout;
import com.mibai.phonelive.gaode.LocationUtils;
import com.mibai.phonelive.network.OkHttp;
import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayConfig;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.mibai.phonelive.AppConfig;
import com.mibai.phonelive.Constants;
import com.mibai.phonelive.R;
import com.mibai.phonelive.adapter.VideoShareAdapter;
import com.mibai.phonelive.bean.ConfigBean;
import com.mibai.phonelive.bean.ShareBean;
import com.mibai.phonelive.bean.UserBean;
import com.mibai.phonelive.http.HttpCallback;
import com.mibai.phonelive.http.HttpUtil;
import com.mibai.phonelive.interfaces.CommonCallback;
import com.mibai.phonelive.interfaces.OnItemClickListener;
import com.mibai.phonelive.upload.UploadStrategy;
import com.mibai.phonelive.upload.VideoQnUpload;
import com.mibai.phonelive.upload.VideoTxUpload;
import com.mibai.phonelive.upload.VideoUploadBean;
import com.mibai.phonelive.upload.VideoUploadManager;
import com.mibai.phonelive.utils.DialogUitl;
import com.mibai.phonelive.utils.SharedSdkUitl;
import com.mibai.phonelive.utils.ToastUtil;
import com.mibai.phonelive.utils.WordUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.sharesdk.framework.Platform;
import okhttp3.Request;

/**
 * 视频发布
 * Created by cxf on 2018/6/26.
 */

public class VideoPublishActivity extends AbsActivity implements OnItemClickListener<ShareBean>, View.OnClickListener, LocationUtils.OnLocationListener {

    private TXCloudVideoView mVideoView;
    private TXLivePlayer mPlayer;
    private String mVideoPath;
    private String mCoverPath;
    private VideoUploadBean mVideoUploadBean;
    private boolean mPaused;
    private boolean mStartPlay;
    private RecyclerView mRecyclerView;
    private TextView mShareTo;
    private ConfigBean mConfigBean;
    private SharedSdkUitl mSharedSdkUitl;
    private Dialog mPublishDialog;
    private EditText mEditTitle;
    private TextView mLength;
    private String mShareType;
    private int mMusicId;
    private int mSaveType;
    private int mLink;
    private String mLinkType;
    private String mLinkVideoId;
    private TextView tv_label, tv_city;

    private LinearLayout ly_jb_setup;

    private RadioButton btn_mf, btn_sf;
    private EditText ed_jb;
    private String money;
    // 返回的列表
    private ArrayList<LabelEntry> list = new ArrayList<>();
    private ArrayList<LabelEntry> newList = new ArrayList<>();

    private TagFlowLayout flowLayout;

    // 流布局适配器
    private TagAdapter<LabelEntry> adapter;
    private StringBuilder builder;

    private String latStr, lngStr, cityStr;


    @Override
    protected int getLayoutId() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        return R.layout.activity_video_publish;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.video_publish));
        LocationUtils.getInstance(mContext).init();
        LocationUtils.getInstance(mContext).startLocation();
        LocationUtils.setListener(this);
        Intent intent = getIntent();
        mVideoPath = intent.getStringExtra(Constants.VIDEO_PATH);
        mCoverPath = intent.getStringExtra(Constants.VIDEO_COVER_PATH);
        mSaveType = intent.getIntExtra(Constants.SAVE_TYPE, Constants.SAVE_TYPE_ALL);
        mLink = intent.getIntExtra(Constants.LINK, 0);
        mLinkType = intent.getStringExtra(Constants.LINK_TYPE);
        mLinkVideoId = intent.getStringExtra(Constants.LINK_VIDEO_ID);
        if (mVideoPath == null || mCoverPath == null) {
            return;
        }
        mMusicId = intent.getIntExtra(Constants.VIDEO_MUSIC_ID, 0);
        mVideoUploadBean = new VideoUploadBean(mVideoPath, mCoverPath);
        ((TextView) findViewById(R.id.city)).setText(AppConfig.getInstance().getCity());
        mShareTo = (TextView) findViewById(R.id.share_to);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        flowLayout = findViewById(R.id.flow_layout);
        tv_label = findViewById(R.id.tv_label);
        ed_jb = findViewById(R.id.ed_jb);
        tv_city = findViewById(R.id.city);
        String str = "<font color='#ffffff'size='30px'>添加标签</font><font color='#525068' size='28px'>（最多添加5个标签）</font>";
        tv_label.setText(Html.fromHtml(str));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        mSharedSdkUitl = new SharedSdkUitl();
        HttpUtil.getConfig(new CommonCallback<ConfigBean>() {
            @Override
            public void callback(ConfigBean configBean) {
                mConfigBean = configBean;
                List<ShareBean> list = mSharedSdkUitl.getShareList(configBean.getShare_type());
                if (list != null && list.size() > 0) {
                    VideoShareAdapter adapter = new VideoShareAdapter(mContext, list, true, true);
                    adapter.setOnItemClickListener(VideoPublishActivity.this);
                    mRecyclerView.setAdapter(adapter);
                    if (list.size() > 4) {
                        mShareTo.setText(WordUtil.getString(R.string.share_to_2));
                    }
                }
            }
        });
        findViewById(R.id.btn_publish).setOnClickListener(this);
        mEditTitle = (EditText) findViewById(R.id.edit_title);
        mLength = (TextView) findViewById(R.id.length);
        ly_jb_setup = findViewById(R.id.ly_jb_setup);
        btn_mf = findViewById(R.id.btn_mf);
        btn_sf = findViewById(R.id.btn_sf);
        btn_mf.setOnClickListener(this);
        btn_sf.setOnClickListener(this);
        mEditTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mLength.setText(s.length() + "/50");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        initPlayer();
        getLabel();
    }

    private void initPlayer() {
        mVideoView = (TXCloudVideoView) findViewById(R.id.video_view);
        mPlayer = new TXLivePlayer(mContext);
        mPlayer.setConfig(new TXLivePlayConfig());
        mPlayer.setPlayerView(mVideoView);
        mPlayer.enableHardwareDecode(false);
        mPlayer.setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT);
        mPlayer.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);
        mPlayer.setPlayListener(mPlayListener);
        if (!TextUtils.isEmpty(mVideoPath)) {
            int result = mPlayer.startPlay(mVideoPath, TXLivePlayer.PLAY_TYPE_LOCAL_VIDEO);
            if (result == 0) {
                mStartPlay = true;
            }
        }
    }

    private ITXLivePlayListener mPlayListener = new ITXLivePlayListener() {
        @Override
        public void onPlayEvent(int e, Bundle bundle) {
            if (e == TXLiveConstants.PLAY_EVT_PLAY_END) {
                onReplay();
            }
        }

        @Override
        public void onNetStatus(Bundle bundle) {

        }
    };

    /**
     * 循环播放
     */
    private void onReplay() {
        if (mStartPlay && mPlayer != null) {
            mPlayer.seek(0);
            mPlayer.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPaused = true;
        if (mStartPlay && mPlayer != null) {
            mPlayer.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPaused) {
            mPaused = false;
            if (mStartPlay && mPlayer != null) {
                mPlayer.resume();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayer != null) {
            mPlayer.stopPlay(true);
        }
        if (mVideoView != null) {
            mVideoView.onDestroy();
        }
        if (mSharedSdkUitl != null) {
            mSharedSdkUitl.cancelListener();
        }

        // 销毁定位
        LocationUtils.getInstance(mContext).destroyLocation();
    }

    @Override
    public void onItemClick(ShareBean bean, int position) {
        if (bean.isChecked()) {
            mShareType = bean.getType();
        } else {
            mShareType = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_publish:
                if (builder == null) {
                    ToastUtil.show("请选择视频标签");
                } else {
                    if (btn_mf.isChecked() == true) {
                        if (TextUtils.isEmpty(builder.toString())) {
                            ToastUtil.show("请选择视频标签");
                        } else {
                            money = "0";
                            publishVideo();
                        }

                    } else if (btn_sf.isChecked() == true) {
                        if (TextUtils.isEmpty(ed_jb.getText().toString().trim())) {
                            ToastUtil.show("请输入视频的价格");
                        } else {
                            if (TextUtils.isEmpty(builder.toString())) {
                                ToastUtil.show("请选择视频标签");
                            } else {
                                money = ed_jb.getText().toString().trim();
                                publishVideo();
                            }
                        }
                    }
                }
                break;
            case R.id.btn_mf:
                ly_jb_setup.setVisibility(View.GONE);
                break;
            case R.id.btn_sf:
                ly_jb_setup.setVisibility(View.VISIBLE);
                break;
        }
    }

    /**
     * 把视频和封面图片上传到云存储
     */
    private void publishVideo() {
        if (mConfigBean == null) {
            return;
        }
        mPublishDialog = DialogUitl.loadingDialog(mContext, WordUtil.getString(R.string.video_publish_ing));
        mPublishDialog.show();
        UploadStrategy strategy = null;
        if (mConfigBean.getCloudtype() == VideoUploadManager.UPLOAD_QN) {//七牛云
            strategy = VideoQnUpload.getInstance();
        } else if (mConfigBean.getCloudtype() == VideoUploadManager.UPLOAD_TX) {//腾讯云
            strategy = VideoTxUpload.getInstance();
        }
        if (strategy == null || mVideoUploadBean == null) {
            return;
        }
        VideoUploadManager.getInstance().upload(mVideoUploadBean, strategy, new VideoUploadManager.OnUploadSuccess() {
            @Override
            public void onSuccess(VideoUploadBean bean) {
                if (mSaveType == Constants.SAVE_TYPE_PUB) {//仅发布
                    String videoPath = mVideoUploadBean.getVideoPath();
                    if (!TextUtils.isEmpty(videoPath)) {
                        File file = new File(videoPath);
                        if (file.exists()) {
                            file.delete();
                        }
                    }
                }
                saveUploadInfo();
            }
        });
    }


    /**
     * 把上传后的视频信息保存到数据库
     */
    private void saveUploadInfo() {
        final String title = mEditTitle.getText().toString();//分享的标题
        String str = builder.toString().substring(0, builder.toString().length() - 1);
        HttpUtil.uploadVideo(latStr, lngStr, cityStr, title, mVideoUploadBean.getImgName(), mVideoUploadBean.getVideoName(), mMusicId, mLink, mLinkType, mLinkVideoId, str, money, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (mPublishDialog != null) {
                    mPublishDialog.dismiss();
                }
                if (code == 0 && info.length > 0) {
                    ToastUtil.show(msg);
                    JSONObject obj = JSON.parseObject(info[0]);
                    if (mShareType == null) {
                        finish();
                        // 告诉首页切换到选项卡1
                        // 同时释放资源
                        EventBus.getDefault().post(new SuccessEvent());
                    } else {
                        share(title, obj.getString("id"), obj.getString("thumb_s"));
                    }
                } else {
                    ToastUtil.show("发布失败");
                }
            }
        });
    }

    /**
     * 分享
     */
    private void share(final String title, final String videoId, final String videoThumb) {
        if (mConfigBean == null || mSharedSdkUitl == null) {
            return;
        }
        UserBean u = AppConfig.getInstance().getUserBean();
        if (u == null) {
            return;
        }
        String des = u.getUser_nicename() + mConfigBean.getVideo_share_des();
        mSharedSdkUitl.share(mShareType, title, des, videoThumb, AppConfig.HOST + "/index.php?g=appapi&m=video&a=index&videoid=" + videoId, new SharedSdkUitl.ShareListener() {
            @Override
            public void onSuccess(Platform platform) {
                //ToastUtil.show(WordUtil.getString(R.string.share_success));
            }

            @Override
            public void onError(Platform platform) {
                //ToastUtil.show(WordUtil.getString(R.string.share_fail));
            }

            @Override
            public void onCancel(Platform platform) {
                //ToastUtil.show(WordUtil.getString(R.string.share_cancel));
            }

            @Override
            public void onShareFinish() {
                EventBus.getDefault().post(new SuccessEvent());
                finish();

            }
        });
    }


    // 获取标签
    public void getLabel() {
        OkHttp.getAsync(HttpUtil.HTTP_URL + "service=Ad.getCase", new OkHttp.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                JsonObject jsonParser = new JsonParser().parse(result).getAsJsonObject();
                JsonObject object = jsonParser.getAsJsonObject("data");
                JsonArray jsonElements = object.getAsJsonArray("case_list");//获取JsonArray对象
                for (JsonElement json : jsonElements) {
                    LabelEntry label = new Gson().fromJson(json, LabelEntry.class);
                    list.add(label);
                }
                adapter = new TagAdapter<LabelEntry>(list) {
                    @Override
                    public View getView(FlowLayout parent, int position, LabelEntry labelEntry) {
                        TextView mTag = (TextView) LayoutInflater.from(mContext).inflate(R.layout.item_send_lable, parent, false);
                        if (labelEntry.isSelect() == true) {
                            mTag.setBackgroundResource(R.drawable.shape_item_label_select_bg);
                            mTag.setTextColor(Color.parseColor("#ffffff"));
                        } else {
                            mTag.setBackgroundResource(R.drawable.shape_item_label_unselect_bg);
                            mTag.setTextColor(Color.parseColor("#525068"));
                        }
                        mTag.setText("#" + labelEntry.getTitle());
                        return mTag;
                    }
                };
                flowLayout.setAdapter(adapter);
                // 流布局子项选择
                flowLayout.setmItemClickListener(new TagFlowLayout.onTagItemClickListener() {
                    @Override
                    public void onItemClick(int pos, Object data) {
                        LabelEntry entry = list.get(pos);
                        if (entry.isSelect() == true) {
                            entry.setSelect(false);
                            newList.remove(entry);
                        } else {
                            if (newList.size() < 5) {
                                entry.setSelect(true);
                                newList.add(entry);
                            } else {
                                ToastUtil.show("最多选择5个标签");
                            }
                        }
                        adapter.refreshTags(list);
                        builder = new StringBuilder();
                        for (int i = 0; i < newList.size(); i++) {
                            if (newList.size() < 5) {
                                if (newList.get(i).isSelect() == true) {
                                    builder.append(newList.get(i).getTitle() + ",");
                                }
                            }
                        }

                    }
                });

            }

            @Override
            public void requestFailure(Request request, IOException e) {

            }
        });
    }


    @Override
    public void onLocation(AMapLocation location) {
        tv_city.setText(location.getCity());
        latStr = String.valueOf(location.getLatitude());
        lngStr = String.valueOf(location.getLongitude());
        cityStr = location.getCity();
    }
}
