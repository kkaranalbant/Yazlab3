package com.kaan.yazlab3.gui;


import com.kaan.yazlab3.model.Product;
import com.kaan.yazlab3.model.User;
import com.kaan.yazlab3.model.UserType;
import com.kaan.yazlab3.service.ConcurrentConfirmManager;
import com.kaan.yazlab3.service.ConcurrentPurchaseManager;
import com.kaan.yazlab3.service.OrderService;
import com.kaan.yazlab3.service.ProductService;
import com.kaan.yazlab3.service.UserService;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;


public class AdminPanel extends JPanel {

    private JButton confirmAllButton;
    private JButton addProductButton;
    private JButton updateStockButton;
    private JButton addUserButton;
    private JPanel actionPanel;
    private JPanel infoPanel;
    private OrderService orderService ;

    public AdminPanel() {
        this.orderService = OrderService.getInstance() ;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Admin Kontrol Paneli"));

        initComponents();
        setupLayout();
        setupListeners();
    }

    private void initComponents() {
        // Butonları oluştur
        confirmAllButton = new JButton("Tüm Siparişleri Onayla");
        addProductButton = new JButton("Yeni Ürün Ekle");
        updateStockButton = new JButton("Stok Güncelle");
        addUserButton = new JButton("Yeni Müşteri Ekle");

        // Action paneli
        actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actionPanel.add(confirmAllButton);
        actionPanel.add(addProductButton);
        actionPanel.add(updateStockButton);
        actionPanel.add(addUserButton);

        // Bilgi paneli
        infoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        infoPanel.add(new JLabel("Aktif İşlem Sayısı: "));
        infoPanel.add(new JLabel("0"));
        infoPanel.add(Box.createHorizontalStrut(20));
        infoPanel.add(new JLabel("Bekleyen Onay: "));
        infoPanel.add(new JLabel("0"));
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        add(actionPanel, BorderLayout.WEST);
        add(infoPanel, BorderLayout.EAST);
    }

