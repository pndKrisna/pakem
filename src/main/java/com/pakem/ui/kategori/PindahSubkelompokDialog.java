package com.pakem.ui.kategori;

import com.pakem.model.Anggota;
import com.pakem.model.KategoriTingkat;
import com.pakem.model.Subkelompok;
import com.pakem.service.interfaces.IKategoriService;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import java.util.Objects;

/**
 * Dialog untuk memindahkan anggota antar-subkelompok secara manual.
 */
public class PindahSubkelompokDialog extends JDialog {

    private final IKategoriService kategoriService;
    private final Anggota anggota;
    private final KategoriTingkat kategori;
    private boolean saved = false;

    private JComboBox<SubItem> cmbSubkelompok;

    private static class SubItem {
        final Integer id;
        final String label;

        SubItem(Integer id, String label) {
            this.id = id;
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    public PindahSubkelompokDialog(JFrame parent, IKategoriService kategoriService,
                                  Anggota anggota, KategoriTingkat kategori) {
        super(parent, "Penempatan Subkelompok: " + anggota.getNamaLengkap(), true);
        this.kategoriService = kategoriService;
        this.anggota = anggota;
        this.kategori = kategori;

        initUI();
        loadSubkelompokList();
    }

    public boolean isSaved() {
        return saved;
    }

    private void initUI() {
        setSize(440, 260);
        setResizable(false);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(14, 20, 14, 20)
        ));

        JLabel lblTitle = new JLabel("Pindahkan Penempatan Subkelompok");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(new Color(30, 41, 59));

        JLabel lblSub = new JLabel("Anggota: " + anggota.getNamaLengkap() + " (" + kategori.getNama() + ")");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(new Color(100, 116, 139));

        header.add(lblTitle);
        header.add(Box.createVerticalStrut(3));
        header.add(lblSub);
        add(header, BorderLayout.NORTH);

        // Form
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(248, 250, 252));
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 4, 8, 4);

        JLabel lblTarget = new JLabel("Pilih Subkelompok Tujuan");
        lblTarget.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTarget.setForeground(new Color(51, 65, 85));

        cmbSubkelompok = new JComboBox<>();
        cmbSubkelompok.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbSubkelompok.setPreferredSize(new Dimension(220, 32));

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.45;
        form.add(lblTarget, gbc);
        gbc.gridx = 1; gbc.weightx = 0.55;
        form.add(cmbSubkelompok, gbc);

        add(form, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));

        JButton btnBatal = new JButton("Batal");
        btnBatal.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnBatal.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBatal.addActionListener(e -> dispose());

        JButton btnSimpan = new JButton("Pindahkan");
        btnSimpan.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSimpan.setBackground(new Color(37, 99, 235));
        btnSimpan.setForeground(Color.WHITE);
        btnSimpan.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSimpan.addActionListener(e -> save());

        footer.add(btnBatal);
        footer.add(btnSimpan);
        add(footer, BorderLayout.SOUTH);
    }

    private void loadSubkelompokList() {
        DefaultComboBoxModel<SubItem> model = new DefaultComboBoxModel<>();
        model.addElement(new SubItem(null, "-- Tanpa Subkelompok --"));

        try {
            List<Subkelompok> subs = kategoriService.getSubkelompokByKategori(kategori.getId());
            int selectIdx = 0;
            for (int i = 0; i < subs.size(); i++) {
                Subkelompok s = subs.get(i);
                model.addElement(new SubItem(s.getId(), s.getNama()));
                if (Objects.equals(s.getId(), anggota.getSubkelompokId())) {
                    selectIdx = i + 1;
                }
            }
            cmbSubkelompok.setModel(model);
            cmbSubkelompok.setSelectedIndex(selectIdx);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat subkelompok: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void save() {
        SubItem item = (SubItem) cmbSubkelompok.getSelectedItem();
        Integer newSubId = item != null ? item.id : null;

        try {
            kategoriService.pindahAnggotaSubkelompok(anggota.getId(), newSubId);
            this.saved = true;
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
