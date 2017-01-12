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
    public static final int THIS_DAY = 2;
    public static final int THIS_HOUR = 3;
    public static final int IN_PROGRESS = 4;

    public DateTimeUtil(Context context) {
        this.context = context;
    }

    public String getTime(long timestamp) {
        cal.setTimeInMillis(timestamp * 1000);

        Date date = cal.getTime();
        DateFormat dfDate = android.text.format.DateFormat.getMediumDateFormat(context);
        DateFormat dfTime = android.text.format.DateFormat.getTimeFormat(context);

        int proximity = getProximity(timestamp);
        TimeDifference timeDiff=  TimeDifference.getTimeDifference(timestamp);

        switch (proximity){
            case FAR:
                return dfDate.format(date)+" "+dfTime.format(date);
            case THIS_WEEK:
                return String.format(Locale.getDefault(), "%s u %s", getDayOfWeek(cal.get(Calendar.DAY_OF_WEEK)),dfTime.format(date));
            case THIS_DAY:
                return String.format(Locale.getDefault(), "U %s", dfTime.format(date));
            case THIS_HOUR:
                return String.format(Locale.getDefault(), "Za %d minuta", timeDiff.getMinutes());
            case IN_PROGRESS:
                return "U toku";
            default:
                return dfDate.format(date)+" "+dfTime.format(date);
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
            cal.setTimeInMillis(timestamp * 1000);

            long currentTime = System.currentTimeMillis();

            if(cal.getTimeInMillis()<=currentTime){
                inProgress = true;
            }

            long diff = Math.abs(cal.getTimeInMillis() - currentTime);

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

        int meetingHour = cal.get(Calendar.HOUR_OF_DAY);
        int meetingMinutes = cal.get(Calendar.MINUTE);

        int days = timeDiff.getDays();
        int hours = timeDiff.getHours();
        int minutes = timeDiff.getMinutes();

        if(timeDiff.isInProgress()){
            return IN_PROGRESS;
        }
        else if (days >= 7) {
            return FAR;
        }
        else if(days >= 1 || (hours >= 1 && hours>meetingHour)){
            return THIS_WEEK;
        }
        else if(hours >= 1 || (hours<1 && minutes>meetingMinutes)){
            return THIS_DAY;
        }
        else{
            return THIS_HOUR;
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
