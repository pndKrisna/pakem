package com.pakem.ui.kategori;

import com.pakem.model.Anggota;
import com.pakem.model.KategoriTingkat;
import com.pakem.model.Subkelompok;
import com.pakem.model.User;
import com.pakem.service.interfaces.IKategoriService;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel utama untuk Modul Kategori Tingkat, Subkelompok, dan Auto-Grouping.
 */
public class KategoriTingkatPanel extends JPanel {

    private final IKategoriService kategoriService;
    private final User currentUser;

    // Left Panel (Kategori Tingkat)
    private JTable tblKategori;
    private DefaultTableModel modelKategori;
    private final List<KategoriTingkat> kategoriList = new ArrayList<>();
    private JButton btnTambahKategori;
    private JButton btnUbahKategori;
    private JButton btnHapusKategori;

    // Right Panel (Detail Subkelompok & Penempatan Anggota)
    private JPanel rightContainer;
    private CardLayout rightCardLayout;
    private static final String CARD_EMPTY = "CARD_EMPTY";
    private static final String CARD_DETAIL = "CARD_DETAIL";

    private JLabel lblSelectedKategoriTitle;
    private JLabel lblKapasitasAnjuran;

    private JTable tblSubkelompok;
    private DefaultTableModel modelSubkelompok;
    private final List<Subkelompok> subkelompokList = new ArrayList<>();
    private JButton btnTambahSub;
    private JButton btnUbahSub;
    private JButton btnHapusSub;
    private JButton btnAutoGrouping;

    private JTable tblAnggotaKategori;
    private DefaultTableModel modelAnggotaKategori;
    private final List<Anggota> anggotaKategoriList = new ArrayList<>();
    private JButton btnPindahSub;

    private KategoriTingkat selectedKategori = null;
    private int kapasitasAnjuran = 25;

    public KategoriTingkatPanel(IKategoriService kategoriService, User currentUser) {
        this.kategoriService = kategoriService;
        this.currentUser = currentUser;

        initUI();
        loadKategoriList();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(241, 245, 249));

