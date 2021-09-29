package com.todo.assignmenttodofx;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import org.controlsfx.control.CheckListView;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class Controller {
    @FXML public CheckMenuItem militaryTimeCheck;
    @FXML public CheckMenuItem muteCheck;
    @FXML public CheckMenuItem autoLoadCheck;
    @FXML public CheckMenuItem autoNotificationCheck;
    @FXML public CheckMenuItem autoHideNotifications;
    @FXML public CheckMenuItem autoHideReminders;
    @FXML public Label summaryText;
    @FXML public Label dueText;
    @FXML public TextArea descriptionArea;
    @FXML public CheckListView<Reminder> reminders;
    @FXML private CheckListView<Assignment> master;
    @FXML private ComboBox<AudioResource> notificationSounds;
    @FXML private Label savedText;
    @FXML private Label loadFilename;
    @FXML private HBox calendar;
    @FXML private MenuItem openMenuItem;
    @FXML private Button openButton;
    @FXML private Button editButton;
    @FXML private Button removeButton;
    @FXML private Button clearButton;
    @FXML private Button cancelReminderButton;
    private WeekView weekView;
    private File loadFile;
    private final SimpleBooleanProperty saved;

    public Controller() {
        saved = new SimpleBooleanProperty(true);
    }

    @FXML
    private void initialize() {
        master.getItems().addListener((ListChangeListener<? super Assignment>) c -> {
            while (c.next()) {
                List<? extends Assignment> removed = c.getRemoved();
                for (Assignment a : removed) {
                    a.cancel();
                }
            }
        });
        reminders.setFocusTraversable(false);
        master.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            boolean isSelection = newV != null;
            boolean hasLink = isSelection && newV.getLink() != null;
            editButton.setDisable(!isSelection);
            openButton.setDisable(!hasLink);
            openMenuItem.setDisable(!hasLink);
        });
        master.getCheckModel().getCheckedItems().addListener((ListChangeListener<? super Assignment>) c ->
                removeButton.setDisable(master.getCheckModel().getCheckedItems().size() <= 0));
        reminders.getCheckModel().getCheckedItems().addListener((ListChangeListener<? super Reminder>) c ->
                cancelReminderButton.setDisable(reminders.getCheckModel().getCheckedItems().size() <= 0));
        master.getItems().addListener((ListChangeListener<? super Assignment>) change ->
                clearButton.setDisable(master.getItems().isEmpty()));
        boolean isMuted = Preference.MUTE.getBoolean();
        boolean isMilitary = Preference.MILITARY.getBoolean();
        muteCheck.setSelected(isMuted);
        muteCheck.selectedProperty().addListener((obs, oldVal, newVal) -> {
            Notification notification = new Notification(Notification.Type.INFORMATIONAL, (newVal ? "Muted" : "Unmuted"));
            notification.setHideAfterSeconds(1.0);
            notification.show();
            Preference.MUTE.put(newVal);
        });
        militaryTimeCheck.setSelected(isMilitary);
        militaryTimeCheck.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                Preference.MILITARY.put(newVal);
                refresh();
            } else
                System.err.println("MilitaryCheck set to null!");
        });
        autoLoadCheck.setSelected(Preference.LOAD_DEFAULT.getBoolean());
        autoLoadCheck.selectedProperty().addListener((obs, oldValue, newValue) -> Preference.LOAD_DEFAULT.put(newValue));
        autoHideNotifications.setSelected(Preference.AUTO_HIDE_NOTIFICATIONS.getBoolean());
        autoHideNotifications.selectedProperty().addListener((obs, oldValue, newValue) -> Preference.AUTO_HIDE_NOTIFICATIONS.put(newValue));
        autoHideReminders.setSelected(Preference.AUTO_HIDE_REMINDERS.getBoolean());
        autoHideReminders.selectedProperty().addListener((obs, oldValue, newValue) -> Preference.AUTO_HIDE_REMINDERS.put(newValue));
        autoNotificationCheck.setSelected(Preference.AUTO_REMIND.getBoolean());
        autoNotificationCheck.selectedProperty().addListener((obs, oldValue, newValue) -> {
            Preference.AUTO_REMIND.put(newValue);
            if (!newValue) {
                master.getItems().forEach(assignment -> {
                    Reminder reminder = assignment.getAutoReminder();
                    if (reminder != null)
                        reminder.cancel();
                });
            } else {
                master.getItems().forEach(Assignment::createAutoReminder);
            }
            refresh();
        });
        weekView = new WeekView(calendar, this);
        master.setCellFactory(listView -> new AssignmentCell(Display.SUMMARY_TIME, master));
        saved.addListener(observable -> {
            boolean isSaved = saved.get();
            savedText.setText(isSaved ? "Saved" : "*Save");
            final String style = "unsaved";
            ObservableList<String> styles = savedText.getStyleClass();
            styles.removeIf(style::equals);
            if (!isSaved) styles.add(style);
        });
        ObservableMap<String, Category> categories = Category.getCategories();
        categories.addListener((MapChangeListener<String, Category>) change -> {
            Category removed = change.getValueRemoved();
            master.getItems().forEach(assignment -> {
                if (assignment.getCategory().equals(removed))
                    assignment.setCategory(categories.get(Category.STANDARD));
            });
        });
        HashMap<String, AudioResource> map = App.getNotificationSounds();
        notificationSounds.getItems().addAll(map.values());
        String wavName = Preference.TONE.get();
        AudioResource initial = map.get(wavName);
        if (initial == null) {
            wavName = (String) Preference.TONE.getInitialValue();
            initial = map.get(wavName);
            notificationSounds.getSelectionModel().select(initial);
        }
        notificationSounds.getSelectionModel().select(initial);
        Reminder.setSound(initial.getClip());
        notificationSounds.getSelectionModel().selectedItemProperty().addListener(this::setSound);
        master.getSelectionModel().selectedItemProperty().addListener(this::setDetails);
        refresh();
    }

    public boolean isNewInstallation() {
        Preference version = Preference.VERSION;
        String oldInstall = version.get("NO_INSTALL");
        return !oldInstall.equals(App.VERSION);
    }

    public void showError(String message) {
        showError(new NullPointerException("No Exception"), message);
    }

    public void showError(Exception e, String message) {
        showError(e, message, null);
    }

    public void showError(Exception e, String message, String log) {
        String show = e.getMessage() + ": " + message + (log != null ? log : "");
        System.err.println(show);
        ErrorAlert alert = new ErrorAlert(e, message, log);
        Platform.runLater(alert::showAndWait);
    }

    public void refresh() {
        if (master != null && weekView != null) {
            master.refresh();
            weekView.refresh();
            setDetails(null, null, getSelectedItem());
        }
    }

    @FXML
    public void loadDefault() {
        loadFile(Filer.getDefaultFile());
        saved.set(true);
    }

    public CheckListView<Assignment> getMaster() {
        return master;
    }

    @FXML
    public void save() {
        if (loadFile == null)
            saveAs();
        else
            saveFile(loadFile);
    }

    @FXML
    private void edit() {
        Assignment toEdit = getSelectedItem();
        AssignmentDialog dialog = new AssignmentDialog(toEdit);
        dialog.showAndWait().ifPresent(assignment -> {
            toEdit.setAs(assignment);
            refresh();
            saved.set(false);
        });
    }

    @FXML
    private void openLink() {
        Assignment toOpen = getSelectedItem();
        if (toOpen == null)
            return;
        final String link = toOpen.getLink();
        if (link == null) {
            showError("Cannot open empty link");
            return;
        }
        File file = new File(link);
        if (file.exists())
            openFile(file);
        else
            browseInternet(link);
    }

    private void openFile(File file) {
        try {
            Desktop desktop = Desktop.getDesktop();
            desktop.open(file);
        } catch (IOException e) {
            showError(e, "Failed to open " + file.getName());
        }
    }

    private void browseInternet(String url) {
        try {
            Desktop desktop = Desktop.getDesktop();
            desktop.browse(new URL(url).toURI());
        } catch (IOException | URISyntaxException e) {
            showError(e, "Failed to open link");
        }
    }


    @FXML
    private void saveAs() {
        File file = fileChooser().showSaveDialog(App.getStage());
        if (file != null)
            saveFile(file);
    }

    private void saveFile(File file) {
        Filer.writeFile(new AssignmentParser(), master.getItems(), file);
        saved.set(true);
    }

    private FileChooser fileChooser() {
        FileChooser chooser = new FileChooser();
        final String extension = Filer.EXTENSION;
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Todo Files", "*." + extension));
        chooser.setInitialDirectory(new File((String) System.getProperties().get("user.dir")));
        return chooser;
    }

    @FXML
    private void newTask() {
        AssignmentDialog dialog = new AssignmentDialog();
        dialog.showAndWait().ifPresent(assignment -> {
            master.getItems().add(assignment);
            refresh();
            saved.set(false);
        });
    }

    @FXML
    private void clear() {
        ClearAlert alert = new ClearAlert();
        alert.showAndWait().ifPresent(button -> {
            if (button.equals(ButtonType.OK)) {
                master.getItems().clear();
                master.getCheckModel().clearChecks();
                refresh();
                saved.set(false);
            }
        });
    }

    @FXML
    private void removeSelectedTasks() {
        ObservableList<Assignment> checked = master.getCheckModel().getCheckedItems();
        if (checked.size() > 0) {
            master.getItems().removeAll(checked);
            master.getCheckModel().getCheckedItems();
            master.getCheckModel().clearChecks();
            refresh();
            saved.set(false);
        }
    }

    @FXML
    private void removeSelectedReminders() {
        Assignment selected = getSelectedItem();
        if (selected != null) {
            ObservableList<Reminder> reminds = reminders.getCheckModel().getCheckedItems();
            selected.getReminders().removeAll(reminds);
            reminders.getCheckModel().clearChecks();
            refresh();
            saved.set(false);
        }
    }

    @FXML
    private void newReminder() {
        Assignment assignment = getSelectedItem();
        if (assignment != null) {
            ReminderDialog dialog = new ReminderDialog(assignment);
            dialog.showAndWait().ifPresent(calendar -> {
                new Reminder(assignment.getCalendar(), assignment.toString(), assignment.getReminders());
                refresh();
                saved.set(false);
            });
        }
    }

    @FXML
    private void promptLoad() {
        File file = fileChooser().showOpenDialog(App.getStage());
        if (file != null)
            loadFile(file);
    }

    @FXML
    private void importAssignment() {
        File file = fileChooser().showOpenDialog(App.getStage());
        if (file != null && file.exists()) {
            master.getItems().addAll(Filer.readFile(new AssignmentParser(), file));
            saved.set(false);
        }
    }

    public void loadFile(File file) {
        ObservableList<Assignment> assignments = master.getItems();
        if (file != null && file.exists()) {
            if (!assignments.isEmpty() && !saved.get()) {
                SaveAlert alert = new SaveAlert();
                alert.showAndWait().ifPresent(button -> {
                    if (button.equals(ButtonType.YES))
                        save();
                });
            }
            assignments.clear();
            assignments.addAll(Filer.readFile(new AssignmentParser(), file));
            loadFile = file;
            loadFilename.setText(file.getName());
            assignments.sort(null);
            saved.set(true);
        }
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    @FXML
    public void resetPreferences() {
        resetPreferences(Preference.values());
        militaryTimeCheck.setSelected(Preference.MILITARY.getBoolean());
        muteCheck.setSelected(Preference.MUTE.getBoolean());
        AudioResource resource = App.getNotificationSounds().get(Preference.TONE.getInitialValue());
        if (resource != null)
            Reminder.setSound(resource.getClip());
    }

    @FXML
    public void minimizeToTray() {
        Tray tray = new Tray(this);
        tray.minimizeToTray();
    }

    @FXML
    public void openWebpage() {
        final String webPage = "https://segedi-uw.github.io/AssignmentTodofx/";
        browseInternet(webPage);
    }

    @FXML
    public void showAbout() {
        AboutDialog dialog = new AboutDialog();
        dialog.showAndWait();
    }

    public boolean isSaved() {
        return saved.get();
    }

    private void resetPreferences(Preference... preferences) {
        for (Preference pref : preferences) {
            pref.reset();
        }
    }

    private Assignment getSelectedItem() {
        return master.getSelectionModel().getSelectedItem();
    }

    private void setDetails(Observable obs, Assignment oldValue, Assignment newValue) {
        String summary = "";
        String description = "(No selection)";
        String due = "";
        reminders.getItems().clear();
        if (newValue != null) {
            reminders.getItems().addAll(newValue.getReminders());
            description = newValue.getDescription();
            summary = newValue.getSummary();
            Calendar c = newValue.getCalendar();
            due = CalendarPrinter.getDateString(c) + " at " + CalendarPrinter.getTimeString(c);
        }
        summaryText.setText(summary);
        dueText.setText(due);
        descriptionArea.setText(description);
    }

    private void setSound(Observable obs, AudioResource oldRes, AudioResource newRes) {
        Reminder.setSound(newRes.getClip());
        Preference.TONE.put(newRes.getResourceName());
        Reminder.play();
    }

    public void forceUpdate() {
        UpdateAlert alert = new UpdateAlert();
        alert.showAndWait().ifPresent(button -> {
            if (button.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                try {
                    AppUpdater.update();
                } catch (IOException e) {
                    showError(e, "Failed to update");
                }
            }
        });
    }
}