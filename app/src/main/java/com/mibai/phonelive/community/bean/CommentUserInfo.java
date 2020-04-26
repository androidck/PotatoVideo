package com.mibai.phonelive.community.bean;

// 评论人用户信息
public class CommentUserInfo {
    /**
     * id : 9
     * nickname : 轮回的悲伤
     * image : /api/public/uploads/b1451b97228b5c6f06c4744122e692a1.png
     * sex : 1
     * age : 18
     * city : 开创
     * period : 1798970703
     * isvip : 1
     */

    private String id;
    private String nickname;
    private String image;
    private String sex;
    private String age;
    private String city;
    private String period;
    private int isvip;
    private int islove;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public int getIsvip() {
        return isvip;
    }

    public void setIsvip(int isvip) {
        this.isvip = isvip;
    }

    public int getIslove() {
        return islove;
    }

    public void setIslove(int islove) {
        this.islove = islove;
    }
}
