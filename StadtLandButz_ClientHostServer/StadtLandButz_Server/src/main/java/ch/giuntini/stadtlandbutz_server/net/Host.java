package ch.giuntini.stadtlandbutz_server.net;

import java.io.IOException;
import java.net.Socket;

public class Host {
    private Socket socket;
    private SenderThread senderThread;
    private ReceiverThread receiverThread;

    public Host(Socket socket) throws IOException {
        this.socket = socket;
        senderThread = new SenderThread(socket.getOutputStream());
        senderThread.start();
        receiverThread = new ReceiverThread(socket.getInputStream());
        receiverThread.start();
    }

    public synchronized void sendPackage(Package p) {
        senderThread.addPackageToSendStack(p);
    }
}