package uicomponents;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

/**
 * The right-hand illustration panel used on Login/Register. Everything is
 * hand-painted with Java2D - gradient background (Tokyo Night blue fading
 * into the app's dark background), decorative circles, a little stack of
 * chat-bubble shapes, and a heading/subheading.
 *
 * Deliberately has no dependency on any image file: the old project's UI
 * broke on other machines specifically because of hardcoded absolute image
 * paths, so this side of the app just doesn't have that failure mode at
 * all.
 *
 * Heading/subheading are set via setters (called from the screen's own
 * constructor, after initComponents()) rather than a custom constructor,
 * so this stays a plain no-arg-constructible bean that the NetBeans form
 * editor is happy to instantiate on its own.
 *
 * @author aditya
 */
public class GradientSidePanel extends JPanel {

    private String heading = "";
    private String subheading = "";

    public GradientSidePanel() {
        setOpaque(false);
    }

    public void setHeading(String heading) {
        this.heading = heading;
        repaint();
    }

    public void setSubheading(String subheading) {
        this.subheading = subheading;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        int w = getWidth();
        int h = getHeight();

        paintBackground(g2, w, h);
        paintDecorativeCircles(g2, w, h);
        paintChatBubbles(g2, w, h);
        paintCaption(g2, w, h);

        g2.dispose();
    }

    private void paintBackground(Graphics2D g2, int w, int h) {
        GradientPaint gradient = new GradientPaint(
                0, 0, Theme.GRADIENT_START,
                w, h, Theme.GRADIENT_END);
        g2.setPaint(gradient);
        g2.fillRect(0, 0, w, h);
    }

    private void paintDecorativeCircles(Graphics2D g2, int w, int h) {
        g2.setColor(withAlpha(Theme.ACCENT, 30));
        g2.fillOval(w - 140, 50, 180, 180);

        g2.setColor(withAlpha(Theme.ACCENT_HOVER, 22));
        g2.fillOval(-70, h - 210, 220, 220);

        g2.setColor(withAlpha(Theme.TEXT_PRIMARY, 16));
        g2.fillOval(w / 2 - 30, h - 340, 90, 90);
    }

    private void paintChatBubbles(Graphics2D g2, int w, int h) {
        int cx = w / 2;
        int topY = h / 2 - 150;

        g2.setColor(withAlpha(Theme.TEXT_PRIMARY, 235));
        g2.fillRoundRect(cx - 130, topY, 200, 70, 26, 26);
        g2.setColor(Theme.BTN_FILL_PRESSED);
        g2.fillOval(cx - 105, topY + 26, 14, 14);
        g2.fillOval(cx - 80, topY + 26, 14, 14);
        g2.fillOval(cx - 55, topY + 26, 14, 14);

        g2.setColor(withAlpha(Theme.TEXT_PRIMARY, 150));
        g2.fillRoundRect(cx - 40, topY + 85, 190, 60, 24, 24);

        g2.setColor(withAlpha(Theme.TEXT_PRIMARY, 100));
        g2.fillRoundRect(cx - 110, topY + 160, 150, 50, 22, 22);
    }

    private void paintCaption(Graphics2D g2, int w, int h) {
        int blockY = h - 160;

        g2.setColor(Theme.TEXT_PRIMARY);
        g2.setFont(Theme.bold(26));
        drawCenteredLine(g2, heading, w, blockY);

        g2.setFont(Theme.regular(15));
        g2.setColor(withAlpha(Theme.TEXT_SECONDARY, 230));
        drawWrappedCentered(g2, subheading, w, blockY + 34, 40);
    }

    private Color withAlpha(Color base, int alpha) {
        return new Color(base.getRed(), base.getGreen(), base.getBlue(), alpha);
    }

    private void drawWrappedCentered(Graphics2D g2, String text, int panelWidth, int startY, int margin) {
        if (text == null || text.length() == 0) {
            return;
        }
        FontMetrics fm = g2.getFontMetrics();
        int maxWidth = panelWidth - margin * 2;
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        int y = startY;

        for (int i = 0; i < words.length; i++) {
            String candidate = line.length() == 0 ? words[i] : line.toString() + " " + words[i];
            if (fm.stringWidth(candidate) > maxWidth && line.length() > 0) {
                drawCenteredLine(g2, line.toString(), panelWidth, y);
                line = new StringBuilder(words[i]);
                y += fm.getHeight();
            } else {
                line = new StringBuilder(candidate);
            }
        }
        if (line.length() > 0) {
            drawCenteredLine(g2, line.toString(), panelWidth, y);
        }
    }

    private void drawCenteredLine(Graphics2D g2, String line, int panelWidth, int y) {
        if (line == null || line.length() == 0) {
            return;
        }
        FontMetrics fm = g2.getFontMetrics();
        int x = (panelWidth - fm.stringWidth(line)) / 2;
        g2.drawString(line, Math.max(20, x), y);
    }
}