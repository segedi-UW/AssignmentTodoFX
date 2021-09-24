package com.todo.assignmenttodofx;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

public class Notification {

    private static Stage stage;

    private final Notifications notification;
    private final Type type;

    public static void createAndShowStage() {
        Stage s = new Stage();
        s.initModality(Modality.NONE);
        s.initStyle(StageStyle.UTILITY);
        s.setOpacity(0);
        Group root = new Group();
        Scene scene = new Scene(root, 1, 1, Color.TRANSPARENT);
        s.setScene(scene);
        stage = s;
        s.show();
    }

    public enum Type {
        WARNING, ERROR, INFORMATIONAL, CONFIRMATION
    }

    public Notification(Type type, String text) {
        this(type, "AssignmentTodo Notification", text);
    }

    public Notification(Type type, String title, String text) {
        if (stage == null || !stage.isShowing())
            throw new IllegalStateException("Cannot create Notification before creating and showing Stage " +
                    "use the createAndShowStage() method before creating Notifications.");
        this.type = type;
        notification = Notifications.create();
        notification.darkStyle().position(Pos.BOTTOM_RIGHT).title(title).text(text).owner(stage);
    }

    public void setHideAfterSeconds(double duration) {
        notification.hideAfter(Duration.seconds(duration));
    }

    public void show() {
        Platform.runLater(this::showOfType);
    }

    private void showOfType() {
        // runs within the javafx application thread
        Stage main = App.getStage();
        if (main.isShowing())
            notification.owner(main);
        switch (type) {
            case WARNING:
                notification.showWarning();
                break;
            case ERROR:
                notification.showError();
                break;
            case INFORMATIONAL:
                notification.showInformation();
                break;
            case CONFIRMATION:
                notification.showConfirm();
                break;
            default:
                notification.show();
                break;
        }
    }

    @Override
    public String toString() {
        return notification.toString();
    }
}
