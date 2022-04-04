package com.example.stadtlandbutzserver.net;

import com.example.stadtlandbutzserver.game.Game;

import java.io.IOException;
import java.util.Iterator;

public class ServerNetInterpreter {

    public static void interpretForClient(Package p, Client client) {
        if (client.getUUID().equals(p.id)) {
            switch (p.prefix) {
                case "0000":
                    testingConnection(client);
                    break;
                case "0010":
                    sendingPlayerName(p, client);
                    break;
                case "0111":
                    clientWordsOfCurrRound(p, client);
                    break;
                case "1000":
                    clientPointsOfCurrRound(p, client);
                    break;
                case "1111":
                    checkHash(p, client);
                    break;
                default:
                    invalidPackage();
            }
        } else {
            switch (p.prefix) {
                case "0000":
                    testingConnection(client);
                    break;
                case "1111":
                    checkHash(p, client);
                    break;
                default:
                    invalidPackage();
            }
        }
    }

    private static void testingConnection(Client client) {
        client.sendPackage(new Package("0000", "", null));
        client.getConnectionHolder().setTested(true);
    }

    private static void sendingPlayerName(Package p, Client client) {
        client.setPlayerName(p.information);
        Game.addClientToGUI(client);

        StringBuilder sb = new StringBuilder();
        Iterator<String> it = Game.getCategories().iterator();
        while (true) {
            String e = it.next();
            sb.append(e);
            if (it.hasNext()) {
                sb.append(',');
            } else {
                break;
            }
        }
        client.sendPackage(new Package("0011", sb.toString(), client.getUUID()));
    }

    private static void clientWordsOfCurrRound(Package p, Client client) {

    }

    private static void clientPointsOfCurrRound(Package p, Client client) {

    }

    private static void checkHash(Package p, Client client) {
        ConnectionHolder connectionHolder = client.getConnectionHolder();
        if (connectionHolder.checkHash(p.information)) {
            connectionHolder.setApproved(true);
            client.sendPackage(new Package("1111", connectionHolder.getHash(), null));
            client.sendPackage(new Package("0001", client.getUUID().toString(), client.getUUID()));
        } else {
            // close socket for security reason
            try {
                client.getSocket().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void invalidPackage() {

    }
}