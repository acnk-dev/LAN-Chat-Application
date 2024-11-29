package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private Socket socket;
    private ClientInputStream clientInputStream;
    private ClientOutputStream clientOutputStream;
    private static Client client = null;

    private Client() {
        try {
            socket = new Socket("127.0.0.1", 8761);
            BufferedReader brServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);

            clientInputStream = new ClientInputStream(brServer);
            clientOutputStream = new ClientOutputStream(pw);
        } catch (IOException e) {
            System.out.println("Server Down! Try again later!");
            System.exit(1);
        }
    }

    public static Client getClient() {
        if (client == null) {
            client = new Client();
        }
        return client;
    }

    public ClientInputStream getClientInputStream() {
        return clientInputStream;
    }

    public ClientOutputStream getClientOutputStream() {
        return clientOutputStream;
    }

    /** Closes the socket - the server notices the disconnect and marks this user offline. */
    public void disconnect() {
        try {
            socket.close();
        } catch (IOException e) {
            // already gone, nothing to clean up
        }
    }

    /** Drops the cached singleton so the next getClient() opens a fresh connection - needed after logout. */
    public static void reset() {
        client = null;
    }
}
