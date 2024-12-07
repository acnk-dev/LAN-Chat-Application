package uicomponents;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;

/**
 * A single row in the contact list: avatar, name, last-message preview,
 * timestamp, and an unread-count badge. Purely a visual component - it
 * knows nothing about where its data came from or how to fetch a real
 * profile picture; both get wired in once the server/client network
 * layer is ported over.
 *
 * @author aditya
 */
public class ChatItem extends JPanel {

    public static final int HEIGHT = 72;
    private static final int AVATAR_SIZE = 44;

    private final int contactUserId;
    private final String contactName;
    private String lastMessage;
    private String timestamp;
    private int unreadCount;
    private boolean selected;
    private boolean online;
    private Image profileImage;
    private Runnable onClick;

    public ChatItem(int contactUserId, String contactName, String lastMessage, String timestamp, int unreadCount) {
        this.contactUserId = contactUserId;
        this.contactName = contactName;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
        this.unreadCount = unreadCount;

        setOpaque(true);
        setBackground(Theme.SURFACE);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(0, HEIGHT));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (onClick != null) {
                    onClick.run();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (!selected) {
                    setBackground(Theme.SURFACE_HOVER);
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!selected) {
                    setBackground(Theme.SURFACE);
                    repaint();
                }
            }
        });
    }

    public void setOnClick(Runnable onClick) {
        this.onClick = onClick;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        setBackground(selected ? Theme.SURFACE_PRESSED : Theme.SURFACE);
        repaint();
    }

    public void setOnline(boolean online) {
        this.online = online;
        repaint();
    }

    public boolean isOnline() {
        return online;
    }

    public void updatePreview(String lastMessage, String timestamp, int unreadCount) {
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
        this.unreadCount = unreadCount;
        repaint();
    }

    public void clearUnread() {
        this.unreadCount = 0;
        repaint();
    }

    public String getContactName() {
        return contactName;
    }

    public int getContactUserId() {
        return contactUserId;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setProfileImage(Image image) {
        this.profileImage = image;
        repaint();
    }

    public Image getProfileImage() {
        return profileImage;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        if (selected) {
            g2.setColor(Theme.ACCENT);
            g2.fillRect(0, 0, 4, HEIGHT);
        }

        int avatarY = (HEIGHT - AVATAR_SIZE) / 2;
        AvatarPainter.paint(g2, 16, avatarY, AVATAR_SIZE, profileImage, Theme.avatarColorFor(contactName), contactName);

        if (online) {
            int dotSize = 12;
            int dx = 16 + AVATAR_SIZE - dotSize + 2;
            int dy = avatarY + AVATAR_SIZE - dotSize + 2;
            g2.setColor(getBackground());
            g2.fillOval(dx - 2, dy - 2, dotSize + 4, dotSize + 4);
            g2.setColor(Theme.GREEN);
            g2.fillOval(dx, dy, dotSize, dotSize);
        }

        int textX = 16 + AVATAR_SIZE + 14;
        int textWidth = getWidth() - textX - 60;

        g2.setFont(Theme.bold(14));
        g2.setColor(Theme.TEXT_PRIMARY);
        g2.drawString(contactName, textX, 30);

        g2.setFont(Theme.regular(12));
        g2.setColor(Theme.TEXT_SECONDARY);
        g2.drawString(truncate(g2, lastMessage, textWidth), textX, 50);

        g2.setFont(Theme.regular(11));
        FontMetrics tsFm = g2.getFontMetrics();
        g2.setColor(Theme.TEXT_SECONDARY);
        int tsX = getWidth() - tsFm.stringWidth(timestamp) - 16;
        g2.drawString(timestamp, tsX, 26);

        if (unreadCount > 0) {
            String badgeText = unreadCount > 99 ? "99+" : String.valueOf(unreadCount);
            g2.setFont(Theme.bold(10));
            FontMetrics bfm = g2.getFontMetrics();
            int diameter = Math.max(18, bfm.stringWidth(badgeText) + 10);
            int bx = getWidth() - diameter - 14;
            int by = 40;
            g2.setColor(Theme.ORANGE);
            g2.fillOval(bx, by, diameter, 18);
            g2.setColor(Theme.BACKGROUND);
            int btx = bx + (diameter - bfm.stringWidth(badgeText)) / 2;
            int bty = by + (18 - bfm.getHeight()) / 2 + bfm.getAscent();
            g2.drawString(badgeText, btx, bty);
        }
    }

    private String truncate(Graphics2D g2, String text, int maxWidth) {
        FontMetrics fm = g2.getFontMetrics();
        if (fm.stringWidth(text) <= maxWidth) {
            return text;
        }
        String ellipsis = "...";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            sb.append(text.charAt(i));
            if (fm.stringWidth(sb.toString() + ellipsis) > maxWidth) {
                sb.setLength(Math.max(0, sb.length() - 1));
                break;
            }
        }
        return sb.toString() + ellipsis;
    }
}
