package ch.giuntini.stadtlandbutz_server.net.connection;

import ch.giuntini.stadtlandbutz_server.game.Game;

import java.io.IOException;
import java.net.ServerSocket;

public class HostConnection extends Thread {

    private ServerSocket serverSocket;

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

    public void close() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
