package com.example.stadtlandbutzserver.net;

public class ServerNetInterpreter {

    public static void interpret(Package p) {
        switch (p.prefix) {
            case "0000":
                testingConnection(p);
                break;
            case "0010":
                sendingPlayerName(p);
                break;
            case "0111":
                clientWordsOfCurrRound(p);
                break;
            case "1000":
                clientPointsOfCurrRound(p);
            default:
                invalidPackage();
        }
    }

    private static void testingConnection(Package p) {

    }

    private static void sendingPlayerName(Package p) {

    }

    private static void clientWordsOfCurrRound(Package p) {

    }

    private static void clientPointsOfCurrRound(Package p) {

    }

    private static void invalidPackage() {

    }
}