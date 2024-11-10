package uicomponents;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;

/**
 * Narrow left-most icon rail. This app only has one real screen (chats),
 * so unlike WhatsApp's full rail (chats/calls/status/communities/settings)
 * this only shows a "you are here" chats marker up top and the signed-in
 * user's own avatar at the bottom.
 *
 * @author aditya
 */
public class SideRail extends JPanel {

    public static final int WIDTH = 64;
    private static final int ICON_SIZE = 40;
    private static final int AVATAR_SIZE = 44;

    private String profileName = "?";
    private Runnable onProfileClick;

    public SideRail() {
        setBackground(Theme.SURFACE);
        setOpaque(true);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int avatarY = getHeight() - AVATAR_SIZE - 20;
                if (e.getY() >= avatarY && onProfileClick != null) {
                    onProfileClick.run();
                }
            }
        });
    }

    public void setProfileName(String name) {
        this.profileName = (name == null || name.length() == 0) ? "?" : name;
        repaint();
    }

    public void setOnProfileClick(Runnable handler) {
        this.onProfileClick = handler;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int cx = getWidth() / 2;

        // Soft glow behind the icon so it reads as "you are here" rather
        // than a flat, isolated shape.
        int iconY = 26;
        g2.setColor(withAlpha(Theme.ACCENT, 40));
        g2.fillRoundRect(cx - ICON_SIZE / 2 - 10, iconY - 10, ICON_SIZE + 20, ICON_SIZE + 10, 18, 18);

        g2.setColor(Theme.ACCENT);
        g2.fillRoundRect(cx - ICON_SIZE / 2, iconY, ICON_SIZE, ICON_SIZE - 8, 12, 12);
        int[] tailX = {cx - 6, cx - 6, cx + 6};
        int[] tailY = {iconY + ICON_SIZE - 8, iconY + ICON_SIZE + 2, iconY + ICON_SIZE - 8};
        g2.fillPolygon(tailX, tailY, 3);

        // Three small message dots inside the bubble instead of a flat fill
        g2.setColor(Theme.BACKGROUND);
        int dotY = iconY + (ICON_SIZE - 8) / 2 - 2;
        g2.fillOval(cx - 10, dotY, 4, 4);
        g2.fillOval(cx - 2, dotY, 4, 4);
        g2.fillOval(cx + 6, dotY, 4, 4);

        int avatarY = getHeight() - AVATAR_SIZE - 20;
        Color avatarColor = Theme.avatarColorFor(profileName);
        g2.setColor(avatarColor);
        g2.fillOval(cx - AVATAR_SIZE / 2, avatarY, AVATAR_SIZE, AVATAR_SIZE);
        String initial = profileName.substring(0, 1).toUpperCase();
        g2.setColor(Theme.BACKGROUND);
        g2.setFont(Theme.bold(16));
        FontMetrics fm = g2.getFontMetrics();
        int tx = cx - fm.stringWidth(initial) / 2;
        int ty = avatarY + (AVATAR_SIZE - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(initial, tx, ty);
    }

    private Color withAlpha(Color c, int alpha) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
    }
}
