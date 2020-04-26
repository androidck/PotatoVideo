package com.mibai.phonelive.community.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.mibai.phonelive.AppConfig;
import com.mibai.phonelive.R;
import com.mibai.phonelive.community.activity.PostDetailsActivity;
import com.mibai.phonelive.community.adapter.CircleAdapter;
import com.mibai.phonelive.community.bean.CircleChildEntry;
import com.mibai.phonelive.community.event.RefreshEvent;
import com.mibai.phonelive.community.network.HttpContract;
import com.mibai.phonelive.fragment.AbsFragment;
import com.mibai.phonelive.network.OkHttp;
import com.mibai.phonelive.utils.ToastUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Request;

// 社区子页面
public class CommunityFragment extends AbsFragment {


    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    Unbinder unbinder;
    @BindView(R.id.refresh)
    SmartRefreshLayout refresh;

    private int p = 1;


    private List<CircleChildEntry> list = new ArrayList<>();// 圈子列表

    private CircleAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_community;
    }

    @Override
    protected void main() {
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        adapter = new CircleAdapter(getContext(), list);
        recyclerView.setAdapter(adapter);

        // 关注按钮点击事件
        adapter.setOnItemAttentionListener(new CircleAdapter.OnItemAttentionListener() {
            @Override
            public void onAttentionClick(int position) {
                CircleChildEntry entry = list.get(position);
                if (TextUtils.isEmpty(entry.getUserinfo().getId())) {
                    ToastUtil.show("操作失败");
                } else {
                    follow(entry.getId(), entry.getUserinfo().getId());
                }
            }
        });
        // 点赞
        adapter.setOnJustLikeListener(new CircleAdapter.OnJustLikeListener() {
            @Override
            public void onJustLike(int position) {
                dianzan(list.get(position).getId());
            }
        });
        // 收藏
        adapter.setOnSendGiftListener(new CircleAdapter.OnSendGiftListener() {
            @Override
            public void onSendGift(int position) {
                collection(list.get(position).getId());
            }
        });
        // 查看详情
        adapter.setOnClickListener(new CircleAdapter.OnClickListener() {
            @Override
            public void onClick(int position) {
                Intent intent = new Intent(getActivity(), PostDetailsActivity.class);
                intent.putExtra("tid", list.get(position).getId());
                startActivity(intent);
            }
        });
        // 举报
        adapter.setOnReportClickListener(new CircleAdapter.OnReportClickListener() {
            @Override
            public void onReport(int position) {

            }
        });

        // 评论
        adapter.setOnCommentClickListener(new CircleAdapter.OnCommentClickListener() {
            @Override
            public void onComment(int position) {
                Intent intent = new Intent(getActivity(), PostDetailsActivity.class);
                intent.putExtra("tid", list.get(position).getId());
                startActivity(intent);
            }
        });


        initData();
        refresh.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        p++;
                        initData();
                        refreshLayout.finishLoadMore();
                    }
                }, 200);
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        p = 1;
                        list.clear();
                        initData();
                        refreshLayout.finishRefresh();
                    }
                }, 200);
            }
        });
    }

    public static CommunityFragment getInstance(int type) {
        CommunityFragment fragment = new CommunityFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        fragment.setArguments(bundle);
        return fragment;
    }

    // 获取列表数据
    public void initData() {
        int position = getArguments().getInt("type");
        if (position == 0) {
            getFollowPostList();
        } else if (position == 1) {
            getHostPostList();
        } else if (position == 2) {
            getNewPostList();
        } else if (position == 3) {
            getJinXuanPostList();
        } else if (position == 4) {
            getMyPostList();
        }
    }

    // 举报评论
    public void report(String cid) {
        Map<String, String> map = new HashMap<>();
        map.put("uid", AppConfig.getInstance().getUid());
        map.put("cid", cid);
        OkHttp.postAsync(HttpContract.JU_BAO, map, new OkHttp.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {

            }

            @Override
            public void requestFailure(Request request, IOException e) {

            }
        });
    }

    // 获取最新帖子
    public void getFollowPostList() {
        Map<String, String> map = new HashMap<>();
        map.put("p", String.valueOf(p));
        map.put("uid", AppConfig.getInstance().getUid());
        OkHttp.postAsync(HttpContract.FOLLOW_LIST, map, new OkHttp.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                JsonObject data = jsonObject.getAsJsonObject("data");
                JsonArray jsonArray = data.getAsJsonArray("ad_list").getAsJsonArray();
                List<CircleChildEntry> childEntries = new Gson().fromJson(jsonArray.toString(), new TypeToken<List<CircleChildEntry>>() {
                }.getType());
                list.addAll(childEntries);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                e.printStackTrace();
            }
        });

    }

    // 获取最新帖子
    public void getNewPostList() {
        Map<String, String> map = new HashMap<>();
        map.put("p", String.valueOf(p));
        map.put("uid", AppConfig.getInstance().getUid());
        OkHttp.postAsync(HttpContract.NEW_LIST, map, new OkHttp.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                JsonObject data = jsonObject.getAsJsonObject("data");
                JsonArray jsonArray = data.getAsJsonArray("ad_list").getAsJsonArray();
                List<CircleChildEntry> childEntries = new Gson().fromJson(jsonArray.toString(), new TypeToken<List<CircleChildEntry>>() {
                }.getType());
                list.addAll(childEntries);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                e.printStackTrace();
            }
        });

    }

    // 获取最热的帖子
    public void getHostPostList() {
        Map<String, String> map = new HashMap<>();
        map.put("p", String.valueOf(p));
        map.put("uid", AppConfig.getInstance().getUid());
        OkHttp.postAsync(HttpContract.HOT_POST, map, new OkHttp.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                JsonObject data = jsonObject.getAsJsonObject("data");
                JsonArray jsonArray = data.getAsJsonArray("ad_list").getAsJsonArray();
                List<CircleChildEntry> childEntries = new Gson().fromJson(jsonArray.toString(), new TypeToken<List<CircleChildEntry>>() {
                }.getType());
                list.addAll(childEntries);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                e.printStackTrace();
            }
        });
    }

    // 获取精选帖子
    public void getJinXuanPostList() {
        Map<String, String> map = new HashMap<>();
        map.put("p", String.valueOf(p));
        map.put("uid", AppConfig.getInstance().getUid());
        OkHttp.postAsync(HttpContract.JIN_POST, map, new OkHttp.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                JsonObject data = jsonObject.getAsJsonObject("data");
                JsonArray jsonArray = data.getAsJsonArray("ad_list").getAsJsonArray();
                List<CircleChildEntry> childEntries = new Gson().fromJson(jsonArray.toString(), new TypeToken<List<CircleChildEntry>>() {
                }.getType());
                list.addAll(childEntries);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                e.printStackTrace();
            }
        });
    }

    // 获取我的帖子
    public void getMyPostList() {
        Map<String, String> map = new HashMap<>();
        map.put("p", String.valueOf(p));
        map.put("uid", AppConfig.getInstance().getUid());
        OkHttp.postAsync(HttpContract.myForumlistURL, map, new OkHttp.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                JsonObject data = jsonObject.getAsJsonObject("data");
                JsonArray jsonArray = data.getAsJsonArray("ad_list").getAsJsonArray();
                List<CircleChildEntry> childEntries = new Gson().fromJson(jsonArray.toString(), new TypeToken<List<CircleChildEntry>>() {
                }.getType());
                list.addAll(childEntries);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                e.printStackTrace();
            }
        });
    }

    // 点赞
    public void dianzan(String tid) {
        Map<String, String> map = new HashMap<>();
        map.put("uid", AppConfig.getInstance().getUid());
        map.put("tid", tid);
        OkHttp.postAsync(HttpContract.LIKE, map, new OkHttp.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                EventBus.getDefault().post(RefreshEvent.getInstance(200));
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                e.printStackTrace();
            }
        });
    }

    // 关注
    public void follow(String tid, String fid) {
        Map<String, String> map = new HashMap<>();
        map.put("tid", tid);
        map.put("uid", AppConfig.getInstance().getUid());
        map.put("fid", fid);
        OkHttp.postAsync(HttpContract.ForumSetgzURL, map, new OkHttp.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                EventBus.getDefault().post(RefreshEvent.getInstance(200));
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                e.printStackTrace();
            }
        });
    }

    // 收藏
    public void collection(String tid) {
        Map<String, String> map = new HashMap<>();
        map.put("tid", tid);
        map.put("uid", AppConfig.getInstance().getUid());
        OkHttp.postAsync(HttpContract.Collection_postURL, map, new OkHttp.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                ToastUtil.show("已收藏");
                EventBus.getDefault().post(RefreshEvent.getInstance(200));
            }

            @Override
            public void requestFailure(Request request, IOException e) {

            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshEvent(RefreshEvent e) {
        if (e.code == 200) {
            refresh.autoRefresh();
        }
    }
}
