package com.kaan.yazlab3.gui;

import com.kaan.yazlab3.model.User;
import com.kaan.yazlab3.model.UserType;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;



public class CustomerTableModel extends AbstractTableModel {

    private List<User> users;
    private final String[] columnNames = {
        "ID", "Ad", "Tür", "Bütçe", "Toplam Harcama", "Bekleme Süresi", "Öncelik"
    };

    public CustomerTableModel() {
        this.users = new ArrayList<>();
    }

    @Override
    public int getRowCount() {
        return users.size();
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
        User user = users.get(row);
        switch (col) {
            case 0:
                return user.getId();
            case 1:
                return user.getName();
            case 2:
                return user.getUserType();
            case 3:
                return String.format("%.2f TL", user.getBudget());
            case 4:
                return String.format("%.2f TL", user.getTotalSpent());
            case 5:
                // Bekleme süresini hesapla
                return calculateWaitingTime(user) + "s";
            case 6:
                return calculatePriorityScore(user);
            default:
                return null;
        }
    }

    public User getUserAt(int row) {
        return users.get(row);
    }

    public void updateData(List<User> newUsers) {
        this.users = new ArrayList<>(newUsers);
        fireTableDataChanged();
    }

    private long calculateWaitingTime(User user) {
        // ConcurrentPurchaseManager ve ConcurrentConfirmManager'dan bekleme süresini hesapla
        return 0; // Şimdilik 0 döndürüyoruz
    }

    private double calculatePriorityScore(User user) {
        // Temel öncelik skoru
        double baseScore = user.getUserType() == UserType.PREMIUM ? 15 : 10;

        // Bekleme süresi ağırlığı
        double waitingTimeWeight = calculateWaitingTime(user) * 0.5;

        return baseScore + waitingTimeWeight;
    }
}
