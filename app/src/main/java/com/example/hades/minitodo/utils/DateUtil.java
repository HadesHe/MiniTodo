package com.example.hades.minitodo.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Hades on 2017/5/3.
 */
public class DateUtil {
    public static String formatDate(String formatString, Date date) {
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat(formatString);
        return simpleDateFormat.format(date);
    }
}
