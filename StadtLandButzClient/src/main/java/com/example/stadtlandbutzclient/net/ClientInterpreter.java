package com.example.stadtlandbutzclient.net;

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
            default:
                invalidPackage();
        }
    }

    private static void testingConnection(Package p) {

    }

    private static void receivingId(Package p) {

    }

    private static void receivingCategories(Package p) {

    }

    private static void roundNumberAndLetter(Package p) {

    }

    private static void enableUserInput() {

    }

    private static void blockUserInput() {

    }

    private static void madePointsInRound(Package p) {

    }

    private static void invalidPackage() {

    }
}