package com.mibai.phonelive.utils;

import java.io.File;

/**
 * @author legendary
 * @version 1.0
 * @created 2018/6/9 3:35
 * @description enter
 */
public class ListenerManger {
    /**
     * recycleview item 点击事件
     */
    public interface OnClickItemRecycListener {
        void onClickItem(int position);
    }
    /**
     * recycleview item 点击事件
     */
    public interface OnClickItemRecycListenerT<T> {
        void onClickItem(int position, T obj);
    }
    /**
     * recycleview item 点击事件
     */
    public interface ImageDownLoadCallBack {
        void onDownLoadSuccess(String url, File file);
        void onDownLoadFailed();
    }

}

