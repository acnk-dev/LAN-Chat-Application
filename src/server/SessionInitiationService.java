package server;

import constants.ActionConstants;
import constants.KeyConstants;
import constants.Status;
import json.JSONHandler;
import parser.ByteArrayParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SessionInitiationService implements Runnable {
    private Socket client;
    private boolean isGuestSessionAlive;

    SessionInitiationService(Socket client) {
        this.client = client;
        this.isGuestSessionAlive = true;
    }

    @Override
    public void run() {
        try {
            BufferedReader brClient = new BufferedReader(new InputStreamReader(client.getInputStream()));
            while (isGuestSessionAlive) {
                String message = brClient.readLine();
                if (message == null) {
                    break;
                }
                System.out.println("[SessionInitiationService] received: " + message);
                Map<String, Object> messageMap = JSONHandler.getParser().parse(message);
                ActionConstants action = ActionConstants.valueOf(messageMap.get(KeyConstants.ACTION.toString()).toString());

                switch (action) {
                    case LOGIN:
                        authenticate(messageMap);
                        break;

                    case REGISTER:
                        register(messageMap);
                        break;

                    default:
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            System.out.println("[SessionInitiationService] failed while handling a message:");
            e.printStackTrace();
        }
    }

    private void authenticate(Map<String, Object> credentials) {
        String username = credentials.get(KeyConstants.USERNAME.toString()).toString().trim();
        String password = credentials.get(KeyConstants.PASSWORD.toString()).toString().trim();
        boolean isValidated = Operations.validateUserCredentials(username, password);
        if (isValidated) {
            int userID = Operations.getUserID(username);
            Server.putUserIdSocket(userID, client);
            new ClientHandler(userID, client);
            sendLoginRegisterStatusMessage(userID, ActionConstants.LOGIN, Status.SUCCESS);
            Server.processWhenUserComesOnline(userID);
            flushUndeliveredMessages(userID);
            killThisThread();
        } else {
            sendLoginRegisterStatusMessage(null, ActionConstants.LOGIN, Status.FAILURE);
        }
    }

    /** Anything sent to this user while they were offline - queued server-side, sent now that they have a live socket. */
    private void flushUndeliveredMessages(int userID) {
        List<String> undelivered = Operations.fetchAndClearUndeliveredMessages(userID);
        for (String raw : undelivered) {
            sendRaw(raw);
        }
    }

    private void sendRaw(String message) {
        try {
            PrintWriter clientWriter = new PrintWriter(client.getOutputStream(), true);
            clientWriter.println(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendLoginRegisterStatusMessage(Integer userID, ActionConstants action, Status status) {
        Map<String, Object> message = new HashMap<String, Object>();
        message.put(KeyConstants.ACTION.toString(), action);
        message.put(KeyConstants.STATUS.toString(), status);
        if (userID != null) {
            message.put(KeyConstants.USER_ID.toString(), userID);
            // Send the signed-in user's own profile back too, so the
            // client has it immediately for the profile page without a
            // separate round trip.
            Map<String, Object> ownProfile = Operations.getUserBasicInfo(userID);
            if (ownProfile != null) {
                message.putAll(ownProfile);
            }
        }
        sendMessage(message);
    }

    private void register(Map<String, Object> userInfo) {
        String name = userInfo.get(KeyConstants.NAME.toString()).toString();
        String username = userInfo.get(KeyConstants.USERNAME.toString()).toString();
        String email = userInfo.get(KeyConstants.EMAIL.toString()).toString();
        String password = userInfo.get(KeyConstants.PASSWORD.toString()).toString();
        String imageData = userInfo.get(KeyConstants.PROFILE_PIC.toString()).toString();
        byte[] profilePic = ByteArrayParser.getParser().parse(imageData);
        boolean created = Operations.createAccount(name, username, email, password, profilePic);

        if (created) {
            // Registering counts as being signed in - same handoff as a
            // successful LOGIN, so the client goes straight to chat
            // instead of having to log in separately right after.
            int userID = Operations.getUserID(username);
            Server.putUserIdSocket(userID, client);
            new ClientHandler(userID, client);
            sendLoginRegisterStatusMessage(userID, ActionConstants.REGISTER, Status.SUCCESS);
            Server.processWhenUserComesOnline(userID);
            flushUndeliveredMessages(userID);
            killThisThread();
        } else {
            sendLoginRegisterStatusMessage(null, ActionConstants.REGISTER, Status.FAILURE);
        }
    }

    private void killThisThread() {
        isGuestSessionAlive = false;
    }

    private void sendMessage(Map<String, Object> message) {
        try {
            PrintWriter clientWriter = new PrintWriter(client.getOutputStream(), true);
            clientWriter.println(JSONHandler.getParser().stringify(message));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
