package main;

import java.util.ArrayList;
import java.util.List;
import uicomponents.ChatItem;
import uicomponents.ContentArea;
import uicomponents.ConversationPanel;
import uicomponents.SideRail;
import uicomponents.TitleBar;

/**
 * Main chat shell: icon rail, contact list, conversation pane. UI only for
 * now - selecting a chat opens the conversation view and typing+sending
 * locally echoes a sent bubble so layout can be checked, but there is no
 * real contact list or networking here yet (that's the server/client port
 * in a later step).
 *
 * @author ancla
 */
public class Main extends javax.swing.JFrame {

    public static final int WINDOW_WIDTH = 1280;
    public static final int CONTENT_HEIGHT = 676;

    private final List<ChatItem> chatItems = new ArrayList<ChatItem>();

    public Main() {
        setUndecorated(true);
        initComponents();
        titleBar.setTitleText("LANchat");
        titleBar.configureForWidth(WINDOW_WIDTH);
        titleBar.enableDragging(this);
        sideRail.setProfileName("Aditya Sharma");
        sideRail.setOnProfileClick(new Runnable() {
            @Override
            public void run() {
                // TODO: pass the real signed-in user's details once LOGIN is wired up
                contentArea.getProfilePanel().setUserDetails("Aditya Sharma", "aditya", "aditya@example.com", true);
                contentArea.showProfile();
            }
        });
        contentArea.getProfilePanel().setOnLogOut(new Runnable() {
            @Override
            public void run() {
                // TODO: wire up logout/disconnect + return to Login (copy over from old project)
            }
        });
        contentArea.getConversationPanel().setSendListener(new ConversationPanel.MessageSendListener() {
            @Override
            public void onSend(String contactUsername, String message) {
                // TODO: wire up SEND_MESSAGE network call here (copy over from old project)
            }
        });
        contentArea.getConversationPanel().setOnClose(new Runnable() {
            @Override
            public void run() {
                for (ChatItem item : chatItems) {
                    item.setSelected(false);
                }
            }
        });
        setSize(WINDOW_WIDTH, CONTENT_HEIGHT + TitleBar.HEIGHT);
        setLocationRelativeTo(null);

        populateDemoChats();
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

    /**
     * Preview-only sample rows so the contact list isn't empty while the
     * UI is being eyeballed in NetBeans - remove once the real contact
     * list is wired up from the server (step 9-13).
     */
    private void populateDemoChats() {
        addDemoChat("Omkar Talgaonkar", "Lol", "2:17 PM", 0, true);
        addDemoChat("Nnk Jio", "Photo", "10:17 PM", 1, false);
        addDemoChat("Harsh Patil", "See you tomorrow", "9:02 PM", 3, true);

        int totalUnread = 0;
        for (ChatItem item : chatItems) {
            totalUnread += item.getUnreadCount();
        }
        contentArea.getChatListUI().setUnreadTotal(totalUnread);
    }

    private void addDemoChat(String contactName, String lastMessage, String timestamp, int unread, boolean online) {
        final ChatItem item = new ChatItem(contactName, lastMessage, timestamp, unread);
        item.setOnline(online);
        item.setOnClick(new Runnable() {
            @Override
            public void run() {
                selectChat(item);
            }
        });
        chatItems.add(item);
        contentArea.getChatListUI().addChatItem(item);
    }

    private void selectChat(ChatItem chosen) {
        for (ChatItem item : chatItems) {
            item.setSelected(item == chosen);
        }
        contentArea.getConversationPanel().openChat(chosen.getContactName(), chosen.getContactName());
        contentArea.getConversationPanel().setOnlineStatus(chosen.isOnline());
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
