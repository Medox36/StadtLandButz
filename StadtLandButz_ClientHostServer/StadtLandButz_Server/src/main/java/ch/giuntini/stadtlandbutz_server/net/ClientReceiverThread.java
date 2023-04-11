package ch.giuntini.stadtlandbutz_server.net;

import ch.giuntini.stadtlandbutz_package.Package;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ClientReceiverThread extends Thread{

    private PackageObjectInputStream objectInputStream;
    private final Client client;
    private boolean stop;

    public ClientReceiverThread(InputStream inputStream, Client client) {
        super("Client-Receiving-Thread");
        try {
            objectInputStream = new PackageObjectInputStream(new BufferedInputStream(inputStream));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.client = client;
    }

    @Override
    public void run() {
        while (!stop) {
            try {
                Package p = (Package) objectInputStream.readObject();
                ServerNetInterpreter.interpretFromClient(p, client);
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
    }

    public synchronized void closeThread() {
        stop = true;
    }
}
