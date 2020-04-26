package com.mibai.phonelive.purse.entry;

public class VipPayEntry {

    /**
     * id : 1
     * title : 月度会员
     * money : 50
     * descn : 30天无限观看
     * old_money : 55
     */

    private String id;
    private String title;
    private String money;
    private String descn;
    private String old_money;

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

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getDescn() {
        return descn;
    }

    public void setDescn(String descn) {
        this.descn = descn;
    }

    public String getOld_money() {
        return old_money;
    }

    public void setOld_money(String old_money) {
        this.old_money = old_money;
    }
}
