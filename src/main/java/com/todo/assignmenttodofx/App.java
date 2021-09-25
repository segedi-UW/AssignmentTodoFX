package com.todo.assignmenttodofx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class App extends Application {

    public static final String VERSION = "AssignmentTodo 3.3.2";
    public static final String DOWNLOAD_URL = "https://segedi-UW.github.io/files/AssignmentTodo.jar";
    public static final String DOWNLOAD_VERSION = "https://segedi-UW.github.io/files/AssignmentTodo.vrs";
    public static final String DOWNLOAD_INSTALLER = "https://segedi-UW.github.io/files/Installer.class";
    private static Stage main;
    private static final HashMap<String, AudioResource> notificationSounds = notificationSounds();

    private Controller controller;

    @Override
    public void start(Stage stage) throws IOException {
        main = stage;
        Notification.createAndShowStage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("assignment-view.fxml"));
        if (fxmlLoader.getLocation() == null) {
            System.err.println("Could not locate the main fxml file [App]");
            throw new IllegalStateException("Could not locate the main fxml file [App]");
        }
        final int width = 800, height = 500;
        Scene scene = new Scene(fxmlLoader.load(), width, height);
        stage.setMinHeight(height);
        stage.setMinWidth(width);
        addStyleSheet(scene);
        setupStage(scene);
        controller = fxmlLoader.getController();
        if (controller != null) {
            if (Preference.LOAD_DEFAULT.getBoolean()) {
                DefaultLoader loader = new DefaultLoader(controller);
                loader.start();
            }
            final Parameters parameters = getParameters();
            Map<String, String> named = parameters.getNamed();
            String update = named.get("update");
            if (update == null) update = ""; // sets to blank
            System.out.println("Update param: " + update);
            boolean forceUpdate = update.equalsIgnoreCase("true");
            boolean noUpdate = update.equalsIgnoreCase("false");
            boolean hasUpdate = (!noUpdate && AppUpdater.hasUpdate()) || forceUpdate;
            boolean failedUpdate = update.equalsIgnoreCase("fail");
            if (failedUpdate) {
                String log = named.get("updateLog");
                controller.showError(new IllegalStateException("Update Failed"), "The update process failed", log);
            }
            if (hasUpdate) {
                UpdateAlert alert = new UpdateAlert();
                alert.showAndWait().ifPresent(button -> {
                    if (button.getButtonData() == ButtonBar.ButtonData.OK_DONE)
                        try {
                            System.out.println("Updating");
                            AppUpdater.update(); // this should kill the program
                        } catch (IOException e) {
                            System.err.println("Failed to update the jar");
                        }
                });
            }
            if (controller.isNewInstallation()) {
                AboutDialog about = new AboutDialog();
                about.showAndWait();
                Preference.VERSION.put(App.VERSION);
            }
        }
    }

    public static Stage getStage() {
        return main;
    }

    public static void addStyleSheet(Scene scene, String resource) {
        URL url = App.class.getResource(resource);
        if (url != null) {
            final String sheet = url.toExternalForm();
            scene.getStylesheets().add(sheet);
        } else {
            System.out.println("Could not add sheet " + resource);
        }
    }

    public static void addStyleSheet(Scene scene) {
        final String defaultSheet = "stylesheet.css";
        addStyleSheet(scene, defaultSheet);
    }

    public static HashMap<String, AudioResource> getNotificationSounds() {
        return notificationSounds;
    }

    private static HashMap<String, AudioResource> notificationSounds() {
        ResourceParser<App> parser = new ResourceParser<>(App.class);
        List<Resource> res = parser.getResources("notification-sounds", ".*wav$");
        final int size = (res.size() * 2) + 1;
        HashMap<String, AudioResource> map = new HashMap<>(size);
        res.forEach(resource -> {
            AudioResource audio = new AudioResource(resource);
            map.put(audio.getResourceName(), audio);
        });
        return map;
    }

    private void setupStage(Scene scene) {
        main.setTitle("AssignmentTodo");
        addIcon();
        main.setOnCloseRequest(this::handleExit);
        main.setScene(scene);
        main.show();
    }

    private void addIcon() {
        URL url = App.class.getResource("images/icon.png");
        if (url != null) {
            Image icon = new Image(url.toExternalForm());
            ObservableList<Image> icons = main.getIcons();
            icons.add(icon);
        } else {
            System.err.println("Icon was not found");
        }
    }

    private void handleExit(WindowEvent event) {
        if (!controller.isSaved()) {
            ExitSaveAlert alert = new ExitSaveAlert();
            alert.showAndWait().ifPresent(button -> {
                if (button.equals(ButtonType.YES)) {
                    controller.save();
                } else if (button.equals(ButtonType.CANCEL)) {
                    event.consume();
                }
            });
        }
        if (!event.isConsumed())
            Platform.exit();
    }
}