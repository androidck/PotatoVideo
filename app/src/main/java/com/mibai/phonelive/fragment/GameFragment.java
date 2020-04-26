package com.mibai.phonelive.fragment;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mibai.phonelive.R;
import com.mibai.phonelive.bean.YuLeBean;
import com.mibai.phonelive.http.HttpUtil;
import com.mibai.phonelive.network.OkHttp;

import java.io.IOException;

import okhttp3.Request;

/**
 * Created by cxf on 2018/6/5.
 */

// 关注
public class GameFragment extends AbsFragment {

    private ImageView image;

    private String url;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_game;
    }

    @Override
    protected void main() {
        image = mRootView.findViewById(R.id.image);
        initData();
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(url);
                intent.setData(content_url);
                startActivity(intent);
            }
        });
    }

    private void initData() {
        OkHttp.getAsync(HttpUtil.HTTP_URL + "service=Ad.getGame", new OkHttp.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                JsonObject data = jsonObject.getAsJsonObject("data");
                JsonObject info = data.getAsJsonObject("shareurl");
                YuLeBean bean = new Gson().fromJson(info.toString(), YuLeBean.class);
                Glide.with(mContext).load(bean.getPic()).into(image);
                url = bean.getUrl();
            }

            @Override
            public void requestFailure(Request request, IOException e) {

            }
        });
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

    }


    @Override
    public void onDestroy() {

        super.onDestroy();
    }
}