        // 1. Top Bar Header
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(16, 24, 16, 24)
        ));

        JPanel titleTextPanel = new JPanel();
        titleTextPanel.setLayout(new BoxLayout(titleTextPanel, BoxLayout.Y_AXIS));
        titleTextPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Master Kategori Tingkat & Subkelompok");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(15, 23, 42));

        JLabel lblSubtitle = new JLabel("Kelola jenjang kemampuan tabuh, subkelompok latihan, serta pembagian merata anggota (Auto-Grouping).");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitle.setForeground(new Color(100, 116, 139));

        titleTextPanel.add(lblTitle);
        titleTextPanel.add(Box.createVerticalStrut(2));
        titleTextPanel.add(lblSubtitle);

        topBar.add(titleTextPanel, BorderLayout.WEST);

        if (!currentUser.canManageKategoriTingkat()) {
            JPanel bannerPelatih = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            bannerPelatih.setOpaque(false);
            JLabel lblPelatih = new JLabel("  Mode Pelatih: Hanya Melihat Struktur  ");
            lblPelatih.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lblPelatih.setOpaque(true);
            lblPelatih.setBackground(new Color(254, 243, 199));
            lblPelatih.setForeground(new Color(180, 83, 9));
            lblPelatih.setBorder(BorderFactory.createLineBorder(new Color(252, 211, 77), 1, true));
            bannerPelatih.add(lblPelatih);
            topBar.add(bannerPelatih, BorderLayout.EAST);
        }

        add(topBar, BorderLayout.NORTH);

        // 2. Split Pane (Left: Kategori, Right: Subkelompok & Anggota)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(360);
        splitPane.setDividerSize(6);
        splitPane.setBorder(null);

        // Left Panel
        JPanel leftPanel = createLeftPanel();
        splitPane.setLeftComponent(leftPanel);

        // Right Panel Container
        rightCardLayout = new CardLayout();
        rightContainer = new JPanel(rightCardLayout);
        rightContainer.setBackground(new Color(241, 245, 249));

        // Empty state view
        JPanel emptyPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 150));
        emptyPanel.setBackground(new Color(248, 250, 252));
        JLabel lblEmpty = new JLabel("Pilih salah satu kategori tingkat di sebelah kiri untuk melihat rincian subkelompok.");
        lblEmpty.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblEmpty.setForeground(new Color(148, 163, 184));
        emptyPanel.add(lblEmpty);
        rightContainer.add(emptyPanel, CARD_EMPTY);

        // Detail state view
        JPanel detailPanel = createRightDetailPanel();
        rightContainer.add(detailPanel, CARD_DETAIL);

        splitPane.setRightComponent(rightContainer);
        add(splitPane, BorderLayout.CENTER);

        rightCardLayout.show(rightContainer, CARD_EMPTY);
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(226, 232, 240)));

        // Header Left
        JPanel headerLeft = new JPanel(new BorderLayout());
        headerLeft.setBackground(new Color(248, 250, 252));
        headerLeft.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));

        JLabel lblLeftTitle = new JLabel("Daftar Jenjang / Kategori");
        lblLeftTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblLeftTitle.setForeground(new Color(30, 41, 59));
        headerLeft.add(lblLeftTitle, BorderLayout.WEST);

        panel.add(headerLeft, BorderLayout.NORTH);

        // Table Kategori
        String[] columns = {"Urut", "Nama Kategori", "Anggota"};
        modelKategori = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tblKategori = new JTable(modelKategori);
        tblKategori.setRowHeight(34);
        tblKategori.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblKategori.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblKategori.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblKategori.getTableHeader().setBackground(new Color(248, 250, 252));

        tblKategori.getColumnModel().getColumn(0).setPreferredWidth(45);
        tblKategori.getColumnModel().getColumn(1).setPreferredWidth(170);
        tblKategori.getColumnModel().getColumn(2).setPreferredWidth(65);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tblKategori.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tblKategori.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);

        tblKategori.getSelectionModel().addListSelectionListener(e -> {
            boolean hasSelection = tblKategori.getSelectedRow() != -1;
            btnUbahKategori.setEnabled(hasSelection && currentUser.canManageKategoriTingkat());
            btnHapusKategori.setEnabled(hasSelection && currentUser.canManageKategoriTingkat());
            if (hasSelection) {
                int row = tblKategori.getSelectedRow();
                if (row >= 0 && row < kategoriList.size()) {
                    selectedKategori = kategoriList.get(row);
                    loadRightDetail(selectedKategori);
                }
            }
        });

        JScrollPane scrollTable = new JScrollPane(tblKategori);
        scrollTable.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        panel.add(scrollTable, BorderLayout.CENTER);

        // Action Buttons Bottom
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
        actionPanel.setBackground(Color.WHITE);
        actionPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));

        btnTambahKategori = new JButton("+ Tambah");
        btnTambahKategori.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnTambahKategori.setBackground(new Color(37, 99, 235));
        btnTambahKategori.setForeground(Color.WHITE);
        btnTambahKategori.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTambahKategori.setVisible(currentUser.canManageKategoriTingkat());
        btnTambahKategori.addActionListener(e -> openAddKategoriDialog());

        btnUbahKategori = new JButton("Ubah");
        btnUbahKategori.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnUbahKategori.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnUbahKategori.setEnabled(false);
        btnUbahKategori.setVisible(currentUser.canManageKategoriTingkat());
        btnUbahKategori.addActionListener(e -> openEditKategoriDialog());

        btnHapusKategori = new JButton("Hapus");
        btnHapusKategori.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnHapusKategori.setForeground(new Color(220, 38, 38));
        btnHapusKategori.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnHapusKategori.setEnabled(false);
        btnHapusKategori.setVisible(currentUser.canManageKategoriTingkat());
        btnHapusKategori.addActionListener(e -> deleteKategori());

        actionPanel.add(btnTambahKategori);
        actionPanel.add(btnUbahKategori);
        actionPanel.add(btnHapusKategori);

        panel.add(actionPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createRightDetailPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(248, 250, 252));

        // Header Kategori Terpilih
        JPanel headerDetail = new JPanel(new BorderLayout());
        headerDetail.setBackground(Color.WHITE);
        headerDetail.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(14, 20, 14, 20)
        ));

        JPanel titleSubPanel = new JPanel();
        titleSubPanel.setLayout(new BoxLayout(titleSubPanel, BoxLayout.Y_AXIS));
        titleSubPanel.setOpaque(false);

        lblSelectedKategoriTitle = new JLabel("Kategori: -");
        lblSelectedKategoriTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblSelectedKategoriTitle.setForeground(new Color(30, 41, 59));

        lblKapasitasAnjuran = new JLabel("Kapasitas Anjuran: 25 orang / kelompok");
        lblKapasitasAnjuran.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblKapasitasAnjuran.setForeground(new Color(100, 116, 139));

        titleSubPanel.add(lblSelectedKategoriTitle);
        titleSubPanel.add(Box.createVerticalStrut(2));
        titleSubPanel.add(lblKapasitasAnjuran);

        headerDetail.add(titleSubPanel, BorderLayout.WEST);

        // Auto-Grouping & Sub Actions (Top Right)
        JPanel actionAutoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actionAutoPanel.setOpaque(false);

        btnAutoGrouping = new JButton("⚡ Auto-Grouping (Bagi Rata)");
        btnAutoGrouping.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAutoGrouping.setBackground(new Color(16, 185, 129)); // Emerald green
        btnAutoGrouping.setForeground(Color.WHITE);
        btnAutoGrouping.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAutoGrouping.setFocusPainted(false);
        btnAutoGrouping.setVisible(currentUser.canManageKategoriTingkat());
        btnAutoGrouping.addActionListener(e -> runAutoGrouping());

        actionAutoPanel.add(btnAutoGrouping);
        headerDetail.add(actionAutoPanel, BorderLayout.EAST);

        panel.add(headerDetail, BorderLayout.NORTH);

        // Tabbed Pane: Tab 1 (Daftar Subkelompok), Tab 2 (Daftar Anggota Kategori)
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 12));

        // Tab 1: Subkelompok
        JPanel tabSubkelompok = createTabSubkelompok();
        tabbedPane.addTab("Daftar Subkelompok", tabSubkelompok);

        // Tab 2: Anggota Kategori
        JPanel tabAnggota = createTabAnggotaKategori();
        tabbedPane.addTab("Penempatan Anggota Kategori", tabAnggota);

        panel.add(tabbedPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createTabSubkelompok() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(248, 250, 252));

        // Toolbar Subkelompok
        JPanel tbSub = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        tbSub.setBackground(Color.WHITE);
        tbSub.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));

        btnTambahSub = new JButton("+ Tambah Subkelompok");
        btnTambahSub.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnTambahSub.setBackground(new Color(37, 99, 235));
        btnTambahSub.setForeground(Color.WHITE);
        btnTambahSub.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTambahSub.setVisible(currentUser.canManageKategoriTingkat());
        btnTambahSub.addActionListener(e -> openAddSubkelompokDialog());

        btnUbahSub = new JButton("Ubah Nama");
        btnUbahSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnUbahSub.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnUbahSub.setEnabled(false);
        btnUbahSub.setVisible(currentUser.canManageKategoriTingkat());
        btnUbahSub.addActionListener(e -> openEditSubkelompokDialog());

        btnHapusSub = new JButton("Hapus Subkelompok");
        btnHapusSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnHapusSub.setForeground(new Color(220, 38, 38));
        btnHapusSub.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnHapusSub.setEnabled(false);
        btnHapusSub.setVisible(currentUser.canManageKategoriTingkat());
        btnHapusSub.addActionListener(e -> deleteSubkelompok());

        tbSub.add(btnTambahSub);
        tbSub.add(btnUbahSub);
        tbSub.add(btnHapusSub);

        panel.add(tbSub, BorderLayout.NORTH);

        // Table Subkelompok
        String[] cols = {"Nama Subkelompok", "Jumlah Anggota", "Status Kapasitas (Anjuran: 25)"};
        modelSubkelompok = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tblSubkelompok = new JTable(modelSubkelompok);
        tblSubkelompok.setRowHeight(36);
        tblSubkelompok.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblSubkelompok.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblSubkelompok.getTableHeader().setBackground(new Color(248, 250, 252));

        tblSubkelompok.getColumnModel().getColumn(0).setPreferredWidth(160);
        tblSubkelompok.getColumnModel().getColumn(1).setPreferredWidth(110);
        tblSubkelompok.getColumnModel().getColumn(2).setPreferredWidth(260);

        DefaultTableCellRenderer centerR = new DefaultTableCellRenderer();
        centerR.setHorizontalAlignment(JLabel.CENTER);
        tblSubkelompok.getColumnModel().getColumn(1).setCellRenderer(centerR);
        tblSubkelompok.getColumnModel().getColumn(2).setCellRenderer(new KapasitasBadgeRenderer());

        tblSubkelompok.getSelectionModel().addListSelectionListener(e -> {
            boolean has = tblSubkelompok.getSelectedRow() != -1;
            btnUbahSub.setEnabled(has && currentUser.canManageKategoriTingkat());
            btnHapusSub.setEnabled(has && currentUser.canManageKategoriTingkat());
        });

        JScrollPane scroll = new JScrollPane(tblSubkelompok);
        scroll.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createTabAnggotaKategori() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(248, 250, 252));

        // Toolbar Anggota Kategori
        JPanel tbAnggota = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        tbAnggota.setBackground(Color.WHITE);
        tbAnggota.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));

        btnPindahSub = new JButton("Pindahkan Subkelompok...");
        btnPindahSub.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnPindahSub.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPindahSub.setEnabled(false);
        btnPindahSub.setVisible(currentUser.canManageKategoriTingkat());
        btnPindahSub.addActionListener(e -> openPindahSubDialog());

        JButton btnRefreshAnggota = new JButton("Refresh Daftar");
        btnRefreshAnggota.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnRefreshAnggota.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefreshAnggota.addActionListener(e -> {
            if (selectedKategori != null) loadRightDetail(selectedKategori);
        });

        tbAnggota.add(btnPindahSub);
        tbAnggota.add(btnRefreshAnggota);

        panel.add(tbAnggota, BorderLayout.NORTH);

        // Table Anggota
        String[] cols = {"ID", "Nama Anggota", "Status", "Subkelompok Saat Ini"};
        modelAnggotaKategori = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tblAnggotaKategori = new JTable(modelAnggotaKategori);
        tblAnggotaKategori.setRowHeight(34);
        tblAnggotaKategori.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblAnggotaKategori.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblAnggotaKategori.getTableHeader().setBackground(new Color(248, 250, 252));

        tblAnggotaKategori.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblAnggotaKategori.getColumnModel().getColumn(1).setPreferredWidth(200);
        tblAnggotaKategori.getColumnModel().getColumn(2).setPreferredWidth(100);
        tblAnggotaKategori.getColumnModel().getColumn(3).setPreferredWidth(160);

        DefaultTableCellRenderer centerR = new DefaultTableCellRenderer();
        centerR.setHorizontalAlignment(JLabel.CENTER);
        tblAnggotaKategori.getColumnModel().getColumn(0).setCellRenderer(centerR);
        tblAnggotaKategori.getColumnModel().getColumn(2).setCellRenderer(centerR);

        tblAnggotaKategori.getSelectionModel().addListSelectionListener(e -> {
            boolean has = tblAnggotaKategori.getSelectedRow() != -1;
            btnPindahSub.setEnabled(has && currentUser.canManageKategoriTingkat());
        });

        JScrollPane scroll = new JScrollPane(tblAnggotaKategori);
        scroll.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    public void loadKategoriList() {
        modelKategori.setRowCount(0);
        kategoriList.clear();

        try {
            kapasitasAnjuran = kategoriService.getKapasitasAnjuran();
            List<KategoriTingkat> data = kategoriService.getSemuaKategori();
            kategoriList.addAll(data);

            for (KategoriTingkat k : data) {
                int jumlahAnggota = kategoriService.getJumlahAnggotaKategori(k.getId());
                modelKategori.addRow(new Object[]{
                        k.getUrutan(),
                        k.getNama(),
                        jumlahAnggota
                });
            }

            if (!kategoriList.isEmpty()) {
                tblKategori.setRowSelectionInterval(0, 0);
            } else {
                rightCardLayout.show(rightContainer, CARD_EMPTY);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat kategori: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadRightDetail(KategoriTingkat kat) {
        if (kat == null) {
            rightCardLayout.show(rightContainer, CARD_EMPTY);
            return;
        }

        rightCardLayout.show(rightContainer, CARD_DETAIL);
        lblSelectedKategoriTitle.setText("Kategori: " + kat.getNama() + " (Urutan #" + kat.getUrutan() + ")");
        lblKapasitasAnjuran.setText("Kapasitas Anjuran Kelompok: " + kapasitasAnjuran + " orang");

        // 1. Muat Subkelompok
        modelSubkelompok.setRowCount(0);
        subkelompokList.clear();
        try {
            List<Subkelompok> subs = kategoriService.getSubkelompokByKategori(kat.getId());
            subkelompokList.addAll(subs);

            for (Subkelompok s : subs) {
                int count = kategoriService.getJumlahAnggotaSubkelompok(s.getId());
                String statusKapasitas;
                if (count > kapasitasAnjuran) {
                    statusKapasitas = "OVER:" + count + "/" + kapasitasAnjuran + " orang (Melebihi Kapasitas Anjuran)";
                } else if (count == 0) {
                    statusKapasitas = "EMPTY:Belum ada anggota";
                } else {
                    statusKapasitas = "OK:" + count + "/" + kapasitasAnjuran + " orang (Optimal)";
                }

                modelSubkelompok.addRow(new Object[]{
                        s.getNama(),
                        count + " orang",
                        statusKapasitas
                });
            }
        } catch (Exception ex) {
            System.err.println("Gagal memuat subkelompok: " + ex.getMessage());
        }

        // 2. Muat Anggota di Kategori Ini
        modelAnggotaKategori.setRowCount(0);
        anggotaKategoriList.clear();
        try {
            List<Anggota> anggotaData = kategoriService.getAnggotaByKategori(kat.getId(), false);
            anggotaKategoriList.addAll(anggotaData);

            for (Anggota a : anggotaData) {
                modelAnggotaKategori.addRow(new Object[]{
                        "#" + a.getId(),
                        a.getNamaLengkap(),
                        a.getStatus().getLabel(),
                        a.getNamaSubkelompok()
                });
            }
        } catch (Exception ex) {
            System.err.println("Gagal memuat anggota kategori: " + ex.getMessage());
        }
    }

    private void openAddKategoriDialog() {
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        KategoriFormDialog dialog = new KategoriFormDialog(parent, kategoriService, null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            loadKategoriList();
        }
    }

    private void openEditKategoriDialog() {
        if (selectedKategori == null) return;
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        KategoriFormDialog dialog = new KategoriFormDialog(parent, kategoriService, selectedKategori);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            loadKategoriList();
        }
    }

    private void deleteKategori() {
        if (selectedKategori == null) return;

        int confirm = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin menghapus kategori '" + selectedKategori.getNama() + "'?\n" +
                "Seluruh subkelompok di dalamnya akan dihapus, dan anggota pada kategori ini akan diset tanpa kategori (tidak dihapus).",
                "Konfirmasi Hapus Kategori",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                kategoriService.hapusKategori(selectedKategori.getId());
                JOptionPane.showMessageDialog(this, "Kategori berhasil dihapus.", "Berhasil", JOptionPane.INFORMATION_MESSAGE);
                loadKategoriList();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal menghapus: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void openAddSubkelompokDialog() {
        if (selectedKategori == null) return;
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        SubkelompokFormDialog dialog = new SubkelompokFormDialog(parent, kategoriService, selectedKategori, null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            loadRightDetail(selectedKategori);
        }
    }

    private void openEditSubkelompokDialog() {
        int row = tblSubkelompok.getSelectedRow();
        if (row < 0 || row >= subkelompokList.size()) return;
        Subkelompok selectedSub = subkelompokList.get(row);

        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        SubkelompokFormDialog dialog = new SubkelompokFormDialog(parent, kategoriService, selectedKategori, selectedSub);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            loadRightDetail(selectedKategori);
        }
    }

    private void deleteSubkelompok() {
        int row = tblSubkelompok.getSelectedRow();
        if (row < 0 || row >= subkelompokList.size()) return;
        Subkelompok selectedSub = subkelompokList.get(row);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Hapus subkelompok '" + selectedSub.getNama() + "'?\nAnggota di dalam subkelompok ini akan menjadi tanpa subkelompok.",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                kategoriService.hapusSubkelompok(selectedSub.getId());
                loadRightDetail(selectedKategori);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal menghapus: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void openPindahSubDialog() {
        int row = tblAnggotaKategori.getSelectedRow();
        if (row < 0 || row >= anggotaKategoriList.size()) return;
        Anggota a = anggotaKategoriList.get(row);

        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        PindahSubkelompokDialog dialog = new PindahSubkelompokDialog(parent, kategoriService, a, selectedKategori);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            loadRightDetail(selectedKategori);
        }
    }

    private void runAutoGrouping() {
        if (selectedKategori == null) return;

        try {
            boolean adaYangSudahPunya = kategoriService.apakahAdaAnggotaSudahPunyaSubkelompok(selectedKategori.getId());

            if (adaYangSudahPunya) {
                int res = JOptionPane.showConfirmDialog(this,
                        "PERINGATAN:\nSebagian anggota aktif di kategori '" + selectedKategori.getNama() + "' sudah memiliki subkelompok.\n\n" +
                        "Menjalankan fitur Auto-Grouping akan MENIMPA penempatan lama dan membagi seluruh anggota aktif secara merata ke subkelompok yang tersedia.\n\n" +
                        "Apakah Anda yakin ingin melanjutkan?",
                        "Konfirmasi Penimpaan Auto-Grouping",
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                if (res != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            int count = kategoriService.jalankanAutoGrouping(selectedKategori.getId());
            JOptionPane.showMessageDialog(this,
                    "Berhasil menjalankan Auto-Grouping!\nSebanyak " + count + " anggota aktif telah dibagi secara merata ke subkelompok yang tersedia.",
                    "Auto-Grouping Selesai", JOptionPane.INFORMATION_MESSAGE);

            loadRightDetail(selectedKategori);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Gagal Auto-Grouping", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Renderer badge khusus untuk Status Kapasitas Subkelompok.
     */
    private static class KapasitasBadgeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (value instanceof String str) {
                if (str.startsWith("OVER:")) {
                    lbl.setText("  " + str.substring(5) + "  ");
                    lbl.setBackground(new Color(254, 242, 242)); // Soft red
                    lbl.setForeground(new Color(220, 38, 38));
                    lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
                } else if (str.startsWith("OK:")) {
                    lbl.setText("  " + str.substring(3) + "  ");
                    lbl.setBackground(new Color(240, 253, 244)); // Soft green
                    lbl.setForeground(new Color(22, 163, 74));
                    lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                } else if (str.startsWith("EMPTY:")) {
                    lbl.setText("  " + str.substring(6) + "  ");
                    lbl.setBackground(new Color(241, 245, 249)); // Neutral
                    lbl.setForeground(new Color(148, 163, 184));
                    lbl.setFont(new Font("Segoe UI", Font.ITALIC, 11));
                }
                lbl.setOpaque(true);
            }
            return lbl;
        }
    }
}
