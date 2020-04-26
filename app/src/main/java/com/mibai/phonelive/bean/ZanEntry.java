package com.mibai.phonelive.bean;

// 赞 实体类
public class ZanEntry {

    /**
     * id : 112
     * uid : 0
     * title : 古娜扎坐莲
     * thumb : http://qiniu.dookou.com/20200321_5e74fe21673e3.jpeg
     * thumb_s : http://qiniu.dookou.com/20200321_5e74fe21673e3.jpeg
     * href : http://qiniu.dookou.com/20200321_5e74fe29dfffd.mp4
     * likes : 5
     * views : 407
     * comments : 0
     * steps : 0
     * shares : 0
     * addtime : 1584725545
     * lat :
     * lng :
     * city :
     * isdel : 0
     * is_urge : 0
     * urge_nums : 0
     * urge_money : 0
     * big_urgenums : 0
     * status : 1
     * music_id : 0
     * xiajia_reason :
     * show_val : 100
     * nopass_time : 0
     * watch_ok : 12
     * cate :
     * subcate : 0
     * ad_id : 0
     * money : 0
     * userinfo : {"user_nicename":"系统管理员","avatar":"http://lipin128.com/default.jpg","avatar_thumb":"http://lipin128.com/default_thumb.jpg","id":"0","coin":"0","sex":"1","signature":"","province":"","city":"城市未填写","birthday":"","praise":"0","fans":"0","follows":"0","workVideos":"0","likeVideos":"0","age":"年龄未填写"}
     * datetime : 13小时前
     * islike : 0
     * isstep : 0
     * isattent : 0
     * musicinfo : {"id":"0","title":"","author":"","img_url":"","length":"00:00","file_url":"","use_nums":"0","music_format":"@系统管理员创作的原声"}
     */

    private String id;
    private String uid;
    private String title;
    private String thumb;
    private String thumb_s;
    private String href;
    private String likes;
    private String views;
    private String comments;
    private String steps;
    private String shares;
    private String addtime;
    private String lat;
    private String lng;
    private String city;
    private String isdel;
    private String is_urge;
    private String urge_nums;
    private String urge_money;
    private String big_urgenums;
    private String status;
    private String music_id;
    private String xiajia_reason;
    private String show_val;
    private String nopass_time;
    private String watch_ok;
    private String cate;
    private String subcate;
    private String ad_id;
    private String money;
    private UserinfoBean userinfo;
    private String datetime;
    private int islike;
    private int isstep;
    private int isattent;
    private MusicinfoBean musicinfo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getThumb_s() {
        return thumb_s;
    }

