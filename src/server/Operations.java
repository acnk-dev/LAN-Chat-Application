package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import constants.KeyConstants;
import json.JSONHandler;
import security.SHA;

public class Operations {

    // DatabaseConnection hands out one shared java.sql.Connection for the
    // whole server process, but every connected client runs its own
    // ClientHandler/SessionInitiationService thread - a Connection (and the
    // Statement/ResultSet it hands out) isn't safe for concurrent use
    // across threads. Without this, two clients' queries running at the
    // same time can scramble each other's ResultSet reads. synchronized on
    // a static method locks on Operations.class, so every method below
    // shares one lock and a query's execute-then-read-results lifecycle
    // always finishes before another thread's query can start.

    protected static synchronized boolean validateUserCredentials(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        ResultSet rs = DatabaseUtils.executeQuery(sql, username, new SHA().getHash(password));
        try {
            return rs != null && rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected static synchronized boolean createAccount(String name, String username, String email, String password, byte[] imageData) {
        String sql = "INSERT INTO users(name, username, email, password, profile_picture) VALUES (?, ?, ?, ?, ?)";
        int rowsAffected = DatabaseUtils.executeUpdate(sql, name, username, email, new SHA().getHash(password), imageData);
        return rowsAffected > 0;
    }

    protected static synchronized int getUserID(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        ResultSet rs = DatabaseUtils.executeQuery(sql, username);
        try {
            if (rs != null && rs.next()) {
                return rs.getInt("userid");
            } else {
                throw new RuntimeException("User not found");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected static synchronized Map<String, Object> getUserBasicInfo(int userID) {
        String sql = "SELECT name, username, email FROM users WHERE userid = ?";
        ResultSet rs = DatabaseUtils.executeQuery(sql, userID);
        try {
            if (rs != null && rs.next()) {
                Map<String, Object> info = new HashMap<String, Object>();
                info.put(KeyConstants.NAME.toString(), rs.getString("name"));
                info.put(KeyConstants.USERNAME.toString(), rs.getString("username"));
                info.put(KeyConstants.EMAIL.toString(), rs.getString("email"));
                return info;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    protected static synchronized HashMap<Integer, String> getAllUsers() {
        HashMap<Integer, String> userIdToName = new HashMap<Integer, String>();
        String sql = "SELECT userid, name FROM users";
        ResultSet rs = DatabaseUtils.executeQuery(sql);
        try {
            while (rs != null && rs.next()) {
                userIdToName.put(rs.getInt("userid"), rs.getString("name"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return userIdToName;
    }

    protected static synchronized Vector<Integer> getOnlineUsers() {
        Vector<Integer> onlineUserIDs = new Vector<Integer>();
        String sql = "SELECT userid FROM users WHERE status = 'online'";
        ResultSet rs = DatabaseUtils.executeQuery(sql);
        try {
            while (rs != null && rs.next()) {
                onlineUserIDs.add(rs.getInt("userid"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return onlineUserIDs;
    }

    protected static synchronized byte[] getProfilePicture(int userID) {
        String sql = "SELECT profile_picture FROM users WHERE userid = ?";
        ResultSet rs = DatabaseUtils.executeQuery(sql, userID);
        try {
            if (rs != null && rs.next()) {
                return rs.getBytes("profile_picture");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    protected static synchronized void setUserOnlineInDatabase(int userID) {
        String sql = "UPDATE users SET status = 'online' WHERE userid = ?";
        DatabaseUtils.executeUpdate(sql, userID);
    }

    protected static synchronized void setUserOfflineInDatabase(int userID) {
        String sql = "UPDATE users SET status = 'offline' WHERE userid = ?";
        DatabaseUtils.executeUpdate(sql, userID);
    }

    protected static synchronized void setPublicKey(int userID, String publicKeyXml) {
        String sql = "INSERT INTO RSAPublicKey (client_id, public_key) VALUES (?, ?) "
                + "ON DUPLICATE KEY UPDATE public_key = VALUES(public_key)";
        DatabaseUtils.executeUpdate(sql, userID, publicKeyXml);
    }

    protected static synchronized String getPublicKey(int userID) {
        String sql = "SELECT public_key FROM RSAPublicKey WHERE client_id = ?";
        ResultSet rs = DatabaseUtils.executeQuery(sql, userID);
        try {
            if (rs != null && rs.next()) {
                return rs.getString("public_key");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    protected static synchronized void storeUndeliveredMessage(int receiverID, String messageJson) {
        String sql = "INSERT INTO undeliveredMessages (receiver_id, message) VALUES (?, ?)";
        DatabaseUtils.executeUpdate(sql, receiverID, messageJson);
    }

    /** Fetches and deletes everything queued for this user - meant to be flushed once, right after they come online. */
    protected static synchronized List<String> fetchAndClearUndeliveredMessages(int receiverID) {
        List<String> messages = new ArrayList<String>();
        String selectSql = "SELECT message FROM undeliveredMessages WHERE receiver_id = ?";
        ResultSet rs = DatabaseUtils.executeQuery(selectSql, receiverID);
        try {
            while (rs != null && rs.next()) {
                messages.add(rs.getString("message"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (!messages.isEmpty()) {
            String deleteSql = "DELETE FROM undeliveredMessages WHERE receiver_id = ?";
            DatabaseUtils.executeUpdate(deleteSql, receiverID);
        }
        return messages;
    }

    protected static void broadcast(Map<String, Object> message) {
        Collection<Socket> clients = Server.getClients();
        String stringified = JSONHandler.getParser().stringify(message);
        for (Socket client : clients) {
            try {
                PrintWriter writer = new PrintWriter(client.getOutputStream(), true);
                writer.println(stringified);
            } catch (IOException ex) {
                Logger.getLogger(Operations.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
