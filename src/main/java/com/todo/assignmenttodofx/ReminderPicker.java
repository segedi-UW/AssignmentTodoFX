package com.todo.assignmenttodofx;

import javafx.beans.property.BooleanProperty;
import javafx.scene.control.CheckBox;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.control.ToolBar;

public class ReminderPicker extends ToolBar {

    private final BooleanProperty isSelected;
    private final TimeSpinner time;

    public ReminderPicker() {
        super();
        time = new TimeSpinner(0,30);
        SpinnerValueFactory<Integer> hours = time.getHours().getValueFactory();
        hours.setWrapAround(false);
        SpinnerValueFactory<Integer> minutes = time.getMinutes().getValueFactory();
        minutes.setWrapAround(false);
        if (minutes instanceof IntegerSpinnerValueFactory) {
            ((IntegerSpinnerValueFactory) minutes).setAmountToStepBy(5);
        }
        time.setConverter((top, bottom) -> {
            String padding = "   ";
            int hour = hours.getValue();
            top.set(padding + hour + " hr" + (hour != 1 ? "s " : " ") + minutes.getValue() + " minutes");
            bottom.set(padding + "Prior to Due Time");
        });
        CheckBox remind = new CheckBox("Reminder");
        isSelected = remind.selectedProperty();
        setIntractable(remind);
        remind.setOnAction(event -> setIntractable(remind));
        getChildren().addAll(remind, time);
    }

    private void setIntractable(CheckBox remind) {
        boolean isUnchecked = !remind.isSelected();
        time.setDisable(isUnchecked);
    }

    public boolean isSelected() {
        return isSelected.get();
    }
}
