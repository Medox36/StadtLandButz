package com.example.stadtlandbutz_server.game;

import com.example.stadtlandbutz_server.net.Client;
import com.example.stadtlandbutz_server.net.Package;
import com.example.stadtlandbutz_server.net.Host;
import com.example.stadtlandbutz_server.net.connection.ClientConnection;
import com.example.stadtlandbutz_server.net.connection.HostConnection;
import javafx.application.Platform;

import java.io.IOException;
import java.net.Socket;
import java.util.UUID;
import java.util.Vector;

public class Game {
    private static Vector<Client> clients = new Vector<>();
    private static Vector<UUID> uuids = new Vector<>();
    private static Host host;
    private static HostConnection hostConnection;
    private static ClientConnection clientConnection;

    public void startServer() {
        hostConnection = new HostConnection();
        clientConnection = new ClientConnection();
    }

    public static void addClient(Socket socket) throws IOException {
        clients.add(new Client(socket));
    }

    public static UUID getNewUUID() {
        UUID uuid = UUID.randomUUID();
        while (uuids.contains(uuid)) {
            uuid = UUID.randomUUID();
        }
        uuids.add(uuid);
        return uuid;
    }

    public synchronized static void sendToAllClients(String prefix, String information) {
        for (Client client : clients) {
            client.sendPackage(new Package("", prefix, information, client.getUUIDString()));
        }
    }

    public static void addHost(Socket socket) {
        try {
            host = new Host(socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void exit(boolean explicit) {
        Thread t = new Thread(() -> exiting(explicit), "Exit Thread");
        t.setPriority(10);
        t.start();
    }

    private static void exiting(boolean explicit) {
        Platform.exit();


        System.gc();
        if (explicit) System.exit(3);
    }
}