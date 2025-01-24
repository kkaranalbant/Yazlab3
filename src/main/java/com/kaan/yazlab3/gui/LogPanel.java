package com.kaan.yazlab3.gui;

import com.kaan.yazlab3.model.Log;
import com.kaan.yazlab3.service.LogService;
import static java.awt.AWTEventMulticaster.add;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;



public class LogPanel extends JPanel {

    private JList<String> logList;
    private DefaultListModel<String> listModel;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final int MAX_LOGS = 100;  // Maksimum gösterilecek log sayısı

    public LogPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Sistem Logları"));

        initList();
        setupLayout();
    }

    private void initList() {
        listModel = new DefaultListModel<>();
        logList = new JList<>(listModel);

        // Log görünümünü özelleştir
        logList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {

                Component c = super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);

                if (value != null) {
                    String logText = value.toString();
                    if (logText.contains("[ERROR]")) {
                        setForeground(Color.RED);
                    } else if (logText.contains("[WARNING]")) {
                        setForeground(Color.ORANGE);
                    } else {
                        setForeground(Color.RED);
                    }
                }

                return c;
            }
        });
    }

    private void setupLayout() {
        // Scroll pane içine al
        JScrollPane scrollPane = new JScrollPane(logList);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Log filtreleme kontrolleri
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JCheckBox errorCheck = new JCheckBox("Hatalar", true);
        JCheckBox warningCheck = new JCheckBox("Uyarılar", true);
        JCheckBox infoCheck = new JCheckBox("Bilgilendirmeler", true);

        filterPanel.add(errorCheck);
        filterPanel.add(warningCheck);
        filterPanel.add(infoCheck);

        // Layout düzenleme
        add(filterPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void updateData() {
        // Logları servisten al ve modele ekle
        List<Log> logs = LogService.getInstance().getAll();

        SwingUtilities.invokeLater(() -> {
            listModel.clear();

            for (Log log : logs) {
                String formattedTime = log.getLogDate().format(formatter);
                String logLevel = "[" + log.getLogType() + "]";
                String userInfo = log.getUser().getName() + " (" + log.getUserType() + ")";

                String logEntry = String.format("%s %s %s: %s",
                        formattedTime, logLevel, userInfo, log.getLogDetails());

                listModel.addElement(logEntry);

                // Log sayısı sınırını kontrol et
                if (listModel.getSize() > MAX_LOGS) {
                    listModel.remove(0);
                }
            }

            // Son log'a scroll
            logList.ensureIndexIsVisible(listModel.getSize() - 1);
        });
    }

    // Yeni log eklemek için yardımcı metod
    public void addLog(String logMessage) {
        SwingUtilities.invokeLater(() -> {
            listModel.addElement(logMessage);
            if (listModel.getSize() > MAX_LOGS) {
                listModel.remove(0);
            }
            logList.ensureIndexIsVisible(listModel.getSize() - 1);
        });
    }
}
