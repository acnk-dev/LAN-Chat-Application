package uicomponents;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Custom title bar for an undecorated JFrame: app name on the left, a
 * single close ("x") button on the right, drag-anywhere-to-move.
 *
 * Background matches the app's main dark surface so it blends into the
 * rest of the window rather than sitting on top as a separate colored
 * band. Close button stays subtle until hovered, then turns red.
 *
 * Used identically on every screen (Login, Register, and later the chat
 * window) so the whole app shares one consistent window chrome instead
 * of each screen rolling its own.
 *
 * No-arg constructible (so the NetBeans form editor can instantiate it on
 * its own) - call configureForWidth() and enableDragging() from the
 * screen's constructor, after initComponents(), since the final pixel
 * width isn't known until then.
 *
 * @author aditya
 */
public class TitleBar extends JPanel {

    public static final int HEIGHT = 44;
    private static final int CLOSE_BUTTON_WIDTH = 52;

    private final JLabel titleLabel = new JLabel();
    private final JLabel closeButton = new JLabel("X");
    private Point dragOffset;

    public TitleBar() {
        setLayout(null);
        setBackground(Theme.BACKGROUND);
        setOpaque(true);

        titleLabel.setFont(Theme.bold(15));
        titleLabel.setForeground(Theme.ACCENT);
        add(titleLabel);

        closeButton.setFont(Theme.bold(16));
        closeButton.setForeground(Theme.TEXT_SECONDARY);
        closeButton.setHorizontalAlignment(SwingConstants.CENTER);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.setOpaque(true);
        closeButton.setBackground(Theme.BACKGROUND);
        closeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                closeButton.setBackground(Theme.CLOSE_HOVER);
                closeButton.setForeground(Theme.BACKGROUND);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                closeButton.setBackground(Theme.BACKGROUND);
                closeButton.setForeground(Theme.TEXT_SECONDARY);
            }
        });
        add(closeButton);
    }

    public void setTitleText(String text) {
        titleLabel.setText(text);
    }

    /**
     * Sizes this bar (and its label/close button) to fit a window of the
     * given fixed pixel width. Every screen in this app uses a fixed,
     * non-resizable window size, so this only needs to run once.
     */
    public void configureForWidth(int width) {
        setBounds(0, 0, width, HEIGHT);
        titleLabel.setBounds(20, 0, width - 80, HEIGHT);
        closeButton.setBounds(width - CLOSE_BUTTON_WIDTH, 0, CLOSE_BUTTON_WIDTH, HEIGHT);
    }

    /**
     * Makes clicking-and-dragging this bar move the given window - the
     * standard trick for giving an undecorated frame back the ability to
     * be repositioned, since there's no OS title bar to drag anymore.
     *
     * Also wires the close button to that same window, dispatching a real
     * WINDOW_CLOSING event instead of hardcoding System.exit(0) - that way
     * each screen's own setDefaultCloseOperation decides what happens
     * (EXIT_ON_CLOSE for Login/Register/Main, DISPOSE_ON_CLOSE for a
     * secondary window like Profile that shouldn't kill the whole app).
     */
    public void enableDragging(final Window window) {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                dragOffset = e.getPoint();
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Point onScreen = e.getLocationOnScreen();
                window.setLocation(onScreen.x - dragOffset.x, onScreen.y - dragOffset.y);
            }
        });
        closeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        super.paintComponent(g);
    }
}