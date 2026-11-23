package com.pakem.ui.latihan;

import com.pakem.model.JadwalLatihan;
import com.pakem.model.KategoriTingkat;
import com.pakem.model.Subkelompok;
import com.pakem.model.User;
import com.pakem.service.interfaces.ILatihanService;
import com.pakem.service.interfaces.IKategoriService;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel utama untuk Modul Jadwal Latihan (Rutin & Khusus Event).
 * Menampilkan daftar jadwal terurut waktu, dengan filter jenis dan kategori.
 */
public class JadwalLatihanPanel extends JPanel {

    private final ILatihanService latihanService;
    private final IKategoriService kategoriService;
    private final User currentUser;

    private JTable tblJadwal;
    private DefaultTableModel modelJadwal;
    private final List<JadwalLatihan> jadwalList = new ArrayList<>();
    private JButton btnTambahJadwal;
    private JButton btnUbahJadwal;
    private JButton btnHapusJadwal;
    private JButton btnLihatSesi;

    private JComboBox<String> filterJenis;
    private JComboBox<KategoriTingkat> filterKategori;
    private JButton btnRefresh;

    private JadwalLatihan selectedJadwal = null;
    private boolean isLoading = false;

    public JadwalLatihanPanel(ILatihanService latihanService, IKategoriService kategoriService, User currentUser) {
        this.latihanService = latihanService;
        this.kategoriService = kategoriService;
        this.currentUser = currentUser;

        initUI();
        loadJadwalList();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(241, 245, 249));

