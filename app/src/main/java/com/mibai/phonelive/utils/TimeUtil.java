package com.mibai.phonelive.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {

    public static String convertTimestamp2Date(Long timestamp, String pattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(new Date(timestamp * 1000));
    }
}
