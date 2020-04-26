package com.mibai.phonelive.purse;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.mibai.phonelive.AppConfig;
import com.mibai.phonelive.R;
import com.mibai.phonelive.activity.AbsActivity;
import com.mibai.phonelive.adapter.MyPurseAdapter;
import com.mibai.phonelive.dialog.WithdrawDialog;
import com.mibai.phonelive.http.HttpUtil;
import com.mibai.phonelive.network.OkHttp;
import com.mibai.phonelive.purse.entry.BaseBuyEntry;
import com.mibai.phonelive.purse.entry.BuyEntry;
import com.mibai.phonelive.purse.entry.CoinEntry;
import com.mibai.phonelive.purse.entry.MyPurseEntry;
import com.mibai.phonelive.purse.entry.WithdrawEntry;
import com.mibai.phonelive.utils.ToastUtil;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Request;

// 我的钱包
public class MyPurseActivity extends AbsActivity {
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.btn_back)
    ImageView btnBack;
    @BindView(R.id.btn_more)
    ImageView btnMore;
    @BindView(R.id.tv_money)
    TextView tvMoney;
    @BindView(R.id.tv_tx)
    TextView tvTx;
    @BindView(R.id.tv_mx)
    TextView tvMx;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private MyPurseAdapter adapter;

    private List<MyPurseEntry> list = new ArrayList<>();

    private int count;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_purse;
    }

    @Override
    protected void main() {
        super.main();
        title.setText("我的钱包");
        title.setTextColor(Color.parseColor("#ffffff"));
        GridLayoutManager manager = new GridLayoutManager(mContext, 3);
        recyclerView.setLayoutManager(manager);
        adapter = new MyPurseAdapter(this, list);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new MyPurseAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                buyJinbi(list.get(position).getMoney());
            }
        });
        getData();
    }

    public void getData() {
        String str = HttpUtil.HTTP_URL + "service=User.getMonco&uid=" + AppConfig.getInstance().getUid();
        OkHttp.getAsync(str, new OkHttp.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                JsonObject object = new JsonParser().parse(result).getAsJsonObject();
                JsonObject jsonObject = object.getAsJsonObject("data");
                JsonObject object1 = jsonObject.getAsJsonObject("info");
                JsonObject object2 = object1.getAsJsonObject("coin");
                CoinEntry entry = new Gson().fromJson(object2.toString(), CoinEntry.class);
                tvMoney.setText(entry.getMocoin());
                count = Integer.parseInt(entry.getMocoin());
                JsonArray jsonArray = object1.getAsJsonArray("type");
                List<MyPurseEntry> myPurseEntries = new Gson().fromJson(jsonArray.toString(), new TypeToken<List<MyPurseEntry>>() {
                }.getType());
                list.addAll(myPurseEntries);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void requestFailure(Request request, IOException e) {

            }
        });
    }

    // 购买金币
    public void buyJinbi(String money) {
        String url = HttpUtil.HTTP_URL + "service=User.userUpmon&uid=" + AppConfig.getInstance().getUid() + "&money=" + money + "&type=" + "1";
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


    @OnClick({R.id.btn_back, R.id.tv_tx, R.id.tv_mx})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.tv_tx:
                new WithdrawDialog(mContext, count, new WithdrawDialog.OnSuccessListener() {
                    @Override
                    public void onSuccess(String money, String account) {
                        // 调用提现的接口
                        tx(money, account);
                    }
                }).show();
                break;
            case R.id.tv_mx:
                startActivity(new Intent(this, ColdCoinDetailsActivity.class));
                break;
        }
    }

    // 提现
    public void tx(String money, String number) {
        String str = HttpUtil.HTTP_URL + "service=User.addTixian&uid=" + AppConfig.getInstance().getUid() + "&money=" + money + "&number=" + number;
        OkHttp.getAsync(str, new OkHttp.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                JsonObject jsonObject1 = jsonObject.getAsJsonObject("data");
                JsonObject object = jsonObject1.getAsJsonObject("info");
                WithdrawEntry entry = new Gson().fromJson(object.toString(), WithdrawEntry.class);
                if ("200".equals(entry.getStatus())) {
                    ToastUtil.show("提现处理中");
                    list.clear();
                    getData();
                } else {
                    ToastUtil.show("提现失败");
                }
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                e.printStackTrace();
            }
        });
    }
}
