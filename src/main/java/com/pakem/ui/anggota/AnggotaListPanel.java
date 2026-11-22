package com.pakem.ui.anggota;

import com.pakem.model.Anggota;
import com.pakem.model.User;
import com.pakem.model.enums.StatusAnggota;
import com.pakem.service.interfaces.IAnggotaService;

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
import javax.swing.JTextField;
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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Panel utama untuk Manajemen Data Anggota Sanggar Seni Tabuh.
 */
public class AnggotaListPanel extends JPanel {

    private final IAnggotaService anggotaService;
    private final User currentUser;

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JComboBox<String> cmbFilterStatus;

    private JButton btnTambah;
    private JButton btnUbah;
    private JButton btnRiwayat;
    private JButton btnUbahStatus;

    private JLabel lblSummaryTotal;
    private JLabel lblSummaryAktif;
    private JLabel lblSummaryVakum;
    private JLabel lblSummaryNonaktif;

    private final List<Anggota> anggotaList = new ArrayList<>();

    public AnggotaListPanel(IAnggotaService anggotaService, User currentUser) {
        this.anggotaService = anggotaService;
        this.currentUser = currentUser;

        initUI();
        loadTableData();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(241, 245, 249));

        // 1. Top Panel (Header & Toolbar)
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(16, 24, 16, 24)
        ));

        // Title Row
        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);

        JLabel lblTitle = new JLabel("Manajemen Data Anggota");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(15, 23, 42));

        JLabel lblSubtitle = new JLabel("Kelola data anggota, kategori tingkat, subkelompok, dan status keaktifan sanggar.");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitle.setForeground(new Color(100, 116, 139));

        JPanel titleTextPanel = new JPanel();
        titleTextPanel.setLayout(new BoxLayout(titleTextPanel, BoxLayout.Y_AXIS));
        titleTextPanel.setOpaque(false);
        titleTextPanel.add(lblTitle);
        titleTextPanel.add(Box.createVerticalStrut(2));
        titleTextPanel.add(lblSubtitle);

        titleRow.add(titleTextPanel, BorderLayout.WEST);

        // Action Buttons Row (Right)
        JPanel actionButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actionButtonPanel.setOpaque(false);

        btnTambah = new JButton("+ Tambah Anggota");
        btnTambah.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnTambah.setBackground(new Color(37, 99, 235));
        btnTambah.setForeground(Color.WHITE);
        btnTambah.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTambah.setFocusPainted(false);
        btnTambah.setVisible(currentUser.canEditAnggota());
        btnTambah.addActionListener(e -> openAddDialog());

        btnUbah = new JButton("Ubah Data");
        btnUbah.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnUbah.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnUbah.setFocusPainted(false);
        btnUbah.setEnabled(false);
        btnUbah.addActionListener(e -> openEditDialog());

        btnRiwayat = new JButton("Riwayat Tingkat");
        btnRiwayat.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnRiwayat.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRiwayat.setFocusPainted(false);
        btnRiwayat.setEnabled(false);
        btnRiwayat.addActionListener(e -> openRiwayatDialog());

        btnUbahStatus = new JButton("Ubah Status...");
        btnUbahStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnUbahStatus.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnUbahStatus.setFocusPainted(false);
        btnUbahStatus.setEnabled(false);
        btnUbahStatus.setVisible(currentUser.canEditAnggota());
        btnUbahStatus.addActionListener(e -> openUbahStatusDialog());

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh.setFocusPainted(false);
        btnRefresh.addActionListener(e -> loadTableData());

        actionButtonPanel.add(btnTambah);
        actionButtonPanel.add(btnUbah);
        actionButtonPanel.add(btnRiwayat);
        actionButtonPanel.add(btnUbahStatus);
        actionButtonPanel.add(btnRefresh);

        titleRow.add(actionButtonPanel, BorderLayout.EAST);
        topPanel.add(titleRow);
        topPanel.add(Box.createVerticalStrut(14));

        // Filter & Search Row
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterRow.setOpaque(false);

        JLabel lblFilter = new JLabel("Filter Status:");
        lblFilter.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblFilter.setForeground(new Color(71, 85, 105));

        cmbFilterStatus = new JComboBox<>(new String[]{
                "Semua Status",
                "Aktif Saja",
                "Vakum (Cuti) Saja",
                "Nonaktif (Keluar) Saja"
        });
        cmbFilterStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmbFilterStatus.addActionListener(e -> loadTableData());

        JLabel lblCari = new JLabel("Pencarian:");
        lblCari.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblCari.setForeground(new Color(71, 85, 105));

        txtSearch = new JTextField();
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtSearch.setPreferredSize(new Dimension(240, 32));
        txtSearch.putClientProperty("JTextField.placeholderText", "Ketik nama, kontak, catatan...");
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    loadTableData();
                }
            }
        });

        JButton btnCari = new JButton("Cari");
        btnCari.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnCari.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCari.addActionListener(e -> loadTableData());

        filterRow.add(lblFilter);
        filterRow.add(cmbFilterStatus);
        filterRow.add(Box.createHorizontalStrut(15));
        filterRow.add(lblCari);
        filterRow.add(txtSearch);
        filterRow.add(btnCari);

        topPanel.add(filterRow);
        add(topPanel, BorderLayout.NORTH);

        // 2. Table Panel
        String[] columnHeaders = {
                "ID", "Nama Lengkap", "Nomor Kontak", "Tgl Bergabung",
                "Status Keanggotaan", "Kategori Tingkat", "Subkelompok", "Catatan"
        };

        tableModel = new DefaultTableModel(columnHeaders, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(36);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(248, 250, 252));
        table.getTableHeader().setForeground(new Color(71, 85, 105));
        table.setShowGrid(true);
        table.setGridColor(new Color(241, 245, 249));

        // Column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(180);
        table.getColumnModel().getColumn(2).setPreferredWidth(120);
        table.getColumnModel().getColumn(3).setPreferredWidth(110);
        table.getColumnModel().getColumn(4).setPreferredWidth(130);
        table.getColumnModel().getColumn(5).setPreferredWidth(120);
        table.getColumnModel().getColumn(6).setPreferredWidth(120);
        table.getColumnModel().getColumn(7).setPreferredWidth(180);

        // Custom Cell Renderer for Status Pill/Badge
        table.getColumnModel().getColumn(4).setCellRenderer(new StatusBadgeRenderer());

        // Center renderers for ID and Date
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

        table.getSelectionModel().addListSelectionListener(e -> {
            boolean hasSelection = table.getSelectedRow() != -1;
            btnUbah.setEnabled(hasSelection);
            btnRiwayat.setEnabled(hasSelection);
            btnUbahStatus.setEnabled(hasSelection && currentUser.canEditAnggota());
        });

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    openEditDialog();
                }
            }
        });

        JScrollPane scrollTable = new JScrollPane(table);
        scrollTable.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        add(scrollTable, BorderLayout.CENTER);

        // 3. Bottom Summary Bar
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 10));
        bottomBar.setBackground(Color.WHITE);
        bottomBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));

        lblSummaryTotal = new JLabel("Total Anggota: 0");
        lblSummaryTotal.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSummaryTotal.setForeground(new Color(30, 41, 59));

        lblSummaryAktif = new JLabel("● Aktif: 0");
        lblSummaryAktif.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSummaryAktif.setForeground(new Color(22, 163, 74)); // Green

        lblSummaryVakum = new JLabel("● Vakum: 0");
        lblSummaryVakum.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSummaryVakum.setForeground(new Color(202, 138, 4)); // Yellow/Amber

        lblSummaryNonaktif = new JLabel("● Nonaktif: 0");
        lblSummaryNonaktif.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSummaryNonaktif.setForeground(new Color(100, 116, 139)); // Slate

        bottomBar.add(lblSummaryTotal);
        bottomBar.add(new JLabel("|"));
        bottomBar.add(lblSummaryAktif);
        bottomBar.add(lblSummaryVakum);
        bottomBar.add(lblSummaryNonaktif);

        add(bottomBar, BorderLayout.SOUTH);
    }

    public void loadTableData() {
        tableModel.setRowCount(0);
        anggotaList.clear();

        StatusAnggota filter = null;
        int filterIdx = cmbFilterStatus.getSelectedIndex();
        if (filterIdx == 1) filter = StatusAnggota.AKTIF;
        else if (filterIdx == 2) filter = StatusAnggota.VAKUM;
        else if (filterIdx == 3) filter = StatusAnggota.NONAKTIF;

        String keyword = txtSearch.getText().trim();

        try {
            List<Anggota> data = anggotaService.getDaftarAnggota(filter, keyword);
            anggotaList.addAll(data);

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            for (Anggota a : data) {
                String tgl = a.getTanggalBergabung() != null ? a.getTanggalBergabung().format(dtf) : "-";
                tableModel.addRow(new Object[]{
                        "#" + a.getId(),
                        a.getNamaLengkap(),
                        (a.getNomorKontak() != null && !a.getNomorKontak().isEmpty()) ? a.getNomorKontak() : "-",
                        tgl,
                        a.getStatus(),
                        a.getNamaKategori(),
                        a.getNamaSubkelompok(),
                        (a.getCatatan() != null && !a.getCatatan().isEmpty()) ? a.getCatatan() : "-"
                });
            }

            // Update Summary Bar
            Map<String, Integer> summary = anggotaService.getRingkasanJumlahAnggota();
            lblSummaryTotal.setText("Total Anggota: " + summary.getOrDefault("TOTAL", 0));
            lblSummaryAktif.setText("● Aktif: " + summary.getOrDefault("AKTIF", 0));
            lblSummaryVakum.setText("● Vakum: " + summary.getOrDefault("VAKUM", 0));
            lblSummaryNonaktif.setText("● Nonaktif: " + summary.getOrDefault("NONAKTIF", 0));

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data anggota: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Anggota getSelectedAnggota() {
        int row = table.getSelectedRow();
        if (row >= 0 && row < anggotaList.size()) {
            return anggotaList.get(row);
        }
        return null;
    }

    private void openAddDialog() {
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        AnggotaFormDialog dialog = new AnggotaFormDialog(parent, anggotaService, currentUser, null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            loadTableData();
        }
    }

    private void openEditDialog() {
        Anggota selected = getSelectedAnggota();
        if (selected == null) return;

        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        AnggotaFormDialog dialog = new AnggotaFormDialog(parent, anggotaService, currentUser, selected);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            loadTableData();
        }
    }

    private void openRiwayatDialog() {
        Anggota selected = getSelectedAnggota();
        if (selected == null) return;

        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        RiwayatTingkatDialog dialog = new RiwayatTingkatDialog(parent, anggotaService, selected);
        dialog.setVisible(true);
    }

    private void openUbahStatusDialog() {
        Anggota selected = getSelectedAnggota();
        if (selected == null) return;

        StatusAnggota[] options = StatusAnggota.values();
        StatusAnggota selectedOption = (StatusAnggota) JOptionPane.showInputDialog(
                this,
                "Pilih status baru untuk anggota: " + selected.getNamaLengkap() + "\nStatus saat ini: " + selected.getStatus().getLabel(),
                "Ubah Status Keanggotaan",
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                selected.getStatus()
        );

        if (selectedOption != null && selectedOption != selected.getStatus()) {
            try {
                anggotaService.ubahStatusAnggota(selected.getId(), selectedOption);
                JOptionPane.showMessageDialog(this,
                        "Status anggota berhasil diubah menjadi: " + selectedOption.getLabel(),
                        "Berhasil", JOptionPane.INFORMATION_MESSAGE);
                loadTableData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal mengubah status: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Renderer badge khusus untuk kolom Status Keanggotaan.
     */
    private static class StatusBadgeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            lbl.setHorizontalAlignment(CENTER);

            if (value instanceof StatusAnggota status) {
                lbl.setText("  " + status.getLabel() + "  ");
                lbl.setOpaque(true);

                if (!isSelected) {
                    switch (status) {
                        case AKTIF -> {
                            lbl.setBackground(new Color(220, 252, 231)); // Soft green
                            lbl.setForeground(new Color(21, 128, 61));
                        }
                        case VAKUM -> {
                            lbl.setBackground(new Color(254, 243, 199)); // Soft amber
                            lbl.setForeground(new Color(180, 83, 9));
                        }
                        case NONAKTIF -> {
                            lbl.setBackground(new Color(241, 245, 249)); // Soft slate
                            lbl.setForeground(new Color(100, 116, 139));
                        }
                    }
                }
            }
            return lbl;
        }
    }
}
