package com.example.stadtlandbutzserver.net;

import java.io.Serializable;

public class Package implements Serializable {
    String prefix;
    String information;

    public Package(String prefix, String information) {
        this.prefix = prefix;
        this.information = information;
    }
}