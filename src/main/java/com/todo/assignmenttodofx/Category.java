package com.todo.assignmenttodofx;

import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.scene.paint.Color;

import java.io.File;
import java.util.List;

public class Category {

    public static final String STANDARD = "Standard";
    public static final String IMPORTANT = "Important";
    public static final String WORK = "Work";
    public static final String EVENT = "Event";
    public static final String MISC = "Misc";

    private static final File file = file();
    private static final ObservableMap<String, Category> categories = categories();

    private static File file() {
        return new File(Filer.getDirectory(), "userCategories");
    }

    private static ObservableMap<String, Category> categories() {
        ObservableMap<String, Category> map = FXCollections.observableHashMap();
        // standard categories
        String[] names = {STANDARD, IMPORTANT, WORK, EVENT, MISC};
        Category[] standardCategories = {standard(), important(), work(), event(), misc()};
        for (int i = 0; i < names.length; i++) {
            map.put(names[i], standardCategories[i]);
        }
        readFromFile(map);
        map.addListener((MapChangeListener<? super String, ? super Category>) c ->
                Filer.writeFile(new CategoryParser(), categories.values(), file));
        return map;
    }

    public static void readFromFile(ObservableMap<String, Category> categories) {
        List<Category> reads = Filer.readFile(new CategoryParser(), file);
        for (Category category : reads) {
            categories.put(category.getName(), category);
        }
    }

    public static ObservableMap<String, Category> getCategories() {
        return categories;
    }

    public static Category standard() {
        Category category = new Category(STANDARD);
        category.color = Color.WHITE;
        category.isUser = false;
        return category;
    }

    public static Category work() {
        Category category = new Category(WORK);
        category.color = Color.ROYALBLUE;
        category.isUser = false;
        return category;
    }

    public static Category event() {
        Category category = new Category(EVENT);
        category.color = Color.FORESTGREEN;
        category.isUser = false;
        return category;
    }

    public static Category misc() {
        Category category = new Category(MISC);
        category.color = Color.YELLOW;
        category.isUser = false;
        return category;
    }

    public static Category important() {
        Category category = new Category(IMPORTANT);
        category.color = Color.FIREBRICK;
        category.isUser = false;
        return category;
    }

    private Color color;
    private String name;
    private boolean isUser;

    public Category(String name) {
        this.name = name;
        isUser = true;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public boolean isUser() {
        return isUser;
    }

    public Color getAlphaOf(double alpha) {
        if (color.isOpaque()) {
            double r = color.getRed(), g = color.getGreen(), b = color.getBlue();
            return new Color(r,g,b,alpha);
        }
        return color;
    }

    @Override
    public String toString() {
        return name;
    }
}
