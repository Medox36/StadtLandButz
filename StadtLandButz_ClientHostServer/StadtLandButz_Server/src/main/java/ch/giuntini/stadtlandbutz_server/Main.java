package ch.giuntini.stadtlandbutz_server;

import ch.giuntini.stadtlandbutz_server.game.Game;

import java.io.ObjectInputFilter;

public class Main {

    public static void main(String[] args) {
        ObjectInputFilter.Config.setSerialFilter(
                ObjectInputFilter.Config.createFilter("ch.giuntini.stadtlandbutz_package.Package"));
        Game.setPw(String.valueOf(System.console().readPassword("Define Password: ")));
        Game.startServer();
    }
}
