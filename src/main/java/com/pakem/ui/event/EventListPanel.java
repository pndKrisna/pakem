package com.pakem.ui.event;

import com.pakem.model.Event;
import com.pakem.model.User;
import com.pakem.model.enums.JenisAcara;
import com.pakem.service.interfaces.IEventService;
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
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel Utama Manajemen Acara Pertunjukan / Event.
 * Layout standar: Header + Toolbar + Filter + Table + Bottom Bar.
 */
public class EventListPanel extends JPanel {

    private final IEventService eventService;
    private final IKategoriService kategoriService;
    private final User currentUser;

    private JComboBox<String> cmbFilterJenis;
    private JTextField txtSearch;
    private JTextField txtDariTanggal;
    private JTextField txtSampaiTanggal;
    private JButton btnFilter;

    private JButton btnTambah;
    private JButton btnUbah;
    private JButton btnHapus;
    private JButton btnKelolaTim;
    private JButton btnSalinWA;

    private JTable tblEvent;
    private DefaultTableModel modelEvent;
    private final List<Event> eventList = new ArrayList<>();
    private Event selectedEvent = null;

    public EventListPanel(IEventService eventService, IKategoriService kategoriService, User currentUser) {
        this.eventService = eventService;
        this.kategoriService = kategoriService;
        this.currentUser = currentUser;

        initUI();
        loadEventData();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(241, 245, 249));

        // 1. Top Panel (Header, Action Buttons, Filter)
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

