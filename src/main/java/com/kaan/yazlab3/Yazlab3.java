/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.kaan.yazlab3;

import com.kaan.yazlab3.gui.AdminFrame;
import com.kaan.yazlab3.gui.LoginFrame;
import com.kaan.yazlab3.service.ConcurrentPurchaseManager;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 *
 * @author root
 */
public class Yazlab3 {

    public static void main(String[] args) {
        ConcurrentPurchaseManager.getInstance().init();

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            SwingUtilities.invokeLater(() -> {
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);

            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
