package com.mibai.phonelive.community.network;

import retrofit2.http.PUT;

// 链接 地址
public class HttpContract {

    public static final String BASE_HOST = "http://14.192.8.68";

    public static final String BASE_PROJECT = "/api/public/?service=";

    public static final String BASE_URL = BASE_HOST + BASE_PROJECT;

    // 关注列表 参数 uid
    public static final String FOLLOW_LIST = BASE_URL + "Forum.getConlist";

    // 最新 参数 p
    public static final String NEW_LIST = BASE_URL + "Forum.getForumlist";

    // 收藏 参数 uid tid
    public static final String COLLECTION = BASE_URL + "Forum.Collection_post";

    // 点赞 参数 uid tid
    public static final String LIKE = BASE_URL + "Forum.Like_post";

    // 获取评论 uid  tid cid
    public static final String GET_COLLECTION = BASE_URL + "Forum.Like_post";

    // 发布评论 / 回复评论 uid  tid cid content like_num
    public static final String SEND_OR_COMMENT = BASE_URL + "Forum.setpinl";

    // 举报 参数：uid cid
    public static final String JU_BAO = BASE_URL + "Forum.setjb";

    // 发布贴子 参数：多图上传 视频 uid title img localtion label content setjb uid cid
    public static final String SEND_POST = BASE_URL + "Forum.setlist";

    // 最热 参数 p
    public static final String HOT_POST = BASE_URL + "Forum.getHotlist";

    // 精选 参数 p
    public static final String JIN_POST = BASE_URL + "Forum.getRecommend";

    //多图上传
    public static final String ForumUpimgURL = BASE_HOST + "/upimg.php";

    // 标签
    public static final String ForumLabeURL = BASE_URL + "Forum.label";

    // 详情
    public static final String ForumDetailstzURL = BASE_URL + "Forum.detailstz";

    //关注
    public static final String ForumSetgzURL = BASE_URL + "Forum.setgz";

    //收藏贴子
    public static final String Collection_postURL = BASE_URL + "Forum.Collection_post";

    //我的 我发布的
    public static final String myForumlistURL = BASE_URL + "Forum.myForumlist";

    //发表评论
    public static final String ForumSetpinlURL = BASE_URL + "Forum.setpinl";

    //评论
    public static final String ForumGetpinlURL = BASE_URL + "Forum.getpinl";
}
