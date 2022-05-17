package ch.giuntini.stadtlandbutzclient.net;

import ch.giuntini.stadtlandbutzclient.game.Game;
import javafx.application.Platform;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public class ClientInterpreter {

    public synchronized static void interpret(Package p) {
        if (ConnectionHolder.isReceivedUUID()) {
            if (p.id.equals(Game.getClient().getUUID())) {
                switch (p.prefix) {
                    case "0000":
                        testingConnection();
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
                    case "1111":
                        checkHash(p);
                        break;
                    default:
                        invalidPackage();
                }
            }
        } else {
            switch (p.prefix) {
                case "0000":
                    testingConnection();
                    break;
                case "0001":
                    receivingUUID(p);
                    break;
                case "1111":
                    checkHash(p);
                    break;
                default:
                    invalidPackage();
            }
        }
    }

    private static void testingConnection() {
        ConnectionHolder.setTested(true);
        Game.getClient().sendPackage(new Package("0000", "", null));
        Game.getClient().sendPackage(new Package("1111", ConnectionHolder.getHash(), null));
    }

    private static void receivingUUID(Package p) {
        if (!Objects.equals(p.information, p.id.toString())) {
            // TODO display Errormessage or close
        }
        Game.getClient().setUUID(UUID.fromString(p.information));
        ConnectionHolder.setReceivedUUID(true);
        Game.getClient().sendPackage(new Package("0010", Game.getClient().getPlayerName(), Game.getClient().getUUID()));
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
        Game.getClient().sendPackage(new Package("0111", Game.collectWordsOfCurrentRound(),Game.getClient().getUUID()));
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

    private static void checkHash(Package p) {
        if (ConnectionHolder.checkHash(p.information)) {
            ConnectionHolder.setApproved(true);
        } else {
            // close socket for security reason
            try {
                Game.getClient().getSocket().close();
                Game.newGame(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void invalidPackage() {

    }
}