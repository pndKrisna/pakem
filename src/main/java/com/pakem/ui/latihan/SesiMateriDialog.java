package com.pakem.ui.latihan;

import com.pakem.model.JadwalLatihan;
import com.pakem.model.MateriTabuh;
import com.pakem.model.SesiMateri;
import com.pakem.service.interfaces.ILatihanService;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Dialog untuk mengelola sesi materi pada jadwal latihan tertentu,
 * melihat riwayat progres sesi sebelumnya, dan menyalin pengumuman ke clipboard WhatsApp.
 */
public class SesiMateriDialog extends JDialog {

    private final ILatihanService latihanService;
    private final JadwalLatihan jadwal;

    private JTable tblSesi;
    private DefaultTableModel modelSesi;
    private final List<SesiMateri> sesiList = new ArrayList<>();

    private JComboBox<MateriTabuh> comboMateri;
    private final List<MateriTabuh> masterMateriList = new ArrayList<>();

    private JLabel lblProgresTerakhirHeader;
    private JTextArea txtProgresTerakhir;
    private JTextArea txtCatatanProgresBaru;

    private JButton btnTambahSesi;
    private JButton btnUbahSesi;
    private JButton btnHapusSesi;
    private JButton btnSalinPengumuman;

    private SesiMateri selectedSesi = null;
    private boolean saved = false;

    public SesiMateriDialog(JFrame parent, ILatihanService latihanService, JadwalLatihan jadwal) {
        super(parent, "Sesi Materi Latihan & Catatan Progres", true);
        this.latihanService = latihanService;
        this.jadwal = jadwal;

        initUI();
        loadMasterMateri();
        loadSesiList();

        pack();
        setLocationRelativeTo(parent);
        setMinimumSize(new Dimension(860, 560));
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(241, 245, 249));

