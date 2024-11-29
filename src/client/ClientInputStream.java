package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientInputStream extends Thread {
    private BufferedReader brServer;

    ClientInputStream(BufferedReader brServer) {
        this.brServer = brServer;
        start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                String message = brServer.readLine();
                if (message == null) {
                    break;
                }
                try {
                    MessageProcessor.handleReceivedMessage(message);
                } catch (RuntimeException ex) {
                    System.out.println("[ClientInputStream] failed to handle message: " + message);
                    ex.printStackTrace();
                }
            } catch (IOException ex) {
                Logger.getLogger(ClientInputStream.class.getName()).log(Level.SEVERE, null, ex);
                break;
            }
        }
    }
}
