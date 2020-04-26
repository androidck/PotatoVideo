package com.mibai.phonelive.bean;

import java.util.List;

public class InvitationAllEntry {

   private String zongji;

   private List<InvitationEntry> info;

    public String getZongji() {
        return zongji;
    }

    public void setZongji(String zongji) {
        this.zongji = zongji;
    }

    public List<InvitationEntry> getInfo() {
        return info;
    }

    public void setInfo(List<InvitationEntry> info) {
        this.info = info;
    }
}
