package com.pakem;

import com.pakem.config.DatabaseConnection;
import com.pakem.dao.impl.AnggotaDaoImpl;
import com.pakem.dao.impl.KategoriTingkatDaoImpl;
import com.pakem.dao.impl.SettingsDaoImpl;
import com.pakem.dao.interfaces.IAnggotaDao;
import com.pakem.dao.interfaces.IKategoriTingkatDao;
import com.pakem.dao.interfaces.ISettingsDao;
import com.pakem.model.Anggota;
import com.pakem.model.Iuran;
import com.pakem.model.KategoriTingkat;
import com.pakem.model.enums.StatusAnggota;
import com.pakem.model.enums.StatusBayar;
import com.pakem.model.enums.StatusIuranWarna;
import com.pakem.service.impl.IuranServiceImpl;
import com.pakem.service.interfaces.IIuranService;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

public class IuranTest {

    private static IIuranService iuranService;
    private static IAnggotaDao anggotaDao;
    private static IKategoriTingkatDao kategoriDao;
    private static ISettingsDao settingsDao;
    private static int anggotaId1, anggotaId2, anggotaId3;
    private static int kategoriId;

    public static void main(String[] args) {
        System.out.println(">>> Memulai Pengujian Otomatis Fase 7 (Iuran Bulanan) <<<");

        if (!DatabaseConnection.testConnection()) {
            System.err.println("[FAIL] Database tidak dapat dihubungi.");
            System.exit(1);
        }

        iuranService = new IuranServiceImpl();
        anggotaDao = new AnggotaDaoImpl();
        kategoriDao = new KategoriTingkatDaoImpl();
        settingsDao = new SettingsDaoImpl();

        try {
            // 0. Bersihkan sisa data pengujian lama agar pengujian idempoten
            cleanOldTestData();

            // Setup pengaturan default
            settingsDao.set("batas_kuning", "15");
            settingsDao.set("batas_merah", "25");
            settingsDao.set("iuran_default", "50000");

            // Setup kategori khusus pengujian
            KategoriTingkat kat = new KategoriTingkat(0, "Kategori Iuran Test", 1);
            kategoriDao.save(kat);
            kategoriId = kat.getId();

            // Setup 3 anggota: 1 AKTIF, 1 VAKUM, 1 NONAKTIF
            Anggota a1 = new Anggota(0, "Anggota Aktif Test", "0811111111", LocalDate.now(),
                    StatusAnggota.AKTIF, kategoriId, null, "Aktif");
            anggotaDao.save(a1);
            anggotaId1 = a1.getId();

            Anggota a2 = new Anggota(0, "Anggota Vakum Test", "0822222222", LocalDate.now(),
                    StatusAnggota.VAKUM, kategoriId, null, "Cuti");
            anggotaDao.save(a2);
            anggotaId2 = a2.getId();

            Anggota a3 = new Anggota(0, "Anggota Nonaktif Test", "0833333333", LocalDate.now(),
                    StatusAnggota.NONAKTIF, kategoriId, null, "Keluar");
            anggotaDao.save(a3);
            anggotaId3 = a3.getId();

            // Test 1: Isolasi Status (hanya AKTIF yang muncul)
            testIsolasiStatus();

            // Test 2: Pencatatan Pembayaran (LUNAS + HIJAU)
            testCatatBayar();

            // Test 3: Logika Warna Merah untuk bulan lalu yang belum bayar
            testWarnaMenunggak();

            // Test 4: Batal Pembayaran kembali ke BELUM_BAYAR
            testBatalBayar();

            // Cleanup setelah seluruh pengujian selesai
            cleanup();

            System.out.println("==================================================");
            System.out.println("SEMUA 4 SKENARIO PENGUJIAN FASE 7 LULUS 100%!");
            System.out.println("==================================================");

        } catch (Exception ex) {
            System.err.println("[FAIL] Terjadi kegagalan pengujian Fase 7: " + ex.getMessage());
            ex.printStackTrace();
            // Upayakan tetap membersihkan database jika gagal
            try { cleanup(); } catch (Exception ignored) {}
            System.exit(1);
        }
    }

