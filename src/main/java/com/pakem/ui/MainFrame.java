package com.pakem.ui;

import com.pakem.model.User;
import com.pakem.model.enums.RoleUser;
import com.pakem.service.impl.AnggotaServiceImpl;
import com.pakem.service.interfaces.IAnggotaService;
import com.pakem.service.interfaces.IAuthService;
import com.pakem.ui.anggota.AnggotaListPanel;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * Jendela utama aplikasi Pakem.
 * Mengelola transisi login dan antarmuka aplikasi dengan menu dinamis berbasis hak akses polimorfik.
 */
public class MainFrame extends JFrame {

    private static final String CARD_LOGIN = "CARD_LOGIN";
    private static final String CARD_APP = "CARD_APP";

    private final IAuthService authService;
    private final IAnggotaService anggotaService;
    private final CardLayout mainCardLayout;
    private final JPanel mainContainer;

    private LoginPanel loginPanel;
    private JPanel appPanel;
    private JPanel contentArea;
    private CardLayout contentCardLayout;

    private final List<JButton> sidebarButtons = new ArrayList<>();

    public MainFrame(IAuthService authService) {
        this(authService, new AnggotaServiceImpl());
    }

    public MainFrame(IAuthService authService, IAnggotaService anggotaService) {
        this.authService = authService;
        this.anggotaService = anggotaService;
        this.mainCardLayout = new CardLayout();
        this.mainContainer = new JPanel(mainCardLayout);

        initFrame();
    }

    private void initFrame() {
        setTitle("Pakem - Sistem Administrasi Sanggar Seni Tabuh");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(980, 640));
        setPreferredSize(new Dimension(1120, 720));
        setLocationRelativeTo(null);

        // Buat Login Panel
        loginPanel = new LoginPanel(authService, this::onLoginSuccess);
        mainContainer.add(loginPanel, CARD_LOGIN);

        // Placeholder App Panel (akan di-render ulang saat user login)
        appPanel = new JPanel(new BorderLayout());
        mainContainer.add(appPanel, CARD_APP);

        add(mainContainer);

