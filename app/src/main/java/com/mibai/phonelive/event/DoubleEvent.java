package com.mibai.phonelive.event;

import com.mibai.phonelive.bean.VideoBean;
import com.mibai.phonelive.custom.VideoPlayWrap;

// 双击点赞
public class DoubleEvent {

    public final VideoBean bean;
    public final VideoPlayWrap wrap;

    private DoubleEvent(VideoPlayWrap wrap, VideoBean videoBean) {
        this.wrap = wrap;
        this.bean = videoBean;
    }

    public static DoubleEvent getInstance(VideoPlayWrap wrap,VideoBean videoBean) {
        return new DoubleEvent(wrap,videoBean);
    }
}
