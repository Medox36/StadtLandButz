package ch.giuntini.stadtlandbutzclient;

import ch.giuntini.stadtlandbutzclient.gui.ClientGUI;
import javafx.application.Application;

import java.io.ObjectInputFilter;

public class Main {

    public static void main(String[] args) {
        ObjectInputFilter.Config.setSerialFilter(
                ObjectInputFilter.Config.createFilter("ch.giuntini.stadtlandbutzhost.net.Package"));
        Application.launch(ClientGUI.class);

    }
}