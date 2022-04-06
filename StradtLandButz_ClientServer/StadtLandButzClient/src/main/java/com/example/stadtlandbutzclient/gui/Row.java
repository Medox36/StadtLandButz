package com.example.stadtlandbutzclient.gui;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class Row {
    private Character letter;

    private String cat0;
    private String cat1;
    private String cat2;
    private String cat3;
    private String cat4;
    private String cat5;
    private String cat6;
    private String cat7;
    private String cat8;
    private String cat9;

    private BooleanProperty editable;

    public Row(Character letter) {
        this.letter = letter;
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

    public String getCat0() {
        return cat0;
    }

    public String getCat1() {
        return cat1;
    }

    public String getCat2() {
        return cat2;
    }

    public String getCat3() {
        return cat3;
    }

    public String getCat4() {
        return cat4;
    }

    public String getCat5() {
        return cat5;
    }

    public String getCat6() {
        return cat6;
    }

    public String getCat7() {
        return cat7;
    }

    public String getCat8() {
        return cat8;
    }

    public String getCat9() {
        return cat9;
    }

    public void setCat0(String cat0) {
        this.cat0 = cat0;
    }

    public void setCat1(String cat1) {
        this.cat1 = cat1;
    }

    public void setCat2(String cat2) {
        this.cat2 = cat2;
    }

    public void setCat3(String cat3) {
        this.cat3 = cat3;
    }

    public void setCat4(String cat4) {
        this.cat4 = cat4;
    }

    public void setCat5(String cat5) {
        this.cat5 = cat5;
    }

    public void setCat6(String cat6) {
        this.cat6 = cat6;
    }

    public void setCat7(String cat7) {
        this.cat7 = cat7;
    }

    public void setCat8(String cat8) {
        this.cat8 = cat8;
    }

    public void setCat9(String cat9) {
        this.cat9 = cat9;
    }
}
