package com.todo.assignmenttodofx;

import javafx.geometry.Insets;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class ColorSampler {

    public static Region getSample(Color color) {
        Region region = new Region();
        region.setMinSize(10, 10);
        CornerRadii circle = new CornerRadii(5);
        region.setBackground(new Background(new BackgroundFill(color, circle, Insets.EMPTY)));
        region.setBorder(new Border(
                new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, circle, BorderWidths.DEFAULT)));
        return region;
    }
}
