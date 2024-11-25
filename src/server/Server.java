package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import constants.ActionConstants;
import constants.KeyConstants;

public class Server {

    private static HashMap<Integer, Socket> userIdSocketMap;

    public static void main(String[] args) throws IOException {
        userIdSocketMap = new HashMap<Integer, Socket>();
        ServerSocket server = new ServerSocket(8761);
        System.out.println("Server is waiting for a connection");
        ExecutorService service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        while (true) {
            Socket client = server.accept();
            try {
                service.execute(new SessionInitiationService(client));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected static void putUserIdSocket(int userId, Socket socket) {
        userIdSocketMap.put(userId, socket);
    }

    protected static Socket getSocket(int userID) {
        return userIdSocketMap.get(userID);
    }

    protected static Collection<Socket> getClients() {
        return userIdSocketMap.values();
    }

    protected static void processWhenUserComesOnline(int userID) {
        Operations.setUserOnlineInDatabase(userID);
        broadcastAvailability(userID, true);
    }

    protected static void processWhenUserGoesOffline(int userID) {
        Operations.setUserOfflineInDatabase(userID);
        userIdSocketMap.remove(userID);
        broadcastAvailability(userID, false);
    }

    private static void broadcastAvailability(int userID, boolean isOnline) {
        Map<String, Object> message = new HashMap<String, Object>();
        message.put(KeyConstants.ACTION.toString(), ActionConstants.UPDATE_USER_AVAILABILITY_STATUS);
        message.put(KeyConstants.USER_ID.toString(), userID);
        message.put(KeyConstants.IS_ONLINE.toString(), isOnline);
        Operations.broadcast(message);
    }
}