    private static void cleanOldTestData() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (Statement stmt = conn.createStatement()) {
                // Hapus data iuran anggota uji
                stmt.execute("DELETE i FROM iuran i " +
                             "JOIN anggota a ON i.anggota_id = a.id " +
                             "WHERE a.nama_lengkap LIKE '%Test%'");
                // Hapus anggota uji
                stmt.execute("DELETE FROM anggota WHERE nama_lengkap LIKE '%Test%'");
                // Hapus kategori uji
                stmt.execute("DELETE FROM kategori_tingkat WHERE nama = 'Kategori Iuran Test'");
            }
        } catch (Exception e) {
            System.out.println("[WARN] Gagal membersihkan data lama: " + e.getMessage());
        }
    }

    private static void testIsolasiStatus() throws Exception {
        LocalDate now = LocalDate.now();
        List<Iuran> daftar = iuranService.getDaftarIuranBulan(now.getMonthValue(), now.getYear());

        boolean foundAktif = false, foundVakum = false, foundNonaktif = false;
        for (Iuran i : daftar) {
            if (i.getAnggotaId() == anggotaId1) foundAktif = true;
            if (i.getAnggotaId() == anggotaId2) foundVakum = true;
            if (i.getAnggotaId() == anggotaId3) foundNonaktif = true;
        }

        assertTrue(foundAktif, "Anggota AKTIF harus muncul di daftar iuran");
        assertFalse(foundVakum, "Anggota VAKUM tidak boleh muncul di daftar iuran");
        assertFalse(foundNonaktif, "Anggota NONAKTIF tidak boleh muncul di daftar iuran");

        System.out.println("[PASS] Test 1: Isolasi status anggota (Hanya Aktif muncul).");
    }

    private static void testCatatBayar() throws Exception {
        LocalDate now = LocalDate.now();
        BigDecimal nominal = new BigDecimal("50000");

        iuranService.catatPembayaran(anggotaId1, now.getMonthValue(), now.getYear(), nominal, now);

        List<Iuran> daftar = iuranService.getDaftarIuranBulan(now.getMonthValue(), now.getYear());
        Iuran found = null;
        for (Iuran i : daftar) {
            if (i.getAnggotaId() == anggotaId1) {
                found = i;
                break;
            }
        }

        assertNotNull(found, "Data iuran untuk anggota aktif harus ditemukan");
        assertEquals(StatusBayar.LUNAS, found.getStatusBayar(), "Status harus LUNAS setelah pembayaran");
        assertEquals(StatusIuranWarna.HIJAU, found.getIndikatorWarna(), "Indikator harus HIJAU (Lunas)");

        System.out.println("[PASS] Test 2: Pencatatan pembayaran iuran (Lunas + Hijau).");
    }

    private static void testWarnaMenunggak() throws Exception {
        LocalDate lastMonth = LocalDate.now().minusMonths(1);
        List<Iuran> daftar = iuranService.getDaftarIuranBulan(lastMonth.getMonthValue(), lastMonth.getYear());

        Iuran found = null;
        for (Iuran i : daftar) {
            if (i.getAnggotaId() == anggotaId1) {
                found = i;
                break;
            }
        }

        assertNotNull(found, "Data iuran untuk anggota aktif bulan lalu harus ada");
        assertEquals(StatusBayar.BELUM_BAYAR, found.getStatusBayar(), "Status harus BELUM_BAYAR");
        assertEquals(StatusIuranWarna.MERAH, found.getIndikatorWarna(), "Harus MERAH karena sudah lewat bulan tagihan");

        System.out.println("[PASS] Test 3: Logika warna Merah untuk iuran menunggak.");
    }

    private static void testBatalBayar() throws Exception {
        LocalDate now = LocalDate.now();
        iuranService.batalkanPembayaran(anggotaId1, now.getMonthValue(), now.getYear());

        List<Iuran> daftar = iuranService.getDaftarIuranBulan(now.getMonthValue(), now.getYear());
        Iuran found = null;
        for (Iuran i : daftar) {
            if (i.getAnggotaId() == anggotaId1) {
                found = i;
                break;
            }
        }

        assertNotNull(found, "Data iuran anggota harus ditemukan setelah pembatalan");
        assertEquals(StatusBayar.BELUM_BAYAR, found.getStatusBayar(), "Status harus kembali BELUM_BAYAR");
        assertNotEquals(StatusIuranWarna.HIJAU, found.getIndikatorWarna(), "Tidak boleh HIJAU setelah dibatalkan");

        System.out.println("[PASS] Test 4: Batal pembayaran kembali ke Belum Bayar.");
    }

    private static void cleanup() throws Exception {
        Connection conn = DatabaseConnection.getConnection();
        try (Statement stmt = conn.createStatement()) {
            if (anggotaId1 > 0 || anggotaId2 > 0 || anggotaId3 > 0) {
                stmt.execute("DELETE FROM iuran WHERE anggota_id IN (" + anggotaId1 + ", " + anggotaId2 + ", " + anggotaId3 + ")");
                stmt.execute("DELETE FROM anggota WHERE id IN (" + anggotaId1 + ", " + anggotaId2 + ", " + anggotaId3 + ")");
            }
            if (kategoriId > 0) {
                stmt.execute("DELETE FROM kategori_tingkat WHERE id = " + kategoriId);
            }
        }
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError("[ASSERT FAILED] " + message);
        }
    }

    private static void assertFalse(boolean condition, String message) {
        if (condition) {
            throw new AssertionError("[ASSERT FAILED] " + message);
        }
    }

    private static void assertEquals(Object expected, Object actual, String message) {
        if (!expected.equals(actual)) {
            throw new AssertionError("[ASSERT FAILED] " + message + " (Expected: " + expected + ", Actual: " + actual + ")");
        }
    }

    private static void assertNotNull(Object obj, String message) {
        if (obj == null) {
            throw new AssertionError("[ASSERT FAILED] " + message);
        }
    }

    private static void assertNotEquals(Object expected, Object actual, String message) {
        if (expected.equals(actual)) {
            throw new AssertionError("[ASSERT FAILED] " + message + " (Nilai tidak boleh sama: " + actual + ")");
        }
    }
}
