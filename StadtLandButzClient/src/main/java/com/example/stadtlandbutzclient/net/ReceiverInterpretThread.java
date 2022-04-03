package com.example.stadtlandbutzclient.net;

import java.util.concurrent.ConcurrentLinkedQueue;

public class ReceiverInterpretThread extends Thread {

    private final ConcurrentLinkedQueue<Package> packages;
    private boolean stop;

    public ReceiverInterpretThread(ConcurrentLinkedQueue<Package> packages) {
        super("Client-ReceiverInterpret-Thread");
        this.packages = packages;
    }

    @Override
    public void run() {
        while (!stop) {
            while (!packages.isEmpty()) {
                Package p = packages.poll();
                if (p != null) {
                    ClientInterpreter.interpret(p);
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
