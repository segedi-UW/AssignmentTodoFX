package com.todo.assignmenttodofx;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import javafx.stage.*;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Notification {

    public static final double NO_AUTO_HIDE = -1;
    private static Stage stage;

    private static final ConcurrentLinkedQueue<Notification> showing = new ConcurrentLinkedQueue<>();
    private static final LinkedList<Notification> showed = new LinkedList<>();
    private final Notifications notification;
    private final Type type;
    private final String title;
    private final String text;

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
        notification = createDefault(title, text);
        Notifications threshNotification = Notifications.create();
        threshNotification.darkStyle().position(Pos.BOTTOM_RIGHT).title("Notifications").text("Click to See Description").owner(stage);
        threshNotification.hideAfter(null);
        threshNotification.onAction(event -> {
            StringBuilder builder = new StringBuilder();
            for (Notification n : showing) {
                builder.append(n.title);
                builder.append("\n");
                builder.append(n.text);
                builder.append("\n\n");
            }
            Platform.runLater(() -> new ThresholdAlert(builder.toString(), "Recent Notifications Summary").show());
            showing.clear();
        });
        notification.threshold(3, threshNotification);
    }

    private static Notifications createDefault(String title, String text) {
        Notifications notification = Notifications.create();
        notification.darkStyle().position(Pos.BOTTOM_RIGHT).title(title).text(text).owner(stage);
        if (Preference.AUTO_HIDE_NOTIFICATIONS.getBoolean())
            notification.hideAfter(Duration.seconds(3));
        else
            notification.hideAfter(Duration.INDEFINITE);
        notification.onAction(event -> {
            App.getStage().show();
            App.getStage().setIconified(false);
            App.getStage().toFront();
        });
        return notification;
    }

    public static void createAndShowStage() {
        Stage s = new Stage();
        s.initModality(Modality.NONE);
        s.initStyle(StageStyle.UTILITY);
        s.setOpacity(0);
        Group root = new Group();
        Rectangle2D screen = Screen.getPrimary().getBounds();
        final int bottomMargin = 60;
        Scene scene = new Scene(root, screen.getWidth(), screen.getHeight() - bottomMargin, Color.TRANSPARENT);
        s.setScene(scene);
        stage = s;
        s.show();
    }

    public static void showThenClearShown() {
        if (showed.size() > 0)
            Platform.runLater(() -> {
                Notifications promptNotification = Notifications.create();
                if (Preference.AUTO_HIDE_REMINDERS.getBoolean())
                    promptNotification.hideAfter(Duration.INDEFINITE);
                promptNotification = createDefault("Notification Summary", "Click here to see notification summary");
                promptNotification.onAction(event -> {
                    StringBuilder builder = new StringBuilder();
                    for (Notification n : showed) {
                        builder.append(n.title);
                        builder.append("\n");
                        builder.append(n.text);
                        builder.append("\n\n");
                    }
                    new ThresholdAlert(builder.toString(), "Notifications Shown While in Tray").showAndWait();
                    showed.clear();
                });
                promptNotification.showConfirm();
            });
    }

    public void setHideAfterSeconds(double duration) {
        if (duration > 0.8)
            notification.hideAfter(Duration.seconds(duration));
        else if (duration >= NO_AUTO_HIDE - 0.001 && duration <= NO_AUTO_HIDE + 0.001)
            notification.hideAfter(Duration.INDEFINITE);
    }

    public void show() {
        Platform.runLater(this::showOfType);
    }

    private void showOfType() {
        // runs within the javafx application thread
        if (!App.getStage().isShowing())
            showed.add(this);
        showing.add(this);
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

    private static class ThresholdAlert extends Alert {
        public ThresholdAlert(String areaText, String header) {
            super(AlertType.INFORMATION);
            setTitle("AssignmentTodo Notifications");
            setHeaderText(header);
            TextArea area = new TextArea(areaText);
            area.setEditable(false);
            getDialogPane().setContent(area);
        }
    }
}
