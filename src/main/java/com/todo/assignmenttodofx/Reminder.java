package com.todo.assignmenttodofx;

import javafx.application.Platform;
import javafx.scene.media.AudioClip;

import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedList;
import java.util.TimerTask;

public class Reminder {

    private static AudioClip clip;
    public final Calendar date;
    public final TimerTask task;
    private final LinkedList<Collection<Reminder>> lists = new LinkedList<>();

    public Reminder(Calendar date, String text) {
        this.date = (Calendar) date.clone();
        task = new TimerTask() {

            @Override
            public void run() {
                Notification notification = new Notification(Notification.Type.INFORMATIONAL, text);
                notification.setHideAfterSeconds(5);
                Platform.runLater(() -> {
                    notification.show();
                    play();
                    cancel();
                });
            }
        };
    }

    public void addTo(Collection<Reminder> reminders) {
        reminders.add(this);
        lists.add(reminders);
    }

    public void cancel() {
        for (Collection<Reminder> c : lists) {
            c.remove(this);
        }
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
        return "Reminding on:\n" + date + "\n" + time ;
    }

}

