package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import constants.ActionConstants;
import constants.KeyConstants;
import json.JSONHandler;
import parser.ByteArrayParser;

/**
 * Post-login handler: FETCH_ALL_USERS (contact list), FETCH_ONLINE_USERS,
 * FETCH_PROFILE_PICTURE. CHAT_MESSAGE comes back with real messaging.
 */
public class ClientHandler extends Thread {
    private Socket client;
    private int userID;

    public ClientHandler(int userID, Socket socket) {
        this.userID = userID;
        this.client = socket;
        start();
    }

    public void run() {
        try {
            BufferedReader brClient = new BufferedReader(new InputStreamReader(client.getInputStream()));
            String message;
            while ((message = brClient.readLine()) != null) {
                processReceivedMessage(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Server.processWhenUserGoesOffline(userID);
        }
    }

    private void processReceivedMessage(String message) {
        Map<String, Object> messageMap = JSONHandler.getParser().parse(message);
        ActionConstants action = ActionConstants.valueOf(messageMap.get(KeyConstants.ACTION.toString()).toString());

        switch (action) {
            case FETCH_ALL_USERS:
                processFetchAllUsers();
                break;

            case FETCH_ONLINE_USERS:
                processFetchOnlineUsers();
                break;

            case FETCH_PROFILE_PICTURE:
                processFetchProfilePicture(messageMap);
                break;

            case GET_PUBLIC_KEY:
                processFetchPublicKey(messageMap);
                break;

            case SET_PUBLIC_KEY:
                processSetPublicKey(messageMap);
                break;

            case CHAT_MESSAGE:
                processChatMessage(messageMap);
                break;

            default:
                break;
        }
    }

    private void sendMessage(Map<String, Object> messageMap) {
        sendMessageTo(client, messageMap);
    }

    private void sendMessageTo(Socket recipient, Map<String, Object> messageMap) {
        try {
            PrintWriter writer = new PrintWriter(recipient.getOutputStream(), true);
            writer.println(JSONHandler.getParser().stringify(messageMap));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void processFetchAllUsers() {
        Map<Integer, String> users = Operations.getAllUsers();

        Integer[] userIds = users.keySet().toArray(new Integer[0]);
        String[] userNames = new String[userIds.length];
        for (int i = 0; i < userIds.length; i++) {
            userNames[i] = users.get(userIds[i]);
        }

        Map<String, Object> msg = new HashMap<String, Object>();
        msg.put(KeyConstants.ACTION.toString(), ActionConstants.FETCH_ALL_USERS);
        msg.put(KeyConstants.USER_IDS.toString(), userIds);
        msg.put(KeyConstants.USER_NAMES.toString(), userNames);
        sendMessage(msg);
    }

    private void processFetchOnlineUsers() {
        Vector<Integer> onlineUsers = Operations.getOnlineUsers();

        Map<String, Object> msg = new HashMap<String, Object>();
        msg.put(KeyConstants.ACTION.toString(), ActionConstants.FETCH_ONLINE_USERS);
        msg.put(KeyConstants.ONLINE_USERS.toString(), onlineUsers.toArray());
        sendMessage(msg);
    }

    private void processFetchProfilePicture(Map<String, Object> messageMap) {
        int targetUserId = (Integer) messageMap.get(KeyConstants.USER_ID.toString());
        byte[] imageData = Operations.getProfilePicture(targetUserId);

        Map<String, Object> msg = new HashMap<String, Object>();
        msg.put(KeyConstants.ACTION.toString(), ActionConstants.FETCH_PROFILE_PICTURE);
        msg.put(KeyConstants.USER_ID.toString(), targetUserId);
        msg.put(KeyConstants.PROFILE_PIC.toString(), ByteArrayParser.getParser().stringify(imageData));
        sendMessage(msg);
    }

    private void processFetchPublicKey(Map<String, Object> messageMap) {
        int targetUserId = (Integer) messageMap.get(KeyConstants.USER_ID.toString());
        String publicKey = Operations.getPublicKey(targetUserId);

        Map<String, Object> msg = new HashMap<String, Object>();
        msg.put(KeyConstants.ACTION.toString(), ActionConstants.GET_PUBLIC_KEY);
        msg.put(KeyConstants.USER_ID.toString(), targetUserId);
        msg.put(KeyConstants.PUBLIC_KEY.toString(), publicKey);
        sendMessage(msg);
    }

    private void processSetPublicKey(Map<String, Object> messageMap) {
        String publicKey = (String) messageMap.get(KeyConstants.PUBLIC_KEY.toString());
        Operations.setPublicKey(userID, publicKey);
    }

    /**
     * Pure relay-or-mailbox: the server never decrypts MESSAGE/METADATA/KEY,
     * it only reads RECEIVER_ID to route. Logged here, in full, so the
     * ciphertext can be eyeballed to confirm nothing readable is leaking
     * through the server.
     */
    private void processChatMessage(Map<String, Object> messageMap) {
        int receiverID = (Integer) messageMap.get(KeyConstants.RECEIVER_ID.toString());
        System.out.println("[ClientHandler] CHAT_MESSAGE relay: sender=" + userID + " receiver=" + receiverID);
        System.out.println("[ClientHandler]   message (DES ciphertext):  " + messageMap.get(KeyConstants.MESSAGE.toString()));
        System.out.println("[ClientHandler]   metadata (DES ciphertext): " + messageMap.get(KeyConstants.METADATA.toString()));
        System.out.println("[ClientHandler]   key (RSA-wrapped):         " + messageMap.get(KeyConstants.KEY.toString()));

        Socket receiverSocket = Server.getSocket(receiverID);
        if (receiverSocket != null) {
            sendMessageTo(receiverSocket, messageMap);
        } else {
            System.out.println("[ClientHandler] receiver " + receiverID + " is offline - storing as undelivered");
            Operations.storeUndeliveredMessage(receiverID, JSONHandler.getParser().stringify(messageMap));
        }
    }
}
