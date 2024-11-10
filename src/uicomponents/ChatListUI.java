package uicomponents;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

/**
 * Left contact-list pane: a "Chats" heading with a total-unread badge, a
 * search field, and the scrollable list of ChatItem rows underneath.
 * Populated from outside via addChatItem()/clearChats() - this class has
 * no idea where chats come from (that's the server port in a later step).
 *
 * @author aditya
 */
public class ChatListUI extends JPanel {

    private final JPanel listContainer = new JPanel();
    private final UnreadBadge unreadBadge = new UnreadBadge();
    private final JTextField searchField = new JTextField();

    public ChatListUI() {
        setBackground(Theme.SURFACE);
        setLayout(new BorderLayout());

        add(buildHeader(), BorderLayout.NORTH);

        listContainer.setOpaque(false);
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));

        JScrollPane scroll = new JScrollPane(listContainer);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(BorderFactory.createEmptyBorder(20, 20, 12, 20));

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        titleRow.setOpaque(false);
        titleRow.setAlignmentX(LEFT_ALIGNMENT);

        JLabel title = new JLabel("Chats");
        title.setFont(Theme.bold(22));
        title.setForeground(Theme.TEXT_PRIMARY);
        titleRow.add(title);
        titleRow.add(unreadBadge);

        Theme.styleUnderlineField(searchField);
        searchField.setAlignmentX(LEFT_ALIGNMENT);
        searchField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        setPlaceholder(searchField, "Search or start a new chat");

        header.add(titleRow);
        header.add(Box.createVerticalStrut(14));
        header.add(searchField);
        return header;
    }

    private void setPlaceholder(final JTextField field, final String placeholder) {
        field.setText(placeholder);
        field.setForeground(Theme.TEXT_SECONDARY);
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Theme.TEXT_PRIMARY);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().length() == 0) {
                    field.setText(placeholder);
                    field.setForeground(Theme.TEXT_SECONDARY);
                }
            }
        });
    }

    public void setUnreadTotal(int count) {
        unreadBadge.setCount(count);
    }

    public void addChatItem(ChatItem item) {
        item.setAlignmentX(LEFT_ALIGNMENT);
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, ChatItem.HEIGHT));
        listContainer.add(item);
        listContainer.revalidate();
        listContainer.repaint();
    }

    public void clearChats() {
        listContainer.removeAll();
        listContainer.revalidate();
        listContainer.repaint();
    }

    /** Small colored circle with a number, used for the total-unread count. */
    private static class UnreadBadge extends JPanel {
        private int count = 0;

        UnreadBadge() {
            setOpaque(false);
            setPreferredSize(new Dimension(26, 22));
        }

        void setCount(int count) {
            this.count = count;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (count <= 0) {
                return;
            }
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            String text = count > 99 ? "99+" : String.valueOf(count);
            g2.setFont(Theme.bold(11));
            FontMetrics fm = g2.getFontMetrics();
            int diameter = Math.max(20, fm.stringWidth(text) + 14);
            g2.setColor(Theme.ORANGE);
            g2.fillOval(0, 1, diameter, 20);
            g2.setColor(Theme.BACKGROUND);
            int tx = (diameter - fm.stringWidth(text)) / 2;
            int ty = 1 + (20 - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(text, tx, ty);
        }
    }
}
