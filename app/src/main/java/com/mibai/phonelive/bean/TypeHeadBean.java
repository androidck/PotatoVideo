package com.mibai.phonelive.bean;

public class TypeHeadBean {

    private String name;
    private String des;
    private String url;
    private String thumb;
    private String if_up;

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

    @Override
    public String toString() {
        return "TypeHeadBean{" +
                "name='" + name + '\'' +
                ", des='" + des + '\'' +
                ", url='" + url + '\'' +
                ", thumb='" + thumb + '\'' +
                ", if_up='" + if_up + '\'' +
                '}';
    }
}
