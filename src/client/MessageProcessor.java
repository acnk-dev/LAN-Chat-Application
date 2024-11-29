package client;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import constants.ActionConstants;
import constants.KeyConstants;
import constants.Status;
import des.Cipher;
import des.SystemMetadata;
import json.JSONHandler;
import login.Login;
import main.Main;
import parser.ByteArrayParser;
import register.Register;
import rsa.RSAPublicKey;
import security.SymmetricCipher;
import xml.XMLHandler;

public class MessageProcessor {

    public static void handleReceivedMessage(String message) {
        Map<String, Object> messageMap = JSONHandler.getParser().parse(message);
        ActionConstants action = ActionConstants.valueOf(messageMap.get(KeyConstants.ACTION.toString()).toString());

        switch (action) {
            case LOGIN:
                handleLogin(messageMap);
                break;

            case REGISTER:
                handleRegister(messageMap);
                break;

            case FETCH_ALL_USERS:
                handleAllUsers(messageMap);
                break;

            case FETCH_ONLINE_USERS:
                handleOnlineUsers(messageMap);
                break;

            case UPDATE_USER_AVAILABILITY_STATUS:
                handleUserAvailabilityStatus(messageMap);
                break;

            case FETCH_PROFILE_PICTURE:
                handleProfilePicture(messageMap);
                break;

            case GET_PUBLIC_KEY:
                handleGetPublicKey(messageMap);
                break;

            case CHAT_MESSAGE:
                handleChatMessage(messageMap);
                break;

            default:
                break;
        }
    }

    public static void handleSendMessage(Map<String, Object> messageMap) {
        String message = JSONHandler.getParser().stringify(messageMap);
        Client.getClient().getClientOutputStream().sendMessage(message);
    }

    /**
     * Encrypts and sends a chat message: DES-encrypts the {SENDER_ID, MESSAGE}
     * packet under a fresh per-message key, RSA-wraps that key with the
     * receiver's public key, and DES-encrypts the message's own metadata
     * under the fixed SystemMetadata tables using that same new key - the
     * server only ever sees ciphertext. If the receiver's public key hasn't
     * been fetched yet, the message is queued and a GET_PUBLIC_KEY request
     * is sent; it's dispatched once the reply arrives.
     */
    public static void sendChatMessage(int receiverUserId, int senderUserId, String plainText) {
        RSAPublicKey publicKey = CryptoSession.getCachedPublicKey(receiverUserId);
        if (publicKey == null) {
            CryptoSession.queuePendingMessage(receiverUserId, senderUserId, plainText);
            requestPublicKey(receiverUserId);
            return;
        }
        dispatchEncryptedChatMessage(receiverUserId, senderUserId, plainText, publicKey);
    }

    private static void requestPublicKey(int contactUserId) {
        Map<String, Object> request = new HashMap<String, Object>();
        request.put(KeyConstants.ACTION.toString(), ActionConstants.GET_PUBLIC_KEY);
        request.put(KeyConstants.USER_ID.toString(), contactUserId);
        handleSendMessage(request);
    }

    private static void dispatchEncryptedChatMessage(int receiverUserId, int senderUserId, String plainText, RSAPublicKey publicKey) {
        Map<String, Object> innerPacket = new HashMap<String, Object>();
        innerPacket.put(KeyConstants.SENDER_ID.toString(), senderUserId);
        innerPacket.put(KeyConstants.MESSAGE.toString(), plainText);
        String innerJson = JSONHandler.getParser().stringify(innerPacket);

        SymmetricCipher cipher = new Cipher();
        String messageMetadata = cipher.generateMetadata();
        String encryptedMessage = cipher.encrypt(innerJson, messageMetadata);

        String newKey = cipher.generateAndSetNewKey();
        BigInteger encryptedKey = CryptoSession.getRSA().encrypt(new BigInteger(newKey), publicKey);
        String encryptedMetadata = cipher.encrypt(messageMetadata, SystemMetadata.JSON, newKey);

        Map<String, Object> envelope = new HashMap<String, Object>();
        envelope.put(KeyConstants.ACTION.toString(), ActionConstants.CHAT_MESSAGE);
        envelope.put(KeyConstants.RECEIVER_ID.toString(), receiverUserId);
        envelope.put(KeyConstants.MESSAGE.toString(), encryptedMessage);
        envelope.put(KeyConstants.METADATA.toString(), encryptedMetadata);
        envelope.put(KeyConstants.KEY.toString(), encryptedKey.toString());

        handleSendMessage(envelope);
    }

    private static void handleLogin(Map<String, Object> infoMap) {
        Status status = Status.valueOf(infoMap.get(KeyConstants.STATUS.toString()).toString());
        if (status == Status.SUCCESS) {
            openMainFromAuthResponse(infoMap);
            Login.disposeWindow();
        } else {
            Login.displayErrorMessage("Login Failed!", "Enter a valid username and password");
        }
    }

    private static void handleRegister(Map<String, Object> infoMap) {
        Status status = Status.valueOf(infoMap.get(KeyConstants.STATUS.toString()).toString());
        if (status == Status.SUCCESS) {
            openMainFromAuthResponse(infoMap);
            Register.disposeWindow();
        } else {
            Register.displayMessage("Registration Failed!", "That username is already taken");
        }
    }

