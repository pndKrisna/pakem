package com.pakem.ui.event;

import com.pakem.model.Event;
import com.pakem.model.PemainTim;
import com.pakem.model.TimEvent;
import com.pakem.model.User;
import com.pakem.model.enums.JenisAcara;
import com.pakem.model.enums.StatusHonor;
import com.pakem.model.enums.StatusKesediaan;
import com.pakem.service.interfaces.IEventService;
import com.pakem.service.interfaces.IKategoriService;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
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
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Dialog Modal Pengelolaan Multi-Tim & Susunan Pemain dalam Event (Fase 6).
 * Menampilkan rincian tim, pemain terpilih, konfirmasi kesediaan, serta pengaturan honor job.
 */
public class TimEventDialog extends JDialog {

    private final IEventService eventService;
    private final IKategoriService kategoriService;
    private final Event event;
    private final User currentUser;

    private JList<TimEvent> listTim;
    private DefaultListModel<TimEvent> modelTim;
    private final List<TimEvent> timList = new ArrayList<>();

    private JTable tblPemain;
    private DefaultTableModel modelPemain;
    private final List<PemainTim> pemainList = new ArrayList<>();

    private JButton btnTambahTim;
    private JButton btnUbahTim;
    private JButton btnHapusTim;

    private JButton btnTambahPemain;
    private JButton btnUbahKesediaan;
    private JButton btnAturHonor;
    private JButton btnHapusPemain;
    private JButton btnSalinWA;

    private JLabel lblStatusTim;
    private TimEvent selectedTim = null;

