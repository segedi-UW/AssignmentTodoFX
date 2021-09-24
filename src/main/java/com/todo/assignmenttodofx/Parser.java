package com.todo.assignmenttodofx;

import java.util.Calendar;
import java.util.Locale;
import java.util.regex.PatternSyntaxException;

public class Parser {

    public static Calendar parseStandardCalendar(String date, String time) throws InvalidFormatException {
        Calendar dateCalendar = parseDate(date);
        Calendar timeCalendar = parseStandardTime(time);
        CalendarSetter.setTime(dateCalendar, timeCalendar);
        return dateCalendar;
    }

    public static Calendar parseDate(String date) throws InvalidFormatException {
        try {
            String[] dateArr = date.split("/");
            if (dateArr.length != 2 && dateArr.length != 3)
                throw new InvalidFormatException(
                        "Dates can only have 2 or 3 elements. This had " + dateArr.length + " elements");
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateArr[1]));
            calendar.set(Calendar.MONTH, Integer.parseInt(dateArr[0]) - 1);
            // (dateArr[0]) - 1 :: change from month number to month index in Calendar
            int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            if (calendar.get(Calendar.DAY_OF_MONTH) > maxDays)
                throw new InvalidFormatException("The month of "
                        + calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
                        + " only has " + maxDays + " days for " + calendar.get(Calendar.YEAR));
            return calendar;
        } catch (PatternSyntaxException e) {
            throw new InvalidFormatException("Date was not separated by \"/\"");
        } catch (NumberFormatException e) {
            throw new InvalidFormatException("Part of the date was not a number");
        }
    }

    public static Calendar parseStandardTime(String time) throws InvalidFormatException {
        try {
            String[] timeParts = time.split(" ");
            return parseStandardHelper(timeParts[0], timeParts[1]);
        } catch (PatternSyntaxException e) {
            throw new InvalidFormatException(
                    "Either did not include a space between time and pm/ am or did not include a colon in time");
        } catch (IndexOutOfBoundsException e) {
            throw new InvalidFormatException(
                    "Did not have enough parts to the time. Requires both the time and meridian");
        }
    }

    private static Calendar parseStandardHelper(String time, String meridian) throws InvalidFormatException {
        try {
            Calendar calendar = Calendar.getInstance();
            checkTimeFormat(new String[] {time, meridian});
            String[] parts = time.split(":");
            int hour = Integer.parseInt(parts[0]);
            calendar.set(Calendar.HOUR, hour == 12 ? 0 : hour);
            calendar.set(Calendar.MINUTE, Integer.parseInt(parts[1]));
            calendar.set(Calendar.AM_PM, (meridian.equalsIgnoreCase("am") ? Calendar.AM : Calendar.PM));
            return calendar;
        } catch (PatternSyntaxException e) {
            throw new InvalidFormatException(
                    "Either did not include a space between time and pm/ am or did not include a colon in time");
        } catch (NumberFormatException e) {
            throw new InvalidFormatException(
                    "A part of the time that was expected to be a number was not");
        }
    }

    private static void checkTimeFormat(String[] timeParts) throws InvalidFormatException {
        if (timeParts.length != 2)
            throw new InvalidFormatException(
                    "Should have a continuous time, a space, and then pm or am (ex: \"12:25 pm\")");
        if (!timeParts[1].equalsIgnoreCase("am") && !timeParts[1].equalsIgnoreCase("pm"))
            throw new InvalidFormatException("need to include am or pm (case insensitive)");
        String[] times = timeParts[0].split(":");
        if (times.length != 2)
            throw new InvalidFormatException(
                    "Improper number of elements associated with colons. Expected 2, found: "
                            + times.length);
        int hr = Integer.parseInt(times[0]);
        int min = Integer.parseInt(times[1]);
        if (hr < 1 || hr > 12)
            throw new InvalidFormatException("The hour needs to be between 1 - 12");
        if (min < 0 || min > 59)
            throw new InvalidFormatException("The minute needs to be between 1 - 59");
    }
}

