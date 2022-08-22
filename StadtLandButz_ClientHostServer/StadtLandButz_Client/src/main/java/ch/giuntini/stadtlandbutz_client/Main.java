package ch.giuntini.stadtlandbutz_client;

import ch.giuntini.stadtlandbutz_client.gui.ClientGUI;
import javafx.application.Application;

import java.io.ObjectInputFilter;

public class Main {

    public static void main(String[] args) {
        ObjectInputFilter.Config.setSerialFilter(
                ObjectInputFilter.Config.createFilter("ch.giuntini.stadtlandbutz_package.Package"));
        Application.launch(ClientGUI.class);

    }
}
