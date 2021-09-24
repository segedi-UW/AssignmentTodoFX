package com.todo.assignmenttodofx;

import javafx.scene.paint.Color;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class CategoryParser implements Filer.Parsable<Category>, Filer.Savable<Category> {

    @Override
    public void save(FileWriter writer, Collection<Category> categories) throws IOException {
        for (Category category : categories) {
            if (!category.isUser())
                continue;
            String name = category.getName();
            Color color = category.getColor();
            double r, g, b;
            r = color.getRed();
            g = color.getGreen();
            b = color.getBlue();
            writer.append(name);
            writer.append(',');
            writer.append(String.valueOf(r));
            writer.append(',');
            writer.append(String.valueOf(g));
            writer.append(',');
            writer.append(String.valueOf(b));
            writer.append("\n");
        }
    }

    @Override
    public List<Category> parse(Iterator<String> lines) {
        List<Category> list = new ArrayList<>();
        lines.forEachRemaining(line -> {
            try {
                String[] split = line.split(",");
                Category category = new Category(split[0]);
                double r, g, b;
                r = Double.parseDouble(split[1]);
                g = Double.parseDouble(split[2]);
                b = Double.parseDouble(split[3]);
                Color color = new Color(r, g, b, 1.0);
                category.setColor(color);
                list.add(category);
            } catch (Exception e) {
                System.err.println("Category not parsable - skipping: " + e.getMessage());
            }
        });
        return list;
    }

}
