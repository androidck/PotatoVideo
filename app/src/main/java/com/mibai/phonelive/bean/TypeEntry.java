package com.mibai.phonelive.bean;

// 视频分类列表
public class TypeEntry {

    /**
     * id : 1
     * title : 电影
     * display : 1
     * image : https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1581159379240&di=332505c418d11cebaaa49416bfef9dc6&imgtype=0&src=http://vdposter.bdstatic.com/10534d37c724f23032be1c2e2326195b.jpeg
     */

    private String id;
    private String title;
    private String display;
    private String image;

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

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
