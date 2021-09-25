package com.todo.assignmenttodofx;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar.ButtonData;

public class UpdateAlert extends Alert {

    public UpdateAlert() {
        super(AlertType.CONFIRMATION);
        initOwner(App.getStage());
        setTitle("Update Available");
        setHeaderText("Update AssignmentTodo");
        setContentText("An update is available for AssignmentTodo.");
        getButtonTypes().clear();
        ButtonType update = new ButtonType("Update And Restart", ButtonData.OK_DONE);
        getButtonTypes().addAll(update, ButtonType.CANCEL);
    }

}
