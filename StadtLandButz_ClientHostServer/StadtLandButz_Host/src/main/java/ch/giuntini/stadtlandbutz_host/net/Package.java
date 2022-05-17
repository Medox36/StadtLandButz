package ch.giuntini.stadtlandbutz_host.net;

import java.io.Serializable;

public class Package implements Serializable {
    String serverPrefix;
    String prefix;
    String information;
    String uuid;

    public Package(String serverPrefix, String prefix, String information, String uuid) {
        this.serverPrefix = serverPrefix;
        this.prefix = prefix;
        this.information = information;
        this.uuid = uuid;
    }
}