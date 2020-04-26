package com.mibai.phonelive.purse.entry;

public class BaseBuyEntry<T> {

    /**
     * code : 383
     * data : null
     * message : 暂无可用支付码
     */

    private int code;
    private T data;
    private String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
