package ch.giuntini.stadtlandbutz_server.net;

import ch.giuntini.stadtlandbutz_package.Package;

import java.util.concurrent.ConcurrentLinkedQueue;

public class ClientReceiverInterpretThread extends Thread{

    private final Client c;
    private final ConcurrentLinkedQueue<Package> packages;
    private final Object lock;
    private boolean stop;

    public ClientReceiverInterpretThread(ConcurrentLinkedQueue<Package> packages, Object lock, Client c) {
        super("Client-ReceiverInterpret-Thread");
        this.packages = packages;
        this.lock = lock;
        this.c = c;
    }

    @Override
    public void run() {
        synchronized (lock) {
            while (!stop) {
                while (!packages.isEmpty()) {
                    Package p = packages.poll();
                    if (p != null) {
                        ServerNetInterpreter.interpretFromClient(p, c);
                    }
                }
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public synchronized void closeThread() {
        stop = true;
        synchronized (lock) {
            lock.notify();
        }
    }
}
