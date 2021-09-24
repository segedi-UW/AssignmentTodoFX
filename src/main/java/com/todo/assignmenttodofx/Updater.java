package com.todo.assignmenttodofx;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class Updater {

    private static int masterSerial = 0;

    private final int serial;
    private Calendar current;
    private final Timer timer;
    private final Runnable action;
    private DateIncrementer incrementer;
    private boolean running;
    private boolean repeat;

    public interface DateIncrementer {
        Calendar setDate(Calendar current);
    }

    private Updater(Calendar start, Runnable action) {
        serial = nextSerial();
        this.action = action;
        timer = new Timer(true);
        current = (Calendar) start.clone();
    }

    public static Updater updater(Calendar start, Runnable action) {
        Updater updater = new Updater(start, action);
        updater.setDateIncrementer(current-> current); // does nothing
        return updater;
    }

    private static synchronized int nextSerial() {
        return masterSerial++;
    }

    /**
     * Takes the calendar instance and calculates when midnight occurs and runs the action then.
     * Repeats for each day after as long as the program is running.
     * @param startDay day to start on
     * @param action to execute
     * @return The updater pending a start() command
     */
    public static Updater dailyUpdater(Calendar startDay, Runnable action) {
        startDay = (Calendar) startDay.clone(); // do not affect the original instance
        startDay.set(Calendar.HOUR_OF_DAY, 0);
        startDay.set(Calendar.MINUTE, 0);
        startDay.set(Calendar.SECOND, 0);
        Updater updater = new Updater(startDay, action);
        updater.repeat = true;
        updater.setDateIncrementer(current -> {
            current.add(Calendar.DAY_OF_YEAR, 1);
            return current;
        });
        return updater;
    }

    public void setDateIncrementer(DateIncrementer incrementer) {
        if (running)
            throw new IllegalStateException("Cannot set DateCalculator after starting Updater");
        this.incrementer = incrementer;
    }

    public Calendar getCalendar() {
        return (Calendar) current.clone();
    }

    public void start() {
        if (running)
            throw new IllegalStateException("Can only start an Updater once");
        running = true;
        update();
    }

    public void cancel() {
        running = false;
        System.out.println("Canceled: " + this);
        timer.cancel();
    }

    private void update() {
        current = incrementer.setDate(current);
        if (current == null) {
            return;
        }
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                System.out.println("Executing updater: " + this);
                action.run();
                if (repeat)
                    update();
            }

        }, current.getTime());
        System.out.println("Started: " + this);
    }

    @Override
    public String toString() {
        String time = current != null ? "" + current.getTime() : "";
        String running = this.running ? "running at " + time : "dormant";
        return "Updater #" + serial + ": " + running;
    }
}
