package ch.giuntini.stadtlandbutz_server.net;

import ch.giuntini.stadtlandbutz_package.Package;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ClientSenderThread extends Thread {

    private ObjectOutputStream objectOutputStream;
    private final ConcurrentLinkedQueue<Package> packages = new ConcurrentLinkedQueue<>();
    private final Client client;
    private volatile boolean stop;

    public ClientSenderThread(OutputStream outputStream, Client client) {
        super("Client-Sender-Thread");
        try {
            objectOutputStream = new ObjectOutputStream(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.client = client;
    }

    @Override
    public void run() {
        runLoop:
        while (!stop) {
            while (!packages.isEmpty()) {
                try {
                    Package p = packages.poll();
                    if (p != null) {
                        objectOutputStream.writeObject(p);
                        objectOutputStream.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    break runLoop;
                }
            }
            Thread.onSpinWait();
        }
        try {
            objectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!stop) {
            client.disconnectOnException();
        }
    }

    protected synchronized void addPackageToSendStack(Package p) {
        packages.add(p);
    }

    public void closeThread() {
        stop = true;
    }
}
