package com.pakem.ui.presensi;

import com.pakem.model.User;
import com.pakem.service.interfaces.IKategoriService;
import com.pakem.service.interfaces.ILatihanService;
import com.pakem.service.interfaces.IPresensiService;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import java.awt.Font;

/**
 * Kontainer Tab Utama untuk Modul Presensi Latihan (Fase 5).
 * Berisi dua tab:
 * 1. 📝 Catat Presensi Latihan
 * 2. 📊 Rekapitulasi Kehadiran Anggota
 */
public class PresensiMainPanel extends JPanel {

    private final PresensiPanel presensiPanel;
    private final RekapPresensiPanel rekapPresensiPanel;

    public PresensiMainPanel(IPresensiService presensiService, ILatihanService latihanService,
                             IKategoriService kategoriService, User currentUser) {
        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));

        presensiPanel = new PresensiPanel(presensiService, latihanService, currentUser);
        rekapPresensiPanel = new RekapPresensiPanel(presensiService, kategoriService, currentUser);

        tabbedPane.addTab("Catat Presensi Sesi", presensiPanel);
        tabbedPane.addTab("Rekapitulasi Kehadiran", rekapPresensiPanel);

        // Auto reload data saat tab berpindah
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 0) {
                presensiPanel.loadJadwalComboBox();
            } else if (tabbedPane.getSelectedIndex() == 1) {
                rekapPresensiPanel.loadRekapData();
            }
        });

        add(tabbedPane, BorderLayout.CENTER);
    }

    public PresensiPanel getPresensiPanel() {
        return presensiPanel;
    }

    public RekapPresensiPanel getRekapPresensiPanel() {
        return rekapPresensiPanel;
    }
}
