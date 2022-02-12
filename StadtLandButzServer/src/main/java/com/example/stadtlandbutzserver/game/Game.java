package com.example.stadtlandbutzserver.game;

import com.example.stadtlandbutzserver.Client;
import com.example.stadtlandbutzserver.gui.ServerGUI;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.Vector;

public class Game {
    private static ArrayList<String> categories = new ArrayList<>();
    private static Vector<Client> clients = new Vector<>();
    private static Server server;
    private static String serverPort;
    private static ServerGUI gui;
    private static int roundNumber;

    public static void startServer() {
        if (server == null) {
            server = new Server();
            serverPort = server.getPort();
            new Thread(() -> server.start()).start();
        }
    }

    public static String getServerPort() {
        return serverPort;
    }

    public static Server getServer() {
        return server;
    }

    public static void setGui(ServerGUI gui) {
        Game.gui = gui;
    }

    public static void addClientToGUI(Client client) {
        gui.addPlayer(client);
    }

    public static void setCategories(ArrayList<String> categories) {
        Game.categories = categories;
    }

    public static ArrayList<String> getCategories() {
        return categories;
    }

    public static Vector<Client> getClients() {
        return clients;
    }

    public static void addClient(Client client) {
        clients.add(client);
    }

    public static void removeClient(Client client) {
        clients.remove(client);
    }

    public static void incRoundNumber() {
        roundNumber++;
    }

    public static int getRoundNumber() {
        return roundNumber;
    }

    public static void exit() {
        categories = null;
        clients = null;
        server = null;
        serverPort = null;
        Platform.exit();
        System.gc();
        System.exit(0);
    }
}