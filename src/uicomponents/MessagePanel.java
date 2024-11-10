package uicomponents;

import java.awt.Color;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Shared look for a single chat bubble: rounded background, wrapped
 * message text, small timestamp underneath. SentMessagePanel and
 * ReceivedMessagePanel just supply the colors.
 *
 * @author aditya
 */
public abstract class MessagePanel extends JPanel {

    protected static final int MAX_TEXT_WIDTH = 320;
    private static final int ARC = 16;

    protected MessagePanel(String message, String timestamp) {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));

        JLabel messageLabel = new JLabel();
        messageLabel.setFont(Theme.regular(13));
        messageLabel.setForeground(getTextColor());
        messageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        messageLabel.setText(wrapIfNeeded(messageLabel, message));

        JLabel timeLabel = new JLabel(timestamp);
        timeLabel.setFont(Theme.regular(10));
        timeLabel.setForeground(fade(getTextColor()));
        timeLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        timeLabel.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));

        add(messageLabel);
        add(timeLabel);
    }

    private String wrapIfNeeded(JLabel label, String message) {
        String escaped = message.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
        FontMetrics fm = label.getFontMetrics(Theme.regular(13));
        if (fm.stringWidth(message) > MAX_TEXT_WIDTH) {
            return "<html><body style='width:" + MAX_TEXT_WIDTH + "px'>" + escaped + "</body></html>";
        }
        return message;
    }

    private Color fade(Color c) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), 170);
    }

    protected abstract Color getBubbleColor();

    protected abstract Color getTextColor();

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBubbleColor());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), ARC, ARC);
    }
}
