package com.example.stadtlandbutzserver.game;

import com.example.stadtlandbutzserver.Client;

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

    public synchronized void start() {
        await = true;
        await();
    }

    private synchronized void await() {
        try {
            serverSocket.setSoTimeout(30000);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        do {
            try {
                Socket socket = serverSocket.accept();
                Game.getClients().add(new Client(socket));
                //TODO implement Client Threads with sockets
            } catch (IOException e) {
                //e.printStackTrace();
            }
        } while (await);
    }

    public synchronized void letClientReconnect() {
        await = true;
        await();
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