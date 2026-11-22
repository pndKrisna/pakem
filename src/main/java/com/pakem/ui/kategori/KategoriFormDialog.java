package com.pakem.ui.kategori;

import com.pakem.model.KategoriTingkat;
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
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
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
 * Dialog untuk menambah atau mengubah master Kategori Tingkat.
 */
public class KategoriFormDialog extends JDialog {

    private final IKategoriService kategoriService;
    private final KategoriTingkat existingKategori;
    private boolean saved = false;

    private JTextField txtNama;
    private JSpinner spUrutan;

    public KategoriFormDialog(JFrame parent, IKategoriService kategoriService, KategoriTingkat existingKategori) {
        super(parent, existingKategori == null ? "Tambah Kategori Tingkat" : "Ubah Kategori Tingkat", true);
        this.kategoriService = kategoriService;
        this.existingKategori = existingKategori;

        initUI();
    }

    public boolean isSaved() {
        return saved;
    }

    private void initUI() {
        setSize(420, 290);
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

        JLabel lblTitle = new JLabel(existingKategori == null ? "Tambah Kategori Tingkat" : "Ubah Kategori Tingkat");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(new Color(30, 41, 59));

        JLabel lblSub = new JLabel("Atur nama tingkatan kemampuan tabuh dan urutannya.");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(new Color(100, 116, 139));

        header.add(lblTitle);
        header.add(Box.createVerticalStrut(3));
        header.add(lblSub);
        add(header, BorderLayout.NORTH);

        // Form
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(248, 250, 252));
        form.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 4, 8, 4);

        // Nama Kategori
        JLabel lblNama = new JLabel("Nama Kategori *");
        lblNama.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblNama.setForeground(new Color(51, 65, 85));

        txtNama = new JTextField();
        txtNama.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtNama.setPreferredSize(new Dimension(220, 32));

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.35;
        form.add(lblNama, gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        form.add(txtNama, gbc);

        // Urutan
        JLabel lblUrutan = new JLabel("Nomor Urut");
        lblUrutan.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUrutan.setForeground(new Color(51, 65, 85));

        spUrutan = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        spUrutan.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        spUrutan.setPreferredSize(new Dimension(80, 32));

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.35;
        form.add(lblUrutan, gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        form.add(spUrutan, gbc);

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

        // Data existing
        if (existingKategori != null) {
            txtNama.setText(existingKategori.getNama());
            spUrutan.setValue(existingKategori.getUrutan());
        }
    }

    private void save() {
        String nama = txtNama.getText().trim();
        if (nama.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama kategori tingkat tidak boleh kosong.", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
            txtNama.requestFocusInWindow();
            return;
        }

        int urutan = (int) spUrutan.getValue();

        try {
            if (existingKategori == null) {
                kategoriService.tambahKategori(nama, urutan);
            } else {
                kategoriService.ubahKategori(existingKategori.getId(), nama, urutan);
            }
            this.saved = true;
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
