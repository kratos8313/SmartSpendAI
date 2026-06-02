package com.smartspend.ai.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
    public static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
    public static final SimpleDateFormat MONTH_YEAR_FORMAT = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
    public static final SimpleDateFormat SHORT_DATE = new SimpleDateFormat("dd MMM", Locale.getDefault());

    public static long[] getCurrentMonthRange() {
        Calendar start = Calendar.getInstance();
        start.set(Calendar.DAY_OF_MONTH, 1);
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);

        Calendar end = Calendar.getInstance();
        end.set(Calendar.DAY_OF_MONTH, end.getActualMaximum(Calendar.DAY_OF_MONTH));
        end.set(Calendar.HOUR_OF_DAY, 23);
        end.set(Calendar.MINUTE, 59);
        end.set(Calendar.SECOND, 59);
        end.set(Calendar.MILLISECOND, 999);

        return new long[]{start.getTimeInMillis(), end.getTimeInMillis()};
    }

    public static long[] getLastMonthRange() {
        Calendar start = Calendar.getInstance();
        start.add(Calendar.MONTH, -1);
        start.set(Calendar.DAY_OF_MONTH, 1);
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);

        Calendar end = Calendar.getInstance();
        end.add(Calendar.MONTH, -1);
        end.set(Calendar.DAY_OF_MONTH, end.getActualMaximum(Calendar.DAY_OF_MONTH));
        end.set(Calendar.HOUR_OF_DAY, 23);
        end.set(Calendar.MINUTE, 59);
        end.set(Calendar.SECOND, 59);
        end.set(Calendar.MILLISECOND, 999);

        return new long[]{start.getTimeInMillis(), end.getTimeInMillis()};
    }

    public static long[] getWeekRange(int weeksAgo) {
        Calendar start = Calendar.getInstance();
        start.add(Calendar.WEEK_OF_YEAR, -weeksAgo);
        start.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);

        Calendar end = (Calendar) start.clone();
        end.add(Calendar.DAY_OF_WEEK, 6);
        end.set(Calendar.HOUR_OF_DAY, 23);
        end.set(Calendar.MINUTE, 59);
        end.set(Calendar.SECOND, 59);

        return new long[]{start.getTimeInMillis(), end.getTimeInMillis()};
    }

    public static String formatDate(long timestamp) {
        return DATE_FORMAT.format(new Date(timestamp));
    }

    public static String formatDateTime(long timestamp) {
        return DATE_TIME_FORMAT.format(new Date(timestamp));
    }

    public static String formatMonthYear(long timestamp) {
        return MONTH_YEAR_FORMAT.format(new Date(timestamp));
    }

    public static String formatShortDate(long timestamp) {
        return SHORT_DATE.format(new Date(timestamp));
    }

    public static boolean isToday(long timestamp) {
        Calendar today = Calendar.getInstance();
        Calendar check = Calendar.getInstance();
        check.setTimeInMillis(timestamp);
        return today.get(Calendar.YEAR) == check.get(Calendar.YEAR)
                && today.get(Calendar.DAY_OF_YEAR) == check.get(Calendar.DAY_OF_YEAR);
    }

    public static boolean isYesterday(long timestamp) {
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_YEAR, -1);
        Calendar check = Calendar.getInstance();
        check.setTimeInMillis(timestamp);
        return yesterday.get(Calendar.YEAR) == check.get(Calendar.YEAR)
                && yesterday.get(Calendar.DAY_OF_YEAR) == check.get(Calendar.DAY_OF_YEAR);
    }

    public static String getRelativeDate(long timestamp) {
        if (isToday(timestamp)) return "Today";
        if (isYesterday(timestamp)) return "Yesterday";
        return formatDate(timestamp);
    }
}
