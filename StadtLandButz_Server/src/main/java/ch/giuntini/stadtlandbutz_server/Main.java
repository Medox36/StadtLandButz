package ch.giuntini.stadtlandbutz_server;

import ch.giuntini.stadtlandbutz_server.game.Game;

import java.io.ObjectInputFilter;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        ObjectInputFilter.Config.setSerialFilter(
                ObjectInputFilter.Config.createFilter("ch.giuntini.stadtlandbutz_package.Package"));
        Runtime.getRuntime().addShutdownHook(new Thread(() -> Game.exit(true), "Exit Thread"));
        if (System.console() != null) {
            Game.setPw(String.valueOf(System.console().readPassword("Define Password: ")));
        } else {
            System.out.print("Define Password: ");
            Game.setPw(new Scanner(System.in).nextLine());
        }
        Game.startServer();
    }
}
