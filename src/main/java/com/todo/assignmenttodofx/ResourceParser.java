package com.todo.assignmenttodofx;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ResourceParser<T> {

    private final boolean inJar;
    private final Class<T> runningClass;

    public ResourceParser(Class<T> runningClass) {
        // determine if loading from a jar or not
        this.runningClass = runningClass;
        inJar = inJar();
    }

    private boolean inJar() {
        URL url = runningClass.getResource("ResourceParser.class");
        if (url != null) {
            String path = url.toExternalForm();
            return path.startsWith("jar");
        } else {
            throw new IllegalStateException("Could not determine if running from jar or file");
        }
    }

    public LinkedList<Resource> getResources(String dirName, String regex) {
        try {
            return inJar ? getJarResources(dirName, regex) : getFileResources(dirName, regex);
        } catch (URISyntaxException | IOException e) {
            return new LinkedList<>();
        }
    }

    private LinkedList<Resource> getJarResources(String dirName, String regex) throws IOException {
        LinkedList<Resource> resources = new LinkedList<>();
        URL dirUrl = runningClass.getResource(dirName);
        if (dirUrl == null) {
            System.err.println("Could not locate directory");
            return resources;
        }
        JarURLConnection jarUrl = (JarURLConnection) dirUrl.openConnection();
        JarFile jar = jarUrl.getJarFile();
        Enumeration<JarEntry> entries = jar.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String name = entry.getName();
            if (name.matches(regex)) {
                final int index = name.lastIndexOf("/"); // Jar files only use forward slash
                if (index < 0) {
                    System.err.println(name + " had no \"/\": ");
                    continue;
                }
                String resourceName = dirName + name.substring(index);
                URL url = runningClass.getResource(resourceName);
                jar.getInputStream(entry);
                if (url != null) {
                    Resource resource = new Resource(name.substring(index + 1), url); // index includes the separator
                    resources.add(resource);
                } else
                    System.err.println("Matched with " + resourceName + " but did not locate");
            }
        }
        return resources;
    }

    private LinkedList<Resource> getFileResources(String dirName, final String regex) throws URISyntaxException, MalformedURLException {
        LinkedList<Resource> resources = new LinkedList<>();
        URL dirUrl = runningClass.getResource(dirName);
        if (dirUrl != null) {
            File dir = new File(dirUrl.toURI().getPath());
            if (dir.exists() && dir.isDirectory()) {
                FilenameFilter filter = (dir1, name) -> name.matches(regex);
                String[] list = dir.list(filter);
                if (list != null) {
                    for (String name : list) {
                        URL fileUrl = new File(dir, name).toURI().toURL();
                        resources.add(new Resource(name, fileUrl));
                    }
                } else
                    System.err.println("Filtered List was null");
            } else
                System.err.println(dir.getName() + " did not exist or was not a directory");
        } else
            System.err.println("Did not find resource " + dirName);
        return resources;
    }
}