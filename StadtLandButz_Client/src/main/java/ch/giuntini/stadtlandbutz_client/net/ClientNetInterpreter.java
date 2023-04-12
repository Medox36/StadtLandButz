package ch.giuntini.stadtlandbutz_client.net;

import ch.giuntini.stadtlandbutz_client.game.Game;
import ch.giuntini.stadtlandbutz_package.Package;

import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class ClientNetInterpreter {

    public static void interpret(Package p) {
        switch (p.prefix) {
            case "0000":
                testingConnection();
                break;
            case "0001":
                receivingUUID(p);
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
        Game.getClient().setUUID(UUID.fromString(p.uuid));
        Game.getClient().sendPackage(new Package("000", "1110", String.valueOf(Game.getGameCode()), Game.getClient().getUUIDString()));
    }

    private static void receivingCategories(Package p) {
        Game.setCategories(new ArrayList<>(Arrays.asList(p.information.split(","))));
    }

    private synchronized static void roundNumberAndLetter(Package p) {
        String[] str = p.information.split("@");

        Game.setRoundNumber(Integer.parseInt(str[0]));
        Game.getGui().addRound(str[1].charAt(0));
    }

    private synchronized static void enableUserInput() {
        Game.allowEditInCurrRow(true);
    }

    private synchronized static void blockUserInput() {
        Game.allowEditInCurrRow(false);
        Game.getClient().sendPackage(new Package("100", "0111", Game.collectWordsOfCurrentRound(), Game.getClient().getUUID().toString()));
    }

    private synchronized static void madePointsInRound(Package p) {
        String[] str = p.information.split("@");
        Game.getClient().addPoints(Integer.parseInt(str[0]));
        Game.getGui().setMadePointsInRound(Integer.parseInt(str[0]), Integer.parseInt(str[1]));
    }

    private static void placeOnScoreboard(Package p) {
        Platform.runLater(() -> Game.getGui().resultStage(p.information));
    }

    private static void startGame() {
        Platform.runLater(() -> Game.getGui().gameStage());
    }

    private synchronized static void checkGameCode(Package p) {
        if (p.information.equals("1")) {
            Platform.runLater(() -> Game.getGui().waitStage());
            Game.getClient().sendPackage(new Package("100", "0010", Game.getClient().getPlayerName(), Game.getClient().getUUIDString()));
        } else {
            Game.getClient().exit();
            Platform.runLater(() -> {
                Alert a = new Alert(Alert.AlertType.WARNING);
                a.setTitle("Game code nicht existent");
                a.setHeaderText(p.information);
                a.showAndWait();
            });
        }
    }
}
