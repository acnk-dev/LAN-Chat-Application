package client;

import java.io.PrintWriter;

public class ClientOutputStream {

    private PrintWriter pw;

    ClientOutputStream(PrintWriter pw) {
        this.pw = pw;
    }

    public void sendMessage(String message) {
        pw.println(message);
    }
}
