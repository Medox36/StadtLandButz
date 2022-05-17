package ch.giuntini.stadtlandbutz_server.net.connection;

import ch.giuntini.stadtlandbutz_server.game.Game;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class ClientConnection extends Thread {
    private boolean await;
    private ServerSocket serverSocket;

    public ClientConnection() {
        try {
            serverSocket = new ServerSocket(24452, 5);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        await = true;
        await();
    }

    private void await() {
        try {
            serverSocket.setSoTimeout(30000);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        do {
            try {
                Socket socket = serverSocket.accept();
                Game.addClient(socket);
                new Thread(() -> {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //client.sendPackage(new Package("0000", "", null));
                }).start();
            } catch (IOException e) {
                //e.printStackTrace();
            }
        } while (await);
    }

    public void letClientsConnect(boolean await) {
        this.await = await;
    }

    public void exit() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}