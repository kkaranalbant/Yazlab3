/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kaan.yazlab3.gui;

import com.kaan.yazlab3.service.UserService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {

    private UserService userService;

    public LoginFrame() {

        userService = UserService.getInstance();

        // Frame ayarları
        setTitle("Login Ekranı");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 200);
        setLayout(new BorderLayout());

        // Panel oluşturma
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));

        // Kullanıcı adı ve şifre etiketleri
        JLabel userLabel = new JLabel("Kullanıcı Adı:");
        JLabel passwordLabel = new JLabel("Şifre:");

        // Kullanıcı adı ve şifre alanları
        JTextField userField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        // Giriş ve Çıkış butonları
        JButton loginButton = new JButton("Giriş Yap");
        JButton exitButton = new JButton("Çıkış");

        // Panel içerisine bileşenleri ekleme
        panel.add(userLabel);
        panel.add(userField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(loginButton);
        panel.add(exitButton);

        // Paneli frame'e ekleme
        add(panel, BorderLayout.CENTER);

        // Butonlara olay dinleyicisi ekleme
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userField.getText();
                String password = new String(passwordField.getPassword());

                if (userService.login(username, password)) {
                    new AdminFrame(userService.getByUsername(username)).setVisible(true);
                } else {
                    new CustomerFrame(userService.getByUsername(username)).setVisible(true);;
                }
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }

}
