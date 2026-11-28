package com.pakem;

import com.pakem.config.DatabaseConnection;
import com.pakem.dao.impl.AnggotaDaoImpl;
import com.pakem.dao.impl.EventDaoImpl;
import com.pakem.dao.impl.JadwalLatihanDaoImpl;
import com.pakem.dao.impl.KategoriTingkatDaoImpl;
import com.pakem.dao.impl.SettingsDaoImpl;
import com.pakem.dao.interfaces.IAnggotaDao;
import com.pakem.dao.interfaces.IEventDao;
import com.pakem.dao.interfaces.IJadwalLatihanDao;
import com.pakem.dao.interfaces.IKategoriTingkatDao;
import com.pakem.dao.interfaces.ISettingsDao;
import com.pakem.model.Anggota;
import com.pakem.model.DashboardSummary;
import com.pakem.model.Event;
import com.pakem.model.JadwalLatihan;
import com.pakem.model.KategoriTingkat;
import com.pakem.model.SanggarSettings;
import com.pakem.model.enums.JenisAcara;
import com.pakem.model.enums.JenisJadwal;
import com.pakem.model.enums.StatusAnggota;
import com.pakem.service.impl.DashboardServiceImpl;
import com.pakem.service.impl.IuranServiceImpl;
import com.pakem.service.interfaces.IDashboardService;
import com.pakem.service.interfaces.IIuranService;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class DashboardTest {

    private static IDashboardService dashboardService;
    private static IIuranService iuranService;
    private static IAnggotaDao anggotaDao;
    private static IKategoriTingkatDao kategoriDao;
    private static IJadwalLatihanDao jadwalDao;
    private static IEventDao eventDao;
    private static ISettingsDao settingsDao;

    private static int katId;
    private static int anggotaAktifId;
    private static int anggotaVakumId;
    private static int jadwalId;
    private static int eventId;

    public static void main(String[] args) {
        System.out.println(">>> Memulai Pengujian Otomatis Fase 8 (Pengaturan Sanggar & Dashboard) <<<");

        if (!DatabaseConnection.testConnection()) {
            System.err.println("[FAIL] Database tidak dapat dihubungi.");
            System.exit(1);
        }

        dashboardService = new DashboardServiceImpl();
        iuranService = new IuranServiceImpl();
        anggotaDao = new AnggotaDaoImpl();
        kategoriDao = new KategoriTingkatDaoImpl();
        jadwalDao = new JadwalLatihanDaoImpl();
        eventDao = new EventDaoImpl();
        settingsDao = new SettingsDaoImpl();

        try {
            // 0. Bersihkan data pengujian sisa
            cleanup();

            // Skenario 1: Simpan & Baca Pengaturan Sanggar
            testPengaturanSanggar();

            // Skenario 2: Setup Data Pendukung Dashboard
            testSetupDataDashboard();

            // Skenario 3: Verifikasi Agregasi Dashboard Ringkasan
            testDashboardSummary();

            // Skenario 4: Verifikasi Query Jadwal & Event Terdekat
            testJadwalDanEventTerdekat();

            // Skenario 5: Cleanup akhir
            cleanup();

            System.out.println("==================================================");
            System.out.println("SEMUA 5 SKENARIO PENGUJIAN FASE 8 LULUS 100%!");
            System.out.println("==================================================");

        } catch (Exception ex) {
            System.err.println("[FAIL] Terjadi kegagalan pengujian Fase 8: " + ex.getMessage());
            ex.printStackTrace();
            try { cleanup(); } catch (Exception ignored) {}
            System.exit(1);
        }
    }

    private static void testPengaturanSanggar() throws Exception {
        SanggarSettings set = new SanggarSettings();
        set.setNamaSanggar("Sanggar Pakem Uji Coba");
        set.setIuranDefault(new BigDecimal("75000"));
        set.setBatasKuning(14);
        set.setBatasMerah(24);
        set.setKapasitasKelompok(30);

        iuranService.simpanPengaturanSanggar(set);

        SanggarSettings loaded = iuranService.getPengaturanSanggar();
        assertEquals("Sanggar Pakem Uji Coba", loaded.getNamaSanggar(), "Nama sanggar harus sesuai");
        assertEquals(new BigDecimal("75000"), loaded.getIuranDefault(), "Iuran default harus 75000");
        assertEquals(14, loaded.getBatasKuning(), "Batas kuning harus 14");
        assertEquals(24, loaded.getBatasMerah(), "Batas merah harus 24");
        assertEquals(30, loaded.getKapasitasKelompok(), "Kapasitas anjuran harus 30");

        System.out.println("[PASS] Test 1: Simpan & baca konfigurasi Pengaturan Sanggar berhasil.");
    }

    private static void testSetupDataDashboard() throws Exception {
        // Kategori Uji
        KategoriTingkat k = new KategoriTingkat(0, "Kategori Dash Test", 1);
        kategoriDao.save(k);
        katId = k.getId();

        // 1 Anggota Aktif & 1 Anggota Vakum
        Anggota a1 = new Anggota(0, "Anggota Aktif Dash", "08111111", LocalDate.now(),
                StatusAnggota.AKTIF, katId, null, "Uji");
        anggotaDao.save(a1);
        anggotaAktifId = a1.getId();

        Anggota a2 = new Anggota(0, "Anggota Vakum Dash", "08222222", LocalDate.now(),
                StatusAnggota.VAKUM, katId, null, "Uji");
        anggotaDao.save(a2);
        anggotaVakumId = a2.getId();

        // Jadwal Mendatang
        JadwalLatihan j = new JadwalLatihan(0, JenisJadwal.RUTIN, LocalDate.now().plusDays(2),
                LocalTime.of(18, 0), LocalTime.of(20, 0), "Wantilan Uji", katId, null, null, "Latihan");
        jadwalDao.save(j);
        jadwalId = j.getId();

        // Event Mendatang
        Event e = new Event(0, "Pentas Dash Test", JenisAcara.JOB, LocalDate.now().plusDays(5),
                LocalTime.of(19, 0), "Panggung Budaya", "Job");
        eventDao.save(e);
        eventId = e.getId();

        System.out.println("[PASS] Test 2: Setup data uji dashboard berhasil.");
    }

    private static void testDashboardSummary() throws Exception {
        DashboardSummary sum = dashboardService.getSummary();

        assertTrue(sum.getTotalAnggota() >= 2, "Total anggota harus >= 2");
        assertTrue(sum.getJumlahAktif() >= 1, "Jumlah aktif harus >= 1");
        assertTrue(sum.getJumlahVakum() >= 1, "Jumlah vakum harus >= 1");
        assertTrue(sum.getTotalJadwalTerdekat() >= 1, "Total jadwal terdekat harus >= 1");
        assertTrue(sum.getTotalEventTerdekat() >= 1, "Total event terdekat harus >= 1");

        System.out.println("[PASS] Test 3: Perhitungan statistik DashboardSummary akurat.");
    }

    private static void testJadwalDanEventTerdekat() throws Exception {
        List<JadwalLatihan> listJadwal = dashboardService.getJadwalLatihanTerdekat(5);
        assertNotNull(listJadwal, "Daftar jadwal tidak boleh null");
        assertTrue(listJadwal.stream().anyMatch(j -> j.getId() == jadwalId), "Jadwal uji harus ada di daftar terdekat");

        List<Event> listEvent = dashboardService.getEventTerdekat(5);
        assertNotNull(listEvent, "Daftar event tidak boleh null");
        assertTrue(listEvent.stream().anyMatch(e -> e.getId() == eventId), "Event uji harus ada di daftar terdekat");

        System.out.println("[PASS] Test 4: Query jadwal dan event mendatang terdekat berhasil.");
    }

    private static void cleanup() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("DELETE FROM iuran WHERE anggota_id IN (SELECT id FROM anggota WHERE nama_lengkap LIKE '%Dash%')");
                stmt.execute("DELETE FROM presensi WHERE jadwal_latihan_id IN (SELECT id FROM jadwal_latihan WHERE catatan = 'Latihan')");
                stmt.execute("DELETE FROM sesi_materi WHERE jadwal_latihan_id IN (SELECT id FROM jadwal_latihan WHERE catatan = 'Latihan')");
                stmt.execute("DELETE FROM jadwal_latihan WHERE catatan = 'Latihan'");
                stmt.execute("DELETE FROM pemain_tim WHERE tim_event_id IN (SELECT id FROM tim_event WHERE event_id IN (SELECT id FROM event WHERE nama_acara LIKE '%Dash%'))");
                stmt.execute("DELETE FROM tim_event WHERE event_id IN (SELECT id FROM event WHERE nama_acara LIKE '%Dash%')");
                stmt.execute("DELETE FROM event WHERE nama_acara LIKE '%Dash%'");
                stmt.execute("DELETE FROM anggota WHERE nama_lengkap LIKE '%Dash%'");
                stmt.execute("DELETE FROM kategori_tingkat WHERE nama LIKE '%Dash%'");

                // Kembalikan settings default
                stmt.execute("INSERT INTO settings (setting_key, setting_value) VALUES " +
                        "('nama_sanggar', 'Sanggar Seni Tabuh Pakem'), " +
                        "('iuran_default', '50000'), " +
                        "('batas_kuning', '15'), " +
                        "('batas_merah', '25'), " +
                        "('kapasitas_kelompok', '25') " +
                        "ON DUPLICATE KEY UPDATE setting_value = VALUES(setting_value)");
            }
        } catch (Exception ignored) {}
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) throw new AssertionError("[ASSERT FAILED] " + message);
    }

    private static void assertEquals(Object expected, Object actual, String message) {
        if (!expected.equals(actual)) {
            throw new AssertionError("[ASSERT FAILED] " + message + " (Expected: " + expected + ", Actual: " + actual + ")");
        }
    }

    private static void assertNotNull(Object obj, String message) {
        if (obj == null) throw new AssertionError("[ASSERT FAILED] " + message);
    }
}
