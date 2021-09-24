package com.todo.assignmenttodofx;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class AppUpdater {

    private static final URL download = download();
    private static final File file = new File("");

    public static boolean hasUpdate() {
        String fileId = "0BwwA4oUTeiV1UVNwOHItT0xfa2M";
        OutputStream outputStream = new ByteArrayOutputStream();
        //driveService.files().get(fileId).executeMediaAndDownloadTo(outputStream);

        return false;
    }

    public static boolean update() {
        return false;
    }

    private static URL download() {
        try {
            return new URL(App.DOWNLOAD_URL);
        } catch (MalformedURLException e) {
            System.err.println("Could not create URL: " + e.getMessage());
        }
        return null;
    }
}
