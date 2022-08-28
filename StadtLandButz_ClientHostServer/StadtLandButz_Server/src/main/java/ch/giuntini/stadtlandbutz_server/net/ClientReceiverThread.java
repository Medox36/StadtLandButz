package ch.giuntini.stadtlandbutz_server.net;

import ch.giuntini.stadtlandbutz_package.Package;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ClientReceiverThread extends Thread{

    private final ClientReceiverInterpretThread receiverInterpretThread;

    private final ConcurrentLinkedQueue<Package> packages = new ConcurrentLinkedQueue<>();
    private PackageObjectInputStream objectInputStream;
    private final Object lock;
    private boolean stop;

    public ClientReceiverThread(InputStream inputStream, Client client) {
        super("Client-Receiving-Thread");
        try {
            objectInputStream = new PackageObjectInputStream(new BufferedInputStream(inputStream));
            /*objectInputStream.setObjectInputFilter(filterInfo -> {
                if (filterInfo.serialClass().getName().equals("Package")) {
                    return ObjectInputFilter.Status.ALLOWED;
                } else {
                    return ObjectInputFilter.Status.REJECTED;
                }
            });*/
        } catch (IOException e) {
            e.printStackTrace();
        }
        lock = new Object();
        receiverInterpretThread = new ClientReceiverInterpretThread(packages, lock, client);
    }

    @Override
    public void run() {
        receiverInterpretThread.start();
        while (!stop) {
            try {
                Package p = (Package) objectInputStream.readObject();
                packages.add(p);
                synchronized (lock) {
                    lock.notify();
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                closeThread();
            }
        }
        try {
            objectInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        receiverInterpretThread.closeThread();
    }

    public synchronized void closeThread() {
        stop = true;
    }
}
