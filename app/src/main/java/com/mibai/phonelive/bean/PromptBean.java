package com.mibai.phonelive.bean;

// 弹窗提示类
public class PromptBean {

    /**
     * stat : 1
     * title : 弹窗
     * content : 默认内容
     */

    private String id;
    private String stat;
    private String title;
    private String content;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStat() {
        return stat;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