    private void setupListeners() {
        // Tüm siparişleri onayla
        confirmAllButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this,
                    "Tüm bekleyen siparişler onaylanacak. Emin misiniz?",
                    "Onay", JOptionPane.YES_NO_OPTION);
            orderService.addOthersToQueue () ;
            if (choice == JOptionPane.YES_OPTION) {
                ConcurrentConfirmManager.getInstance().confirmAll();
            }
        });

        // Yeni ürün ekle
        addProductButton.addActionListener(e -> {
            showAddProductDialog();
        });

        // Stok güncelle
        updateStockButton.addActionListener(e -> {
            showUpdateStockDialog();
        });

        // Yeni müşteri ekle
        addUserButton.addActionListener(e -> {
            showAddUserDialog();
        });
    }

    private void showAddProductDialog() {
        Window window = SwingUtilities.getWindowAncestor(this);
        JDialog dialog;
        if (window instanceof Frame) {
            dialog = new JDialog((Frame) window, "Yeni Ürün Ekle", true);
        } else if (window instanceof Dialog) {
            dialog = new JDialog((Dialog) window, "Yeni Ürün Ekle", true);
        } else {
            dialog = new JDialog();
        }
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Form alanları
        JTextField nameField = new JTextField(20);
        JSpinner stockSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 1000, 1));
        JSpinner priceSpinner = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 10000.0, 0.1));

        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        dialog.add(new JLabel("Ürün Adı:"), gbc);
        gbc.gridx = 1;
        dialog.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        dialog.add(new JLabel("Stok:"), gbc);
        gbc.gridx = 1;
        dialog.add(stockSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        dialog.add(new JLabel("Fiyat:"), gbc);
        gbc.gridx = 1;
        dialog.add(priceSpinner, gbc);

        JButton saveButton = new JButton("Kaydet");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        dialog.add(saveButton, gbc);

        saveButton.addActionListener(e -> {
            try {
                Product product = new Product();
                product.setName(nameField.getText());
                product.setStock((Integer) stockSpinner.getValue());
                product.setPrice(((Double) priceSpinner.getValue()).floatValue());

                ProductService.getInstance().saveProduct(product);
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Ürün eklenirken hata oluştu: " + ex.getMessage(),
                        "Hata", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showUpdateStockDialog() {
        Window window = SwingUtilities.getWindowAncestor(this);
        JDialog dialog;
        if (window instanceof Frame) {
            dialog = new JDialog((Frame) window, "Stok Güncelle", true);
        } else if (window instanceof Dialog) {
            dialog = new JDialog((Dialog) window, "Stok Güncelle", true);
        } else {
            dialog = new JDialog();
        }
        dialog.setLayout(new BorderLayout());

        // Ürün listesi
        DefaultListModel<Product> listModel = new DefaultListModel<>();
        ProductService.getInstance().getAll().forEach(listModel::addElement);

        JList<Product> productList = new JList<>(listModel);
        productList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Stok güncelleme alanı
        JPanel updatePanel = new JPanel(new FlowLayout());
        JSpinner stockSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 1000, 1));
        JButton updateButton = new JButton("Güncelle");

        updatePanel.add(new JLabel("Yeni Stok:"));
        updatePanel.add(stockSpinner);
        updatePanel.add(updateButton);

        updateButton.addActionListener(e -> {
            Product selectedProduct = productList.getSelectedValue();
            if (selectedProduct != null) {
                try {
                    selectedProduct.setStock((Integer) stockSpinner.getValue());
                    ProductService.getInstance().saveProduct(selectedProduct);
                    dialog.dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog,
                            "Stok güncellenirken hata oluştu: " + ex.getMessage(),
                            "Hata", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        dialog.add(new JScrollPane(productList), BorderLayout.CENTER);
        dialog.add(updatePanel, BorderLayout.SOUTH);

        dialog.setSize(300, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showAddUserDialog() {
        Window window = SwingUtilities.getWindowAncestor(this);
        JDialog dialog;
        if (window instanceof Frame) {
            dialog = new JDialog((Frame) window, "Yeni Müşteri Ekle", true);
        } else if (window instanceof Dialog) {
            dialog = new JDialog((Dialog) window, "Yeni Müşteri Ekle", true);
        } else {
            dialog = new JDialog();
        }
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Form alanları
        JTextField nameField = new JTextField(20);
        JSpinner budgetSpinner = new JSpinner(new SpinnerNumberModel(500.0, 500.0, 3000.0, 100.0));
        JComboBox<UserType> typeCombo = new JComboBox<>(UserType.values());

        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        dialog.add(new JLabel("Müşteri Adı:"), gbc);
        gbc.gridx = 1;
        dialog.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        dialog.add(new JLabel("Başlangıç Bütçesi:"), gbc);
        gbc.gridx = 1;
        dialog.add(budgetSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        dialog.add(new JLabel("Müşteri Türü:"), gbc);
        gbc.gridx = 1;
        dialog.add(typeCombo, gbc);

        JButton saveButton = new JButton("Kaydet");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        dialog.add(saveButton, gbc);

        saveButton.addActionListener(e -> {
            try {
                User user = new User();
                user.setName(nameField.getText());
                user.setBudget(((Double) budgetSpinner.getValue()).floatValue());
                user.setUserType((UserType) typeCombo.getSelectedItem());
                user.setTotalSpent(0F);

                UserService.getInstance().saveUser(user);
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Müşteri eklenirken hata oluştu: " + ex.getMessage(),
                        "Hata", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    public void updateInfo() {
        SwingUtilities.invokeLater(() -> {
            // Aktif işlem ve bekleyen onay sayılarını güncelle
            int activeProcesses = ConcurrentPurchaseManager.getInstance().getQueue().size();
            int pendingConfirms = ConcurrentConfirmManager.getInstance().getQueue().size();

            // Info panel'deki label'ları güncelle
            Component[] components = infoPanel.getComponents();
            for (int i = 0; i < components.length; i++) {
                if (components[i] instanceof JLabel) {
                    JLabel label = (JLabel) components[i];
                    if (label.getText().startsWith("Aktif İşlem Sayısı:")) {
                        ((JLabel) components[i + 1]).setText(String.valueOf(activeProcesses));
                    } else if (label.getText().startsWith("Bekleyen Onay:")) {
                        ((JLabel) components[i + 1]).setText(String.valueOf(pendingConfirms));
                    }
                }
            }
        });
    }
}
