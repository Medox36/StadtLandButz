package com.example.stadtlandbutzhost.game;

import com.example.stadtlandbutzhost.net.Client;
import com.example.stadtlandbutzhost.net.Package;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class Server {
    private boolean await;
    private ServerSocket serverSocket;

    private final int BACKLOG = 5;

    public Server() {
        //TODO implement port changing if server cant run on a specific port
        try {
            serverSocket = new ServerSocket(24452, BACKLOG);
        } catch (IOException e) {
            e.printStackTrace();
            //TODO try running on other ports
            try {
                // letting java search a port
                serverSocket = new ServerSocket(0, BACKLOG);
            } catch (IOException ex) {
                ex.printStackTrace();
                System.out.println("WARN: No Port found for the Server");
            } finally {
                Game.exit(true);
            }
        }
        await = true;
    }

    public void start() {
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
                Client client = new Client(socket);
                Game.getClients().add(client);
                new Thread(() -> {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    client.sendPackage(new Package("0000", "", null));
                    client.getConnectionHolder().setTesteSent(true);
                }).start();
            } catch (IOException e) {
                //e.printStackTrace();
            }
        } while (await);
    }

    public void letClientsConnect(boolean await) {
        this.await = await;
    }

    public boolean isAwait() {
        return await;
    }

    public synchronized String getPort() {
        return String.valueOf(serverSocket.getLocalPort());
    }

    public boolean isClosed() {
        return serverSocket.isClosed();
    }

    public void exit() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}