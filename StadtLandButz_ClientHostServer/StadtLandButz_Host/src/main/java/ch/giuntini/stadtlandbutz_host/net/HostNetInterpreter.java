package ch.giuntini.stadtlandbutz_host.net;

import ch.giuntini.stadtlandbutz_host.game.Game;
import ch.giuntini.stadtlandbutz_package.Package;

import javafx.application.Platform;

import java.util.UUID;

public class HostNetInterpreter {

    public synchronized static void interpret(Package p) {
        switch (p.prefix) {
            case "0010":
                newClient(p);
                break;
            case "0111":
                addWordsOfClientFromCurrentRound(p);
                break;
            case "1101":
                passwordResponse(p);
                break;
            case "1110":
                gameCode(p);
                break;
        }
    }

    private synchronized static void newClient(Package p) {
        Client client = new Client(UUID.fromString(p.uuid));
        client.setPlayerName(p.information);
        Game.addClient(client);
        Game.addClientToGUI(client);
    }

    private synchronized static void addWordsOfClientFromCurrentRound(Package p) {
        Game.addWordsOfClient(p.information.split(","), UUID.fromString(p.uuid));
    }

    private synchronized static void passwordResponse(Package p) {
        if (p.information.equals("1")) {
            Platform.runLater(() -> Game.getGui().selectionStage());
        } else {
            Platform.runLater(() -> Game.getGui().showPasswordMessage(p.information));
            Game.getHost().stop();
        }
    }

    private synchronized static void gameCode(Package p) {
        Game.setGameCode(Integer.parseInt(p.information));
        Platform.runLater(() -> Game.getGui().setGameCode(String.valueOf(Game.getGameCode())));
    }
}