        // Default tampilkan halaman login
        mainCardLayout.show(mainContainer, CARD_LOGIN);
    }

    private void onLoginSuccess(User user) {
        buildAppView(user);
        mainCardLayout.show(mainContainer, CARD_APP);
    }

    private void buildAppView(User user) {
        appPanel.removeAll();

        // 1. Top Bar (Header)
        JPanel topBar = createTopBar(user);
        appPanel.add(topBar, BorderLayout.NORTH);

        // 2. Sidebar Navigasi
        sidebarButtons.clear();
        JPanel sidebar = createSidebar(user);
        appPanel.add(sidebar, BorderLayout.WEST);

        // 3. Area Konten Utama
        contentCardLayout = new CardLayout();
        contentArea = new JPanel(contentCardLayout);

        // Tambah Halaman Dashboard
        contentArea.add(createDashboardPanel(user), "NAV_DASHBOARD");

        // Modul Fase 2: Manajemen Data Anggota Aktif
        contentArea.add(new AnggotaListPanel(anggotaService, user), "NAV_ANGGOTA");

        // Tambah Halaman Placeholder untuk modul-modul fase selanjutnya
        contentArea.add(createPlaceholderPanel("Kategori Tingkat & Subkelompok", "Fase 3",
                user.canManageKategoriTingkat() ? "Hak Akses: Penuh (Kelola Master & Auto-Grouping)" : "Hak Akses: Terbatas (Lihat Master)"), "NAV_KATEGORI");

        contentArea.add(createPlaceholderPanel("Jadwal Latihan & Materi Tabuh", "Fase 4",
                "Hak Akses: Penuh (Buat Jadwal, Sambung Progres Materi Tabuh, Salin WA)"), "NAV_LATIHAN");

        contentArea.add(createPlaceholderPanel("Presensi Latihan", "Fase 5",
                "Hak Akses: Penuh (Pencatatan Kehadiran & Rekap Persentase Anggota Aktif)"), "NAV_PRESENSI");

        contentArea.add(createPlaceholderPanel("Pertunjukan / Event", "Fase 6",
                user.canAccessHonor() ? "Hak Akses: Penuh (Kelola Tim Lintas Tingkat, Kesediaan, & Pencatatan Honor Job)"
                                     : "Hak Akses: Terbatas (Lihat Acara & Catat Kesediaan Pemain)"), "NAV_EVENT");

        if (user.canAccessIuran()) {
            contentArea.add(createPlaceholderPanel("Iuran Bulanan", "Fase 7",
                    "Hak Akses: Penuh (Pemantauan Status Indikator Warna Tagihan & Pencatatan Bayar)"), "NAV_IURAN");
        }

        if (user.canAccessPengaturan()) {
            contentArea.add(createPlaceholderPanel("Pengaturan Sanggar", "Fase 8",
                    "Hak Akses: Penuh (Nama Sanggar, Ambang Batas Kuning/Merah, Kapasitas Kelompok)"), "NAV_PENGATURAN");
        }

        appPanel.add(contentArea, BorderLayout.CENTER);

        // Aktifkan tombol pertama (Dashboard)
        if (!sidebarButtons.isEmpty()) {
            activateSidebarButton(sidebarButtons.get(0));
            contentCardLayout.show(contentArea, "NAV_DASHBOARD");
        }

        appPanel.revalidate();
        appPanel.repaint();
    }

    private JPanel createTopBar(User user) {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(Color.WHITE);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(12, 24, 12, 24)
        ));

        // Brand Title
        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        brandPanel.setOpaque(false);

        JLabel lblLogo = new JLabel("PAKEM");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblLogo.setForeground(new Color(30, 41, 59));

        JLabel lblSanggar = new JLabel("|  Sanggar Seni Tabuh");
        lblSanggar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSanggar.setForeground(new Color(100, 116, 139));

        brandPanel.add(lblLogo);
        brandPanel.add(lblSanggar);

        // User Profile & Logout
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        userPanel.setOpaque(false);

        JLabel lblUsername = new JLabel(user.getUsername());
        lblUsername.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUsername.setForeground(new Color(30, 41, 59));

        // Role Badge Polimorfik
        boolean isPengurus = user.getRole() == RoleUser.PENGURUS;
        JLabel lblRoleBadge = new JLabel("  " + user.getRole().getLabel() + "  ");
        lblRoleBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblRoleBadge.setOpaque(true);
        if (isPengurus) {
            lblRoleBadge.setBackground(new Color(238, 242, 255)); // Soft indigo
            lblRoleBadge.setForeground(new Color(79, 70, 229));
            lblRoleBadge.setBorder(BorderFactory.createLineBorder(new Color(199, 210, 254), 1, true));
        } else {
            lblRoleBadge.setBackground(new Color(240, 253, 244)); // Soft emerald
            lblRoleBadge.setForeground(new Color(22, 163, 74));
            lblRoleBadge.setBorder(BorderFactory.createLineBorder(new Color(187, 247, 208), 1, true));
        }

        JButton btnLogout = new JButton("Keluar");
        btnLogout.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnLogout.setBackground(new Color(248, 250, 252));
        btnLogout.setForeground(new Color(71, 85, 105));
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> performLogout());

        userPanel.add(lblUsername);
        userPanel.add(lblRoleBadge);
        userPanel.add(Box.createHorizontalStrut(8));
        userPanel.add(btnLogout);

        bar.add(brandPanel, BorderLayout.WEST);
        bar.add(userPanel, BorderLayout.EAST);
        return bar;
    }

    private JPanel createSidebar(User user) {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(248, 250, 252));
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(226, 232, 240)));

        sidebar.add(Box.createVerticalStrut(16));

        addNavSectionLabel(sidebar, "OPERASIONAL UTAMA");
        addSidebarButton(sidebar, "Dashboard", "NAV_DASHBOARD");
        addSidebarButton(sidebar, "Data Anggota", "NAV_ANGGOTA");
        addSidebarButton(sidebar, "Kategori & Kelompok", "NAV_KATEGORI");
        addSidebarButton(sidebar, "Jadwal Latihan", "NAV_LATIHAN");
        addSidebarButton(sidebar, "Presensi Latihan", "NAV_PRESENSI");
        addSidebarButton(sidebar, "Pertunjukan / Event", "NAV_EVENT");

        // Polimorfisme Hak Akses: Menu Iuran & Pengaturan hanya ditambahkan jika role mengizinkan
        if (user.canAccessIuran() || user.canAccessPengaturan()) {
            sidebar.add(Box.createVerticalStrut(14));
            addNavSectionLabel(sidebar, "ADMINISTRASI & KEUANGAN");

            if (user.canAccessIuran()) {
                addSidebarButton(sidebar, "Iuran Bulanan", "NAV_IURAN");
            }
            if (user.canAccessPengaturan()) {
                addSidebarButton(sidebar, "Pengaturan Sanggar", "NAV_PENGATURAN");
            }
        }

        sidebar.add(Box.createVerticalGlue());

        // Info Versi di bagian bawah
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setOpaque(false);
        JLabel lblVer = new JLabel("Pakem v1.0 • Java 25");
        lblVer.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblVer.setForeground(new Color(148, 163, 184));
        footer.add(lblVer);
        sidebar.add(footer);
        sidebar.add(Box.createVerticalStrut(10));

        return sidebar;
    }

    private void addNavSectionLabel(JPanel parent, String title) {
        JLabel lbl = new JLabel("  " + title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(new Color(148, 163, 184));
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        lbl.setMaximumSize(new Dimension(216, 22));
        parent.add(lbl);
        parent.add(Box.createVerticalStrut(4));
    }

    private void addSidebarButton(JPanel parent, String text, String cardName) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(216, 38));
        btn.setPreferredSize(new Dimension(216, 38));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));

        btn.addActionListener(e -> {
            activateSidebarButton(btn);
            contentCardLayout.show(contentArea, cardName);
        });

        sidebarButtons.add(btn);
        parent.add(btn);
        parent.add(Box.createVerticalStrut(4));
    }

    private void activateSidebarButton(JButton activeBtn) {
        for (JButton btn : sidebarButtons) {
            if (btn == activeBtn) {
                btn.setBackground(new Color(238, 242, 255));
                btn.setForeground(new Color(37, 99, 235));
                btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
            } else {
                btn.setBackground(new Color(248, 250, 252));
                btn.setForeground(new Color(51, 65, 85));
                btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            }
        }
    }

    private JPanel createDashboardPanel(User user) {
        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBackground(new Color(241, 245, 249));
        root.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        // Welcome Banner Card
        JPanel banner = new JPanel(new BorderLayout());
        banner.setBackground(Color.WHITE);
        banner.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
                BorderFactory.createEmptyBorder(20, 24, 20, 24)
        ));
        banner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        JPanel bannerText = new JPanel();
        bannerText.setLayout(new BoxLayout(bannerText, BoxLayout.Y_AXIS));
        bannerText.setOpaque(false);

        JLabel lblGreeting = new JLabel("Selamat Datang, " + user.getUsername() + "!");
        lblGreeting.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblGreeting.setForeground(new Color(15, 23, 42));

        JLabel lblDesc = new JLabel("Anda masuk sebagai " + user.getRole().getLabel() + ". Hak akses modul diatur secara otomatis.");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblDesc.setForeground(new Color(100, 116, 139));

        bannerText.add(lblGreeting);
        bannerText.add(Box.createVerticalStrut(4));
        bannerText.add(lblDesc);

        banner.add(bannerText, BorderLayout.WEST);
        root.add(banner);
        root.add(Box.createVerticalStrut(20));

        // Matriks Hak Akses Peran (PBO Polymorphism in Action)
        JPanel accessCard = new JPanel(new BorderLayout());
        accessCard.setBackground(Color.WHITE);
        accessCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
                BorderFactory.createEmptyBorder(20, 24, 20, 24)
        ));

        JLabel lblAccessTitle = new JLabel("Matriks Hak Akses Peran Aktif (" + user.getRole().getLabel() + ")");
        lblAccessTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblAccessTitle.setForeground(new Color(30, 41, 59));
        accessCard.add(lblAccessTitle, BorderLayout.NORTH);

        JPanel gridPanel = new JPanel(new GridLayout(0, 2, 16, 12));
        gridPanel.setOpaque(false);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));

        addPermissionRow(gridPanel, "Kelola Identitas & Status Anggota", user.canEditAnggota());
        addPermissionRow(gridPanel, "Ubah / Usulan Tingkat Anggota", user.canUpdateTingkatAnggota());
        addPermissionRow(gridPanel, "Kelola Master Kategori & Auto-Grouping", user.canManageKategoriTingkat());
        addPermissionRow(gridPanel, "Kelola Jadwal Latihan & Progres Tabuh", user.canManageJadwalLatihan());
        addPermissionRow(gridPanel, "Catat & Rekapitulasi Presensi Latihan", user.canRecordPresensi());
        addPermissionRow(gridPanel, "Kelola Acara Pertunjukan & Tim", user.canManageEvent());
        addPermissionRow(gridPanel, "Catat Kesediaan Calon Pemain Event", user.canRecordKesediaanPemain());
        addPermissionRow(gridPanel, "Pencatatan Honor Job Pertunjukan", user.canAccessHonor());
        addPermissionRow(gridPanel, "Modul Iuran Bulanan & Rekap Keuangan", user.canAccessIuran());
        addPermissionRow(gridPanel, "Konfigurasi Pengaturan Sanggar", user.canAccessPengaturan());

        accessCard.add(gridPanel, BorderLayout.CENTER);
        root.add(accessCard);

        root.add(Box.createVerticalGlue());

        JScrollPane scrollPane = new JScrollPane(root);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(14);

        JPanel container = new JPanel(new BorderLayout());
        container.add(scrollPane, BorderLayout.CENTER);
        return container;
    }

    private void addPermissionRow(JPanel parent, String feature, boolean allowed) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);

        JLabel lblFeature = new JLabel("• " + feature);
        lblFeature.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblFeature.setForeground(new Color(51, 65, 85));

        JLabel lblStatus = new JLabel(allowed ? "  DIIZINKAN  " : "  TIDAK DIIZINKAN  ");
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblStatus.setOpaque(true);
        if (allowed) {
            lblStatus.setBackground(new Color(240, 253, 244));
            lblStatus.setForeground(new Color(22, 163, 74));
            lblStatus.setBorder(BorderFactory.createLineBorder(new Color(187, 247, 208), 1, true));
        } else {
            lblStatus.setBackground(new Color(254, 242, 242));
            lblStatus.setForeground(new Color(220, 38, 38));
            lblStatus.setBorder(BorderFactory.createLineBorder(new Color(254, 202, 202), 1, true));
        }

        row.add(lblFeature, BorderLayout.WEST);
        row.add(lblStatus, BorderLayout.EAST);
        parent.add(row);
    }

    private JPanel createPlaceholderPanel(String title, String phase, String accessInfo) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(241, 245, 249));

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
                BorderFactory.createEmptyBorder(32, 40, 32, 40)
        ));
        card.setMaximumSize(new Dimension(540, 260));
        card.setPreferredSize(new Dimension(540, 260));

        JLabel lblPhase = new JLabel("RENCANA PENGERJAAN: " + phase.toUpperCase());
        lblPhase.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblPhase.setForeground(new Color(37, 99, 235));
        lblPhase.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(15, 23, 42));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblInfo = new JLabel(accessInfo);
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblInfo.setForeground(new Color(71, 85, 105));
        lblInfo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblNote = new JLabel("Modul ini siap dikembangkan pada tahapan sprint berikutnya.");
        lblNote.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblNote.setForeground(new Color(148, 163, 184));
        lblNote.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(lblPhase);
        card.add(Box.createVerticalStrut(8));
        card.add(lblTitle);
        card.add(Box.createVerticalStrut(14));
        card.add(lblInfo);
        card.add(Box.createVerticalStrut(12));
        card.add(lblNote);

        panel.add(card);
        return panel;
    }

    private void performLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin keluar dari sistem?",
                "Konfirmasi Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            authService.logout();
            loginPanel.resetForm();
            mainCardLayout.show(mainContainer, CARD_LOGIN);
        }
    }
}
