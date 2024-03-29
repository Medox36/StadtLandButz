package ch.giuntini.stadtlandbutz_client.gui;

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
        fillArray();
        editable = new SimpleBooleanProperty(false);
    }

    private void fillArray() {
        for (int i = 0; i < categories.length; i++) {
            categories[i] = new SimpleStringProperty("");
        }
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
