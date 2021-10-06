package com.todo.assignmenttodofx;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class AssignmentParser implements Filer.Savable<Assignment>, Filer.Parsable<Assignment> {

    private static final String END_SAVE = "END";

    @Override
    public List<Assignment> parse(Iterator<String> lines) {
        List<Assignment> list = new ArrayList<>();
        while (lines.hasNext()) {
            String summary = lines.next();
            String description = lineToDescription(lines.next());
            String date = lines.next();
            Category category = null;
            if (date.indexOf(',') >= 0) {
                String[] split = date.split(",");
                date = split[0];
                category = Category.getCategories().get(split[1]);
            }
            String time = lines.next();
            String link = lines.next();
            ArrayList<String> reminderDates = new ArrayList<>();
            ArrayList<String> reminderTimes = new ArrayList<>();
            String reminder = lines.next();
            while (!reminder.equals(END_SAVE)) {
                reminderDates.add(reminder);
                reminder = lines.next();
                reminderTimes.add(reminder);
                reminder = lines.next();
            }
            if (description.equals("null"))
                description = null;
            if (link.equals("null"))
                link = null;
            Calendar calendar = parseCalendar(date, time);
            Assignment assignment = new Assignment(summary, calendar, link);
            if (category != null)
                assignment.setCategory(category);
            if (description != null)
                assignment.setDescription(description);
            for (int i = 0; i < reminderDates.size(); i++) {
                Calendar remind = parseCalendar(reminderDates.get(i), reminderTimes.get(i));
                new Reminder(remind, assignment.toString(), assignment.getReminders());
            }
            list.add(assignment);
        }
        return list;
    }

    private Calendar parseCalendar(String date, String time) {
        try {
            // TODO One day may implement easier toParse method
            return Parser.parseStandardCalendar(date, time);
        } catch (InvalidFormatException e) {
            throw new IllegalStateException("Failed to parse date");
        }
    }

    @Override
    public void save(FileWriter writer, Collection<Assignment> assignments) throws IOException {
        StringBuilder builder = new StringBuilder();
        for (Assignment a : assignments) {
            // remove autoReminder
            Reminder autoReminder = a.getAutoReminder();
            if (autoReminder != null) autoReminder.cancel();
            // continue writing
            Calendar calendar = a.getCalendar();
            builder.append(a.getSummary());
            builder.append("\n");
            builder.append(descriptionToLine(a.getDescription()));
            builder.append("\n");
            builder.append(getDateLine(a));
            builder.append(CalendarPrinter.getTimeString(calendar, false)); // always writes to standard time
            builder.append("\n");
            builder.append(a.getLink());
            builder.append("\n");
            List<Reminder> reminders = a.getReminders();
            for (Reminder reminder : reminders) {
                Calendar remind = reminder.date;
                builder.append(CalendarPrinter.getDateString(remind));
                builder.append("\n");
                builder.append(CalendarPrinter.getTimeString(remind, false));
                builder.append("\n");
            }
            builder.append(END_SAVE);
            builder.append("\n");
        }
        writer.append(builder.toString());
    }

    private String descriptionToLine(String description) {
        return description.replaceAll("\n", "\\\\n");
    }

    private String lineToDescription(String line) {
        return line.replaceAll("\\\\n", "\n");
    }

    private static String getDateLine(Assignment a) {
        Calendar calendar = a.getCalendar();
        String date = CalendarPrinter.getDateString(calendar);
        Category cat = a.getCategory();
        String category = "," + cat.getName();
        return date + category + "\n";
    }

}
