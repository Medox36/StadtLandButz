package com.example.stadtlandbutzhost.gui;

import javafx.scene.control.Label;

public class TimeLabel extends Label {

    private int mins;
    private int secs;

    public TimeLabel(int mins, int secs) {
        setTime(String.valueOf(mins), String.valueOf(secs));
        setStyle("-fx-font-family: Gadugi; -fx-font-size: 130; -fx-text-fill: white; -fx-background-color: #2c7973; -fx-alignment: center");
        setMinSize(320, 150);
        setPrefSize(320,150);
        this.mins = mins;
        this.secs = secs;
    }

    private void setTime(String minutes, String seconds) {
        setText(zeroFill(minutes) + ":" + zeroFill(seconds));
    }

    public void incr() {
        if (secs + 1 > 59) {
            mins++;
            secs = 0;
        } else {
            secs++;
        }
        setTime(String.valueOf(mins), String.valueOf(secs));
    }

    private String zeroFill(String val) {
        if (Integer.parseInt(val) < 10)
            return "0" + val;
        else
            return val;
    }
}