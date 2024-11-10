package uicomponents;

import java.awt.BasicStroke;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

/**
 * Hand-painted "back" chevron, used anywhere a screen needs a way to step
 * back to whatever it replaced (ProfilePanel's own back button, the
 * conversation header's close-chat button). Not a font glyph - Unicode
 * arrow/chevron characters aren't guaranteed to be in every installed
 * font and can render as a blank box.
 *
 * @author aditya
 */
public class BackChevron extends JPanel {

    private boolean hovered;

    public BackChevron() {
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public void setHovered(boolean hovered) {
        this.hovered = hovered;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        int cx = getWidth() / 2 + 2;
        int cy = getHeight() / 2;
        int size = 5;

        g2.setColor(hovered ? Theme.ACCENT : Theme.TEXT_SECONDARY);
        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(cx, cy - size, cx - size, cy);
        g2.drawLine(cx - size, cy, cx, cy + size);
    }
}
