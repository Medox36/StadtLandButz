package com.example.stadtlandbutzserver.net;

import com.example.stadtlandbutzserver.game.Game;

import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

public class Client {
    private final SenderThread senderThread;
    private final ReceiverThread receiverThread;
    private final Socket socket;
    private String playerName = "";
    private final UUID uuid;
    private int points;

    private final ConnectionHolder connectionHolder;

    public Client(Socket socket) throws IOException {
        this.socket = socket;

        uuid = Game.getNewUUID();
        senderThread = new SenderThread(socket.getOutputStream(), uuid);
        receiverThread = new ReceiverThread(socket.getInputStream(), this);

        connectionHolder = new ConnectionHolder();
        connectionHolder.setConnected(true);
        System.out.println("(origin=client) client connected: " + uuid);
    }

    public Socket getSocket() {
        return socket;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public UUID getUUID() {
        return uuid;
    }

    public int getPoints() {
        return points;
    }

    public void addPoints(int points) {
        this.points += points;
    }

    public ConnectionHolder getConnectionHolder() {
        return connectionHolder;
    }

    public void sendPackage(Package p) {
        senderThread.addPackageToSendStack(p);
    }

    public void closeSocketIfOpen() throws IOException {
        if (!socket.isClosed()) {
            senderThread.closeThread();
            receiverThread.closeThread();
            socket.close();
        }
    }
}