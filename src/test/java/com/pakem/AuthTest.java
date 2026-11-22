package com.pakem;

import com.pakem.config.DatabaseConnection;
import com.pakem.dao.impl.UserDaoImpl;
import com.pakem.model.Pelatih;
import com.pakem.model.Pengurus;
import com.pakem.model.User;
import com.pakem.service.impl.AuthServiceImpl;
import com.pakem.service.interfaces.IAuthService;

public class AuthTest {

    public static void main(String[] args) {
        System.out.println(">>> Memulai Pengujian Otomatis Fase 1 (Autentikasi & Hak Akses) <<<");

        if (!DatabaseConnection.testConnection()) {
            System.err.println("[FAIL] Gagal koneksi ke database. Pastikan MySQL dan pakem_db aktif.");
            System.exit(1);
        }
        System.out.println("[PASS] Koneksi database aktif.");

        IAuthService authService = new AuthServiceImpl(new UserDaoImpl());

        // Test 1: Login Pengurus
        try {
            User pengurus = authService.login("pengurus", "pengurus123");
            assertNotNull(pengurus, "User pengurus tidak boleh null");
            assertTrue(pengurus instanceof Pengurus, "User pengurus harus bertipe Pengurus");
            assertTrue(pengurus.canAccessHonor(), "Pengurus harus bisa akses honor");
            assertTrue(pengurus.canAccessIuran(), "Pengurus harus bisa akses iuran");
            assertTrue(pengurus.canAccessPengaturan(), "Pengurus harus bisa akses pengaturan");
            System.out.println("[PASS] Test 1: Login Pengurus & Hak Akses valid.");
        } catch (Exception e) {
            System.err.println("[FAIL] Test 1: " + e.getMessage());
            System.exit(1);
        }

        // Test 2: Login Pelatih
        try {
            User pelatih = authService.login("pelatih", "pelatih123");
            assertNotNull(pelatih, "User pelatih tidak boleh null");
            assertTrue(pelatih instanceof Pelatih, "User pelatih harus bertipe Pelatih");
            assertFalse(pelatih.canAccessHonor(), "Pelatih TIDAK boleh akses honor");
            assertFalse(pelatih.canAccessIuran(), "Pelatih TIDAK boleh akses iuran");
            assertFalse(pelatih.canAccessPengaturan(), "Pelatih TIDAK boleh akses pengaturan");
            assertTrue(pelatih.canManageJadwalLatihan(), "Pelatih harus bisa kelola jadwal");
            assertTrue(pelatih.canRecordPresensi(), "Pelatih harus bisa catat presensi");
            System.out.println("[PASS] Test 2: Login Pelatih & Hak Akses valid (Polimorfisme terbukti).");
        } catch (Exception e) {
            System.err.println("[FAIL] Test 2: " + e.getMessage());
            System.exit(1);
        }

        // Test 3: Password Salah
        try {
            authService.login("pengurus", "passwordsalah");
            System.err.println("[FAIL] Test 3: Harusnya gagal saat password salah!");
            System.exit(1);
        } catch (Exception e) {
            System.out.println("[PASS] Test 3: Penolakan password salah berhasil (" + e.getMessage() + ")");
        }

        // Test 4: Username Tidak Ada
        try {
            authService.login("usernametidakada", "bebas");
            System.err.println("[FAIL] Test 4: Harusnya gagal saat username tidak ada!");
            System.exit(1);
        } catch (Exception e) {
            System.out.println("[PASS] Test 4: Penolakan username salah berhasil (" + e.getMessage() + ")");
        }

        System.out.println("==================================================");
        System.out.println("SEMUA 4 SKENARIO PENGUJIAN FASE 1 LULUS 100%!");
        System.out.println("==================================================");
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError("[ASSERT FAILED] Expected true, but got false: " + message);
        }
    }

    private static void assertFalse(boolean condition, String message) {
        if (condition) {
            throw new AssertionError("[ASSERT FAILED] Expected false, but got true: " + message);
        }
    }

    private static void assertNotNull(Object obj, String message) {
        if (obj == null) {
            throw new AssertionError("[ASSERT FAILED] Expected not null: " + message);
        }
    }
}
