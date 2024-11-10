package uicomponents;

import java.awt.Color;

/**
 * Bubble for messages the signed-in user sent - right-aligned, accent fill.
 *
 * @author aditya
 */
public class SentMessagePanel extends MessagePanel {

    public SentMessagePanel(String message, String timestamp) {
        super(message, timestamp);
    }

    @Override
    protected Color getBubbleColor() {
        return Theme.ACCENT;
    }

    @Override
    protected Color getTextColor() {
        return Theme.BACKGROUND;
    }
}
