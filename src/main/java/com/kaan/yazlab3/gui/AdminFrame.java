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



public class AdminFrame extends JFrame {

    // Panels
    private User user ;
    private CustomerPanel customerPanel;
    private ProductPanel productPanel;
    private LogPanel logPanel;
    private DynamicPriorityPanel priorityPanel;
    private AdminPanel adminPanel;

    public AdminFrame(User user) {
        this.user = user ;
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
        customerPanel = new CustomerPanel(user);
        productPanel = new ProductPanel();
        logPanel = new LogPanel();
        priorityPanel = new DynamicPriorityPanel();
        adminPanel = new AdminPanel();
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Sol panel (Müşteri ve Sipariş)
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(500, getHeight()));
        leftPanel.add(customerPanel, BorderLayout.CENTER);
        leftPanel.add(priorityPanel, BorderLayout.SOUTH);

        // Sağ panel (Ürün ve Log)
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(productPanel, BorderLayout.CENTER);
        rightPanel.add(logPanel, BorderLayout.SOUTH);

        // Panelleri ana pencereye ekle
        add(adminPanel, BorderLayout.NORTH);
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);

        // Panel boyutları
        priorityPanel.setPreferredSize(new Dimension(500, 200));
        logPanel.setPreferredSize(new Dimension(rightPanel.getWidth(), 200));
        adminPanel.setPreferredSize(new Dimension(getWidth(), 60));
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
            logPanel.updateData();
            priorityPanel.updateData();
        });
    }

}
