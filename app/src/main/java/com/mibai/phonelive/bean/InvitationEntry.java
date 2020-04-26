package com.mibai.phonelive.bean;

public class InvitationEntry {

    /**
     * id : 1
     * uid : 11617
     * touid : 298430
     * coin : 0
     * addtime : 1584800780
     * userinfo : {"id":"298430","user_login":"13127158259","user_pass":"###69ab35392243263c399550cf918f191b","user_nicename":"手机用户8259","user_email":"","user_url":"","avatar":"/default.jpg","avatar_thumb":"/default_thumb.jpg","sex":"2","age":"0","birthday":"","signature":"这家伙很懒，什么都没留下","last_login_ip":"27.211.65.180","last_login_time":"2020-03-21 22:26:06","create_time":"2020-02-14 11:35:57","vip_endtime":"0","free_times":"30","last_viewtime":"1584720000","front_freetimes":"0","user_activation_key":"","user_status":"1","score":"0","user_type":"2","coin":"3","mocoin":"0","mobile":"13127158259","token":"592d262e11e0b2a50e49db4de87fac13","expiretime":"1610720766","weixin":"","consumption":"0","votes":"0","votestotal":"0","province":"","city":"","area":"","isrecommend":"0","openid":"","login_type":"phone","iszombie":"0","isrecord":"0","iszombiep":"0","issuper":"0","ishot":"1","isauth":"0","bonus_day":"0","bonus_time":"0","goodnum":"0","code":"Y5R5H5","divide_family":"0","praise":"1","praisetotal":"1","money":"0.00","device":"867622021063881"}
     */

    private String id;
    private String uid;
    private String touid;
    private String coin;
    private String addtime;
    private UserinfoBean userinfo;

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

    public String getTouid() {
        return touid;
    }

    public void setTouid(String touid) {
        this.touid = touid;
    }

    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    public UserinfoBean getUserinfo() {
        return userinfo;
    }

    public void setUserinfo(UserinfoBean userinfo) {
        this.userinfo = userinfo;
    }

    public static class UserinfoBean {
        /**
         * id : 298430
         * user_login : 13127158259
         * user_pass : ###69ab35392243263c399550cf918f191b
         * user_nicename : 手机用户8259
         * user_email :
         * user_url :
         * avatar : /default.jpg
         * avatar_thumb : /default_thumb.jpg
         * sex : 2
         * age : 0
         * birthday :
         * signature : 这家伙很懒，什么都没留下
         * last_login_ip : 27.211.65.180
         * last_login_time : 2020-03-21 22:26:06
         * create_time : 2020-02-14 11:35:57
         * vip_endtime : 0
         * free_times : 30
         * last_viewtime : 1584720000
         * front_freetimes : 0
         * user_activation_key :
         * user_status : 1
         * score : 0
         * user_type : 2
         * coin : 3
         * mocoin : 0
         * mobile : 13127158259
         * token : 592d262e11e0b2a50e49db4de87fac13
         * expiretime : 1610720766
         * weixin :
         * consumption : 0
         * votes : 0
         * votestotal : 0
         * province :
         * city :
         * area :
         * isrecommend : 0
         * openid :
         * login_type : phone
         * iszombie : 0
         * isrecord : 0
         * iszombiep : 0
         * issuper : 0
         * ishot : 1
         * isauth : 0
         * bonus_day : 0
         * bonus_time : 0
         * goodnum : 0
         * code : Y5R5H5
         * divide_family : 0
         * praise : 1
         * praisetotal : 1
         * money : 0.00
         * device : 867622021063881
         */

        private String id;
        private String user_login;
        private String user_pass;
        private String user_nicename;
        private String user_email;
        private String user_url;
        private String avatar;
        private String avatar_thumb;
        private String sex;
        private String age;
        private String birthday;
        private String signature;
        private String last_login_ip;
        private String last_login_time;
        private String create_time;
        private String vip_endtime;
        private String free_times;
        private String last_viewtime;
        private String front_freetimes;
        private String user_activation_key;
        private String user_status;
        private String score;
        private String user_type;
        private String coin;
        private String mocoin;
        private String mobile;
        private String token;
        private String expiretime;
        private String weixin;
        private String consumption;
        private String votes;
        private String votestotal;
        private String province;
        private String city;
        private String area;
        private String isrecommend;
        private String openid;
        private String login_type;
        private String iszombie;
        private String isrecord;
        private String iszombiep;
        private String issuper;
        private String ishot;
        private String isauth;
        private String bonus_day;
        private String bonus_time;
        private String goodnum;
        private String code;
        private String divide_family;
        private String praise;
        private String praisetotal;
        private String money;
        private String device;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUser_login() {
            return user_login;
        }

