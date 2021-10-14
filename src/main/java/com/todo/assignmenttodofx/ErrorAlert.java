package com.todo.assignmenttodofx;

import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;

public class ErrorAlert extends Alert {

    public ErrorAlert(Exception e, String message, String log) {
        super(AlertType.ERROR);
        App.addStyleSheet(getDialogPane().getScene());
        setHeaderText(e != null ? "Exception Message: " + e.getMessage() : "Non-Exception Error");
        initOwner(App.getStage());
        setContentText(message);
        if (log != null) {
            TextArea area = new TextArea(log);
            area.setWrapText(true);
            getDialogPane().setExpandableContent(area);
        }
    }
}
