package uicomponents;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 * Right-hand pane: swaps between the empty-state placeholder and an
 * active conversation (header + scrollable bubble list + input bar).
 *
 * Sending currently just echoes the typed text back as a local sent
 * bubble so the bubble layout/wrapping can be eyeballed while the UI is
 * being built - this gets replaced by a real SEND_MESSAGE call once the
 * client/server network layer is ported over.
 *
 * @author aditya
 */
public class ConversationPanel extends JPanel {

    private static final String CARD_EMPTY = "empty";
    private static final String CARD_ACTIVE = "active";

    private final CardLayout cardLayout = new CardLayout();
    private final AvatarLabel headerAvatar = new AvatarLabel();
    private final JLabel headerName = new JLabel();
    private final JPanel messageColumn = new JPanel();
    private final JScrollPane messageScroll;
    private final JTextField inputField = new JTextField();

    private String activeContact;
    private MessageSendListener sendListener;
    private Runnable onClose;

    public interface MessageSendListener {
        void onSend(String contactUsername, String message);
    }

    public ConversationPanel() {
        setLayout(cardLayout);
        setBackground(Theme.BACKGROUND);

        add(new InitialMessagePanel(), CARD_EMPTY);

        JPanel activePanel = new JPanel(new BorderLayout());
        activePanel.setBackground(Theme.BACKGROUND);
        activePanel.add(buildHeader(), BorderLayout.NORTH);

        messageColumn.setOpaque(false);
        messageColumn.setLayout(new BoxLayout(messageColumn, BoxLayout.Y_AXIS));
        messageColumn.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));

        messageScroll = new JScrollPane(messageColumn);
        messageScroll.setBorder(BorderFactory.createEmptyBorder());
        messageScroll.setOpaque(false);
        messageScroll.getViewport().setOpaque(false);
        messageScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        messageScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        activePanel.add(messageScroll, BorderLayout.CENTER);

        activePanel.add(buildInputBar(), BorderLayout.SOUTH);

        add(activePanel, CARD_ACTIVE);
        cardLayout.show(this, CARD_EMPTY);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.SURFACE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)));
        header.setPreferredSize(new Dimension(0, 64));
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));

        headerAvatar.setPreferredSize(new Dimension(40, 40));
        headerAvatar.setMaximumSize(new Dimension(40, 40));
        headerName.setFont(Theme.bold(15));
        headerName.setForeground(Theme.TEXT_PRIMARY);
        headerName.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 0));

        final BackChevron closeChat = new BackChevron();
        closeChat.setPreferredSize(new Dimension(28, 28));
        closeChat.setToolTipText("Back to chats");
        closeChat.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showEmptyState();
                if (onClose != null) {
                    onClose.run();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                closeChat.setHovered(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                closeChat.setHovered(false);
            }
        });

        // Grouped in a left-hugging sub-panel (chevron, then identity) so
        // BorderLayout gives it only its own preferred width instead of
        // letting the name label stretch and strand the button elsewhere.
        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.X_AXIS));
        left.add(closeChat);
        left.add(Box.createHorizontalStrut(14));
        left.add(headerAvatar);
        left.add(headerName);

        header.add(left, BorderLayout.WEST);
        return header;
    }

    private JPanel buildInputBar() {
        JPanel bar = new JPanel(new BorderLayout(10, 0));
        bar.setBackground(Theme.SURFACE);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.BORDER),
                BorderFactory.createEmptyBorder(12, 20, 12, 20)));

        JLabel plusIcon = new JLabel("+", SwingConstants.CENTER);
        plusIcon.setFont(Theme.bold(20));
        plusIcon.setForeground(Theme.TEXT_SECONDARY);
        plusIcon.setPreferredSize(new Dimension(30, 30));
        plusIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));
        plusIcon.setToolTipText("Attachments - not implemented yet");

        inputField.setFont(Theme.regular(14));
        inputField.setForeground(Theme.TEXT_PRIMARY);
        inputField.setBackground(Theme.SURFACE_HOVER);
        inputField.setCaretColor(Theme.ACCENT);
        inputField.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        setPlaceholder(inputField, "Type a message");
        inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendCurrentMessage();
            }
        });

        SendButton sendIcon = new SendButton();
        sendIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                sendCurrentMessage();
            }
        });

        bar.add(plusIcon, BorderLayout.WEST);
        bar.add(inputField, BorderLayout.CENTER);
        bar.add(sendIcon, BorderLayout.EAST);
        return bar;
    }

    private void setPlaceholder(final JTextField field, final String placeholder) {
        field.setText(placeholder);
        field.setForeground(Theme.TEXT_SECONDARY);
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Theme.TEXT_PRIMARY);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().length() == 0) {
                    field.setText(placeholder);
                    field.setForeground(Theme.TEXT_SECONDARY);
                }
            }
        });
    }

    private void sendCurrentMessage() {
        String text = inputField.getText().trim();
        if (text.length() == 0 || activeContact == null) {
            return;
        }
        addMessage(new SentMessagePanel(text, currentTimestamp()), true);
        inputField.setText("");
        if (sendListener != null) {
            sendListener.onSend(activeContact, text);
        }
    }

    public void setSendListener(MessageSendListener listener) {
        this.sendListener = listener;
    }

    /** Fired when the header's back/close button is clicked, after this panel has already switched to the empty state. */
    public void setOnClose(Runnable onClose) {
        this.onClose = onClose;
    }

    public void openChat(String contactUsername, String displayName) {
        this.activeContact = contactUsername;
        headerName.setText(displayName);
        headerAvatar.setInitial(displayName);
        messageColumn.removeAll();
        messageColumn.revalidate();
        messageColumn.repaint();
        cardLayout.show(this, CARD_ACTIVE);
    }

    public void setOnlineStatus(boolean online) {
        headerAvatar.setOnline(online);
    }

    public void showEmptyState() {
        this.activeContact = null;
        cardLayout.show(this, CARD_EMPTY);
    }

    public void addMessage(MessagePanel bubble, boolean sentByMe) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setBorder(BorderFactory.createEmptyBorder(2, 16, 2, 16));
        row.add(bubble, sentByMe ? BorderLayout.EAST : BorderLayout.WEST);
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, bubble.getPreferredSize().height + 12));
        messageColumn.add(row);
        messageColumn.revalidate();
        messageColumn.repaint();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JScrollBar bar = messageScroll.getVerticalScrollBar();
                bar.setValue(bar.getMaximum());
            }
        });
    }

    private String currentTimestamp() {
        SimpleDateFormat fmt = new SimpleDateFormat("h:mm a");
        return fmt.format(new Date());
    }

    /**
     * Round accent-filled send button with a hand-painted triangle - not a
     * font glyph, since Unicode arrow characters (e.g. U+27A4) aren't
     * guaranteed to be in every installed font and can render as a blank
     * "tofu" box (this happened on the actual send arrow before).
     */
    private static class SendButton extends JPanel {
        SendButton() {
            setOpaque(false);
            setPreferredSize(new Dimension(32, 32));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int size = Math.min(getWidth(), getHeight());
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;

            g2.setColor(Theme.ACCENT);
            g2.fillOval(x, y, size, size);

            int cx = x + size / 2;
            int cy = y + size / 2;
            int triW = size / 3;
            int triH = (int) (triW * 1.1);
            int[] triX = {cx - triW / 2 + 1, cx - triW / 2 + 1, cx + triW / 2 + 1};
            int[] triY = {cy - triH / 2, cy + triH / 2, cy};
            g2.setColor(Theme.BACKGROUND);
            g2.fillPolygon(triX, triY, 3);
        }
    }

    /** Small circular avatar with an initial letter, used in the conversation header. */
    private static class AvatarLabel extends JLabel {
        private String initial = "?";
        private java.awt.Color bgColor = Theme.ACCENT;
        private boolean online;

        void setInitial(String name) {
            String source = (name == null || name.length() == 0) ? "?" : name;
            this.initial = source.substring(0, 1).toUpperCase();
            this.bgColor = Theme.avatarColorFor(source);
            repaint();
        }

        void setOnline(boolean online) {
            this.online = online;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fillOval(0, 0, getWidth(), getHeight());
            g2.setColor(Theme.BACKGROUND);
            g2.setFont(Theme.bold(14));
            FontMetrics fm = g2.getFontMetrics();
            int tx = (getWidth() - fm.stringWidth(initial)) / 2;
            int ty = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(initial, tx, ty);

            if (online) {
                int dotSize = 11;
                int dx = getWidth() - dotSize;
                int dy = getHeight() - dotSize;
                g2.setColor(Theme.SURFACE);
                g2.fillOval(dx - 2, dy - 2, dotSize + 4, dotSize + 4);
                g2.setColor(Theme.GREEN);
                g2.fillOval(dx, dy, dotSize, dotSize);
            }
        }
    }
}
