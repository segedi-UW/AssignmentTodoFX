package com.todo.assignmenttodofx;

import javafx.application.Platform;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

public class Tray {

    private final Controller controller;

        public Tray(Controller controller) {
            this.controller = controller;
        }

        /**
         * SystemTray code thanks to @author Ayaskant Mishra from StackOverflow:
         * https://stackoverflow.com/questions/40571199/creating-tray-icon-using-javafx
         */
        public void minimizeToTray() {
            Toolkit.getDefaultToolkit();
            if (SystemTray.isSupported()) {
                SystemTray tray = SystemTray.getSystemTray();
                Image image = getTrayIcon();
                if (image == null) {
                    System.err.println("Could not create Icon");
                    return;
                }
                TrayIcon icon = new TrayIcon(image);
                // if the user double-clicks on the tray icon, show the main app stage.
                icon.addActionListener(event -> Platform.runLater(() -> unminimizeTray(tray, icon)));

                // if the user selects the default menu item (which includes the app name),
                // show the main app stage.
                MenuItem openItem = new MenuItem("Open");
                openItem.addActionListener(event -> Platform.runLater(() -> unminimizeTray(tray, icon)));

                // the convention for tray icons seems to be to set the default icon for opening
                // the application stage in a bold font.
                Font defaultFont = Font.decode(null);
                Font boldFont = defaultFont.deriveFont(Font.BOLD);
                openItem.setFont(boldFont);

                // to really exit the application, the user must go to the system tray icon
                // and select the exit option, this will shutdown JavaFX and remove the
                // tray icon (removing the tray icon will also shut down AWT).
                MenuItem exitItem = new MenuItem("Exit");
                exitItem.addActionListener(event -> {
                    Platform.exit();
                    tray.remove(icon);
                });

                // setup the popup menu for the application.
                final PopupMenu popup = new PopupMenu();
                popup.add(openItem);
                popup.addSeparator();
                popup.add(exitItem);
                icon.setPopupMenu(popup);

                try {
                    tray.add(icon);
                    App.getStage().hide();
                    String text = "Default Tray Location is here next to clock";
                    String title = "AssignmentTodo Minimized To Tray";
                    Notification notification;
                    notification = new Notification(Notification.Type.INFORMATIONAL, title, text);
                    notification.setHideAfterSeconds(2);
                    notification.show();
                } catch (AWTException e) {
                    controller.showError(e, "Could not add Tray to the System Tray");
                }

            } else {
                controller.showError(new NullPointerException(), "The System does not support System Tray.");
            }
        }

        private void unminimizeTray(SystemTray tray, TrayIcon icon) {
            Stage stage = App.getStage();
            if (stage != null) {
                stage.show();
                stage.toFront();
                tray.remove(icon);
            }
        }

        private Image getTrayIcon() {
            try {
                Dimension size = SystemTray.getSystemTray().getTrayIconSize();
                URL imageLoc = Tray.class.getResource("images/icon.png");
                if (imageLoc != null) {
                    Image image = ImageIO.read(imageLoc);
                    image = image.getScaledInstance(size.width, size.height, Image.SCALE_SMOOTH);
                    return image;
                }
            } catch (IOException e) {
                controller.showError(e, "TrayIcon could not be found.");
            }
            return null;
        }

}
