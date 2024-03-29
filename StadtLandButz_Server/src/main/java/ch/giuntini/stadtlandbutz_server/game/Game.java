package ch.giuntini.stadtlandbutz_server.game;

import ch.giuntini.stadtlandbutz_package.Package;
import ch.giuntini.stadtlandbutz_server.net.Host;
import ch.giuntini.stadtlandbutz_server.net.Client;
import ch.giuntini.stadtlandbutz_server.net.connection.ClientConnection;
import ch.giuntini.stadtlandbutz_server.net.connection.HostConnection;

import java.io.IOException;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.UUID;
import java.util.Vector;

public class Game {

    private static String pw;
    private static int gameCode;
    private static final Vector<Client> waitingClients = new Vector<>();
    private static final Vector<Client> clients = new Vector<>();
    private static final Vector<UUID> uuids = new Vector<>();
    private static Host host;
    private static HostConnection hostConnection;
    private static ClientConnection clientConnection;

    public static void startServer() {
        hostConnection = new HostConnection();
        hostConnection.start();
        clientConnection = new ClientConnection();
        clientConnection.start();
    }


    public static void putClientOnWait(Socket socket) throws IOException {
        waitingClients.add(new Client(socket));
    }

    public static void removeWaitingClient(Client client) {
        waitingClients.remove(client);
        removeUUID(client.getUUID());
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        client.disconnectOnRefusal();
    }

    public static void acceptClient(Client client) {
        waitingClients.remove(client);
        clients.add(client);
    }

    public static Client getClientByUUID(String uuid) {
        for (Client client : clients) {
            if (client.getUUIDString().equals(uuid)) {
                return client;
            }
        }
        return null;
    }

    public static void removeUUID(UUID uuid) {
        uuids.remove(uuid);
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
            System.out.println("host connected");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void closeHost() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        host.stop();
        hostConnection.close();
        hostConnection = new HostConnection();
        hostConnection.start();
        System.out.println("host disconnected due to wrong pwd");
    }

    public static Host getHost() {
        return host;
    }

    public static void generateNewGameCode() {
        // min=1111111, max=9999999
        gameCode = new SecureRandom().nextInt(8888888) + 1111111;
        System.out.println("new GameCode generated:" + gameCode);
    }

    public static void exit(boolean explicit) {
        Thread t = new Thread(() -> exiting(explicit), "Exit Thread");
        t.setPriority(10);
        t.start();
    }

    private static void exiting(boolean explicit) {
        pw = null;

        hostConnection.close();
        clientConnection.close();
        host.stop();

        host = null;

        System.gc();
        if (explicit) System.exit(3);
    }

    public static int getGameCode() {
        return gameCode;
    }

    public static String getPw() {
        return pw;
    }

    public static void setPw(String pw) {
        Game.pw = pw;
    }
}
