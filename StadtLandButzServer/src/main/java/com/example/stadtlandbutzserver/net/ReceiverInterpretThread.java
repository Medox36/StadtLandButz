package com.example.stadtlandbutzserver.net;

import java.util.concurrent.ConcurrentLinkedQueue;

public class ReceiverInterpretThread extends Thread {

    private final ConcurrentLinkedQueue<Package> packages;
    private final Client client;
    private boolean stop;

    public ReceiverInterpretThread(ConcurrentLinkedQueue<Package> packages, Client client) {
        super("Client-ReceiverInterpret-Thread");
        this.packages = packages;
        this.client = client;
    }

    @Override
    public void run() {
        while (!stop) {
            while (!packages.isEmpty()) {
                Package p = packages.poll();
                if (p != null) {
                    ServerNetInterpreter.interpretForClient(p, client);
                }
            }
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void closeThread() {
        stop = true;
        this.notify();
    }
}
