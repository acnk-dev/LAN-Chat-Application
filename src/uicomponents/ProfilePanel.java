package uicomponents;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Read-only profile view for the signed-in user: photo, name, username,
 * status, and email - exactly the columns the users table actually has,
 * nothing invented. Fills the entire content area (chat list + conversation
 * combined, see ContentArea) rather than being squeezed into the narrow
 * contact-list column.
 *
 * Layout follows the usual profile-page shape: a gradient banner with the
 * avatar overlapping its bottom edge, name/username/status underneath,
 * then a settings-style card for the rest of the fields - not just a
 * flat stack of labels floating in the middle of a big empty pane.
 *
 * Photo can only be set at registration - the old project never had a
 * way to change it afterward, so this page doesn't pretend it does.
 *
 * @author aditya
 */
public class ProfilePanel extends JPanel {

    private static final int CARD_WIDTH = 460;

    private final ProfileBanner banner = new ProfileBanner();
    private final JLabel lblName = new JLabel();
    private final JLabel lblUsername = new JLabel();
    private final StatusPill statusPill = new StatusPill();
    private final JLabel lblEmailValue = new JLabel();
    private final RoundedButton btnLogOut = new RoundedButton("Log Out");

    private Runnable onBack;
    private Runnable onLogOut;

    public ProfilePanel() {
        setBackground(Theme.BACKGROUND);
        setLayout(new BorderLayout());

        add(buildHeader(), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(16, 12, 0, 20));

        final BackChevron back = new BackChevron();
        back.setPreferredSize(new Dimension(32, 32));
        back.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (onBack != null) {
                    onBack.run();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                back.setHovered(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                back.setHovered(false);
            }
        });

        JLabel title = new JLabel("Profile");
        title.setFont(Theme.bold(18));
        title.setForeground(Theme.TEXT_PRIMARY);
        title.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));

        header.add(back, BorderLayout.WEST);
        header.add(title, BorderLayout.CENTER);
        return header;
    }

    private JPanel buildBody() {
        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        banner.setAlignmentX(Component.CENTER_ALIGNMENT);
        banner.setMaximumSize(new Dimension(Integer.MAX_VALUE, banner.getPreferredSize().height));

        lblName.setFont(Theme.bold(20));
        lblName.setForeground(Theme.TEXT_PRIMARY);
        lblName.setHorizontalAlignment(SwingConstants.CENTER);
        lblName.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblName.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        lblUsername.setFont(Theme.regular(13));
        lblUsername.setForeground(Theme.TEXT_SECONDARY);
        lblUsername.setHorizontalAlignment(SwingConstants.CENTER);
        lblUsername.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblUsername.setMaximumSize(new Dimension(Integer.MAX_VALUE, 18));

        statusPill.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusPill.setPreferredSize(new Dimension(110, 24));
        statusPill.setMaximumSize(new Dimension(110, 24));

        JPanel card = buildInfoCard();
        card.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnLogOut.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogOut.setMaximumSize(new Dimension(CARD_WIDTH, 46));
        btnLogOut.setPreferredSize(new Dimension(CARD_WIDTH, 46));
        btnLogOut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (onLogOut != null) {
                    onLogOut.run();
                }
            }
        });

        body.add(banner);
        body.add(Box.createVerticalStrut(14));
        body.add(lblName);
        body.add(Box.createVerticalStrut(4));
        body.add(lblUsername);
        body.add(Box.createVerticalStrut(14));
        body.add(statusPill);
        body.add(Box.createVerticalStrut(32));
        body.add(card);
        body.add(Box.createVerticalStrut(24));
        body.add(btnLogOut);
        body.add(Box.createVerticalGlue());
        return body;
    }

    /** Settings-style card holding the account fields below name/status. */
    private JPanel buildInfoCard() {
        RoundedPanel card = new RoundedPanel(16, Theme.SURFACE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        card.setPreferredSize(new Dimension(CARD_WIDTH, 84));
        card.setMaximumSize(new Dimension(CARD_WIDTH, 84));

        JLabel lblEmailLabel = new JLabel("EMAIL");
        lblEmailLabel.setFont(Theme.bold(11));
        lblEmailLabel.setForeground(Theme.TEXT_SECONDARY);
        lblEmailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblEmailValue.setFont(Theme.regular(15));
        lblEmailValue.setForeground(Theme.TEXT_PRIMARY);
        lblEmailValue.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(lblEmailLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(lblEmailValue);
        return card;
    }

    public void setUserDetails(String name, String username, String email, boolean online) {
        banner.setDisplayName(name);
        lblName.setText(name);
        lblUsername.setText("@" + username);
        lblEmailValue.setText(email);
        statusPill.setOnline(online);
    }

    public void setProfileImage(java.awt.Image image) {
        banner.setImage(image);
    }

    public void setOnBack(Runnable onBack) {
        this.onBack = onBack;
    }

    public void setOnLogOut(Runnable onLogOut) {
        this.onLogOut = onLogOut;
    }

    /** Flat solid rounded-rect background, used for the settings card. */
    private static class RoundedPanel extends JPanel {
        private final int arc;
        private final Color fill;

        RoundedPanel(int arc, Color fill) {
            this.arc = arc;
            this.fill = fill;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
        }
    }

    /**
     * Gradient banner with the avatar overlapping its bottom edge - the
     * classic profile-page shape instead of a flat stack of labels. Same
     * gradient colors as GradientSidePanel so Profile still feels part of
     * the same app as Login/Register's illustration panel.
     */
    private static class ProfileBanner extends JPanel {
        private static final int BANNER_HEIGHT = 130;
        private static final int AVATAR_SIZE = 112;

        private String displayName = "?";
        private java.awt.Image image;

        ProfileBanner() {
            setOpaque(false);
            setPreferredSize(new Dimension(0, BANNER_HEIGHT + AVATAR_SIZE / 2 + 10));
        }

        void setDisplayName(String name) {
            this.displayName = (name == null || name.length() == 0) ? "?" : name;
            repaint();
        }

        void setImage(java.awt.Image image) {
            this.image = image;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth();
            GradientPaint gradient = new GradientPaint(0, 0, Theme.GRADIENT_START, w, BANNER_HEIGHT, Theme.BACKGROUND);
            g2.setPaint(gradient);
            g2.fillRect(0, 0, w, BANNER_HEIGHT);

            int cx = w / 2;
            int cy = BANNER_HEIGHT;
            int ringPad = 6;
            g2.setColor(Theme.BACKGROUND);
            g2.fillOval(cx - AVATAR_SIZE / 2 - ringPad, cy - AVATAR_SIZE / 2 - ringPad,
                    AVATAR_SIZE + ringPad * 2, AVATAR_SIZE + ringPad * 2);

            AvatarPainter.paint(g2, cx - AVATAR_SIZE / 2, cy - AVATAR_SIZE / 2, AVATAR_SIZE, image,
                    Theme.avatarColorFor(displayName), displayName);
        }
    }
}
