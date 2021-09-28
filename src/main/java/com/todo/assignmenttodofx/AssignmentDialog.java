package com.todo.assignmenttodofx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Calendar;

public class AssignmentDialog extends Dialog<Assignment> {

    private final Assignment toEdit;
    @FXML private TextArea description;
    @FXML private TextField summary;
    @FXML private DatePicker datePicker;
    @FXML private TimeSpinner time;
    @FXML private Button removeCategory;
    @FXML private CategoryPicker categoryPicker;
    @FXML private TextField link;
    @FXML private ReminderPicker reminder;

    public AssignmentDialog() {
        this(null);
    }

    public AssignmentDialog(Assignment toEdit) {
        super();
        this.toEdit = toEdit;
        setContent();
    }

    @FXML
    private void initialize() {
        datePicker.setValue(LocalDate.now());
        removeCategory.setDisable(true);
        categoryPicker.setValue(Category.getCategories().get(Category.STANDARD));
        categoryPicker.getSelectionModel().selectedItemProperty().addListener((arg0, selected, selection) -> {
            boolean isRemovable = selection != null && !selection.isUser();
            removeCategory.setDisable(isRemovable);
        });
    }

    private void setContent() {

        String title = (toEdit == null ? "Add" : "Edit") + " Assignment";
        setTitle(title);
        setHeaderText("To create an assignment fill in the summary, date, and time. Other fields are optional.");
        DialogPane pane = getDialogPane();
        App.addStyleSheet(pane.getScene());
        pane.getButtonTypes().addAll(ButtonType.FINISH, ButtonType.CANCEL);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("assignment-dialog.fxml"));
            loader.setController(this);
            pane.setContent(loader.load());
        } catch (IOException e) {
            System.out.println("Could not load AssignmentDialog content: " + e.getMessage());
        }

        Node finish = pane.lookupButton(ButtonType.FINISH);
        finish.addEventFilter(ActionEvent.ACTION, event -> {
            boolean isInvalid = false;
            if (summary.getText().isBlank()) {
                isInvalid = true;
                summary.getStyleClass().add("invalid");
            }
            if (datePicker.getValue() == null) {
                isInvalid = true;
                datePicker.getStyleClass().add("invalid");
            }

            if (isInvalid)
                event.consume();
        });

        setResultConverter(button -> {
            if (!button.equals(ButtonType.FINISH))
                return null;
            LocalDate day = datePicker.getValue();
            Calendar due = CalendarSetter.fromLocalDate(day);
            due.set(Calendar.MILLISECOND, 0);
            due.set(Calendar.HOUR_OF_DAY, time.getHour());
            due.set(Calendar.MINUTE, time.getMinute());
            Assignment assignment = new Assignment(summary.getText(), due, null, toEdit == null);
            String message = description.getText();
            if (message != null && !message.isBlank())
                assignment.setDescription(message);
            String toLink = link.getText();
            if (toLink != null && !toLink.isBlank())
                assignment.setLink(toLink);
            if (reminder.isSelected()) {
                new Reminder(due, assignment.toString(), assignment.getReminders());
            }
            assignment.setCategory(categoryPicker.getValue());
            return assignment;
        });

        if (toEdit != null) {
            // autofill in the boxes
            summary.setText(toEdit.getSummary());
            Calendar day = (Calendar) toEdit.getCalendar().clone();
            time.setTime(day.get(Calendar.HOUR_OF_DAY), day.get(Calendar.MINUTE));
            int year = day.get(Calendar.YEAR);
            int month = day.get(Calendar.MONTH) + 1; // revalue from Calendar to LocalDate
            int dayOfMonth = day.get(Calendar.DAY_OF_MONTH);
            LocalDate date = LocalDate.of(year, month, dayOfMonth);
            datePicker.setValue(date);
            link.setText(toEdit.getLink());
        }
    }

    @FXML
    private void addCategory() {
        System.out.println("Adding Categories");
        CategoryDialog dialog = new CategoryDialog();
        dialog.showAndWait().ifPresent(category -> {
            Category.getCategories().put(category.getName(), category);
            categoryPicker.getItems().add(category);
            categoryPicker.getSelectionModel().select(category);
        });
    }

    @FXML
    private void removeCategory() {
        Category category = categoryPicker.getSelectionModel().getSelectedItem();
        if (category.isUser()) {
            Category.getCategories().remove(category.getName());
            categoryPicker.getItems().remove(category);
        }
    }
}
