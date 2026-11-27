package com.pakem.ui.iuran;

import com.pakem.model.Iuran;
import com.pakem.model.SanggarSettings;
import com.pakem.model.User;
import com.pakem.model.enums.StatusBayar;
import com.pakem.model.enums.StatusIuranWarna;
import com.pakem.service.interfaces.IIuranService;

import javax.swing.BorderFactory;
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
 * Panel untuk manajemen Iuran Bulanan (Fase 7).
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
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        setBackground(new Color(241, 245, 249));

        initComponents();
        loadData();
    }

    private void initComponents() {
        // --- Header Section ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setOpaque(false);
        JLabel lblTitle = new JLabel("Iuran Bulanan");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(15, 23, 42));
        titlePanel.add(lblTitle);

        // Filter Periode
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filterPanel.setOpaque(false);

        String[] bulanNames = {"Januari", "Februari", "Maret", "April", "Mei", "Juni",
                               "Juli", "Agustus", "September", "Oktober", "November", "Desember"};
        cbBulan = new JComboBox<>(bulanNames);
        cbBulan.setSelectedIndex(LocalDate.now().getMonthValue() - 1);
        cbBulan.addActionListener(e -> loadData());

        int currentYear = LocalDate.now().getYear();
        Integer[] years = new Integer[5];
        for (int i = 0; i < 5; i++) {
            years[i] = currentYear - 2 + i;
        }
        cbTahun = new JComboBox<>(years);
        cbTahun.setSelectedItem(currentYear);
        cbTahun.addActionListener(e -> loadData());

        filterPanel.add(new JLabel("Periode:"));
        filterPanel.add(cbBulan);
        filterPanel.add(cbTahun);

        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(filterPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // --- Table Section ---
        String[] columns = {"ID", "Indikator", "Nama Anggota", "Kategori/Kelompok", "Status", "Nominal", "Tgl Bayar", "Aksi"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableIuran = new JTable(tableModel);
        tableIuran.setRowHeight(35);
        tableIuran.removeColumn(tableIuran.getColumnModel().getColumn(0)); // Sembunyikan ID Anggota

        // Cell Renderers
        tableIuran.getColumnModel().getColumn(0).setCellRenderer(new ColorIndicatorRenderer());
        tableIuran.getColumnModel().getColumn(3).setCellRenderer(new StatusRenderer());

        JScrollPane scrollPane = new JScrollPane(tableIuran);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        add(scrollPane, BorderLayout.CENTER);

        // --- Footer / Actions Section ---
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setOpaque(false);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));

        lblSummary = new JLabel("Memuat data...");
        lblSummary.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSummary.setForeground(new Color(71, 85, 105));

        JPanel actionButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionButtons.setOpaque(false);

        JButton btnBayar = new JButton("💰 Catat Pembayaran");
        btnBayar.addActionListener(e -> handleBayar());

        JButton btnBatal = new JButton("🔄 Batalkan Status");
        btnBatal.addActionListener(e -> handleBatal());

        actionButtons.add(btnBatal);
        actionButtons.add(btnBayar);

        footerPanel.add(lblSummary, BorderLayout.WEST);
        footerPanel.add(actionButtons, BorderLayout.EAST);
        add(footerPanel, BorderLayout.SOUTH);
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

    private static class ColorIndicatorRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, "  ", isSelected, hasFocus, row, column);
            label.setOpaque(true);

            if (value instanceof StatusIuranWarna status) {
                Color bg = switch (status) {
                    case HIJAU -> new Color(34, 197, 94);
                    case KUNING -> new Color(234, 179, 8);
                    case MERAH -> new Color(239, 68, 68);
                    case PUTIH -> Color.WHITE;
                };
                label.setBackground(bg);
                label.setToolTipText(status.getLabel());
            }
            return label;
        }
    }

    private static class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setFont(new Font("Segoe UI", Font.BOLD, 12));

            if (value instanceof StatusBayar status) {
                label.setText(status.getLabel().toUpperCase());
                if (status == StatusBayar.LUNAS) {
                    label.setForeground(new Color(22, 163, 74));
                } else {
                    label.setForeground(new Color(71, 85, 105));
                }
            }
            return label;
        }
    }
}
