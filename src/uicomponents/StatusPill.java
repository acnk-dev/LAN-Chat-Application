package uicomponents;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

/**
 * Small colored dot + label - "Online" / "Offline" - shown on the
 * profile page next to the signed-in user's status.
 *
 * @author aditya
 */
public class StatusPill extends JPanel {

    private boolean online;

    public StatusPill() {
        setOpaque(false);
    }

    public void setOnline(boolean online) {
        this.online = online;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        String text = online ? "Online" : "Offline";
        Color dotColor = online ? Theme.GREEN : Theme.TEXT_SECONDARY;

        g2.setFont(Theme.regular(12));
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int totalWidth = 10 + 6 + textWidth;
        int startX = (getWidth() - totalWidth) / 2;

        int dotY = (getHeight() - 10) / 2;
        g2.setColor(dotColor);
        g2.fillOval(startX, dotY, 10, 10);

        g2.setColor(Theme.TEXT_SECONDARY);
        int ty = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(text, startX + 16, ty);
    }
}
