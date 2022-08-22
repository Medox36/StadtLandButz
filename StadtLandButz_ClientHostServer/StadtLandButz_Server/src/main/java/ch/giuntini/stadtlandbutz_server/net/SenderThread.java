package ch.giuntini.stadtlandbutz_server.net;

import ch.giuntini.stadtlandbutz_package.Package;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SenderThread extends Thread {

    private ObjectOutputStream objectOutputStream;
    private final ConcurrentLinkedQueue<Package> packages = new ConcurrentLinkedQueue<>();
    private final Object lock;
    private boolean stop;

    public SenderThread(OutputStream outputStream) {
        super("Client-Sender-Thread");
        try {
            objectOutputStream = new ObjectOutputStream(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        lock = new Object();
    }

    @Override
    public void run() {
        while (!stop) {
            synchronized (lock) {
                while (!packages.isEmpty()) {
                    try {
                        Package p = packages.poll();
                        if (p != null) {
                            objectOutputStream.writeObject(p);
                            objectOutputStream.flush();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            objectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected synchronized void addPackageToSendStack(Package p) {
        packages.add(p);
        synchronized (lock) {
            lock.notify();
        }
    }

    public synchronized void closeThread() {
        stop = true;
        synchronized (lock) {
            lock.notify();
        }
    }
}
