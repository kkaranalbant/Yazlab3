package com.kaan.yazlab3.gui;

import com.kaan.yazlab3.model.Product;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;



public class ProductTableModel extends AbstractTableModel {

    private List<Product> products;
    private final String[] columnNames = {
        "ID", "Ürün Adı", "Stok", "Fiyat", "Durum"
    };

    public ProductTableModel() {
        this.products = new ArrayList<>();
    }

    @Override
    public int getRowCount() {
        return products.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int row, int col) {
        Product product = products.get(row);
        switch (col) {
            case 0:
                return product.getId();
            case 1:
                return product.getName();
            case 2:
                return product.getStock();
            case 3:
                return String.format("%.2f TL", product.getPrice());
            case 4:
                return getStockStatus(product.getStock());
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        Product product = products.get(row);
        switch (col) {
            case 2: // Stok
                if (value instanceof Integer) {
                    product.setStock((Integer) value);
                    fireTableCellUpdated(row, col);
                }
                break;
            case 3: // Fiyat
                if (value instanceof Float) {
                    product.setPrice((Float) value);
                    fireTableCellUpdated(row, col);
                }
                break;
        }
    }

    public Product getProductAt(int row) {
        return products.get(row);
    }

    public void updateData(List<Product> newProducts) {
        this.products = new ArrayList<>(newProducts);
        fireTableDataChanged();
    }

    private String getStockStatus(int stock) {
        if (stock == 0) {
            return "Stokta Yok";
        } else if (stock < 20) {
            return "Kritik Seviye";
        } else {
            return "Yeterli";
        }
    }
}
