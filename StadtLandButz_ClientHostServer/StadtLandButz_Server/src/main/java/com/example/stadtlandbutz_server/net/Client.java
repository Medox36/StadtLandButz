package com.example.stadtlandbutz_server.net;

import com.example.stadtlandbutz_server.game.Game;

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
        senderThread = new SenderThread(socket.getOutputStream());
        senderThread.start();
        receiverThread = new ReceiverThread(socket.getInputStream());
        receiverThread.start();

        connectionHolder = new ConnectionHolder();
        //connectionHolder.setConnected(true);
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

    public String getUUIDString() {
        return uuid.toString();
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

    public synchronized void sendPackage(Package p) {
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