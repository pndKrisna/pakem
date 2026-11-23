package com.pakem.ui.latihan;

import com.pakem.model.enums.JenisJadwal;
import com.pakem.model.JadwalLatihan;
import com.pakem.model.KategoriTingkat;
import com.pakem.model.Subkelompok;
import com.pakem.service.interfaces.ILatihanService;
import com.pakem.service.interfaces.IKategoriService;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Dialog modal tambah/ubah Jadwal Latihan.
 */
public class JadwalLatihanFormDialog extends JDialog {

    private final ILatihanService latihanService;
    private final IKategoriService kategoriService;
    private final JadwalLatihan existingJadwal;

    private JTextField txtTanggal;
    private JSpinner spinJamMulai;
    private JSpinner spinJamSelesai;
    private JTextField txtLokasi;
    private JTextArea txtCatatan;

    private JRadioButton rbRutin;
    private JRadioButton rbKhususEvent;

    private JComboBoxKategori kategoriCombo;
    private JComboBoxSubkelompok subkelompokCombo;
    private JCheckBox chkSubkelompok;

    private JButton btnSimpan;
    private JButton btnBatal;

    private boolean saved = false;

    public JadwalLatihanFormDialog(JFrame parent, ILatihanService latihanService,
                                   IKategoriService kategoriService, JadwalLatihan existingJadwal) {
        super(parent, existingJadwal == null ? "Tambah Jadwal Latihan" : "Ubah Jadwal Latihan", true);
        this.latihanService = latihanService;
        this.kategoriService = kategoriService;
        this.existingJadwal = existingJadwal;

        initUI();
        if (existingJadwal != null) {
            txtTanggal.setText(existingJadwal.getTanggal().toString());
            if (existingJadwal.getJamMulai() != null) {
                spinJamMulai.setValue(java.sql.Time.valueOf(existingJadwal.getJamMulai()));
            }
            if (existingJadwal.getJamSelesai() != null) {
                spinJamSelesai.setValue(java.sql.Time.valueOf(existingJadwal.getJamSelesai()));
            }
            txtLokasi.setText(existingJadwal.getLokasi() != null ? existingJadwal.getLokasi() : "");
            txtCatatan.setText(existingJadwal.getCatatan() != null ? existingJadwal.getCatatan() : "");

            if (existingJadwal.getJenisJadwal() == com.pakem.model.enums.JenisJadwal.RUTIN) {
                rbRutin.setSelected(true);
            } else {
                rbKhususEvent.setSelected(true);
            }

            kategoriCombo.setSelectedKategoriId(existingJadwal.getKategoriTingkatId());
            subkelompokCombo.setSelectedSubkelompokId(existingJadwal.getSubkelompokId());
            chkSubkelompok.setSelected(existingJadwal.getSubkelompokId() != null);
            subkelompokCombo.setEnabled(existingJadwal.getSubkelompokId() != null);
        }

        if (parent == null) {
            setLocationRelativeTo(null);
        } else {
            setLocationRelativeTo(parent);
        }
        setMinimumSize(new Dimension(520, 520));
        pack();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(241, 245, 249));

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BorderLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
                BorderFactory.createEmptyBorder(24, 24, 24, 24)
        ));

        JPanel fields = new JPanel();
        fields.setLayout(new BoxLayout(fields, BoxLayout.Y_AXIS));
        fields.setOpaque(false);

        // Jenis Jadwal
        JLabel lblJenis = new JLabel("Jenis Jadwal *");
        lblJenis.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblJenis.setForeground(new Color(30, 41, 59));
        lblJenis.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel panelJenis = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        panelJenis.setOpaque(false);

        rbRutin = new JRadioButton("Rutin / Wajib");
        rbRutin.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        rbRutin.setOpaque(false);
        rbRutin.setSelected(true);

        rbKhususEvent = new JRadioButton("Khusus Event");
        rbKhususEvent.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        rbKhususEvent.setOpaque(false);

        ButtonGroup groupJenis = new ButtonGroup();
        groupJenis.add(rbRutin);
        groupJenis.add(rbKhususEvent);

        panelJenis.add(rbRutin);
        panelJenis.add(rbKhususEvent);

        fields.add(lblJenis);
        fields.add(Box.createVerticalStrut(4));
        fields.add(panelJenis);
        fields.add(Box.createVerticalStrut(12));

        // Tanggal
        JLabel lblTanggal = new JLabel("Tanggal *");
        lblTanggal.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTanggal.setForeground(new Color(30, 41, 59));
        lblTanggal.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtTanggal = new JTextField();
        txtTanggal.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtTanggal.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        txtTanggal.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtTanggal.setToolTipText("Format: YYYY-MM-DD (contoh: 2026-11-25)");

        fields.add(lblTanggal);
        fields.add(Box.createVerticalStrut(4));
        fields.add(txtTanggal);
        fields.add(Box.createVerticalStrut(12));

        // Jam Mulai & Jam Selesai
        JPanel panelJam = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        panelJam.setOpaque(false);

        JLabel lblJamMulai = new JLabel("Jam Mulai:");
        lblJamMulai.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblJamMulai.setForeground(new Color(30, 41, 59));

        SpinnerDateModel modelMulai = new SpinnerDateModel();
        modelMulai.setValue(java.sql.Time.valueOf(java.time.LocalTime.of(8, 0)));
        spinJamMulai = new JSpinner(modelMulai);
        spinJamMulai.setEditor(new javax.swing.JSpinner.DateEditor(spinJamMulai, "HH:mm"));
        spinJamMulai.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JLabel lblJamSelesai = new JLabel("Jam Selesai:");
        lblJamSelesai.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblJamSelesai.setForeground(new Color(30, 41, 59));

        SpinnerDateModel modelSelesai = new SpinnerDateModel();
        modelSelesai.setValue(java.sql.Time.valueOf(java.time.LocalTime.of(10, 0)));
        spinJamSelesai = new JSpinner(modelSelesai);
        spinJamSelesai.setEditor(new javax.swing.JSpinner.DateEditor(spinJamSelesai, "HH:mm"));
        spinJamSelesai.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        panelJam.add(lblJamMulai);
        panelJam.add(spinJamMulai);
        panelJam.add(lblJamSelesai);
        panelJam.add(spinJamSelesai);

        fields.add(panelJam);
        fields.add(Box.createVerticalStrut(12));

        // Lokasi
        JLabel lblLokasi = new JLabel("Lokasi");
        lblLokasi.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblLokasi.setForeground(new Color(30, 41, 59));
        lblLokasi.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtLokasi = new JTextField();
        txtLokasi.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtLokasi.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        txtLokasi.setAlignmentX(Component.LEFT_ALIGNMENT);

        fields.add(lblLokasi);
        fields.add(Box.createVerticalStrut(4));
        fields.add(txtLokasi);
        fields.add(Box.createVerticalStrut(12));

        // Kategori & Subkelompok
        JLabel lblKategori = new JLabel("Kategori Tingkat (untuk Rutin)");
        lblKategori.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblKategori.setForeground(new Color(30, 41, 59));
        lblKategori.setAlignmentX(Component.LEFT_ALIGNMENT);

        kategoriCombo = new JComboBoxKategori(kategoriService);
        kategoriCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        kategoriCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        kategoriCombo.addActionListener(e -> {
            Integer kId = kategoriCombo.getSelectedKategoriId();
            if (kId != null) {
                subkelompokCombo.setKategoriId(kId);
            }
        });

        chkSubkelompok = new JCheckBox("Tentukan Subkelompok (opsional)");
        chkSubkelompok.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        chkSubkelompok.setOpaque(false);
        chkSubkelompok.setAlignmentX(Component.LEFT_ALIGNMENT);
        chkSubkelompok.addActionListener(e -> subkelompokCombo.setEnabled(chkSubkelompok.isSelected()));

        subkelompokCombo = new JComboBoxSubkelompok(kategoriService);
        subkelompokCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        subkelompokCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        subkelompokCombo.setEnabled(false);

        fields.add(lblKategori);
        fields.add(Box.createVerticalStrut(4));
        fields.add(kategoriCombo);
        fields.add(Box.createVerticalStrut(4));
        fields.add(chkSubkelompok);
        fields.add(Box.createVerticalStrut(4));
        fields.add(subkelompokCombo);
        fields.add(Box.createVerticalStrut(12));

        // Catatan
        JLabel lblCatatan = new JLabel("Catatan");
        lblCatatan.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblCatatan.setForeground(new Color(30, 41, 59));
        lblCatatan.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtCatatan = new JTextArea(3, 30);
        txtCatatan.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtCatatan.setLineWrap(true);
        txtCatatan.setWrapStyleWord(true);
        txtCatatan.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        txtCatatan.setAlignmentX(Component.LEFT_ALIGNMENT);

        fields.add(lblCatatan);
        fields.add(Box.createVerticalStrut(4));
        fields.add(txtCatatan);

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

        btnSimpan = new JButton(existingJadwal == null ? "Simpan" : "Update");
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

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    private void simpan() {
        try {
            String tanggalStr = txtTanggal.getText().trim();
            LocalDate tanggal;
            try {
                tanggal = LocalDate.parse(tanggalStr);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Format tanggal tidak valid. Gunakan YYYY-MM-DD.", "Validasi", JOptionPane.WARNING_MESSAGE);
                txtTanggal.requestFocusInWindow();
                return;
            }

            java.util.Date jamMulaiUtil = (java.util.Date) spinJamMulai.getValue();
            java.sql.Time jamMulaiSql = new java.sql.Time(jamMulaiUtil.getTime());
            LocalTime jamMulai = jamMulaiSql.toLocalTime();

            java.util.Date jamSelesaiUtil = (java.util.Date) spinJamSelesai.getValue();
            java.sql.Time jamSelesaiSql = new java.sql.Time(jamSelesaiUtil.getTime());
            LocalTime jamSelesai = jamSelesaiSql.toLocalTime();

            String lokasi = txtLokasi.getText().trim();
            String catatan = txtCatatan.getText().trim();

            com.pakem.model.enums.JenisJadwal jenis = rbRutin.isSelected()
                    ? com.pakem.model.enums.JenisJadwal.RUTIN
                    : com.pakem.model.enums.JenisJadwal.KHUSUS_EVENT;

            Integer kategoriId = kategoriCombo.getSelectedKategoriId();
            Integer subkelompokId = chkSubkelompok.isSelected() ? subkelompokCombo.getSelectedSubkelompokId() : null;

            // Validasi: Khusus Event harus memilih kategori atau subkelompok? Tidak wajib, bisa null.
            // Validasi: Rutin harus memilih kategori
            if (jenis == com.pakem.model.enums.JenisJadwal.RUTIN && kategoriId == null) {
                JOptionPane.showMessageDialog(this, "Untuk jadwal Rutin, kategori tingkat harus dipilih.", "Validasi", JOptionPane.WARNING_MESSAGE);
                return;
            }

            JadwalLatihan jadwal = new JadwalLatihan(
                    existingJadwal != null ? existingJadwal.getId() : 0,
                    jenis, tanggal, jamMulai, jamSelesai,
                    lokasi.isEmpty() ? null : lokasi,
                    kategoriId, subkelompokId, null, catatan
            );

            if (existingJadwal == null) {
                latihanService.simpanJadwalLatihan(jadwal);
            } else {
                latihanService.ubahJadwalLatihan(jadwal);
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

    // ===================== Combo Box Kategori =====================
    private static class JComboBoxKategori extends javax.swing.JComboBox<KategoriTingkat> {
        private final IKategoriService kategoriService;
        private final List<KategoriTingkat> kategoriList = new ArrayList<>();

        JComboBoxKategori(IKategoriService kategoriService) {
            this.kategoriService = kategoriService;
            addItem(new KategoriTingkat(0, "Pilih Kategori", 0));
            loadKategori();
        }

        private void loadKategori() {
            try {
                kategoriList.clear();
                kategoriList.addAll(kategoriService.getSemuaKategori());
                for (KategoriTingkat k : kategoriList) {
                    addItem(k);
                }
            } catch (Exception ex) {
                System.err.println("Gagal memuat kategori: " + ex.getMessage());
            }
        }

        void setSelectedKategoriId(Integer id) {
            if (id == null) return;
            for (int i = 0; i < getItemCount(); i++) {
                KategoriTingkat k = getItemAt(i);
                if (k.getId() == id) {
                    setSelectedIndex(i);
                    break;
                }
            }
        }

        Integer getSelectedKategoriId() {
            Object obj = getSelectedItem();
            if (obj instanceof KategoriTingkat k && k.getId() > 0) {
                return k.getId();
            }
            return null;
        }
    }

    // ===================== Combo Box Subkelompok =====================
    private static class JComboBoxSubkelompok extends javax.swing.JComboBox<Subkelompok> {
        private final IKategoriService kategoriService;
        private int kategoriIdTerpilih = 0;

        JComboBoxSubkelompok(IKategoriService kategoriService) {
            this.kategoriService = kategoriService;
            addItem(new Subkelompok(0, 0, "Pilih Subkelompok"));
        }

        void setSelectedSubkelompokId(Integer id) {
            if (id == null) return;
            for (int i = 0; i < getItemCount(); i++) {
                Subkelompok s = getItemAt(i);
                if (s.getId() == id) {
                    setSelectedIndex(i);
                    break;
                }
            }
        }

        Integer getSelectedSubkelompokId() {
            Object obj = getSelectedItem();
            if (obj instanceof Subkelompok s && s.getId() > 0) {
                return s.getId();
            }
            return null;
        }

        void setKategoriId(int kategoriId) {
            this.kategoriIdTerpilih = kategoriId;
            removeAllItems();
            addItem(new Subkelompok(0, 0, "Pilih Subkelompok"));
            if (kategoriId > 0) {
                try {
                    List<Subkelompok> subs = kategoriService.getSubkelompokByKategori(kategoriId);
                    for (Subkelompok s : subs) {
                        addItem(s);
                    }
                } catch (Exception ex) {
                    System.err.println("Gagal memuat subkelompok: " + ex.getMessage());
                }
            }
        }
    }
}
