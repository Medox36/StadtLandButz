package ch.giuntini.stadtlandbutz_server;

import ch.giuntini.stadtlandbutz_server.gui.ServerGUI;
import javafx.application.Application;

import java.io.ObjectInputFilter;

public class Main {

    public static void main(String[] args) {
        ObjectInputFilter.Config.setSerialFilter(
                ObjectInputFilter.Config.createFilter("ch.giuntini.stadtlandbutz_client.net.Package;" +
                        "ch.giuntini.stadtlandbutz_host.net.Package"));
        Application.launch(ServerGUI.class);

    }
}