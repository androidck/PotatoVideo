package com.mibai.phonelive.community.event;

// 刷新eventBus
public class RefreshEvent {

    public final int code;

    private RefreshEvent(int code) {
        this.code = code;
    }

    public static RefreshEvent getInstance(int code) {
        return new RefreshEvent(code);
    }
}
