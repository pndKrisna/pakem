package com.pakem.ui.pengaturan;

import com.formdev.flatlaf.FlatClientProperties;
import com.pakem.model.SanggarSettings;
import com.pakem.model.User;
import com.pakem.service.interfaces.IIuranService;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
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
 * Panel Konfigurasi Pengaturan Sanggar.
 * Layout standar: Header + Form Card + Footer Actions.
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
        // 1. Top Panel (Header)
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(16, 24, 12, 24)
        ));

        // Title Row
        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);

        JLabel lblTitle = new JLabel("Pengaturan Sanggar");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(15, 23, 42));

        JLabel lblSubtitle = new JLabel("Konfigurasi profil sanggar, nominal iuran, ambang batas indikator keterlambatan, dan anjuran kapasitas kelompok.");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitle.setForeground(new Color(100, 116, 139));

        JPanel titleTextPanel = new JPanel();
        titleTextPanel.setLayout(new BoxLayout(titleTextPanel, BoxLayout.Y_AXIS));
        titleTextPanel.setOpaque(false);
        titleTextPanel.add(lblTitle);
        titleTextPanel.add(Box.createVerticalStrut(2));
        titleTextPanel.add(lblSubtitle);

        titleRow.add(titleTextPanel, BorderLayout.WEST);
        topPanel.add(titleRow);

        add(topPanel, BorderLayout.NORTH);

        // 2. Form Panel (Center) - compact
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(true);
        centerPanel.setBackground(new Color(241, 245, 249));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        // Form Card Panel
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
                BorderFactory.createEmptyBorder(20, 24, 20, 24)
        ));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(780, Integer.MAX_VALUE));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Label & Input style constants
        Font labelFont = new Font("Segoe UI", Font.BOLD, 13);
        Color labelColor = new Color(51, 65, 85);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 13);
        Dimension fieldSize = new Dimension(340, 38);
        String inputStyle = "arc: 8; borderColor: #CBD5E1; focusWidth: 1";

        // Helper: add label
        java.util.function.BiConsumer<Integer, String> addLabel = (row, text) -> {
            JLabel lbl = new JLabel(text);
            lbl.setFont(labelFont);
            lbl.setForeground(labelColor);
            gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
            card.add(lbl, gbc);
        };

        // Helper: add field
        java.util.function.BiConsumer<Integer, Component> addField = (row, comp) -> {
            gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1.0;
            card.add(comp, gbc);
        };

        // 1. Nama Sanggar
        addLabel.accept(0, "Nama Sanggar:");
        txtNamaSanggar = new JTextField(30);
        styleTextField(txtNamaSanggar, fieldFont, fieldSize, inputStyle);
        addField.accept(0, txtNamaSanggar);

        // 2. Nominal Default Iuran Bulanan
        addLabel.accept(1, "Nominal Default Iuran (Rp):");
        txtIuranDefault = new JTextField(15);
        styleTextField(txtIuranDefault, fieldFont, fieldSize, inputStyle);
        addField.accept(1, txtIuranDefault);

        // 3. Batas Tanggal Kuning (Mendekati Batas)
        addLabel.accept(2, "Batas Tanggal Kuning (1 - 28):");
        spBatasKuning = new JSpinner(new SpinnerNumberModel(15, 1, 28, 1));
        styleSpinner(spBatasKuning, fieldFont, fieldSize, inputStyle);
        addField.accept(2, spBatasKuning);

        // 4. Batas Tanggal Merah (Menunggak)
        addLabel.accept(3, "Batas Tanggal Merah (1 - 31):");
        spBatasMerah = new JSpinner(new SpinnerNumberModel(25, 1, 31, 1));
        styleSpinner(spBatasMerah, fieldFont, fieldSize, inputStyle);
        addField.accept(3, spBatasMerah);

        // 5. Kapasitas Anjuran Kelompok
        addLabel.accept(4, "Kapasitas Anjuran Kelompok (Orang):");
        spKapasitasKelompok = new JSpinner(new SpinnerNumberModel(25, 5, 100, 1));
        styleSpinner(spKapasitasKelompok, fieldFont, fieldSize, inputStyle);
        addField.accept(4, spKapasitasKelompok);

        // Action Buttons Row
        gbc.gridx = 1; gbc.gridy = 5;
        gbc.insets = new Insets(10, 8, 4, 8);
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 1.0;

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        btnPanel.setOpaque(false);

        btnReset = new JButton("Reset Form");
        btnReset.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnReset.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnReset.setFocusPainted(false);
        btnReset.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1, true),
                BorderFactory.createEmptyBorder(8, 18, 8, 18)
        ));
        btnReset.setBackground(Color.WHITE);
        btnReset.setForeground(new Color(51, 65, 85));
        btnReset.addActionListener(e -> loadPengaturan());

        btnSimpan = new JButton("Simpan Pengaturan");
        btnSimpan.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSimpan.setBackground(new Color(37, 99, 235));
        btnSimpan.setForeground(Color.WHITE);
        btnSimpan.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSimpan.setFocusPainted(false);
        btnSimpan.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btnSimpan.addActionListener(e -> simpanPengaturan());

        btnPanel.add(btnReset);
        btnPanel.add(btnSimpan);
        card.add(btnPanel, gbc);

        // Wrapper non-stretch: card tinggi mengikuti isi, tidak meregang ke bawah
        JPanel cardWrapper = new JPanel(new BorderLayout());
        cardWrapper.setOpaque(false);
        cardWrapper.add(card, BorderLayout.NORTH);
        centerPanel.add(cardWrapper);

        JScrollPane scroll = new JScrollPane(centerPanel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getViewport().setOpaque(true);
        scroll.getViewport().setBackground(new Color(241, 245, 249));

        add(scroll, BorderLayout.CENTER);
    }

    private void styleTextField(JTextField field, Font font, Dimension size, String style) {
        field.setFont(font);
        field.setPreferredSize(size);
        field.putClientProperty(FlatClientProperties.STYLE, style + "; margin: 4,10,4,10");
    }

    private void styleSpinner(JSpinner spinner, Font font, Dimension size, String style) {
        spinner.setFont(font);
        spinner.setPreferredSize(size);
        spinner.putClientProperty(FlatClientProperties.STYLE, style);

        if (spinner.getEditor() instanceof JSpinner.DefaultEditor editor) {
            JFormattedTextField tf = editor.getTextField();
            tf.setFont(font);
            tf.setHorizontalAlignment(SwingConstants.LEFT);
            tf.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        }
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