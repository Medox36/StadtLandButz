package ch.giuntini.stadtlandbutz_client.net;

import java.util.concurrent.ConcurrentLinkedQueue;

public class ReceiverInterpretThread extends Thread {

    private final ConcurrentLinkedQueue<Package> packages;
    private final Object lock;
    private boolean stop;

    public ReceiverInterpretThread(ConcurrentLinkedQueue<Package> packages, Object lock) {
        super("Client-ReceiverInterpret-Thread");
        this.packages = packages;
        this.lock = lock;
    }

    @Override
    public void run() {
        synchronized (lock) {
            while (!stop) {
                while (!packages.isEmpty()) {
                    Package p = packages.poll();
                    if (p != null) {
                        ClientNetInterpreter.interpret(p);
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
