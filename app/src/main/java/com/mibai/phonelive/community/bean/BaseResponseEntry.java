package com.mibai.phonelive.community.bean;

public class BaseResponseEntry {


    /**
     * ret : 200
     * data : null
     * msg :
     */

    private int ret;
    private Object data;
    private String msg;

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
