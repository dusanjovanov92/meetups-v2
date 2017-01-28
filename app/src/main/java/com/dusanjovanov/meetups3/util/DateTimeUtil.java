package com.dusanjovanov.meetups3.util;

import android.content.Context;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by duca on 12/1/2017.
 */

public class DateTimeUtil {

    public static String getMeetingDateTime(long timestamp,Context context){
        String format = null;
        if(System.currentTimeMillis()>timestamp*1000){
            format = "Počeo %s u %s";
        }
        else{
            format = "Počinje %s u %s";
        }

        Date date = new Date(timestamp*1000);

        DateFormat dfDate = android.text.format.DateFormat.getMediumDateFormat(context);
        DateFormat dfTime = android.text.format.DateFormat.getTimeFormat(context);
        return String.format(Locale.getDefault(), format, dfDate.format(date), dfTime.format(date));
    }

    public static String getChatDateTime(long timestamp,Context context){
        Date date = new Date(timestamp);

        DateFormat dfDate = android.text.format.DateFormat.getMediumDateFormat(context);
        DateFormat dfTime = android.text.format.DateFormat.getTimeFormat(context);
        return String.format(Locale.getDefault(), "%s u %s", dfDate.format(date), dfTime.format(date));
    }

}
