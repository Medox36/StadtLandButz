package ch.giuntini.stadtlandbutzhost;

import ch.giuntini.stadtlandbutzhost.gui.HostGUI;
import javafx.application.Application;

import java.io.ObjectInputFilter;

public class Main {

    public static void main(String[] args) {
        ObjectInputFilter.Config.setSerialFilter(
                ObjectInputFilter.Config.createFilter("ch.giuntini.stadtlandbutzclient.net.Package"));
        Application.launch(HostGUI.class);

    }
}