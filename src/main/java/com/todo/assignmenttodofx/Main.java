package com.todo.assignmenttodofx;

import javafx.application.Application;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.StandardOpenOption;
import java.util.Calendar;

public class Main {

    private static final String lockFilename = "appLockFile";
    private static final String logFilename = "log.txt";
    private static final File logFile = new File(Filer.getDirectory(), logFilename);
    private static final File lockFile = new File(Filer.getDirectory(), lockFilename);

    public static void main(String[] args) {
        try (FileChannel channel = FileChannel.open(lockFile.toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
            FileLock lock = channel.tryLock();
            if (lock != null) {
                try (PrintStream stream = new PrintStream(logFile)) {
                    System.setErr(stream);
                    System.err.println("Log file start - " + App.VERSION);
                    System.err.println("Started: " + Calendar.getInstance().getTime());
                    stream.flush();
                    Application.launch(App.class, args);
                    stream.flush();
                } catch (Exception e) {
                    System.err.println("Exception: " + e.getMessage());
                } finally {
                    lock.release();
                }
            } else {
                System.err.println("Application Already Running");
            }
        } catch (IOException e) {
            System.err.println("Could not establish FileLock");
        }
    }
}
