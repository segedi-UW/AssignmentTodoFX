package com.todo.assignmenttodofx;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Notification {

    private static Stage stage;

    private static final ConcurrentLinkedQueue<Notification> showing = new ConcurrentLinkedQueue<>();
    private static final Timer remover = new Timer(true);
    private final Notifications notification;
    private final Type type;
    private final String title;
    private final String text;

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
        this.title = title;
        this.text = text;
        this.type = type;
        notification = Notifications.create();
        notification.darkStyle().position(Pos.BOTTOM_RIGHT).title(title).text(text).owner(stage);
        setHideAfterSeconds(3);
        Notifications threshNotification = Notifications.create();
        threshNotification.darkStyle().position(Pos.BOTTOM_RIGHT).title("Notifications").text("Click to See Description").owner(stage);
        threshNotification.onAction(event -> {
            StringBuilder builder = new StringBuilder();
            for (Notification n : showing) {
                builder.append(n.title);
                builder.append("\n");
                builder.append(n.text);
                builder.append("\n\n");
            }
            Platform.runLater(() -> new ThresholdAlert(builder.toString()).show());
        });
        notification.threshold(3, threshNotification);
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
        showing.add(this);
        final long autoRemove = 30 * 1000L; // 10 seconds
        remover.schedule(removeTask(), autoRemove);
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

    private TimerTask removeTask() {
        Notification current = this;
        return new TimerTask() {
            @Override
            public void run() {
                showing.remove(current);
            }
        };
    }

    @Override
    public String toString() {
        return notification.toString();
    }

    private static class ThresholdAlert extends Alert {
        public ThresholdAlert(String areaText) {
            super(AlertType.INFORMATION);
            setTitle("AssignmentTodo Notifications");
            setHeaderText("Recent Notifications Summary");
            TextArea area = new TextArea(areaText);
            area.setEditable(false);
            getDialogPane().setContent(area);
        }
    }
}
