package ch.giuntini.stadtlandbutz_client.net;

import ch.giuntini.stadtlandbutz_package.Package;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ReceiverThread extends Thread {

    private PackageObjectInputStream objectInputStream;
    private final Client client;
    private volatile boolean stop;

    public ReceiverThread(InputStream inputStream, Client client) {
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
                ClientNetInterpreter.interpret(p);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                break;
            }
        }
        try {
            objectInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!stop) {
            client.disconnectOnException();
        }
    }

    public void closeThread() {
        stop = true;
    }
}
