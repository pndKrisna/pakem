package com.pakem.ui.dashboard;

import com.pakem.model.DashboardSummary;
import com.pakem.model.Event;
import com.pakem.model.JadwalLatihan;
import com.pakem.model.User;
import com.pakem.service.interfaces.IDashboardService;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * Panel Dashboard Ringkasan Interaktif (Fase 8).
 * Menampilkan ringkasan statistik anggota, jadwal terdekat, presensi, dan status iuran (khusus Pengurus).
 */
public class DashboardPanel extends JPanel {

    private final IDashboardService dashboardService;
    private final User currentUser;

    private JLabel lblTotalAnggota;
    private JLabel lblAnggotaAktifVakum;
    private JLabel lblKehadiran;
    private JLabel lblIuranStatus;
    private JPanel cardIuran;

    private JTable tblJadwal;
    private DefaultTableModel modelJadwal;
    private JTable tblEvent;
    private DefaultTableModel modelEvent;

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.of("id", "ID"));
    private final DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");

    public DashboardPanel(IDashboardService dashboardService, User currentUser) {
        this.dashboardService = dashboardService;
        this.currentUser = currentUser;

        setLayout(new BorderLayout());
        setBackground(new Color(241, 245, 249));

        initComponents();
        loadDashboardData();
    }

    private void initComponents() {
        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setOpaque(false);
        root.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        // 1. Welcome Header
        JPanel banner = new JPanel(new BorderLayout());
        banner.setBackground(Color.WHITE);
        banner.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
                BorderFactory.createEmptyBorder(20, 24, 20, 24)
        ));
        banner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        JPanel bannerText = new JPanel();
        bannerText.setLayout(new BoxLayout(bannerText, BoxLayout.Y_AXIS));
        bannerText.setOpaque(false);

        JLabel lblGreeting = new JLabel("Selamat Datang di Sanggar Seni Tabuh Pakem!");
        lblGreeting.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblGreeting.setForeground(new Color(15, 23, 42));

        JLabel lblDesc = new JLabel("Halo, " + currentUser.getUsername() + " (" + currentUser.getRole().getLabel() + "). Berikut ringkasan operasional dan kegiatan terkini.");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblDesc.setForeground(new Color(100, 116, 139));

        bannerText.add(lblGreeting);
        bannerText.add(Box.createVerticalStrut(4));
        bannerText.add(lblDesc);
        banner.add(bannerText, BorderLayout.WEST);

        root.add(banner);
        root.add(Box.createVerticalStrut(20));

        // 2. Metric Stat Cards Row
        int statGridCols = currentUser.canAccessIuran() ? 4 : 3;
        JPanel statGrid = new JPanel(new GridLayout(1, statGridCols, 16, 0));
        statGrid.setOpaque(false);
        statGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 115));

        // Card 1: Anggota
        JPanel cardAnggota = createStatCard("TOTAL ANGGOTA", "0", "0 Aktif • 0 Vakum", new Color(37, 99, 235), new Color(238, 242, 255));
        lblTotalAnggota = (JLabel) cardAnggota.getClientProperty("valLabel");
        lblAnggotaAktifVakum = (JLabel) cardAnggota.getClientProperty("subLabel");
        statGrid.add(cardAnggota);

        // Card 2: Kehadiran Presensi
        JPanel cardPresensi = createStatCard("KEHADIRAN BULAN INI", "0%", "Rata-rata presensi latihan", new Color(22, 163, 74), new Color(240, 253, 244));
        lblKehadiran = (JLabel) cardPresensi.getClientProperty("valLabel");
        statGrid.add(cardPresensi);

        // Card 3: Iuran Bulanan (Khusus Pengurus / canAccessIuran)
        if (currentUser.canAccessIuran()) {
            cardIuran = createStatCard("IURAN BULAN INI", "0%", "0 Lunas / 0 Aktif", new Color(217, 119, 6), new Color(254, 243, 199));
            lblIuranStatus = (JLabel) cardIuran.getClientProperty("valLabel");
            statGrid.add(cardIuran);
        }

        // Card 4: Jadwal Operasional
        JPanel cardOperasional = createStatCard("JADWAL TERDEKAT", "0", "Latihan & Pertunjukan", new Color(124, 58, 237), new Color(245, 243, 255));
        statGrid.add(cardOperasional);

        root.add(statGrid);
        root.add(Box.createVerticalStrut(24));

        // 3. Two Columns: Latihan Terdekat & Event Terdekat
        JPanel tablesPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        tablesPanel.setOpaque(false);
        tablesPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 380));

        // Panel Latihan Terdekat
        JPanel pnlLatihan = createSectionCard("📅 Jadwal Latihan Terdekat");
        String[] colLatihan = {"Tanggal", "Waktu", "Kelompok / Event", "Lokasi"};
        modelJadwal = new DefaultTableModel(colLatihan, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tblJadwal = new JTable(modelJadwal);
        tblJadwal.setRowHeight(28);
        JScrollPane spLatihan = new JScrollPane(tblJadwal);
        spLatihan.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        pnlLatihan.add(spLatihan, BorderLayout.CENTER);
        tablesPanel.add(pnlLatihan);

        // Panel Event Terdekat
        JPanel pnlEvent = createSectionCard("🎭 Pertunjukan / Event Terdekat");
        String[] colEvent = {"Tanggal", "Nama Acara", "Jenis", "Lokasi"};
        modelEvent = new DefaultTableModel(colEvent, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tblEvent = new JTable(modelEvent);
        tblEvent.setRowHeight(28);
        JScrollPane spEvent = new JScrollPane(tblEvent);
        spEvent.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        pnlEvent.add(spEvent, BorderLayout.CENTER);
        tablesPanel.add(pnlEvent);

        root.add(tablesPanel);
        root.add(Box.createVerticalGlue());

        JScrollPane scroll = new JScrollPane(root);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel createStatCard(String title, String val, String sub, Color textColor, Color bgColor) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
                BorderFactory.createEmptyBorder(14, 18, 14, 18)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblTitle.setForeground(new Color(100, 116, 139));

        JLabel lblVal = new JLabel(val);
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblVal.setForeground(textColor);

        JLabel lblSub = new JLabel(sub);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSub.setForeground(new Color(148, 163, 184));

        card.add(lblTitle);
        card.add(Box.createVerticalStrut(4));
        card.add(lblVal);
        card.add(Box.createVerticalStrut(2));
        card.add(lblSub);

        card.putClientProperty("valLabel", lblVal);
        card.putClientProperty("subLabel", lblSub);
        return card;
    }

    private JPanel createSectionCard(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));

        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(new Color(15, 23, 42));
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        panel.add(lbl, BorderLayout.NORTH);
        return panel;
    }

    public void loadDashboardData() {
        try {
            DashboardSummary sum = dashboardService.getSummary();

            lblTotalAnggota.setText(String.valueOf(sum.getTotalAnggota()));
            lblAnggotaAktifVakum.setText(sum.getJumlahAktif() + " Aktif • " + sum.getJumlahVakum() + " Vakum • " + sum.getJumlahNonaktif() + " Nonaktif");

            lblKehadiran.setText(String.format(Locale.US, "%.1f%%", sum.getPersentaseKehadiranBulanIni()));

            if (currentUser.canAccessIuran() && cardIuran != null) {
                lblIuranStatus.setText(String.format(Locale.US, "%.0f%%", sum.getPersentaseIuranLunas()));
                JLabel subLabel = (JLabel) cardIuran.getClientProperty("subLabel");
                if (subLabel != null) {
                    subLabel.setText(sum.getIuranLunas() + " Lunas / " + sum.getIuranTotalAktif() + " Aktif (" + sum.getIuranBelumBayar() + " Belum)");
                }
            }

            // Load Tabel Jadwal Terdekat (Maksimal 5)
            modelJadwal.setRowCount(0);
            List<JadwalLatihan> upcomingLatihan = dashboardService.getJadwalLatihanTerdekat(5);
            for (JadwalLatihan j : upcomingLatihan) {
                String target = j.getNamaKategori();
                if (j.getNamaSubkelompok() != null && !j.getNamaSubkelompok().equals("-")) {
                    target += " (" + j.getNamaSubkelompok() + ")";
                }
                String waktu = (j.getJamMulai() != null ? j.getJamMulai().format(timeFmt) : "-") +
                        " - " + (j.getJamSelesai() != null ? j.getJamSelesai().format(timeFmt) : "-");
                modelJadwal.addRow(new Object[]{
                        j.getTanggal() != null ? j.getTanggal().format(dtf) : "-",
                        waktu,
                        target,
                        j.getLokasi() != null ? j.getLokasi() : "-"
                });
            }

            // Load Tabel Event Terdekat (Maksimal 5)
            modelEvent.setRowCount(0);
            List<Event> upcomingEvent = dashboardService.getEventTerdekat(5);
            for (Event e : upcomingEvent) {
                modelEvent.addRow(new Object[]{
                        e.getTanggal() != null ? e.getTanggal().format(dtf) : "-",
                        e.getNamaAcara(),
                        e.getJenisAcara() != null ? e.getJenisAcara().getLabel() : "-",
                        e.getLokasi() != null ? e.getLokasi() : "-"
                });
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data dashboard: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
