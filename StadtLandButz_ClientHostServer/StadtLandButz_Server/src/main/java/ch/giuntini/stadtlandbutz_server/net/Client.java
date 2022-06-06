package ch.giuntini.stadtlandbutz_server.net;

import ch.giuntini.stadtlandbutz_server.game.Game;

import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

public class Client {
    private final SenderThread senderThread;
    private final ClientReceiverThread clientReceiverThread;
    private final Socket socket;
    private final UUID uuid;

    public Client(Socket socket) throws IOException {
        this.socket = socket;

        uuid = Game.getNewUUID();
        senderThread = new SenderThread(socket.getOutputStream());
        senderThread.start();
        clientReceiverThread = new ClientReceiverThread(socket.getInputStream(), this);
        clientReceiverThread.start();

        //connectionHolder.setConnected(true);
        sendPackage(new Package("", "0001", "", uuid.toString()));
        System.out.println("(origin=client) client connected: " + uuid);
    }

    public Socket getSocket() {
        return socket;
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getUUIDString() {
        return uuid.toString();
    }

    public synchronized void sendPackage(Package p) {
        senderThread.addPackageToSendStack(p);
    }

    public void closeSocketIfOpen() {
        if (!socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        senderThread.closeThread();
        clientReceiverThread.closeThread();
    }
}