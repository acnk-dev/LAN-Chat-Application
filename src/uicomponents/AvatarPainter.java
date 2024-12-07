package uicomponents;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Shared avatar rendering: a real photo clipped to a circle if one's
 * available, otherwise the same colored-initial-circle fallback used
 * everywhere in the app. Used by ChatItem, ConversationPanel's header
 * avatar, ProfilePanel's banner, and SideRail.
 */
public final class AvatarPainter {

    private AvatarPainter() {
    }

    /** Decodes raw image bytes once (e.g. when a FETCH_PROFILE_PICTURE reply arrives) - null if there's no picture. */
    public static Image decode(byte[] imageData) {
        if (imageData == null || imageData.length == 0) {
            return null;
        }
        try {
            return ImageIO.read(new ByteArrayInputStream(imageData));
        } catch (IOException ex) {
            return null;
        }
    }

    public static void paint(Graphics2D g2, int x, int y, int size, Image image, Color fallbackColor, String fallbackInitial) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        if (image != null) {
            Shape oldClip = g2.getClip();
            g2.clip(new Ellipse2D.Float(x, y, size, size));
            g2.drawImage(image, x, y, size, size, null);
            g2.setClip(oldClip);
            return;
        }

        g2.setColor(fallbackColor);
        g2.fillOval(x, y, size, size);

        String initial = (fallbackInitial == null || fallbackInitial.length() == 0) ? "?" : fallbackInitial.substring(0, 1).toUpperCase();
        g2.setColor(Theme.BACKGROUND);
        g2.setFont(Theme.bold(Math.max(10, size / 2)));
        FontMetrics fm = g2.getFontMetrics();
        int tx = x + (size - fm.stringWidth(initial)) / 2;
        int ty = y + (size - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(initial, tx, ty);
    }
}
