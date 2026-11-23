package com.pakem.ui.latihan;

import com.pakem.model.MateriTabuh;
import com.pakem.service.interfaces.ILatihanService;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.BoxLayout;

/**
 * Dialog modal tambah/ubah Materi Tabuh.
 */
public class MateriTabuhFormDialog extends JDialog {

    private final ILatihanService latihanService;
    private final MateriTabuh existingMateri;

    private JTextField txtNama;
    private JTextArea txtDeskripsi;
    private JButton btnSimpan;
    private JButton btnBatal;

    private boolean saved = false;

    public MateriTabuhFormDialog(JFrame parent, ILatihanService latihanService, MateriTabuh existingMateri) {
        super(parent, existingMateri == null ? "Tambah Materi Tabuh" : "Ubah Materi Tabuh", true);
        this.latihanService = latihanService;
        this.existingMateri = existingMateri;

        initUI();
        if (existingMateri != null) {
            txtNama.setText(existingMateri.getNama());
            txtDeskripsi.setText(existingMateri.getDeskripsi() != null ? existingMateri.getDeskripsi() : "");
        }

        pack();
        setLocationRelativeTo(parent);
        setMinimumSize(new Dimension(450, 300));
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(241, 245, 249));

        // Panel Form
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BorderLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
                BorderFactory.createEmptyBorder(24, 24, 24, 24)
        ));

        JPanel fields = new JPanel();
        fields.setLayout(new BorderLayout(12, 12));
        fields.setOpaque(false);

        JLabel lblNama = new JLabel("Nama Materi / Gending *");
        lblNama.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblNama.setForeground(new Color(30, 41, 59));

        txtNama = new JTextField();
        txtNama.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtNama.setPreferredSize(new Dimension(0, 34));

        JLabel lblDeskripsi = new JLabel("Deskripsi");
        lblDeskripsi.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblDeskripsi.setForeground(new Color(30, 41, 59));

        txtDeskripsi = new JTextArea(4, 30);
        txtDeskripsi.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtDeskripsi.setLineWrap(true);
        txtDeskripsi.setWrapStyleWord(true);

        fields.add(lblNama, BorderLayout.NORTH);
        fields.add(txtNama, BorderLayout.CENTER);
        fields.add(lblDeskripsi, BorderLayout.SOUTH);

        JScrollPane scrollDeskripsi = new JScrollPane(txtDeskripsi);
        scrollDeskripsi.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
                BorderFactory.createEmptyBorder(4, 4, 4, 4)
        ));
        scrollDeskripsi.setOpaque(false);
        fields.add(scrollDeskripsi, BorderLayout.SOUTH);

        formPanel.add(fields, BorderLayout.CENTER);

        // Panel Tombol
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));

        btnBatal = new JButton("Batal");
        btnBatal.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnBatal.setBackground(new Color(248, 250, 252));
        btnBatal.setForeground(new Color(71, 85, 105));
        btnBatal.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnBatal.setFocusPainted(false);
        btnBatal.addActionListener(e -> dispose());

        btnSimpan = new JButton(existingMateri == null ? "Simpan" : "Update");
        btnSimpan.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSimpan.setBackground(new Color(37, 99, 235));
        btnSimpan.setForeground(Color.WHITE);
        btnSimpan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSimpan.setFocusPainted(false);
        btnSimpan.addActionListener(e -> simpan());

        buttonPanel.add(btnBatal);
        buttonPanel.add(btnSimpan);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Tekan Enter untuk simpan
        txtNama.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    simpan();
                }
            }
        });

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    private void simpan() {
        String nama = txtNama.getText().trim();
        String deskripsi = txtDeskripsi.getText().trim();

        if (nama.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama materi tabuh tidak boleh kosong.", "Validasi", JOptionPane.WARNING_MESSAGE);
            txtNama.requestFocusInWindow();
            return;
        }

        try {
            if (existingMateri == null) {
                latihanService.tambahMateriTabuh(nama, deskripsi);
            } else {
                latihanService.ubahMateriTabuh(existingMateri.getId(), nama, deskripsi);
            }
            saved = true;
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() {
        return saved;
    }
}
