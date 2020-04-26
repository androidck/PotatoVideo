package com.mibai.phonelive.wrap;

// evenBus
public class PlayViewProgressWrap {

    public final PlayProgressEntry entry;

    private PlayViewProgressWrap(PlayProgressEntry entry) {
        this.entry = entry;
    }

    public static PlayViewProgressWrap getInstance(PlayProgressEntry entry) {
        return new PlayViewProgressWrap(entry);
    }
}
