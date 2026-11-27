package com.pakem.ui.event;

import com.pakem.model.Event;
import com.pakem.model.enums.JenisAcara;
import com.pakem.service.interfaces.IEventService;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Component;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * Dialog Modal Tambah / Ubah Acara Pertunjukan (Fase 6).
 */
public class EventFormDialog extends JDialog {

    private final IEventService eventService;
    private final Event eventToEdit;
    private boolean saved = false;

    private JTextField txtNamaAcara;
    private JComboBox<JenisAcara> cmbJenisAcara;
    private JTextField txtTanggal;
    private JSpinner spinnerWaktu;
    private JTextField txtLokasi;
    private JTextArea txtCatatan;

    public EventFormDialog(JFrame parent, IEventService eventService, Event eventToEdit) {
        super(parent, eventToEdit == null ? "Tambah Acara Pertunjukan" : "Ubah Acara Pertunjukan", true);
        this.eventService = eventService;
        this.eventToEdit = eventToEdit;

        initUI();
        populateData();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setResizable(false);

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(16, 20, 16, 20)
        ));

        JLabel lblTitle = new JLabel(eventToEdit == null ? "Tambah Acara Baru" : "Ubah Data Acara");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(new Color(15, 23, 42));

        JLabel lblDesc = new JLabel("Isi informasi detail acara pentas, ngayah, atau ujian sanggar.");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDesc.setForeground(new Color(100, 116, 139));

        JPanel headerText = new JPanel();
        headerText.setLayout(new BoxLayout(headerText, BoxLayout.Y_AXIS));
        headerText.setOpaque(false);
        headerText.add(lblTitle);
        headerText.add(Box.createVerticalStrut(2));
        headerText.add(lblDesc);

        headerPanel.add(headerText, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // Form Body
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(new Color(248, 250, 252));
        formPanel.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        txtNamaAcara = new JTextField();
        txtNamaAcara.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtNamaAcara.setPreferredSize(new Dimension(360, 32));

        cmbJenisAcara = new JComboBox<>(JenisAcara.values());
        cmbJenisAcara.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmbJenisAcara.setPreferredSize(new Dimension(360, 32));

        txtTanggal = new JTextField();
        txtTanggal.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtTanggal.setPreferredSize(new Dimension(360, 32));
        txtTanggal.putClientProperty("JTextField.placeholderText", "Format: YYYY-MM-DD");

        // Spinner Waktu
        SpinnerDateModel timeModel = new SpinnerDateModel();
        spinnerWaktu = new JSpinner(timeModel);
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(spinnerWaktu, "HH:mm");
        spinnerWaktu.setEditor(timeEditor);
        spinnerWaktu.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        spinnerWaktu.setPreferredSize(new Dimension(360, 32));

        txtLokasi = new JTextField();
        txtLokasi.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtLokasi.setPreferredSize(new Dimension(360, 32));
        txtLokasi.putClientProperty("JTextField.placeholderText", "Contoh: Pura Dalem Pengembak / Hotel Hyatt");

        txtCatatan = new JTextArea(3, 20);
        txtCatatan.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtCatatan.setLineWrap(true);
        txtCatatan.setWrapStyleWord(true);
        JScrollPane scrollCatatan = new JScrollPane(txtCatatan);

        addFormField(formPanel, "Nama Acara *", txtNamaAcara);
        addFormField(formPanel, "Jenis Acara *", cmbJenisAcara);
        addFormField(formPanel, "Tanggal Acara (YYYY-MM-DD) *", txtTanggal);
        addFormField(formPanel, "Waktu Acara", spinnerWaktu);
        addFormField(formPanel, "Lokasi Acara", txtLokasi);
        addFormField(formPanel, "Catatan Tambahan", scrollCatatan);

        add(formPanel, BorderLayout.CENTER);

        // Buttons Footer
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));

        JButton btnBatal = new JButton("Batal");
        btnBatal.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnBatal.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBatal.addActionListener(e -> dispose());

        JButton btnSimpan = new JButton("Simpan Acara");
        btnSimpan.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSimpan.setBackground(new Color(37, 99, 235));
        btnSimpan.setForeground(Color.WHITE);
        btnSimpan.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSimpan.addActionListener(e -> performSave());

        buttonPanel.add(btnBatal);
        buttonPanel.add(btnSimpan);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addFormField(JPanel parent, String labelText, Component comp) {
        JPanel pnl = new JPanel(new BorderLayout(0, 4));
        pnl.setOpaque(false);

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(new Color(51, 65, 85));

        pnl.add(lbl, BorderLayout.NORTH);
        pnl.add(comp, BorderLayout.CENTER);

        parent.add(pnl);
        parent.add(Box.createVerticalStrut(10));
    }

    private void populateData() {
        if (eventToEdit != null) {
            txtNamaAcara.setText(eventToEdit.getNamaAcara());
            cmbJenisAcara.setSelectedItem(eventToEdit.getJenisAcara());
            txtTanggal.setText(eventToEdit.getTanggal() != null ? eventToEdit.getTanggal().toString() : "");

            if (eventToEdit.getWaktu() != null) {
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY, eventToEdit.getWaktu().getHour());
                cal.set(Calendar.MINUTE, eventToEdit.getWaktu().getMinute());
                spinnerWaktu.setValue(cal.getTime());
            }

            txtLokasi.setText(eventToEdit.getLokasi() != null ? eventToEdit.getLokasi() : "");
            txtCatatan.setText(eventToEdit.getCatatan() != null ? eventToEdit.getCatatan() : "");
        } else {
            txtTanggal.setText(LocalDate.now().toString());
        }
    }

    private void performSave() {
        String nama = txtNamaAcara.getText().trim();
        if (nama.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama acara wajib diisi.", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
            txtNamaAcara.requestFocus();
            return;
        }

        JenisAcara jenis = (JenisAcara) cmbJenisAcara.getSelectedItem();
        if (jenis == null) {
            JOptionPane.showMessageDialog(this, "Jenis acara wajib dipilih.", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate tanggal;
        try {
            tanggal = LocalDate.parse(txtTanggal.getText().trim());
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Format tanggal salah. Gunakan format YYYY-MM-DD.", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
            txtTanggal.requestFocus();
            return;
        }

        Date wktDate = (Date) spinnerWaktu.getValue();
        LocalTime waktu = (wktDate != null)
                ? wktDate.toInstant().atZone(ZoneId.systemDefault()).toLocalTime()
                : null;

        String lokasi = txtLokasi.getText().trim();
        String catatan = txtCatatan.getText().trim();

        try {
            if (eventToEdit == null) {
                Event newEvent = new Event(0, nama, jenis, tanggal, waktu,
                        lokasi.isEmpty() ? null : lokasi,
                        catatan.isEmpty() ? null : catatan);
                eventService.simpanEvent(newEvent);
                JOptionPane.showMessageDialog(this, "Acara baru berhasil disimpan beserta Tim Utama!", "Berhasil", JOptionPane.INFORMATION_MESSAGE);
            } else {
                eventToEdit.setNamaAcara(nama);
                eventToEdit.setJenisAcara(jenis);
                eventToEdit.setTanggal(tanggal);
                eventToEdit.setWaktu(waktu);
                eventToEdit.setLokasi(lokasi.isEmpty() ? null : lokasi);
                eventToEdit.setCatatan(catatan.isEmpty() ? null : catatan);
                eventService.ubahEvent(eventToEdit);
                JOptionPane.showMessageDialog(this, "Data acara berhasil diperbarui!", "Berhasil", JOptionPane.INFORMATION_MESSAGE);
            }
            saved = true;
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan acara: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() {
        return saved;
    }
}
