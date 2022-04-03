package com.example.stadtlandbutzserver.net;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SenderThread extends Thread {

    private ObjectOutputStream objectOutputStream;
    private final ConcurrentLinkedQueue<Package> packages = new ConcurrentLinkedQueue<>();
    private boolean stop;

    public SenderThread(OutputStream outputStream, UUID uuid) {
        super("Client-Sender-Thread for Client: " + uuid);

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
                }
            }
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
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
        this.notify();
    }

    public synchronized void closeThread() {
        stop = true;
        this.notify();
    }
}