package com.kaan.yazlab3.gui;

import com.kaan.yazlab3.service.ConcurrentConfirmManager;
import com.kaan.yazlab3.service.ConcurrentPurchaseManager;
import static java.awt.AWTEventMulticaster.add;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;



public class DynamicPriorityPanel extends JPanel {

    private JTable priorityTable;
    private PriorityTableModel tableModel;
    private JPanel statusPanel;

    public DynamicPriorityPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Dinamik Öncelik ve Bekleme Durumu"));

        initComponents();
        setupLayout();
    }

    private void initComponents() {
        // Tablo modelini oluştur
        tableModel = new PriorityTableModel();
        priorityTable = new JTable(tableModel);

        // Kolon genişliklerini ayarla
        priorityTable.getColumnModel().getColumn(0).setPreferredWidth(150); // Müşteri
        priorityTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Tür
        priorityTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Ürün
        priorityTable.getColumnModel().getColumn(3).setPreferredWidth(80);  // Miktar
        priorityTable.getColumnModel().getColumn(4).setPreferredWidth(120); // Bekleme Süresi
        priorityTable.getColumnModel().getColumn(5).setPreferredWidth(120); // Öncelik Skoru

        // Durum panelini oluştur
        statusPanel = createStatusPanel();
    }

    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("Öncelik Bilgileri"));

        // Premium müşteri öncelik skoru
        JLabel premiumLabel = new JLabel("Premium Müşteri Temel Skor: 15");
        premiumLabel.setForeground(new Color(0, 100, 0));

        // Normal müşteri öncelik skoru
        JLabel normalLabel = new JLabel("Normal Müşteri Temel Skor: 10");
        normalLabel.setForeground(new Color(0, 0, 100));

        // Bekleme süresi ağırlığı
        JLabel weightLabel = new JLabel("Bekleme Süresi Ağırlığı: 0.5 puan/saniye");
        weightLabel.setForeground(new Color(100, 0, 0));

        panel.add(premiumLabel);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(normalLabel);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(weightLabel);

        return panel;
    }

    private void setupLayout() {
        // Tablo scroll pane içinde
        JScrollPane scrollPane = new JScrollPane(priorityTable);

        // Ana layout
        add(statusPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void updateData() {
        SwingUtilities.invokeLater(() -> {
            // ConcurrentPurchaseManager'dan aktif işlemleri al
            var purchaseQueue = ConcurrentPurchaseManager.getInstance().getQueue();

            // ConcurrentConfirmManager'dan bekleyen onayları al
            var confirmQueue = ConcurrentConfirmManager.getInstance().getQueue();

            // Tablo modelini güncelle
            tableModel.updateData(purchaseQueue, confirmQueue);

            // Tabloyu yeniden çiz
            priorityTable.repaint();
        });
    }
}
