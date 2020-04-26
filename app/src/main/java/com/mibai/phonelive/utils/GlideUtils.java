package com.mibai.phonelive.utils;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;

import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by qingning on 2016/12/5.
 */
public class GlideUtils {

    /**
     * 加载图片的载体，图片会跟随载体生命周期销毁，??????好不要使用Application，图片会??????直存??????
     *
     * @param t   Fragment ?????? FragmentActivity ?????? Activity ?????? Context
     * @param <T> Fragment ?????? FragmentActivity ?????? Activity ?????? Context
     * @return glide请求管理??????
     */

    public static <T> RequestManager with(T t) {
        if (t instanceof Fragment) {
            Fragment with = (Fragment) t;
            return Glide.with(with);
        } else if (t instanceof FragmentActivity) {
            FragmentActivity with = (FragmentActivity) t;
            return Glide.with(with);
        } else if (t instanceof Activity) {
            Activity with = (Activity) t;
            return Glide.with(with);
        } else if (t instanceof Fragment) {
            Fragment with = (Fragment) t;
            return Glide.with(with);
        } else if (t instanceof Context) {
            Context with = (Context) t;
            return Glide.with(with);
        } else {
            System.out.println("图片会一直存??????");
            //return Glide.with(MumuApplication.application);
            return null;
        }
    }

//
//    /**
//     * 加载网络图片
//     * @param imageView
//     * @param imgUrl
//     * @param context
//     * @param placeholderResId  加载中图片或颜色
//     * @param errorResId        加载失败图片或颜??????
//     */
//    public static void loadNetImageViewImg(ImageView imageView, String imgUrl, Context context, int placeholderResId, int errorResId) {
// 	   GlideUtils.with(context).load(imgUrl).dontAnimate().placeholder(placeholderResId).error(errorResId).diskCacheStrategy(DiskCacheStrategy.RESULT).crossFade().into(imageView);
//     }
//

    /**
     * 加载网络图片到view
     *
     * @param imageView
     * @param imgUrl
     * @param context
     */
//    public static void loadNetImageViewImg(ImageView imageView, String imgUrl, Context context) {
//        RequestOptions options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE).centerCrop();
//        GlideUtils.with(context).load(imgUrl).apply(options)
//                .into(imageView);
//    }
//
//
//    /**
//     * 加载网络图片到view
//     *
//     * @param imageView
//     * @param imgUrl
//     * @param context
//     */
//    public static void loadNetImageViewImgALL(ImageView imageView, String imgUrl, Context context) {
//        RequestOptions options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE).centerCrop();
//        GlideUtils.with(context).load(imgUrl).apply(options)
//                //.dontAnimate()
//                .into(imageView);
//    }

    /**
     * 单线程列队执行
     */
    private static ExecutorService singleExecutor = null;

    /**
     * 执行单线程列队执行
     */
    public static void runOnQueue(Runnable runnable) {
        if (singleExecutor == null) {
            singleExecutor = Executors.newSingleThreadExecutor();
        }
        singleExecutor.submit(runnable);
    }

    /**
     * 下载网络图片
     * @param imageView
     * @param imgUrl
     * @param context
     */
    /**
     * 启动图片下载线程
     */
    public static void downLoadImg(Context context, String url, ListenerManger.ImageDownLoadCallBack callBack) {
        DownLoadImageService service = new DownLoadImageService(context, url, callBack);
        //启动图片下载线程
        runOnQueue(service);
    }
//
//    /**
//     * 加载本地图片
//     * @param imageView
//     * @param imgResId
//     * @param context
//     */
//    public static void loadLocImageViewImg(ImageView imageView, int imgResId, Context context) {
// 	   GlideUtils.with(context).load(imgResId).crossFade().into(imageView);
//     }


//    public static <T> void loadBlur(T t, String url, ImageView imageView) {
//        with(t).load(url).bitmapTransform(new BlurTransformation(MyApplication.getInstance())).placeholder(R.color.white).error(R.color.white).into(imageView);
//    }

//    public static <T> void loadOnceGif(T t, int res, ImageView imageView, final OnCompleteListener onCompleteListener) {
//        GlideDrawableImageViewTarget glideDrawableImageViewTarget = new GlideDrawableImageViewTarget(imageView, 1);
//        with(t).load(res).listener(new RequestListener<Integer, GlideDrawable>() {
//            @Override
//            public boolean onException(Exception e, Integer model, Target<GlideDrawable> target, boolean isFirstResource) {
//                return false;
//            }
//
//            @Override
//            public boolean onResourceReady(GlideDrawable resource, Integer model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                int duration = 0;
//                try {
//                    GifDrawable gifDrawable = (GifDrawable) resource;
//                    GifDecoder decoder = gifDrawable.getDecoder();
//                    for (int i = 0; i < gifDrawable.getFrameCount(); i++) {
//                        int delay = decoder.getDelay(i);
//                        duration += delay;
//                    }
//                } catch (Throwable e) {
//                    e.printStackTrace();
//                }
//                Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (onCompleteListener != null) {
//                            onCompleteListener.onComplete();
//                        }
//                    }
//                }, duration);
//                return false;
//            }
//        }).into(glideDrawableImageViewTarget);
//    }
}
