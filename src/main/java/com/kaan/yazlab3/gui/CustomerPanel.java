package com.kaan.yazlab3.gui;

import com.kaan.yazlab3.model.Order;
import com.kaan.yazlab3.model.OrderStatus;
import com.kaan.yazlab3.model.User;
import com.kaan.yazlab3.service.ConcurrentPurchaseManager;
import com.kaan.yazlab3.service.OrderService;
import com.kaan.yazlab3.service.UserService;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;


public class CustomerPanel extends JPanel {
    
    private User user ;
    private JTable customerTable;
    private CustomerTableModel tableModel;
    private OrderForm orderForm;
    private JPanel statusPanel;

    public CustomerPanel(User user) {
        this.user = user ;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Müşteri Paneli"));

        initTable();
        initOrderForm();
        initStatusPanel();
        setupLayout();
    }

    private void initTable() {
        tableModel = new CustomerTableModel();
        customerTable = new JTable(tableModel);
        customerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Kolon genişliklerini ayarla
        customerTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        customerTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Ad
        customerTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Tür
        customerTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Bütçe
        customerTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Bekleme

        // Seçim listener'ı
        customerTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = customerTable.getSelectedRow();
                if (selectedRow != -1) {
                    User selectedUser = tableModel.getUserAt(selectedRow);
                    orderForm.setUser(selectedUser);
                }
            }
        });
    }

    private void initOrderForm() {
        orderForm = new OrderForm(user);
    }

    private void initStatusPanel() {
        statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBorder(BorderFactory.createTitledBorder("Sipariş Durumu"));

        // Durum göstergeleri
        JLabel waitingLabel = new JLabel("Bekliyor: ");
        JLabel processingLabel = new JLabel("İşleniyor: ");
        JLabel completedLabel = new JLabel("Tamamlandı: ");

        waitingLabel.setForeground(Color.ORANGE);
        processingLabel.setForeground(Color.BLUE);
        completedLabel.setForeground(Color.GREEN);

        statusPanel.add(waitingLabel);
        statusPanel.add(Box.createHorizontalStrut(20));
        statusPanel.add(processingLabel);
        statusPanel.add(Box.createHorizontalStrut(20));
        statusPanel.add(completedLabel);
    }

    private void setupLayout() {
        // Tablo scroll pane içinde
        JScrollPane scrollPane = new JScrollPane(customerTable);
        scrollPane.setPreferredSize(new Dimension(450, 300));

        // Layout düzenleme
        add(scrollPane, BorderLayout.CENTER);
        add(orderForm, BorderLayout.SOUTH);
        add(statusPanel, BorderLayout.NORTH);
    }

    public void updateData() {
        // Müşteri listesini güncelle
        tableModel.updateData(UserService.getInstance().getAll());

        // Sipariş durumlarını güncelle
        SwingUtilities.invokeLater(() -> {
            // İlgili sipariş sayılarını servisten al
            List<Order> orders = OrderService.getInstance().getAll();
            long waitingCount = 0;
            long completedCount = 0;

            for (Order order : orders) {
                if (order.getOrderStatus() == OrderStatus.WAITING) {
                    waitingCount++;
                } else if (order.getOrderStatus() == OrderStatus.OK) {
                    completedCount++;
                }
            }

            long processingCount = ConcurrentPurchaseManager.getInstance().getQueue().size();

            // Status label'ları güncelle
            for (Component c : statusPanel.getComponents()) {
                if (c instanceof JLabel) {
                    JLabel label = (JLabel) c;
                    if (label.getText().startsWith("Bekliyor")) {
                        label.setText("Bekliyor: " + waitingCount);
                    } else if (label.getText().startsWith("İşleniyor")) {
                        label.setText("İşleniyor: " + processingCount);
                    } else if (label.getText().startsWith("Tamamlandı")) {
                        label.setText("Tamamlandı: " + completedCount);
                    }
                }
            }
        });
    }
}
