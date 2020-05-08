package com.mibai.phonelive.activity;

import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.mibai.phonelive.AppConfig;
import com.mibai.phonelive.R;
import com.mibai.phonelive.bean.AdvertiseBean;
import com.mibai.phonelive.bean.UserBean;
import com.mibai.phonelive.http.CheckTokenCallback;
import com.mibai.phonelive.http.HttpUtil;
import com.mibai.phonelive.utils.AdvertiseUtils;
import com.mibai.phonelive.utils.GlideUtils;
import com.mibai.phonelive.utils.JsonUtils;
import com.mibai.phonelive.utils.ListenerManger;
import com.mibai.phonelive.utils.SharedPreferencesUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import java.io.File;

import static com.mibai.phonelive.http.HttpUtil.GET_AD_LIST;

/**
 * 启动页面
 */
public class LauncherActivity extends AbsActivity implements ListenerManger.ImageDownLoadCallBack {

    private Handler mHandler;
    private AdvertiseBean mAdvertiseBean;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_launcher;
    }

    @Override
    protected void main() {
        getAdvertise();
        mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (AppConfig.getInstance().isLogin()) {
                    //检查Token是否过期
                    HttpUtil.ifToken(new CheckTokenCallback() {
                        @Override
                        public void onSuccess(int code, String msg, String[] info) {
                            if (code == 0 && info.length > 0) {
                                UserBean userBean = JSON.parseObject(info[0], UserBean.class);
                                if (userBean != null) {
                                    SharedPreferencesUtil.getInstance().saveUserBeanJson(info[0]);
                                    AppConfig.getInstance().setUserBean(userBean);
                                }
                            } else if (code == 700) {
                                AppConfig.getInstance().logout();
                                AppConfig.getInstance().logoutJPush();
                            }
                        }
                    });
                } else {
                    //forwardMainActivity();
                }
            }
        }, 500);
    }


    private void forwardMainActivity() {
        startActivity(new Intent(mContext, MainActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        HttpUtil.cancel(HttpUtil.GET_CONFIG);
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        HttpUtil.cancel(HttpUtil.GET_AD_LIST);
        super.onDestroy();
    }

    private void getAdvertise() {
        OkGo.<String>get(HttpUtil.HTTP_URL + "service=Ad.getAdList").headers("Connection", "close")
                .tag(GET_AD_LIST).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                String datas = JsonUtils.getResponseByKey(response.body(), "data");
                Log.d("dasdasd", datas);
                int ret = JsonUtils.getResponseIntByKey(response.body(), "ret");
                if (ret == 200) {
                    String data = JsonUtils.getResponseByKey(response.body(), "data");
                    String ad_list = JsonUtils.getResponseByKey(data, "ad_list");
                    if (!ad_list.equals("false")) {
                        mAdvertiseBean = JsonUtils.deserialize(ad_list, AdvertiseBean.class);
                        if (!TextUtils.isEmpty(mAdvertiseBean.thumb)) {
                            GlideUtils.downLoadImg(LauncherActivity.this, mAdvertiseBean.thumb, LauncherActivity.this);
                        } else {
                            isGotoAdvActivity(!TextUtils.isEmpty(mAdvertiseBean.thumb));
                        }
                    } else {
                        isGotoAdvActivity(false);
                    }
                }
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                dismissLoading();
            }
        });

    }

    public void isGotoAdvActivity() {
        isGotoAdvActivity(true);
    }

    public void isGotoAdvActivity(boolean isGotoAdv) {
        //本地广告信息如果有就跳转
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if ((!AdvertiseUtils.isEmptyAdvLocPath(this)) && isGotoAdv) {
            //去广告页
            startActivity(AdvertiseActivity.class);
            overridePendingTransition(0, 0);
            finish();
        } else {
            //去主页
            if (!isGotoAdv) {
                AdvertiseUtils.delAdvLocPath(this);
            }
            startActivity(new Intent(LauncherActivity.this, MainActivity.class));
            finish();
        }
        finish();
    }

    @Override
    public void onDownLoadSuccess(String url, File file) {
        if (mAdvertiseBean == null) isGotoAdvActivity();
        //存储广告信息
        AdvertiseUtils.saveAdvInfo(mAdvertiseBean, LauncherActivity.this);
        //更新广告图片本地存储path
        AdvertiseUtils.saveAdvLocPath(file.getPath(), LauncherActivity.this);
        //跳转广告页
        isGotoAdvActivity();
    }

    @Override
    public void onDownLoadFailed() {
        //跳转广告页
        isGotoAdvActivity();
    }
}
