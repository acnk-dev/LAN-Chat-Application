package uicomponents;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

/**
 * Placeholder shown in the conversation pane when no chat is selected yet.
 *
 * @author aditya
 */
public class InitialMessagePanel extends JPanel {

    public InitialMessagePanel() {
        setBackground(Theme.BACKGROUND);
        setOpaque(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int cx = w / 2;
        int iconSize = 90;
        int iconY = h / 2 - 90;

        g2.setColor(Theme.SURFACE);
        g2.fillRoundRect(cx - iconSize / 2, iconY, iconSize, iconSize - 20, 24, 24);
        int[] tailX = {cx - 14, cx - 14, cx + 10};
        int[] tailY = {iconY + iconSize - 20, iconY + iconSize + 4, iconY + iconSize - 20};
        g2.fillPolygon(tailX, tailY, 3);

        String line = "Select a chat to start messaging";
        g2.setFont(Theme.regular(15));
        g2.setColor(Theme.TEXT_SECONDARY);
        FontMetrics fm = g2.getFontMetrics();
        int tx = cx - fm.stringWidth(line) / 2;
        int ty = iconY + iconSize + 50;
        g2.drawString(line, tx, ty);
    }
}
