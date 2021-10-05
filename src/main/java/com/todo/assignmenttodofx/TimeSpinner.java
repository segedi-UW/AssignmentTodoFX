package com.todo.assignmenttodofx;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Calendar;

public class TimeSpinner extends HBox implements InvalidationListener {

    private final Spinner<Integer> hours;
    private final Spinner<Integer> minutes;
    private final StringProperty top;
    private final StringProperty bottom;
    private Converter converter;

    public TimeSpinner() {
        this(23, 59);
    }

    public TimeSpinner(int initHour, int initMin) {
        super();
        hours = createSpinner(23, initHour);
        minutes = createSpinner(59, initMin);
        top = new SimpleStringProperty();
        bottom = new SimpleStringProperty();
        setConverter(null);
        setContent();
    }

    private Spinner<Integer> createSpinner(int max, int initial) {
        final int min = 0;
        IntegerSpinnerValueFactory factory = new IntegerSpinnerValueFactory(min, max, initial);
        factory.setWrapAround(true);
        Spinner<Integer> spinner = new Spinner<>(factory);
        spinner.setEditable(true);
        spinner.setMaxWidth(60);
        return spinner;
    }

    private void setContent() {
        Label military = new Label();
        military.textProperty().bind(top);
        Label standard = new Label();
        standard.textProperty().bind(bottom);
        hours.valueProperty().addListener(this);
        minutes.valueProperty().addListener(this);
        invalidated(null);
        getChildren().addAll(hours, minutes, new VBox(military, standard));
    }

    public Spinner<Integer> getHours() {
        return hours;
    }

    public Spinner<Integer> getMinutes() {
        return minutes;
    }

    public int getHour() {
        return hours.getValue();
    }

    public int getMinute() {
        return minutes.getValue();
    }

    public Calendar getCalendar() {
        Calendar instance = Calendar.getInstance();
        CalendarSetter.setTimeTo(instance, hours.getValue(), minutes.getValue());
        return instance;
    }

    public void setTime(int hour, int minute) {
        this.hours.getValueFactory().setValue(hour);
        this.minutes.getValueFactory().setValue(minute);
    }

    public void setConverter(Converter converter) {
        if (converter == null) {
            setConverter((top, bottom) -> {
                int hour = this.hours.getValue();
                int minute = this.minutes.getValue();
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                top.set("   " + CalendarPrinter.getTimeString(calendar, true));
                bottom.set("   " + CalendarPrinter.getTimeString(calendar, false));
            });
        } else {
            this.converter = converter;
        }
    }

    public interface Converter {
        void convert(StringProperty top, StringProperty bottom);
    }

    @Override
    public void invalidated(Observable value) {
        converter.convert(top, bottom);
    }
}
