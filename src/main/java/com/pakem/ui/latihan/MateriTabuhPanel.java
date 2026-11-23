package com.pakem.ui.latihan;

import com.pakem.model.MateriTabuh;
import com.pakem.model.User;
import com.pakem.service.interfaces.ILatihanService;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Panel utama untuk Modul Master Materi Tabuh (Repertoar / Gending).
 * Pengurus dan Pelatih memiliki hak akses penuh terhadap master materi tabuh.
 */
public class MateriTabuhPanel extends JPanel {

    private final ILatihanService latihanService;
    private final User currentUser;

    private JTable tblMateri;
    private DefaultTableModel modelMateri;
    private final List<MateriTabuh> materiList = new ArrayList<>();
    private JButton btnTambahMateri;
    private JButton btnUbahMateri;
    private JButton btnHapusMateri;

    public MateriTabuhPanel(ILatihanService latihanService, User currentUser) {
        this.latihanService = latihanService;
        this.currentUser = currentUser;

        initUI();
        loadMateriList();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(241, 245, 249));

        // Top Bar Header
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(16, 24, 16, 24)
        ));

        JPanel titleTextPanel = new JPanel();
        titleTextPanel.setLayout(new BoxLayout(titleTextPanel, BoxLayout.Y_AXIS));
        titleTextPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Master Materi Tabuh");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(15, 23, 42));

        JLabel lblSubtitle = new JLabel("Kelola daftar nama materi / gending tabuh yang dipelajari di sanggar.");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitle.setForeground(new Color(100, 116, 139));

        titleTextPanel.add(lblTitle);
        titleTextPanel.add(Box.createVerticalStrut(2));
        titleTextPanel.add(lblSubtitle);

        topBar.add(titleTextPanel, BorderLayout.WEST);

        if (!currentUser.canManageMateriTabuh()) {
            JPanel bannerPelatih = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            bannerPelatih.setOpaque(false);
            JLabel lblPelatih = new JLabel("  Mode Pelatih: Hanya Melihat Materi  ");
            lblPelatih.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lblPelatih.setOpaque(true);
            lblPelatih.setBackground(new Color(254, 243, 199));
            lblPelatih.setForeground(new Color(180, 83, 9));
            lblPelatih.setBorder(BorderFactory.createLineBorder(new Color(252, 211, 77), 1, true));
            bannerPelatih.add(lblPelatih);
            topBar.add(bannerPelatih, BorderLayout.EAST);
        }

        add(topBar, BorderLayout.NORTH);

        // Table Area
        String[] columns = {"ID", "Nama Materi / Gending", "Deskripsi"};
        modelMateri = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tblMateri = new JTable(modelMateri);
        tblMateri.setRowHeight(36);
        tblMateri.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblMateri.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblMateri.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblMateri.getTableHeader().setBackground(new Color(248, 250, 252));

        tblMateri.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblMateri.getColumnModel().getColumn(1).setPreferredWidth(200);
        tblMateri.getColumnModel().getColumn(2).setPreferredWidth(400);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tblMateri.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);

        tblMateri.getSelectionModel().addListSelectionListener(e -> {
            boolean hasSelection = tblMateri.getSelectedRow() != -1;
            btnUbahMateri.setEnabled(hasSelection && currentUser.canManageMateriTabuh());
            btnHapusMateri.setEnabled(hasSelection && currentUser.canManageMateriTabuh());
        });

        JScrollPane scrollTable = new JScrollPane(tblMateri);
        scrollTable.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        add(scrollTable, BorderLayout.CENTER);

        // Action Buttons Bottom
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
        actionPanel.setBackground(Color.WHITE);
        actionPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));

        btnTambahMateri = new JButton("+ Tambah Materi");
        btnTambahMateri.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnTambahMateri.setBackground(new Color(37, 99, 235));
        btnTambahMateri.setForeground(Color.WHITE);
        btnTambahMateri.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTambahMateri.setVisible(currentUser.canManageMateriTabuh());
        btnTambahMateri.addActionListener(e -> openAddMateriDialog());

        btnUbahMateri = new JButton("Ubah Nama");
        btnUbahMateri.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnUbahMateri.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnUbahMateri.setEnabled(false);
        btnUbahMateri.setVisible(currentUser.canManageMateriTabuh());
        btnUbahMateri.addActionListener(e -> openEditMateriDialog());

        btnHapusMateri = new JButton("Hapus Materi");
        btnHapusMateri.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnHapusMateri.setForeground(new Color(220, 38, 38));
        btnHapusMateri.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnHapusMateri.setEnabled(false);
        btnHapusMateri.setVisible(currentUser.canManageMateriTabuh());
        btnHapusMateri.addActionListener(e -> deleteMateri());

        actionPanel.add(btnTambahMateri);
        actionPanel.add(btnUbahMateri);
        actionPanel.add(btnHapusMateri);

        add(actionPanel, BorderLayout.SOUTH);
    }

    private void loadMateriList() {
        modelMateri.setRowCount(0);
        materiList.clear();

        try {
            List<MateriTabuh> data = latihanService.getSemuaMateriTabuh();
            materiList.addAll(data);

            for (MateriTabuh m : data) {
                modelMateri.addRow(new Object[]{
                        m.getId(),
                        m.getNama(),
                        m.getDeskripsi() != null ? m.getDeskripsi() : "-"
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat materi tabuh: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openAddMateriDialog() {
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        MateriTabuhFormDialog dialog = new MateriTabuhFormDialog(parent, latihanService, null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            loadMateriList();
        }
    }

    private void openEditMateriDialog() {
        int row = tblMateri.getSelectedRow();
        if (row < 0 || row >= materiList.size()) return;
        MateriTabuh selectedMateri = materiList.get(row);

        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        MateriTabuhFormDialog dialog = new MateriTabuhFormDialog(parent, latihanService, selectedMateri);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            loadMateriList();
        }
    }

    private void deleteMateri() {
        int row = tblMateri.getSelectedRow();
        if (row < 0 || row >= materiList.size()) return;
        MateriTabuh selectedMateri = materiList.get(row);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin menghapus materi tabuh '" + selectedMateri.getNama() + "'?\n" +
                "Materi yang sudah digunakan dalam sesi latihan tidak dapat dihapus.",
                "Konfirmasi Hapus Materi Tabuh",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                latihanService.hapusMateriTabuh(selectedMateri.getId());
                JOptionPane.showMessageDialog(this, "Materi tabuh berhasil dihapus.", "Berhasil", JOptionPane.INFORMATION_MESSAGE);
                loadMateriList();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal menghapus: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
