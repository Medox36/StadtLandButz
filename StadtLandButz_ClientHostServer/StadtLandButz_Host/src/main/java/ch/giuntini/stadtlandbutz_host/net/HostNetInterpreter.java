package ch.giuntini.stadtlandbutz_host.net;

import ch.giuntini.stadtlandbutz_host.game.Game;

import javafx.application.Platform;

public class HostNetInterpreter {

    public synchronized static void interpret(Package p) {
        switch (p.prefix) {
            case "1101":
                passwordResponse(p);
                break;
            case "1110":
                gameCode(p);
                break;
        }
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
        Platform.runLater(() -> Game.getGui().setGameCode(p.information));
    }
}