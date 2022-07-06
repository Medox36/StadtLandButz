package ch.giuntini.stadtlandbutz_client.net;

import ch.giuntini.stadtlandbutz_client.game.Game;

import javafx.application.Platform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class ClientNetInterpreter {

    public synchronized static void interpret(Package p) {
        switch (p.prefix) {
            case "0000":
                testingConnection();
                break;
            case "0001":
                receivingUUID(p);
                break;
            case "0010":
                requestingName();
                break;
            case "0011":
                receivingCategories(p);
                break;
            case "0100":
                roundNumberAndLetter(p);
                break;
            case "0101":
                enableUserInput();
                break;
            case "0110":
                blockUserInput();
                break;
            case "1000":
                madePointsInRound(p);
                break;
            case "1001":
                placeOnScoreboard(p);
                break;
            case "1010":
                startGame();
                break;
            case "1110":
                checkGameCode(p);
        }
    }

    private static void testingConnection() {
        Game.getClient().sendPackage(new Package("000", "0000", "", null));
    }

    private static void receivingUUID(Package p) {
        Game.getClient().setUUID(UUID.fromString(p.information));
        Game.getClient().sendPackage(new Package("000", "1110", String.valueOf(Game.getGameCode()), Game.getClient().getUUIDString()));
    }

    private static void requestingName() {
        Client client = Game.getClient();
        client.sendPackage(new Package("100", "0010", client.getPlayerName(), client.getUUIDString()));
    }

    private static void receivingCategories(Package p) {
        Game.setCategories(new ArrayList<>(Arrays.asList(p.information.split(","))));
    }

    private static void roundNumberAndLetter(Package p) {
        String[] str = p.information.split("@");

        Game.setRoundNumber(Integer.parseInt(str[0]));
        Game.getGui().addRound(str[1].charAt(0));
    }

    private static void enableUserInput() {
        Game.allowEditInCurrRow(true);
    }

    private static void blockUserInput() {
        Game.allowEditInCurrRow(false);
        Game.getClient().sendPackage(new Package("000", "0111", Game.collectWordsOfCurrentRound(), Game.getClient().getUUID().toString()));
    }

    private static void madePointsInRound(Package p) {
        String[] str = p.information.split("@");
        Game.getClient().addPoints(Integer.parseInt(str[0]));
        Game.getGui().setMadePointsInRound(Integer.parseInt(str[0]), Integer.parseInt(str[1]));
    }

    private static void placeOnScoreboard(Package p) {
        Game.getGui().resultStage(p.information);
    }

    private static void startGame() {
        Platform.runLater(() -> Game.getGui().gameStage());
    }

    private synchronized static void checkGameCode(Package p) {
        if (p.information.equals("1")) {
            Platform.runLater(() -> Game.getGui().waitStage());
            Game.getClient().sendPackage(new Package("100", "0010", Game.getClient().getPlayerName(), Game.getClient().getUUIDString()));
        }
    }
}