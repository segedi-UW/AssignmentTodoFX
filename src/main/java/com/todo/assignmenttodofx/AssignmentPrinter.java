package com.todo.assignmenttodofx;

public class AssignmentPrinter {

    public static String getDisplayText(Assignment assignment, Display display) {
        String text;
        switch (display) {
            case FULL:
                text = assignment.toString();
                break;
            case SUMMARY:
                text = "" + assignment.getSummary();
                break;
            case SUMMARY_DATE:
                text = "" + assignment.getSummary() + " on " + CalendarPrinter.getDateString(assignment.getCalendar());
                break;
            case SUMMARY_TIME:
                text = "" + assignment.getSummary() + " at "
                        + CalendarPrinter.getTimeString(assignment.getCalendar());
                break;
            default:
                text = "Error in display type";
                break;
        }
        return text;
    }
}
