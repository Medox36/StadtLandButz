package com.example.stadtlandbutzserver;

import com.example.stadtlandbutzserver.game.Game;

import java.net.Socket;
import java.util.UUID;

public class Client {
    private final Socket socket;
    private String playerName = "";
    private final UUID uuid;
    private int points;

    public Client(Socket socket) {
        this.socket = socket;

        uuid = Game.getNewUUID();
        //wait for Data of Client
        Game.addClientToGUI(this);
        System.out.println("(origin=client) client connected");
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
}