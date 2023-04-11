package ch.giuntini.stadtlandbutz_server.net;

import ch.giuntini.stadtlandbutz_package.Package;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ReceiverThread extends Thread {

    private PackageObjectInputStream objectInputStream;
    private volatile boolean stop;

    public ReceiverThread(InputStream inputStream) {
        super("Client-Receiving-Thread");
        try {
            objectInputStream = new PackageObjectInputStream(new BufferedInputStream(inputStream));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (!stop) {
            try {
                Package p = (Package) objectInputStream.readObject();
                ServerNetInterpreter.interpret(p);
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

    public void closeThread() {
        stop = true;
    }
}
