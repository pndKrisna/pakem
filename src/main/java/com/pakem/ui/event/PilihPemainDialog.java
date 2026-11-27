package com.pakem.ui.event;

import com.pakem.model.Anggota;
import com.pakem.model.Event;
import com.pakem.model.KategoriTingkat;
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
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
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
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Dialog Pemilihan Pemain Baru ke dalam Tim Acara (Fase 6).
 * Mendukung pemilihan pemain fleksibel lintas tingkat kemampuan sanggar.
 * Hanya anggota dengan status AKTIF yang ditampilkan.
 */
public class PilihPemainDialog extends JDialog {

    private final IEventService eventService;
    private final IKategoriService kategoriService;
    private final TimEvent tim;
    private final Event event;
    private final User currentUser;
    private boolean saved = false;

    private JComboBox<KategoriTingkat> cmbKategori;
    private JTable tblAnggota;
    private DefaultTableModel modelAnggota;
    private final List<Anggota> masterAnggotaList = new ArrayList<>();
    private final List<Anggota> filteredAnggotaList = new ArrayList<>();

    private JComboBox<StatusKesediaan> cmbKesediaan;
    private JTextField txtNominalHonor;
    private JComboBox<StatusHonor> cmbStatusHonor;
    private JPanel panelHonor;

    public PilihPemainDialog(JDialog parent, IEventService eventService, IKategoriService kategoriService,
                             TimEvent tim, Event event, User currentUser) {
        super(parent, "Pilih Pemain - " + tim.getNamaTim(), true);
        this.eventService = eventService;
        this.kategoriService = kategoriService;
        this.tim = tim;
        this.event = event;
        this.currentUser = currentUser;

        initUI();
        loadKategoriFilter();
        loadAnggotaData();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setMinimumSize(new Dimension(680, 520));
        setPreferredSize(new Dimension(720, 560));

        // Top Header
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(14, 20, 14, 20)
        ));

        JLabel lblTitle = new JLabel("Pilih Pemain untuk " + tim.getNamaTim());
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(new Color(15, 23, 42));

        JLabel lblDesc = new JLabel("Pilih anggota aktif sanggar. Pemilihan bersifat fleksibel lintas tingkat.");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDesc.setForeground(new Color(100, 116, 139));

        topPanel.add(lblTitle);
        topPanel.add(Box.createVerticalStrut(2));
        topPanel.add(lblDesc);
        topPanel.add(Box.createVerticalStrut(10));

        // Filter Bar Kategori
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filterBar.setOpaque(false);

        JLabel lblFilter = new JLabel("Filter Kategori:");
        lblFilter.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblFilter.setForeground(new Color(51, 65, 85));

        cmbKategori = new JComboBox<>();
        cmbKategori.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmbKategori.setPreferredSize(new Dimension(180, 30));
        cmbKategori.addActionListener(e -> applyFilter());

        filterBar.add(lblFilter);
        filterBar.add(cmbKategori);
        topPanel.add(filterBar);

        add(topPanel, BorderLayout.NORTH);

        // Center Table
        String[] cols = {"ID", "Nama Lengkap", "Kategori Tingkat", "Subkelompok", "Nomor Kontak"};
        modelAnggota = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tblAnggota = new JTable(modelAnggota);
        tblAnggota.setRowHeight(34);
        tblAnggota.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblAnggota.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblAnggota.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblAnggota.getTableHeader().setBackground(new Color(248, 250, 252));

        tblAnggota.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblAnggota.getColumnModel().getColumn(1).setPreferredWidth(200);
        tblAnggota.getColumnModel().getColumn(2).setPreferredWidth(120);
        tblAnggota.getColumnModel().getColumn(3).setPreferredWidth(120);
        tblAnggota.getColumnModel().getColumn(4).setPreferredWidth(130);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        tblAnggota.getColumnModel().getColumn(0).setCellRenderer(center);

        add(new JScrollPane(tblAnggota), BorderLayout.CENTER);

        // Bottom Configuration & Actions
        JPanel bottomContainer = new JPanel();
        bottomContainer.setLayout(new BoxLayout(bottomContainer, BoxLayout.Y_AXIS));
        bottomContainer.setBackground(Color.WHITE);
        bottomContainer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));

        // Form Kesediaan & Honor
        JPanel formInput = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        formInput.setOpaque(false);

        JLabel lblKes = new JLabel("Status Kesediaan:");
        lblKes.setFont(new Font("Segoe UI", Font.BOLD, 12));
        cmbKesediaan = new JComboBox<>(StatusKesediaan.values());
        cmbKesediaan.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmbKesediaan.setPreferredSize(new Dimension(140, 30));

        formInput.add(lblKes);
        formInput.add(cmbKesediaan);

        // Honor panel (Hanya untuk JOB dan role Pengurus)
        boolean canSeeHonor = (event.getJenisAcara() == JenisAcara.JOB) && (currentUser != null && currentUser.canAccessHonor());
        panelHonor = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelHonor.setOpaque(false);

        if (canSeeHonor) {
            JLabel lblHonor = new JLabel("Nominal Honor (Rp):");
            lblHonor.setFont(new Font("Segoe UI", Font.BOLD, 12));
            txtNominalHonor = new JTextField();
            txtNominalHonor.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            txtNominalHonor.setPreferredSize(new Dimension(120, 30));
            txtNominalHonor.putClientProperty("JTextField.placeholderText", "Contoh: 150000");

            JLabel lblStHonor = new JLabel("Status Honor:");
            lblStHonor.setFont(new Font("Segoe UI", Font.BOLD, 12));
            cmbStatusHonor = new JComboBox<>(StatusHonor.values());
            cmbStatusHonor.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            cmbStatusHonor.setPreferredSize(new Dimension(150, 30));

            panelHonor.add(lblHonor);
            panelHonor.add(txtNominalHonor);
            panelHonor.add(lblStHonor);
            panelHonor.add(cmbStatusHonor);

            formInput.add(panelHonor);
        }

        bottomContainer.add(formInput);

        // Action Buttons Row
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnRow.setOpaque(false);

        JButton btnBatal = new JButton("Tutup");
        btnBatal.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnBatal.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBatal.addActionListener(e -> dispose());

        JButton btnTambah = new JButton("+ Tambahkan ke Tim");
        btnTambah.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnTambah.setBackground(new Color(37, 99, 235));
        btnTambah.setForeground(Color.WHITE);
        btnTambah.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTambah.addActionListener(e -> performTambahPemain());

        btnRow.add(btnBatal);
        btnRow.add(btnTambah);
        bottomContainer.add(btnRow);

        add(bottomContainer, BorderLayout.SOUTH);
    }

    private void loadKategoriFilter() {
        cmbKategori.removeAllItems();
        cmbKategori.addItem(new KategoriTingkat(0, "Semua Kategori (Lintas Tingkat)", 0));
        try {
            List<KategoriTingkat> allKat = kategoriService.getSemuaKategori();
            for (KategoriTingkat k : allKat) {
                cmbKategori.addItem(k);
            }
        } catch (Exception ex) {
            System.err.println("Gagal memuat kategori: " + ex.getMessage());
        }
    }

    private void loadAnggotaData() {
        masterAnggotaList.clear();
        try {
            List<Anggota> data = eventService.getCalonPemainTersedia(tim.getId());
            masterAnggotaList.addAll(data);
            applyFilter();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat calon pemain: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void applyFilter() {
        modelAnggota.setRowCount(0);
        filteredAnggotaList.clear();

        Object sel = cmbKategori.getSelectedItem();
        int katId = (sel instanceof KategoriTingkat k) ? k.getId() : 0;

        for (Anggota a : masterAnggotaList) {
            if (katId == 0 || (a.getKategoriTingkatId() != null && a.getKategoriTingkatId() == katId)) {
                filteredAnggotaList.add(a);
                modelAnggota.addRow(new Object[]{
                        "#" + a.getId(),
                        a.getNamaLengkap(),
                        a.getNamaKategori(),
                        a.getNamaSubkelompok(),
                        a.getNomorKontak() != null ? a.getNomorKontak() : "-"
                });
            }
        }
    }

    private void performTambahPemain() {
        int row = tblAnggota.getSelectedRow();
        if (row < 0 || row >= filteredAnggotaList.size()) {
            JOptionPane.showMessageDialog(this, "Silakan pilih salah satu anggota pada tabel terlebih dahulu.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Anggota selectedAnggota = filteredAnggotaList.get(row);
        StatusKesediaan kesediaan = (StatusKesediaan) cmbKesediaan.getSelectedItem();

        BigDecimal honor = null;
        StatusHonor statusHonor = null;

        if (event.getJenisAcara() == JenisAcara.JOB && currentUser != null && currentUser.canAccessHonor()) {
            String honText = txtNominalHonor.getText().trim();
            if (!honText.isEmpty()) {
                try {
                    honor = new BigDecimal(honText);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Format nominal honor harus angka valid.", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
                    txtNominalHonor.requestFocus();
                    return;
                }
            }
            statusHonor = (StatusHonor) cmbStatusHonor.getSelectedItem();
        }

        try {
            eventService.tambahPemain(tim.getId(), selectedAnggota.getId(), kesediaan, honor, statusHonor, currentUser);
            saved = true;
            JOptionPane.showMessageDialog(this,
                    "Anggota '" + selectedAnggota.getNamaLengkap() + "' berhasil ditambahkan ke " + tim.getNamaTim() + "!",
                    "Berhasil", JOptionPane.INFORMATION_MESSAGE);

            // Refresh sisa calon pemain
            loadAnggotaData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal menambahkan pemain: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() {
        return saved;
    }
}
