package com.todo.assignmenttodofx;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

public final class Filer {

    public static final String FOLDER = "AssignmentTodo";
    public static final String EXTENSION = "tdo";
    public static final String DEFAULT_FILE = "AssignmentTodo-Default." + EXTENSION;

    public interface Savable<T> {
        void save(FileWriter writer, Collection<T> list) throws IOException;
    }

    public interface Parsable<T> {
        List<T> parse(Iterator<String> lines);
    }

    private Filer() {
    }

    public static File getDefaultFile() {
        return new File(getDirectory(), DEFAULT_FILE);
    }

    public static File getDirectory() {
        StringBuilder builder = new StringBuilder();
        builder.append(System.getProperty("user.home"));
        builder.append(File.separator);
        builder.append(FOLDER);
        File folder = new File(builder.toString());
        if (!folder.exists())
            System.out.println("Folder created: " + folder.mkdir());
        return new File(builder.toString());
    }

    public static List<String> readResource(String name) {
        List<String> lines = new LinkedList<>();
        try {
            lines = readResourceThrow(name);
        } catch (NullPointerException e) {
            System.err.println("Stream of " + name + " was null - returning empty list");
        }
        return lines;
    }

    public static List<String> readResourceThrow(String name) {
        LinkedList<String> lines = new LinkedList<>();
        InputStream stream = Filer.class.getResourceAsStream(name);
        if (stream == null)
            throw new NullPointerException("Stream of " + name + " was null");
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        reader.lines().forEach(lines::add);
        return lines;
    }

    public static <T> void writeFile(Savable<T> saver, Collection<T> list, File file) {
        try (FileWriter writer = new FileWriter(file)) {
            saver.save(writer, list);
        } catch (IOException e) {
            System.err.println("Error Saving File");
        }
    }

    public static <T> List<T> readFile(Parsable<T> parser, File file) {
        List<T> list = new ArrayList<>();
        if (!file.exists())
            return list;
        try {
            Iterator<String> iterator = Files.readAllLines(file.toPath()).listIterator();
            list.addAll(parser.parse(iterator));
        } catch (IOException e) {
            System.err.println("Error parsing files");
        }
        return list;
    }
}
