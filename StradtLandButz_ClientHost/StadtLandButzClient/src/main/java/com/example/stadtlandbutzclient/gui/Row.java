package com.example.stadtlandbutzclient.gui;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

public class Row {
    private Character letter;

    private final SimpleStringProperty[] categories;

    private final BooleanProperty editable;

    public Row(Character letter) {
        this.letter = letter;
        categories = new SimpleStringProperty[10];
        editable = new SimpleBooleanProperty(false);
    }

    public Character getLetter() {
        return letter;
    }

    public void setLetter(Character letter) {
        this.letter = letter;
    }

    public void setEditable(boolean editable) {
        this.editable.set(editable);
    }

    public BooleanProperty editableProperty() {
        return editable;
    }

    public SimpleStringProperty getCategoryProperty(int index) {
        return categories[index];
    }

    public String getCategoryString(int index) {
        return categories[index].getValueSafe();
    }

    public void setCat(String newValue, int index) {
        categories[index] = new SimpleStringProperty(newValue);
    }
}
