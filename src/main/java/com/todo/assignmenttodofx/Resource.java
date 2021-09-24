package com.todo.assignmenttodofx;

import java.net.URL;

public class Resource {

    private final String name;
    private final URL url;

    public Resource(String name, URL url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name.substring(0, name.length() - 4);
    }

    public String getResourceName() {
        return name;
    }

    public URL getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return getName();
    }
}
