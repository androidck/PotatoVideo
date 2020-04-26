package com.mibai.phonelive.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.mibai.phonelive.bean.AdvertiseBean;

/**
 * Created by Administrator on 2019/4/25.
 */

public class AdvertiseUtils {


    /**
     * 存储启动广告信息
     *
     * @param adv
     * @param context
     */
    public static void saveAdvInfo(AdvertiseBean adv, Context context) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences("avd", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString("name", adv.name);
        editor.putString("des", adv.des);
        editor.putString("url", adv.url);
        editor.putString("thumb", adv.thumb);
        editor.commit();
    }


//   id	string	广告id
//   name	string	广告名称
//   link	string	广告链接
//   image	string	广告图片

    /**
     * 获取启动广告信息
     *
     * @param context
     * @return
     */
    public static AdvertiseBean getAdvInfo(Context context) {
        AdvertiseBean avd = new AdvertiseBean();
        SharedPreferences sharedPreferences = context.getSharedPreferences("avd", Activity.MODE_PRIVATE);
        avd.name = sharedPreferences.getString("name", "");
        avd.des = sharedPreferences.getString("des", "");
        avd.url = sharedPreferences.getString("url", "");
        avd.thumb = sharedPreferences.getString("thumb", "");
        ;
        return avd;
    }

    /**
     * 删除启动广告信息
     */
    public static void delAdvInfo(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("avd", Activity.MODE_PRIVATE);
        sharedPreferences.edit().clear().commit();
    }


    /**
     * 存储启动广告本地缓存path
     *
     * @param path
     * @param context
     */
    public static void saveAdvLocPath(String path, Context context) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences("avd_loc_path", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString("path", path);
        editor.commit();
    }

    /**
     * 获取启动广告本地缓存path
     *
     * @param context
     * @return
     */
    public static String getAdvLocPath(Context context) {
        AdvertiseBean avd = new AdvertiseBean();
        SharedPreferences sharedPreferences = context.getSharedPreferences("avd_loc_path", Activity.MODE_PRIVATE);
        return sharedPreferences.getString("path", "");
    }

    /**
     * 删除启动广告本地缓存path
     */
    public static void delAdvLocPath(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("avd_loc_path", Activity.MODE_PRIVATE);
        sharedPreferences.edit().clear().commit();
    }


    /**
     * 校验本地图片是否有缓存path
     *
     * @param context
     */
    public static boolean isEmptyAdvLocPath(Context context) {
        String path = getAdvLocPath(context);
        return TextUtils.isEmpty(path);
    }
}
