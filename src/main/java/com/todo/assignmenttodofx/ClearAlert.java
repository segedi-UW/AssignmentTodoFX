package com.todo.assignmenttodofx;

import javafx.scene.control.Alert;

public class ClearAlert extends Alert {
    public ClearAlert() {
        super(AlertType.CONFIRMATION);
        initOwner(App.getStage());
        setTitle("Clear Alert");
        setHeaderText("Clear items");
        setContentText("Clearing does not delete items until file save.");
    }
}
