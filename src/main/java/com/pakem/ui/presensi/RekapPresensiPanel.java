package com.pakem.ui.presensi;

import com.pakem.model.KategoriTingkat;
import com.pakem.model.RekapPresensi;
import com.pakem.model.Subkelompok;
import com.pakem.model.User;
import com.pakem.service.interfaces.IKategoriService;
import com.pakem.service.interfaces.IPresensiService;

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
import javax.swing.JTextField;
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
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel Rekapitulasi Kehadiran Latihan Anggota Sanggar (Fase 5).
 * Menampilkan statistik kehadiran, persentase keaktifan per anggota, dan ekspor teks.
 */
public class RekapPresensiPanel extends JPanel {

    private final IPresensiService presensiService;
    private final IKategoriService kategoriService;
    private final User currentUser;

    private JComboBox<KategoriTingkat> cmbKategori;
    private JComboBox<Subkelompok> cmbSubkelompok;
    private JTextField txtDariTanggal;
    private JTextField txtSampaiTanggal;
    private JButton btnFilter;
    private JButton btnSalin;

    private JTable tblRekap;
    private DefaultTableModel modelRekap;
    private final List<RekapPresensi> rekapList = new ArrayList<>();

    public RekapPresensiPanel(IPresensiService presensiService, IKategoriService kategoriService, User currentUser) {
        this.presensiService = presensiService;
        this.kategoriService = kategoriService;
        this.currentUser = currentUser;

        initUI();
        loadKategoriFilter();
        loadRekapData();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(241, 245, 249));

