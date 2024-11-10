package register;

import uicomponents.GradientSidePanel;
import uicomponents.RoundedButton;
import uicomponents.Theme;
import uicomponents.TitleBar;
import login.Login;

/**
 * Register screen. UI only for now - btnRegister and btnChoosePhoto click
 * handlers are stubs; actual REGISTER network call + file-chooser logic
 * get copied over from the old project once the wire protocol is wired up.
 *
 * @author ancla
 */
public class Register extends javax.swing.JFrame {

    public static final int WINDOW_WIDTH = 900;
    public static final int CONTENT_HEIGHT = 556;

    public Register() {
        setUndecorated(true);
        initComponents();
        rightSidePanel.setHeading("Join the network");
        rightSidePanel.setSubheading("Create your account and start chatting with everyone on your LAN.");
        Theme.styleUnderlineField(tfName);
        Theme.styleUnderlineField(tfUsername);
        Theme.styleUnderlineField(tfEmail);
        Theme.styleUnderlineField(pfPassword);
        Theme.styleUnderlineField(pfConfirmPassword);
        btnChoosePhoto.useNeutralStyle();
        lblLogin.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
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
        btnChoosePhoto = new uicomponents.RoundedButton("+");
        lblPhotoHint = new javax.swing.JLabel();
        lblName = new javax.swing.JLabel();
        tfName = new javax.swing.JTextField();
        lblUsername = new javax.swing.JLabel();
        tfUsername = new javax.swing.JTextField();
        lblEmail = new javax.swing.JLabel();
        tfEmail = new javax.swing.JTextField();
        lblPassword = new javax.swing.JLabel();
        pfPassword = new javax.swing.JPasswordField();
        lblConfirmPassword = new javax.swing.JLabel();
        pfConfirmPassword = new javax.swing.JPasswordField();
        btnRegister = new uicomponents.RoundedButton("Create Account");
        lblHaveAccount = new javax.swing.JLabel();
        lblLogin = new javax.swing.JLabel();
        rightSidePanel = new uicomponents.GradientSidePanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("LAN Chat - Create Account");
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        getContentPane().add(titleBar, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 900, 44));

        leftSidePanel.setBackground(new java.awt.Color(26, 27, 38));
        leftSidePanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblLogo.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblLogo.setForeground(new java.awt.Color(122, 162, 247));
        lblLogo.setText("LANchat");
        leftSidePanel.add(lblLogo, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 20, 200, 24));

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(192, 202, 245));
        lblTitle.setText("Create your account");
        leftSidePanel.add(lblTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 52, 400, 28));

        lblSubtitle.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        lblSubtitle.setForeground(new java.awt.Color(154, 165, 206));
        lblSubtitle.setText("Join the chat - takes less than a minute.");
        leftSidePanel.add(lblSubtitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 84, 400, 18));

        btnChoosePhoto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChoosePhotoActionPerformed(evt);
            }
        });
        leftSidePanel.add(btnChoosePhoto, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 116, 50, 50));

        lblPhotoHint.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        lblPhotoHint.setForeground(new java.awt.Color(154, 165, 206));
        lblPhotoHint.setText("Add a profile photo (optional)");
        leftSidePanel.add(lblPhotoHint, new org.netbeans.lib.awtextra.AbsoluteConstraints(94, 132, 250, 18));

        lblName.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        lblName.setForeground(new java.awt.Color(154, 165, 206));
        lblName.setText("NAME");
        leftSidePanel.add(lblName, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 178, 250, 14));

        tfName.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        leftSidePanel.add(tfName, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 194, 400, 30));

        lblUsername.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        lblUsername.setForeground(new java.awt.Color(154, 165, 206));
        lblUsername.setText("USERNAME");
        leftSidePanel.add(lblUsername, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 234, 250, 14));

        tfUsername.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        leftSidePanel.add(tfUsername, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 250, 400, 30));

        lblEmail.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        lblEmail.setForeground(new java.awt.Color(154, 165, 206));
        lblEmail.setText("EMAIL");
        leftSidePanel.add(lblEmail, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 290, 250, 14));

        tfEmail.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        leftSidePanel.add(tfEmail, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 306, 400, 30));

        lblPassword.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        lblPassword.setForeground(new java.awt.Color(154, 165, 206));
        lblPassword.setText("PASSWORD");
        leftSidePanel.add(lblPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 346, 250, 14));

        pfPassword.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        leftSidePanel.add(pfPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 362, 400, 30));

        lblConfirmPassword.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        lblConfirmPassword.setForeground(new java.awt.Color(154, 165, 206));
        lblConfirmPassword.setText("CONFIRM PASSWORD");
        leftSidePanel.add(lblConfirmPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 402, 250, 14));

        pfConfirmPassword.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        leftSidePanel.add(pfConfirmPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 418, 400, 30));

        btnRegister.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegisterActionPerformed(evt);
            }
        });
        leftSidePanel.add(btnRegister, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 462, 400, 40));

        lblHaveAccount.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        lblHaveAccount.setForeground(new java.awt.Color(154, 165, 206));
        lblHaveAccount.setText("Already have an account?");
        leftSidePanel.add(lblHaveAccount, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 518, 200, 18));

        lblLogin.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        lblLogin.setForeground(new java.awt.Color(122, 162, 247));
        lblLogin.setText("Log in");
        lblLogin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblLoginMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblLoginMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblLoginMouseExited(evt);
            }
        });
        leftSidePanel.add(lblLogin, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 518, 70, 18));

        getContentPane().add(leftSidePanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 44, 460, 556));
        getContentPane().add(rightSidePanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 44, 440, 556));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnRegisterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegisterActionPerformed
        // TODO: wire up REGISTER network call here (copy over from old project)
    }//GEN-LAST:event_btnRegisterActionPerformed

    private void btnChoosePhotoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChoosePhotoActionPerformed
        // TODO: wire up JFileChooser + thumbnail logic here (copy over from old project)
    }//GEN-LAST:event_btnChoosePhotoActionPerformed

    private void lblLoginMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblLoginMouseClicked
        this.dispose();
        new Login().setVisible(true);
    }//GEN-LAST:event_lblLoginMouseClicked

    private void lblLoginMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblLoginMouseEntered
        lblLogin.setForeground(Theme.ACCENT_HOVER);
    }//GEN-LAST:event_lblLoginMouseEntered

    private void lblLoginMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblLoginMouseExited
        lblLogin.setForeground(Theme.ACCENT);
    }//GEN-LAST:event_lblLoginMouseExited

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Register.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Register.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Register.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Register.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Register().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private uicomponents.RoundedButton btnChoosePhoto;
    private uicomponents.RoundedButton btnRegister;
    private javax.swing.JPanel leftSidePanel;
    private javax.swing.JLabel lblConfirmPassword;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblHaveAccount;
    private javax.swing.JLabel lblLogin;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblPassword;
    private javax.swing.JLabel lblPhotoHint;
    private javax.swing.JLabel lblSubtitle;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblUsername;
    private javax.swing.JPasswordField pfConfirmPassword;
    private javax.swing.JPasswordField pfPassword;
    private uicomponents.GradientSidePanel rightSidePanel;
    private uicomponents.TitleBar titleBar;
    private javax.swing.JTextField tfEmail;
    private javax.swing.JTextField tfName;
    private javax.swing.JTextField tfUsername;
    // End of variables declaration//GEN-END:variables
}
