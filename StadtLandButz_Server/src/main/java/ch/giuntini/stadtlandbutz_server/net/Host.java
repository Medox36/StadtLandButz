package ch.giuntini.stadtlandbutz_server.net;

import ch.giuntini.stadtlandbutz_package.Package;

import java.io.IOException;
import java.net.Socket;

public class Host {
    private final Socket socket;
    private final SenderThread senderThread;
    private final ReceiverThread receiverThread;

    public Host(Socket socket) throws IOException {
        this.socket = socket;
        senderThread = new SenderThread(socket.getOutputStream(), this);
        senderThread.start();
        receiverThread = new ReceiverThread(socket.getInputStream(), this);
        receiverThread.start();
    }

    public void sendPackage(Package p) {
        senderThread.addPackageToSendStack(p);
    }

    public synchronized void stop() {
        try {
            if (!socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        senderThread.closeThread();
        receiverThread.closeThread();
    }

    public void disconnectOnException() {
        stop();
        System.out.println("(host disconnected)");
    }
}
