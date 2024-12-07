package main;

import java.awt.Image;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import client.Client;
import client.CryptoSession;
import client.LocalStorage;
import client.MessageProcessor;
import constants.ActionConstants;
import constants.KeyConstants;
import login.Login;
import rsa.RSAPublicKey;
import uicomponents.AvatarPainter;
import uicomponents.ChatItem;
import uicomponents.ContentArea;
import uicomponents.ConversationPanel;
import uicomponents.ReceivedMessagePanel;
import uicomponents.SentMessagePanel;
import uicomponents.SideRail;
import uicomponents.TitleBar;
import xml.XMLHandler;

/**
 * Main chat shell: icon rail, contact list, conversation pane. Contact
 * list, online status, profile pictures, and CHAT_MESSAGE send/receive
 * (DES-per-message + RSA-wrapped key, server-blind relay) are all real.
 *
 * @author ancla
 */
public class Main extends javax.swing.JFrame {

    public static final int WINDOW_WIDTH = 1280;
    public static final int CONTENT_HEIGHT = 676;

    private static Main instance;

    private final Map<Integer, ChatItem> chatItemsById = new LinkedHashMap<Integer, ChatItem>();
    // Undelivered messages flush immediately on login, before the
    // FETCH_ALL_USERS reply has populated chatItemsById - unread counts
    // have to survive independently of the ChatItem existing yet, or a
    // message that arrives in that window silently loses its badge.
    private final Map<Integer, Integer> unreadCounts = new HashMap<Integer, Integer>();
    private int userID;
    private String userName;
    private String userUsername;
    private String userEmail;

    public Main(int userID, String userName, String userUsername, String userEmail) {
        this();
        this.userID = userID;
        this.userName = userName;
        this.userUsername = userUsername;
        this.userEmail = userEmail;

        sideRail.setProfileName(userName);
        sideRail.setOnProfileClick(new Runnable() {
            @Override
            public void run() {
                contentArea.getProfilePanel().setUserDetails(Main.this.userName, Main.this.userUsername, Main.this.userEmail, true);
                contentArea.showProfile();
            }
        });

        requestAllUsers();
        requestOnlineUsers();
        requestProfilePicture(userID);
        sendOwnPublicKey();
    }

    public Main() {
        instance = this;
        setUndecorated(true);
        initComponents();
        titleBar.setTitleText("LANchat");
        titleBar.configureForWidth(WINDOW_WIDTH);
        titleBar.enableDragging(this);

        contentArea.getProfilePanel().setOnLogOut(new Runnable() {
            @Override
            public void run() {
                Client.getClient().disconnect();
                Client.reset();
                CryptoSession.reset();
                Main.this.dispose();
                new Login().setVisible(true);
            }
        });
        contentArea.getConversationPanel().setSendListener(new ConversationPanel.MessageSendListener() {
            @Override
            public void onSend(int contactUserId, String message) {
                String timestamp = currentTimestamp();
                LocalStorage.append(Main.this.userID, contactUserId, message, timestamp, true);
                ChatItem item = chatItemsById.get(contactUserId);
                if (item != null) {
                    item.updatePreview(message, timestamp, 0);
                }
                MessageProcessor.sendChatMessage(contactUserId, Main.this.userID, message);
            }
        });
        contentArea.getConversationPanel().setOnClose(new Runnable() {
            @Override
            public void run() {
                for (ChatItem item : chatItemsById.values()) {
                    item.setSelected(false);
                }
            }
        });
        setSize(WINDOW_WIDTH, CONTENT_HEIGHT + TitleBar.HEIGHT);
        setLocationRelativeTo(null);

        preventSearchFieldFromStealingInitialFocus();
    }

