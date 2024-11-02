/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package chatapplication;

import login.Login;

/**
 * Application entry point. Update your project's "Main Class" setting
 * (Project Properties -> Run) to chatapplication.ChatApplication if it
 * isn't already.
 *
 * @author ancla
 */
public class ChatApplication {

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Login().setVisible(true);
            }
        });
    }
}
