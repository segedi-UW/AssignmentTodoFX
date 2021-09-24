package com.todo.assignmenttodofx;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.Calendar;
import java.util.List;
import java.util.Timer;

public class Assignment implements Comparable<Assignment> {

    public static final int SOON_CUTOFF = 8;
    private static final Display display = Display.SUMMARY_TIME;

    private final boolean update;
    private String summary;
    private String description;
    private String link;
    private Updater updater;
    private Calendar updateTime;
    private Reminder autoReminder;
    private TaskType type;
    private Timer notifier;
    private ObservableList<Reminder> reminders;
    private Category category = Category.standard();

    private Calendar due;

    public void createAutoReminder() {
        if (autoReminder != null)
            autoReminder.cancel();
        autoReminder = new Reminder(updateTime, toString());
        autoReminder.addTo(reminders);
    }

    public void cancel() {
        updater.cancel();
    }

    public enum TaskType {
        STANDARD, SOON, OVERDUE
    }

    public Assignment(String summary, Calendar due, String link) {
        this(summary, due, link, true);
    }

    public Assignment(String summary, Calendar due, String link, boolean update) {
        this.update = update;
        initialize();
        this.summary = summary;
        this.link = link;
        description = "";
        setCalendar(due);
    }

    private void initialize() {
        notifier = new Timer(true);
        reminders = FXCollections.observableArrayList();
        reminders.addListener(new ListChangeListener<>() {
            @Override
            public void onChanged(Change<? extends Reminder> change) {
                while (change.next()) {
                    if (change.getRemovedSize() > 0) {
                        for (Reminder reminder : change.getRemoved()) {
                            reminder.task.cancel();
                        }
                    } else {
                        List<? extends Reminder> added = change.getAddedSubList();
                        for (Reminder reminder : added) {
                            schedule(reminder);
                        }
                    }
                }
            }

            private void schedule(Reminder reminder) {
                try {
                    notifier.schedule(reminder.task, reminder.date.getTime());
                } catch (IllegalArgumentException e) {
                    System.err.println("Error creating reminder: " + e.getMessage());
                }
            }
        });
    }

    public Category getCategory() {
        return category;
    }

    public void setCalendar(Calendar due) {
        this.due = due;
        setType();
        if (update) {
            Calendar updateTime = (Calendar)due.clone();
            updateTime.set(Calendar.SECOND, 0);
            setUpdate(updateTime);
        }
    }

    private void setUpdate(Calendar updateTime) {
        this.updateTime = updateTime;
        if (updater != null)
            updater.cancel();

        if (Preference.AUTO_REMIND.getBoolean())
            createAutoReminder();

        updater = Updater.updater(updateTime, this::setNextType);
        updater.setDateIncrementer(current -> {
            switch(type) {
                case OVERDUE:
                    // There is nothing after overdue
                    current = null;
                    break;
                case SOON:
                    current = (Calendar)due.clone();
                    break;
                case STANDARD:
                    current = (Calendar)due.clone();
                    current.add(Calendar.HOUR_OF_DAY, -SOON_CUTOFF);
                    break;
                default:
                    break;
            }
            return current;
        });
        updater.start();
    }

    private void setNextType() {
        switch (type) {
            case SOON:
                type = TaskType.OVERDUE;
                break;
            case STANDARD:
                type = TaskType.SOON;
                break;
            case OVERDUE:
            default:
        }
    }

    private void setType() {
        Calendar now = Calendar.getInstance();
        Calendar soon = Calendar.getInstance();
        soon.add(Calendar.HOUR_OF_DAY, SOON_CUTOFF);
        boolean isOverdue = due.before(now);
        boolean isDueSoon = due.before(soon);
        if (isOverdue)
            type = TaskType.OVERDUE;
        else if (isDueSoon)
            type = TaskType.SOON;
        else
            type = TaskType.STANDARD;
    }

    public void setAs(Assignment assignment) {
        setCalendar(assignment.getCalendar());
        setDescription(assignment.getDescription());
        setSummary(assignment.getSummary());
        setLink(assignment.getLink());
        category = assignment.getCategory();
    }

    public ObservableList<Reminder> getReminders() {
        return reminders;
    }

    public Reminder getAutoReminder() {
        return autoReminder;
    }

    public Calendar getCalendar() {
        return due;
    }

    public TaskType getType() {
        return type;
    }

    public String getDescription() {
        String text = description;
        return (text == null || text.isBlank()) ? "No Description." : text;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    @Override
    public String toString() {
        return AssignmentPrinter.getDisplayText(this, display);
    }

    @Override
    public int compareTo(Assignment o) {
        return due.compareTo(o.due);
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}