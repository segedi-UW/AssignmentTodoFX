package com.todo.assignmenttodofx;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class SaveAlert extends Alert {

    public SaveAlert() {
        super(AlertType.CONFIRMATION);
        initOwner(App.getStage());
        setHeaderText("Save Current File?");
        setContentText("Would you like to save your current assignments?");
        getButtonTypes().clear();
        getButtonTypes().addAll(ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
    }
}
