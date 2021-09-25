package com.todo.assignmenttodofx;

import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;

public class ErrorAlert extends Alert {

    public ErrorAlert(Exception e, String message, String log) {
        super(AlertType.ERROR);
        Alert error = new Alert(AlertType.ERROR);
        error.setHeaderText(e != null ? e.getMessage() : "Non-Exception Error");
        error.initOwner(App.getStage());
        error.setContentText(message);
        if (log != null) {
            TextArea area = new TextArea(log);
            area.setWrapText(true);
            error.getDialogPane().setExpandableContent(area);
        }
    }
}
