package com.mibai.phonelive;

import android.content.Context;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.orhanobut.hawk.Hawk;
import com.orhanobut.hawk.NoEncryption;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.crashreport.CrashReport;
import com.mibai.phonelive.http.HttpUtil;
import com.mibai.phonelive.jpush.JMessageUtil;
import com.mibai.phonelive.jpush.JPushUtil;
import com.mibai.phonelive.utils.SharedPreferencesUtil;
import com.tencent.rtmp.TXLiveBase;

import cn.sharesdk.framework.ShareSDK;
import cn.tillusory.sdk.TiSDK;


/**
 * Created by cxf on 2017/8/3.
 */

public class AppContext extends MultiDexApplication {

    public static AppContext sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        //腾讯云鉴权url
        String ugcLicenceUrl = "http://license.vod2.myqcloud.com/license/v1/efe42ecec6195bc3b6b49a85724c6570/TXUgcSDK.licence";
        //腾讯云鉴权key
        String ugcKey = "04695dbce3ae7845e87785c0f0046b67";
        TXLiveBase.getInstance().setLicence(this, ugcLicenceUrl, ugcKey);
        //初始化腾讯bugly
        CrashReport.initCrashReport(getApplicationContext());

        Bugly.init(this, "123dd0b57f", true);
        //初始化http
        HttpUtil.init();
        //初始化ShareSdk
        ShareSDK.initSDK(this);
        //初始化极光推送
        JPushUtil.getInstance().init();
        //初始化极光IM
        JMessageUtil.getInstance().init();
        //初始化萌颜
       // TiSDK.init(AppConfig.BEAUTY_KEY, this);
        //获取uid和token
        String[] uidAndToken = SharedPreferencesUtil.getInstance().readUidAndToken();
        if (uidAndToken != null) {
            AppConfig.getInstance().login(uidAndToken[0], uidAndToken[1]);
        }

        Hawk.init(this)
                .setEncryption(new NoEncryption())
                .build();

        initImageLoader();
    }

    @Override
    protected void attachBaseContext(Context base) {
        MultiDex.install(this);
        super.attachBaseContext(base);
    }

    private void initImageLoader() {
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(this);
        ImageLoader.getInstance().init(configuration);

    }

    //全局设置上拉加载、下拉刷新样式
    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                layout.setPrimaryColorsId(R.color.white, R.color.colorDeep);//全局设置主题颜色
                return new ClassicsHeader(context);//指定为经典Header，默认是 贝塞尔雷达Header
            }
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                //指定为经典Footer，默认是 BallPulseFooter
                return new ClassicsFooter(context).setDrawableSize(20);
            }
        });
    }
}
