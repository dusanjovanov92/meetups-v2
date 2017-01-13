package com.dusanjovanov.meetups3.util;

import android.content.Context;

import com.dusanjovanov.meetups3.R;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by duca on 12/1/2017.
 */

public class DateTimeUtil {
    private static final Calendar cal = Calendar.getInstance();
    private Context context;
    public static final int FAR = 0;
    public static final int THIS_WEEK = 1;
    public static final int TOMORROW = 2;
    public static final int TODAY_AFTER = 3;
    public static final int THIS_HOUR_AFTER = 4;
    public static final int THIS_HOUR_BEFORE = -1;
    public static final int TODAY_BEFORE = -2;
    public static final int YESTERDAY = -3;
    public static final int LAST_WEEK = -4;
    public static final int BEFORE = -5;

    public DateTimeUtil(Context context) {
        this.context = context;
    }

    public String getMeetingTime(long timestamp) {
        cal.setTimeInMillis(timestamp * 1000);

        Date date = cal.getTime();
        DateFormat dfDate = android.text.format.DateFormat.getMediumDateFormat(context);
        DateFormat dfTime = android.text.format.DateFormat.getTimeFormat(context);

        int proximity = getProximity(timestamp* 1000);
        TimeDifference timeDiff=  TimeDifference.getTimeDifference(timestamp * 1000);

        switch (proximity){
            case FAR:
                return dfDate.format(date)+" u "+dfTime.format(date);
            case THIS_WEEK:
                return String.format(Locale.getDefault(), "%s u %s", getDayOfWeek(cal.get(Calendar.DAY_OF_WEEK)),dfTime.format(date));
            case TOMORROW:
                return String.format(Locale.getDefault(), "Sutra u %s", dfTime.format(date));
            case TODAY_AFTER:
                return String.format(Locale.getDefault(), "Danas u %s", dfTime.format(date));
            case THIS_HOUR_AFTER:
                return String.format(Locale.getDefault(), "Za %d minuta", timeDiff.getMinutes());
            case THIS_HOUR_BEFORE:
                return String.format(Locale.getDefault(), "U toku,počeo pre %d minuta", timeDiff.getMinutes());
            case TODAY_BEFORE:
                return String.format(Locale.getDefault(), "U toku,počeo danas u %s (pre %d sati)", dfTime.format(date),timeDiff.getHours());
            case YESTERDAY:
                int days = timeDiff.getDays();
                int hours = timeDiff.getHours();
                if(days>0){
                    hours+=24;
                }
                return String.format(Locale.getDefault(),"U toku,počeo juče u %s (pre %d sati)",dfTime.format(date),hours);
            case LAST_WEEK:
                return String.format(Locale.getDefault(),"U toku,počeo %s u %s (pre %d dana i %d sati)",dfDate.format(date),
                        dfTime.format(date), timeDiff.getDays(), timeDiff.getHours());
            case BEFORE:
                return String.format(Locale.getDefault(),"U toku,počeo %s u %s (pre %d dana i %sati)",
                        dfDate.format(date),dfTime.format(date),timeDiff.getDays(),timeDiff.getHours());
            default:
                return dfDate.format(date)+" u "+dfTime.format(date);
        }
    }

    public String getChatTime(long timestamp){
        cal.setTimeInMillis(timestamp);

        Date date = cal.getTime();
        DateFormat dfDate = android.text.format.DateFormat.getMediumDateFormat(context);
        DateFormat dfTime = android.text.format.DateFormat.getTimeFormat(context);

        int proximity = getProximity(timestamp);
        TimeDifference timeDiff=  TimeDifference.getTimeDifference(timestamp);

        switch (proximity){
            case BEFORE:
                return String.format(Locale.getDefault(),"%s u %s",dfDate.format(date),dfTime.format(date));
            case LAST_WEEK:
                return String.format(Locale.getDefault(),"%s u %s",getDayOfWeek(cal.get(Calendar.DAY_OF_WEEK)),dfTime.format(date));
            case YESTERDAY:
                return String.format(Locale.getDefault(),"Juče u %s",dfTime.format(date));
            case TODAY_BEFORE:
                return String.format(Locale.getDefault(),"%s",dfTime.format(date));
            case THIS_HOUR_BEFORE:
                return String.format(Locale.getDefault(),"Pre %d minuta",timeDiff.getMinutes());
            default:
                return String.format(Locale.getDefault(),"%s u %s",dfDate.format(date),dfTime.format(date));
        }
    }

    private static class TimeDifference{
        private int days;
        private int hours;
        private int minutes;
        private boolean inProgress = false;

        static TimeDifference getTimeDifference(long timestamp){
            return new TimeDifference(timestamp);
        }

        private TimeDifference(long timestamp){
            long currentTime = System.currentTimeMillis();

            if(timestamp<=currentTime){
                inProgress = true;
            }

            long diff = Math.abs(timestamp - currentTime);

            days = (int) (diff / 1000 / 60 / 60 / 24);
            hours = (int) ((diff - days * 1000 * 60 * 60 * 24) / 1000 / 60 / 60);
            minutes = (int) ((diff - hours * 1000 * 60 * 60) / 1000 / 60);
        }

        int getDays() {
            return days;
        }

        int getHours() {
            return hours;
        }

        int getMinutes() {
            return minutes;
        }

        boolean isInProgress() {
            return inProgress;
        }
    }

    public static int getProximity(long timestamp){
        TimeDifference timeDiff = TimeDifference.getTimeDifference(timestamp);

        int eventDay = cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY ? 7 : cal.get(Calendar.DAY_OF_WEEK)-1;
        int eventHour = cal.get(Calendar.HOUR_OF_DAY);
        int eventMinutes = cal.get(Calendar.MINUTE);

        int days = timeDiff.getDays();
        int hours = timeDiff.getHours();
        int minutes = timeDiff.getMinutes();

        Calendar calCurrent= Calendar.getInstance();
        int currentDay = calCurrent.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY ? 7 : cal.get(Calendar.DAY_OF_WEEK)-1;
        int currentHour = calCurrent.get(Calendar.HOUR_OF_DAY);

        if(timeDiff.isInProgress()){
            if(eventDay>currentDay){
                return BEFORE;
            }
            else if(days>1){
                return LAST_WEEK;
            }
            else if((days==1 && eventHour<=hours) || (days==0 && hours>currentHour)){
                return YESTERDAY;
            }
            else if(hours>=1){
                return TODAY_BEFORE;
            }
            else{
                return THIS_HOUR_BEFORE;
            }
        }
        else if(days>=7-currentDay && hours>24-eventHour){
            return FAR;
        }
        else if(days>1){
            return THIS_WEEK;
        }
        else if((days==1 && hours<=24-eventHour) || (days==0 && hours>24-eventHour)){
            return TOMORROW;
        }
        else if(hours>=1 && hours<=24-eventHour){
            return TODAY_AFTER;
        }
        else{
            return THIS_HOUR_AFTER;
        }
    }

    private String getDayOfWeek(int dayOfWeek){
        String[] daysOfWeek = context.getResources().getStringArray(R.array.days_of_week);

        if(dayOfWeek == 1){
            return daysOfWeek[6];
        }
        return daysOfWeek[dayOfWeek-2];
    }
}
