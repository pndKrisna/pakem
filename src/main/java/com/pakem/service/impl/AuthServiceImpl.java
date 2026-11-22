package com.pakem.service.impl;

import com.pakem.dao.impl.UserDaoImpl;
import com.pakem.dao.interfaces.IUserDao;
import com.pakem.model.User;
import com.pakem.service.interfaces.IAuthService;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Implementasi layanan bisnis autentikasi.
 */
public class AuthServiceImpl implements IAuthService {

    private final IUserDao userDao;
    private User currentUser = null;

    public AuthServiceImpl() {
        this(new UserDaoImpl());
    }

    public AuthServiceImpl(IUserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public User login(String username, String password) throws Exception {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username tidak boleh kosong.");
        }
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password tidak boleh kosong.");
        }

        User user = userDao.findByUsername(username.trim());
        if (user == null) {
            throw new Exception("Username '" + username.trim() + "' tidak ditemukan.");
        }

        if (!user.isActive()) {
            throw new Exception("Akun pengguna ini berstatus nonaktif. Silakan hubungi pengurus.");
        }

        String inputHash = hashPassword(password);
        if (!MessageDigest.isEqual(inputHash.getBytes(StandardCharsets.UTF_8),
                                   user.getPasswordHash().getBytes(StandardCharsets.UTF_8))) {
            throw new Exception("Password yang Anda masukkan salah.");
        }

        this.currentUser = user;
        return user;
    }

    @Override
    public void logout() {
        this.currentUser = null;
    }

    @Override
    public User getCurrentUser() {
        return currentUser;
    }

    @Override
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    @Override
    public String hashPassword(String plainPassword) {
        if (plainPassword == null) return "";
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(plainPassword.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Algoritma enkripsi SHA-256 tidak didukung pada sistem ini.", e);
        }
    }
}
