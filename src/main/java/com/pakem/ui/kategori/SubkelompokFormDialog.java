package com.pakem.ui.kategori;

import com.pakem.model.KategoriTingkat;
import com.pakem.model.Subkelompok;
import com.pakem.service.interfaces.IKategoriService;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/**
 * Dialog untuk menambah atau mengubah Subkelompok.
 */
public class SubkelompokFormDialog extends JDialog {

    private final IKategoriService kategoriService;
    private final KategoriTingkat kategori;
    private final Subkelompok existingSubkelompok;
    private boolean saved = false;

    private JTextField txtNama;

    public SubkelompokFormDialog(JFrame parent, IKategoriService kategoriService,
                                 KategoriTingkat kategori, Subkelompok existingSubkelompok) {
        super(parent, existingSubkelompok == null ? "Tambah Subkelompok" : "Ubah Subkelompok", true);
        this.kategoriService = kategoriService;
        this.kategori = kategori;
        this.existingSubkelompok = existingSubkelompok;

        initUI();
    }

    public boolean isSaved() {
        return saved;
    }

    private void initUI() {
        setSize(420, 260);
        setResizable(false);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(14, 20, 14, 20)
        ));

        JLabel lblTitle = new JLabel(existingSubkelompok == null ? "Tambah Subkelompok Baru" : "Ubah Nama Subkelompok");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(new Color(30, 41, 59));

        JLabel lblSub = new JLabel("Kategori Tingkat: " + kategori.getNama());
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(new Color(100, 116, 139));

        header.add(lblTitle);
        header.add(Box.createVerticalStrut(3));
        header.add(lblSub);
        add(header, BorderLayout.NORTH);

        // Form
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(248, 250, 252));
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 4, 8, 4);

        JLabel lblNama = new JLabel("Nama Subkelompok *");
        lblNama.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblNama.setForeground(new Color(51, 65, 85));

        txtNama = new JTextField();
        txtNama.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtNama.setPreferredSize(new Dimension(220, 32));
        txtNama.putClientProperty("JTextField.placeholderText", "Contoh: " + kategori.getNama() + " A");

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.4;
        form.add(lblNama, gbc);
        gbc.gridx = 1; gbc.weightx = 0.6;
        form.add(txtNama, gbc);

        add(form, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));

        JButton btnBatal = new JButton("Batal");
        btnBatal.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnBatal.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBatal.addActionListener(e -> dispose());

        JButton btnSimpan = new JButton("Simpan");
        btnSimpan.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSimpan.setBackground(new Color(37, 99, 235));
        btnSimpan.setForeground(Color.WHITE);
        btnSimpan.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSimpan.addActionListener(e -> save());

        footer.add(btnBatal);
        footer.add(btnSimpan);
        add(footer, BorderLayout.SOUTH);

        if (existingSubkelompok != null) {
            txtNama.setText(existingSubkelompok.getNama());
        }
    }

    private void save() {
        String nama = txtNama.getText().trim();
        if (nama.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama subkelompok tidak boleh kosong.", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
            txtNama.requestFocusInWindow();
            return;
        }

        try {
            if (existingSubkelompok == null) {
                kategoriService.tambahSubkelompok(kategori.getId(), nama);
            } else {
                kategoriService.ubahSubkelompok(existingSubkelompok.getId(), kategori.getId(), nama);
            }
            this.saved = true;
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
