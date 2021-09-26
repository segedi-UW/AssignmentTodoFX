package com.todo.assignmenttodofx;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.TextArea;

public class UpdateAlert extends Alert {

    public UpdateAlert() {
        super(AlertType.CONFIRMATION);
        initOwner(App.getStage());
        setTitle("Update Available");
        setHeaderText("Update AssignmentTodo");
        setContentText("An update is available for AssignmentTodo.");
        final String warning = "IMPORTANT: Let the process restart on its own - it may take up to 10 seconds for the\n" +
                "application to update itself. You do not need to do anything besides press update and restart - the updates\n" +
                "will be fetched and installed on their own. However, if you open the application and update again\n" +
                "(during this process), the results are indeterminate.";
        getDialogPane().setExpandableContent(new TextArea(warning));
        getButtonTypes().clear();
        ButtonType update = new ButtonType("Update And Restart", ButtonData.OK_DONE);
        getButtonTypes().addAll(update, ButtonType.CANCEL);
    }

}
