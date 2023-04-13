package ch.giuntini.stadtlandbutz_host.net;

import ch.giuntini.stadtlandbutz_host.game.Game;
import ch.giuntini.stadtlandbutz_package.Package;

import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.util.UUID;

public class HostNetInterpreter {

    public static void interpret(Package p) {
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
            case "1111":
                disconnect(p);
                break;
        }
    }

    private synchronized static void newClient(Package p) {
        Client client = new Client(UUID.fromString(p.uuid));
        client.setPlayerName(p.information);
        Game.addClient(client);
        Game.addClientToGUI(client);
        Game.getHost().sendPackage(new Package("010", "0011", Game.getCategoryString(), client.getUUID().toString()));
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

    private synchronized static void disconnect(Package p) {
        Client client = Game.getClientByUUID(UUID.fromString(p.uuid));
        Game.removeClientByUUID(UUID.fromString(p.uuid));
        if (client != null) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Spieler");
                alert.setHeaderText("Verbindung zu einem Spieler verloren.");
                alert.setContentText("Name: " + client.getPlayerName() + "\nUUID: " + client.getUUID().toString());
                alert.show();
            });
        }
    }
}
