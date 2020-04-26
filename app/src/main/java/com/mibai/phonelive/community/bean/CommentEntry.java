package com.mibai.phonelive.community.bean;

public class CommentEntry {


    /**
     * id : 10
     * tid : 13
     * cid : 0
     * uid : 4
     * content : 测试评论内容
     * like_num : 0
     * addtime : 1587049028
     */

    private String id;
    private String tid;
    private String cid;
    private String uid;
    private String content;
    private String like_num;
    private String addtime;

    private CircleChildUserInfo userinfo;

    public CircleChildUserInfo getUserinfo() {
        return userinfo;
    }

    public void setUserinfo(CircleChildUserInfo userinfo) {
        this.userinfo = userinfo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLike_num() {
        return like_num;
    }

    public void setLike_num(String like_num) {
        this.like_num = like_num;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }
}
