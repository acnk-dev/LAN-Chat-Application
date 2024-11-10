package uicomponents;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import javax.swing.JPanel;

/**
 * Everything to the right of SideRail: swaps between the normal chat view
 * (contact list + conversation pane side by side) and the profile view,
 * which takes over this entire area rather than being squeezed into the
 * narrow contact-list column.
 *
 * @author aditya
 */
public class ContentArea extends JPanel {

    private static final String CARD_CHAT = "chat";
    private static final String CARD_PROFILE = "profile";
    private static final int CHAT_LIST_WIDTH = 360;

    private final CardLayout cardLayout = new CardLayout();
    private final ChatListUI chatListUI = new ChatListUI();
    private final ConversationPanel conversationPanel = new ConversationPanel();
    private final ProfilePanel profilePanel = new ProfilePanel();

    public ContentArea() {
        setLayout(cardLayout);

        JPanel chatCard = new JPanel(new BorderLayout());
        chatCard.setOpaque(false);
        chatListUI.setPreferredSize(new Dimension(CHAT_LIST_WIDTH, 0));
        chatCard.add(chatListUI, BorderLayout.WEST);
        chatCard.add(conversationPanel, BorderLayout.CENTER);

        add(chatCard, CARD_CHAT);
        add(profilePanel, CARD_PROFILE);
        cardLayout.show(this, CARD_CHAT);

        profilePanel.setOnBack(new Runnable() {
            @Override
            public void run() {
                showChat();
            }
        });
    }

    public ChatListUI getChatListUI() {
        return chatListUI;
    }

    public ConversationPanel getConversationPanel() {
        return conversationPanel;
    }

    public ProfilePanel getProfilePanel() {
        return profilePanel;
    }

    public void showChat() {
        cardLayout.show(this, CARD_CHAT);
    }

    public void showProfile() {
        cardLayout.show(this, CARD_PROFILE);
    }
}
