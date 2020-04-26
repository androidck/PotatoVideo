package com.mibai.phonelive.fragment;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.mibai.phonelive.R;
import com.mibai.phonelive.activity.TypeActivity;
import com.mibai.phonelive.activity.VideoPlayActivity;
import com.mibai.phonelive.adapter.TodayHotAndZanAdapter;
import com.mibai.phonelive.adapter.TypeAdapter;
import com.mibai.phonelive.adapter.TypeHeadAdapter;
import com.mibai.phonelive.bean.BannerEntry;
import com.mibai.phonelive.bean.TypeEntry;
import com.mibai.phonelive.bean.TypeHeadBean;
import com.mibai.phonelive.bean.VideoBean;
import com.mibai.phonelive.network.OkHttp;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Request;

import static com.mibai.phonelive.http.HttpUtil.HTTP_URL;

// 分类
public class TypeFragment extends AbsFragment {
    @BindView(R.id.title)
    TextView title;
    Unbinder unbinder;

    View headView;
    @BindView(R.id.recyclerView)
    SwipeMenuRecyclerView recyclerView;

    private RecyclerView headRecyclerView, today_hot_recyclerView, today_zan_recyclerView;
    private TodayHotAndZanAdapter hotAdapter, zanAdapter;
    private List<VideoBean> hotEntries = new ArrayList<>();
    private List<VideoBean> zanEntries = new ArrayList<>();

    private List<TypeHeadBean> headBeanList = new ArrayList<>();
    private List<TypeEntry> list = new ArrayList<>();

    private TypeHeadAdapter headAdapter;
    private TypeAdapter adapter;

    private String url;

