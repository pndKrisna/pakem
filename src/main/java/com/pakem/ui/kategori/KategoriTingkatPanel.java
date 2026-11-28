package com.pakem.ui.kategori;

import com.formdev.flatlaf.FlatClientProperties;
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
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel utama untuk Modul Kategori Tingkat, Subkelompok, dan Auto-Grouping.
 */
public class KategoriTingkatPanel extends JPanel {

    private static final Color GRID_LINE = new Color(241, 245, 249);
    private static final Color TEXT_PRIMARY = new Color(15, 23, 42);
    private static final Color TEXT_SECONDARY = new Color(100, 116, 139);
    private static final Color ACCENT_BLUE = new Color(37, 99, 235);
    private static final Color SELECTION_BG = new Color(239, 246, 255);
    private static final Color SELECTION_FG = new Color(29, 78, 216);
    private static final Color BG_CANVAS = new Color(248, 250, 252);
    private static final Color BG_GAP = new Color(241, 245, 249);
    private static final Color BORDER_SUBTLE = new Color(226, 232, 240);

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

    private JLabel lblFooterSummary;

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
        setBackground(Color.WHITE);

        // 1. Top Bar Header (Seragam dengan Jadwal Latihan & Data Anggota)
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_SUBTLE),
                BorderFactory.createEmptyBorder(16, 24, 16, 24)
        ));

        JPanel titleTextPanel = new JPanel();
        titleTextPanel.setLayout(new BoxLayout(titleTextPanel, BoxLayout.Y_AXIS));
        titleTextPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Master Kategori Tingkat & Subkelompok");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(TEXT_PRIMARY);

        JLabel lblSubtitle = new JLabel("Kelola jenjang kemampuan tabuh, subkelompok latihan, serta pembagian merata anggota (Auto-Grouping).");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitle.setForeground(TEXT_SECONDARY);

        titleTextPanel.add(lblTitle);
        titleTextPanel.add(Box.createVerticalStrut(2));
        titleTextPanel.add(lblSubtitle);

        topBar.add(titleTextPanel, BorderLayout.WEST);

        // Auto-Grouping & Banner Pelatih (Top Right)
        if (currentUser.canManageKategoriTingkat()) {
            btnAutoGrouping = createPrimaryButton("Auto-Grouping (Bagi Rata)");
            btnAutoGrouping.addActionListener(e -> runAutoGrouping());

            JPanel autoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            autoPanel.setOpaque(false);
            autoPanel.add(btnAutoGrouping);
            topBar.add(autoPanel, BorderLayout.EAST);
        } else {
            JPanel bannerPelatih = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            bannerPelatih.setOpaque(false);
            JLabel lblPelatih = new JLabel("  Mode Pelatih: Hanya Melihat Struktur  ");
            lblPelatih.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lblPelatih.setOpaque(true);
            lblPelatih.setBackground(new Color(254, 243, 199));
            lblPelatih.setForeground(new Color(180, 83, 9));
            lblPelatih.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(252, 211, 77), 1, true),
                    BorderFactory.createEmptyBorder(4, 8, 4, 8)
            ));
            bannerPelatih.add(lblPelatih);
            topBar.add(bannerPelatih, BorderLayout.EAST);
        }

        add(topBar, BorderLayout.NORTH);

        // 2. Split Pane (Left: Kategori, Right: Subkelompok & Anggota)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(380);
        splitPane.setDividerSize(12);
        splitPane.setBorder(null);
        splitPane.setBackground(BG_GAP);
        splitPane.setOneTouchExpandable(false);

        JPanel leftPanel = createLeftPanel();
        splitPane.setLeftComponent(leftPanel);

        rightCardLayout = new CardLayout();
        rightContainer = new JPanel(rightCardLayout);
        rightContainer.setBackground(Color.WHITE);
        rightContainer.setBorder(BorderFactory.createLineBorder(BORDER_SUBTLE, 1));

        // Empty state view
        JPanel emptyPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 160));
        emptyPanel.setBackground(Color.WHITE);
        JLabel lblEmpty = new JLabel("Pilih salah satu kategori tingkat di sebelah kiri untuk melihat rincian subkelompok.");
        lblEmpty.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblEmpty.setForeground(new Color(148, 163, 184));
        emptyPanel.add(lblEmpty);
        rightContainer.add(emptyPanel, CARD_EMPTY);

        // Detail state view
        JPanel detailPanel = createRightDetailPanel();
        rightContainer.add(detailPanel, CARD_DETAIL);

        splitPane.setRightComponent(rightContainer);

        // 3. Footer Ringkasan
        JPanel footerBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 10));
        footerBar.setBackground(Color.WHITE);
        footerBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_SUBTLE));

        lblFooterSummary = new JLabel("Total Kategori: 0  |  Total Subkelompok: 0");
        lblFooterSummary.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblFooterSummary.setForeground(new Color(30, 41, 59));
        footerBar.add(lblFooterSummary);

        JPanel centerGapWrapper = new JPanel(new BorderLayout());
        centerGapWrapper.setBackground(BG_GAP);
        centerGapWrapper.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));
        centerGapWrapper.add(splitPane, BorderLayout.CENTER);

        JPanel mainWrapper = new JPanel(new BorderLayout());
        mainWrapper.setBackground(BG_GAP);
        mainWrapper.add(centerGapWrapper, BorderLayout.CENTER);
        mainWrapper.add(footerBar, BorderLayout.SOUTH);
        add(mainWrapper, BorderLayout.CENTER);

        rightCardLayout.show(rightContainer, CARD_EMPTY);
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(BORDER_SUBTLE, 1));

        // Top section: Header judul + baris toolbar terpisah agar rapi dan sejajar
        JPanel leftTopWrapper = new JPanel();
        leftTopWrapper.setLayout(new BoxLayout(leftTopWrapper, BoxLayout.Y_AXIS));
        leftTopWrapper.setBackground(Color.WHITE);

        JPanel headerLeft = new JPanel(new BorderLayout());
        headerLeft.setBackground(Color.WHITE);
        headerLeft.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_SUBTLE),
                BorderFactory.createEmptyBorder(14, 20, 14, 20)
        ));

        JPanel leftTitleBox = new JPanel();
        leftTitleBox.setLayout(new BoxLayout(leftTitleBox, BoxLayout.Y_AXIS));
        leftTitleBox.setOpaque(false);

        JLabel lblLeftTitle = new JLabel("Daftar Jenjang / Kategori");
        lblLeftTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblLeftTitle.setForeground(TEXT_PRIMARY);

        JLabel lblLeftSub = new JLabel("Pilih jenjang untuk mengatur subkelompok");
        lblLeftSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblLeftSub.setForeground(TEXT_SECONDARY);

        leftTitleBox.add(lblLeftTitle);
        leftTitleBox.add(Box.createVerticalStrut(2));
        leftTitleBox.add(lblLeftSub);
        headerLeft.add(leftTitleBox, BorderLayout.WEST);
        leftTopWrapper.add(headerLeft);

        if (currentUser.canManageKategoriTingkat()) {
            JPanel leftToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
            leftToolbar.setBackground(BG_CANVAS);
            leftToolbar.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_SUBTLE),
                    BorderFactory.createEmptyBorder(2, 12, 2, 12)
            ));

            btnTambahKategori = createPrimaryButton("+ Tambah");
            btnTambahKategori.addActionListener(e -> openAddKategoriDialog());

            btnUbahKategori = createSecondaryButton("Ubah");
            btnUbahKategori.setEnabled(false);
            btnUbahKategori.addActionListener(e -> openEditKategoriDialog());

            btnHapusKategori = createDangerButton("Hapus");
            btnHapusKategori.setEnabled(false);
            btnHapusKategori.addActionListener(e -> deleteKategori());

            leftToolbar.add(btnTambahKategori);
            leftToolbar.add(btnUbahKategori);
            leftToolbar.add(btnHapusKategori);
            leftTopWrapper.add(leftToolbar);
        }

        panel.add(leftTopWrapper, BorderLayout.NORTH);

        // Table Kategori
        String[] columns = {"Urut", "Nama Kategori", "Anggota"};
        modelKategori = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tblKategori = new JTable(modelKategori);
        styleTable(tblKategori);

        tblKategori.getColumnModel().getColumn(0).setPreferredWidth(55);
        tblKategori.getColumnModel().getColumn(0).setMaxWidth(65);
        tblKategori.getColumnModel().getColumn(1).setPreferredWidth(210);
        tblKategori.getColumnModel().getColumn(2).setPreferredWidth(75);
        tblKategori.getColumnModel().getColumn(2).setMaxWidth(85);

        DefaultTableCellRenderer centerRenderer = createPaddedRenderer(SwingConstants.CENTER);
        tblKategori.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tblKategori.getColumnModel().getColumn(1).setCellRenderer(createPaddedRenderer(SwingConstants.LEFT));
        tblKategori.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);

        tblKategori.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            boolean hasSelection = tblKategori.getSelectedRow() != -1;
            if (btnUbahKategori != null) {
                btnUbahKategori.setEnabled(hasSelection && currentUser.canManageKategoriTingkat());
            }
            if (btnHapusKategori != null) {
                btnHapusKategori.setEnabled(hasSelection && currentUser.canManageKategoriTingkat());
            }
            if (hasSelection) {
                int row = tblKategori.getSelectedRow();
                if (row >= 0 && row < kategoriList.size()) {
                    selectedKategori = kategoriList.get(row);
                    loadRightDetail(selectedKategori);
                }
            }
        });

        JScrollPane scrollTable = new JScrollPane(tblKategori);
        scrollTable.setBorder(BorderFactory.createEmptyBorder());
        scrollTable.getViewport().setBackground(Color.WHITE);
        panel.add(scrollTable, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createRightDetailPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // Header Kategori Terpilih (Tinggi selaras dengan headerLeft)
        JPanel headerDetail = new JPanel(new BorderLayout());
        headerDetail.setBackground(Color.WHITE);
        headerDetail.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_SUBTLE),
                BorderFactory.createEmptyBorder(14, 24, 14, 24)
        ));

        JPanel titleSubPanel = new JPanel();
        titleSubPanel.setLayout(new BoxLayout(titleSubPanel, BoxLayout.Y_AXIS));
        titleSubPanel.setOpaque(false);

        lblSelectedKategoriTitle = new JLabel("Kategori: -");
        lblSelectedKategoriTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblSelectedKategoriTitle.setForeground(TEXT_PRIMARY);

        lblKapasitasAnjuran = new JLabel("Kapasitas Anjuran: 25 orang / kelompok");
        lblKapasitasAnjuran.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblKapasitasAnjuran.setForeground(TEXT_SECONDARY);

        titleSubPanel.add(lblSelectedKategoriTitle);
        titleSubPanel.add(Box.createVerticalStrut(2));
        titleSubPanel.add(lblKapasitasAnjuran);

        headerDetail.add(titleSubPanel, BorderLayout.WEST);
        panel.add(headerDetail, BorderLayout.NORTH);

        // Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabbedPane.setBackground(Color.WHITE);
        tabbedPane.putClientProperty(FlatClientProperties.TABBED_PANE_TAB_AREA_INSETS, new java.awt.Insets(0, 16, 0, 16));

        tabbedPane.addTab("Daftar Subkelompok", createTabSubkelompok());
        tabbedPane.addTab("Penempatan Anggota Kategori", createTabAnggotaKategori());

        panel.add(tabbedPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createTabSubkelompok() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        if (currentUser.canManageKategoriTingkat()) {
            JPanel tbSub = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
            tbSub.setBackground(BG_CANVAS);
            tbSub.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_SUBTLE),
                    BorderFactory.createEmptyBorder(2, 16, 2, 16)
            ));

            btnTambahSub = createPrimaryButton("+ Tambah Subkelompok");
            btnTambahSub.addActionListener(e -> openAddSubkelompokDialog());

            btnUbahSub = createSecondaryButton("Ubah Nama");
            btnUbahSub.setEnabled(false);
            btnUbahSub.addActionListener(e -> openEditSubkelompokDialog());

            btnHapusSub = createDangerButton("Hapus Subkelompok");
            btnHapusSub.setEnabled(false);
            btnHapusSub.addActionListener(e -> deleteSubkelompok());

            tbSub.add(btnTambahSub);
            tbSub.add(btnUbahSub);
            tbSub.add(btnHapusSub);

            panel.add(tbSub, BorderLayout.NORTH);
        }

        // Table Subkelompok
        String[] cols = {"Nama Subkelompok", "Jumlah Anggota", "Status Kapasitas (Anjuran: 25)"};
        modelSubkelompok = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tblSubkelompok = new JTable(modelSubkelompok);
        styleTable(tblSubkelompok);

        tblSubkelompok.getColumnModel().getColumn(0).setPreferredWidth(200);
        tblSubkelompok.getColumnModel().getColumn(1).setPreferredWidth(140);
        tblSubkelompok.getColumnModel().getColumn(2).setPreferredWidth(280);

        tblSubkelompok.getColumnModel().getColumn(0).setCellRenderer(createPaddedRenderer(SwingConstants.LEFT));
        tblSubkelompok.getColumnModel().getColumn(1).setCellRenderer(createPaddedRenderer(SwingConstants.CENTER));
        tblSubkelompok.getColumnModel().getColumn(2).setCellRenderer(new KapasitasPillRenderer());

        tblSubkelompok.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            boolean has = tblSubkelompok.getSelectedRow() != -1;
            if (btnUbahSub != null) btnUbahSub.setEnabled(has && currentUser.canManageKategoriTingkat());
            if (btnHapusSub != null) btnHapusSub.setEnabled(has && currentUser.canManageKategoriTingkat());
        });

        JScrollPane scroll = new JScrollPane(tblSubkelompok);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createTabAnggotaKategori() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JPanel tbAnggota = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        tbAnggota.setBackground(BG_CANVAS);
        tbAnggota.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_SUBTLE),
                BorderFactory.createEmptyBorder(2, 16, 2, 16)
        ));

        btnPindahSub = createPrimaryButton("Pindahkan Subkelompok...");
        btnPindahSub.setEnabled(false);
        btnPindahSub.setVisible(currentUser.canManageKategoriTingkat());
        btnPindahSub.addActionListener(e -> openPindahSubDialog());

        JButton btnRefreshAnggota = createSecondaryButton("Refresh Daftar");
        btnRefreshAnggota.addActionListener(e -> {
            if (selectedKategori != null) loadRightDetail(selectedKategori);
        });

        if (currentUser.canManageKategoriTingkat()) {
            tbAnggota.add(btnPindahSub);
        }
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
        styleTable(tblAnggotaKategori);

        tblAnggotaKategori.getColumnModel().getColumn(0).setPreferredWidth(65);
        tblAnggotaKategori.getColumnModel().getColumn(0).setMaxWidth(80);
        tblAnggotaKategori.getColumnModel().getColumn(1).setPreferredWidth(240);
        tblAnggotaKategori.getColumnModel().getColumn(2).setPreferredWidth(120);
        tblAnggotaKategori.getColumnModel().getColumn(3).setPreferredWidth(180);

        tblAnggotaKategori.getColumnModel().getColumn(0).setCellRenderer(createPaddedRenderer(SwingConstants.CENTER));
        tblAnggotaKategori.getColumnModel().getColumn(1).setCellRenderer(createPaddedRenderer(SwingConstants.LEFT));
        tblAnggotaKategori.getColumnModel().getColumn(2).setCellRenderer(new StatusAnggotaPillRenderer());
        tblAnggotaKategori.getColumnModel().getColumn(3).setCellRenderer(createPaddedRenderer(SwingConstants.LEFT));

        tblAnggotaKategori.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            boolean has = tblAnggotaKategori.getSelectedRow() != -1;
            if (btnPindahSub != null) btnPindahSub.setEnabled(has && currentUser.canManageKategoriTingkat());
        });

        JScrollPane scroll = new JScrollPane(tblAnggotaKategori);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);
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
                selectedKategori = null;
                rightCardLayout.show(rightContainer, CARD_EMPTY);
                updateFooterSummary();
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat kategori: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadRightDetail(KategoriTingkat kat) {
        if (kat == null) {
            rightCardLayout.show(rightContainer, CARD_EMPTY);
            updateFooterSummary();
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
                    statusKapasitas = "OVER:" + count + "/" + kapasitasAnjuran + " orang (Melebihi Kapasitas)";
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

        updateFooterSummary();

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

    private void updateFooterSummary() {
        if (lblFooterSummary != null) {
            lblFooterSummary.setText("Total Kategori: " + kategoriList.size() + "  |  Total Subkelompok: " + subkelompokList.size());
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

    // --- UI Helpers ---

    private JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(ACCENT_BLUE);
        btn.setForeground(Color.WHITE);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        btn.putClientProperty(FlatClientProperties.STYLE, "arc: 8; margin: 5,14,5,14");
        return btn;
    }

    private JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setBackground(Color.WHITE);
        btn.setForeground(new Color(51, 65, 85));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        btn.putClientProperty(FlatClientProperties.STYLE, "arc: 8; borderColor: #CBD5E1; margin: 5,12,5,12");
        return btn;
    }

    private JButton createDangerButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setBackground(Color.WHITE);
        btn.setForeground(new Color(220, 38, 38));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        btn.putClientProperty(FlatClientProperties.STYLE, "arc: 8; borderColor: #FECACA; margin: 5,12,5,12");
        return btn;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setForeground(TEXT_PRIMARY);
        table.setBackground(Color.WHITE);
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(SELECTION_BG);
        table.setSelectionForeground(SELECTION_FG);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setGridColor(GRID_LINE);
        table.setIntercellSpacing(new Dimension(0, 1));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(BG_CANVAS);
        header.setForeground(new Color(71, 85, 105));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 36));
        header.setReorderingAllowed(false);
    }

    private DefaultTableCellRenderer createPaddedRenderer(int alignment) {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(alignment);
                lbl.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                if (isSelected) {
                    lbl.setBackground(SELECTION_BG);
                    lbl.setForeground(SELECTION_FG);
                    lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));
                } else {
                    lbl.setBackground(Color.WHITE);
                    lbl.setForeground(TEXT_PRIMARY);
                }
                return lbl;
            }
        };
    }

    private static class PillCellPanel extends JPanel {
        private final JLabel pillLabel = new JLabel();
        private Color pillBg = new Color(241, 245, 249);

        PillCellPanel() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 0, 8));
            setOpaque(true);
            pillLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
            pillLabel.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
            pillLabel.setOpaque(false);
            add(pillLabel);
        }

        void configure(String text, Color bg, Color fg, boolean isSelected) {
            pillLabel.setText(text);
            pillLabel.setForeground(fg);
            this.pillBg = bg;
            setBackground(isSelected ? SELECTION_BG : Color.WHITE);
        }

        @Override
        protected void paintChildren(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(pillBg);
            g2.fillRoundRect(pillLabel.getX(), pillLabel.getY(), pillLabel.getWidth(), pillLabel.getHeight(), 14, 14);
            g2.dispose();
            super.paintChildren(g);
        }
    }

    private static class KapasitasPillRenderer extends DefaultTableCellRenderer {
        private final PillCellPanel cell = new PillCellPanel();

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            String str = value != null ? value.toString() : "";
            if (str.startsWith("OVER:")) {
                cell.configure(str.substring(5), new Color(254, 226, 226), new Color(185, 28, 28), isSelected);
            } else if (str.startsWith("OK:")) {
                cell.configure(str.substring(3), new Color(220, 252, 231), new Color(22, 101, 52), isSelected);
            } else if (str.startsWith("EMPTY:")) {
                cell.configure(str.substring(6), new Color(241, 245, 249), new Color(100, 116, 139), isSelected);
            } else {
                cell.configure(str, new Color(241, 245, 249), new Color(71, 85, 105), isSelected);
            }
            return cell;
        }
    }

    private static class StatusAnggotaPillRenderer extends DefaultTableCellRenderer {
        private final PillCellPanel cell = new PillCellPanel();

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            String status = value != null ? value.toString() : "-";
            switch (status.toLowerCase()) {
                case "aktif" -> cell.configure(status, new Color(220, 252, 231), new Color(22, 101, 52), isSelected);
                case "vakum" -> cell.configure(status, new Color(254, 243, 199), new Color(146, 64, 14), isSelected);
                default -> cell.configure(status, new Color(254, 226, 226), new Color(185, 28, 28), isSelected);
            }
            return cell;
        }
    }
}