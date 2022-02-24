package com.example.stadtlandbutzserver.game;

import com.example.stadtlandbutzserver.Client;
import com.example.stadtlandbutzserver.gui.ServerGUI;
import javafx.application.Platform;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.UUID;
import java.util.Vector;

public class Game {
    private static ArrayList<Character> letters = new ArrayList<>();
    private static ArrayList<String> categories = new ArrayList<>();
    private static Vector<Client> clients = new Vector<>();
    private static Vector<UUID> uuids = new Vector<>();
    private static Server server;
    private static String serverPort;
    private static Thread serverThread;
    private static ServerGUI gui;
    private static int roundNumber = -1;

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
        if (serverThread != null) serverThread.interrupt();
        serverThread = null;
        server = null;
        serverPort = null;
        uuids.clear();
        letters.clear();
        categories.clear();
        roundNumber = -1;
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

    public static ArrayList<String> getCategories() {
        return categories;
    }

    public static Vector<Client> getClients() {
        return clients;
    }

    public static void incRoundNumber() {
        roundNumber++;
    }

    public static int getRoundNumber() {
        return roundNumber;
    }

    public static int getVisualRoundNumber() {
        return roundNumber + 1;
    }

    public static UUID getNewUUID() {
        UUID uuid = UUID.randomUUID();
        while (uuids.contains(uuid)) {
            uuid = UUID.randomUUID();
        }
        uuids.add(uuid);
        return uuid;
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

    public static void exit(boolean explicit) {
        Thread t = new Thread(() -> exiting(explicit), "Exit Thread");
        t.setPriority(10);
        t.start();
    }

    private static void exiting(boolean explicit) {
        stopServer();
        Platform.exit();
        gui = null;
        uuids = null;
        clients = null;
        letters = null;
        categories = null;
        System.gc();
        if (explicit) System.exit(3);
    }
}