package com.mibai.phonelive.community.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.location.AMapLocation;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.broadcast.BroadcastAction;
import com.luck.picture.lib.broadcast.BroadcastManager;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.decoration.GridSpacingItemDecoration;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.style.PictureCropParameterStyle;
import com.luck.picture.lib.style.PictureParameterStyle;
import com.luck.picture.lib.style.PictureWindowAnimationStyle;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.luck.picture.lib.tools.ScreenUtils;
import com.luck.picture.lib.tools.ToastUtils;
import com.mibai.phonelive.AppConfig;
import com.mibai.phonelive.R;
import com.mibai.phonelive.activity.AbsActivity;
import com.mibai.phonelive.bean.LabelEntry;
import com.mibai.phonelive.community.adapter.GridImageAdapter;
import com.mibai.phonelive.community.adapter.SendPostCircleAdapter;
import com.mibai.phonelive.community.bean.CircleChildHeaderEntry;
import com.mibai.phonelive.community.bean.UpLoadBean;
import com.mibai.phonelive.community.dialog.MenuDialog;
import com.mibai.phonelive.community.event.RefreshEvent;
import com.mibai.phonelive.community.manager.FullyGridLayoutManager;
import com.mibai.phonelive.community.network.HttpContract;
import com.mibai.phonelive.community.upload.FileUploadObserver;
import com.mibai.phonelive.community.upload.RetrofitClient;
import com.mibai.phonelive.community.upload.UploadFileApi;
import com.mibai.phonelive.gaode.LocationUtils;
import com.mibai.phonelive.network.OkHttp;
import com.mibai.phonelive.utils.GlideEngine;
import com.mibai.phonelive.utils.StatusBarUtil;
import com.mibai.phonelive.utils.ToastUtil;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Request;
import okhttp3.ResponseBody;

// 发布帖子
public class SendPostActivity extends AbsActivity implements LocationUtils.OnLocationListener {
    private static final String TAG = "SendPostActivity";
    @BindView(R.id.btn_back)
    ImageView btnBack;
    @BindView(R.id.tv_send)
    TextView tvSend;
    @BindView(R.id.recyclerView)
    SwipeMenuRecyclerView recyclerView;

    private int maxSelectNum = 6;

    private int language = -1;
    private boolean isUpward;

    private View headView;

    private RecyclerView recycler;
    private TextView tv_location;
    private GridImageAdapter mAdapter;

    private PictureParameterStyle mPictureParameterStyle;
    private PictureCropParameterStyle mCropParameterStyle;
    private PictureWindowAnimationStyle mWindowAnimationStyle;
    private int themeId;

    private List<LocalMedia> selectList;
    private EditText edContent;

    // 圈子列表
    private List<CircleChildHeaderEntry> list = new ArrayList<>();

    private List<CircleChildHeaderEntry> newList = new ArrayList<>();
    private List<CircleChildHeaderEntry> labelList = new ArrayList<>();

    private SendPostCircleAdapter adapter;

    private String image;

    private List<File> files;

    // 上传的类型
    private int type = 0;

    private StringBuilder builder;

    // 标签
    private String label;

