package com.mibai.phonelive.community.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.mibai.phonelive.AppConfig;
import com.mibai.phonelive.R;
import com.mibai.phonelive.activity.AbsActivity;
import com.mibai.phonelive.community.adapter.CommentNewAdapter;
import com.mibai.phonelive.community.bean.BaseResponseEntry;
import com.mibai.phonelive.community.bean.CommentEntry;
import com.mibai.phonelive.community.bean.CommentUserInfo;
import com.mibai.phonelive.community.bean.PostDetailsEntry;
import com.mibai.phonelive.community.event.RefreshEvent;
import com.mibai.phonelive.community.network.HttpContract;
import com.mibai.phonelive.community.ninegridlayout.NineGridTestLayout;
import com.mibai.phonelive.community.util.KeyboardUtils;
import com.mibai.phonelive.network.OkHttp;
import com.mibai.phonelive.utils.RelativeDateFormatUtils;
import com.mibai.phonelive.utils.StatusBarUtil;
import com.mibai.phonelive.utils.ToastUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.zhy.autolayout.AutoLinearLayout;

import org.greenrobot.eventbus.EventBus;
import org.salient.artplayer.VideoView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Request;


// 帖子详情
public class PostDetailsActivity extends AbsActivity {
    @BindView(R.id.titleView)
    TextView titleView;
    @BindView(R.id.btn_back)
    ImageView btnBack;


    @BindView(R.id.refresh)
    SmartRefreshLayout refresh;
    @BindView(R.id.ed_content)
    EditText edContent;
    @BindView(R.id.img_emoji)
    ImageView imgEmoji;
    @BindView(R.id.btn_send)
    Button btnSend;
    @BindView(R.id.ly_comment)
    AutoLinearLayout lyComment;
    @BindView(R.id.recyclerView)
    SwipeMenuRecyclerView recyclerView;

    private View headView;

    private CircleImageView user_head;
    private TextView tv_nick_name, img_gift, img_comment;
    private ImageView tv_is_vip;
    private TextView tv_location, tv_time, tv_attention, tv_report, tv_content, tv_label, tv_read_count, tv_support, tv_comment;
    private NineGridTestLayout ly_nine;
    private VideoView videoView;


    private String circleId;

    private CommentNewAdapter adapter;

    private List<CommentEntry> list = new ArrayList<>();

    private PostDetailsEntry entry;


    private String id;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_post_details;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @Override
    protected void main() {
        super.main();
        StatusBarUtil.setStatusBarMode(this, true, R.color.white);
        id = this.getIntent().getStringExtra("tid");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        headView = getLayoutInflater().inflate(R.layout.view_header_post_details, recyclerView, false);
        recyclerView.addHeaderView(headView);
        adapter = new CommentNewAdapter(this, list);
        recyclerView.setAdapter(adapter);
        user_head = headView.findViewById(R.id.user_head);
        tv_nick_name = headView.findViewById(R.id.tv_nick_name);
        tv_is_vip = headView.findViewById(R.id.tv_is_vip);
        img_gift = headView.findViewById(R.id.img_gift);
        img_comment = headView.findViewById(R.id.img_comment);
        tv_location = headView.findViewById(R.id.tv_location);
        tv_time = headView.findViewById(R.id.tv_time);
        tv_attention = headView.findViewById(R.id.tv_attention);
        tv_report = headView.findViewById(R.id.tv_report);
        tv_content = headView.findViewById(R.id.tv_content);
        tv_label = headView.findViewById(R.id.tv_label);
        tv_read_count = headView.findViewById(R.id.tv_read_count);
        tv_support = headView.findViewById(R.id.tv_support);
        tv_comment = headView.findViewById(R.id.tv_comment);
        ly_nine = headView.findViewById(R.id.ly_nine);
        videoView = headView.findViewById(R.id.video_view);

        // 删除评论
        adapter.setOnDelCommentListener(new CommentNewAdapter.OnDelCommentListener() {
            @Override
            public void onDelComment(int position) {
                ToastUtil.show("敬请期待");
            }
        });
        // 评论点赞
        adapter.setOnLoveListener(new CommentNewAdapter.OnLoveListener() {
            @Override
            public void onLove(int position) {
                ToastUtil.show("敬请期待");
            }
        });

        setData();
        getComment();
    }

