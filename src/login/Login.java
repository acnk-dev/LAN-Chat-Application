package login;

import uicomponents.GradientSidePanel;
import uicomponents.RoundedButton;
import uicomponents.Theme;
import uicomponents.TitleBar;
import register.Register;

/**
 * Login screen. UI only for now - btnLogIn's click handler is a stub;
 * the actual LOGIN network call gets copied over from the old project
 * once the wire protocol is wired up.
 *
 * @author ancla
 */
public class Login extends javax.swing.JFrame {

    public static final int WINDOW_WIDTH = 900;
    public static final int CONTENT_HEIGHT = 556;

    public Login() {
        setUndecorated(true);
        initComponents();
        rightSidePanel.setHeading("Stay connected");
        rightSidePanel.setSubheading("Chat with anyone on your local network, instantly.");
        Theme.styleUnderlineField(tfUsername);
        Theme.styleUnderlineField(pfPassword);
        lblSignUp.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        titleBar.setTitleText("LANchat");
        titleBar.configureForWidth(WINDOW_WIDTH);
        titleBar.enableDragging(this);
        setSize(WINDOW_WIDTH, CONTENT_HEIGHT + TitleBar.HEIGHT);
        setLocationRelativeTo(null);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        titleBar = new uicomponents.TitleBar();
        leftSidePanel = new javax.swing.JPanel();
        lblLogo = new javax.swing.JLabel();
        lblTitle = new javax.swing.JLabel();
        lblSubtitle = new javax.swing.JLabel();
        lblUsername = new javax.swing.JLabel();
        tfUsername = new javax.swing.JTextField();
        lblPassword = new javax.swing.JLabel();
        pfPassword = new javax.swing.JPasswordField();
        btnLogIn = new uicomponents.RoundedButton();
        lblNoAccount = new javax.swing.JLabel();
        lblSignUp = new javax.swing.JLabel();
        rightSidePanel = new uicomponents.GradientSidePanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("LAN Chat - Log In");
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        getContentPane().add(titleBar, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 900, 44));

        leftSidePanel.setBackground(new java.awt.Color(26, 27, 38));
        leftSidePanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblLogo.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblLogo.setForeground(new java.awt.Color(122, 162, 247));
        lblLogo.setText("LANchat");
        leftSidePanel.add(lblLogo, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 120, 250, 26));

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(192, 202, 245));
        lblTitle.setText("Welcome back");
        leftSidePanel.add(lblTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 160, 340, 34));

        lblSubtitle.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblSubtitle.setForeground(new java.awt.Color(154, 165, 206));
        lblSubtitle.setText("Log in to keep the conversation going.");
        leftSidePanel.add(lblSubtitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 196, 340, 20));

        lblUsername.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        lblUsername.setForeground(new java.awt.Color(154, 165, 206));
        lblUsername.setText("USERNAME");
        leftSidePanel.add(lblUsername, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 240, 200, 16));

        tfUsername.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        leftSidePanel.add(tfUsername, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 258, 340, 34));

        lblPassword.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        lblPassword.setForeground(new java.awt.Color(154, 165, 206));
        lblPassword.setText("PASSWORD");
        leftSidePanel.add(lblPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 306, 200, 16));

        pfPassword.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        leftSidePanel.add(pfPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 324, 340, 34));

        btnLogIn.setText("Log In");
        btnLogIn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogInActionPerformed(evt);
            }
        });
        leftSidePanel.add(btnLogIn, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 376, 340, 42));

        lblNoAccount.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblNoAccount.setForeground(new java.awt.Color(154, 165, 206));
        lblNoAccount.setText("Don't have an account?");
        leftSidePanel.add(lblNoAccount, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 436, 200, 18));

        lblSignUp.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblSignUp.setForeground(new java.awt.Color(122, 162, 247));
        lblSignUp.setText("Sign up");
        lblSignUp.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblSignUpMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblSignUpMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblSignUpMouseExited(evt);
            }
        });
        leftSidePanel.add(lblSignUp, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 436, 90, 18));

        getContentPane().add(leftSidePanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 44, 400, 556));
        getContentPane().add(rightSidePanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 44, 500, 556));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnLogInActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogInActionPerformed
        // TODO: wire up LOGIN network call here (copy over from old project)
    }//GEN-LAST:event_btnLogInActionPerformed

    private void lblSignUpMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblSignUpMouseClicked
        this.dispose();
        new Register().setVisible(true);
    }//GEN-LAST:event_lblSignUpMouseClicked

    private void lblSignUpMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblSignUpMouseEntered
        lblSignUp.setForeground(Theme.ACCENT_HOVER);
    }//GEN-LAST:event_lblSignUpMouseEntered

    private void lblSignUpMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblSignUpMouseExited
        lblSignUp.setForeground(Theme.ACCENT);
    }//GEN-LAST:event_lblSignUpMouseExited

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Login().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private uicomponents.RoundedButton btnLogIn;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblNoAccount;
    private javax.swing.JLabel lblPassword;
    private javax.swing.JLabel lblSignUp;
    private javax.swing.JLabel lblSubtitle;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblUsername;
    private javax.swing.JPanel leftSidePanel;
    private javax.swing.JPasswordField pfPassword;
    private uicomponents.GradientSidePanel rightSidePanel;
    private javax.swing.JTextField tfUsername;
    private uicomponents.TitleBar titleBar;
    // End of variables declaration//GEN-END:variables
}
