package ch.giuntini.stadtlandbutz_server.net;

import ch.giuntini.stadtlandbutz_package.Package;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SenderThread extends Thread {

    private ObjectOutputStream objectOutputStream;
    private final ConcurrentLinkedQueue<Package> packages = new ConcurrentLinkedQueue<>();
    private volatile boolean stop;

    public SenderThread(OutputStream outputStream) {
        super("Client-Sender-Thread");
        try {
            objectOutputStream = new ObjectOutputStream(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
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
                    closeThread();
                }
            }
            Thread.onSpinWait();
        }
        try {
            objectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected synchronized void addPackageToSendStack(Package p) {
        packages.add(p);
    }

    public void closeThread() {
        stop = true;
    }
}
