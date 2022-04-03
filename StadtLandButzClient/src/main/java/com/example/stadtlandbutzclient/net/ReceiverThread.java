package com.example.stadtlandbutzclient.net;

import java.io.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ReceiverThread extends Thread {

    private final ReceiverInterpretThread receiverInterpretThread;

    private final ConcurrentLinkedQueue<Package> packages = new ConcurrentLinkedQueue<>();
    private ObjectInputStream objectInputStream;
    private boolean stop;

    public ReceiverThread(InputStream inputStream) {
        super("Client-Receiving-Thread");
        try {
            objectInputStream = new ObjectInputStream(new BufferedInputStream(inputStream));
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
        receiverInterpretThread = new ReceiverInterpretThread(packages);
    }

    @Override
    public void run() {
        receiverInterpretThread.start();
        while (!stop) {
            try {
                Package p = (Package) objectInputStream.readObject();
                packages.add(p);
                receiverInterpretThread.notify();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
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