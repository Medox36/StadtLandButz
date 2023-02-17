package ch.giuntini.stadtlandbutz_client.net;

import ch.giuntini.stadtlandbutz_package.Package;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Objects;
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

    public void createConnection(List<String> args) throws IOException {
        String url = "";
        if (!args.isEmpty()) {
            if (args.size() == 2) {
                if (Objects.equals(args.get(0), "-s")) {
                    url = args.get(1);
                }
            }
        } else {
            url = "giuntini-ch.dynv6.net";
        }
        socket = new Socket(url, 24452);
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
