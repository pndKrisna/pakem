package com.pakem.ui;

import com.pakem.model.User;
import com.pakem.service.interfaces.IAuthService;

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
        setBackground(new Color(245, 247, 250)); // Latar belakang netral bersih

        // Card Container Utama
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 230, 238), 1, true),
                BorderFactory.createEmptyBorder(35, 40, 35, 40)
        ));
        cardPanel.setPreferredSize(new Dimension(420, 480));
        cardPanel.setMaximumSize(new Dimension(420, 480));

        // Header: Nama Aplikasi & Subtitle
        JLabel lblTitle = new JLabel("PAKEM");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(new Color(30, 41, 59));
        lblTitle.setAlignmentX(CENTER_ALIGNMENT);

        JLabel lblSubtitle = new JLabel("Administrasi Sanggar Seni Tabuh");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitle.setForeground(new Color(100, 116, 139));
        lblSubtitle.setAlignmentX(CENTER_ALIGNMENT);

        JLabel lblInstruction = new JLabel("Silakan masuk menggunakan akun Anda");
        lblInstruction.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblInstruction.setForeground(new Color(71, 85, 105));
        lblInstruction.setAlignmentX(CENTER_ALIGNMENT);

        // Form Fields
        JLabel lblUserTag = new JLabel("Username");
        lblUserTag.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUserTag.setForeground(new Color(51, 65, 85));
        lblUserTag.setAlignmentX(LEFT_ALIGNMENT);

        txtUsername = new JTextField();
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsername.setMaximumSize(new Dimension(340, 38));
        txtUsername.setPreferredSize(new Dimension(340, 38));
        txtUsername.putClientProperty("JTextField.placeholderText", "Masukkan username...");

        JLabel lblPassTag = new JLabel("Password");
        lblPassTag.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPassTag.setForeground(new Color(51, 65, 85));
        lblPassTag.setAlignmentX(LEFT_ALIGNMENT);

        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setMaximumSize(new Dimension(340, 38));
        txtPassword.setPreferredSize(new Dimension(340, 38));
        txtPassword.putClientProperty("JTextField.placeholderText", "Masukkan password...");

        // Pesan Error
        lblError = new JLabel(" ");
        lblError.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblError.setForeground(new Color(220, 38, 38)); // Merah
        lblError.setAlignmentX(CENTER_ALIGNMENT);
        lblError.setHorizontalAlignment(SwingConstants.CENTER);

        // Tombol Login
        btnLogin = new JButton("Masuk ke Sistem");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setBackground(new Color(37, 99, 235)); // Biru primer
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setMaximumSize(new Dimension(340, 42));
        btnLogin.setPreferredSize(new Dimension(340, 42));
        btnLogin.setFocusPainted(false);
        btnLogin.setAlignmentX(CENTER_ALIGNMENT);

        // Hint Akun Bawaan
        JPanel hintPanel = new JPanel();
        hintPanel.setLayout(new BoxLayout(hintPanel, BoxLayout.Y_AXIS));
        hintPanel.setOpaque(false);
        hintPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(241, 245, 249)),
                BorderFactory.createEmptyBorder(12, 0, 0, 0)
        ));

        JLabel lblHint1 = new JLabel("Akun Bawaan:");
        lblHint1.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblHint1.setForeground(new Color(100, 116, 139));
        lblHint1.setAlignmentX(CENTER_ALIGNMENT);

        JLabel lblHint2 = new JLabel("Pengurus: pengurus / pengurus123");
        lblHint2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHint2.setForeground(new Color(100, 116, 139));
        lblHint2.setAlignmentX(CENTER_ALIGNMENT);

        JLabel lblHint3 = new JLabel("Pelatih: pelatih / pelatih123");
        lblHint3.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHint3.setForeground(new Color(100, 116, 139));
        lblHint3.setAlignmentX(CENTER_ALIGNMENT);

        hintPanel.add(lblHint1);
        hintPanel.add(Box.createVerticalStrut(3));
        hintPanel.add(lblHint2);
        hintPanel.add(Box.createVerticalStrut(2));
        hintPanel.add(lblHint3);

        // Susun Komponen ke dalam Card
        cardPanel.add(lblTitle);
        cardPanel.add(Box.createVerticalStrut(4));
        cardPanel.add(lblSubtitle);
        cardPanel.add(Box.createVerticalStrut(15));
        cardPanel.add(lblInstruction);
        cardPanel.add(Box.createVerticalStrut(20));

        cardPanel.add(lblUserTag);
        cardPanel.add(Box.createVerticalStrut(6));
        cardPanel.add(txtUsername);
        cardPanel.add(Box.createVerticalStrut(14));

        cardPanel.add(lblPassTag);
        cardPanel.add(Box.createVerticalStrut(6));
        cardPanel.add(txtPassword);
        cardPanel.add(Box.createVerticalStrut(8));

        cardPanel.add(lblError);
        cardPanel.add(Box.createVerticalStrut(10));

        cardPanel.add(btnLogin);
        cardPanel.add(Box.createVerticalStrut(16));
        cardPanel.add(hintPanel);

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
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());

        lblError.setText(" ");
        btnLogin.setEnabled(false);

        try {
            User user = authService.login(username, password);
            if (loginListener != null) {
                loginListener.onLoginSuccess(user);
            }
        } catch (Exception ex) {
            lblError.setText(ex.getMessage());
            txtPassword.setText("");
            txtPassword.requestFocusInWindow();
        } finally {
            btnLogin.setEnabled(true);
        }
    }

    public void resetForm() {
        txtUsername.setText("");
        txtPassword.setText("");
        lblError.setText(" ");
        txtUsername.requestFocusInWindow();
    }
}
