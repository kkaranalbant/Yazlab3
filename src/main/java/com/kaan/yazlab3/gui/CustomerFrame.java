package com.kaan.yazlab3.gui;

import com.kaan.yazlab3.model.User;
import com.kaan.yazlab3.service.ConcurrentPurchaseManager;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;

public class CustomerFrame extends JFrame {

    private User user;
    private CustomerPanel customerPanel;
    private ProductPanel productPanel;

    public CustomerFrame(User user) {
        this.user = user;
        // Frame ayarları
        setTitle("Eş Zamanlı Sipariş ve Stok Yönetimi");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        initComponents();
        setupLayout();
        startUpdateTimer();
    }

    private void initComponents() {
        // Panel nesnelerini oluştur
        customerPanel = new CustomerPanel(user);
        productPanel = new ProductPanel();
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Sol panel (Müşteri ve Sipariş)
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(500, getHeight()));
        leftPanel.add(customerPanel, BorderLayout.CENTER);

        // Sağ panel (Ürün ve Log)
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(productPanel, BorderLayout.CENTER);

        // Panelleri ana pencereye ekle
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);

    }

    private void startUpdateTimer() {
        // Her saniye panelleri güncelle
        Timer timer = new Timer(1000, e -> updateAllPanels());
        timer.start();
    }

    private void updateAllPanels() {
        SwingUtilities.invokeLater(() -> {
            customerPanel.updateData();
            productPanel.updateData();
        });
    }

}