    private static void openMainFromAuthResponse(Map<String, Object> infoMap) {
        int userID = (Integer) infoMap.get(KeyConstants.USER_ID.toString());
        String name = (String) infoMap.get(KeyConstants.NAME.toString());
        String username = (String) infoMap.get(KeyConstants.USERNAME.toString());
        String email = (String) infoMap.get(KeyConstants.EMAIL.toString());
        // Runs synchronously on this thread (the same one that will go on
        // to process any undelivered messages flushed right after login),
        // so the keypair is guaranteed ready before the first decrypt -
        // Main.open() only schedules UI construction on the EDT.
        CryptoSession.initForUser(userID);
        Main.open(userID, name, username, email);
    }

    @SuppressWarnings("unchecked")
    private static void handleAllUsers(Map<String, Object> infoMap) {
        ArrayList<Integer> userIds = (ArrayList<Integer>) infoMap.get(KeyConstants.USER_IDS.toString());
        ArrayList<String> userNames = (ArrayList<String>) infoMap.get(KeyConstants.USER_NAMES.toString());
        Main.populateContacts(userIds, userNames);
    }

    @SuppressWarnings("unchecked")
    private static void handleOnlineUsers(Map<String, Object> infoMap) {
        ArrayList<Integer> onlineUsers = (ArrayList<Integer>) infoMap.get(KeyConstants.ONLINE_USERS.toString());
        Main.updateOnlineUsers(new Vector<Integer>(onlineUsers));
    }

    private static void handleUserAvailabilityStatus(Map<String, Object> messageMap) {
        int userID = (Integer) messageMap.get(KeyConstants.USER_ID.toString());
        boolean isOnline = (Boolean) messageMap.get(KeyConstants.IS_ONLINE.toString());
        Main.updateUserStatus(userID, isOnline);
    }

    private static void handleProfilePicture(Map<String, Object> messageMap) {
        int userID = (Integer) messageMap.get(KeyConstants.USER_ID.toString());
        String imageData = messageMap.get(KeyConstants.PROFILE_PIC.toString()).toString();
        byte[] profilePic = ByteArrayParser.getParser().parse(imageData);
        Main.updateProfilePicture(userID, profilePic);
    }

    private static void handleGetPublicKey(Map<String, Object> messageMap) {
        int contactUserId = (Integer) messageMap.get(KeyConstants.USER_ID.toString());
        Object publicKeyXml = messageMap.get(KeyConstants.PUBLIC_KEY.toString());
        if (publicKeyXml == null) {
            // No key on file means they've never logged in - there's no
            // way to encrypt to a key that doesn't exist yet. Surface
            // this instead of leaving the queued message to vanish
            // silently.
            List<CryptoSession.PendingMessage> dropped = CryptoSession.drainPendingMessages(contactUserId);
            if (!dropped.isEmpty()) {
                Main.messageDeliveryFailed(contactUserId, dropped.size());
            }
            return;
        }
        RSAPublicKey publicKey = XMLHandler.parseRSAPublicKey(publicKeyXml.toString());
        CryptoSession.cachePublicKey(contactUserId, publicKey);

        for (CryptoSession.PendingMessage pendingMessage : CryptoSession.drainPendingMessages(contactUserId)) {
            dispatchEncryptedChatMessage(contactUserId, pendingMessage.senderUserId, pendingMessage.plainText, publicKey);
        }
    }

    private static final int DES_KEY_LENGTH = 64;

    private static void handleChatMessage(Map<String, Object> messageMap) {
        String encryptedMessage = messageMap.get(KeyConstants.MESSAGE.toString()).toString();
        String encryptedMetadata = messageMap.get(KeyConstants.METADATA.toString()).toString();
        BigInteger encryptedKey = new BigInteger(messageMap.get(KeyConstants.KEY.toString()).toString());

        // The DES key is a fixed-width string of 64 '0'/'1' digits, but it
        // travels as a BigInteger through RSA - which silently drops any
        // leading zero digits. Re-pad back to the known width or a key that
        // happened to start with '0' comes back short and decryption breaks.
        String rawDecryptedKey = CryptoSession.getRSA().decrypt(encryptedKey, CryptoSession.getOwnPrivateKey()).toString();
        String decryptedKey = padKey(rawDecryptedKey);

        SymmetricCipher cipher = new Cipher();
        String decryptedMessageMetadata = cipher.decrypt(encryptedMetadata, SystemMetadata.JSON, decryptedKey);
        String decryptedInnerJson = cipher.decrypt(encryptedMessage, decryptedMessageMetadata);

        Map<String, Object> innerPacket = JSONHandler.getParser().parse(decryptedInnerJson);
        int senderID = (Integer) innerPacket.get(KeyConstants.SENDER_ID.toString());
        String text = (String) innerPacket.get(KeyConstants.MESSAGE.toString());
        Main.receiveMessage(senderID, text);
    }

    private static String padKey(String key) {
        StringBuilder padded = new StringBuilder();
        for (int i = key.length(); i < DES_KEY_LENGTH; i++) {
            padded.append('0');
        }
        padded.append(key);
        return padded.toString();
    }
}
