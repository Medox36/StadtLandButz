package ch.giuntini.stadtlandbutz_client.game;

import ch.giuntini.stadtlandbutz_client.gui.ClientGUI;
import ch.giuntini.stadtlandbutz_client.gui.Row;
import ch.giuntini.stadtlandbutz_client.net.Client;

import javafx.application.Platform;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Game {

    private static ArrayList<String> categories = new ArrayList<>();
    private static Client client;
    private static ClientGUI gui;
    private static int gameCode;
    private volatile static int roundNumber = -1;
    private volatile static boolean editAllowed;

    public static void newGame(boolean isExit) {
        if (client != null) client.exit();
        client = null;
        categories.clear();
        roundNumber = -1;
        editAllowed = false;
        if (!isExit) Platform.runLater(() -> Game.getGui().joinStage());
    }

    public static boolean createClient(String playerName, List<String> args) {
        client = new Client(playerName);
        try {
            client.createConnection(args);
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

    public synchronized static void setRoundNumber(int roundNumber) {
        Game.roundNumber = roundNumber;
    }

    public static int getGameCode() {
        return gameCode;
    }

    public static void setGameCode(int gameCode) {
        Game.gameCode = gameCode;
    }

    public static int getRoundNumber() {
        return roundNumber;
    }

    public static int getVisualRoundNumber() {
        return roundNumber + 1;
    }

    public synchronized static void allowEditInCurrRow(boolean editAllowed) {
        Game.editAllowed = editAllowed;
        Platform.runLater(() -> gui.setTableEditable(editAllowed, roundNumber));
    }

    public synchronized static boolean isEditAllowed() {
        return editAllowed;
    }

    public synchronized static String collectWordsOfCurrentRound() {
        Row row = gui.getCurrentRow(roundNumber);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < categories.size(); i++) {
            String word = row.getCategoryString(i).replaceAll("[^-a-zA-Z_0-9 ]", "").trim();
            if (word.isEmpty()) {
                word = "~";
            }
            sb.append(word);
            if (i < (categories.size() - 1)) {
                sb.append(",");
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
