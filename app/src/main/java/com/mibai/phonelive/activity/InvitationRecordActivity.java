package com.mibai.phonelive.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.mibai.phonelive.AppConfig;
import com.mibai.phonelive.R;
import com.mibai.phonelive.adapter.InvitationRecordAdapter;
import com.mibai.phonelive.bean.InvitationAllEntry;
import com.mibai.phonelive.bean.InvitationEntry;
import com.mibai.phonelive.http.HttpUtil;
import com.mibai.phonelive.network.OkHttp;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Request;

// 邀请记录
public class InvitationRecordActivity extends AbsActivity {
    @BindView(R.id.titleView)
    TextView titleView;
    @BindView(R.id.btn_back)
    ImageView btnBack;
    @BindView(R.id.recyclerView)
    SwipeMenuRecyclerView recyclerView;
    @BindView(R.id.tv_all_count)
    TextView tvAllCount;
    @BindView(R.id.ly_data)
    LinearLayout lyData;
    @BindView(R.id.no_data)
    TextView noData;
    @BindView(R.id.ly_no_data)
    LinearLayout lyNoData;

    private InvitationRecordAdapter adapter;

    private List<InvitationEntry> list = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_invitaion_record;
    }

    @Override
    protected void main() {
        super.main();
        initData();
        titleView.setText("邀请记录");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new InvitationRecordAdapter(this, list);
        recyclerView.setAdapter(adapter);
    }

    private void initData() {
        getData();
    }

    public void getData() {
        OkHttp.getAsync(HttpUtil.HTTP_URL + "service=User.userShare&uid=" + AppConfig.getInstance().getUid(), new OkHttp.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                JsonObject data = jsonObject.getAsJsonObject("data");
                JsonArray array = data.getAsJsonArray("info");
                if (array != null) {
                    lyData.setVisibility(View.VISIBLE);
                    lyNoData.setVisibility(View.GONE);
                    InvitationAllEntry entry = new Gson().fromJson(data.toString(), InvitationAllEntry.class);
                    tvAllCount.setText("团队总邀请人数：" + entry.getZongji());
                    List<InvitationEntry> entries = new Gson().fromJson(array.toString(), new TypeToken<List<InvitationEntry>>() {
                    }.getType());
                    list.addAll(entries);
                    adapter.notifyDataSetChanged();
                } else {
                    lyData.setVisibility(View.GONE);
                    lyNoData.setVisibility(View.VISIBLE);
                    noData.setText("暂无数据");
                }

            }

            @Override
            public void requestFailure(Request request, IOException e) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

   /* public List<InvitationEntry> getData() {
        List<InvitationEntry> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            InvitationEntry entry = new InvitationEntry();
            entry.setInvitationCode("bPaJq");
            entry.setPhone("131****8259");
            entry.setRegisterStatus("已注册");
            list.add(entry);
        }
        return list;
    }*/
}
