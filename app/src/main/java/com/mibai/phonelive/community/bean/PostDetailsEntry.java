package com.mibai.phonelive.community.bean;

public class PostDetailsEntry {

    /**
     * id : 13
     * title : lz容嬷嬷哦
     * content : lz容嬷嬷哦
     * img : http://14.192.8.68/test/images/1587029476qxlarge-dsc-1664C3E7BE72E79A7D0B0D13C08AA667.png
     * video : 0
     * uid : 4
     * localtion : 济南市
     * comments_num : 0
     * collection_num : 0
     * like_num : 0
     * view_num : 0
     * label : 厕刷
     * status : 1
     * recommend : 0
     * addtime : 1587029476
     * type : 0
     * userinfo : {"user_nicename":"已删除"}
     */

    private String id;
    private String title;
    private String content;
    private String img;
    private String video;
    private String uid;
    private String localtion;
    private String comments_num;
    private String collection_num;
    private String like_num;
    private String view_num;
    private String label;
    private String status;
    private String recommend;
    private String addtime;
    private String type;
    private CircleChildUserInfo userinfo;
    /**
     * isgz : 0
     * isdz : 0
     */

    private int isgz;
    private int isdz;


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

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getLocaltion() {
        return localtion;
    }

    public void setLocaltion(String localtion) {
        this.localtion = localtion;
    }

    public String getComments_num() {
        return comments_num;
    }

    public void setComments_num(String comments_num) {
        this.comments_num = comments_num;
    }

    public String getCollection_num() {
        return collection_num;
    }

    public void setCollection_num(String collection_num) {
        this.collection_num = collection_num;
    }

    public String getLike_num() {
        return like_num;
    }

    public void setLike_num(String like_num) {
        this.like_num = like_num;
    }

    public String getView_num() {
        return view_num;
    }

    public void setView_num(String view_num) {
        this.view_num = view_num;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRecommend() {
        return recommend;
    }

    public void setRecommend(String recommend) {
        this.recommend = recommend;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public int getIsgz() {
        return isgz;
    }

    public void setIsgz(int isgz) {
        this.isgz = isgz;
    }

    public int getIsdz() {
        return isdz;
    }

    public void setIsdz(int isdz) {
        this.isdz = isdz;
    }
}
