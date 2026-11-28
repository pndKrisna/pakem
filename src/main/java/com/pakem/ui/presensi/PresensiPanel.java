package com.pakem.ui.presensi;

import com.pakem.model.JadwalLatihan;
import com.pakem.model.Presensi;
import com.pakem.model.User;
import com.pakem.model.enums.StatusHadir;
import com.pakem.service.interfaces.ILatihanService;
import com.pakem.service.interfaces.IPresensiService;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
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
import java.util.Map;

/**
 * Panel Lembar Pencatatan Presensi Kehadiran Latihan.
 * Layout standar: Header + Selector + Toolbar + Table + Bottom Bar.
 */
public class PresensiPanel extends JPanel {

    private final IPresensiService presensiService;
    private final ILatihanService latihanService;
    private final User currentUser;

    private JComboBox<JadwalComboItem> cmbJadwal;
    private JLabel lblDetailJadwal;
    private JTable tblPresensi;
    private DefaultTableModel modelPresensi;
    private final List<Presensi> currentPresensiList = new ArrayList<>();

    private JLabel lblStatTotal;
    private JLabel lblStatHadir;
    private JLabel lblStatIzin;
    private JLabel lblStatSakit;
    private JLabel lblStatAlpa;

    private JButton btnSetSemuaHadir;
    private JButton btnSimpan;
    private JButton btnMuat;

    public PresensiPanel(IPresensiService presensiService, ILatihanService latihanService, User currentUser) {
        this.presensiService = presensiService;
        this.latihanService = latihanService;
        this.currentUser = currentUser;

        initUI();
        loadJadwalComboBox();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(241, 245, 249));

