package com.todo.assignmenttodofx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;

public class ReminderDialog extends  Dialog<Calendar> {

    private final Assignment toRemind;
    @FXML private TimeSpinner timeSpinner;
    @FXML private DatePicker datePicker;

    public ReminderDialog(Assignment toRemind) {
        this.toRemind = toRemind;
        String title = "Create Reminder";
        initOwner(App.getStage());
        setTitle(title);
        setHeaderText("Select the time and date for the reminder.");
        setContent();
    }

    @FXML
    private void initialize() {
        Calendar c = toRemind.getCalendar();
        LocalDate date = LocalDate.ofInstant(c.toInstant(), ZoneId.systemDefault());
        datePicker.setValue(date);
    }

    @SuppressWarnings("MagicConstant")
    public void setContent() {
        DialogPane pane = getDialogPane();
        App.addStyleSheet(pane.getScene());
        pane.getButtonTypes().addAll(ButtonType.FINISH, ButtonType.CANCEL);

        Calendar due = (Calendar)toRemind.getCalendar().clone();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("reminder-dialog.fxml"));
            loader.setController(this);
            pane.setContent(loader.load());
        } catch (IOException e) {
            System.err.println("Could not load fxml - ReminderDialog");
        }

        setResultConverter(button -> {
            if (!button.equals(ButtonType.FINISH))
                return null;
            due.set(Calendar.MILLISECOND, 0);
            due.set(Calendar.HOUR_OF_DAY, timeSpinner.getHour());
            due.set(Calendar.MINUTE, timeSpinner.getMinute());

            LocalDate day = datePicker.getValue();
            final int monthIndex = day.getMonthValue() - LocalDate.MIN.getMonthValue();
            due.set(day.getYear(), monthIndex, day.getDayOfMonth());
            return due;
        });
    }
}
