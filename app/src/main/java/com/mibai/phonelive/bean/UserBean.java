package com.mibai.phonelive.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by cxf on 2017/8/14.
 */

public class UserBean implements Parcelable {
    private String id;
    private String user_nicename;
    private String avatar;
    private String avatar_thumb;
    private int sex;
    private String signature;
    private int coin;
    private String province;
    private String city;
    private String area;
    private String birthday;
    private int follows;
    private int fans;
    private String age;
    private String praise;
    private int workVideos;//作品数量
    private int likeVideos;//喜欢别人的视频数量
    private int buyVideos;//喜欢别人的视频数量
    private String addtime;//拉黑的时间，黑名单用
    private String vip_endtime;// 会员到期时间
    private String free_times;
    private String last_viewtime;
    private String front_freetimes;
    private String isvip;// 是否是会员
    private String sharecont;
    private String login_type;
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getLogin_type() {
        return login_type;
    }

    public void setLogin_type(String login_type) {
        this.login_type = login_type;
    }

    public String getSharecont() {
        return sharecont;
    }

    public void setSharecont(String sharecont) {
        this.sharecont = sharecont;
    }

    public String getIsvip() {
        return isvip;
    }

    public void setIsvip(String isvip) {
        this.isvip = isvip;
    }

    public String getVip_endtime() {
        return vip_endtime;
    }

    public void setVip_endtime(String vip_endtime) {
        this.vip_endtime = vip_endtime;
    }

    public String getFree_times() {
        return free_times;
    }

    public void setFree_times(String free_times) {
        this.free_times = free_times;
    }

    public String getLast_viewtime() {
        return last_viewtime;
    }

    public void setLast_viewtime(String last_viewtime) {
        this.last_viewtime = last_viewtime;
    }

    public String getFront_freetimes() {
        return front_freetimes;
    }

    public void setFront_freetimes(String front_freetimes) {
        this.front_freetimes = front_freetimes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_nicename() {
        return user_nicename;
    }

    public void setUser_nicename(String user_nicename) {
        this.user_nicename = user_nicename;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAvatar_thumb() {
        return avatar_thumb;
    }

    public void setAvatar_thumb(String avatar_thumb) {
        this.avatar_thumb = avatar_thumb;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }


    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }


    public int getFollows() {
        return follows;
    }

    public void setFollows(int follows) {
        this.follows = follows;
    }

    public int getFans() {
        return fans;
    }

    public void setFans(int fans) {
        this.fans = fans;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getPraise() {
        return praise;
    }

    public void setPraise(String praise) {
        this.praise = praise;
    }

    public int getWorkVideos() {
        return workVideos;
    }

    public void setWorkVideos(int workVideos) {
        this.workVideos = workVideos;
    }

    public int getLikeVideos() {
        return likeVideos;
    }

    public int getBuyVideos() {
        return buyVideos;
    }

    public void setBuyVideos(int buyVideos) {
        this.buyVideos = buyVideos;
    }

    public void setLikeVideos(int likeVideos) {
        this.likeVideos = likeVideos;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    public UserBean() {
    }

    public UserBean(Parcel in) {
        this.id = in.readString();
        this.user_nicename = in.readString();
        this.avatar = in.readString();
        this.avatar_thumb = in.readString();
        this.sex = in.readInt();
        this.signature = in.readString();
        this.coin = in.readInt();
        this.province = in.readString();
        this.city = in.readString();
        this.area = in.readString();
        this.birthday = in.readString();
        this.follows = in.readInt();
        this.fans = in.readInt();
        this.age = in.readString();
        this.praise = in.readString();
        this.workVideos = in.readInt();
        this.likeVideos = in.readInt();
        this.buyVideos = in.readInt();
        this.addtime = in.readString();
        this.login_type=in.readString();
        this.token=in.readString();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.user_nicename);
        dest.writeString(this.avatar);
        dest.writeString(this.avatar_thumb);
        dest.writeInt(this.sex);
        dest.writeString(this.signature);
        dest.writeInt(this.coin);
        dest.writeString(this.province);
        dest.writeString(this.city);
        dest.writeString(this.area);
        dest.writeString(this.birthday);
        dest.writeInt(this.follows);
        dest.writeInt(this.fans);
        dest.writeString(this.age);
        dest.writeString(this.praise);
        dest.writeInt(this.workVideos);
        dest.writeInt(this.likeVideos);
        dest.writeInt(this.buyVideos);
        dest.writeString(this.addtime);
        dest.writeString(this.login_type);
        dest.writeString(this.token);
    }

    public static final Creator<UserBean> CREATOR = new Creator<UserBean>() {
        @Override
        public UserBean[] newArray(int size) {
            return new UserBean[size];
        }

        @Override
        public UserBean createFromParcel(Parcel in) {
            return new UserBean(in);
        }
    };


}
