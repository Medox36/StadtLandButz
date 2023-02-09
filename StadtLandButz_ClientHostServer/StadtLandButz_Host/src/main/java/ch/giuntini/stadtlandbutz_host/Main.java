package ch.giuntini.stadtlandbutz_host;

import ch.giuntini.stadtlandbutz_host.game.Game;
import ch.giuntini.stadtlandbutz_host.gui.HostGUI;

import javafx.application.Application;

import java.io.ObjectInputFilter;

public class Main {

    public static void main(String[] args) {
        ObjectInputFilter.Config.setSerialFilter(
                ObjectInputFilter.Config.createFilter("ch.giuntini.stadtlandbutz_package.Package"));
        Runtime.getRuntime().addShutdownHook(new Thread(() -> Game.exit(true), "Exit Thread"));
        Application.launch(HostGUI.class);

    }
}
