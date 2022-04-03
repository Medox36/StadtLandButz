package com.example.stadtlandbutzclient.net;

import com.example.stadtlandbutzclient.game.Game;
import javafx.application.Platform;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class ClientInterpreter {

    public static void interpret(Package p) {
        switch (p.prefix) {
            case "0000":
                testingConnection(p);
                break;
            case "0001":
                receivingId(p);
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
            case "1111":
                checkHash(p);
                break;
            default:
                invalidPackage();
        }
    }

    private static void testingConnection(Package p) {
        //TODO after th socket is connected wait until the server sends back this package
        ConnectionHolder.setTested(true);
    }

    private static void receivingId(Package p) {
        Game.getClient().setUUID(UUID.fromString(p.information));
        ConnectionHolder.setReceivedUUID(true);
    }

    private static void receivingCategories(Package p) {
        Game.setCategories(new ArrayList<>(Arrays.asList(p.information.split(","))));
        Platform.runLater(() -> Game.getGui().gameStage());
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
    }

    private static void madePointsInRound(Package p) {
        String[] str = p.information.split("@");
        Game.getClient().addPoints(Integer.parseInt(str[0]));
        Game.getGui().setMadePointsInRound(Integer.parseInt(str[0]), Integer.parseInt(str[1]));
    }

    private static void placeOnScoreboard(Package p) {
        Game.getGui().resultStage(p.information);
    }

    private static void checkHash(Package p) {
        if (ConnectionHolder.checkHash(p.information)) {
            ConnectionHolder.setApproved(true);
        } else {
            //TODO close socket for security reason
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