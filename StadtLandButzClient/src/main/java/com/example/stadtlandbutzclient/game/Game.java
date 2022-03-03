package com.example.stadtlandbutzclient.game;

import com.example.stadtlandbutzclient.Client;
import com.example.stadtlandbutzclient.gui.ClientGUI;
import javafx.application.Platform;

import java.io.IOException;
import java.util.ArrayList;

public class Game {

    private static ArrayList<String> categories = new ArrayList<>();
    private static Client client;
    private static ClientGUI gui;
    private static int roundNumber = -1;
    private static boolean editAllowed;

    public static void newGame() {
        if (client != null) client.exit();
        client = null;
        categories.clear();
        roundNumber = -1;
        editAllowed = false;
    }

    public static boolean createClient(String ip, Integer port, String playerName) {
        client = new Client(playerName);
        try {
            client.createConnection(ip, port);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static Client getClient() {
        return client;
    }

    public static ArrayList<String> getCategories() {
        return categories;
    }

    public static void setCategories(ArrayList<String> categories) {
        Game.categories = categories;
    }

    public static void setGui(ClientGUI gui) {
        Game.gui = gui;
    }

    public static ClientGUI getGui() {
        return gui;
    }

    public static void setRoundNumber(int roundNumber) {
        Game.roundNumber = roundNumber;
    }

    public static int getRoundNumber() {
        return roundNumber;
    }

    public static int getVisualRoundNumber() {
        return roundNumber + 1;
    }

    public static void allowEditInCurrRow(boolean editAllowed) {
        Game.editAllowed = editAllowed;
        Platform.runLater(() -> gui.setTableEditable(editAllowed, roundNumber));
    }

    public static boolean isEditAllowed() {
        return editAllowed;
    }

    public static void exit(boolean explicit) {
        Thread t = new Thread(() -> exiting(explicit), "Exit Thread");
        t.setPriority(10);
        t.start();
    }

    private static void exiting(boolean explicit) {
        newGame();
        Platform.exit();
        gui = null;
        categories = null;
        System.gc();
        if (explicit) System.exit(3);
    }
}