package ch.giuntini.stadtlandbutz_host;

import ch.giuntini.stadtlandbutz_host.gui.HostGUI;
import javafx.application.Application;

import java.io.ObjectInputFilter;

public class Main {

    public static void main(String[] args) {
        ObjectInputFilter.Config.setSerialFilter(
                ObjectInputFilter.Config.createFilter("ch.giuntini.stadtlandbutz_package.Package"));
        Application.launch(HostGUI.class);

    }
}