        public void setUser_login(String user_login) {
            this.user_login = user_login;
        }

        public String getUser_pass() {
            return user_pass;
        }

        public void setUser_pass(String user_pass) {
            this.user_pass = user_pass;
        }

        public String getUser_nicename() {
            return user_nicename;
        }

        public void setUser_nicename(String user_nicename) {
            this.user_nicename = user_nicename;
        }

        public String getUser_email() {
            return user_email;
        }

        public void setUser_email(String user_email) {
            this.user_email = user_email;
        }

        public String getUser_url() {
            return user_url;
        }

        public void setUser_url(String user_url) {
            this.user_url = user_url;
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

        public String getBirthday() {
            return birthday;
        }

        public void setBirthday(String birthday) {
            this.birthday = birthday;
        }

        public String getSignature() {
            return signature;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }

        public String getLast_login_ip() {
            return last_login_ip;
        }

        public void setLast_login_ip(String last_login_ip) {
            this.last_login_ip = last_login_ip;
        }

        public String getLast_login_time() {
            return last_login_time;
        }

        public void setLast_login_time(String last_login_time) {
            this.last_login_time = last_login_time;
        }

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
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

        public String getUser_activation_key() {
            return user_activation_key;
        }

        public void setUser_activation_key(String user_activation_key) {
            this.user_activation_key = user_activation_key;
        }

        public String getUser_status() {
            return user_status;
        }

        public void setUser_status(String user_status) {
            this.user_status = user_status;
        }

        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
        }

        public String getUser_type() {
            return user_type;
        }

        public void setUser_type(String user_type) {
            this.user_type = user_type;
        }

        public String getCoin() {
            return coin;
        }

        public void setCoin(String coin) {
            this.coin = coin;
        }

        public String getMocoin() {
            return mocoin;
        }

        public void setMocoin(String mocoin) {
            this.mocoin = mocoin;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getExpiretime() {
            return expiretime;
        }

        public void setExpiretime(String expiretime) {
            this.expiretime = expiretime;
        }

        public String getWeixin() {
            return weixin;
        }

        public void setWeixin(String weixin) {
            this.weixin = weixin;
        }

        public String getConsumption() {
            return consumption;
        }

        public void setConsumption(String consumption) {
            this.consumption = consumption;
        }

        public String getVotes() {
            return votes;
        }

        public void setVotes(String votes) {
            this.votes = votes;
        }

        public String getVotestotal() {
            return votestotal;
        }

        public void setVotestotal(String votestotal) {
            this.votestotal = votestotal;
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

        public String getIsrecommend() {
            return isrecommend;
        }

        public void setIsrecommend(String isrecommend) {
            this.isrecommend = isrecommend;
        }

        public String getOpenid() {
            return openid;
        }

        public void setOpenid(String openid) {
            this.openid = openid;
        }

        public String getLogin_type() {
            return login_type;
        }

        public void setLogin_type(String login_type) {
            this.login_type = login_type;
        }

        public String getIszombie() {
            return iszombie;
        }

        public void setIszombie(String iszombie) {
            this.iszombie = iszombie;
        }

        public String getIsrecord() {
            return isrecord;
        }

        public void setIsrecord(String isrecord) {
            this.isrecord = isrecord;
        }

        public String getIszombiep() {
            return iszombiep;
        }

        public void setIszombiep(String iszombiep) {
            this.iszombiep = iszombiep;
        }

        public String getIssuper() {
            return issuper;
        }

        public void setIssuper(String issuper) {
            this.issuper = issuper;
        }

        public String getIshot() {
            return ishot;
        }

        public void setIshot(String ishot) {
            this.ishot = ishot;
        }

        public String getIsauth() {
            return isauth;
        }

        public void setIsauth(String isauth) {
            this.isauth = isauth;
        }

        public String getBonus_day() {
            return bonus_day;
        }

        public void setBonus_day(String bonus_day) {
            this.bonus_day = bonus_day;
        }

        public String getBonus_time() {
            return bonus_time;
        }

        public void setBonus_time(String bonus_time) {
            this.bonus_time = bonus_time;
        }

        public String getGoodnum() {
            return goodnum;
        }

        public void setGoodnum(String goodnum) {
            this.goodnum = goodnum;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getDivide_family() {
            return divide_family;
        }

        public void setDivide_family(String divide_family) {
            this.divide_family = divide_family;
        }

        public String getPraise() {
            return praise;
        }

        public void setPraise(String praise) {
            this.praise = praise;
        }

        public String getPraisetotal() {
            return praisetotal;
        }

        public void setPraisetotal(String praisetotal) {
            this.praisetotal = praisetotal;
        }

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }

        public String getDevice() {
            return device;
        }

        public void setDevice(String device) {
            this.device = device;
        }
    }
}
