package com.mibai.phonelive.wrap;

import android.content.Context;

public class PlayProgressEntry {

    // 当前播放进度
    private int progress;

    // 总进度
    private int allProgress;

    private Context mContext;

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getAllProgress() {
        return allProgress;
    }

    public void setAllProgress(int allProgress) {
        this.allProgress = allProgress;
    }
}
