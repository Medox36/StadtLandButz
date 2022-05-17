package ch.giuntini.stadtlandbutzclient.net;

import java.io.Serializable;
import java.util.UUID;

public class Package implements Serializable {
    String prefix;
    String information;
    UUID id;

    public Package(String prefix, String information, UUID id) {
        this.prefix = prefix;
        this.information = information;
        this.id = id;
    }
}