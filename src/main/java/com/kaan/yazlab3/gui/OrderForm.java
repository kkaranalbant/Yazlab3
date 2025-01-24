package com.kaan.yazlab3.gui;

import com.kaan.yazlab3.model.ConfirmProcess;
import com.kaan.yazlab3.model.Product;
import com.kaan.yazlab3.model.PurchaseProcess;
import com.kaan.yazlab3.model.User;
import com.kaan.yazlab3.service.ProductService;
import static java.awt.AWTEventMulticaster.add;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;



public class OrderForm extends JPanel {

    private User currentUser;
    private JComboBox<Product> productCombo;
    private JSpinner quantitySpinner;
    private JButton orderButton;
    private JLabel totalPriceLabel;
    private JLabel statusLabel;

    public OrderForm(User user) {
        currentUser = user ;
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createTitledBorder("Sipariş Oluştur"));

        initComponents();
        setupLayout();
        setupListeners();
        updateProductList();
    }

    private void initComponents() {
        // Ürün seçimi
        productCombo = new JComboBox<>();
        productCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Product) {
                    Product product = (Product) value;
                    setText(String.format("%s (%.2f TL - Stok: %d)",
                            product.getName(), product.getPrice(), product.getStock()));
                }
                return this;
            }
        });

        // Miktar spinner'ı
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1, 1, 5, 1);
        quantitySpinner = new JSpinner(spinnerModel);

        // Sipariş butonu
        orderButton = new JButton("Sipariş Ver");
        orderButton.setEnabled(false); // Başlangıçta kullanıcı seçilmediği için devre dışı

        // Toplam fiyat etiketi
        totalPriceLabel = new JLabel("Toplam: 0.00 TL");

        // Durum etiketi
        statusLabel = new JLabel("");
        statusLabel.setForeground(Color.RED);
    }

    private void setupLayout() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Ürün seçimi
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Ürün:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        add(productCombo, gbc);

        // Miktar
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        add(new JLabel("Miktar:"), gbc);
        gbc.gridx = 1;
        add(quantitySpinner, gbc);

        // Toplam fiyat
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        add(totalPriceLabel, gbc);

        // Sipariş butonu
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        add(orderButton, gbc);

        // Durum etiketi
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        add(statusLabel, gbc);
    }

    private void setupListeners() {
        // Ürün veya miktar değiştiğinde toplam fiyatı güncelle
        productCombo.addActionListener(e -> updateTotalPrice());
        quantitySpinner.addChangeListener(e -> updateTotalPrice());

        // Sipariş butonu listener'ı
        orderButton.addActionListener(e -> placeOrder());
    }

    private void updateTotalPrice() {
        Product selectedProduct = (Product) productCombo.getSelectedItem();
        if (selectedProduct != null) {
            int quantity = (Integer) quantitySpinner.getValue();
            float totalPrice = selectedProduct.getPrice() * quantity;
            totalPriceLabel.setText(String.format("Toplam: %.2f TL", totalPrice));

            // Bütçe kontrolü
            if (currentUser != null && currentUser.getBudget() < totalPrice) {
                statusLabel.setText("Uyarı: Yetersiz bütçe!");
                orderButton.setEnabled(false);
            } else if (selectedProduct.getStock() < quantity) {
                statusLabel.setText("Uyarı: Yetersiz stok!");
                orderButton.setEnabled(false);
            } else {
                statusLabel.setText("");
                orderButton.setEnabled(true);
            }
        }
    }

    private void placeOrder() {
        if (currentUser == null) {
            statusLabel.setText("Hata: Müşteri seçilmedi!");
            return;
        }

        Product selectedProduct = (Product) productCombo.getSelectedItem();
        if (selectedProduct == null) {
            statusLabel.setText("Hata: Ürün seçilmedi!");
            return;
        }

        int quantity = (Integer) quantitySpinner.getValue();
        float totalPrice = selectedProduct.getPrice() * quantity;

        if (currentUser.getBudget() < totalPrice) {
            statusLabel.setText("Hata: Yetersiz bütçe!");
            return;
        }

        if (selectedProduct.getStock() < quantity) {
            statusLabel.setText("Hata: Yetersiz stok!");
            return;
        }

        try {
            // Siparişi oluştur
            PurchaseProcess.createProcess(currentUser, selectedProduct, quantity);
            
            // UI'ı güncelle
            statusLabel.setText("Sipariş işleme alındı!");
            statusLabel.setForeground(Color.GREEN);

            // Form'u sıfırla
            quantitySpinner.setValue(1);
            updateProductList();

        } catch (Exception ex) {
            statusLabel.setText("Hata: " + ex.getMessage());
            statusLabel.setForeground(Color.RED);
        }
    }

    public void setUser(User user) {
        this.currentUser = user;
        orderButton.setEnabled(user != null);
        updateTotalPrice();
    }

    public void updateProductList() {
        productCombo.removeAllItems();
        List<Product> products = ProductService.getInstance().getAll();
        for (Product product : products) {
            if (product.getStock() > 0) {
                productCombo.addItem(product);
            }
        }
    }
}