    public TimEventDialog(JFrame parent, IEventService eventService, IKategoriService kategoriService,
                          Event event, User currentUser) {
        super(parent, "Kelola Tim & Pemain - " + event.getNamaAcara(), true);
        this.eventService = eventService;
        this.kategoriService = kategoriService;
        this.event = event;
        this.currentUser = currentUser;

        initUI();
        loadTimList();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setMinimumSize(new Dimension(940, 620));
        setPreferredSize(new Dimension(1020, 680));

        // 1. Top Event Header
        JPanel topHeader = new JPanel();
        topHeader.setLayout(new BoxLayout(topHeader, BoxLayout.Y_AXIS));
        topHeader.setBackground(Color.WHITE);
        topHeader.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(14, 20, 14, 20)
        ));

        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);

        JLabel lblTitle = new JLabel("Susunan Tim & Pemain: " + event.getNamaAcara());
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(15, 23, 42));

        // Badge Jenis Acara
        JLabel lblJenis = new JLabel("  " + event.getJenisAcara().getLabel() + "  ");
        lblJenis.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblJenis.setOpaque(true);
        if (event.getJenisAcara() == JenisAcara.JOB) {
            lblJenis.setBackground(new Color(238, 242, 255));
            lblJenis.setForeground(new Color(37, 99, 235));
            lblJenis.setBorder(BorderFactory.createLineBorder(new Color(199, 210, 254), 1, true));
        } else if (event.getJenisAcara() == JenisAcara.NGAYAH) {
            lblJenis.setBackground(new Color(240, 253, 244));
            lblJenis.setForeground(new Color(22, 163, 74));
            lblJenis.setBorder(BorderFactory.createLineBorder(new Color(187, 247, 208), 1, true));
        } else {
            lblJenis.setBackground(new Color(254, 243, 199));
            lblJenis.setForeground(new Color(180, 83, 9));
            lblJenis.setBorder(BorderFactory.createLineBorder(new Color(252, 211, 77), 1, true));
        }

        JPanel headerLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        headerLeft.setOpaque(false);
        headerLeft.add(lblTitle);
        headerLeft.add(lblJenis);

        btnSalinWA = new JButton("📋 Salin Info Acara & Pemain (WA)");
        btnSalinWA.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSalinWA.setBackground(new Color(34, 197, 94));
        btnSalinWA.setForeground(Color.WHITE);
        btnSalinWA.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSalinWA.addActionListener(e -> salinPengumumanWA());

        titleRow.add(headerLeft, BorderLayout.WEST);
        titleRow.add(btnSalinWA, BorderLayout.EAST);

        String jam = event.getWaktu() != null ? event.getWaktu().toString() : "--:--";
        JLabel lblSub = new JLabel(String.format("📅 Tanggal: %s | ⏰ Waktu: %s | 📍 Lokasi: %s",
                event.getTanggal(), jam, event.getLokasi() != null ? event.getLokasi() : "-"));
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(new Color(100, 116, 139));

        topHeader.add(titleRow);
        topHeader.add(Box.createVerticalStrut(4));
        topHeader.add(lblSub);

        add(topHeader, BorderLayout.NORTH);

        // 2. Split View (Left: Daftar Tim, Right: Tabel Pemain)
        JPanel leftPanel = createLeftTimPanel();
        JPanel rightPanel = createRightPemainPanel();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(280);
        splitPane.setDividerSize(6);
        splitPane.setBorder(null);

        add(splitPane, BorderLayout.CENTER);

        // 3. Footer Close Button
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 10));
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));

        JButton btnTutup = new JButton("Tutup");
        btnTutup.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnTutup.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTutup.addActionListener(e -> dispose());
        footer.add(btnTutup);

        add(footer, BorderLayout.SOUTH);
    }

    private JPanel createLeftTimPanel() {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setBackground(new Color(248, 250, 252));
        pnl.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(226, 232, 240)));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(12, 14, 10, 14));

        JLabel lbl = new JLabel("Daftar Tim Acara");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(new Color(30, 41, 59));
        header.add(lbl, BorderLayout.WEST);

        btnTambahTim = new JButton("+ Tim");
        btnTambahTim.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnTambahTim.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTambahTim.setVisible(currentUser.canManageEvent());
        btnTambahTim.addActionListener(e -> openTambahTimDialog());
        header.add(btnTambahTim, BorderLayout.EAST);

        pnl.add(header, BorderLayout.NORTH);

        modelTim = new DefaultListModel<>();
        listTim = new JList<>(modelTim);
        listTim.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        listTim.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listTim.setFixedCellHeight(38);
        listTim.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectedTim = listTim.getSelectedValue();
                onTimSelected();
            }
        });

        pnl.add(new JScrollPane(listTim), BorderLayout.CENTER);

        // Tim Action Buttons
        JPanel timActions = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 8));
        timActions.setOpaque(false);
        timActions.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));

        btnUbahTim = new JButton("Ubah");
        btnUbahTim.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnUbahTim.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnUbahTim.setEnabled(false);
        btnUbahTim.setVisible(currentUser.canManageEvent());
        btnUbahTim.addActionListener(e -> openUbahTimDialog());

        btnHapusTim = new JButton("Hapus");
        btnHapusTim.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnHapusTim.setForeground(new Color(220, 38, 38));
        btnHapusTim.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnHapusTim.setEnabled(false);
        btnHapusTim.setVisible(currentUser.canManageEvent());
        btnHapusTim.addActionListener(e -> deleteSelectedTim());

        timActions.add(btnUbahTim);
        timActions.add(btnHapusTim);

        pnl.add(timActions, BorderLayout.SOUTH);

        return pnl;
    }

    private JPanel createRightPemainPanel() {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setBackground(Color.WHITE);

        // Header Panel Pemain & Action Toolbar
        JPanel topPemain = new JPanel(new BorderLayout());
        topPemain.setOpaque(false);
        topPemain.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));

        lblStatusTim = new JLabel("Pilih salah satu tim di panel kiri.");
        lblStatusTim.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblStatusTim.setForeground(new Color(30, 41, 59));
        topPemain.add(lblStatusTim, BorderLayout.WEST);

        // Action Buttons Row Pemain
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        actions.setOpaque(false);

        btnTambahPemain = new JButton("+ Pilih Pemain");
        btnTambahPemain.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnTambahPemain.setBackground(new Color(37, 99, 235));
        btnTambahPemain.setForeground(Color.WHITE);
        btnTambahPemain.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTambahPemain.setEnabled(false);
        btnTambahPemain.setVisible(currentUser.canManageEvent());
        btnTambahPemain.addActionListener(e -> openPilihPemainDialog());

        btnUbahKesediaan = new JButton("Ubah Kesediaan...");
        btnUbahKesediaan.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnUbahKesediaan.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnUbahKesediaan.setEnabled(false);
        btnUbahKesediaan.setVisible(currentUser.canRecordKesediaanPemain());
        btnUbahKesediaan.addActionListener(e -> openUbahKesediaanDialog());

        boolean canManageHonor = (event.getJenisAcara() == JenisAcara.JOB) && currentUser.canAccessHonor();
        btnAturHonor = new JButton("Atur Honor...");
        btnAturHonor.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnAturHonor.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAturHonor.setEnabled(false);
        btnAturHonor.setVisible(canManageHonor);
        btnAturHonor.addActionListener(e -> openAturHonorDialog());

        btnHapusPemain = new JButton("Hapus");
        btnHapusPemain.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnHapusPemain.setForeground(new Color(220, 38, 38));
        btnHapusPemain.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnHapusPemain.setEnabled(false);
        btnHapusPemain.setVisible(currentUser.canManageEvent());
        btnHapusPemain.addActionListener(e -> deleteSelectedPemain());

        actions.add(btnTambahPemain);
        actions.add(btnUbahKesediaan);
        if (canManageHonor) {
            actions.add(btnAturHonor);
        }
        actions.add(btnHapusPemain);

        topPemain.add(actions, BorderLayout.EAST);
        pnl.add(topPemain, BorderLayout.NORTH);

        // Tabel Pemain Terpilih
        boolean showHonorColumns = (event.getJenisAcara() == JenisAcara.JOB) && currentUser.canAccessHonor();
        String[] cols;
        if (showHonorColumns) {
            cols = new String[]{"No", "ID", "Nama Anggota", "Kontak", "Tingkat", "Subkelompok", "Kesediaan", "Nominal Honor", "Status Honor"};
        } else {
            cols = new String[]{"No", "ID", "Nama Anggota", "Kontak", "Tingkat", "Subkelompok", "Kesediaan"};
        }

        modelPemain = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tblPemain = new JTable(modelPemain);
        tblPemain.setRowHeight(34);
        tblPemain.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblPemain.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblPemain.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblPemain.getTableHeader().setBackground(new Color(248, 250, 252));

        tblPemain.getColumnModel().getColumn(0).setPreferredWidth(40);
        tblPemain.getColumnModel().getColumn(1).setPreferredWidth(50);
        tblPemain.getColumnModel().getColumn(2).setPreferredWidth(170);
        tblPemain.getColumnModel().getColumn(3).setPreferredWidth(110);
        tblPemain.getColumnModel().getColumn(4).setPreferredWidth(100);
        tblPemain.getColumnModel().getColumn(5).setPreferredWidth(100);
        tblPemain.getColumnModel().getColumn(6).setPreferredWidth(130);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        tblPemain.getColumnModel().getColumn(0).setCellRenderer(center);
        tblPemain.getColumnModel().getColumn(1).setCellRenderer(center);

        // Renderer Status Kesediaan
        tblPemain.getColumnModel().getColumn(6).setCellRenderer(new KesediaanBadgeRenderer());

        if (showHonorColumns) {
            tblPemain.getColumnModel().getColumn(7).setPreferredWidth(120);
            tblPemain.getColumnModel().getColumn(8).setPreferredWidth(130);

            DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
            rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
            tblPemain.getColumnModel().getColumn(7).setCellRenderer(rightRenderer);
            tblPemain.getColumnModel().getColumn(8).setCellRenderer(new HonorBadgeRenderer());
        }

        tblPemain.getSelectionModel().addListSelectionListener(e -> {
            boolean has = tblPemain.getSelectedRow() != -1;
            btnUbahKesediaan.setEnabled(has && currentUser.canRecordKesediaanPemain());
            btnAturHonor.setEnabled(has && currentUser.canAccessHonor() && event.getJenisAcara() == JenisAcara.JOB);
            btnHapusPemain.setEnabled(has && currentUser.canManageEvent());
        });

        pnl.add(new JScrollPane(tblPemain), BorderLayout.CENTER);

        return pnl;
    }

    private void loadTimList() {
        modelTim.clear();
        timList.clear();

        try {
            List<TimEvent> list = eventService.getDaftarTimByEvent(event.getId());
            timList.addAll(list);

            for (TimEvent t : list) {
                modelTim.addElement(t);
            }

            if (!list.isEmpty()) {
                listTim.setSelectedIndex(0);
            } else {
                selectedTim = null;
                onTimSelected();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat tim acara: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onTimSelected() {
        boolean hasTim = selectedTim != null;
        btnUbahTim.setEnabled(hasTim && currentUser.canManageEvent());
        btnHapusTim.setEnabled(hasTim && currentUser.canManageEvent());
        btnTambahPemain.setEnabled(hasTim && currentUser.canManageEvent());

        if (hasTim) {
            lblStatusTim.setText("Pemain: " + selectedTim.getNamaTim() + " (" + selectedTim.getJumlahPemain() + " orang)");
            loadPemainList(selectedTim.getId());
        } else {
            lblStatusTim.setText("Belum ada tim yang dipilih.");
            modelPemain.setRowCount(0);
            pemainList.clear();
        }
    }

    private void loadPemainList(int timId) {
        modelPemain.setRowCount(0);
        pemainList.clear();

        try {
            List<PemainTim> list = eventService.getDaftarPemainByTim(timId);
            pemainList.addAll(list);

            boolean showHonor = (event.getJenisAcara() == JenisAcara.JOB) && currentUser.canAccessHonor();
            NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.of("id", "ID"));

            int no = 1;
            for (PemainTim p : list) {
                if (showHonor) {
                    String honorStr = p.getNominalHonor() != null ? nf.format(p.getNominalHonor()) : "-";
                    modelPemain.addRow(new Object[]{
                            no++,
                            "#" + p.getAnggotaId(),
                            p.getNamaAnggota(),
                            p.getNomorKontak() != null ? p.getNomorKontak() : "-",
                            p.getNamaKategori(),
                            p.getNamaSubkelompok(),
                            p.getStatusKesediaan(),
                            honorStr,
                            p.getStatusHonor()
                    });
                } else {
                    modelPemain.addRow(new Object[]{
                            no++,
                            "#" + p.getAnggotaId(),
                            p.getNamaAnggota(),
                            p.getNomorKontak() != null ? p.getNomorKontak() : "-",
                            p.getNamaKategori(),
                            p.getNamaSubkelompok(),
                            p.getStatusKesediaan()
                    });
                }
            }

            // Update counter pada model tim
            if (selectedTim != null) {
                selectedTim.setJumlahPemain(list.size());
                listTim.repaint();
                lblStatusTim.setText("Pemain: " + selectedTim.getNamaTim() + " (" + list.size() + " orang)");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat susunan pemain: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openTambahTimDialog() {
        String nama = JOptionPane.showInputDialog(this, "Masukkan nama tim baru (contoh: Tim Madya, Tim B):", "Tambah Tim", JOptionPane.PLAIN_MESSAGE);
        if (nama != null && !nama.trim().isEmpty()) {
            try {
                eventService.tambahTim(event.getId(), nama.trim(), null);
                loadTimList();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal menambah tim: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void openUbahTimDialog() {
        if (selectedTim == null) return;
        String nama = (String) JOptionPane.showInputDialog(this, "Ubah nama tim:", "Ubah Tim",
                JOptionPane.PLAIN_MESSAGE, null, null, selectedTim.getNamaTim());
        if (nama != null && !nama.trim().isEmpty()) {
            try {
                eventService.ubahTim(selectedTim.getId(), nama.trim(), selectedTim.getCatatan());
                loadTimList();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal mengubah tim: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelectedTim() {
        if (selectedTim == null) return;
        int conf = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin menghapus tim '" + selectedTim.getNamaTim() + "'?\n" +
                "Seluruh daftar pemain dalam tim ini akan ikut terhapus.",
                "Konfirmasi Hapus Tim", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (conf == JOptionPane.YES_OPTION) {
            try {
                eventService.hapusTim(selectedTim.getId());
                loadTimList();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal menghapus tim: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void openPilihPemainDialog() {
        if (selectedTim == null) return;
        PilihPemainDialog dlg = new PilihPemainDialog(this, eventService, kategoriService, selectedTim, event, currentUser);
        dlg.setVisible(true);
        if (dlg.isSaved()) {
            loadPemainList(selectedTim.getId());
        }
    }

    private void openUbahKesediaanDialog() {
        int row = tblPemain.getSelectedRow();
        if (row < 0 || row >= pemainList.size()) return;
        PemainTim p = pemainList.get(row);

        StatusKesediaan[] options = StatusKesediaan.values();
        StatusKesediaan sel = (StatusKesediaan) JOptionPane.showInputDialog(this,
                "Pilih status kesediaan untuk: " + p.getNamaAnggota(),
                "Ubah Status Kesediaan",
                JOptionPane.QUESTION_MESSAGE, null, options, p.getStatusKesediaan());

        if (sel != null && sel != p.getStatusKesediaan()) {
            try {
                eventService.ubahKesediaanPemain(p.getId(), sel);
                loadPemainList(selectedTim.getId());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal mengubah kesediaan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void openAturHonorDialog() {
        int row = tblPemain.getSelectedRow();
        if (row < 0 || row >= pemainList.size()) return;
        PemainTim p = pemainList.get(row);

        JTextField txtHonor = new JTextField();
        txtHonor.setText(p.getNominalHonor() != null ? p.getNominalHonor().toPlainString() : "");

        JComboBox<StatusHonor> cmbSt = new JComboBox<>(StatusHonor.values());
        cmbSt.setSelectedItem(p.getStatusHonor() != null ? p.getStatusHonor() : StatusHonor.BELUM_DIBAGIKAN);

        Object[] msg = {
                "Pemain: " + p.getNamaAnggota(),
                "Nominal Honor (Rp):", txtHonor,
                "Status Pembagian:", cmbSt
        };

        int res = JOptionPane.showConfirmDialog(this, msg, "Atur Honor Pemain", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            BigDecimal honor = null;
            String text = txtHonor.getText().trim();
            if (!text.isEmpty()) {
                try {
                    honor = new BigDecimal(text);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Nominal honor harus angka valid.", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
            StatusHonor st = (StatusHonor) cmbSt.getSelectedItem();

            try {
                eventService.ubahHonorPemain(p.getId(), honor, st, currentUser);
                loadPemainList(selectedTim.getId());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal mengatur honor: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelectedPemain() {
        int row = tblPemain.getSelectedRow();
        if (row < 0 || row >= pemainList.size()) return;
        PemainTim p = pemainList.get(row);

        int conf = JOptionPane.showConfirmDialog(this,
                "Hapus pemain '" + p.getNamaAnggota() + "' dari tim " + selectedTim.getNamaTim() + "?",
                "Konfirmasi Hapus Pemain", JOptionPane.YES_NO_OPTION);
        if (conf == JOptionPane.YES_OPTION) {
            try {
                eventService.hapusPemain(p.getId());
                loadPemainList(selectedTim.getId());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal menghapus pemain: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void salinPengumumanWA() {
        try {
            String text = eventService.formatPengumumanEvent(event.getId());
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text), null);
            JOptionPane.showMessageDialog(this,
                    "Rincian jadwal acara dan susunan pemain berhasil disalin ke Clipboard!\nSiap dibagikan ke grup WhatsApp.",
                    "Berhasil Disalin", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal menyalin pengumuman: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Renderer badge khusus status kesediaan.
     */
    private static class KesediaanBadgeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            lbl.setHorizontalAlignment(CENTER);

            if (value instanceof StatusKesediaan status) {
                lbl.setText("  " + status.getLabel() + "  ");
                lbl.setOpaque(true);

                if (!isSelected) {
                    switch (status) {
                        case BERSEDIA -> {
                            lbl.setBackground(new Color(220, 252, 231));
                            lbl.setForeground(new Color(21, 128, 61));
                        }
                        case BELUM_DITANYA -> {
                            lbl.setBackground(new Color(254, 243, 199));
                            lbl.setForeground(new Color(180, 83, 9));
                        }
                        case TIDAK_BERSEDIA -> {
                            lbl.setBackground(new Color(254, 226, 226));
                            lbl.setForeground(new Color(220, 38, 38));
                        }
                    }
                }
            }
            return lbl;
        }
    }

    /**
     * Renderer badge khusus status honor.
     */
    private static class HonorBadgeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            lbl.setHorizontalAlignment(CENTER);

            if (value instanceof StatusHonor status) {
                lbl.setText("  " + status.getLabel() + "  ");
                lbl.setOpaque(true);

                if (!isSelected) {
                    if (status == StatusHonor.SUDAH_DIBAGIKAN) {
                        lbl.setBackground(new Color(220, 252, 231));
                        lbl.setForeground(new Color(21, 128, 61));
                    } else {
                        lbl.setBackground(new Color(254, 243, 199));
                        lbl.setForeground(new Color(180, 83, 9));
                    }
                }
            } else {
                lbl.setText("-");
            }
            return lbl;
        }
    }
}
