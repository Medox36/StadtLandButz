package ch.giuntini.stadtlandbutz_host.net;

import java.io.IOException;
import java.net.Socket;

public class Host {
    private final Socket socket;
    private final SenderThread senderThread;
    private final ReceiverThread receiverThread;

    public Host() throws IOException {
        socket = new Socket("giuntini-ch.dynv6.net", 25541);
        senderThread = new SenderThread(socket.getOutputStream());
        senderThread.start();
        receiverThread = new ReceiverThread(socket.getInputStream());
        receiverThread.start();
    }

    public synchronized void sendPackage(Package p) {
        senderThread.addPackageToSendStack(p);
    }

    public synchronized void stop() {
        senderThread.closeThread();
        receiverThread.closeThread();
        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
