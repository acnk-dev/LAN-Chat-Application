package uicomponents;

import java.awt.Color;

/**
 * Bubble for messages received from the other user - left-aligned, neutral
 * surface fill.
 *
 * @author aditya
 */
public class ReceivedMessagePanel extends MessagePanel {

    public ReceivedMessagePanel(String message, String timestamp) {
        super(message, timestamp);
    }

    @Override
    protected Color getBubbleColor() {
        return Theme.SURFACE_HOVER;
    }

    @Override
    protected Color getTextColor() {
        return Theme.TEXT_PRIMARY;
    }
}
