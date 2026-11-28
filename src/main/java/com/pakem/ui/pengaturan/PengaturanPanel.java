package com.pakem.ui.pengaturan;

import com.pakem.model.SanggarSettings;
import com.pakem.model.User;
import com.pakem.service.interfaces.IIuranService;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;

/**
 * Panel Konfigurasi Pengaturan Sanggar (Fase 8).
 * Hanya dapat diakses oleh Pengurus.
 */
public class PengaturanPanel extends JPanel {

    private final IIuranService iuranService;
    private final User currentUser;

    private JTextField txtNamaSanggar;
    private JTextField txtIuranDefault;
    private JSpinner spBatasKuning;
    private JSpinner spBatasMerah;
    private JSpinner spKapasitasKelompok;
    private JButton btnSimpan;
    private JButton btnReset;

    public PengaturanPanel(IIuranService iuranService, User currentUser) {
        this.iuranService = iuranService;
        this.currentUser = currentUser;

        setLayout(new BorderLayout());
        setBackground(new Color(241, 245, 249));

        initComponents();
        loadPengaturan();
    }

    private void initComponents() {
        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setOpaque(false);
        root.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        // Header Title
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JLabel lblTitle = new JLabel("Pengaturan Sanggar");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(15, 23, 42));

        JLabel lblSubtitle = new JLabel("Konfigurasi profil sanggar, nominal iuran, ambang batas indikator keterlambatan, dan anjuran kapasitas kelompok.");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitle.setForeground(new Color(100, 116, 139));

        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setOpaque(false);
        titleBox.add(lblTitle);
        titleBox.add(Box.createVerticalStrut(4));
        titleBox.add(lblSubtitle);

        headerPanel.add(titleBox, BorderLayout.WEST);
        root.add(headerPanel);
        root.add(Box.createVerticalStrut(20));

        // Form Card Panel
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
                BorderFactory.createEmptyBorder(24, 28, 24, 28)
        ));
        card.setMaximumSize(new Dimension(750, 420));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        // 1. Nama Sanggar
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        JLabel lblNama = new JLabel("Nama Sanggar:");
        lblNama.setFont(new Font("Segoe UI", Font.BOLD, 13));
        card.add(lblNama, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        txtNamaSanggar = new JTextField(30);
        txtNamaSanggar.setPreferredSize(new Dimension(300, 34));
        card.add(txtNamaSanggar, gbc);

        // 2. Nominal Default Iuran Bulanan
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        JLabel lblIuran = new JLabel("Nominal Default Iuran (Rp):");
        lblIuran.setFont(new Font("Segoe UI", Font.BOLD, 13));
        card.add(lblIuran, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        txtIuranDefault = new JTextField(15);
        txtIuranDefault.setPreferredSize(new Dimension(300, 34));
        card.add(txtIuranDefault, gbc);

        // 3. Batas Tanggal Kuning (Mendekati Batas)
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        JLabel lblBatasKuning = new JLabel("Batas Tanggal Kuning (1 - 28):");
        lblBatasKuning.setFont(new Font("Segoe UI", Font.BOLD, 13));
        card.add(lblBatasKuning, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        spBatasKuning = new JSpinner(new SpinnerNumberModel(15, 1, 28, 1));
        spBatasKuning.setPreferredSize(new Dimension(100, 34));
        card.add(spBatasKuning, gbc);

        // 4. Batas Tanggal Merah (Menunggak)
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.3;
        JLabel lblBatasMerah = new JLabel("Batas Tanggal Merah (1 - 31):");
        lblBatasMerah.setFont(new Font("Segoe UI", Font.BOLD, 13));
        card.add(lblBatasMerah, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        spBatasMerah = new JSpinner(new SpinnerNumberModel(25, 1, 31, 1));
        spBatasMerah.setPreferredSize(new Dimension(100, 34));
        card.add(spBatasMerah, gbc);

        // 5. Kapasitas Anjuran Kelompok
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.3;
        JLabel lblKapasitas = new JLabel("Kapasitas Anjuran Kelompok (Orang):");
        lblKapasitas.setFont(new Font("Segoe UI", Font.BOLD, 13));
        card.add(lblKapasitas, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        spKapasitasKelompok = new JSpinner(new SpinnerNumberModel(25, 5, 100, 1));
        spKapasitasKelompok.setPreferredSize(new Dimension(100, 34));
        card.add(spKapasitasKelompok, gbc);

        // Action Buttons Row
        gbc.gridx = 1; gbc.gridy = 5;
        gbc.insets = new Insets(20, 8, 8, 8);
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);

        btnReset = new JButton("Reset Form");
        btnReset.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnReset.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnReset.addActionListener(e -> loadPengaturan());

        btnSimpan = new JButton("💾 Simpan Pengaturan");
        btnSimpan.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSimpan.setBackground(new Color(37, 99, 235));
        btnSimpan.setForeground(Color.WHITE);
        btnSimpan.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSimpan.addActionListener(e -> simpanPengaturan());

        btnPanel.add(btnReset);
        btnPanel.add(btnSimpan);
        card.add(btnPanel, gbc);

        root.add(card);
        root.add(Box.createVerticalGlue());

        JScrollPane scroll = new JScrollPane(root);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);
    }

    private void loadPengaturan() {
        try {
            SanggarSettings s = iuranService.getPengaturanSanggar();
            txtNamaSanggar.setText(s.getNamaSanggar());
            txtIuranDefault.setText(s.getIuranDefault().toPlainString());
            spBatasKuning.setValue(s.getBatasKuning());
            spBatasMerah.setValue(s.getBatasMerah());
            spKapasitasKelompok.setValue(s.getKapasitasKelompok());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Gagal memuat pengaturan sanggar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void simpanPengaturan() {
        String nama = txtNamaSanggar.getText().trim();
        if (nama.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama sanggar tidak boleh kosong.", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        BigDecimal iuran;
        try {
            iuran = new BigDecimal(txtIuranDefault.getText().trim());
            if (iuran.compareTo(BigDecimal.ZERO) < 0) {
                throw new NumberFormatException();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Nominal default iuran harus berupa angka >= 0.", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int kuning = (int) spBatasKuning.getValue();
        int merah = (int) spBatasMerah.getValue();
        if (kuning >= merah) {
            JOptionPane.showMessageDialog(this, "Tanggal Batas Kuning harus lebih kecil daripada Batas Merah.", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int kapasitas = (int) spKapasitasKelompok.getValue();

        SanggarSettings s = new SanggarSettings();
        s.setNamaSanggar(nama);
        s.setIuranDefault(iuran);
        s.setBatasKuning(kuning);
        s.setBatasMerah(merah);
        s.setKapasitasKelompok(kapasitas);

        try {
            iuranService.simpanPengaturanSanggar(s);
            JOptionPane.showMessageDialog(this,
                    "Pengaturan sanggar berhasil disimpan!\nPerubahan langsung diterapkan pada sistem.",
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Gagal menyimpan pengaturan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
