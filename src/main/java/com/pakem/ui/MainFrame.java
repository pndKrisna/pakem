package com.pakem.ui;

import com.pakem.model.User;
import com.pakem.model.enums.RoleUser;
import com.pakem.service.impl.AnggotaServiceImpl;
import com.pakem.service.impl.DashboardServiceImpl;
import com.pakem.service.impl.EventServiceImpl;
import com.pakem.service.impl.IuranServiceImpl;
import com.pakem.service.impl.KategoriServiceImpl;
import com.pakem.service.impl.LatihanServiceImpl;
import com.pakem.service.impl.PresensiServiceImpl;
import com.pakem.service.interfaces.IDashboardService;
import com.pakem.service.interfaces.IAnggotaService;
import com.pakem.service.interfaces.IAuthService;
import com.pakem.service.interfaces.IEventService;
import com.pakem.service.interfaces.IIuranService;
import com.pakem.service.interfaces.IKategoriService;
import com.pakem.service.interfaces.ILatihanService;
import com.pakem.service.interfaces.IPresensiService;
import com.pakem.ui.anggota.AnggotaListPanel;
import com.pakem.ui.dashboard.DashboardPanel;
import com.pakem.ui.event.EventListPanel;
import com.pakem.ui.iuran.IuranPanel;
import com.pakem.ui.kategori.KategoriTingkatPanel;
import com.pakem.ui.latihan.JadwalLatihanPanel;
import com.pakem.ui.latihan.MateriTabuhPanel;
import com.pakem.ui.pengaturan.PengaturanPanel;
import com.pakem.ui.presensi.PresensiMainPanel;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
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
    private final IKategoriService kategoriService;
    private final ILatihanService latihanService;
    private final IPresensiService presensiService;
    private final IEventService eventService;
    private final IIuranService iuranService;
    private final IDashboardService dashboardService;
    private final CardLayout mainCardLayout;
    private final JPanel mainContainer;

    private LoginPanel loginPanel;
    private JPanel appPanel;
    private JPanel contentArea;
    private CardLayout contentCardLayout;

    private final List<JButton> sidebarButtons = new ArrayList<>();

    public MainFrame(IAuthService authService) {
        this(authService, new AnggotaServiceImpl(), new KategoriServiceImpl(), new LatihanServiceImpl(),
                new PresensiServiceImpl(), new EventServiceImpl(), new IuranServiceImpl(), new DashboardServiceImpl());
    }

    public MainFrame(IAuthService authService, IAnggotaService anggotaService,
                     IKategoriService kategoriService, ILatihanService latihanService) {
        this(authService, anggotaService, kategoriService, latihanService,
                new PresensiServiceImpl(), new EventServiceImpl(), new IuranServiceImpl(), new DashboardServiceImpl());
    }

    public MainFrame(IAuthService authService, IAnggotaService anggotaService,
                     IKategoriService kategoriService, ILatihanService latihanService,
                     IPresensiService presensiService) {
        this(authService, anggotaService, kategoriService, latihanService,
                presensiService, new EventServiceImpl(), new IuranServiceImpl(), new DashboardServiceImpl());
    }

    public MainFrame(IAuthService authService, IAnggotaService anggotaService,
                     IKategoriService kategoriService, ILatihanService latihanService,
                     IPresensiService presensiService, IEventService eventService) {
        this(authService, anggotaService, kategoriService, latihanService,
                presensiService, eventService, new IuranServiceImpl(), new DashboardServiceImpl());
    }

    public MainFrame(IAuthService authService, IAnggotaService anggotaService,
                     IKategoriService kategoriService, ILatihanService latihanService,
                     IPresensiService presensiService, IEventService eventService,
                     IIuranService iuranService) {
        this(authService, anggotaService, kategoriService, latihanService,
                presensiService, eventService, iuranService, new DashboardServiceImpl());
    }

    public MainFrame(IAuthService authService, IAnggotaService anggotaService,
                     IKategoriService kategoriService, ILatihanService latihanService,
                     IPresensiService presensiService, IEventService eventService,
                     IIuranService iuranService, IDashboardService dashboardService) {
        this.authService = authService;
        this.anggotaService = anggotaService;
        this.kategoriService = kategoriService;
        this.latihanService = latihanService;
        this.presensiService = presensiService;
        this.eventService = eventService;
        this.iuranService = iuranService;
        this.dashboardService = dashboardService;
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
        try {
            buildAppView(user);
            mainCardLayout.show(mainContainer, CARD_APP);
        } catch (Throwable ex) {
            String msg = ex.getMessage() != null ? ex.getMessage() : ex.toString();
            JOptionPane.showMessageDialog(this,
                    "Gagal membuka aplikasi setelah login: " + msg, "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
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

        // Modul Fase 8: Dashboard Ringkasan Interaktif
        contentArea.add(new DashboardPanel(dashboardService, user), "NAV_DASHBOARD");

        // Modul Fase 2: Manajemen Data Anggota Aktif
        contentArea.add(new AnggotaListPanel(anggotaService, user), "NAV_ANGGOTA");

        // Modul Fase 3: Master Kategori Tingkat & Subkelompok
        contentArea.add(new KategoriTingkatPanel(kategoriService, user), "NAV_KATEGORI");

        // Modul Fase 4: Jadwal Latihan & Master Materi Tabuh
        contentArea.add(new JadwalLatihanPanel(latihanService, kategoriService, user), "NAV_LATIHAN");
        contentArea.add(new MateriTabuhPanel(latihanService, user), "NAV_MATERI");

        // Modul Fase 5: Presensi Latihan & Rekapitulasi Kehadiran
        contentArea.add(new PresensiMainPanel(presensiService, latihanService, kategoriService, user), "NAV_PRESENSI");

        // Modul Fase 6: Pertunjukan / Event (Multi-Tim, Susunan Pemain, Kesediaan, & Honor)
        contentArea.add(new EventListPanel(eventService, kategoriService, user), "NAV_EVENT");

        // Modul Fase 7: Iuran Bulanan (Indikator Warna & Pencatatan Bayar)
        if (user.canAccessIuran()) {
            contentArea.add(new IuranPanel(iuranService, user), "NAV_IURAN");
        }

        // Modul Fase 8: Pengaturan Sanggar (Konfigurasi Key-Value)
        if (user.canAccessPengaturan()) {
            contentArea.add(new PengaturanPanel(iuranService, user), "NAV_PENGATURAN");
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
        addSidebarButton(sidebar, "Materi Tabuh", "NAV_MATERI");
        addSidebarButton(sidebar, "Presensi Latihan", "NAV_PRESENSI");
        addSidebarButton(sidebar, "Pertunjukan / Event", "NAV_EVENT");

        boolean hasAdminAccess = user.canAccessIuran() || user.canAccessPengaturan();
        if (hasAdminAccess) {
            sidebar.add(Box.createVerticalStrut(8));
            JSeparator sep = new JSeparator();
            sep.setMaximumSize(new Dimension(216, 1));
            sep.setForeground(new Color(226, 232, 240));
            sidebar.add(sep);
            sidebar.add(Box.createVerticalStrut(8));

            addNavSectionLabel(sidebar, "ADMINISTRASI & KEUANGAN");

            if (user.canAccessIuran()) {
                addSidebarButton(sidebar, "Iuran Bulanan", "NAV_IURAN");
                sidebar.add(Box.createVerticalStrut(4));
            }
            if (user.canAccessPengaturan()) {
                addSidebarButton(sidebar, "Pengaturan Sanggar", "NAV_PENGATURAN");
                sidebar.add(Box.createVerticalStrut(4));
            }
        }

        sidebar.add(Box.createVerticalStrut(16));

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
