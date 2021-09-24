package com.todo.assignmenttodofx;

import java.time.LocalDate;
import java.util.Calendar;

public class CalendarSetter {

    public static void setDate(Calendar calendar, Calendar date) {
        final int day = Calendar.DAY_OF_MONTH;
        final int month = Calendar.MONTH;
        final int year = Calendar.YEAR;
        calendar.set(day, date.get(day));
        calendar.set(month, date.get(month));
        calendar.set(year, date.get(year));
    }

    public static void setTime(Calendar calendar, Calendar time) {
        final int hour = Calendar.HOUR_OF_DAY;
        final int minute = Calendar.MINUTE;
        setTimeTo(calendar, time.get(hour), time.get(minute));
    }

    public static Calendar fromLocalDate(LocalDate date) {
        Calendar calendar = Calendar.getInstance();
        final int day = Calendar.DAY_OF_MONTH;
        final int month = Calendar.MONTH;
        final int year = Calendar.YEAR;
        calendar.set(day, date.getDayOfMonth());
        calendar.set(month, date.getMonthValue() - 1); // accounts for the shift from value to index
        calendar.set(year, date.getYear());
        return calendar;
    }

    public static void setTimeTo(Calendar calendar, Integer hours, Integer minutes) {
        final int hour = Calendar.HOUR_OF_DAY;
        final int minute = Calendar.MINUTE;
        final int second = Calendar.SECOND;
        calendar.set(hour, hours);
        calendar.set(minute, minutes);
        calendar.set(second, 0);
    }
}