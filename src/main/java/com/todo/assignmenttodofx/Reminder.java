package com.todo.assignmenttodofx;

import javafx.application.Platform;
import javafx.scene.media.AudioClip;

import java.util.Calendar;
import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;

public class Reminder {

    private static final Timer timer = new Timer(true);
    private static AudioClip clip;
    public final Calendar date;
    public final TimerTask task;
    private final Collection<Reminder> list;

    public Reminder(Calendar date, String text, Collection<Reminder> list) {
        this.date = (Calendar) date.clone();
        this.list = list;
        list.add(this);
        Reminder reminder = this;
        task = new TimerTask() {

            @Override
            public void run() {
                Notification notification = new Notification(Notification.Type.INFORMATIONAL, "Reminder", text);
                if (Preference.AUTO_HIDE_REMINDERS.getBoolean())
                    notification.setHideAfterSeconds(8);
                else
                    notification.setHideAfterSeconds(Notification.NO_AUTO_HIDE);
                Platform.runLater(() -> {
                    notification.show();
                    play();
                    reminder.cancel();
                });
            }
        };
        timer.schedule(task, date.getTime());
    }

    public void cancel() {
        list.remove(this);
        task.cancel();
    }

    public static void play() {
        if (clip == null) System.err.println("Clip is null - did not play sound");
        else if (!Preference.MUTE.getBoolean()) clip.play();
    }

    public static void setSound(AudioClip audioClip) {
        clip = audioClip;
    }

    @Override
    public String toString() {
        String time = CalendarPrinter.getTimeString(date);
        String date = CalendarPrinter.getDateString(this.date);
        return "Reminding on:\n" + date + "\n" + time;
    }

}

