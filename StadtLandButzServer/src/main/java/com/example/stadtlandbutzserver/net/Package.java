package com.example.stadtlandbutzserver.net;

import java.io.Serializable;

public class Package implements Serializable {
    String prefix;
    String information;
    String id;

    public Package(String prefix, String information, String id) {
        this.prefix = prefix;
        this.information = information;
        this.id = id;
    }
}