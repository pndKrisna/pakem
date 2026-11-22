package com.pakem.ui.anggota;

import com.pakem.model.Anggota;
import com.pakem.model.RiwayatTingkat;
import com.pakem.service.interfaces.IAnggotaService;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Dialog untuk menampilkan riwayat perubahan kategori tingkat kemampuan anggota.
 */
public class RiwayatTingkatDialog extends JDialog {

    private final IAnggotaService anggotaService;
    private final Anggota anggota;

    private JTable table;
    private DefaultTableModel tableModel;

    public RiwayatTingkatDialog(JFrame parent, IAnggotaService anggotaService, Anggota anggota) {
        super(parent, "Riwayat Tingkat: " + anggota.getNamaLengkap(), true);
        this.anggotaService = anggotaService;
        this.anggota = anggota;

        initUI();
        loadData();
    }

    private void initUI() {
        setSize(700, 440);
        setMinimumSize(new Dimension(580, 360));
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());

        // Header Panel
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(16, 20, 16, 20)
        ));

        JLabel lblTitle = new JLabel("Riwayat Perkembangan Tingkat Kemampuan");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(new Color(30, 41, 59));

        JLabel lblSub = new JLabel("Anggota: " + anggota.getNamaLengkap() + "  |  Tingkat Saat Ini: " + anggota.getNamaKategori());
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(new Color(100, 116, 139));

        header.add(lblTitle);
        header.add(Box.createVerticalStrut(4));
        header.add(lblSub);
        add(header, BorderLayout.NORTH);

        // Table Panel
        String[] columns = {"Tanggal Perubahan", "Tingkat Sebelumnya", "Tingkat Baru", "Keterangan / Alasan"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(32);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(248, 250, 252));
        table.getTableHeader().setForeground(new Color(71, 85, 105));

        // Column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(120);
        table.getColumnModel().getColumn(1).setPreferredWidth(130);
        table.getColumnModel().getColumn(2).setPreferredWidth(130);
        table.getColumnModel().getColumn(3).setPreferredWidth(260);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        add(scrollPane, BorderLayout.CENTER);

        // Footer Button Panel
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 12));
        footer.setBackground(new Color(248, 250, 252));
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));

        JButton btnClose = new JButton("Tutup");
        btnClose.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> dispose());
        footer.add(btnClose);

        add(footer, BorderLayout.SOUTH);
    }

    private void loadData() {
        tableModel.setRowCount(0);
        try {
            List<RiwayatTingkat> list = anggotaService.getRiwayatTingkatAnggota(anggota.getId());
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            if (list.isEmpty()) {
                tableModel.addRow(new Object[]{"-", "-", anggota.getNamaKategori(), "(Belum ada catatan perubahan tingkat)"});
            } else {
                for (RiwayatTingkat r : list) {
                    String tglStr = r.getTanggalPerubahan() != null ? r.getTanggalPerubahan().format(dtf) : "-";
                    tableModel.addRow(new Object[]{
                            tglStr,
                            r.getNamaTingkatLama(),
                            r.getNamaTingkatBaru(),
                            r.getKeterangan()
                    });
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat riwayat: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
