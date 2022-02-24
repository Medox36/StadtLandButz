package com.example.stadtlandbutzclient;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.UUID;

public class Client {
    private Socket socket;
    private final String playerName;
    private UUID uuid;
    private int points;

    public Client(String playerName) {
        this.playerName = playerName;
    }

    public void createConnection(String ip, Integer port) throws IOException {
        socket = new Socket(InetAddress.getByName(ip), port);
    }

    public Socket getSocket() {
        return socket;
    }

    public String getPlayerName() {
        return playerName;
    }

    public UUID getUUID() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public int getPoints() {
        return points;
    }

    public void addPoints(int points) {
        this.points += points;
    }

    public void exit() {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}