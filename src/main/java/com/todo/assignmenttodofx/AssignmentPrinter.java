package com.todo.assignmenttodofx;

public class AssignmentPrinter {

    public static String getDisplayText(Assignment assignment, Display display) {
        String text;
        String date = CalendarPrinter.getDateString(assignment.getCalendar());
        String time = CalendarPrinter.getTimeString(assignment.getCalendar());
        String summary = assignment.getSummary();
        switch (display) {
            case FULL:
                text = "" + summary + " due on " + date + " at " + time;
                break;
            case SUMMARY:
                text = "" + summary;
                break;
            case SUMMARY_DATE:
                text = "" + summary + " due on " + date;
                break;
            case SUMMARY_TIME:
                text = "" + summary + " due at " + time;
                break;
            default:
                text = "Error in display type";
                break;
        }
        return text;
    }
}
