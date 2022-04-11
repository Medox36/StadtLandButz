package com.example.stadtlandbutzclient.gui;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

public class Row {
    private Character letter;

    private final SimpleStringProperty[] cats;

    private final BooleanProperty editable;

    public Row(Character letter) {
        this.letter = letter;
        cats = new SimpleStringProperty[10];
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

    public SimpleStringProperty getCat(int index) {
        return cats[index];
    }

    public void setCat(String newValue, int index) {
        cats[index] = new SimpleStringProperty(newValue);
    }
}
