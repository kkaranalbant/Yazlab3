package com.kaan.yazlab3.gui;

import com.kaan.yazlab3.model.Product;
import com.kaan.yazlab3.service.ProductService;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;



public class ProductPanel extends JPanel {

    private JTable productTable;
    private ProductTableModel tableModel;
    private StockBarChart stockChart;

    public ProductPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Ürün Stok Durumu"));

        initTable();
        initChart();
        setupLayout();
    }

    private void initTable() {
        tableModel = new ProductTableModel();
        productTable = new JTable(tableModel);

        // Kolon genişliklerini ayarla
        productTable.getColumnModel().getColumn(0).setPreferredWidth(50);   // ID
        productTable.getColumnModel().getColumn(1).setPreferredWidth(200);  // Ürün Adı
        productTable.getColumnModel().getColumn(2).setPreferredWidth(100);  // Stok
        productTable.getColumnModel().getColumn(3).setPreferredWidth(100);  // Fiyat
    }

    private void initChart() {
        stockChart = new StockBarChart();
    }

    private void setupLayout() {
        // Tablo scroll pane içinde
        JScrollPane scrollPane = new JScrollPane(productTable);

        // Layout düzenleme
        add(scrollPane, BorderLayout.CENTER);
        add(stockChart, BorderLayout.SOUTH);
    }

    public void updateData() {
        List<Product> products = ProductService.getInstance().getAll();

        // Tabloyu güncelle
        tableModel.updateData(products);

        // Grafiği güncelle
        stockChart.updateData(products);
    }
}

// Basit bir bar chart implementasyonu
class StockBarChart extends JPanel {

    private List<Product> products;
    private static final int BAR_WIDTH = 50;
    private static final int BAR_GAP = 20;
    private static final int MAX_BAR_HEIGHT = 200;
    private static final Color BAR_COLOR = new Color(65, 105, 225);
    private static final Color CRITICAL_COLOR = new Color(220, 20, 60);

    public StockBarChart() {
        setPreferredSize(new Dimension(500, 250));
    }

    public void updateData(List<Product> products) {
        this.products = products;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (products == null || products.isEmpty()) {
            return;
        }

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int maxStock = products.stream()
                .mapToInt(Product::getStock)
                .max()
                .orElse(100);

        int x = BAR_GAP;
        for (Product product : products) {
            // Bar yüksekliğini hesapla
            int height = (int) ((product.getStock() * MAX_BAR_HEIGHT) / (double) maxStock);
            int y = getHeight() - height - 30; // Alt kısımda yer bırak

            // Bar'ı çiz
            if (product.getStock() < 20) {
                g2d.setColor(CRITICAL_COLOR);
            } else {
                g2d.setColor(BAR_COLOR);
            }
            g2d.fillRect(x, y, BAR_WIDTH, height);

            // Çerçeve çiz
            g2d.setColor(Color.BLACK);
            g2d.drawRect(x, y, BAR_WIDTH, height);

            // Ürün adını yaz
            g2d.rotate(-Math.PI / 4);
            g2d.drawString(product.getName(),
                    (int) (x - y * 0.7),
                    (int) (x + height * 0.3 + BAR_WIDTH));
            g2d.rotate(Math.PI / 4);

            // Stok miktarını yaz
            g2d.drawString(String.valueOf(product.getStock()),
                    x + 5,
                    y - 5);

            x += BAR_WIDTH + BAR_GAP;
        }
    }
}
