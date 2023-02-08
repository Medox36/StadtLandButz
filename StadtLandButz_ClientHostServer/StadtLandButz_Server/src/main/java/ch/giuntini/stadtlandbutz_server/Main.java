package ch.giuntini.stadtlandbutz_server;

import ch.giuntini.stadtlandbutz_server.game.Game;

import java.io.ObjectInputFilter;

public class Main {

    public static void main(String[] args) {
        ObjectInputFilter.Config.setSerialFilter(
                ObjectInputFilter.Config.createFilter("ch.giuntini.stadtlandbutz_package.Package"));
        Runtime.getRuntime().addShutdownHook(new Thread(() -> Game.exit(true), "Exit Thread"));
        //Game.setPw(String.valueOf(System.console().readPassword("Define Password: ")));
        Game.setPw("abc");
        Game.startServer();
    }
}
