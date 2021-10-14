package com.todo.assignmenttodofx;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class CategoryDialog extends Dialog<Category> {

    public CategoryDialog() {
        super();
        setTitle("Create Category");
        setHeaderText("Create a new Category");
        ObservableList<ButtonType> buttons = getDialogPane().getButtonTypes();
        buttons.clear();
        buttons.addAll(ButtonType.FINISH, ButtonType.CANCEL);
        App.addStyleSheet(getDialogPane().getScene());

        TextField nameField = new TextField();
        nameField.setPromptText("Label");
        ColorPicker colorPicker = new ColorPicker(Color.DARKMAGENTA);
        Label invalidLabel = new Label("");
        VBox nameBox = new VBox(nameField, invalidLabel);
        HBox content = new HBox(nameBox, colorPicker);
        Node finish = getDialogPane().lookupButton(ButtonType.FINISH);
        finish.addEventFilter(ActionEvent.ACTION, event -> {
            String text = nameField.getText();
            boolean isBlank = text.isBlank();
            boolean hasComma = text.contains(",");
            if (isBlank || hasComma) {
                nameField.getStyleClass().add("invalid");
                if (isBlank)
                    invalidLabel.setText("Cannot be blank");
                else
                    invalidLabel.setText("Cannot have commas");
                event.consume();
            }
        });
        getDialogPane().setContent(content);
        setResultConverter(button -> {
            if (button.equals(ButtonType.FINISH)) {
                Color color = colorPicker.getValue();
                String name = nameField.getText();
                Category category = new Category(name);
                category.setColor(color);
                return category;
            }
            return null;
        });
    }
}