        // 1. Top Panel (Header + Selector)
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(16, 24, 12, 24)
        ));

        // Title Row
        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);

        JLabel lblTitle = new JLabel("Pencatatan Presensi Latihan");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(15, 23, 42));

        JLabel lblSubtitle = new JLabel("Pilih jadwal latihan, periksa kehadiran anggota aktif, dan simpan catatan kehadiran.");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitle.setForeground(new Color(100, 116, 139));

        JPanel titleTextPanel = new JPanel();
        titleTextPanel.setLayout(new BoxLayout(titleTextPanel, BoxLayout.Y_AXIS));
        titleTextPanel.setOpaque(false);
        titleTextPanel.add(lblTitle);
        titleTextPanel.add(Box.createVerticalStrut(2));
        titleTextPanel.add(lblSubtitle);

        titleRow.add(titleTextPanel, BorderLayout.WEST);

        // Aksi di pojok kanan atas sejajar dengan judul
        btnSetSemuaHadir = new JButton("Set Semua Hadir");
        btnSetSemuaHadir.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnSetSemuaHadir.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSetSemuaHadir.setFocusPainted(false);
        btnSetSemuaHadir.setEnabled(false);
        btnSetSemuaHadir.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1, true),
                BorderFactory.createEmptyBorder(6, 14, 6, 14)
        ));
        btnSetSemuaHadir.setBackground(Color.WHITE);
        btnSetSemuaHadir.setForeground(new Color(51, 65, 85));
        btnSetSemuaHadir.addActionListener(e -> setSemuaHadir());

        btnSimpan = new JButton("Simpan Presensi");
        btnSimpan.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSimpan.setBackground(new Color(37, 99, 235));
        btnSimpan.setForeground(Color.WHITE);
        btnSimpan.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSimpan.setFocusPainted(false);
        btnSimpan.setEnabled(false);
        btnSimpan.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));
        btnSimpan.addActionListener(e -> simpanPresensi());

        JPanel actionButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actionButtonPanel.setOpaque(false);
        actionButtonPanel.add(btnSetSemuaHadir);
        actionButtonPanel.add(btnSimpan);

        titleRow.add(actionButtonPanel, BorderLayout.EAST);
        topPanel.add(titleRow);
        topPanel.add(Box.createVerticalStrut(10));

        // Selector Row
        JPanel selectorRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        selectorRow.setOpaque(false);

        JLabel lblPilih = new JLabel("Pilih Jadwal Latihan:");
        lblPilih.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPilih.setForeground(new Color(51, 65, 85));

        cmbJadwal = new JComboBox<>();
        cmbJadwal.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmbJadwal.setPreferredSize(new Dimension(380, 32));
        cmbJadwal.addActionListener(e -> onJadwalSelected());

        btnMuat = new JButton("Muat Lembar Presensi");
        btnMuat.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnMuat.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnMuat.setFocusPainted(false);
        btnMuat.addActionListener(e -> onJadwalSelected());

        selectorRow.add(lblPilih);
        selectorRow.add(cmbJadwal);
        selectorRow.add(btnMuat);

        topPanel.add(selectorRow);
        topPanel.add(Box.createVerticalStrut(10));

        // Detail Label
        lblDetailJadwal = new JLabel("Silakan pilih sesi jadwal latihan di atas.");
        lblDetailJadwal.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDetailJadwal.setForeground(new Color(71, 85, 105));

        JPanel detailPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        detailPanel.setOpaque(false);
        detailPanel.add(lblDetailJadwal);
        topPanel.add(detailPanel);

        add(topPanel, BorderLayout.NORTH);

        // 2. Table Panel
        String[] columnNames = {"No", "ID Anggota", "Nama Anggota", "Kategori", "Subkelompok", "Status Kehadiran", "Keterangan"};
        modelPresensi = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 5 || col == 6;
            }

            @Override
            public Class<?> getColumnClass(int col) {
                if (col == 5) return StatusHadir.class;
                return String.class;
            }
        };

        tblPresensi = new JTable(modelPresensi);
        tblPresensi.setRowHeight(36);
        tblPresensi.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblPresensi.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblPresensi.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblPresensi.getTableHeader().setBackground(new Color(248, 250, 252));
        tblPresensi.setShowGrid(true);
        tblPresensi.setGridColor(new Color(241, 245, 249));

        // Column widths - reduced to fit sidebar-collapsed viewport
        tblPresensi.getColumnModel().getColumn(0).setPreferredWidth(40);
        tblPresensi.getColumnModel().getColumn(1).setPreferredWidth(70);
        tblPresensi.getColumnModel().getColumn(2).setPreferredWidth(160);
        tblPresensi.getColumnModel().getColumn(3).setPreferredWidth(100);
        tblPresensi.getColumnModel().getColumn(4).setPreferredWidth(100);
        tblPresensi.getColumnModel().getColumn(5).setPreferredWidth(140);
        tblPresensi.getColumnModel().getColumn(6).setPreferredWidth(180);

        // Center renderers
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tblPresensi.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tblPresensi.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);

        // ComboBox Editor & Renderer for Status Kehadiran
        JComboBox<StatusHadir> cmbEditorStatus = new JComboBox<>(StatusHadir.values());
        cmbEditorStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblPresensi.getColumnModel().getColumn(5).setCellEditor(new DefaultCellEditor(cmbEditorStatus));
        tblPresensi.getColumnModel().getColumn(5).setCellRenderer(new StatusHadirRenderer());

        tblPresensi.getModel().addTableModelListener(e -> updateStatistikLive());

        JScrollPane scrollTable = new JScrollPane(tblPresensi);
        scrollTable.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        add(scrollTable, BorderLayout.CENTER);

        // 4. Bottom Summary Bar
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 10));
        bottomBar.setBackground(Color.WHITE);
        bottomBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));

        lblStatTotal = new JLabel("Total Peserta: 0");
        lblStatTotal.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblStatTotal.setForeground(new Color(30, 41, 59));

        lblStatHadir = new JLabel("Hadir: 0");
        lblStatHadir.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblStatHadir.setForeground(new Color(22, 163, 74));

        lblStatIzin = new JLabel("Izin: 0");
        lblStatIzin.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblStatIzin.setForeground(new Color(37, 99, 235));

        lblStatSakit = new JLabel("Sakit: 0");
        lblStatSakit.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblStatSakit.setForeground(new Color(202, 138, 4));

        lblStatAlpa = new JLabel("Alpa: 0");
        lblStatAlpa.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblStatAlpa.setForeground(new Color(220, 38, 38));

        bottomBar.add(lblStatTotal);
        bottomBar.add(new JLabel("|"));
        bottomBar.add(lblStatHadir);
        bottomBar.add(new JLabel("|"));
        bottomBar.add(lblStatIzin);
        bottomBar.add(new JLabel("|"));
        bottomBar.add(lblStatSakit);
        bottomBar.add(new JLabel("|"));
        bottomBar.add(lblStatAlpa);

        add(bottomBar, BorderLayout.SOUTH);
    }

    public void loadJadwalComboBox() {
        cmbJadwal.removeAllItems();
        try {
            List<JadwalLatihan> allJadwal = latihanService.getSemuaJadwalLatihan();
            if (allJadwal.isEmpty()) {
                lblDetailJadwal.setText("Belum ada jadwal latihan yang terdaftar.");
                return;
            }

            for (JadwalLatihan j : allJadwal) {
                cmbJadwal.addItem(new JadwalComboItem(j));
            }

            if (cmbJadwal.getItemCount() > 0) {
                cmbJadwal.setSelectedIndex(0);
                onJadwalSelected();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat daftar jadwal: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onJadwalSelected() {
        JadwalComboItem item = (JadwalComboItem) cmbJadwal.getSelectedItem();
        if (item == null || item.jadwal == null) {
            modelPresensi.setRowCount(0);
            currentPresensiList.clear();
            btnSetSemuaHadir.setEnabled(false);
            btnSimpan.setEnabled(false);
            lblDetailJadwal.setText("Pilih sesi latihan.");
            return;
        }

        JadwalLatihan j = item.jadwal;
        String jam = (j.getJamMulai() != null ? j.getJamMulai().toString() : "--:--") + " - " +
                     (j.getJamSelesai() != null ? j.getJamSelesai().toString() : "--:--");
        String sasaran = j.getNamaKategori() + (j.getNamaSubkelompok() != null && !j.getNamaSubkelompok().equals("-") ? " (" + j.getNamaSubkelompok() + ")" : "");
        lblDetailJadwal.setText(String.format("%s | %s | Sasaran: %s | Lokasi: %s",
                j.getTanggal(), jam, sasaran, j.getLokasi() != null ? j.getLokasi() : "-"));

        loadLembarPresensi(j.getId());
    }

    private void loadLembarPresensi(int jadwalId) {
        modelPresensi.setRowCount(0);
        currentPresensiList.clear();

        try {
            List<Presensi> list = presensiService.getLembarPresensiJadwal(jadwalId);
            currentPresensiList.addAll(list);

            int no = 1;
            for (Presensi p : list) {
                modelPresensi.addRow(new Object[]{
                        no++,
                        "#" + p.getAnggotaId(),
                        p.getNamaAnggota(),
                        p.getNamaKategori(),
                        p.getNamaSubkelompok(),
                        p.getStatusHadir(),
                        p.getKeterangan() != null ? p.getKeterangan() : ""
                });
            }

            boolean hasData = !list.isEmpty();
            btnSetSemuaHadir.setEnabled(hasData);
            btnSimpan.setEnabled(hasData);

            updateStatistikLive();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat lembar presensi: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setSemuaHadir() {
        for (int row = 0; row < modelPresensi.getRowCount(); row++) {
            modelPresensi.setValueAt(StatusHadir.HADIR, row, 5);
        }
        updateStatistikLive();
    }

    private void updateStatistikLive() {
        int total = modelPresensi.getRowCount();
        int hadir = 0;
        int izin = 0;
        int sakit = 0;
        int alpa = 0;

        for (int i = 0; i < total; i++) {
            Object val = modelPresensi.getValueAt(i, 5);
            if (val instanceof StatusHadir status) {
                switch (status) {
                    case HADIR -> hadir++;
                    case IZIN -> izin++;
                    case SAKIT -> sakit++;
                    case TANPA_KETERANGAN -> alpa++;
                }
            }
        }

        lblStatTotal.setText("Total Peserta: " + total);
        lblStatHadir.setText("Hadir: " + hadir);
        lblStatIzin.setText("Izin: " + izin);
        lblStatSakit.setText("Sakit: " + sakit);
        lblStatAlpa.setText("Alpa: " + alpa);
    }

    private void simpanPresensi() {
        if (tblPresensi.isEditing()) {
            tblPresensi.getCellEditor().stopCellEditing();
        }

        JadwalComboItem item = (JadwalComboItem) cmbJadwal.getSelectedItem();
        if (item == null || item.jadwal == null) return;
        int jadwalId = item.jadwal.getId();

        List<Presensi> toSave = new ArrayList<>();
        for (int i = 0; i < modelPresensi.getRowCount(); i++) {
            if (i >= currentPresensiList.size()) break;
            Presensi p = currentPresensiList.get(i);

            Object valStatus = modelPresensi.getValueAt(i, 5);
            StatusHadir sh = valStatus instanceof StatusHadir ? (StatusHadir) valStatus : StatusHadir.HADIR;
            p.setStatusHadir(sh);

            Object valKet = modelPresensi.getValueAt(i, 6);
            String ket = valKet != null ? valKet.toString().trim() : "";
            p.setKeterangan(ket.isEmpty() ? null : ket);

            toSave.add(p);
        }

        try {
            presensiService.simpanDaftarPresensi(toSave);
            JOptionPane.showMessageDialog(this,
                    "Catatan presensi berhasil disimpan! (" + toSave.size() + " anggota)",
                    "Berhasil", JOptionPane.INFORMATION_MESSAGE);
            loadLembarPresensi(jadwalId);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Gagal menyimpan presensi: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    /**
     * Item pembantu untuk JComboBox Jadwal Latihan.
     */
    private static class JadwalComboItem {
        final JadwalLatihan jadwal;

        JadwalComboItem(JadwalLatihan jadwal) {
            this.jadwal = jadwal;
        }

        @Override
        public String toString() {
            if (jadwal == null) return "-";
            String kat = jadwal.getNamaKategori() != null ? jadwal.getNamaKategori() : "Umum";
            if (jadwal.getNamaSubkelompok() != null && !jadwal.getNamaSubkelompok().equals("-")) {
                kat += " (" + jadwal.getNamaSubkelompok() + ")";
            }
            return String.format("%s - %s [%s]", jadwal.getTanggal(), kat, jadwal.getJenisJadwal().getLabel());
        }
    }

    /**
     * Renderer badge khusus untuk Status Kehadiran di tabel.
     */
    private static class StatusHadirRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            lbl.setHorizontalAlignment(CENTER);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lbl.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
            lbl.setOpaque(true);

            if (value instanceof StatusHadir status) {
                lbl.setText("  " + status.getLabel() + "  ");
                if (!isSelected) {
                    switch (status) {
                        case HADIR -> {
                            lbl.setBackground(new Color(220, 252, 231));
                            lbl.setForeground(new Color(21, 128, 61));
                        }
                        case IZIN -> {
                            lbl.setBackground(new Color(238, 242, 255));
                            lbl.setForeground(new Color(37, 99, 235));
                        }
                        case SAKIT -> {
                            lbl.setBackground(new Color(254, 243, 199));
                            lbl.setForeground(new Color(180, 83, 9));
                        }
                        case TANPA_KETERANGAN -> {
                            lbl.setBackground(new Color(254, 226, 226));
                            lbl.setForeground(new Color(220, 38, 38));
                        }
                    }
                }
            }
            return lbl;
        }
    }
}