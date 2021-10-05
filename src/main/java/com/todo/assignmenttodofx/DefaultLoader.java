package com.todo.assignmenttodofx;

import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DefaultLoader {

    private final Controller controller;

    public DefaultLoader(Controller controller) {
        this.controller = controller;
    }

    public void start() {
        initialCheck();
    }

    private void initialCheck() {
        boolean loadDefaultPref = Preference.LOAD_DEFAULT.getBoolean();
        if (loadDefaultPref) {
            if (hasDefault())
                loadDefault();
            else {
                promptCreateDefault();
            }
        }
        showStatusNotification();
    }

    public void promptCreateDefault() {
        Alert alert = new CreateAlert();
        alert.showAndWait().ifPresent(button -> {
            if (button.equals(ButtonType.YES) && createDefaultFile()) loadDefault();
            else {
                if (!button.equals(ButtonType.YES)) return;
                controller.showError(new NullPointerException(), "Failed to create default file");
            }
        });
    }

    private void showStatusNotification() {
        boolean hasDefault = hasDefault();
        boolean loadDefaultPref = Preference.LOAD_DEFAULT.getBoolean();
        String defaultText = hasDefault ? "The default file was found and loaded"
                : "The default file could not be found";
        String noDefaultText = "Autoload default file is turned off. Turn on in Preferences to use.";
        String text = loadDefaultPref ? defaultText : noDefaultText;
        Notification notification = new Notification(Notification.Type.INFORMATIONAL, text);
        notification.setHideAfterSeconds(2.6);
        notification.show();
    }

    private static class CreateAlert extends Alert {
        CreateAlert() {
            super(AlertType.CONFIRMATION);
            setTitle("Default File");
            setHeaderText("Create the Default File?");
            Label label = new Label("Would you like to create a system wide default file for AssignmentTodo?\n"
                    + "It will be stored at your user root directory, so you can freely move the jar file\n"
                    + "wherever you want without worrying about bringing a file around.");
            DialogPane pane = getDialogPane();
            pane.setContent(label);
            ObservableList<ButtonType> buttons = pane.getButtonTypes();
            buttons.clear();
            buttons.addAll(ButtonType.YES, ButtonType.NO);
        }

    }

    private boolean createDefaultFile() {
        File defaultFile = Filer.getDefaultFile();
        try {
            FileWriter writer = new FileWriter(defaultFile);
            writer.close();
            return true;
        } catch (IOException e) {
            System.err.println("There was an error creating the default file: " + e.getMessage());
            return false;
        }
    }

    private boolean hasDefault() {
        return Filer.getDefaultFile().exists();
    }

    public void loadDefault() {
        File file = Filer.getDefaultFile();
        if (file.exists()) {
            controller.loadFile(file);
        }
    }

}
