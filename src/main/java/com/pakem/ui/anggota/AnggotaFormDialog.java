package com.pakem.ui.anggota;

import com.pakem.model.Anggota;
import com.pakem.model.KategoriTingkat;
import com.pakem.model.Subkelompok;
import com.pakem.model.User;
import com.pakem.model.enums.StatusAnggota;
import com.pakem.service.interfaces.IAnggotaService;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

/**
 * Dialog form untuk menambah atau mengubah data anggota sanggar.
 * Menghormati batasan hak akses polimorfik antara Pengurus dan Pelatih.
 */
public class AnggotaFormDialog extends JDialog {

    private final IAnggotaService anggotaService;
    private final User currentUser;
    private final Anggota existingAnggota;
    private boolean saved = false;

    private JTextField txtNama;
    private JTextField txtKontak;
    private JTextField txtTanggalBergabung;
    private JComboBox<StatusAnggota> cmbStatus;
    private JComboBox<KategoriComboItem> cmbKategori;
    private JComboBox<SubkelompokComboItem> cmbSubkelompok;
    private JTextField txtKeteranganTingkat;
    private JTextArea txtCatatan;
    private JLabel lblKeteranganTingkat;

    private Integer originalKategoriId = null;

    // Helper wrapper classes untuk JComboBox
    private static class KategoriComboItem {
        final Integer id;
        final String label;

        KategoriComboItem(Integer id, String label) {
            this.id = id;
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    private static class SubkelompokComboItem {
        final Integer id;
        final String label;

        SubkelompokComboItem(Integer id, String label) {
            this.id = id;
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    public AnggotaFormDialog(JFrame parent, IAnggotaService anggotaService, User currentUser, Anggota existingAnggota) {
        super(parent, existingAnggota == null ? "Tambah Anggota Baru" : "Ubah Data Anggota: " + existingAnggota.getNamaLengkap(), true);
        this.anggotaService = anggotaService;
        this.currentUser = currentUser;
        this.existingAnggota = existingAnggota;

        if (existingAnggota != null) {
            this.originalKategoriId = existingAnggota.getKategoriTingkatId();
        }

        initUI();
        populateInitialData();
    }

    public boolean isSaved() {
        return saved;
    }

    private void initUI() {
        setSize(580, 680);
        setMinimumSize(new Dimension(520, 600));
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(16, 24, 16, 24)
        ));

        JLabel lblTitle = new JLabel(existingAnggota == null ? "Pendaftaran Anggota Baru" : "Pembaruan Informasi Anggota");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblTitle.setForeground(new Color(30, 41, 59));

        JLabel lblSubtitle = new JLabel(existingAnggota == null
                ? "Lengkapi identitas anggota sanggar seni tabuh."
                : "ID Anggota: #" + existingAnggota.getId() + " - " + existingAnggota.getNamaLengkap());
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitle.setForeground(new Color(100, 116, 139));

        header.add(lblTitle);
        header.add(Box.createVerticalStrut(4));
        header.add(lblSubtitle);

        // Jika Pelatih, tambahkan banner informasi hak akses terbatas
        if (!currentUser.canEditAnggota()) {
            header.add(Box.createVerticalStrut(10));
            JPanel bannerPelatih = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
            bannerPelatih.setBackground(new Color(254, 243, 199)); // Soft amber
            bannerPelatih.setBorder(BorderFactory.createLineBorder(new Color(252, 211, 77), 1, true));

            JLabel lblWarning = new JLabel("Mode Pelatih: Anda berwenang mengubah / mengusulkan Kategori Tingkat.");
            lblWarning.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lblWarning.setForeground(new Color(146, 64, 14));
            bannerPelatih.add(lblWarning);
            header.add(bannerPelatih);
        }

        add(header, BorderLayout.NORTH);

        // Form Fields (GridBagLayout)
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(248, 250, 252));
        formPanel.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 4, 6, 4);

        int row = 0;

        // 1. Nama Lengkap
        txtNama = new JTextField();
        txtNama.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        addFormField(formPanel, gbc, row++, "Nama Lengkap", txtNama);

        // 2. Nomor Kontak
        txtKontak = new JTextField();
        txtKontak.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        addFormField(formPanel, gbc, row++, "Nomor HP / WA", txtKontak);

        // 3. Tanggal Bergabung (format YYYY-MM-DD)
        txtTanggalBergabung = new JTextField();
        txtTanggalBergabung.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtTanggalBergabung.setText(LocalDate.now().toString());
        addFormField(formPanel, gbc, row++, "Tgl Bergabung (YYYY-MM-DD)", txtTanggalBergabung);

        // 4. Status Keanggotaan
        cmbStatus = new JComboBox<>(StatusAnggota.values());
        cmbStatus.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        addFormField(formPanel, gbc, row++, "Status Keanggotaan", cmbStatus);

