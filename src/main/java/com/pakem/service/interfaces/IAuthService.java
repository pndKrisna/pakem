package com.pakem.service.interfaces;

import com.pakem.model.User;

/**
 * Kontrak layanan logika bisnis autentikasi dan sesi pengguna.
 */
public interface IAuthService {

    /**
     * Memvalidasi kredensial pengguna dan menginisialisasi sesi login.
     * @param username username input
     * @param password plain text password input
     * @return User yang berhasil login
     * @throws Exception jika username tidak ditemukan, password salah, atau akun nonaktif
     */
    User login(String username, String password) throws Exception;

    /**
     * Mengakhiri sesi pengguna saat ini.
     */
    void logout();

    /**
     * Mendapatkan pengguna yang sedang aktif dalam sesi.
     */
    User getCurrentUser();

    /**
     * Memeriksa apakah ada pengguna yang sedang login.
     */
    boolean isLoggedIn();

    /**
     * Menghasilkan hash SHA-256 dari teks password.
     */
    String hashPassword(String plainPassword);
}
