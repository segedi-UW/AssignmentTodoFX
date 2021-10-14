package com.todo.assignmenttodofx;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class ExitSaveAlert extends Alert {
    public ExitSaveAlert() {
        super(AlertType.CONFIRMATION);
        initOwner(App.getStage());
        App.addStyleSheet(getDialogPane().getScene());
        setHeaderText("Save before exiting?");
        setContentText("Would you like to save before exiting?");
        setTitle("Save and Exit?");
        getButtonTypes().clear();
        getButtonTypes().addAll(ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
    }
}
