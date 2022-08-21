package ch.giuntini.stadtlandbutz_client.net;

import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

public class Client {
    private SenderThread senderThread;
    private ReceiverThread receiverThread;
    private Socket socket;
    private final String playerName;
    private UUID uuid;
    private int points;

    public Client(String playerName) {
        this.playerName = playerName;
    }

    public void createConnection() throws IOException {
        socket = new Socket("giuntini-ch.dynv6.net", 24452);
        senderThread = new SenderThread(socket.getOutputStream());
        senderThread.start();
        receiverThread = new ReceiverThread(socket.getInputStream());
        receiverThread.start();
    }

    public Socket getSocket() {
        return socket;
    }

    public String getPlayerName() {
        return playerName;
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getUUIDString() {
        return uuid.toString();
    }

    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    public int getPoints() {
        return points;
    }

    public void addPoints(int points) {
        this.points += points;
    }

    public void sendPackage(Package p) {
        senderThread.addPackageToSendStack(p);
    }

    public void exit() {
        if (socket != null) {
            try {
                if (!socket.isClosed()) {
                    senderThread.closeThread();
                    receiverThread.closeThread();
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}