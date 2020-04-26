package com.mibai.phonelive.bean;

/**
 * Created by cxf on 2018/7/20.
 */

public class OffLineMsgEvent {
    private ChatUserBean mBean;

    public OffLineMsgEvent(ChatUserBean bean) {
        mBean = bean;
    }

    public ChatUserBean getChatUserBean() {
        return mBean;
    }
}