        // 5. Kategori Tingkat
        cmbKategori = new JComboBox<>();
        cmbKategori.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbKategori.addActionListener(e -> onKategoriChanged());
        addFormField(formPanel, gbc, row++, "Kategori Tingkat", cmbKategori);

        // 6. Subkelompok
        cmbSubkelompok = new JComboBox<>();
        cmbSubkelompok.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        addFormField(formPanel, gbc, row++, "Subkelompok", cmbSubkelompok);

        // 7. Keterangan Perubahan Tingkat
        txtKeteranganTingkat = new JTextField();
        txtKeteranganTingkat.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblKeteranganTingkat = new JLabel("Catatan Perubahan Tingkat");
        lblKeteranganTingkat.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblKeteranganTingkat.setForeground(new Color(51, 65, 85));

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.35;
        formPanel.add(lblKeteranganTingkat, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;
        formPanel.add(txtKeteranganTingkat, gbc);
        row++;

        // 8. Catatan Bebas
        txtCatatan = new JTextArea(3, 20);
        txtCatatan.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtCatatan.setLineWrap(true);
        txtCatatan.setWrapStyleWord(true);
        JScrollPane scrollCatatan = new JScrollPane(txtCatatan);
        scrollCatatan.setPreferredSize(new Dimension(0, 70));
        addFormField(formPanel, gbc, row++, "Catatan Tambahan", scrollCatatan);

        JScrollPane scrollForm = new JScrollPane(formPanel);
        scrollForm.setBorder(null);
        add(scrollForm, BorderLayout.CENTER);

        // Footer Action Buttons
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 14));
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));

        JButton btnBatal = new JButton("Batal");
        btnBatal.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnBatal.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBatal.addActionListener(e -> dispose());

        JButton btnSimpan = new JButton(existingAnggota == null ? "Daftarkan Anggota" : "Simpan Perubahan");
        btnSimpan.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSimpan.setBackground(new Color(37, 99, 235));
        btnSimpan.setForeground(Color.WHITE);
        btnSimpan.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSimpan.addActionListener(e -> saveAnggota());

        footer.add(btnBatal);
        footer.add(btnSimpan);
        add(footer, BorderLayout.SOUTH);

        // Terapkan batasan hak akses Pelatih
        applyRoleRestrictions();
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String label, java.awt.Component field) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(new Color(51, 65, 85));

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.35;
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;
        panel.add(field, gbc);
    }

    private void applyRoleRestrictions() {
        if (!currentUser.canEditAnggota()) {
            // Pelatih tidak boleh mengedit identitas dasar & status
            txtNama.setEditable(false);
            txtNama.setBackground(new Color(241, 245, 249));
            txtKontak.setEditable(false);
            txtKontak.setBackground(new Color(241, 245, 249));
            txtTanggalBergabung.setEditable(false);
            txtTanggalBergabung.setBackground(new Color(241, 245, 249));
            cmbStatus.setEnabled(false);
            // Kategori tingkat, subkelompok, keterangan tingkat, dan catatan tetap aktif!
        }
    }

    private void populateInitialData() {
        // Muat Kategori Tingkat
        try {
            List<KategoriTingkat> kategoriList = anggotaService.getSemuaKategori();
            DefaultComboBoxModel<KategoriComboItem> modelKat = new DefaultComboBoxModel<>();
            modelKat.addElement(new KategoriComboItem(null, "-- Pilih Kategori Tingkat --"));
            for (KategoriTingkat k : kategoriList) {
                modelKat.addElement(new KategoriComboItem(k.getId(), k.getNama()));
            }
            cmbKategori.setModel(modelKat);

            if (existingAnggota != null) {
                txtNama.setText(existingAnggota.getNamaLengkap());
                txtKontak.setText(existingAnggota.getNomorKontak());
                if (existingAnggota.getTanggalBergabung() != null) {
                    txtTanggalBergabung.setText(existingAnggota.getTanggalBergabung().toString());
                }
                cmbStatus.setSelectedItem(existingAnggota.getStatus());
                txtCatatan.setText(existingAnggota.getCatatan());

                // Set Kategori terpilih
                if (existingAnggota.getKategoriTingkatId() != null) {
                    for (int i = 0; i < cmbKategori.getItemCount(); i++) {
                        KategoriComboItem item = cmbKategori.getItemAt(i);
                        if (Objects.equals(item.id, existingAnggota.getKategoriTingkatId())) {
                            cmbKategori.setSelectedIndex(i);
                            break;
                        }
                    }
                }

                // Muat Subkelompok dan set terpilih
                loadSubkelompok(existingAnggota.getKategoriTingkatId(), existingAnggota.getSubkelompokId());
            } else {
                loadSubkelompok(null, null);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat master kategori: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onKategoriChanged() {
        KategoriComboItem selectedKat = (KategoriComboItem) cmbKategori.getSelectedItem();
        Integer katId = selectedKat != null ? selectedKat.id : null;
        loadSubkelompok(katId, null);

        // Jika mengubah kategori dari kategori awal pada data lama, sorot keterangan
        if (existingAnggota != null && !Objects.equals(katId, originalKategoriId)) {
            txtKeteranganTingkat.setEnabled(true);
            txtKeteranganTingkat.setBackground(Color.WHITE);
            lblKeteranganTingkat.setText("Alasan Kenaikan/Perubahan Tingkat *");
            lblKeteranganTingkat.setForeground(new Color(220, 38, 38));
        } else {
            lblKeteranganTingkat.setText("Catatan Perubahan Tingkat");
            lblKeteranganTingkat.setForeground(new Color(51, 65, 85));
        }
    }

    private void loadSubkelompok(Integer kategoriId, Integer selectedSubkelompokId) {
        DefaultComboBoxModel<SubkelompokComboItem> modelSub = new DefaultComboBoxModel<>();
        modelSub.addElement(new SubkelompokComboItem(null, "-- Tanpa Subkelompok --"));

        if (kategoriId != null) {
            try {
                List<Subkelompok> subs = anggotaService.getSubkelompokByKategori(kategoriId);
                int selectIdx = 0;
                for (int i = 0; i < subs.size(); i++) {
                    Subkelompok s = subs.get(i);
                    SubkelompokComboItem item = new SubkelompokComboItem(s.getId(), s.getNama());
                    modelSub.addElement(item);
                    if (selectedSubkelompokId != null && Objects.equals(s.getId(), selectedSubkelompokId)) {
                        selectIdx = i + 1;
                    }
                }
                cmbSubkelompok.setModel(modelSub);
                cmbSubkelompok.setSelectedIndex(selectIdx);
            } catch (Exception e) {
                System.err.println("Gagal memuat subkelompok: " + e.getMessage());
            }
        } else {
            cmbSubkelompok.setModel(modelSub);
        }
    }

    private void saveAnggota() {
        String nama = txtNama.getText().trim();
        if (nama.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama lengkap anggota tidak boleh kosong.", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
            txtNama.requestFocusInWindow();
            return;
        }

        String tglStr = txtTanggalBergabung.getText().trim();
        LocalDate tglBergabung;
        try {
            tglBergabung = LocalDate.parse(tglStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Format tanggal bergabung tidak valid. Gunakan format YYYY-MM-DD (contoh: 2026-11-22).", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
            txtTanggalBergabung.requestFocusInWindow();
            return;
        }

        KategoriComboItem selectedKat = (KategoriComboItem) cmbKategori.getSelectedItem();
        Integer katId = selectedKat != null ? selectedKat.id : null;

        SubkelompokComboItem selectedSub = (SubkelompokComboItem) cmbSubkelompok.getSelectedItem();
        Integer subId = selectedSub != null ? selectedSub.id : null;

        String keteranganTingkat = txtKeteranganTingkat.getText().trim();

        // Validasi jika kategori diubah pada data lama
        if (existingAnggota != null && !Objects.equals(katId, originalKategoriId)) {
            if (keteranganTingkat.isEmpty()) {
                int res = JOptionPane.showConfirmDialog(this,
                        "Anda mengubah Kategori Tingkat anggota ini.\nApakah ingin menyimpan tanpa menyertakan keterangan alasan perubahan?",
                        "Konfirmasi Keterangan",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (res != JOptionPane.YES_OPTION) {
                    txtKeteranganTingkat.requestFocusInWindow();
                    return;
                }
            }
        }

        try {
            if (existingAnggota == null) {
                // Tambah Anggota Baru
                Anggota baru = new Anggota();
                baru.setNamaLengkap(nama);
                baru.setNomorKontak(txtKontak.getText().trim());
                baru.setTanggalBergabung(tglBergabung);
                baru.setStatus((StatusAnggota) cmbStatus.getSelectedItem());
                baru.setKategoriTingkatId(katId);
                baru.setSubkelompokId(subId);
                baru.setCatatan(txtCatatan.getText().trim());

                anggotaService.simpanAnggotaBaru(baru, keteranganTingkat);
                JOptionPane.showMessageDialog(this, "Data anggota baru berhasil didaftarkan!", "Berhasil", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Ubah Anggota
                if (currentUser.canEditAnggota()) {
                    existingAnggota.setNamaLengkap(nama);
                    existingAnggota.setNomorKontak(txtKontak.getText().trim());
                    existingAnggota.setTanggalBergabung(tglBergabung);
                    existingAnggota.setStatus((StatusAnggota) cmbStatus.getSelectedItem());
                }
                existingAnggota.setSubkelompokId(subId);
                existingAnggota.setCatatan(txtCatatan.getText().trim());

                anggotaService.perbaruiAnggota(existingAnggota, katId, keteranganTingkat);
                JOptionPane.showMessageDialog(this, "Pembaruan data anggota berhasil disimpan!", "Berhasil", JOptionPane.INFORMATION_MESSAGE);
            }

            this.saved = true;
            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
