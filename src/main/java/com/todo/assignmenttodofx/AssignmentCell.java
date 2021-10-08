package com.todo.assignmenttodofx;


import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.controlsfx.control.CheckListView;

public class AssignmentCell extends CheckBoxListCell<Assignment> implements ChangeListener<Assignment> {

    private final Display display;
    private final CheckListView<Assignment> listView;
    private BooleanProperty checked;
    private ChangeListener<Boolean> checkListener;

    private enum Style {
        STANDARD("standard-due"), DUE_SOON("due-soon"), OVERDUE("overdue"), DONE("done");

        final String style;

        Style(String style) {
            this.style = style;
        }

        @Override
        public String toString() {
            return style;
        }
    }

    public AssignmentCell(Display type, CheckListView<Assignment> listView) {
        super(listView::getItemBooleanProperty);
        this.display = type;
        this.listView = listView;
        itemProperty().addListener(this);
    }

    @Override
    public void changed(ObservableValue<? extends Assignment> observable, Assignment oldValue, Assignment newValue) {
        if (newValue != null) {
            if (checkListener != null)
                checked.removeListener(checkListener);
            checked = listView.getItemBooleanProperty(newValue);
            checkListener = checkListener();
            checked.addListener(checkListener);
        }
    }

    @Override
    public void updateItem(Assignment assignment, boolean isEmpty) {
        super.updateItem(assignment, isEmpty);
        updateStyle(assignment, isEmpty);
        if (assignment != null && !isEmpty) {
            double alpha = isSelected() ? 0.2 : 0.1;
            Category category = assignment.getCategory();
            Color fill = category.getAlphaOf(alpha);
            Color borderFill = assignment.getCategory().getColor();
            setUnderline(isSelected());
            setBackground(new Background(new BackgroundFill(fill, CornerRadii.EMPTY, Insets.EMPTY)));
            setBorder(new Border(
                    new BorderStroke(borderFill, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2.0))));
        } else {
            setText("");
            setBorder(null);
            setBackground(null);
        }
    }

    private void updateStyle(Assignment assignment, boolean isEmpty) {
        ObservableList<String> style = getStyleClass();
        style.removeIf(name -> name.contains("due") || name.contains(Style.DONE.style)); // This relies on the css elements containing "due" in the name
        if (assignment != null && !isEmpty) {
            setText(AssignmentPrinter.getDisplayText(assignment, display));
            String current;
            switch (assignment.getType()) {
                case OVERDUE:
                    current = Style.OVERDUE.style;
                    break;
                case SOON:
                    current = Style.DUE_SOON.style;
                    break;
                case STANDARD:
                    current = Style.STANDARD.style;
                    break;
                default:
                    throw new IllegalStateException("No case in AssignmentCell for " + assignment.getType());
            }

            String doneStyle = Style.DONE.style;
            style.add(current);
            if (checked.get())
                style.add(doneStyle);
        }
    }

    private ChangeListener<Boolean> checkListener() {
        return ((obs, wasChecked, isChecked) -> {
            ObservableList<String> style = getStyleClass();
            String doneStyle = Style.DONE.style;
            if (isChecked)
                style.add(doneStyle);
            else
                style.remove(doneStyle);
        });
    }

    /* TODO Add functionality for user choice in display
    just change the display from final to not
    public void setDisplay(Display display) {
        this.display = display;
    }
     */
}
