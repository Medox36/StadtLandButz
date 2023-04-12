package ch.giuntini.stadtlandbutz_server.net;

import ch.giuntini.stadtlandbutz_package.Package;
import ch.giuntini.stadtlandbutz_server.game.Game;

import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

public class Client {
    private final ClientSenderThread senderThread;
    private final ClientReceiverThread clientReceiverThread;
    private final Socket socket;
    private final UUID uuid;

    public Client(Socket socket) throws IOException {
        this.socket = socket;

        uuid = Game.getNewUUID();
        senderThread = new ClientSenderThread(socket.getOutputStream(), this);
        senderThread.start();
        clientReceiverThread = new ClientReceiverThread(socket.getInputStream(), this);
        clientReceiverThread.start();

        sendPackage(new Package("", "0001", "", uuid.toString()));
        System.out.println("client connected: " + uuid);
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getUUIDString() {
        return uuid.toString();
    }

    public void sendPackage(Package p) {
        senderThread.addPackageToSendStack(p);
    }

    public void disconnectOnException() {
        Game.getHost().sendPackage(new Package("", "1111", "", getUUIDString()));
        close();
        System.out.println("client disconnected: " + uuid);
    }

    public void disconnectOnRefusal() {
        close();
        System.out.println("client refused: " + uuid);
    }

    private synchronized void close() {
        if (!socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        senderThread.closeThread();
        clientReceiverThread.closeThread();
    }
}
