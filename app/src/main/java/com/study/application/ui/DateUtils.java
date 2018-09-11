package com.study.application.ui;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    public static boolean isDate2Bigger(String str1, String str2) {
        Log.i("Owen","isDate2Bigger - Thread ID = " + Thread.currentThread().getId());
        boolean isBigger = false;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date dt1 = null;
        Date dt2 = null;
        try {
            dt1 = sdf.parse(str1);
            dt2 = sdf.parse(str2);
            Log.e("Owen dt1:"+dt1.getTime(),"dt2:"+dt2.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (dt1.getTime() > dt2.getTime()) {
            isBigger = false;
        } else if (dt1.getTime() <= dt2.getTime()) {
            isBigger = true;
        }
        return isBigger;
    }
}

