package com.mibai.phonelive.glide;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.luck.picture.lib.tools.MediaUtils;
import com.luck.picture.lib.widget.longimage.ImageSource;
import com.luck.picture.lib.widget.longimage.ImageViewState;
import com.luck.picture.lib.widget.longimage.SubsamplingScaleImageView;
import com.mibai.phonelive.AppContext;
import com.mibai.phonelive.R;

import java.io.File;

/**
 * Created by cxf on 2017/8/9.
 */

public class ImgLoader {
    private static RequestManager sManager;

    static {
        sManager = Glide.with(AppContext.sInstance);
    }

    public static void display(String url, ImageView imageView) {
        sManager.load(url).dontAnimate().centerCrop().into(imageView);
    }


    public static void displayAvatar(String url, ImageView imageView) {
        sManager.load(url).dontAnimate().centerCrop().placeholder(R.mipmap.ic_default_avatar).into(imageView);
    }


    public static void display(File file, ImageView imageView) {
        sManager.load(file).dontAnimate().centerCrop().into(imageView);
    }

    /**
     * 显示视频封面缩略图
     */
    public static void displayVideoThumb(String videoPath, ImageView imageView) {
        sManager.load(Uri.fromFile(new File(videoPath))).into(imageView);
    }

    public static void displayBitmap(String url, final BitmapCallback bitmapCallback) {

   /*     sManager.load(url)*/
        /*sManager.load(url).asBitmap().skipMemoryCache(true).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                if (bitmapCallback != null) {
                    bitmapCallback.callback(bitmap);
                }
            }
        });*/
        Glide.with(AppContext.sInstance)
                .asBitmap()
                .load(url)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        if (bitmapCallback != null) {
                            bitmapCallback.callback(resource);
                        }
                    }
                });

    }

    public static void display(String url, ImageView imageView, int placeholderRes) {
        sManager.load(url).placeholder(placeholderRes).into(imageView);
    }

    public static void display(int res, ImageView imageView) {
        sManager.load(res).into(imageView);
    }


    public interface BitmapCallback {
        void callback(Bitmap bitmap);
    }


}
