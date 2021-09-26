package com.todo.assignmenttodofx;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressIndicator;

public class CheckUpdateAlert extends Alert {

    public CheckUpdateAlert() {
        super(AlertType.INFORMATION);
        initOwner(App.getStage());
        setTitle("Checking for Update");
        setHeaderText("Checking Online Version...");
        ProgressIndicator indicator = new ProgressIndicator(ProgressIndicator.INDETERMINATE_PROGRESS);
        setGraphic(indicator);
        getButtonTypes().clear();
        getButtonTypes().add(ButtonType.CANCEL);
        Node cancel = getDialogPane().lookupButton(ButtonType.CANCEL);
        cancel.setVisible(false);
    }

}
