package ch.giuntini.stadtlandbutz_host.net;

import ch.giuntini.stadtlandbutz_package.Package;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Objects;

public class Host {
    private final Socket socket;
    private final SenderThread senderThread;
    private final ReceiverThread receiverThread;

    public Host(List<String> args) throws IOException {
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
        socket = new Socket(url, 25541);
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
