package ch.giuntini.stadtlandbutz_host.game;

import ch.giuntini.stadtlandbutz_host.gui.HostGUI;
import ch.giuntini.stadtlandbutz_host.net.Client;
import ch.giuntini.stadtlandbutz_host.net.Package;
import ch.giuntini.stadtlandbutz_host.net.Host;
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
    private static HostGUI gui;
    private static Host host;
    private static int gameCode;
    private static int roundNumber = -1;

    public static void startHost() throws IOException {
        host = new Host();
    }

    public static void stopHost() {
        clients = null;
        uuids.clear();
        letters.clear();
        categories.clear();
        roundNumber = -1;
    }

    public static void setGui(HostGUI gui) {
        Game.gui = gui;
    }

    public static HostGUI getGui() {
        return gui;
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

    public static int getGameCode() {
        return gameCode;
    }

    public static void setGameCode(int gameCode) {
        Game.gameCode = gameCode;
    }

    public static Host getHost() {
        return host;
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

    public synchronized static void sendToAllClients(String serverPrefix, String prefix, String information) {
        for (Client client : clients) {
            client.sendPackage(new Package(serverPrefix, prefix, information, client.getUUID().toString()));
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