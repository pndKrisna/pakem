package com.pakem.ui.iuran;

import com.pakem.model.Iuran;
import com.pakem.model.SanggarSettings;
import com.pakem.model.User;
import com.pakem.model.enums.StatusBayar;
import com.pakem.model.enums.StatusIuranWarna;
import com.pakem.service.interfaces.IIuranService;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Panel untuk manajemen Iuran Bulanan.
 * Layout standar: Header + Table + Footer Actions.
 */
public class IuranPanel extends JPanel {

    private final IIuranService iuranService;
    private final User currentUser;

    private JComboBox<String> cbBulan;
    private JComboBox<Integer> cbTahun;
    private JTable tableIuran;
    private DefaultTableModel tableModel;
    private JLabel lblSummary;
    private List<Iuran> daftarIuran = new ArrayList<>();

    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.of("id", "ID"));
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public IuranPanel(IIuranService iuranService, User currentUser) {
        this.iuranService = iuranService;
        this.currentUser = currentUser;

        setLayout(new BorderLayout());
        setBackground(new Color(241, 245, 249));

        initComponents();
        loadData();
    }

    private void initComponents() {
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

        JLabel lblTitle = new JLabel("Iuran Bulanan");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(15, 23, 42));

        JLabel lblSubtitle = new JLabel("Pantau status pembayaran iuran bulanan anggota aktif per periode.");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitle.setForeground(new Color(100, 116, 139));

        JPanel titleTextPanel = new JPanel();
        titleTextPanel.setLayout(new BoxLayout(titleTextPanel, BoxLayout.Y_AXIS));
        titleTextPanel.setOpaque(false);
        titleTextPanel.add(lblTitle);
        titleTextPanel.add(Box.createVerticalStrut(2));
        titleTextPanel.add(lblSubtitle);

        titleRow.add(titleTextPanel, BorderLayout.WEST);

        // Filter Periode (Right)
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filterPanel.setOpaque(false);

        String[] bulanNames = {"Januari", "Februari", "Maret", "April", "Mei", "Juni",
                               "Juli", "Agustus", "September", "Oktober", "November", "Desember"};
        cbBulan = new JComboBox<>(bulanNames);
        cbBulan.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbBulan.setPreferredSize(new Dimension(140, 32));
        cbBulan.setSelectedIndex(LocalDate.now().getMonthValue() - 1);
        cbBulan.addActionListener(e -> loadData());

        int currentYear = LocalDate.now().getYear();
        Integer[] years = new Integer[5];
        for (int i = 0; i < 5; i++) {
            years[i] = currentYear - 2 + i;
        }
        cbTahun = new JComboBox<>(years);
        cbTahun.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbTahun.setPreferredSize(new Dimension(80, 32));
        cbTahun.setSelectedItem(currentYear);
        cbTahun.addActionListener(e -> loadData());

        filterPanel.add(new JLabel("Periode:"));
        filterPanel.add(cbBulan);
        filterPanel.add(cbTahun);

        titleRow.add(filterPanel, BorderLayout.EAST);
        topPanel.add(titleRow);
        topPanel.add(Box.createVerticalStrut(14));

        add(topPanel, BorderLayout.NORTH);

        // 2. Table Section
        String[] columns = {"ID", "Status", "Nama Anggota", "Kategori/Kelompok", "Status Bayar", "Nominal", "Tgl Bayar", "Aksi"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableIuran = new JTable(tableModel);
        tableIuran.setRowHeight(36);
        tableIuran.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tableIuran.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tableIuran.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tableIuran.getTableHeader().setBackground(new Color(248, 250, 252));
        tableIuran.getTableHeader().setForeground(new Color(71, 85, 105));
        tableIuran.setShowGrid(true);
        tableIuran.setGridColor(new Color(241, 245, 249));

        // Hide ID column
        tableIuran.removeColumn(tableIuran.getColumnModel().getColumn(0));

        // Column widths
        tableIuran.getColumnModel().getColumn(0).setPreferredWidth(90);   // Status pill
        tableIuran.getColumnModel().getColumn(1).setPreferredWidth(180);  // Nama
        tableIuran.getColumnModel().getColumn(2).setPreferredWidth(160);  // Kategori/Kelompok
        tableIuran.getColumnModel().getColumn(3).setPreferredWidth(110);  // Status Bayar
        tableIuran.getColumnModel().getColumn(4).setPreferredWidth(110);  // Nominal
        tableIuran.getColumnModel().getColumn(5).setPreferredWidth(100);  // Tgl Bayar
        tableIuran.getColumnModel().getColumn(6).setPreferredWidth(90);   // Aksi

        // Cell Renderers - Status pill for indicator
        tableIuran.getColumnModel().getColumn(0).setCellRenderer(new StatusPillRenderer());
        // Status badge for Status Bayar
        tableIuran.getColumnModel().getColumn(3).setCellRenderer(new StatusBayarRenderer());

        JScrollPane scrollPane = new JScrollPane(tableIuran);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        add(scrollPane, BorderLayout.CENTER);

        // 3. Bottom Summary & Actions
        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.setBackground(Color.WHITE);
        bottomBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(8, 24, 8, 24)
        ));

        lblSummary = new JLabel("Memuat data...");
        lblSummary.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSummary.setForeground(new Color(71, 85, 105));

        JPanel actionButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionButtons.setOpaque(false);

        JButton btnBayar = new JButton("Catat Pembayaran");
        btnBayar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnBayar.setBackground(new Color(37, 99, 235));
        btnBayar.setForeground(Color.WHITE);
        btnBayar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBayar.setFocusPainted(false);
        btnBayar.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btnBayar.addActionListener(e -> handleBayar());

        JButton btnBatal = new JButton("Batalkan Status");
        btnBatal.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnBatal.setForeground(new Color(220, 38, 38));
        btnBatal.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBatal.setFocusPainted(false);
        btnBatal.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(252, 165, 165), 1, true),
                BorderFactory.createEmptyBorder(8, 14, 8, 14)
        ));
        btnBatal.setBackground(Color.WHITE);
        btnBatal.addActionListener(e -> handleBatal());

        actionButtons.add(btnBatal);
        actionButtons.add(btnBayar);

        bottomBar.add(lblSummary, BorderLayout.WEST);
        bottomBar.add(actionButtons, BorderLayout.EAST);
        add(bottomBar, BorderLayout.SOUTH);
    }

    private void loadData() {
        int bulan = cbBulan.getSelectedIndex() + 1;
        int tahun = (int) cbTahun.getSelectedItem();

        try {
            daftarIuran = iuranService.getDaftarIuranBulan(bulan, tahun);
            tableModel.setRowCount(0);

            int lunasCount = 0;
            for (Iuran i : daftarIuran) {
                String subText = i.getKategoriTingkat();
                if (i.getSubkelompok() != null) {
                    subText += " (" + i.getSubkelompok() + ")";
                }

                tableModel.addRow(new Object[]{
                        i.getAnggotaId(),
                        i.getIndikatorWarna(),
                        i.getNamaAnggota(),
                        subText,
                        i.getStatusBayar(),
                        i.getNominal() != null ? currencyFormat.format(i.getNominal()) : "-",
                        i.getTanggalBayar() != null ? i.getTanggalBayar().format(dateFormatter) : "-",
                        i.getStatusBayar() == StatusBayar.LUNAS ? "Selesai" : "Tunggakan"
                });

                if (i.getStatusBayar() == StatusBayar.LUNAS) lunasCount++;
            }

            lblSummary.setText(String.format("Total: %d Anggota Aktif  |  Lunas: %d  |  Belum: %d",
                    daftarIuran.size(), lunasCount, daftarIuran.size() - lunasCount));

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data iuran: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void handleBayar() {
        int selectedRow = tableIuran.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Pilih anggota terlebih dahulu.");
            return;
        }

        Iuran i = daftarIuran.get(selectedRow);
        if (i.getStatusBayar() == StatusBayar.LUNAS) {
            JOptionPane.showMessageDialog(this, "Anggota ini sudah lunas untuk periode ini.");
            return;
        }

        try {
            SanggarSettings settings = iuranService.getPengaturanSanggar();
            String input = JOptionPane.showInputDialog(this,
                    "Nominal Pembayaran untuk " + i.getNamaAnggota() + ":",
                    settings.getIuranDefault().toPlainString());

            if (input == null || input.trim().isEmpty()) return;

            BigDecimal nominal = new BigDecimal(input.trim());
            iuranService.catatPembayaran(i.getAnggotaId(), i.getBulan(), i.getTahun(), nominal, LocalDate.now());

            JOptionPane.showMessageDialog(this, "Pembayaran berhasil dicatat.");
            loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal mencatat pembayaran: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleBatal() {
        int selectedRow = tableIuran.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Pilih anggota terlebih dahulu.");
            return;
        }

        Iuran i = daftarIuran.get(selectedRow);
        if (i.getStatusBayar() == StatusBayar.BELUM_BAYAR) {
            JOptionPane.showMessageDialog(this, "Anggota ini memang belum membayar.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Batalkan status lunas untuk " + i.getNamaAnggota() + "?",
                "Konfirmasi", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                iuranService.batalkanPembayaran(i.getAnggotaId(), i.getBulan(), i.getTahun());
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal membatalkan pembayaran: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // --- Inner Classes for Rendering ---

    /**
     * Renderer pill status indikator (lingkaran kecil berwarna + teks).
     */
    private static class StatusPillRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
            panel.setOpaque(false);
            if (isSelected) {
                panel.setBackground(table.getSelectionBackground());
            }

            JLabel dot = new JLabel("●");
            dot.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            JLabel text = new JLabel();
            text.setFont(new Font("Segoe UI", Font.PLAIN, 12));

            if (value instanceof StatusIuranWarna status) {
                Color fg = switch (status) {
                    case HIJAU -> new Color(22, 163, 74);
                    case KUNING -> new Color(202, 138, 4);
                    case MERAH -> new Color(220, 38, 38);
                    case PUTIH -> new Color(148, 163, 184);
                };
                dot.setForeground(fg);
                text.setForeground(fg);
                text.setText(status.getLabel());
            }

            panel.add(dot);
            panel.add(text);
            return panel;
        }
    }

    /**
     * Renderer badge untuk kolom Status Bayar (LUNAS / BELUM BAYAR).
     */
    private static class StatusBayarRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lbl.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
            lbl.setOpaque(true);

            if (value instanceof StatusBayar status) {
                lbl.setText(status.getLabel().toUpperCase());
                if (!isSelected) {
                    if (status == StatusBayar.LUNAS) {
                        lbl.setBackground(new Color(220, 252, 231));
                        lbl.setForeground(new Color(21, 128, 61));
                    } else {
                        lbl.setBackground(new Color(254, 243, 199));
                        lbl.setForeground(new Color(180, 83, 9));
                    }
                }
            }
            return lbl;
        }
    }
}