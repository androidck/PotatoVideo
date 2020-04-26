package com.mibai.phonelive.bean;

import java.util.List;

public class VideoBuyBean {


    /**
     * code : 400
     * msg : 你没钱
     * info : []
     */

    private int code;
    private String msg;

    private VideoBuyInfo info;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public VideoBuyInfo getInfo() {
        return info;
    }

    public void setInfo(VideoBuyInfo info) {
        this.info = info;
    }
}