        JLabel lblTitle = new JLabel("Manajemen Acara Pertunjukan / Event");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(15, 23, 42));

        JLabel lblSubtitle = new JLabel("Kelola acara pentas/job, ngayah, ujian, pembentukan multi-tim, dan susunan pemain lintas tingkat.");
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

        btnTambah = new JButton("+ Tambah Acara");
        btnTambah.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnTambah.setBackground(new Color(37, 99, 235));
        btnTambah.setForeground(Color.WHITE);
        btnTambah.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTambah.setFocusPainted(false);
        btnTambah.setVisible(currentUser.canManageEvent());
        btnTambah.addActionListener(e -> openAddDialog());

        btnUbah = new JButton("Ubah Acara");
        btnUbah.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnUbah.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnUbah.setFocusPainted(false);
        btnUbah.setEnabled(false);
        btnUbah.setVisible(currentUser.canManageEvent());
        btnUbah.addActionListener(e -> openEditDialog());

        btnHapus = new JButton("Hapus Acara");
        btnHapus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnHapus.setForeground(new Color(220, 38, 38));
        btnHapus.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnHapus.setFocusPainted(false);
        btnHapus.setEnabled(false);
        btnHapus.setVisible(currentUser.canManageEvent());
        btnHapus.addActionListener(e -> deleteEvent());

        btnKelolaTim = new JButton("Kelola Tim & Pemain...");
        btnKelolaTim.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnKelolaTim.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnKelolaTim.setFocusPainted(false);
        btnKelolaTim.setEnabled(false);
        btnKelolaTim.addActionListener(e -> openKelolaTimDialog());

        btnSalinWA = new JButton("Salin WA");
        btnSalinWA.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnSalinWA.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSalinWA.setFocusPainted(false);
        btnSalinWA.setEnabled(false);
        btnSalinWA.addActionListener(e -> salinPengumumanWA());

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh.setFocusPainted(false);
        btnRefresh.addActionListener(e -> loadEventData());

        actionButtonPanel.add(btnTambah);
        actionButtonPanel.add(btnUbah);
        actionButtonPanel.add(btnHapus);
        actionButtonPanel.add(btnKelolaTim);
        actionButtonPanel.add(btnSalinWA);
        actionButtonPanel.add(btnRefresh);

        titleRow.add(actionButtonPanel, BorderLayout.EAST);
        topPanel.add(titleRow);
        topPanel.add(Box.createVerticalStrut(14));

        // Filter Row
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterRow.setOpaque(false);

        JLabel lblFilterJenis = new JLabel("Jenis Acara:");
        lblFilterJenis.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblFilterJenis.setForeground(new Color(71, 85, 105));

        cmbFilterJenis = new JComboBox<>(new String[]{"Semua Jenis", "Pentas / Job", "Ngayah", "Ujian Kenaikan Tingkat"});
        cmbFilterJenis.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmbFilterJenis.setPreferredSize(new Dimension(170, 32));
        cmbFilterJenis.addActionListener(e -> loadEventData());

        JLabel lblCari = new JLabel("Pencarian:");
        lblCari.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblCari.setForeground(new Color(71, 85, 105));

        txtSearch = new JTextField();
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtSearch.setPreferredSize(new Dimension(200, 32));
        txtSearch.putClientProperty("JTextField.placeholderText", "Ketik nama acara, lokasi...");
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    loadEventData();
                }
            }
        });

        JLabel lblDari = new JLabel("Dari:");
        lblDari.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtDariTanggal = new JTextField();
        txtDariTanggal.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtDariTanggal.setPreferredSize(new Dimension(110, 32));
        txtDariTanggal.putClientProperty("JTextField.placeholderText", "YYYY-MM-DD");

        JLabel lblSampai = new JLabel("Sampai:");
        lblSampai.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtSampaiTanggal = new JTextField();
        txtSampaiTanggal.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtSampaiTanggal.setPreferredSize(new Dimension(110, 32));
        txtSampaiTanggal.putClientProperty("JTextField.placeholderText", "YYYY-MM-DD");

        btnFilter = new JButton("Filter");
        btnFilter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnFilter.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnFilter.setFocusPainted(false);
        btnFilter.addActionListener(e -> loadEventData());

        filterRow.add(lblFilterJenis);
        filterRow.add(cmbFilterJenis);
        filterRow.add(Box.createHorizontalStrut(15));
        filterRow.add(lblCari);
        filterRow.add(txtSearch);
        filterRow.add(Box.createHorizontalStrut(10));
        filterRow.add(lblDari);
        filterRow.add(txtDariTanggal);
        filterRow.add(lblSampai);
        filterRow.add(txtSampaiTanggal);
        filterRow.add(btnFilter);

        topPanel.add(filterRow);
        add(topPanel, BorderLayout.NORTH);

        // 2. Table Panel
        String[] columns = {
                "ID", "Nama Acara", "Jenis Acara", "Tanggal", "Waktu", "Lokasi", "Jumlah Tim", "Total Pemain", "Catatan"
        };

        modelEvent = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tblEvent = new JTable(modelEvent);
        tblEvent.setRowHeight(36);
        tblEvent.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblEvent.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblEvent.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblEvent.getTableHeader().setBackground(new Color(248, 250, 252));
        tblEvent.getTableHeader().setForeground(new Color(71, 85, 105));
        tblEvent.setShowGrid(true);
        tblEvent.setGridColor(new Color(241, 245, 249));

        // Column widths - reduced to fit sidebar-collapsed viewport
        tblEvent.getColumnModel().getColumn(0).setPreferredWidth(45);
        tblEvent.getColumnModel().getColumn(1).setPreferredWidth(160);
        tblEvent.getColumnModel().getColumn(2).setPreferredWidth(120);
        tblEvent.getColumnModel().getColumn(3).setPreferredWidth(90);
        tblEvent.getColumnModel().getColumn(4).setPreferredWidth(70);
        tblEvent.getColumnModel().getColumn(5).setPreferredWidth(140);
        tblEvent.getColumnModel().getColumn(6).setPreferredWidth(70);
        tblEvent.getColumnModel().getColumn(7).setPreferredWidth(80);
        tblEvent.getColumnModel().getColumn(8).setPreferredWidth(160);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        tblEvent.getColumnModel().getColumn(0).setCellRenderer(center);
        tblEvent.getColumnModel().getColumn(3).setCellRenderer(center);
        tblEvent.getColumnModel().getColumn(4).setCellRenderer(center);
        tblEvent.getColumnModel().getColumn(6).setCellRenderer(center);
        tblEvent.getColumnModel().getColumn(7).setCellRenderer(center);

        // Renderer Badge Jenis Acara
        tblEvent.getColumnModel().getColumn(2).setCellRenderer(new JenisAcaraBadgeRenderer());

        tblEvent.getSelectionModel().addListSelectionListener(e -> {
            boolean has = tblEvent.getSelectedRow() != -1;
            btnUbah.setEnabled(has && currentUser.canManageEvent());
            btnHapus.setEnabled(has && currentUser.canManageEvent());
            btnKelolaTim.setEnabled(has);
            btnSalinWA.setEnabled(has);

            if (has) {
                int row = tblEvent.getSelectedRow();
                if (row >= 0 && row < eventList.size()) {
                    selectedEvent = eventList.get(row);
                }
            } else {
                selectedEvent = null;
            }
        });

        tblEvent.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tblEvent.getSelectedRow() != -1) {
                    openKelolaTimDialog();
                }
            }
        });

        JScrollPane scrollTable = new JScrollPane(tblEvent);
        scrollTable.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        add(scrollTable, BorderLayout.CENTER);

        // 3. Bottom Summary Bar
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 10));
        bottomBar.setBackground(Color.WHITE);
        bottomBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));

        JLabel lblSummary = new JLabel("Total Acara: 0");
        lblSummary.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSummary.setForeground(new Color(30, 41, 59));
        bottomBar.add(lblSummary);

        add(bottomBar, BorderLayout.SOUTH);
    }

    public void loadEventData() {
        modelEvent.setRowCount(0);
        eventList.clear();

        JenisAcara jenis = null;
        int idx = cmbFilterJenis.getSelectedIndex();
        if (idx == 1) jenis = JenisAcara.JOB;
        else if (idx == 2) jenis = JenisAcara.NGAYAH;
        else if (idx == 3) jenis = JenisAcara.UJIAN;

        String kw = txtSearch.getText().trim();
        LocalDate dari = null;
        if (!txtDariTanggal.getText().trim().isEmpty()) {
            try {
                dari = LocalDate.parse(txtDariTanggal.getText().trim());
            } catch (DateTimeParseException ignored) {
            }
        }

        LocalDate sampai = null;
        if (!txtSampaiTanggal.getText().trim().isEmpty()) {
            try {
                sampai = LocalDate.parse(txtSampaiTanggal.getText().trim());
            } catch (DateTimeParseException ignored) {
            }
        }

        try {
            List<Event> list = eventService.getDaftarEvent(jenis, dari, sampai, kw.isEmpty() ? null : kw);
            eventList.addAll(list);

            for (Event e : list) {
                modelEvent.addRow(new Object[]{
                        "#" + e.getId(),
                        e.getNamaAcara(),
                        e.getJenisAcara(),
                        e.getTanggal() != null ? e.getTanggal().toString() : "-",
                        e.getWaktu() != null ? e.getWaktu().toString() : "-",
                        e.getLokasi() != null ? e.getLokasi() : "-",
                        e.getJumlahTim(),
                        e.getTotalPemain(),
                        e.getCatatan() != null ? e.getCatatan() : "-"
                });
            }

            selectedEvent = null;
            btnUbah.setEnabled(false);
            btnHapus.setEnabled(false);
            btnKelolaTim.setEnabled(false);
            btnSalinWA.setEnabled(false);

            // Update summary
            JLabel lblSummary = (JLabel) ((JPanel) getComponent(2)).getComponent(0);
            lblSummary.setText("Total Acara: " + list.size());

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat acara pertunjukan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openAddDialog() {
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        EventFormDialog dlg = new EventFormDialog(parent, eventService, null);
        dlg.setVisible(true);
        if (dlg.isSaved()) {
            loadEventData();
        }
    }

    private void openEditDialog() {
        if (selectedEvent == null) return;
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        EventFormDialog dlg = new EventFormDialog(parent, eventService, selectedEvent);
        dlg.setVisible(true);
        if (dlg.isSaved()) {
            loadEventData();
        }
    }

    private void deleteEvent() {
        if (selectedEvent == null) return;
        int conf = JOptionPane.showConfirmDialog(this,
                "Hapus acara pertunjukan '" + selectedEvent.getNamaAcara() + "'?\n" +
                "Seluruh tim dan susunan pemain di acara ini akan ikut terhapus.",
                "Konfirmasi Hapus Acara", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (conf == JOptionPane.YES_OPTION) {
            try {
                eventService.hapusEvent(selectedEvent.getId());
                JOptionPane.showMessageDialog(this, "Acara berhasil dihapus.", "Berhasil", JOptionPane.INFORMATION_MESSAGE);
                loadEventData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal menghapus acara: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void openKelolaTimDialog() {
        if (selectedEvent == null) return;
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        TimEventDialog dlg = new TimEventDialog(parent, eventService, kategoriService, selectedEvent, currentUser);
        dlg.setVisible(true);
        loadEventData();
    }

    private void salinPengumumanWA() {
        if (selectedEvent == null) return;
        try {
            String text = eventService.formatPengumumanEvent(selectedEvent.getId());
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text), null);
            JOptionPane.showMessageDialog(this,
                    "Rincian jadwal acara dan susunan pemain berhasil disalin ke Clipboard!\nSiap dibagikan ke grup WhatsApp.",
                    "Berhasil Disalin", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal menyalin pengumuman: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Renderer badge khusus Jenis Acara.
     */
    private static class JenisAcaraBadgeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            lbl.setHorizontalAlignment(CENTER);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lbl.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
            lbl.setOpaque(true);

            if (value instanceof JenisAcara jenis) {
                lbl.setText("  " + jenis.getLabel() + "  ");
                if (!isSelected) {
                    switch (jenis) {
                        case JOB -> {
                            lbl.setBackground(new Color(238, 242, 255));
                            lbl.setForeground(new Color(79, 70, 229));
                        }
                        case NGAYAH -> {
                            lbl.setBackground(new Color(240, 253, 244));
                            lbl.setForeground(new Color(22, 163, 74));
                        }
                        case UJIAN -> {
                            lbl.setBackground(new Color(254, 243, 199));
                            lbl.setForeground(new Color(180, 83, 9));
                        }
                    }
                }
            }
            return lbl;
        }
    }
}