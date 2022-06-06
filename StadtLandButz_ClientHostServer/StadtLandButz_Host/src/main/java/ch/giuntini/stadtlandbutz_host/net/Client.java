package ch.giuntini.stadtlandbutz_host.net;

import java.io.IOException;
import java.util.UUID;

public class Client {
    private String playerName = "";
    private final UUID uuid;
    private int points;

    public Client(UUID uuid) {
        this.uuid = uuid;
    }


    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public UUID getUUID() {
        return uuid;
    }

    public int getPoints() {
        return points;
    }

    public void addPoints(int points) {
        this.points += points;
    }

    public synchronized void sendPackage(Package p) {

    }

}