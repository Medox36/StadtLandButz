package com.example.stadtlandbutzhost.game;

import com.example.stadtlandbutzhost.net.Client;
import com.example.stadtlandbutzhost.gui.HostGUI;
import com.example.stadtlandbutzhost.net.Package;
import javafx.application.Platform;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.UUID;
import java.util.Vector;

public class Game {
    private static ArrayList<Character> letters = new ArrayList<>();
    private static ArrayList<String> categories = new ArrayList<>();
    private static Vector<Client> clients = new Vector<>();
    private static Vector<UUID> uuids = new Vector<>();
    private static Host host;
    private static String serverPort;
    private static Thread serverThread;
    private static HostGUI gui;
    private static int roundNumber = -1;

    public static void startHost() {
        if (host == null) {
            host = new Host();
            serverPort = host.getPort();
            serverThread = new Thread(() -> host.start());
            serverThread.start();
        }
    }

    public static void stopHost() {
        if (host != null && !host.isClosed()) host.exit();
        if (serverThread != null) serverThread.interrupt();
        serverThread = null;
        host = null;
        serverPort = null;
        closeAllSockets();
        clients = null;
        uuids.clear();
        letters.clear();
        categories.clear();
        roundNumber = -1;
    }

    public static String getServerPort() {
        return serverPort;
    }

    public static Host getHost() {
        return host;
    }

    public static void setGui(HostGUI gui) {
        Game.gui = gui;
    }

    public static void addClientToGUI(Client client) {
        Platform.runLater(() -> gui.addPlayer(client));
    }

    public static ArrayList<String> getCategories() {
        return categories;
    }

    public static Vector<Client> getClients() {
        return clients;
    }

    public static Client getClientByUUID(UUID uuid) {
        for (Client client : clients) {
            if (client.getUUID().equals(uuid)) {
                return client;
            }
        }
        return null;
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

    public synchronized static void sendToAllClients(String prefix, String information) {
        for (Client client : clients) {
            client.sendPackage(new Package(prefix, information, client.getUUID()));
        }
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

    private static void closeAllSockets() {
        for (Client client : clients) {
            try {
                client.closeSocketIfOpen();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void exit(boolean explicit) {
        Thread t = new Thread(() -> exiting(explicit), "Exit Thread");
        t.setPriority(10);
        t.start();
    }

    private static void exiting(boolean explicit) {
        stopHost();
        Platform.exit();
        gui = null;
        uuids = null;
        letters = null;
        categories = null;
        System.gc();
        if (explicit) System.exit(3);
    }
}