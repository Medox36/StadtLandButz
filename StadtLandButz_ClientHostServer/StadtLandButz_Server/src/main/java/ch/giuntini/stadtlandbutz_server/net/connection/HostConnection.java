package ch.giuntini.stadtlandbutz_server.net.connection;

import ch.giuntini.stadtlandbutz_server.game.Game;
import ch.giuntini.stadtlandbutz_server.net.Host;

import java.io.IOException;
import java.net.ServerSocket;

public class HostConnection extends Thread {

    private ServerSocket serverSocket;
    private Host host;

    public HostConnection() {
        try {
            serverSocket = new ServerSocket(25541, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            Game.addHost(serverSocket.accept());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}