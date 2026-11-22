package com.pakem;

import com.formdev.flatlaf.FlatLightLaf;
import com.pakem.config.DatabaseConnection;
import com.pakem.service.impl.AuthServiceImpl;
import com.pakem.service.interfaces.IAuthService;
import com.pakem.ui.MainFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Main Application Entry Point untuk Pakem.
 */
public class Main {

    public static void main(String[] args) {
        // Setup FlatLaf Look and Feel
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("[Main] Gagal menerapkan tema FlatLaf: " + ex.getMessage());
        }

        System.out.println("==================================================");
        System.out.println("  Pakem - Sistem Administrasi Sanggar Seni Tabuh  ");
        System.out.println("  Versi 1.0.0 (Java 25 - Fase 1)");
        System.out.println("==================================================");

        // Inisialisasi Service Layer & UI pada Swing Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            boolean dbConnected = DatabaseConnection.testConnection();
            if (dbConnected) {
                System.out.println("[Main] Database tersambung: " + DatabaseConnection.getJdbcUrl());
            } else {
                System.err.println("[Main] Peringatan: Tidak dapat terhubung ke database.");
                System.err.println("[Main] Pastikan MySQL aktif dan file schema.sql telah dieksekusi.");
            }

            IAuthService authService = new AuthServiceImpl();
            MainFrame mainFrame = new MainFrame(authService);
            mainFrame.setVisible(true);
        });
    }
}