        // 1. Header
        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setBackground(Color.WHITE);
        topContainer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(16, 24, 16, 24)
        ));

        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);

        JLabel lblTitle = new JLabel("Rekapitulasi Kehadiran Anggota");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(15, 23, 42));

        JLabel lblSubtitle = new JLabel("Statistik tingkat keaktifan anggota aktif sanggar berdasarkan jumlah kehadiran dan persentase.");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitle.setForeground(new Color(100, 116, 139));

        JPanel titleText = new JPanel();
        titleText.setLayout(new BoxLayout(titleText, BoxLayout.Y_AXIS));
        titleText.setOpaque(false);
        titleText.add(lblTitle);
        titleText.add(Box.createVerticalStrut(2));
        titleText.add(lblSubtitle);

        titleRow.add(titleText, BorderLayout.WEST);

        // Filter Bar
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        filterBar.setOpaque(false);

        JLabel lblKat = new JLabel("Kategori:");
        lblKat.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblKat.setForeground(new Color(51, 65, 85));

        cmbKategori = new JComboBox<>();
        cmbKategori.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmbKategori.setPreferredSize(new Dimension(150, 30));
        cmbKategori.addActionListener(e -> onKategoriChanged());

        JLabel lblSub = new JLabel("Subkelompok:");
        lblSub.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSub.setForeground(new Color(51, 65, 85));

        cmbSubkelompok = new JComboBox<>();
        cmbSubkelompok.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmbSubkelompok.setPreferredSize(new Dimension(150, 30));

        JLabel lblDari = new JLabel("Dari (YYYY-MM-DD):");
        lblDari.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDari.setForeground(new Color(51, 65, 85));

        txtDariTanggal = new JTextField();
        txtDariTanggal.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtDariTanggal.setPreferredSize(new Dimension(110, 30));

        JLabel lblSampai = new JLabel("Sampai:");
        lblSampai.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSampai.setForeground(new Color(51, 65, 85));

        txtSampaiTanggal = new JTextField();
        txtSampaiTanggal.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtSampaiTanggal.setPreferredSize(new Dimension(110, 30));

        btnFilter = new JButton("Terapkan Filter");
        btnFilter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnFilter.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnFilter.addActionListener(e -> loadRekapData());

        btnSalin = new JButton("📋 Salin Rekap Teks");
        btnSalin.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnSalin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSalin.addActionListener(e -> salinRekapKeClipboard());

        filterBar.add(lblKat);
        filterBar.add(cmbKategori);
        filterBar.add(lblSub);
        filterBar.add(cmbSubkelompok);
        filterBar.add(lblDari);
        filterBar.add(txtDariTanggal);
        filterBar.add(lblSampai);
        filterBar.add(txtSampaiTanggal);
        filterBar.add(btnFilter);
        filterBar.add(btnSalin);

        topContainer.add(titleRow);
        topContainer.add(Box.createVerticalStrut(10));
        topContainer.add(filterBar);

        add(topContainer, BorderLayout.NORTH);

        // 2. Tabel Rekapitulasi
        String[] columns = {
                "ID", "Nama Anggota", "Kategori", "Subkelompok",
                "Total Sesi", "Hadir", "Izin", "Sakit", "Alpa", "Persentase Kehadiran"
        };

        modelRekap = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tblRekap = new JTable(modelRekap);
        tblRekap.setRowHeight(36);
        tblRekap.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblRekap.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblRekap.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblRekap.getTableHeader().setBackground(new Color(248, 250, 252));
        tblRekap.setShowGrid(true);
        tblRekap.setGridColor(new Color(241, 245, 249));

        // Column widths
        tblRekap.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblRekap.getColumnModel().getColumn(1).setPreferredWidth(200);
        tblRekap.getColumnModel().getColumn(2).setPreferredWidth(120);
        tblRekap.getColumnModel().getColumn(3).setPreferredWidth(120);
        tblRekap.getColumnModel().getColumn(4).setPreferredWidth(80);
        tblRekap.getColumnModel().getColumn(5).setPreferredWidth(75);
        tblRekap.getColumnModel().getColumn(6).setPreferredWidth(75);
        tblRekap.getColumnModel().getColumn(7).setPreferredWidth(75);
        tblRekap.getColumnModel().getColumn(8).setPreferredWidth(75);
        tblRekap.getColumnModel().getColumn(9).setPreferredWidth(140);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i <= 8; i++) {
            if (i != 1) { // bukan kolom nama
                tblRekap.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }

        // Custom Renderer for Persentase Kehadiran Badge
        tblRekap.getColumnModel().getColumn(9).setCellRenderer(new PersentaseBadgeRenderer());

        add(new JScrollPane(tblRekap), BorderLayout.CENTER);
    }

    private void loadKategoriFilter() {
        cmbKategori.removeAllItems();
        cmbKategori.addItem(new KategoriTingkat(0, "Semua Kategori", 0));
        try {
            List<KategoriTingkat> allKat = kategoriService.getSemuaKategori();
            for (KategoriTingkat k : allKat) {
                cmbKategori.addItem(k);
            }
        } catch (Exception ex) {
            System.err.println("Gagal memuat kategori filter: " + ex.getMessage());
        }
        onKategoriChanged();
    }

    private void onKategoriChanged() {
        cmbSubkelompok.removeAllItems();
        cmbSubkelompok.addItem(new Subkelompok(0, 0, "Semua Subkelompok"));

        Object sel = cmbKategori.getSelectedItem();
        if (sel instanceof KategoriTingkat kat && kat.getId() > 0) {
            try {
                List<Subkelompok> subList = kategoriService.getSubkelompokByKategori(kat.getId());
                for (Subkelompok s : subList) {
                    cmbSubkelompok.addItem(s);
                }
            } catch (Exception ex) {
                System.err.println("Gagal memuat subkelompok filter: " + ex.getMessage());
            }
        }
    }

    public void loadRekapData() {
        modelRekap.setRowCount(0);
        rekapList.clear();

        Object katSel = cmbKategori.getSelectedItem();
        Integer katId = (katSel instanceof KategoriTingkat k && k.getId() > 0) ? k.getId() : null;

        Object subSel = cmbSubkelompok.getSelectedItem();
        Integer subId = (subSel instanceof Subkelompok s && s.getId() > 0) ? s.getId() : null;

        LocalDate dari = null;
        if (!txtDariTanggal.getText().trim().isEmpty()) {
            try {
                dari = LocalDate.parse(txtDariTanggal.getText().trim());
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Format tanggal 'Dari' salah. Gunakan YYYY-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        LocalDate sampai = null;
        if (!txtSampaiTanggal.getText().trim().isEmpty()) {
            try {
                sampai = LocalDate.parse(txtSampaiTanggal.getText().trim());
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Format tanggal 'Sampai' salah. Gunakan YYYY-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        try {
            List<RekapPresensi> list = presensiService.getRekapitulasi(katId, subId, dari, sampai);
            rekapList.addAll(list);

            for (RekapPresensi r : list) {
                modelRekap.addRow(new Object[]{
                        "#" + r.getAnggotaId(),
                        r.getNamaAnggota(),
                        r.getNamaKategori(),
                        r.getNamaSubkelompok(),
                        r.getTotalPertemuan(),
                        r.getJumlahHadir(),
                        r.getJumlahIzin(),
                        r.getJumlahSakit(),
                        r.getJumlahAlpa(),
                        r.getPersentaseKehadiran()
                });
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data rekapitulasi: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void salinRekapKeClipboard() {
        if (rekapList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tidak ada data rekapitulasi untuk disalin.", "Informasi", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("📊 *REKAPITULASI PRESENSI KEHADIRAN ANGGOTA SANGGAR*\n");
        sb.append("Generated on: ").append(LocalDate.now()).append("\n");
        sb.append("=========================================\n");

        for (RekapPresensi r : rekapList) {
            sb.append(String.format("• %s (%s / %s)\n", r.getNamaAnggota(), r.getNamaKategori(), r.getNamaSubkelompok()));
            sb.append(String.format("  Kehadiran: %d Hadir, %d Izin, %d Sakit, %d Alpa (Total: %d Sesi) -> *%s*\n",
                    r.getJumlahHadir(), r.getJumlahIzin(), r.getJumlahSakit(), r.getJumlahAlpa(),
                    r.getTotalPertemuan(), r.getFormattedPersentase()));
        }

        sb.append("=========================================\n");

        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(sb.toString()), null);
        JOptionPane.showMessageDialog(this, "Ringkasan rekapitulasi berhasil disalin ke Clipboard!", "Berhasil", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Renderer badge khusus untuk kolom Persentase Kehadiran.
     */
    private static class PersentaseBadgeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            lbl.setHorizontalAlignment(CENTER);

            if (value instanceof Number num) {
                double pct = num.doubleValue();
                lbl.setText(String.format("  %.1f%%  ", pct));
                lbl.setOpaque(true);

                if (!isSelected) {
                    if (pct >= 80.0) {
                        lbl.setBackground(new Color(220, 252, 231)); // Soft green
                        lbl.setForeground(new Color(21, 128, 61));
                    } else if (pct >= 50.0) {
                        lbl.setBackground(new Color(254, 243, 199)); // Soft amber
                        lbl.setForeground(new Color(180, 83, 9));
                    } else {
                        lbl.setBackground(new Color(254, 226, 226)); // Soft red
                        lbl.setForeground(new Color(220, 38, 38));
                    }
                }
            }
            return lbl;
        }
    }
}
