package ch.giuntini.stadtlandbutz_package;

import java.io.Serializable;

public class Package implements Serializable {
    public String serverPrefix;
    public String prefix;
    public String information;
    public String uuid;

    public Package(String serverPrefix, String prefix, String information, String uuid) {
        this.serverPrefix = serverPrefix;
        this.prefix = prefix;
        this.information = information;
        this.uuid = uuid;
    }
}
