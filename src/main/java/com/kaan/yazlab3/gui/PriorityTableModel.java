package com.kaan.yazlab3.gui;

import com.kaan.yazlab3.model.ConfirmProcess;
import com.kaan.yazlab3.model.PurchaseProcess;
import com.kaan.yazlab3.model.User;
import com.kaan.yazlab3.model.UserType;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;
import javax.swing.table.AbstractTableModel;



public class PriorityTableModel extends AbstractTableModel {

    private List<ProcessInfo> processes;
    private final String[] columnNames = {
        "Müşteri", "Tür", "Ürün", "Miktar", "Bekleme Süresi", "Öncelik Skoru", "Durum"
    };

    public PriorityTableModel() {
        this.processes = new ArrayList<>();
    }

    @Override
    public int getRowCount() {
        return processes.size();
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
        ProcessInfo process = processes.get(row);
        switch (col) {
            case 0:
                return process.getCustomerName();
            case 1:
                return process.getCustomerType();
            case 2:
                return process.getProductName();
            case 3:
                return process.getQuantity();
            case 4:
                return String.format("%.1fs", process.getWaitingTime());
            case 5:
                return String.format("%.2f", process.getPriorityScore());
            case 6:
                return process.getStatus();
            default:
                return null;
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 3:
                return Integer.class;
            case 4:
            case 5:
                return Double.class;
            default:
                return String.class;
        }
    }

    public void updateData(PriorityBlockingQueue<Runnable> purchaseQueue, PriorityBlockingQueue<Runnable> confirmQueue) {
        processes.clear();

        // Satın alma kuyruğundaki işlemleri ekle
        for (Runnable runnable : purchaseQueue) {
            if (runnable instanceof PurchaseProcess) {
                PurchaseProcess process = (PurchaseProcess) runnable;
                processes.add(new ProcessInfo(
                        process.getUser().getName(),
                        process.getUser().getUserType(),
                        process.getProduct().getName(),
                        process.getQuantity(),
                        calculateWaitingTime(process.getTimestamp()),
                        calculatePriorityScore(process),
                        "Bekliyor"
                ));
            }
        }

        // Onay kuyruğundaki işlemleri ekle
        for (Runnable runnable : confirmQueue) {
            if (runnable instanceof ConfirmProcess) {
                ConfirmProcess process = (ConfirmProcess) runnable;
                processes.add(new ProcessInfo(
                        process.getUser().getName(),
                        process.getUser().getUserType(),
                        process.getProduct().getName(),
                        0, // Quantity is not available in ConfirmProcess
                        calculateWaitingTime(process.getTimestamp()),
                        process.getPriority(),
                        "Onay Bekliyor"
                ));
            }
        }

        fireTableDataChanged();
    }

    private double calculateWaitingTime(long timestamp) {
        return (System.currentTimeMillis() - timestamp) / 1000.0;
    }

    private double calculatePriorityScore(PurchaseProcess process) {
        User user = process.getUser();
        double baseScore = user.getUserType() == UserType.PREMIUM ? 15 : 10;
        double waitingTime = calculateWaitingTime(process.getTimestamp());
        return baseScore + (waitingTime * 0.5);
    }
}

// İşlem bilgilerini tutan yardımcı sınıf
class ProcessInfo {

    private String customerName;
    private UserType customerType;
    private String productName;
    private int quantity;
    private double waitingTime;
    private double priorityScore;
    private String status;

    public ProcessInfo(String customerName, UserType customerType, String productName,
            int quantity, double waitingTime, double priorityScore, String status) {
        this.customerName = customerName;
        this.customerType = customerType;
        this.productName = productName;
        this.quantity = quantity;
        this.waitingTime = waitingTime;
        this.priorityScore = priorityScore;
        this.status = status;
    }

    // Getter metodları
    public String getCustomerName() {
        return customerName;
    }

    public UserType getCustomerType() {
        return customerType;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getWaitingTime() {
        return waitingTime;
    }

    public double getPriorityScore() {
        return priorityScore;
    }

    public String getStatus() {
        return status;
    }
}
