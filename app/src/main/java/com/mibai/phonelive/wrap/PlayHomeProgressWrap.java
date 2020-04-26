package com.mibai.phonelive.wrap;

import android.app.Activity;
import android.content.Context;

// evenBus
public class PlayHomeProgressWrap {

    public final int type;

    public final int progressAll;

    private PlayHomeProgressWrap(int type, int progressAll) {
        this.type = type;
        this.progressAll = progressAll;
    }

    public static PlayHomeProgressWrap getInstance(int type, int progressAll) {
        return new PlayHomeProgressWrap(type, progressAll);
    }
}
