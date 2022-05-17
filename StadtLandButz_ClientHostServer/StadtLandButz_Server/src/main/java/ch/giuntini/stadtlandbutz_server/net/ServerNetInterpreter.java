package ch.giuntini.stadtlandbutz_server.net;

public class ServerNetInterpreter {

    public synchronized static void interpret(Package p) {
        switch (p.serverPrefix) {
            case "000":
                // for the server from a client

                break;
            case "001":
                // for the server from the host

                break;
            case "010":
                // send to specific client from the host

                break;
            case "011":
                // send to all clients from the host

                break;
            case "100":
                // send to the host from a client

                break;
            default:

        }
    }

}