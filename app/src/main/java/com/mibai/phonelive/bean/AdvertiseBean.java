package com.mibai.phonelive.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2019/4/25.
 */

public class AdvertiseBean implements Serializable{


    /**
     * name : 超级女声 养成期全景直播
     * des : 222历经5个月层层筛选，超女100强从61万选手中脱颖而出。接下来，百强选手们将进入比赛的第二阶段——“养成”阶段。每天24小时全程全景直播，精彩不间断！
     * url : http://api.hongyuqkl.com/9005
     * thumb : http://pq287addd.bkt.clouddn.com/20190418_5cb75edfb3630.jpeg
     * if_up : 1
     */

    public String name;
    public String des;
    public String url;
    public String thumb;
    public String if_up;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getIf_up() {
        return if_up;
    }

    public void setIf_up(String if_up) {
        this.if_up = if_up;
    }
}