    public void setThumb_s(String thumb_s) {
        this.thumb_s = thumb_s;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public String getShares() {
        return shares;
    }

    public void setShares(String shares) {
        this.shares = shares;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getIsdel() {
        return isdel;
    }

    public void setIsdel(String isdel) {
        this.isdel = isdel;
    }

    public String getIs_urge() {
        return is_urge;
    }

    public void setIs_urge(String is_urge) {
        this.is_urge = is_urge;
    }

    public String getUrge_nums() {
        return urge_nums;
    }

    public void setUrge_nums(String urge_nums) {
        this.urge_nums = urge_nums;
    }

    public String getUrge_money() {
        return urge_money;
    }

    public void setUrge_money(String urge_money) {
        this.urge_money = urge_money;
    }

    public String getBig_urgenums() {
        return big_urgenums;
    }

    public void setBig_urgenums(String big_urgenums) {
        this.big_urgenums = big_urgenums;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMusic_id() {
        return music_id;
    }

    public void setMusic_id(String music_id) {
        this.music_id = music_id;
    }

    public String getXiajia_reason() {
        return xiajia_reason;
    }

    public void setXiajia_reason(String xiajia_reason) {
        this.xiajia_reason = xiajia_reason;
    }

    public String getShow_val() {
        return show_val;
    }

    public void setShow_val(String show_val) {
        this.show_val = show_val;
    }

    public String getNopass_time() {
        return nopass_time;
    }

    public void setNopass_time(String nopass_time) {
        this.nopass_time = nopass_time;
    }

    public String getWatch_ok() {
        return watch_ok;
    }

    public void setWatch_ok(String watch_ok) {
        this.watch_ok = watch_ok;
    }

    public String getCate() {
        return cate;
    }

    public void setCate(String cate) {
        this.cate = cate;
    }

    public String getSubcate() {
        return subcate;
    }

    public void setSubcate(String subcate) {
        this.subcate = subcate;
    }

    public String getAd_id() {
        return ad_id;
    }

    public void setAd_id(String ad_id) {
        this.ad_id = ad_id;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public UserinfoBean getUserinfo() {
        return userinfo;
    }

    public void setUserinfo(UserinfoBean userinfo) {
        this.userinfo = userinfo;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public int getIslike() {
        return islike;
    }

    public void setIslike(int islike) {
        this.islike = islike;
    }

    public int getIsstep() {
        return isstep;
    }

    public void setIsstep(int isstep) {
        this.isstep = isstep;
    }

    public int getIsattent() {
        return isattent;
    }

    public void setIsattent(int isattent) {
        this.isattent = isattent;
    }

    public MusicinfoBean getMusicinfo() {
        return musicinfo;
    }

    public void setMusicinfo(MusicinfoBean musicinfo) {
        this.musicinfo = musicinfo;
    }

    public static class UserinfoBean {
        /**
         * user_nicename : 系统管理员
         * avatar : http://lipin128.com/default.jpg
         * avatar_thumb : http://lipin128.com/default_thumb.jpg
         * id : 0
         * coin : 0
         * sex : 1
         * signature :
         * province :
         * city : 城市未填写
         * birthday :
         * praise : 0
         * fans : 0
         * follows : 0
         * workVideos : 0
         * likeVideos : 0
         * age : 年龄未填写
         */

        private String user_nicename;
        private String avatar;
        private String avatar_thumb;
        private String id;
        private String coin;
        private String sex;
        private String signature;
        private String province;
        private String city;
        private String birthday;
        private String praise;
        private String fans;
        private String follows;
        private String workVideos;
        private String likeVideos;
        private String age;

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

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCoin() {
            return coin;
        }

        public void setCoin(String coin) {
            this.coin = coin;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public String getSignature() {
            return signature;
        }

        public void setSignature(String signature) {
            this.signature = signature;
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

        public String getBirthday() {
            return birthday;
        }

        public void setBirthday(String birthday) {
            this.birthday = birthday;
        }

        public String getPraise() {
            return praise;
        }

        public void setPraise(String praise) {
            this.praise = praise;
        }

        public String getFans() {
            return fans;
        }

        public void setFans(String fans) {
            this.fans = fans;
        }

        public String getFollows() {
            return follows;
        }

        public void setFollows(String follows) {
            this.follows = follows;
        }

        public String getWorkVideos() {
            return workVideos;
        }

        public void setWorkVideos(String workVideos) {
            this.workVideos = workVideos;
        }

        public String getLikeVideos() {
            return likeVideos;
        }

        public void setLikeVideos(String likeVideos) {
            this.likeVideos = likeVideos;
        }

        public String getAge() {
            return age;
        }

        public void setAge(String age) {
            this.age = age;
        }
    }

    public static class MusicinfoBean {
        /**
         * id : 0
         * title :
         * author :
         * img_url :
         * length : 00:00
         * file_url :
         * use_nums : 0
         * music_format : @系统管理员创作的原声
         */

        private String id;
        private String title;
        private String author;
        private String img_url;
        private String length;
        private String file_url;
        private String use_nums;
        private String music_format;

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

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getImg_url() {
            return img_url;
        }

        public void setImg_url(String img_url) {
            this.img_url = img_url;
        }

        public String getLength() {
            return length;
        }

        public void setLength(String length) {
            this.length = length;
        }

        public String getFile_url() {
            return file_url;
        }

        public void setFile_url(String file_url) {
            this.file_url = file_url;
        }

        public String getUse_nums() {
            return use_nums;
        }

        public void setUse_nums(String use_nums) {
            this.use_nums = use_nums;
        }

        public String getMusic_format() {
            return music_format;
        }

        public void setMusic_format(String music_format) {
            this.music_format = music_format;
        }
    }
}
