package com.todo.assignmenttodofx;

import javafx.application.Platform;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class AppUpdater {

    private static final URL version = toURL(App.DOWNLOAD_VERSION);
    private static final URL installer = toURL(App.DOWNLOAD_INSTALLER);

    public static boolean hasUpdate() {
        if (version != null) {
            try {
                URLConnection connection = version.openConnection();
                InputStream stream = connection.getInputStream();
                InputStreamReader streamReader = new InputStreamReader(stream);
                BufferedReader reader = new BufferedReader(streamReader);
                String uploadedVersion = reader.readLine();
                System.out.println("Read online version as: " + uploadedVersion);
                return !App.VERSION.equals(uploadedVersion);
            } catch (IOException e) {
                System.err.println("Failed to check version");
            }
        }
        return false;
    }

    public static void update() throws IOException {
        if (installer != null) {
            File dir = Filer.getDirectory();
            File tmp = new File(dir,"Installer.class");
            // move file from jar to tmp
            System.out.println("Created: " + tmp.getName());
            try (InputStream stream = installer.openStream()) {
                if (stream != null) {
                    Files.copy(stream, tmp.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    String workDir = System.getProperty("user.dir");
                    String cmd = "java " + "-cp " + dir.getAbsolutePath() + " Installer \"" + App.DOWNLOAD_URL + "\" \"" + workDir + "\"";
                    Platform.exit();
                    System.out.println("Created class file in " + tmp.getAbsolutePath());
                    System.out.println("Running: " + cmd);
                    Runtime.getRuntime().exec(cmd);
                } else System.err.println("Error reading class file");
            } catch (Exception e) {
                System.err.println("Failed to start download process " + e.getMessage());
            }
        }
    }

    private static URL toURL(String name) {
        try {
            return new URL(name);
        } catch (MalformedURLException e) {
            System.err.println("Could not create URL from " + name + ": " + e.getMessage());
        }
        return null;
    }

}
