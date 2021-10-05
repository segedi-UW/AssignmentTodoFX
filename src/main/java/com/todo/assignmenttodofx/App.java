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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class App extends Application {

    public static final String VERSION_ERROR = "Error Reading Version";
    public static final String VERSION = readVersion();
    public static final String DOWNLOAD_URL = "https://github.com/segedi-UW/AssignmentTodofx/blob/master/out/artifacts/AssignmentTodo_jar/AssignmentTodo.jar?raw=true";
    public static final String DOWNLOAD_VERSION = "https://github.com/segedi-UW/AssignmentTodofx/raw/master/src/main/resources/com/todo/assignmenttodofx/AssignmentTodo.vrs";
    public static final String DOWNLOAD_INSTALLER = "https://github.com/segedi-UW/AssignmentTodofx/blob/master/target/classes/Installer.class?raw=true";
    private static Stage main;
    private static final HashMap<String, AudioResource> notificationSounds = notificationSounds();

    private Controller controller;


    private static String readVersion() {
        List<String> versionFile = Filer.readResource("AssignmentTodo.vrs");
        return !versionFile.isEmpty() ? versionFile.get(0) : VERSION_ERROR;

    }

    public static boolean checkUpdate(Controller controller, Parameters params) {
        Notification notification = new Notification(Notification.Type.INFORMATIONAL, "Checking for Update");
        notification.setHideAfterSeconds(1);
        notification.show();
        String update = "";
        if (params != null) {
            Map<String, String> named = params.getNamed();
            System.out.println("Mapped names: " + named.keySet() + " : " + named.values());
            final String updateName = "update";
            update = named.get(updateName) != null ? named.get(updateName) : "";
            boolean failedUpdate = update.equalsIgnoreCase("fail");
            if (failedUpdate) {
                String log = named.get("updateLog");
                System.err.println("log: " + log);
                controller.showError(new IllegalStateException("Update Failed"), "The update process failed", log);
            }
        }
        System.out.println("Update param: " + update);
        boolean forceUpdate = update.equalsIgnoreCase("true");
        boolean noUpdate = update.equalsIgnoreCase("false");
        boolean hasUpdate = forceUpdate || (!noUpdate && AppUpdater.hasUpdate());
        if (App.VERSION.equals(App.VERSION_ERROR)) {
            controller.showError(new NullPointerException(""), "Version failed to be read - try reinstalling if the error persists",
                    "Note that In the case of Version Read Failure, the Application will work correctly, it just" +
                            "\nwill not update automatically. This would typically be a version error, so if it occurs\n" +
                            "it will be unlikely for it to not occur on the next startup. Request that I update the jar\n" +
                            "at aj.segedi@gmail.com if you have this error, or reach out to me at 262-955-5532");
        } else if (hasUpdate) {
            Platform.runLater(() -> {
                UpdateAlert updateAlert = new UpdateAlert();
                updateAlert.showAndWait().ifPresent(button -> {
                    if (button.getButtonData() == ButtonBar.ButtonData.OK_DONE)
                        try {
                            System.out.println("Updating");
                            AppUpdater.update(); // this should kill the program
                        } catch (IOException e) {
                            controller.showError(e, "Update Failed");
                        }
                });
            });
        }
        return false;
    }

    @Override
    public void start(Stage stage) throws IOException {
        main = stage;
        Notification.createAndShowStage();
        stage.setOnShowing(event -> {
            System.out.println("Showing Main Stage");
            Notification.showThenClearShown();
        });
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
        if (controller == null) {
            System.err.println("Fatal Error - could not load main fxml controller");
            throw new NullPointerException("Could not load main fxml controller");
        }
        Thread thread = new Thread(() -> {
            if (!checkUpdate(controller, getParameters()) && controller.isNewInstallation()) {
                Platform.runLater(() -> {
                    AboutDialog about = new AboutDialog();
                    about.show();
                });
                Preference.VERSION.put(App.VERSION);
            }
        });
        thread.setDaemon(true);
        thread.start();
        Updater updater = Updater.dailyUpdater(Calendar.getInstance(), () -> checkUpdate(controller, null));
        updater.start();
        if (Preference.LOAD_DEFAULT.getBoolean()) {
            DefaultLoader loader = new DefaultLoader(controller);
            loader.start();
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
