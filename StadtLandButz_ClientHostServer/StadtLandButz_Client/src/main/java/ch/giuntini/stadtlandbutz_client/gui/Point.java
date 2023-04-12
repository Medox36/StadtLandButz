package ch.giuntini.stadtlandbutz_client.gui;

import javafx.beans.property.SimpleIntegerProperty;

public class Point {
    private final SimpleIntegerProperty point = new SimpleIntegerProperty();

    public Point() {
    }

    public Point(int point) {
        this.point.setValue(point);
    }

    public Integer getPoint() {
        return point.getValue();
    }

    public void setPoint(Integer point) {
        this.point.setValue(point);
    }
}
