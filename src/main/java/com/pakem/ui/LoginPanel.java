package com.pakem.ui;

import com.pakem.model.User;
import com.pakem.service.interfaces.IAuthService;

import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Panel Login modern dengan tema FlatLaf.
 * Tanpa emoji, tanpa info akun demo, show/hide password bawaan FlatLaf.
 */
public class LoginPanel extends JPanel {

    public interface LoginListener {
        void onLoginSuccess(User user);
    }

    private final IAuthService authService;
    private final LoginListener loginListener;

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JLabel lblError;

    public LoginPanel(IAuthService authService, LoginListener loginListener) {
        this.authService = authService;
        this.loginListener = loginListener;
        initUI();
    }

    private void initUI() {
        setLayout(new GridBagLayout());
        setBackground(new Color(245, 247, 250));

        // Card Container Utama
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 230, 238), 1, true),
                BorderFactory.createEmptyBorder(40, 48, 40, 48)
        ));
        cardPanel.setPreferredSize(new Dimension(420, 460));
        cardPanel.setMaximumSize(new Dimension(420, 460));

        // Header: Nama Aplikasi & Subtitle (Rata Tengah)
        JLabel lblTitle = new JLabel("PAKEM", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(new Color(30, 41, 59));
        lblTitle.setAlignmentX(LEFT_ALIGNMENT);
        lblTitle.setMaximumSize(new Dimension(324, 38));

        JLabel lblSubtitle = new JLabel("Administrasi Sanggar Seni Tabuh", SwingConstants.CENTER);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitle.setForeground(new Color(100, 116, 139));
        lblSubtitle.setAlignmentX(LEFT_ALIGNMENT);
        lblSubtitle.setMaximumSize(new Dimension(324, 22));

        // Form Fields
        JLabel lblUserTag = new JLabel("Username");
        lblUserTag.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUserTag.setForeground(new Color(51, 65, 85));
        lblUserTag.setAlignmentX(LEFT_ALIGNMENT);

        txtUsername = new JTextField();
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsername.setAlignmentX(LEFT_ALIGNMENT);
        txtUsername.setMaximumSize(new Dimension(324, 40));
        txtUsername.setPreferredSize(new Dimension(324, 40));
        txtUsername.putClientProperty("JTextField.placeholderText", "Masukkan username");

        JLabel lblPassTag = new JLabel("Password");
        lblPassTag.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPassTag.setForeground(new Color(51, 65, 85));
        lblPassTag.setAlignmentX(LEFT_ALIGNMENT);

        // Password field dengan FlatLaf built-in show/hide button
        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setAlignmentX(LEFT_ALIGNMENT);
        txtPassword.setMaximumSize(new Dimension(324, 40));
        txtPassword.setPreferredSize(new Dimension(324, 40));
        txtPassword.putClientProperty("JTextField.placeholderText", "Masukkan password");
        txtPassword.putClientProperty(FlatClientProperties.STYLE, "showRevealButton: true");
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        // Pesan Error Inline (Tanpa Pop-up)
        lblError = new JLabel(" ");
        lblError.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblError.setForeground(new Color(220, 38, 38));
        lblError.setHorizontalAlignment(SwingConstants.LEFT);
        lblError.setAlignmentX(LEFT_ALIGNMENT);
        lblError.setPreferredSize(new Dimension(324, 28));
        lblError.setMaximumSize(new Dimension(324, 36));

        // Tombol Login
        btnLogin = new JButton("Masuk ke Sistem");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setBackground(new Color(37, 99, 235));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setMaximumSize(new Dimension(324, 44));
        btnLogin.setPreferredSize(new Dimension(324, 44));
        btnLogin.setFocusPainted(false);
        btnLogin.setAlignmentX(LEFT_ALIGNMENT);
        btnLogin.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

        // Susun Komponen ke dalam Card
        cardPanel.add(lblTitle);
        cardPanel.add(Box.createVerticalStrut(4));
        cardPanel.add(lblSubtitle);
        cardPanel.add(Box.createVerticalStrut(28));

        cardPanel.add(lblUserTag);
        cardPanel.add(Box.createVerticalStrut(6));
        cardPanel.add(txtUsername);
        cardPanel.add(Box.createVerticalStrut(16));

        cardPanel.add(lblPassTag);
        cardPanel.add(Box.createVerticalStrut(6));
        cardPanel.add(txtPassword);
        cardPanel.add(Box.createVerticalStrut(8));

        cardPanel.add(lblError);
        cardPanel.add(Box.createVerticalStrut(10));

        cardPanel.add(btnLogin);

        add(cardPanel);

        // Event Handling
        btnLogin.addActionListener(e -> performLogin());

        KeyAdapter enterListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        };
        txtUsername.addKeyListener(enterListener);
        txtPassword.addKeyListener(enterListener);
    }

    private void performLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        lblError.setText(" ");
        btnLogin.setEnabled(false);
        btnLogin.setText("Memproses...");

        try {
            User user = authService.login(username, password);
            if (loginListener != null) {
                loginListener.onLoginSuccess(user);
            }
        } catch (Throwable ex) {
            String msg = (ex.getMessage() != null && !ex.getMessage().isBlank())
                    ? ex.getMessage()
                    : "Username atau password salah.";
            lblError.setText("<html>" + msg + "</html>");
            txtPassword.setText("");
            txtPassword.requestFocusInWindow();
        } finally {
            btnLogin.setEnabled(true);
            btnLogin.setText("Masuk ke Sistem");
        }
    }

    public void resetForm() {
        txtUsername.setText("");
        txtPassword.setText("");
        lblError.setText(" ");
        txtUsername.requestFocusInWindow();
    }
}