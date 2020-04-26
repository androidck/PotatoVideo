package com.mibai.phonelive.purse;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.mibai.phonelive.AppConfig;
import com.mibai.phonelive.R;
import com.mibai.phonelive.activity.AbsActivity;
import com.mibai.phonelive.adapter.VipPayAdapter;
import com.mibai.phonelive.http.HttpUtil;
import com.mibai.phonelive.network.OkHttp;
import com.mibai.phonelive.purse.entry.BaseBuyEntry;
import com.mibai.phonelive.purse.entry.BuyEntry;
import com.mibai.phonelive.purse.entry.UserInfoEntry;
import com.mibai.phonelive.purse.entry.VipPayEntry;
import com.mibai.phonelive.utils.TimeUtil;
import com.mibai.phonelive.utils.ToastUtil;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Request;

// vip 充值
public class VipPayActivity extends AbsActivity {
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.btn_back)
    ImageView btnBack;
    @BindView(R.id.btn_more)
    ImageView btnMore;
    @BindView(R.id.user_head)
    CircleImageView userHead;
    @BindView(R.id.tv_account)
    TextView tvAccount;
    @BindView(R.id.img_icon)
    ImageView imgIcon;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private VipPayAdapter adapter;

    private List<VipPayEntry> list = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_vip_pay;
    }

    @Override
    protected void main() {
        super.main();
        title.setText("VIP会员充值");
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        adapter = new VipPayAdapter(this, list);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new VipPayAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                buyVip(list.get(position).getMoney());
            }
        });
        getData();
    }

    // 获取数据
    public void getData() {
        OkHttp.getAsync(HttpUtil.HTTP_URL + "service=User.userViptype&uid=" + AppConfig.getInstance().getUid(), new OkHttp.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                JsonObject object = jsonObject.getAsJsonObject("data");
                JsonArray jsonArray = object.getAsJsonArray("info");
                JsonObject jsonObject1 = object.getAsJsonObject("user");
                UserInfoEntry entry = new Gson().fromJson(jsonObject1.toString(), UserInfoEntry.class);
                tvAccount.setText(entry.getUser_nicename());
                if (!AppConfig.getInstance().isUser()) {
                    Glide.with(mContext).load("http://douying.qgnix.cn/default.jpg").into(userHead);
                }else {
                    Glide.with(mContext).load(entry.getAvatar()).into(userHead);
                }
                if (entry.getIsvip() != 0) {
                    tvTime.setText(entry.getVip_endtime());
                    tvTime.setVisibility(View.GONE);
                }else {
                    tvTime.setVisibility(View.VISIBLE);
                    tvTime.setText(TimeUtil.convertTimestamp2Date(Long.parseLong(entry.getVip_endtime()),"yyyy-MM-dd")+"\t到期");
                }
                List<VipPayEntry> entries = new Gson().fromJson(jsonArray, new TypeToken<List<VipPayEntry>>() {
                }.getType());
                list.addAll(entries);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    @OnClick({R.id.title, R.id.btn_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title:
                break;
            case R.id.btn_back:
                finish();
                break;
        }
    }

    // 购买金币
    public void buyVip(String money) {
        String url = HttpUtil.HTTP_URL + "service=User.userUpmon&uid=" + AppConfig.getInstance().getUid() + "&money=" + money + "&type=" + "0";
        OkHttp.getAsync(url, new OkHttp.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                Log.d("buyJinbiResult", result);
                JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                JsonObject data_one = jsonObject.getAsJsonObject("data");
                JsonObject info = data_one.getAsJsonObject("info");
                JsonObject datas = info.getAsJsonObject("datas");
                BaseBuyEntry<BuyEntry> response = new Gson().fromJson(datas.toString(), BaseBuyEntry.class);
                if (response.getData() == null) {
                    ToastUtil.show("暂无可用通道");
                } else {
                    JsonObject data = datas.getAsJsonObject("data");
                    BuyEntry buyEntry = new Gson().fromJson(data.toString(), BuyEntry.class);
                    String url = URLDecoder.decode(buyEntry.getPay_qr_code(), "UTF-8");
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse(url);
                    intent.setData(content_url);
                    startActivity(intent);
                }
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                e.printStackTrace();
            }
        });
    }
}
