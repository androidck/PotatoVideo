package com.mibai.phonelive.purse;


import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.mibai.phonelive.AppConfig;
import com.mibai.phonelive.R;
import com.mibai.phonelive.activity.AbsActivity;
import com.mibai.phonelive.adapter.ColdCoinAdapter;
import com.mibai.phonelive.http.HttpUtil;
import com.mibai.phonelive.network.OkHttp;
import com.mibai.phonelive.purse.entry.JinBiDetailsEntry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Request;

//金币收支明细
public class ColdCoinDetailsActivity extends AbsActivity {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.btn_back)
    ImageView btnBack;
    @BindView(R.id.btn_more)
    ImageView btnMore;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private List<JinBiDetailsEntry> list = new ArrayList<>();
    private ColdCoinAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_cold_coin_details;
    }


    @Override
    protected void main() {
        super.main();
        title.setText("金币收支明细");
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        adapter = new ColdCoinAdapter(mContext, list);
        recyclerView.setAdapter(adapter);
        getDetails();
    }

    @OnClick(R.id.btn_back)
    public void onViewClicked() {
        finish();
    }

    public void getDetails() {
        String str = HttpUtil.HTTP_URL + "service=User.userBudget&uid=" + AppConfig.getInstance().getUid();
        OkHttp.getAsync(str, new OkHttp.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                Log.d("coldcoinResult", result);

                JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                JsonObject jsonObject1 = jsonObject.getAsJsonObject("data");
                JsonArray jsonArray = jsonObject1.getAsJsonArray("info");
                List<JinBiDetailsEntry> entries = new Gson().fromJson(jsonArray.toString(), new TypeToken<List<JinBiDetailsEntry>>() {
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
}
