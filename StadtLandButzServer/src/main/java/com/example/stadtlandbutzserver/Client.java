package com.example.stadtlandbutzserver;

import com.example.stadtlandbutzserver.game.Game;

import java.net.Socket;

public class Client {
    private Socket socket;
    private String playerName = "";

    public Client(Socket socket) {
        this.socket = socket;

        //wait for Data of Client
        Game.addClientToGUI(this);
    }

    public String getPlayerName() {
        return playerName;
    }
}