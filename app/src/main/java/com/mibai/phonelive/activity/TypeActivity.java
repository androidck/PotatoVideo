package com.mibai.phonelive.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mibai.phonelive.AppConfig;
import com.mibai.phonelive.Constants;
import com.mibai.phonelive.R;
import com.mibai.phonelive.adapter.FollowVideoAdapter;
import com.mibai.phonelive.bean.StatusBean;
import com.mibai.phonelive.bean.VideoBean;
import com.mibai.phonelive.custom.ItemDecoration;
import com.mibai.phonelive.custom.RefreshAdapter;
import com.mibai.phonelive.custom.RefreshView;
import com.mibai.phonelive.custom.VideoPlayWrap;
import com.mibai.phonelive.dialog.FrequencyDialog;
import com.mibai.phonelive.http.HttpCallback;
import com.mibai.phonelive.http.HttpUtil;
import com.mibai.phonelive.interfaces.OnItemClickListener;
import com.mibai.phonelive.network.OkHttp;
import com.mibai.phonelive.utils.VideoStorge;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Request;

// 视频列表
public class TypeActivity extends AbsActivity implements OnItemClickListener<VideoBean>, VideoPlayWrap.OnVideoCodeListener {
    @BindView(R.id.titleView)
    TextView titleView;
    @BindView(R.id.btn_back)
    ImageView btnBack;
    @BindView(R.id.refreshView)
    RefreshView refreshView;

    private FollowVideoAdapter mFollowAdapter;
    private boolean mFirst = true;

    private String typeNames;

    private FrequencyDialog frequencyDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_type;
    }

    @Override
    protected void main() {
        super.main();
        initView();
        initData();
        VideoPlayWrap.setOnVideoCodeListener(this);
    }

    private void initView() {
        typeNames = this.getIntent().getStringExtra("type_name");
        titleView.setText(typeNames);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        refreshView.setNoDataLayoutId(R.layout.view_no_data_default);
        refreshView.setDataHelper(new RefreshView.DataHelper<VideoBean>() {

            @Override
            public RefreshAdapter<VideoBean> getAdapter() {
                if (mFollowAdapter == null) {
                    mFollowAdapter = new FollowVideoAdapter(mContext);
                    mFollowAdapter.setOnItemClickListener(TypeActivity.this);
                }
                return mFollowAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                HttpUtil.getTypeVideoList(p, typeNames, callback);
            }


            @Override
            public List<VideoBean> processData(String[] info) {
                return JSON.parseArray(Arrays.toString(info), VideoBean.class);
            }

            @Override
            public void onRefresh(List<VideoBean> list) {
                VideoStorge.getInstance().put(Constants.VIDEO_HOT, list);
            }

            @Override
            public void onNoData(boolean noData) {

            }

            @Override
            public void onLoadDataCompleted(int dataCount) {

            }

        });
        refreshView.setLayoutManager(new GridLayoutManager(mContext, 2, GridLayoutManager.VERTICAL, false));
        ItemDecoration decoration = new ItemDecoration(mContext, 0x00000000, 2, 2);
        decoration.setOnlySetItemOffsetsButNoDraw(true);
        refreshView.setItemDecoration(decoration);
    }

    private void initData() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @Override
    public void onItemClick(VideoBean bean, int position) {
        if (refreshView != null && bean != null && bean.getUserinfo() != null) {
            getShare(bean,position);
        }
    }

    @Override
    public void onDestroy() {
        if (refreshView != null) {
            refreshView.setDataHelper(null);
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshView.initData();
    }

    @Override
    public void onVideoCode(int code,String msg,String id) {

    }

    public void getShare(final VideoBean bean,final int position) {
        OkHttp.getAsync(HttpUtil.HTTP_URL + "service=User.ifree&uid=" + AppConfig.getInstance().getUid(), new OkHttp.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                JsonObject jsonParser = new JsonParser().parse(result).getAsJsonObject();
                JsonObject object = jsonParser.getAsJsonObject("data");
                JsonObject jsonObject = object.getAsJsonObject("info");
                StatusBean beans=new Gson().fromJson(jsonObject.toString(),StatusBean.class);
                if (AppConfig.getInstance().isUser()){
                    if (beans.getStatus()==400){
                        shareDialog(beans.getMsg());
                    }else {
                        VideoPlayActivity.forwardVideoPlay(mContext, Constants.VIDEO_HOT, position, refreshView.getPage(), bean.getUserinfo(), bean.getIsattent());
                    }
                }else {
                    VideoPlayActivity.forwardVideoPlay(mContext, Constants.VIDEO_HOT, position, refreshView.getPage(), bean.getUserinfo(), bean.getIsattent());
                }
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                e.printStackTrace();
            }
        });
    }

    // 分享dialog
    public void shareDialog(String content) {
        frequencyDialog = new FrequencyDialog(TypeActivity.this,content, new FrequencyDialog.OnItemClickListener() {
            @Override
            public void onClick() {
                Intent intent = new Intent(TypeActivity.this, PopularizeActivity.class);
                intent.putExtra("uid", AppConfig.getInstance().getUid());
                startActivity(intent);
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
}
