package com.example.stadtlandbutzserver.net;

public class ConnectionHolder {

    private boolean connected;
    private boolean testedSent;
    private boolean testedReceived;
    private boolean approved;

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public void setTesteSent(boolean testeSent) {
        this.testedSent = testeSent;
    }

    public void setTestedReceived(boolean testedReceived) {
        this.testedReceived = testedReceived;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public boolean isConnected() {
        return connected;
    }

    public boolean isApproved() {
        return approved;
    }

    public boolean isTestedSent() {
        return testedSent;
    }

    public boolean isTestedReceived() {
        return testedReceived;
    }

    public boolean checkHash(String hash) {
        return "3cdf6a6dd102367f9881b2fe5b2e082b9e0c5e967021f359adc42f6fab22d123".equals(hash);
    }

    protected String getHash() {
        return "f3a6c45268729247be27b73f0caaf776ee1fc097e6dcf710affc5d2f78c06433";
    }
}
