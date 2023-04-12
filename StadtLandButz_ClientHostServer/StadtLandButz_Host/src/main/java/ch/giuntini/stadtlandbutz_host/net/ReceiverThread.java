package ch.giuntini.stadtlandbutz_host.net;

import ch.giuntini.stadtlandbutz_package.Package;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ReceiverThread extends Thread {

    private PackageObjectInputStream objectInputStream;
    private final Host host;
    private volatile boolean stop;

    public ReceiverThread(InputStream inputStream, Host host) {
        super("Client-Receiving-Thread");
        try {
            objectInputStream = new PackageObjectInputStream(new BufferedInputStream(inputStream));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.host = host;
    }

    @Override
    public void run() {
        while (!stop) {
            try {
                Package p = (Package) objectInputStream.readObject();
                HostNetInterpreter.interpret(p);
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
            host.stop();
        }
    }

    public void closeThread() {
        stop = true;
    }
}
