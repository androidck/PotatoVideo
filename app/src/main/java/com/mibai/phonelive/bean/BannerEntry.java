package com.mibai.phonelive.bean;

public class BannerEntry {


    /**
     * id : 1
     * title : 测试1
     * pic : https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1582394461148&di=0f0274d9bbf5e86b8596c9091471594c&imgtype=0&src=http%3A%2F%2Fimg.zcool.cn%2Fcommunity%2F010588583c063da801219c77b14fe8.jpg
     * url : http://www.baidu.com
     * addtime : null
     */

    private String id;
    private String title;
    private String pic;
    private String url;
    private Object addtime;

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

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Object getAddtime() {
        return addtime;
    }

    public void setAddtime(Object addtime) {
        this.addtime = addtime;
    }
}
