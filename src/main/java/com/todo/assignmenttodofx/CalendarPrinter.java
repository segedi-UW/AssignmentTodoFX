package com.todo.assignmenttodofx;

import java.util.Calendar;
import java.util.Locale;

public class CalendarPrinter {

    public static String getDateString(Calendar calendar) {
        int month = calendar.get(Calendar.MONTH) + 1;
        String monthStr = month >= 10 ? "" + month : "0" + month;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String dayStr = day >= 10 ? "" + day : "0" + day;
        return monthStr + "/" + dayStr + "/" + calendar.get(Calendar.YEAR);
    }

    public static String getTimeString(Calendar calendar) {
        boolean isMilitary = Preference.MILITARY.getBoolean();
        return getTimeString(calendar, isMilitary);
    }

    public static String getTimeString(Calendar calendar, boolean isMilitary) {
        int minInt = calendar.get(Calendar.MINUTE);
        int hourInt = isMilitary ? calendar.get(Calendar.HOUR_OF_DAY) : (calendar.get(Calendar.HOUR) == 0 ? 12 : calendar.get(Calendar.HOUR));
        String min = minInt < 10 ? "0" + minInt : "" + minInt;
        String hour = hourInt == 0 ? "12" : (hourInt < 10 ? "0" + hourInt : "" + hourInt);
        return "" + hour + (isMilitary ? "" : ":") + min + " "
                + (isMilitary ? "" : calendar.getDisplayName(Calendar.AM_PM, Calendar.SHORT, Locale.getDefault()));
    }

}