        // Top Bar Header
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(16, 24, 16, 24)
        ));

        JPanel titleTextPanel = new JPanel();
        titleTextPanel.setLayout(new BoxLayout(titleTextPanel, BoxLayout.Y_AXIS));
        titleTextPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Jadwal Latihan & Materi Tabuh");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(15, 23, 42));

        JLabel lblSubtitle = new JLabel("Kelola jadwal latihan rutin dan khusus event, serta catat progres materi per sesi.");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitle.setForeground(new Color(100, 116, 139));

        titleTextPanel.add(lblTitle);
        titleTextPanel.add(Box.createVerticalStrut(2));
        titleTextPanel.add(lblSubtitle);

        topBar.add(titleTextPanel, BorderLayout.WEST);

        if (!currentUser.canManageJadwalLatihan()) {
            JPanel bannerPelatih = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            bannerPelatih.setOpaque(false);
            JLabel lblPelatih = new JLabel("  Mode Pelatih: Lihat & Catat Progres  ");
            lblPelatih.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lblPelatih.setOpaque(true);
            lblPelatih.setBackground(new Color(254, 243, 199));
            lblPelatih.setForeground(new Color(180, 83, 9));
            lblPelatih.setBorder(BorderFactory.createLineBorder(new Color(252, 211, 77), 1, true));
            bannerPelatih.add(lblPelatih);
            topBar.add(bannerPelatih, BorderLayout.EAST);
        }

        add(topBar, BorderLayout.NORTH);

        // Toolbar Filter & Aksi
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        toolbar.setBackground(Color.WHITE);
        toolbar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));

        JLabel lblJenis = new JLabel("Jenis:");
        lblJenis.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblJenis.setForeground(new Color(51, 65, 85));

        filterJenis = new JComboBox<>(new String[]{"Semua", "Rutin", "Khusus Event"});
        filterJenis.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        filterJenis.setPreferredSize(new Dimension(130, 30));

        JLabel lblKategori = new JLabel("Kategori:");
        lblKategori.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblKategori.setForeground(new Color(51, 65, 85));

        filterKategori = new JComboBox<>();
        filterKategori.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        filterKategori.setPreferredSize(new Dimension(150, 30));

        btnRefresh = new JButton("Refresh");
        btnRefresh.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh.addActionListener(e -> loadJadwalList());

        btnTambahJadwal = new JButton("+ Tambah Jadwal");
        btnTambahJadwal.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnTambahJadwal.setBackground(new Color(37, 99, 235));
        btnTambahJadwal.setForeground(Color.WHITE);
        btnTambahJadwal.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTambahJadwal.setVisible(currentUser.canManageJadwalLatihan());
        btnTambahJadwal.addActionListener(e -> openAddJadwalDialog());

        btnUbahJadwal = new JButton("Ubah Jadwal");
        btnUbahJadwal.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnUbahJadwal.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnUbahJadwal.setEnabled(false);
        btnUbahJadwal.setVisible(currentUser.canManageJadwalLatihan());
        btnUbahJadwal.addActionListener(e -> openEditJadwalDialog());

        btnHapusJadwal = new JButton("Hapus Jadwal");
        btnHapusJadwal.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnHapusJadwal.setForeground(new Color(220, 38, 38));
        btnHapusJadwal.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnHapusJadwal.setEnabled(false);
        btnHapusJadwal.setVisible(currentUser.canManageJadwalLatihan());
        btnHapusJadwal.addActionListener(e -> deleteJadwal());

        btnLihatSesi = new JButton("Lihat Sesi Materi");
        btnLihatSesi.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnLihatSesi.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLihatSesi.setEnabled(false);
        btnLihatSesi.addActionListener(e -> openSesiMateriDialog());

        toolbar.add(lblJenis);
        toolbar.add(filterJenis);
        toolbar.add(lblKategori);
        toolbar.add(filterKategori);
        toolbar.add(btnRefresh);
        toolbar.add(Box.createHorizontalStrut(20));
        toolbar.add(btnTambahJadwal);
        toolbar.add(btnUbahJadwal);
        toolbar.add(btnHapusJadwal);
        toolbar.add(btnLihatSesi);

        add(toolbar, BorderLayout.NORTH);

        // Tabel Jadwal
        String[] columns = {"ID", "Tanggal", "Jam Mulai", "Jam Selesai", "Jenis", "Kategori", "Subkelompok", "Lokasi"};
        modelJadwal = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tblJadwal = new JTable(modelJadwal);
        tblJadwal.setRowHeight(34);
        tblJadwal.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblJadwal.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblJadwal.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblJadwal.getTableHeader().setBackground(new Color(248, 250, 252));

        tblJadwal.getColumnModel().getColumn(0).setPreferredWidth(45);
        tblJadwal.getColumnModel().getColumn(1).setPreferredWidth(90);
        tblJadwal.getColumnModel().getColumn(2).setPreferredWidth(75);
        tblJadwal.getColumnModel().getColumn(3).setPreferredWidth(75);
        tblJadwal.getColumnModel().getColumn(4).setPreferredWidth(90);
        tblJadwal.getColumnModel().getColumn(5).setPreferredWidth(120);
        tblJadwal.getColumnModel().getColumn(6).setPreferredWidth(120);
        tblJadwal.getColumnModel().getColumn(7).setPreferredWidth(150);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i <= 4; i++) {
            tblJadwal.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        tblJadwal.getSelectionModel().addListSelectionListener(e -> {
            boolean has = tblJadwal.getSelectedRow() != -1;
            btnUbahJadwal.setEnabled(has && currentUser.canManageJadwalLatihan());
            btnHapusJadwal.setEnabled(has && currentUser.canManageJadwalLatihan());
            btnLihatSesi.setEnabled(has);
            if (has) {
                int row = tblJadwal.getSelectedRow();
                if (row >= 0 && row < jadwalList.size()) {
                    selectedJadwal = jadwalList.get(row);
                }
            }
        });

        JScrollPane scrollTable = new JScrollPane(tblJadwal);
        scrollTable.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        add(scrollTable, BorderLayout.CENTER);

        filterJenis.addActionListener(e -> applyFilter());
        filterKategori.addActionListener(e -> applyFilter());
    }

    private void loadJadwalList() {
        isLoading = true;
        modelJadwal.setRowCount(0);
        jadwalList.clear();

        try {
            List<JadwalLatihan> data = latihanService.getSemuaJadwalLatihan();
            jadwalList.addAll(data);

            // Muat daftar kategori untuk filter
            filterKategori.removeAllItems();
            filterKategori.addItem(new KategoriTingkat(0, "Semua Kategori", 0));
            try {
                List<KategoriTingkat> kategoriList = kategoriService.getSemuaKategori();
                for (KategoriTingkat k : kategoriList) {
                    filterKategori.addItem(k);
                }
            } catch (Exception ex) {
                System.err.println("Gagal memuat kategori filter: " + ex.getMessage());
            }

            for (JadwalLatihan j : data) {
                modelJadwal.addRow(new Object[]{
                        j.getId(),
                        j.getTanggal().toString(),
                        j.getJamMulai() != null ? j.getJamMulai().toString() : "-",
                        j.getJamSelesai() != null ? j.getJamSelesai().toString() : "-",
                        j.getJenisJadwal().getLabel(),
                        j.getNamaKategori(),
                        j.getNamaSubkelompok(),
                        j.getLokasi() != null ? j.getLokasi() : "-"
                });
            }

            selectedJadwal = null;
            btnUbahJadwal.setEnabled(false);
            btnHapusJadwal.setEnabled(false);
            btnLihatSesi.setEnabled(false);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat jadwal latihan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            isLoading = false;
        }
    }

    private void applyFilter() {
        if (isLoading) return;
        String jenis = (String) filterJenis.getSelectedItem();
        Object katItem = filterKategori.getSelectedItem();
        int kategoriId = katItem instanceof KategoriTingkat ? ((KategoriTingkat) katItem).getId() : 0;

        modelJadwal.setRowCount(0);
        for (JadwalLatihan j : jadwalList) {
            boolean matchJenis = jenis.equals("Semua") || j.getJenisJadwal().name().equalsIgnoreCase(jenis);
            boolean matchKategori = kategoriId == 0 || j.getKategoriTingkatId() != null && j.getKategoriTingkatId() == kategoriId;
            if (matchJenis && matchKategori) {
                modelJadwal.addRow(new Object[]{
                        j.getId(),
                        j.getTanggal().toString(),
                        j.getJamMulai() != null ? j.getJamMulai().toString() : "-",
                        j.getJamSelesai() != null ? j.getJamSelesai().toString() : "-",
                        j.getJenisJadwal().getLabel(),
                        j.getNamaKategori(),
                        j.getNamaSubkelompok(),
                        j.getLokasi() != null ? j.getLokasi() : "-"
                });
            }
        }
    }

    private void openAddJadwalDialog() {
        try {
            JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (parent == null) {
                JOptionPane.showMessageDialog(this,
                        "Tidak dapat menemukan jendela induk aplikasi.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            JadwalLatihanFormDialog dialog = new JadwalLatihanFormDialog(parent, latihanService, kategoriService, null);
            dialog.setVisible(true);
            if (dialog.isSaved()) {
                loadJadwalList();
            }
        } catch (Throwable ex) {
            JOptionPane.showMessageDialog(this,
                    "Gagal membuka dialog tambah jadwal: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void openEditJadwalDialog() {
        if (selectedJadwal == null) return;
        try {
            JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (parent == null) {
                JOptionPane.showMessageDialog(this,
                        "Tidak dapat menemukan jendela induk aplikasi.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            JadwalLatihanFormDialog dialog = new JadwalLatihanFormDialog(parent, latihanService, kategoriService, selectedJadwal);
            dialog.setVisible(true);
            if (dialog.isSaved()) {
                loadJadwalList();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Gagal membuka dialog ubah jadwal: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void deleteJadwal() {
        if (selectedJadwal == null) return;

        int confirm = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin menghapus jadwal latihan '" + selectedJadwal.getTanggal() + "'?\n" +
                "Semua sesi materi dan catatan progres pada jadwal ini akan ikut dihapus.",
                "Konfirmasi Hapus Jadwal",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                latihanService.hapusJadwalLatihan(selectedJadwal.getId());
                JOptionPane.showMessageDialog(this, "Jadwal latihan berhasil dihapus.", "Berhasil", JOptionPane.INFORMATION_MESSAGE);
                loadJadwalList();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal menghapus: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void openSesiMateriDialog() {
        if (selectedJadwal == null) return;
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        SesiMateriDialog dialog = new SesiMateriDialog(parent, latihanService, selectedJadwal);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            loadJadwalList();
        }
    }
}