        // Top Bar Info Jadwal & Tombol Salin WA
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(14, 20, 14, 20)
        ));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        String kelompokInfo = jadwal.getNamaKategori() +
                (jadwal.getNamaSubkelompok() != null && !jadwal.getNamaSubkelompok().equals("-")
                        ? " (" + jadwal.getNamaSubkelompok() + ")" : "");

        String jamInfo = (jadwal.getJamMulai() != null ? jadwal.getJamMulai().toString() : "-") +
                " s.d. " + (jadwal.getJamSelesai() != null ? jadwal.getJamSelesai().toString() : "-");

        JLabel lblTitle = new JLabel("Latihan: " + kelompokInfo + " • " + jadwal.getTanggal());
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblTitle.setForeground(new Color(15, 23, 42));

        JLabel lblSubtitle = new JLabel("Waktu: " + jamInfo + " | Lokasi: " + (jadwal.getLokasi() != null ? jadwal.getLokasi() : "Sanggar") + " | Jenis: " + jadwal.getJenisJadwal().getLabel());
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitle.setForeground(new Color(100, 116, 139));

        titlePanel.add(lblTitle);
        titlePanel.add(Box.createVerticalStrut(2));
        titlePanel.add(lblSubtitle);

        btnSalinPengumuman = new JButton("📋 Salin Pengumuman (WA)");
        btnSalinPengumuman.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSalinPengumuman.setBackground(new Color(16, 185, 129)); // Emerald Green
        btnSalinPengumuman.setForeground(Color.WHITE);
        btnSalinPengumuman.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSalinPengumuman.setFocusPainted(false);
        btnSalinPengumuman.addActionListener(e -> salinFormatPengumuman());

        topBar.add(titlePanel, BorderLayout.WEST);
        topBar.add(btnSalinPengumuman, BorderLayout.EAST);

        add(topBar, BorderLayout.NORTH);

        // Center: Split Pane (Left: Daftar Sesi Materi, Right: Form Input / Edit & Referensi Progres)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(420);
        splitPane.setDividerSize(6);
        splitPane.setBorder(null);

        // Left Panel (Tabel Materi di Sesi Ini)
        JPanel leftPanel = createLeftPanel();
        splitPane.setLeftComponent(leftPanel);

        // Right Panel (Form Materi & Referensi Catatan Terakhir)
        JPanel rightPanel = createRightPanel();
        splitPane.setRightComponent(rightPanel);

        add(splitPane, BorderLayout.CENTER);

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(226, 232, 240)));

        JPanel headerLeft = new JPanel(new BorderLayout());
        headerLeft.setBackground(new Color(248, 250, 252));
        headerLeft.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)
        ));

        JLabel lblHeader = new JLabel("Materi yang Dilatih Sesi Ini");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblHeader.setForeground(new Color(30, 41, 59));
        headerLeft.add(lblHeader, BorderLayout.WEST);

        panel.add(headerLeft, BorderLayout.NORTH);

        String[] cols = {"Materi Tabuh", "Catatan Progres Sesi Ini"};
        modelSesi = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tblSesi = new JTable(modelSesi);
        tblSesi.setRowHeight(34);
        tblSesi.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblSesi.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblSesi.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblSesi.getTableHeader().setBackground(new Color(248, 250, 252));

        tblSesi.getColumnModel().getColumn(0).setPreferredWidth(140);
        tblSesi.getColumnModel().getColumn(1).setPreferredWidth(240);

        tblSesi.getSelectionModel().addListSelectionListener(e -> {
            boolean has = tblSesi.getSelectedRow() != -1;
            btnUbahSesi.setEnabled(has);
            btnHapusSesi.setEnabled(has);
            if (has) {
                int row = tblSesi.getSelectedRow();
                if (row >= 0 && row < sesiList.size()) {
                    selectedSesi = sesiList.get(row);
                    loadSesiIntoForm(selectedSesi);
                }
            } else {
                selectedSesi = null;
                resetForm();
            }
        });

        JScrollPane scrollTable = new JScrollPane(tblSesi);
        scrollTable.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        panel.add(scrollTable, BorderLayout.CENTER);

        // Action Toolbar Bawah
        JPanel botActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        botActions.setBackground(Color.WHITE);
        botActions.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));

        JButton btnBaru = new JButton("+ Sesi Baru");
        btnBaru.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnBaru.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBaru.addActionListener(e -> {
            tblSesi.clearSelection();
            resetForm();
        });

        btnHapusSesi = new JButton("Hapus Sesi");
        btnHapusSesi.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnHapusSesi.setForeground(new Color(220, 38, 38));
        btnHapusSesi.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnHapusSesi.setEnabled(false);
        btnHapusSesi.addActionListener(e -> hapusSesi());

        botActions.add(btnBaru);
        botActions.add(btnHapusSesi);

        panel.add(botActions, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(248, 250, 252));

        JPanel formCard = new JPanel();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setOpaque(false);
        formCard.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        // 1. Pilihan Materi Tabuh
        JLabel lblMateri = new JLabel("Pilih Materi Tabuh *");
        lblMateri.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblMateri.setForeground(new Color(30, 41, 59));
        lblMateri.setAlignmentX(Component.LEFT_ALIGNMENT);

        comboMateri = new JComboBox<>();
        comboMateri.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboMateri.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        comboMateri.setAlignmentX(Component.LEFT_ALIGNMENT);
        comboMateri.addActionListener(e -> onMateriSelected());

        formCard.add(lblMateri);
        formCard.add(Box.createVerticalStrut(4));
        formCard.add(comboMateri);
        formCard.add(Box.createVerticalStrut(14));

        // 2. Info Catatan Progres Sesi Sebelumnya (Kontinuitas Progres)
        lblProgresTerakhirHeader = new JLabel("📖 Catatan Progres Terakhir (Pertemuan Sebelumnya):");
        lblProgresTerakhirHeader.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblProgresTerakhirHeader.setForeground(new Color(79, 70, 229)); // Indigo
        lblProgresTerakhirHeader.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtProgresTerakhir = new JTextArea(3, 20);
        txtProgresTerakhir.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        txtProgresTerakhir.setLineWrap(true);
        txtProgresTerakhir.setWrapStyleWord(true);
        txtProgresTerakhir.setEditable(false);
        txtProgresTerakhir.setBackground(new Color(243, 244, 246));
        txtProgresTerakhir.setText("Pilih materi tabuh di atas untuk melihat progres latihan terakhir.");

        JScrollPane scrollPrev = new JScrollPane(txtProgresTerakhir);
        scrollPrev.setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));
        scrollPrev.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPrev.setBorder(BorderFactory.createLineBorder(new Color(229, 231, 235), 1, true));

        formCard.add(lblProgresTerakhirHeader);
        formCard.add(Box.createVerticalStrut(4));
        formCard.add(scrollPrev);
        formCard.add(Box.createVerticalStrut(14));

        // 3. Input Catatan Progres Sesi Ini
        JLabel lblProgresBaru = new JLabel("Catatan Progres Sesi Ini:");
        lblProgresBaru.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblProgresBaru.setForeground(new Color(30, 41, 59));
        lblProgresBaru.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtCatatanProgresBaru = new JTextArea(4, 20);
        txtCatatanProgresBaru.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtCatatanProgresBaru.setLineWrap(true);
        txtCatatanProgresBaru.setWrapStyleWord(true);

        JScrollPane scrollNew = new JScrollPane(txtCatatanProgresBaru);
        scrollNew.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        scrollNew.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollNew.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225), 1, true));

        formCard.add(lblProgresBaru);
        formCard.add(Box.createVerticalStrut(4));
        formCard.add(scrollNew);
        formCard.add(Box.createVerticalStrut(14));

        // Tombol Simpan / Ubah
        JPanel actionBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actionBtn.setOpaque(false);
        actionBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        actionBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        btnUbahSesi = new JButton("Update Catatan");
        btnUbahSesi.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnUbahSesi.setBackground(new Color(37, 99, 235));
        btnUbahSesi.setForeground(Color.WHITE);
        btnUbahSesi.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnUbahSesi.setEnabled(false);
        btnUbahSesi.addActionListener(e -> simpanSesi(true));

        btnTambahSesi = new JButton("+ Tambah ke Jadwal");
        btnTambahSesi.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnTambahSesi.setBackground(new Color(37, 99, 235));
        btnTambahSesi.setForeground(Color.WHITE);
        btnTambahSesi.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTambahSesi.addActionListener(e -> simpanSesi(false));

        actionBtn.add(btnUbahSesi);
        actionBtn.add(btnTambahSesi);

        formCard.add(actionBtn);

        panel.add(formCard, BorderLayout.CENTER);
        return panel;
    }

    private void loadMasterMateri() {
        comboMateri.removeAllItems();
        masterMateriList.clear();

        try {
            List<MateriTabuh> data = latihanService.getSemuaMateriTabuh();
            masterMateriList.addAll(data);
            comboMateri.addItem(new MateriTabuh(0, "-- Pilih Materi Tabuh --", ""));
            for (MateriTabuh m : data) {
                comboMateri.addItem(m);
            }
        } catch (Exception ex) {
            System.err.println("Gagal memuat master materi: " + ex.getMessage());
        }
    }

    private void loadSesiList() {
        modelSesi.setRowCount(0);
        sesiList.clear();

        try {
            List<SesiMateri> data = latihanService.getSesiMateriByJadwal(jadwal.getId());
            sesiList.addAll(data);

            for (SesiMateri s : data) {
                modelSesi.addRow(new Object[]{
                        s.getNamaMateri(),
                        s.getCatatanProgres() != null && !s.getCatatanProgres().isEmpty() ? s.getCatatanProgres() : "(Belum ada catatan)"
                });
            }

            resetForm();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat sesi materi: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onMateriSelected() {
        Object item = comboMateri.getSelectedItem();
        if (item instanceof MateriTabuh m && m.getId() > 0) {
            // Ambil progres terakhir materi ini
            try {
                SesiMateri last = latihanService.getProgresTerakhirByMateri(m.getId());
                if (last != null && last.getCatatanProgres() != null && !last.getCatatanProgres().trim().isEmpty()) {
                    lblProgresTerakhirHeader.setText("📖 Catatan Progres Terakhir (" + last.getTanggalJadwal() + "):");
                    txtProgresTerakhir.setText(last.getCatatanProgres());
                } else {
                    lblProgresTerakhirHeader.setText("📖 Catatan Progres Terakhir:");
                    txtProgresTerakhir.setText("Belum ada riwayat progres sebelumnya untuk materi ini.");
                }
            } catch (Exception ex) {
                txtProgresTerakhir.setText("Tidak dapat mengambil referensi progres: " + ex.getMessage());
            }
        } else {
            lblProgresTerakhirHeader.setText("📖 Catatan Progres Terakhir (Pertemuan Sebelumnya):");
            txtProgresTerakhir.setText("Pilih materi tabuh di atas untuk melihat progres latihan terakhir.");
        }
    }

    private void loadSesiIntoForm(SesiMateri sesi) {
        if (sesi == null) return;

        for (int i = 0; i < comboMateri.getItemCount(); i++) {
            MateriTabuh m = comboMateri.getItemAt(i);
            if (m.getId() == sesi.getMateriTabuhId()) {
                comboMateri.setSelectedIndex(i);
                break;
            }
        }

        txtCatatanProgresBaru.setText(sesi.getCatatanProgres() != null ? sesi.getCatatanProgres() : "");
        btnTambahSesi.setEnabled(false);
        btnUbahSesi.setEnabled(true);
    }

    private void resetForm() {
        if (comboMateri.getItemCount() > 0) {
            comboMateri.setSelectedIndex(0);
        }
        txtCatatanProgresBaru.setText("");
        btnTambahSesi.setEnabled(true);
        btnUbahSesi.setEnabled(false);
        selectedSesi = null;
    }

    private void simpanSesi(boolean isUpdate) {
        Object item = comboMateri.getSelectedItem();
        if (!(item instanceof MateriTabuh m) || m.getId() <= 0) {
            JOptionPane.showMessageDialog(this, "Silakan pilih materi tabuh terlebih dahulu.", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String catatan = txtCatatanProgresBaru.getText().trim();

        try {
            if (isUpdate && selectedSesi != null) {
                selectedSesi.setMateriTabuhId(m.getId());
                selectedSesi.setCatatanProgres(catatan);
                latihanService.ubahSesiMateri(selectedSesi);
                JOptionPane.showMessageDialog(this, "Catatan progres materi berhasil diperbarui.", "Berhasil", JOptionPane.INFORMATION_MESSAGE);
            } else {
                SesiMateri baru = new SesiMateri(0, jadwal.getId(), m.getId(), catatan);
                latihanService.simpanSesiMateri(baru);
                JOptionPane.showMessageDialog(this, "Materi tabuh berhasil ditambahkan ke jadwal sesi ini.", "Berhasil", JOptionPane.INFORMATION_MESSAGE);
            }

            saved = true;
            loadSesiList();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void hapusSesi() {
        if (selectedSesi == null) return;

        int confirm = JOptionPane.showConfirmDialog(this,
                "Hapus materi '" + selectedSesi.getNamaMateri() + "' dari jadwal sesi ini?",
                "Konfirmasi Hapus Sesi",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                latihanService.hapusSesiMateri(selectedSesi.getId());
                saved = true;
                loadSesiList();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal menghapus: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Memformat teks rapi informasi jadwal latihan dan materi ke clipboard (fitur Salin WA).
     */
    private void salinFormatPengumuman() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", new Locale("id", "ID"));
        String tanggalFormatted = jadwal.getTanggal().format(dtf);

        String jamMulaiStr = jadwal.getJamMulai() != null ? jadwal.getJamMulai().toString() : "00:00";
        String jamSelesaiStr = jadwal.getJamSelesai() != null ? jadwal.getJamSelesai().toString() : "selesai";

        String lokasiStr = jadwal.getLokasi() != null && !jadwal.getLokasi().isEmpty() ? jadwal.getLokasi() : "Sanggar";
        String kelompokStr = jadwal.getNamaKategori() +
                (jadwal.getNamaSubkelompok() != null && !jadwal.getNamaSubkelompok().equals("-")
                        ? " - " + jadwal.getNamaSubkelompok() : "");

        StringBuilder sb = new StringBuilder();
        sb.append("📢 *PENGUMUMAN JADWAL LATIHAN TABUH*\n");
        sb.append("------------------------------------------\n");
        sb.append("🗓 *Hari/Tanggal:* ").append(tanggalFormatted).append("\n");
        sb.append("⏰ *Waktu:* ").append(jamMulaiStr).append(" - ").append(jamSelesaiStr).append(" WITA\n");
        sb.append("📍 *Lokasi:* ").append(lokasiStr).append("\n");
        sb.append("👥 *Kelompok:* ").append(kelompokStr).append("\n");
        sb.append("🏷 *Jenis Latihan:* ").append(jadwal.getJenisJadwal().getLabel()).append("\n\n");

        sb.append("🎵 *Materi yang Dipelajari:*\n");
        if (sesiList.isEmpty()) {
            sb.append("  _(Materi tabuh belum ditentukan)_\n");
        } else {
            for (int i = 0; i < sesiList.size(); i++) {
                SesiMateri s = sesiList.get(i);
                sb.append("  ").append(i + 1).append(". *").append(s.getNamaMateri()).append("*\n");
                if (s.getCatatanProgres() != null && !s.getCatatanProgres().trim().isEmpty()) {
                    sb.append("     _Target/Progres: ").append(s.getCatatanProgres().trim()).append("_\n");
                }
            }
        }

        if (jadwal.getCatatan() != null && !jadwal.getCatatan().trim().isEmpty()) {
            sb.append("\n📝 *Catatan Tambahan:*\n");
            sb.append(jadwal.getCatatan().trim()).append("\n");
        }

        sb.append("\n_Diharapkan hadir tepat waktu demi kelancaran latihan bersama. Suksma._ 🙏✨");

        StringSelection selection = new StringSelection(sb.toString());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);

        JOptionPane.showMessageDialog(this,
                "Format pengumuman jadwal latihan berhasil disalin ke clipboard!\nAnda dapat langsung menempelkannya (Paste / Ctrl+V) ke grup WhatsApp.",
                "Berhasil Disalin", JOptionPane.INFORMATION_MESSAGE);
    }

    public boolean isSaved() {
        return saved;
    }
}
