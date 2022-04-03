package com.example.stadtlandbutzserver.net;

public class ConnectionHolder {

    private boolean connected;
    private boolean tested;
    private boolean approved;

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public void setTested(boolean tested) {
        this.tested = tested;
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

    public boolean isTested() {
        return tested;
    }

    public boolean checkHash(String hash) {
        return "3cdf6a6dd102367f9881b2fe5b2e082b9e0c5e967021f359adc42f6fab22d123".equals(hash);
    }
}
