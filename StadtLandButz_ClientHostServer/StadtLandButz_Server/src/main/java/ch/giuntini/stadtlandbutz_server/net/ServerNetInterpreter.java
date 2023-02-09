package ch.giuntini.stadtlandbutz_server.net;

import ch.giuntini.stadtlandbutz_package.Package;
import ch.giuntini.stadtlandbutz_server.game.Game;

public class ServerNetInterpreter {

    public synchronized static void interpret(Package p) {
        switch (p.serverPrefix) {
            case "001":
                // for the server from the host
                interpretSH(p);
                break;
            case "010":
                // send to specific client from the host
                interpretCH(p);
                break;
            case "011":
                // send to all clients from the host
                interpretACH(p);
                break;
            default:

        }
    }

    public synchronized static void interpretFromClient(Package p, Client c) {
        switch (p.serverPrefix) {
            case "000":
                // for the server from a client
                interpretSC(p, c);
                break;
            case "100":
                // send to the host from a client
                interpretHC(p);
                break;
        }
    }

    private synchronized static void interpretSC(Package p, Client c) {
        if (p.prefix.equals("1110")) {
            checkGameCode(p, c);
        }
    }

    private synchronized static void checkGameCode(Package p, Client c) {
        if (p.information.equals(String.valueOf(Game.getGameCode()))) {
            Game.acceptClient(c);
            c.sendPackage(new Package("", "1110", "1", c.getUUIDString()));
        } else {
            c.sendPackage(new Package("", "1110", "Kein aktives Spiel", c.getUUIDString()));
            Game.removeWaitingClient(c);
        }
    }

    private synchronized static void interpretSH(Package p) {
        switch (p.prefix) {
            case "1011":
                checkPw(p);
                break;
            case "1100":
                generateGameCode();
                break;
        }
    }

    private synchronized static void checkPw(Package p) {
        if (!Game.getPw().equals(p.information)) {
            Game.getHost().sendPackage(new Package("", "1101", "Falsches Password", null));
            Game.closeHost();
        } else {
            Game.getHost().sendPackage(new Package("", "1101", "1", null));
        }
    }

    private synchronized static void generateGameCode() {
        Game.generateNewGameCode();
        Game.getHost().sendPackage(new Package("", "1110", String.valueOf(Game.getGameCode()), null));
    }

    private synchronized static void interpretCH(Package p) {
        Client client = Game.getClientByUUID(p.uuid);
        if (client != null) {
            client.sendPackage(p);
        }
    }

    private synchronized static void interpretACH(Package p) {
        Game.sendToAllClients(p.prefix, p.information);
    }

    private synchronized static void interpretHC(Package p) {
        Game.getHost().sendPackage(p);
    }

}