    public void setData() {
        Map<String, String> map = new HashMap<>();
        map.put("id", id);
        map.put("uid", AppConfig.getInstance().getUid());
        OkHttp.postAsync(HttpContract.ForumDetailstzURL, map, new OkHttp.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                JsonObject data = jsonObject.getAsJsonObject("data");
                JsonArray jsonArray = data.getAsJsonArray("ad_list");
                List<PostDetailsEntry> lists = new Gson().fromJson(jsonArray.toString(), new TypeToken<List<PostDetailsEntry>>() {
                }.getType());
                entry = lists.get(0);
                getDetails(lists.get(0));
            }

            @Override
            public void requestFailure(Request request, IOException e) {

            }
        });
    }

    public void getComment() {
        Map<String, String> map = new HashMap<>();
        map.put("tid", id);
        map.put("uid", AppConfig.getInstance().getUid());
        OkHttp.postAsync(HttpContract.ForumGetpinlURL, map, new OkHttp.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                JsonObject data = jsonObject.getAsJsonObject("data");
                JsonArray jsonArray = data.getAsJsonArray("ad_list");
                List<CommentEntry> entryList = new Gson().fromJson(jsonArray.toString(), new TypeToken<List<CommentEntry>>() {
                }.getType());
                list.addAll(entryList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void requestFailure(Request request, IOException e) {

            }
        });
    }


    // 获取帖子详情
    public void getDetails(PostDetailsEntry entry) {
        setTitle(entry.getUserinfo().getUser_nicename());
        // 头像
        Glide.with(this).load(entry.getUserinfo().getAvatar()).placeholder(R.mipmap.ic_launcher).into(user_head);
        tv_nick_name.setText(entry.getUserinfo().getUser_nicename());
        tv_time.setText(RelativeDateFormatUtils.format(new Date(Long.parseLong(entry.getAddtime()) * 1000)));
        tv_content.setText(entry.getContent());
        if ("0".equals(entry.getType())) {
            String[] str = entry.getImg().split(",");
            ly_nine.setVisibility(View.VISIBLE);
            videoView.setVisibility(View.GONE);
            ly_nine.setUrlList(Arrays.asList(str));
        } else if ("1".equals(entry.getType())) {
            videoView.setVisibility(View.VISIBLE);
            ly_nine.setVisibility(View.GONE);
        }
        if (1 == entry.getIsdz()) {
            Drawable drawableLeft = getResources().getDrawable(R.mipmap.icon_yizan);
            drawableLeft.setBounds(0, 0, drawableLeft.getMinimumWidth(), drawableLeft.getMinimumHeight());
            tv_support.setCompoundDrawables(drawableLeft, null, null, null);
        } else {
            Drawable drawableLeft = getResources().getDrawable(R.mipmap.icon_zan);
            drawableLeft.setBounds(0, 0, drawableLeft.getMinimumWidth(), drawableLeft.getMinimumHeight());
            tv_support.setCompoundDrawables(drawableLeft, null, null, null);
        }


        // 是否关注
        if (1 == entry.getIsgz()) {
            tv_attention.setBackgroundResource(R.drawable.shape_setup_attent_button);
            tv_attention.setText("已关注");
            tv_attention.setCompoundDrawables(null, null, null, null);
            tv_attention.setPadding(0, 6, 0, 6);
        } else {
            tv_attention.setBackgroundResource(R.drawable.shape_setup_button);
            tv_attention.setText("关注");
            tv_attention.setCompoundDrawables(setDrawable(R.mipmap.icon_add_follow), null, null, null);
            tv_attention.setPadding(20, 6, 0, 6);
        }

        tv_label.setText(entry.getLabel());
        //  tv_read_count.setText("浏览量：" + entry.getLiulan());
        tv_support.setText(entry.getLike_num());
        tv_comment.setText(entry.getComments_num());
        tv_location.setText(entry.getLocaltion());
        circleId = entry.getId();
        list.clear();

        // 收藏
        img_gift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collection(id);
            }
        });

        // 点赞
        tv_support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dianzan(id);
            }
        });

        // 关注 取消关注
        tv_attention.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                follow(id, entry.getUserinfo().getId());
            }
        });

        // 举报
        tv_support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // 私信
        img_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public Drawable setDrawable(int desId) {
        Drawable drawable = getResources().getDrawable(desId);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        return drawable;
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
                EventBus.getDefault().post(RefreshEvent.getInstance(200));
            }

            @Override
            public void requestFailure(Request request, IOException e) {

            }
        });
    }


    @OnClick(R.id.btn_send)
    public void onViewClicked() {
        if (TextUtils.isEmpty(edContent.getText().toString().trim())) {
            ToastUtil.show("请输入评论内容");
        } else {
            onComment(edContent.getText().toString().trim());
        }
    }

    public void onComment(String content) {
        Map<String, String> map = new HashMap<>();
        map.put("tid", id);
        map.put("uid", AppConfig.getInstance().getUid());
        map.put("content", content);
        OkHttp.postAsync(HttpContract.ForumSetpinlURL, map, new OkHttp.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                BaseResponseEntry entry = new Gson().fromJson(result, BaseResponseEntry.class);
                if (entry.getRet() == 200) {
                    ToastUtil.show("评论成功");
                    // 刷新评论列表
                    list.clear();
                    getComment();
                    KeyboardUtils.hideKeyboard(edContent);
                } else {
                    ToastUtil.show("评论失败");
                }
                edContent.setText("");

            }

            @Override
            public void requestFailure(Request request, IOException e) {

            }
        });
    }
}
