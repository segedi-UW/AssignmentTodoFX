package com.todo.assignmenttodofx;

import javafx.scene.control.Alert;

public class ErrorAlert extends Alert {

    public ErrorAlert(Exception e, String message) {
        super(AlertType.ERROR);
        Alert error = new Alert(AlertType.ERROR);
        error.setHeaderText(e != null ? e.getMessage() : "Non-Exception Error");
        error.initOwner(App.getStage());
        error.setContentText(message);
    }
}
