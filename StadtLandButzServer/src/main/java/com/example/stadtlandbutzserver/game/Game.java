package com.example.stadtlandbutzserver.game;

import com.example.stadtlandbutzserver.Client;
import com.example.stadtlandbutzserver.gui.ServerGUI;
import javafx.application.Platform;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Vector;

public class Game {
    private static final ArrayList<Character> letters = new ArrayList<>();
    private static ArrayList<String> categories = new ArrayList<>();
    private static Vector<Client> clients = new Vector<>();
    private static Server server;
    private static String serverPort;
    private static Thread serverThread;
    private static ServerGUI gui;
    private static int roundNumber;

    public static void startServer() {
        if (server == null) {
            server = new Server();
            serverPort = server.getPort();
            serverThread = new Thread(() -> server.start());
            serverThread.start();
        }
    }

    public static void stopServer() {
        if (server != null && !server.isClosed()) server.exit();
        serverThread.interrupt();
        serverThread = null;
        server = null;
        serverPort = null;
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

    public static String nextLetter() {
        SecureRandom s = new SecureRandom();
        Character letter = (char) (s.nextInt(26) + 'a');
        while (letters.contains(letter)) {
            letter = (char) (s.nextInt(26) + 'a');
            //TODO check if all letters already have been used
        }
        letters.add(letter);
        return String.valueOf(letter).toUpperCase();
    }

    public static void exit() {
        Thread t = new Thread(Game::exiting, "Exit Thread");
        t.setPriority(10);
        t.start();
    }

    private static void exiting() {
        categories = null;
        clients = null;
        stopServer();
        Platform.exit();
        System.gc();
        //System.exit(0);
    }
}