    private String cityStr;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_send_post;
    }

    @Override
    protected void main() {
        super.main();
        StatusBarUtil.setStatusBarMode(this, true, R.color.white);
        GridLayoutManager manager = new GridLayoutManager(this, 3);
        LocationUtils.getInstance(mContext).init();
        LocationUtils.getInstance(mContext).startLocation();
        LocationUtils.setListener(this);
        recyclerView.setLayoutManager(manager);
        headView = getLayoutInflater().inflate(R.layout.view_header_post, recyclerView, false);
        recyclerView.addHeaderView(headView);
        edContent = headView.findViewById(R.id.ed_content);
        tv_location = headView.findViewById(R.id.tv_location);
        recycler = headView.findViewById(R.id.recycler);

        adapter = new SendPostCircleAdapter(this, newList);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new SendPostCircleAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                CircleChildHeaderEntry entry = newList.get(position);
                if (entry.isSelect() == true) {
                    entry.setSelect(false);
                    labelList.remove(entry);
                } else {
                    entry.setSelect(true);
                    labelList.add(entry);
                }
                adapter.notifyDataSetChanged();
                builder = new StringBuilder();
                for (int i = 0; i < labelList.size(); i++) {
                    if (labelList.get(i).isSelect() == true) {
                        builder.append(labelList.get(i).getTitle() + ",");
                    }
                }
            }
        });

        // 照片选择器
        mAdapter = new GridImageAdapter(this, onAddPicClickListener);
        FullyGridLayoutManager fullyGridLayoutManager = new FullyGridLayoutManager(this,
                4, GridLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(fullyGridLayoutManager);
        recycler.addItemDecoration(new GridSpacingItemDecoration(4,
                ScreenUtils.dip2px(this, 8), false));
        themeId = R.style.picture_default_style;
        getDefaultStyle();
        mAdapter.setSelectMax(maxSelectNum);
        recycler.setAdapter(mAdapter);
        mWindowAnimationStyle = new PictureWindowAnimationStyle();
        // adapter 点击事件
        mAdapter.setOnItemClickListener((position, v) -> {
            List<LocalMedia> selectList = mAdapter.getData();
            if (selectList.size() > 0) {
                LocalMedia media = selectList.get(position);
                String mimeType = media.getMimeType();
                int mediaType = PictureMimeType.getMimeType(mimeType);
                switch (mediaType) {
                    case PictureConfig.TYPE_VIDEO:
                        // 预览视频
                        PictureSelector.create(SendPostActivity.this)
                                .themeStyle(R.style.picture_default_style)
                                .setPictureStyle(mPictureParameterStyle)// 动态自定义相册主题
                                .externalPictureVideo(media.getPath());
                        break;
                    case PictureConfig.TYPE_AUDIO:
                        // 预览音频
                        PictureSelector.create(SendPostActivity.this)
                                .externalPictureAudio(
                                        media.getPath().startsWith("content://") ? media.getAndroidQToPath() : media.getPath());
                        break;
                    default:
                        // 预览图片 可自定长按保存路径
                        PictureSelector.create(SendPostActivity.this)
                                .themeStyle(R.style.picture_default_style) // xml设置主题
                                .setPictureStyle(mPictureParameterStyle)// 动态自定义相册主题
                                .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)// 设置相册Activity方向，不设置默认使用系统
                                .isNotPreviewDownload(true)// 预览图片长按是否可以下载
                                .loadImageEngine(GlideEngine.createGlideEngine())// 外部传入图片加载引擎，必传项
                                .openExternalPreview(position, selectList);
                        break;
                }
            }
        });

        // 注册外部预览图片删除按钮回调
        BroadcastManager.getInstance(SendPostActivity.this).registerReceiver(broadcastReceiver,
                BroadcastAction.ACTION_DELETE_PREVIEW_POSITION);

        getLabel();
    }

    // 默认主题
    private void getDefaultStyle() {
        // 相册主题
        mPictureParameterStyle = new PictureParameterStyle();
        // 是否改变状态栏字体颜色(黑白切换)
        mPictureParameterStyle.isChangeStatusBarFontColor = false;
        // 是否开启右下角已完成(0/9)风格
        mPictureParameterStyle.isOpenCompletedNumStyle = false;
        // 是否开启类似QQ相册带数字选择风格
        mPictureParameterStyle.isOpenCheckNumStyle = false;
        // 相册状态栏背景色
        mPictureParameterStyle.pictureStatusBarColor = Color.parseColor("#393a3e");
        // 相册列表标题栏背景色
        mPictureParameterStyle.pictureTitleBarBackgroundColor = Color.parseColor("#393a3e");
        // 相册列表标题栏右侧上拉箭头
        mPictureParameterStyle.pictureTitleUpResId = R.drawable.picture_icon_arrow_up;
        // 相册列表标题栏右侧下拉箭头
        mPictureParameterStyle.pictureTitleDownResId = R.drawable.picture_icon_arrow_down;
        // 相册文件夹列表选中圆点
        mPictureParameterStyle.pictureFolderCheckedDotStyle = R.drawable.picture_orange_oval;
        // 相册返回箭头
        mPictureParameterStyle.pictureLeftBackIcon = R.drawable.picture_icon_back;
        // 标题栏字体颜色
        mPictureParameterStyle.pictureTitleTextColor = ContextCompat.getColor(this, R.color.picture_color_white);
        // 相册右侧取消按钮字体颜色  废弃 改用.pictureRightDefaultTextColor和.pictureRightDefaultTextColor
        mPictureParameterStyle.pictureCancelTextColor = ContextCompat.getColor(this, R.color.picture_color_white);
        // 相册列表勾选图片样式
        mPictureParameterStyle.pictureCheckedStyle = R.drawable.picture_checkbox_selector;
        // 相册列表底部背景色
        mPictureParameterStyle.pictureBottomBgColor = ContextCompat.getColor(this, R.color.picture_color_grey);
        // 已选数量圆点背景样式
        mPictureParameterStyle.pictureCheckNumBgStyle = R.drawable.picture_num_oval;
        // 相册列表底下预览文字色值(预览按钮可点击时的色值)
        mPictureParameterStyle.picturePreviewTextColor = ContextCompat.getColor(this, R.color.picture_color_fa632d);
        // 相册列表底下不可预览文字色值(预览按钮不可点击时的色值)
        mPictureParameterStyle.pictureUnPreviewTextColor = ContextCompat.getColor(this, R.color.picture_color_white);
        // 相册列表已完成色值(已完成 可点击色值)
        mPictureParameterStyle.pictureCompleteTextColor = ContextCompat.getColor(this, R.color.picture_color_fa632d);
        // 相册列表未完成色值(请选择 不可点击色值)
        mPictureParameterStyle.pictureUnCompleteTextColor = ContextCompat.getColor(this, R.color.picture_color_white);
        // 预览界面底部背景色
        mPictureParameterStyle.picturePreviewBottomBgColor = ContextCompat.getColor(this, R.color.picture_color_grey);
        // 外部预览界面删除按钮样式
        mPictureParameterStyle.pictureExternalPreviewDeleteStyle = R.drawable.picture_icon_delete;
        // 原图按钮勾选样式  需设置.isOriginalImageControl(true); 才有效
        mPictureParameterStyle.pictureOriginalControlStyle = R.drawable.picture_original_wechat_checkbox;
        // 原图文字颜色 需设置.isOriginalImageControl(true); 才有效
        mPictureParameterStyle.pictureOriginalFontColor = ContextCompat.getColor(this, R.color.white);
        // 外部预览界面是否显示删除按钮
        mPictureParameterStyle.pictureExternalPreviewGonePreviewDelete = true;
        // 设置NavBar Color SDK Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP有效
        mPictureParameterStyle.pictureNavBarColor = getResources().getColor(R.color.colorAccent);
    }

    private int gallery; // 类型
    private int maxNum; // 最大数量
    private int multiple;// 单选or多选

    private GridImageAdapter.onAddPicClickListener onAddPicClickListener = new GridImageAdapter.onAddPicClickListener() {
        @Override
        public void onAddPicClick() {
            // 照片
            if (selectList != null) {
                selectList.clear();
                mAdapter.notifyDataSetChanged();
            }
            gallery = PictureMimeType.ofImage();
            maxNum = maxSelectNum;
            multiple = PictureConfig.MULTIPLE;
            photoIsVideo(gallery, maxNum, multiple);
            type = 0;
          /*  new MenuDialog(SendPostActivity.this, new MenuDialog.OnItemClickListener() {
                @Override
                public void onClick(int position) {
                    switch (position) {
                        case 0:

                            break;
                        case 1:
                            if (selectList != null) {
                                selectList.clear();
                                mAdapter.notifyDataSetChanged();
                            }
                            gallery = PictureMimeType.ofVideo();
                            maxNum = 1;
                            multiple = PictureConfig.SINGLE;
                            photoIsVideo(gallery, maxNum, multiple);
                            type = 1;
                            break;
                    }
                }
            }).show();*/
        }
    };

    // 选择视频还是照片
    public void photoIsVideo(int gallery, int maxNum, int multiple) {
        PictureSelector.create(SendPostActivity.this)
                .openGallery(gallery)// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .loadImageEngine(GlideEngine.createGlideEngine())// 外部传入图片加载引擎，必传项
                .theme(themeId)// 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style v2.3.3后 建议使用setPictureStyle()动态方式
                .setLanguage(language)// 设置语言，默认中文
                .setPictureStyle(mPictureParameterStyle)// 动态自定义相册主题
                .setPictureCropStyle(mCropParameterStyle)// 动态自定义裁剪主题
                .setPictureWindowAnimationStyle(mWindowAnimationStyle)// 自定义相册启动退出动画
                .isWithVideoImage(true)// 图片和视频是否可以同选,只在ofAll模式下有效
                .maxSelectNum(maxNum)// 最大图片选择数量
                .minSelectNum(1)// 最小选择数量
                .maxVideoSelectNum(1) // 视频最大选择数量，如果没有单独设置的需求则可以不设置，同用maxSelectNum字段
                .imageSpanCount(4)// 每行显示个数
                .isReturnEmpty(false)// 未选择数据时点击按钮是否可以返回
                .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)// 设置相册Activity方向，不设置默认使用系统
                .isOriginalImageControl(false)// 是否显示原图控制按钮，如果设置为true则用户可以自由选择是否使用原图，压缩、裁剪功能将会失效
                .selectionMode(multiple)// 多选 or 单选
                .isSingleDirectReturn(true)// 单选模式下是否直接返回，PictureConfig.SINGLE模式下有效
                .previewImage(true)// 是否可预览图片
                .previewVideo(true)// 是否可预览视频
                .enablePreviewAudio(true) // 是否可播放音频
                .isCamera(true)// 是否显示拍照按钮
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .synOrAsy(true)//同步true或异步false 压缩 默认同步
                .isGif(false)// 是否显示gif图片
                .selectionMedia(mAdapter.getData())// 是否传入已选图片
                .cutOutQuality(90)// 裁剪输出质量 默认100
                .minimumCompressSize(100)// 小于100kb的图片不压缩
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
    }

    // 权限管理
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PictureConfig.APPLY_STORAGE_PERMISSIONS_CODE:
                // 存储权限
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        PictureFileUtils.deleteCacheDirFile(this, PictureMimeType.ofImage());
                    } else {
                        Toast.makeText(SendPostActivity.this,
                                getString(R.string.picture_jurisdiction), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mAdapter != null && mAdapter.getData() != null && mAdapter.getData().size() > 0) {
            outState.putParcelableArrayList("selectorList",
                    (ArrayList<? extends Parcelable>) mAdapter.getData());
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Bundle extras;
            switch (action) {
                case BroadcastAction.ACTION_DELETE_PREVIEW_POSITION:
                    // 外部预览删除按钮回调
                    extras = intent.getExtras();
                    int position = extras.getInt(PictureConfig.EXTRA_PREVIEW_DELETE_POSITION);
                    ToastUtils.s(SendPostActivity.this, "delete image index:" + position);
                    if (position < mAdapter.getData().size()) {
                        mAdapter.remove(position);
                        mAdapter.notifyItemRemoved(position);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null) {
            BroadcastManager.getInstance(SendPostActivity.this).unregisterReceiver(broadcastReceiver,
                    BroadcastAction.ACTION_DELETE_PREVIEW_POSITION);
        }
        LocationUtils.getInstance(this).destroyLocation();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    selectList = PictureSelector.obtainMultipleResult(data);
                    for (LocalMedia media : selectList) {
                        Log.i(TAG, "是否压缩:" + media.isCompressed());
                        Log.i(TAG, "压缩:" + media.getCompressPath());
                        Log.i(TAG, "原图:" + media.getPath());
                        Log.i(TAG, "是否裁剪:" + media.isCut());
                        Log.i(TAG, "裁剪:" + media.getCutPath());
                        Log.i(TAG, "是否开启原图:" + media.isOriginal());
                        Log.i(TAG, "原图路径:" + media.getOriginalPath());
                        Log.i(TAG, "Android Q 特有Path:" + media.getAndroidQToPath());
                    }
                    mAdapter.setList(selectList);
                    mAdapter.notifyDataSetChanged();
                    break;
                case PictureConfig.PREVIEW_VIDEO_CODE:
                    selectList = PictureSelector.obtainMultipleResult(data);
                    mAdapter.setList(selectList);
                    mAdapter.notifyDataSetChanged();
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_back, R.id.tv_send})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.tv_send:
                if (TextUtils.isEmpty(edContent.getText().toString().trim())) {
                    ToastUtil.show("帖子内容不能为空");
                } else {
                    files = new ArrayList<>();
                    for (int i = 0; i < selectList.size(); i++) {
                        files.add(new File(selectList.get(i).getPath()));
                    }
                    imgUpload(files);
                }

                break;
        }
    }

    // 图片上传 / 视频上传
    public void imgUpload(List<File> files) {
        showLoading();
        RetrofitClient.getInstance().upLoadFiles(UploadFileApi.UPLOAD_FILE_URL, files,
                new FileUploadObserver<ResponseBody>() {
                    @Override
                    public void onUploadSuccess(ResponseBody responseBody) {
                        try {
                            JSONObject jsonObject = new JSONObject(responseBody.string());
                            image = jsonObject.getString("img");
                            sendPost();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        dismissLoading();
                    }

                    @Override
                    public void onUploadFail(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onProgress(int progress) {
                        Log.d("进度", String.valueOf(progress));
                    }
                });

    }


    // 发布帖子
    public void sendPost() {
        if (builder == null) {
            label = newList.get(0).getTitle();
        } else {
            label = builder.toString().substring(0, builder.toString().length() - 1);
        }
        Map<String, String> map = new HashMap<>();
        map.put("title", edContent.getText().toString().trim());// 标题
        map.put("content", edContent.getText().toString().trim());// 内容
        if (TextUtils.isEmpty(cityStr)) {
            map.put("localtion", "北京市");// 点位
        } else {
            map.put("localtion", cityStr);// 点位
        }

        map.put("img", image);// 图片集合
        map.put("video", "0");// 视频地址
        map.put("type", "0");// 类型 0 图片 1 视频
        map.put("label", label);
        map.put("uid", AppConfig.getInstance().getUid());
        OkHttp.postAsync(HttpContract.SEND_POST, map, new OkHttp.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                finish();
                ToastUtil.show("发布成功");
                EventBus.getDefault().post(RefreshEvent.getInstance(200));
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                e.printStackTrace();
                dismissLoading();
            }
        });
    }


    // 获取标签
    public void getLabel() {
        OkHttp.getAsync(HttpContract.ForumLabeURL, new OkHttp.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                // {"ret":200,"data":{"ad_list":[{"id":"1","title":"\u5395\u52371","addtime":"1586288071"},{"id":"2","title":"\u5395\u5237","addtime":"1586288071"}]},"msg":""}
                JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                JsonObject data = jsonObject.getAsJsonObject("data");
                JsonArray jsonArray = data.getAsJsonArray("ad_list");
                List<CircleChildHeaderEntry> childHeaderEntries = new Gson().fromJson(jsonArray.toString(), new TypeToken<List<CircleChildHeaderEntry>>() {
                }.getType());
                list.addAll(childHeaderEntries);
                for (int i = 0; i < list.size(); i++) {
                    if (i == 0) {
                        list.get(i).setSelect(true);
                    } else
                        list.get(i).setSelect(false);
                    newList.add(list.get(i));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void requestFailure(Request request, IOException e) {

            }
        });
    }

    @Override
    public void onLocation(AMapLocation location) {
        cityStr = location.getCity();
        tv_location.setText(cityStr);
    }
}
