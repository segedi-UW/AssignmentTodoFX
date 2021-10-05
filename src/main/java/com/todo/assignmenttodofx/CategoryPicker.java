package com.todo.assignmenttodofx;

import javafx.collections.ObservableMap;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;

public class CategoryPicker extends ComboBox<Category> {

    public CategoryPicker() {
        super();
        setWidth(60);
        setCellFactory(view -> new Cell());
        setButtonCell(new Cell());
        ObservableMap<String, Category> categories = Category.getCategories();
        Category standard = categories.get("standard");
        assert (standard != null);
        getItems().addAll(categories.values());
        getSelectionModel().select(standard);
    }

    private static class Cell extends ListCell<Category> {
        @Override
        protected void updateItem(Category category, boolean isEmpty) {
            super.updateItem(category, isEmpty);
            if (category != null && !isEmpty) {
                setText(category.getName());
                setGraphic(ColorSampler.getSample(category.getColor()));
            } else {
                setText("");
                setGraphic(null);
            }
        }
    }
}
