package ch.giuntini.stadtlandbutzclient.net;

public class ConnectionHolder {

    private static boolean connected;
    private static boolean tested;
    private static boolean receivedUUID;
    private static boolean approved;

    public static void setConnected(boolean connected) {
        ConnectionHolder.connected = connected;
    }

    public static void setTested(boolean tested) {
        ConnectionHolder.tested = tested;
    }

    public static void setReceivedUUID(boolean receivedUUID) {
        ConnectionHolder.receivedUUID = receivedUUID;
    }

    public static void setApproved(boolean approved) {
        ConnectionHolder.approved = approved;
    }

    public static boolean isConnected() {
        return connected;
    }

    public static boolean isApproved() {
        return approved;
    }

    public static boolean isReceivedUUID() {
        return receivedUUID;
    }

    public static boolean isTested() {
        return tested;
    }

    public static boolean checkHash(String hash) {
        return "f3a6c45268729247be27b73f0caaf776ee1fc097e6dcf710affc5d2f78c06433".equals(hash);
    }

    protected static String getHash() {
        return "3cdf6a6dd102367f9881b2fe5b2e082b9e0c5e967021f359adc42f6fab22d123";
    }
}