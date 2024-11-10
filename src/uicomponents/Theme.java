package uicomponents;

import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.border.Border;

/**
 * Shared colors and fonts for every screen - Tokyo Night palette
 * (https://wixdaq.github.io/Tokyo-Night-Website/palette.html), dark
 * variant. One theme, used consistently everywhere; add to this as new
 * screens need it rather than hardcoding colors elsewhere.
 *
 * @author ancla
 */
public final class Theme {

    private Theme() {
    }

    // Backgrounds
    public static final Color BACKGROUND = new Color(0x1A, 0x1B, 0x26);
    public static final Color SURFACE = new Color(0x1F, 0x22, 0x33);
    public static final Color SURFACE_HOVER = new Color(0x26, 0x2A, 0x40);
    public static final Color SURFACE_PRESSED = new Color(0x2D, 0x32, 0x50);

    // Borders / dividers
    public static final Color BORDER = new Color(0x41, 0x48, 0x68);

    // Text
    public static final Color TEXT_PRIMARY = new Color(0xC0, 0xCA, 0xF5);
    public static final Color TEXT_SECONDARY = new Color(0x9A, 0xA5, 0xCE);

    // Accent - used for links, wordmark, focus states
    public static final Color ACCENT = new Color(0x7A, 0xA2, 0xF7);
    public static final Color ACCENT_HOVER = new Color(0x7D, 0xCF, 0xFF);

    // Primary button fill (same blue family as ACCENT, tuned for use as
    // a solid fill with dark text on top rather than text-on-background)
    public static final Color BTN_FILL = new Color(0x7A, 0xA2, 0xF7);
    public static final Color BTN_FILL_HOVER = new Color(0x7D, 0xCF, 0xFF);
    public static final Color BTN_FILL_PRESSED = new Color(0x5C, 0x7A, 0xB9);
    public static final Color BTN_TEXT = BACKGROUND;

    // Gradient side panel
    public static final Color GRADIENT_START = new Color(0x58, 0x6C, 0xA1);
    public static final Color GRADIENT_END = BACKGROUND;

    // Close button hover - Tokyo Night's actual red, not a generic one
    public static final Color CLOSE_HOVER = new Color(0xF7, 0x76, 0x8E);

    // Rest of the Tokyo Night accent palette - kept unused until now, which
    // is why everything read as flat blue-and-gray. These carry real
    // meaning (status, unread, per-contact color) rather than being
    // decoration for its own sake.
    public static final Color RED = CLOSE_HOVER;
    public static final Color ORANGE = new Color(0xFF, 0x9E, 0x64);
    public static final Color YELLOW = new Color(0xE0, 0xAF, 0x68);
    public static final Color GREEN = new Color(0x9E, 0xCE, 0x6A);
    public static final Color TEAL = new Color(0x73, 0xDA, 0xCA);
    public static final Color PURPLE = new Color(0xBB, 0x9A, 0xF7);

    // Fixed rotation of accent colors for per-contact avatars. GREEN and
    // ORANGE are deliberately excluded since they're reserved for meaning
    // (online status, unread count) - if an avatar could land on those
    // same hues, a badge or status dot could camouflage into its own
    // avatar. RED is excluded too since it reads as an error/danger
    // color elsewhere (close button). All remaining colors are light
    // enough that BACKGROUND text stays readable on top, same contrast
    // rule as BTN_TEXT on BTN_FILL.
    private static final Color[] AVATAR_PALETTE = {
        ACCENT, PURPLE, TEAL, YELLOW, ACCENT_HOVER
    };

    public static final String FONT_FAMILY = "Segoe UI";

    public static Font bold(int size) {
        return new Font(FONT_FAMILY, Font.BOLD, size);
    }

    public static Font regular(int size) {
        return new Font(FONT_FAMILY, Font.PLAIN, size);
    }

    /**
     * Turns a plain JTextField/JPasswordField into the borderless,
     * bottom-line-only style used across the app. Called from a screen's
     * constructor after initComponents() - not something we can express
     * cleanly as a single form-editor Property, so it lives here instead.
     *
     * Explicitly sets the field's background too - Swing defaults text
     * fields to a plain white box regardless of the surrounding panel's
     * color, which would look broken sitting on a dark theme otherwise.
     */
    public static void styleUnderlineField(JTextField field) {
        Border line = BorderFactory.createMatteBorder(0, 0, 2, 0, BORDER);
        Border padding = BorderFactory.createEmptyBorder(4, 2, 4, 2);
        field.setBorder(BorderFactory.createCompoundBorder(line, padding));
        field.setBackground(BACKGROUND);
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(ACCENT);
    }

    /**
     * Deterministic color per name, so the same contact always lands on
     * the same avatar color instead of it changing every time the list
     * repaints. Text drawn on top of the returned color should use
     * BACKGROUND, same contrast rule as BTN_TEXT on BTN_FILL.
     */
    public static Color avatarColorFor(String name) {
        if (name == null || name.length() == 0) {
            return ACCENT;
        }
        int index = Math.abs(name.hashCode()) % AVATAR_PALETTE.length;
        return AVATAR_PALETTE[index];
    }
}