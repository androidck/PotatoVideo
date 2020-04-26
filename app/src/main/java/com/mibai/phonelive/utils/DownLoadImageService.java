package com.mibai.phonelive.utils;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;

import java.io.File;

public class DownLoadImageService implements Runnable {
    private String url;
    private Context context;
    private ListenerManger.ImageDownLoadCallBack callBack;

    public DownLoadImageService(Context context, String url, ListenerManger.ImageDownLoadCallBack callBack) {
        this.url = url;
        this.callBack = callBack;
        this.context = context;
    }

    @Override
    public void run() {
        File file = null;
        try {
            FutureTarget<File> future = Glide.with(context)
                    .load(url)
                    .downloadOnly(500, 500);

            file = future.get();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (file != null) {
                callBack.onDownLoadSuccess(url, file);
            } else {
                callBack.onDownLoadFailed();
            }
        }
    }

}
