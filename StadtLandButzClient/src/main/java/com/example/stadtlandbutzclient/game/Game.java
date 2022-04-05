package com.example.stadtlandbutzclient.game;

import com.example.stadtlandbutzclient.gui.Row;
import com.example.stadtlandbutzclient.net.Client;
import com.example.stadtlandbutzclient.gui.ClientGUI;
import com.example.stadtlandbutzclient.net.ConnectionHolder;
import com.example.stadtlandbutzclient.net.Package;
import javafx.application.Platform;

import java.io.IOException;
import java.util.ArrayList;

public class Game {

    private static ArrayList<String> categories = new ArrayList<>();
    private static Client client;
    private static ClientGUI gui;
    private static int roundNumber = -1;
    private static boolean editAllowed;

    public static void newGame(boolean isExit) {
        if (client != null) client.exit();
        client = null;
        categories.clear();
        roundNumber = -1;
        editAllowed = false;
        if (!isExit) Platform.runLater(() -> Game.getGui().joinStage());
    }

    public static boolean createClient(String ip, Integer port, String playerName) {
        client = new Client(playerName);
        try {
            client.createConnection(ip, port);
            ConnectionHolder.setConnected(true);
            client.sendPackage(new Package("0000", "", null));
            ConnectionHolder.setTestedSent(true);
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

    public static String collectWordsOfCurrentRound() {
        Row row = gui.getCurrentRow(roundNumber);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < categories.size(); i++) {
            if (i == 0) {
                sb.append(row.getCat0());
            }
            if (i == 1) {
                sb.append(row.getCat1());
            }
            if (i == 2) {
                sb.append(row.getCat2());
            }
            if (i == 3) {
                sb.append(row.getCat3());
            }
            if (i == 4) {
                sb.append(row.getCat4());
            }
            if (i == 5) {
                sb.append(row.getCat5());
            }
            if (i == 6) {
                sb.append(row.getCat6());
            }
            if (i == 7) {
                sb.append(row.getCat7());
            }
            if (i == 8) {
                sb.append(row.getCat8());
            }
            if (i == 9) {
                sb.append(row.getCat9());
            }
        }

        return sb.toString();
    }

    public static void exit(boolean explicit) {
        Thread t = new Thread(() -> exiting(explicit), "Exit Thread");
        t.setPriority(10);
        t.start();
    }

    private static void exiting(boolean explicit) {
        newGame(true);
        Platform.exit();
        gui = null;
        categories = null;
        System.gc();
        if (explicit) System.exit(3);
    }
}