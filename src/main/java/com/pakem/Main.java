package com.pakem;

import com.formdev.flatlaf.FlatLightLaf;
import com.pakem.config.DatabaseConnection;

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
            System.err.println("Gagal menerapkan tema FlatLaf: " + ex.getMessage());
        }

        System.out.println("==================================================");
        System.out.println("  Pakem - Sistem Administrasi Sanggar Seni Tabuh  ");
        System.out.println("  Versi 1.0.0 (Java 25)");
        System.out.println("==================================================");

        // Uji koneksi database awal
        SwingUtilities.invokeLater(() -> {
            System.out.println("[Init] Menguji koneksi database JDBC...");
            boolean dbConnected = DatabaseConnection.testConnection();
            if (dbConnected) {
                System.out.println("[Init] Koneksi ke MySQL berhasil: " + DatabaseConnection.getJdbcUrl());
            } else {
                System.out.println("[Init] Catatan: Database belum tersambung atau schema.sql belum dijalankan.");
                System.out.println("[Init] Pastikan MySQL aktif dan database 'pakem_db' sudah dibuat.");
            }
        });
    }
}
