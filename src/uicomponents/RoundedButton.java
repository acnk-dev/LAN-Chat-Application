package uicomponents;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JButton;

/**
 * A JButton that paints itself as a flat rounded rectangle instead of the
 * default platform chrome, with its own hover/pressed colors. Real JButton
 * underneath (keyboard focus, ActionListener, etc. all still work) - only
 * the painting is custom.
 *
 * Default style is a filled Tokyo Night blue with dark text (better
 * contrast than white against this particular blue, since it's fairly
 * light/pastel). useNeutralStyle() switches to a dark surface fill with
 * light text for secondary actions.
 *
 * @author aditya
 */
public class RoundedButton extends JButton {

    private static final int ARC = 14;

    private Color baseColor = Theme.BTN_FILL;
    private Color hoverColor = Theme.BTN_FILL_HOVER;
    private Color pressedColor = Theme.BTN_FILL_PRESSED;

    public RoundedButton() {
        this("Button");
    }

    public RoundedButton(String text) {
        super(text);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
        setForeground(Theme.BTN_TEXT);
        setFont(Theme.bold(16));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    /**
     * Switches this button to the dark "secondary action" look (used for
     * things like the "Add Photo" button on Register) instead of the
     * default filled accent-color look.
     */
    public void useNeutralStyle() {
        this.baseColor = Theme.SURFACE;
        this.hoverColor = Theme.SURFACE_HOVER;
        this.pressedColor = Theme.SURFACE_PRESSED;
        setForeground(Theme.TEXT_SECONDARY);
        repaint();
    }

    public void setBaseColor(Color baseColor) {
        this.baseColor = baseColor;
        repaint();
    }

    public void setHoverColor(Color hoverColor) {
        this.hoverColor = hoverColor;
    }

    public void setPressedColor(Color pressedColor) {
        this.pressedColor = pressedColor;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        Color fill;
        if (getModel().isPressed()) {
            fill = pressedColor;
        } else if (getModel().isRollover()) {
            fill = hoverColor;
        } else {
            fill = baseColor;
        }

        g2.setColor(fill);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, ARC, ARC);

        super.paintComponent(g);
    }
}