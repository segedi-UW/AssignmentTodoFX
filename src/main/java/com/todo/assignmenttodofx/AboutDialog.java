package com.todo.assignmenttodofx;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextArea;

import java.util.List;

public class AboutDialog extends Dialog<ButtonType> {

    public AboutDialog() {
        super();
        setTitle("About AssignmentTodo");
        initOwner(App.getStage());
        createDialog();
    }

    private static String readText() {
        final String aboutFilename = "aboutNew.txt";
        StringBuilder text = new StringBuilder();
        text.append(App.VERSION);
        text.append("\n\n");
        List<String> lines = Filer.readResource(aboutFilename);
        for (String line : lines) {
            text.append(line);
            text.append("\n");
        }
        return text.toString();
    }

    private void createDialog() {
        DialogPane pane = getDialogPane();
        pane.getButtonTypes().clear();
        pane.getButtonTypes().add(ButtonType.OK);
        TextArea display = new TextArea(readText());
        display.setMinSize(500, 500);
        display.setEditable(false);
        pane.setContent(display);
    }

}
