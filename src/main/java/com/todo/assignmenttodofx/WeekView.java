package com.todo.assignmenttodofx;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.controlsfx.control.CheckListView;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class WeekView {

    private final DayView[] days = new DayView[5];
    private final CheckListView<Assignment> master;
    private final Calendar start;

    public WeekView(HBox hbox, Controller controller) {
        this.master = controller.getMaster();
        start = Calendar.getInstance();
        setTime0(start);
        setDays();
        Updater updater = Updater.dailyUpdater(start, () -> {
            for (DayView day : days) {
                Platform.runLater(() -> {
                    day.start.add(Calendar.DAY_OF_MONTH, 1);
                    day.setDay(day.start);
                    day.refresh();
                });
            }
        });
        updater.start();
        hbox.getChildren().addAll(days);
    }

    private void setDays() {
        Calendar start = (Calendar) this.start.clone(); // do not alter the actual start day

        for (int i = 0; i < days.length; i++) {
            // Adds the actual WeekView
            DayView day = new DayView();
            master.getItems().addListener(day);
            day.view.setCellFactory(cell -> new AssignmentCell(Display.SUMMARY, master));

            day.view.focusedProperty().addListener((focus, hadFocus, isFocused) -> {
                if (isFocused && !hadFocus) {
                    SelectionModel<Assignment> focused = day.view.getSelectionModel();
                    SelectionModel<Assignment> primary = master.getSelectionModel();
                    Assignment selected = focused.getSelectedItem();
                    if (selected == null && !day.view.getItems().isEmpty())
                        selected = day.view.getItems().get(0);
                    primary.select(selected);
                }
            });
            day.view.getSelectionModel().selectedItemProperty().addListener((obs, selected, selection) -> {
                if (selection != null)
                    master.getSelectionModel().select(selection);
            });

            master.getSelectionModel().selectedItemProperty().addListener((obs, selected, selection) -> {
                if (day.view.getItems().contains(selection)) {
                    day.view.getSelectionModel().select(selection);
                } else {
                    day.view.getSelectionModel().clearSelection();
                }
            });
            day.setDay(start);
            start.add(Calendar.DAY_OF_MONTH, 1);
            days[i] = day;
        }
    }

    private void setTime0(Calendar calendar) {
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    public void refresh() {
        for (DayView day : days) {
            day.refresh();
        }
    }

    private class DayView extends VBox implements ListChangeListener<Assignment> {
        Calendar start, end;
        ListView<Assignment> view;
        private final SimpleStringProperty text;

        public DayView() {
            super();
            text = new SimpleStringProperty();
            view = new ListView<>();
            setFillWidth(true);
            ObservableList<Node> children = getChildren();
            Label label = new Label();
            label.setMaxWidth(Double.MAX_VALUE);
            label.setId("calendar-header");
            label.textProperty().bind(text);
            children.addAll(label, view);
        }

        public void refresh() {
            /* TODO Can optimize to stop checking after adding one then any one after is
             * not between since list is sorted
             */
            view.getItems().clear();
            for (Assignment assignment : master.getItems()) {
                addIfBetween(assignment);
            }
        }

        public void setDay(Calendar start) {
            this.start = (Calendar) start.clone();
            end = (Calendar) start.clone();
            end.add(Calendar.DAY_OF_MONTH, 1);
            String dayLabel = start.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
            String dayOfMonth = "" + start.get(Calendar.DAY_OF_MONTH);
            String label = dayLabel + " " + dayOfMonth;
            text.set(label);
            view.setTooltip(new Tooltip(label));
        }

        @Override
        public void onChanged(Change<? extends Assignment> change) {
            if (change == null)
                return;
            while (change.next()) {
                List<? extends Assignment> toRemove = change.getRemoved();
                if (toRemove != null)
                    view.getItems().removeAll(toRemove);
                List<? extends Assignment> added = change.getAddedSubList();
                for (Assignment assignment : added) {
                    addIfBetween(assignment);
                }
            }
        }

        private void addIfBetween(Assignment assignment) {
            Calendar due = assignment.getCalendar();
            if ((start.before(due) && end.after(due))) {
                ObservableList<Assignment> assignments = view.getItems();
                assignments.add(assignment);
                assignments.sort(null);
            }
        }

    }

}