    private Banner banner;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_type;
    }

    @Override
    protected void main() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        iniView();
        initData();
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void iniView() {
        // 创建整体布局
        GridLayoutManager manager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(manager);
        headView = getLayoutInflater().inflate(R.layout.view_header_type, recyclerView, false);
        recyclerView.addHeaderView(headView);

        banner = headView.findViewById(R.id.banner);

        headRecyclerView = headView.findViewById(R.id.head_recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        headRecyclerView.setLayoutManager(layoutManager);

        headAdapter = new TypeHeadAdapter(getActivity(), headBeanList);
        headRecyclerView.setAdapter(headAdapter);
        headAdapter.setOnItemClickListener(new TypeHeadAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                TypeHeadBean bean = headBeanList.get(position);
                url = bean.getUrl();
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(url);
                intent.setData(content_url);
                startActivity(intent);
            }
        });
        // 今日最热
        today_hot_recyclerView = headView.findViewById(R.id.today_hot_recyclerView);
        LinearLayoutManager todayHotManager = new LinearLayoutManager(getActivity());
        todayHotManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        today_hot_recyclerView.setLayoutManager(todayHotManager);
        hotAdapter = new TodayHotAndZanAdapter(mContext, hotEntries);
        today_hot_recyclerView.setAdapter(hotAdapter);
        hotAdapter.setOnItemClickListener(new TodayHotAndZanAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                VideoBean bean = hotEntries.get(position);
                VideoPlayActivity.forwardSingleVideoPlay(mContext, bean);
            }
        });

        // 点赞最多
        today_zan_recyclerView = headView.findViewById(R.id.today_zan_recyclerView);
        LinearLayoutManager todayZanManager = new LinearLayoutManager(getActivity());
        todayZanManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        today_zan_recyclerView.setLayoutManager(todayZanManager);
        zanAdapter = new TodayHotAndZanAdapter(mContext, zanEntries);
        today_zan_recyclerView.setAdapter(zanAdapter);
        zanAdapter.setOnItemClickListener(new TodayHotAndZanAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                VideoBean bean = hotEntries.get(position);
                VideoPlayActivity.forwardSingleVideoPlay(mContext, bean);
            }
        });

        adapter = new TypeAdapter(getActivity(), list);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new TypeAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                TypeEntry entry = list.get(position);
                Intent intent = new Intent(getActivity(), TypeActivity.class);
                intent.putExtra("type_name", entry.getTitle());
                getActivity().startActivity(intent);
            }
        });


    }

    private void initData() {
        getTypeList();
        getTypeAdvert();
        getBanner();
        getZanAndHot();
    }

    // 获取广告
    public void getTypeAdvert() {
        OkHttp.getAsync(HTTP_URL + "service=Ad.getAdLists", new OkHttp.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                JsonObject jsonParser = new JsonParser().parse(result).getAsJsonObject();
                JsonObject object = jsonParser.getAsJsonObject("data");
                JsonArray jsonElements = object.getAsJsonArray("ad_list");//获取JsonArray对象
                for (JsonElement json : jsonElements) {
                    TypeHeadBean bean = new Gson().fromJson(json, TypeHeadBean.class);
                    headBeanList.add(bean);
                }
                headAdapter.notifyDataSetChanged();
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                e.printStackTrace();

            }
        });
    }

    // 获取分类列表
    public void getTypeList() {
        OkHttp.getAsync(HTTP_URL + "service=Ad.getCase", new OkHttp.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                JsonObject jsonParser = new JsonParser().parse(result).getAsJsonObject();
                JsonObject object = jsonParser.getAsJsonObject("data");
                JsonArray jsonElements = object.getAsJsonArray("case_list");//获取JsonArray对象
                for (JsonElement json : jsonElements) {
                    TypeEntry typeEntry = new Gson().fromJson(json, TypeEntry.class);
                    list.add(typeEntry);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void requestFailure(Request request, IOException e) {

            }
        });
    }

    // 获取bannner
    public void getBanner() {
        OkHttp.getAsync(HTTP_URL + "service=Home.getBanner", new OkHttp.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                JsonObject object = jsonObject.getAsJsonObject("data");
                JsonArray jsonArray = object.getAsJsonArray("info");
                final List<BannerEntry> list = new Gson().fromJson(jsonArray.toString(), new TypeToken<List<BannerEntry>>() {
                }.getType());
                //设置banner样式(显示圆形指示器)
                banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
                //设置指示器位置（指示器居右）
                banner.setIndicatorGravity(BannerConfig.CENTER);
                //设置图片集合
                banner.setImages(list);
                //设置图片加载器
                banner.setImageLoader(new GlideImageLoader());
                //设置banner动画效果
                banner.setBannerAnimation(Transformer.DepthPage);
                //设置自动轮播，默认为true
                banner.isAutoPlay(true);
                //设置轮播时间
                banner.setDelayTime(3000);
                banner.setOnBannerListener(new OnBannerListener() {
                    @Override
                    public void OnBannerClick(int position) {
                        BannerEntry entry = list.get(position);
                        startBrowserActivity(mContext, 1, entry.getUrl());
                    }
                });
                //banner设置方法全部调用完毕时最后调用
                banner.start();
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public class GlideImageLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            BannerEntry entry = (BannerEntry) path;
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            Glide.with(mContext).load(entry.getPic()).into(imageView);
        }
    }


    // 获取今日最多点赞 今日播放最多视频
    public void getZanAndHot() {
        OkHttp.getAsync(HTTP_URL + "service=Video.HotVideolist", new OkHttp.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                JsonObject data = jsonObject.getAsJsonObject("data");
                JsonObject info = data.getAsJsonObject("info");
                JsonArray hotArray = info.getAsJsonArray("hot");
                JsonArray zanArray = info.getAsJsonArray("zan");
                List<VideoBean> hotList = new Gson().fromJson(hotArray.toString(), new TypeToken<List<VideoBean>>() {
                }.getType());
                List<VideoBean> zanList = new Gson().fromJson(zanArray.toString(), new TypeToken<List<VideoBean>>() {
                }.getType());
                hotEntries.addAll(hotList);
                zanEntries.addAll(zanList);
                hotAdapter.notifyDataSetChanged();
                zanAdapter.notifyDataSetChanged();
            }

            @Override
            public void requestFailure(Request request, IOException e) {

            }
        });
    }

}