    /**
     * Swing hands keyboard focus to the first focusable component on
     * window open, which happened to be the search field - that
     * immediately fired its focus listener and wiped the "Search or
     * start a new chat" placeholder before anyone saw it. Steal focus
     * back to a neutral, non-editable component once the window is
     * actually showing.
     */
    private void preventSearchFieldFromStealingInitialFocus() {
        sideRail.setFocusable(true);
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                sideRail.requestFocusInWindow();
            }
        });
    }

    private void requestAllUsers() {
        Map<String, Object> request = new HashMap<String, Object>();
        request.put(KeyConstants.ACTION.toString(), ActionConstants.FETCH_ALL_USERS);
        MessageProcessor.handleSendMessage(request);
    }

    private void requestOnlineUsers() {
        Map<String, Object> request = new HashMap<String, Object>();
        request.put(KeyConstants.ACTION.toString(), ActionConstants.FETCH_ONLINE_USERS);
        MessageProcessor.handleSendMessage(request);
    }

    private void requestProfilePicture(int targetUserId) {
        Map<String, Object> request = new HashMap<String, Object>();
        request.put(KeyConstants.ACTION.toString(), ActionConstants.FETCH_PROFILE_PICTURE);
        request.put(KeyConstants.USER_ID.toString(), targetUserId);
        MessageProcessor.handleSendMessage(request);
    }

    /** Publishes this user's RSA public key so other clients can encrypt messages to them. */
    private void sendOwnPublicKey() {
        RSAPublicKey publicKey = CryptoSession.getOwnPublicKey();
        Map<String, Object> request = new HashMap<String, Object>();
        request.put(KeyConstants.ACTION.toString(), ActionConstants.SET_PUBLIC_KEY);
        request.put(KeyConstants.PUBLIC_KEY.toString(), XMLHandler.stringifyRSAPublicKey(publicKey));
        MessageProcessor.handleSendMessage(request);
    }

    private void addContact(int contactUserId, String name) {
        final ChatItem item = new ChatItem(contactUserId, name, "No messages yet", "", 0);
        List<LocalStorage.StoredMessage> history = LocalStorage.load(userID, contactUserId);
        if (!history.isEmpty()) {
            LocalStorage.StoredMessage last = history.get(history.size() - 1);
            Integer unread = unreadCounts.get(contactUserId);
            item.updatePreview(last.text, last.timestamp, unread != null ? unread : 0);
        }
        item.setOnClick(new Runnable() {
            @Override
            public void run() {
                selectChat(item);
            }
        });
        chatItemsById.put(contactUserId, item);
        contentArea.getChatListUI().addChatItem(item);
        requestProfilePicture(contactUserId);
    }

    private void selectChat(ChatItem chosen) {
        for (ChatItem item : chatItemsById.values()) {
            item.setSelected(item == chosen);
        }
        contentArea.getConversationPanel().openChat(chosen.getContactUserId(), chosen.getContactName(), chosen.getContactName());
        contentArea.getConversationPanel().setOnlineStatus(chosen.isOnline());
        for (LocalStorage.StoredMessage stored : LocalStorage.load(userID, chosen.getContactUserId())) {
            uicomponents.MessagePanel bubble = stored.sentByMe
                    ? new SentMessagePanel(stored.text, stored.timestamp)
                    : new ReceivedMessagePanel(stored.text, stored.timestamp);
            contentArea.getConversationPanel().addMessage(bubble, stored.sentByMe);
        }
        // openChat() always resets the header avatar to the initial-letter
        // fallback, since it has no idea whether a picture was already
        // fetched - the ChatItem does know (it cached it when the
        // FETCH_PROFILE_PICTURE reply arrived, likely before this chat was
        // ever opened), so pull it back in right after.
        contentArea.getConversationPanel().setProfileImage(chosen.getContactUserId(), chosen.getProfileImage());

        chosen.clearUnread();
        unreadCounts.put(chosen.getContactUserId(), 0);
        contentArea.getChatListUI().setUnreadTotal(totalUnreadCount());
    }

    private int totalUnreadCount() {
        int total = 0;
        for (ChatItem item : chatItemsById.values()) {
            total += item.getUnreadCount();
        }
        return total;
    }

    public static void populateContacts(final List<Integer> userIds, final List<String> userNames) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (instance != null) {
                    instance.doPopulateContacts(userIds, userNames);
                }
            }
        });
    }

    private void doPopulateContacts(List<Integer> userIds, List<String> userNames) {
        contentArea.getChatListUI().clearChats();
        chatItemsById.clear();
        for (int i = 0; i < userIds.size(); i++) {
            int contactUserId = userIds.get(i);
            if (contactUserId == userID) {
                continue;
            }
            addContact(contactUserId, userNames.get(i));
        }
        contentArea.getChatListUI().setUnreadTotal(totalUnreadCount());
    }

    public static void updateOnlineUsers(final Vector<Integer> onlineUserIds) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (instance != null) {
                    instance.doUpdateOnlineUsers(onlineUserIds);
                }
            }
        });
    }

    private void doUpdateOnlineUsers(Vector<Integer> onlineUserIds) {
        for (Map.Entry<Integer, ChatItem> entry : chatItemsById.entrySet()) {
            entry.getValue().setOnline(onlineUserIds.contains(entry.getKey()));
        }
    }

    public static void updateUserStatus(final int userID, final boolean isOnline) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (instance != null) {
                    instance.doUpdateUserStatus(userID, isOnline);
                }
            }
        });
    }

    private void doUpdateUserStatus(int targetUserId, boolean isOnline) {
        if (isOnline) {
            // They just logged in, which means a brand new RSA keypair -
            // any public key we had cached for them is now stale.
            CryptoSession.invalidatePublicKey(targetUserId);
        }
        ChatItem item = chatItemsById.get(targetUserId);
        if (item != null) {
            item.setOnline(isOnline);
        }
        if (contentArea.getConversationPanel().getActiveContactUserId() == targetUserId) {
            contentArea.getConversationPanel().setOnlineStatus(isOnline);
        }
    }

    public static void updateProfilePicture(final int targetUserId, final byte[] imageData) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (instance != null) {
                    instance.doUpdateProfilePicture(targetUserId, imageData);
                }
            }
        });
    }

    private void doUpdateProfilePicture(int targetUserId, byte[] imageData) {
        Image image = AvatarPainter.decode(imageData);
        if (image == null) {
            return;
        }
        if (targetUserId == userID) {
            sideRail.setProfileImage(image);
            contentArea.getProfilePanel().setProfileImage(image);
            return;
        }
        ChatItem item = chatItemsById.get(targetUserId);
        if (item != null) {
            item.setProfileImage(image);
        }
        contentArea.getConversationPanel().setProfileImage(targetUserId, image);
    }

    /** A queued outgoing message got dropped because the recipient has never logged in - no public key exists to encrypt against. */
    public static void messageDeliveryFailed(final int contactUserId, final int messageCount) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (instance != null) {
                    instance.doMessageDeliveryFailed(contactUserId, messageCount);
                }
            }
        });
    }

    private void doMessageDeliveryFailed(int contactUserId, int messageCount) {
        ChatItem item = chatItemsById.get(contactUserId);
        String name = item != null ? item.getContactName() : "This user";
        String plural = messageCount == 1 ? "message" : "messages";
        javax.swing.JOptionPane.showMessageDialog(this,
                name + " hasn't finished setting up their account yet, so " + messageCount + " " + plural
                        + " couldn't be sent. Try again once they've logged in at least once.",
                "Message not delivered", javax.swing.JOptionPane.WARNING_MESSAGE);
    }

    public static void receiveMessage(final int senderID, final String message) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (instance != null) {
                    instance.doReceiveMessage(senderID, message);
                }
            }
        });
    }

    private void doReceiveMessage(int senderID, String message) {
        String timestamp = currentTimestamp();
        LocalStorage.append(userID, senderID, message, timestamp, false);
        boolean isActiveChat = contentArea.getConversationPanel().getActiveContactUserId() == senderID;
        if (isActiveChat) {
            contentArea.getConversationPanel().addMessage(new ReceivedMessagePanel(message, timestamp), false);
        }

        int unreadCount;
        if (isActiveChat) {
            unreadCount = 0;
            unreadCounts.put(senderID, 0);
        } else {
            Integer current = unreadCounts.get(senderID);
            unreadCount = (current == null ? 0 : current) + 1;
            unreadCounts.put(senderID, unreadCount);
        }

        ChatItem item = chatItemsById.get(senderID);
        if (item != null) {
            item.updatePreview(message, timestamp, unreadCount);
            contentArea.getChatListUI().setUnreadTotal(totalUnreadCount());
        }
    }

    private String currentTimestamp() {
        return new SimpleDateFormat("h:mm a").format(new Date());
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        titleBar = new uicomponents.TitleBar();
        sideRail = new uicomponents.SideRail();
        contentArea = new uicomponents.ContentArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("LANchat");
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        getContentPane().add(titleBar, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1280, 44));
        getContentPane().add(sideRail, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 44, 64, 676));
        getContentPane().add(contentArea, new org.netbeans.lib.awtextra.AbsoluteConstraints(64, 44, 1216, 676));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public static void open(final int userID, final String name, final String username, final String email) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Main(userID, name, username, email).setVisible(true);
            }
        });
    }

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Main().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private uicomponents.ContentArea contentArea;
    private uicomponents.SideRail sideRail;
    private uicomponents.TitleBar titleBar;
    // End of variables declaration//GEN-END:variables
}
