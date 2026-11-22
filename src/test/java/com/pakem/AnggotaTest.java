package com.pakem;

import com.pakem.config.DatabaseConnection;
import com.pakem.model.Anggota;
import com.pakem.model.KategoriTingkat;
import com.pakem.model.RiwayatTingkat;
import com.pakem.model.enums.StatusAnggota;
import com.pakem.service.impl.AnggotaServiceImpl;
import com.pakem.service.interfaces.IAnggotaService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class AnggotaTest {

    public static void main(String[] args) {
        System.out.println(">>> Memulai Pengujian Otomatis Fase 2 (Manajemen Data Anggota) <<<");

        if (!DatabaseConnection.testConnection()) {
            System.err.println("[FAIL] Database tidak dapat dihubungi.");
            System.exit(1);
        }

        IAnggotaService service = new AnggotaServiceImpl();

        try {
            // 1. Ambil Kategori Tingkat bawaan (Dasar & Madya)
            List<KategoriTingkat> kategoriList = service.getSemuaKategori();
            assertTrue(!kategoriList.isEmpty(), "Master kategori tingkat tidak boleh kosong (seed data)");
            KategoriTingkat katDasar = kategoriList.get(0);
            KategoriTingkat katMadya = kategoriList.size() > 1 ? kategoriList.get(1) : katDasar;
            System.out.println("[PASS] Master kategori tingkat berhasil dimuat: " + kategoriList.size() + " kategori.");

            // 2. Test Pendaftaran Anggota Baru (Aktif)
            String testNama = "I Wayan Test Tabuh " + System.currentTimeMillis();
            Anggota anggotaBaru = new Anggota();
            anggotaBaru.setNamaLengkap(testNama);
            anggotaBaru.setNomorKontak("08123456789");
            anggotaBaru.setTanggalBergabung(LocalDate.now());
            anggotaBaru.setStatus(StatusAnggota.AKTIF);
            anggotaBaru.setKategoriTingkatId(katDasar.getId());
            anggotaBaru.setCatatan("Pemain kendang pemula");

            service.simpanAnggotaBaru(anggotaBaru, "Pendaftaran murid baru sanggar");
            assertTrue(anggotaBaru.getId() > 0, "ID Anggota baru harus digenerate oleh auto-increment");
            System.out.println("[PASS] Test 1: Simpan anggota baru berhasil dengan ID #" + anggotaBaru.getId());

            // 3. Verifikasi Riwayat Tingkat Awal Tercatat
            List<RiwayatTingkat> riwayatAwal = service.getRiwayatTingkatAnggota(anggotaBaru.getId());
            assertTrue(!riwayatAwal.isEmpty(), "Riwayat tingkat awal harus otomatis terbentuk");
            assertEquals(1, riwayatAwal.size(), "Jumlah riwayat awal harus 1");
            assertEquals(katDasar.getId(), riwayatAwal.get(0).getKategoriTingkatIdBaru(), "Tingkat baru harus Dasar");
            System.out.println("[PASS] Test 2: Riwayat awal anggota otomatis tercatat di riwayat_tingkat.");

            // 4. Verifikasi Muncul di Filter Aktif
            List<Anggota> aktifList = service.getDaftarAnggota(StatusAnggota.AKTIF, testNama);
            assertEquals(1, aktifList.size(), "Anggota baru harus muncul pada filter AKTIF");
            System.out.println("[PASS] Test 3: Anggota aktif tampil di daftar aktif.");

            // 5. Test Kenaikan Tingkat (dari Dasar ke Madya)
            Anggota fetched = service.getAnggotaById(anggotaBaru.getId());
            fetched.setCatatan("Meningkat kemampuannya, naik ke tingkat Madya");
            service.perbaruiAnggota(fetched, katMadya.getId(), "Lulus ujian kenaikan tingkat dasar");

            List<RiwayatTingkat> riwayatKenaikan = service.getRiwayatTingkatAnggota(anggotaBaru.getId());
            assertEquals(2, riwayatKenaikan.size(), "Harus ada 2 log riwayat tingkat setelah kenaikan");
            assertEquals(katDasar.getId(), riwayatKenaikan.get(0).getKategoriTingkatIdLama(), "Log kedua harus mencatat tingkat lama = Dasar");
            assertEquals(katMadya.getId(), riwayatKenaikan.get(0).getKategoriTingkatIdBaru(), "Log kedua harus mencatat tingkat baru = Madya");
            System.out.println("[PASS] Test 4: Perubahan tingkat berhasil & riwayat kenaikan tercatat lengkap.");

            // 6. Test Isolasi Status: Ubah ke VAKUM (Cuti)
            service.ubahStatusAnggota(anggotaBaru.getId(), StatusAnggota.VAKUM);
            List<Anggota> aktifListSetelahCuti = service.getDaftarAnggota(StatusAnggota.AKTIF, testNama);
            assertEquals(0, aktifListSetelahCuti.size(), "Anggota berstatus VAKUM harus otomatis hilang dari daftar AKTIF");

            List<Anggota> vakumList = service.getDaftarAnggota(StatusAnggota.VAKUM, testNama);
            assertEquals(1, vakumList.size(), "Anggota harus muncul di filter VAKUM");
            System.out.println("[PASS] Test 5: Isolasi status VAKUM berhasil (hilang dari aktif, tetap ada di arsip cuti).");

            // 7. Test Isolasi Status: Ubah ke NONAKTIF (Keluar)
            service.ubahStatusAnggota(anggotaBaru.getId(), StatusAnggota.NONAKTIF);
            List<Anggota> aktifListSetelahKeluar = service.getDaftarAnggota(StatusAnggota.AKTIF, testNama);
            assertEquals(0, aktifListSetelahKeluar.size(), "Anggota berstatus NONAKTIF tidak boleh muncul di daftar AKTIF");

            // Riwayat tingkat harus tetap tersimpan utuh sebagai arsip
            List<RiwayatTingkat> riwayatSetelahNonaktif = service.getRiwayatTingkatAnggota(anggotaBaru.getId());
            assertEquals(2, riwayatSetelahNonaktif.size(), "Riwayat tingkat anggota nonaktif harus tetap utuh sebagai arsip permanen");
            System.out.println("[PASS] Test 6: Isolasi status NONAKTIF berhasil & riwayat perkembangan tetap abadi.");

            // 8. Test Ringkasan Jumlah
            Map<String, Integer> summary = service.getRingkasanJumlahAnggota();
            assertTrue(summary.containsKey("TOTAL"), "Summary harus memiliki TOTAL");
            assertTrue(summary.containsKey("AKTIF"), "Summary harus memiliki AKTIF");
            assertTrue(summary.containsKey("VAKUM"), "Summary harus memiliki VAKUM");
            assertTrue(summary.containsKey("NONAKTIF"), "Summary harus memiliki NONAKTIF");
            System.out.println("[PASS] Test 7: Ringkasan statistik jumlah anggota akurat: " + summary);

            System.out.println("==================================================");
            System.out.println("SEMUA 7 SKENARIO PENGUJIAN FASE 2 LULUS 100%!");
            System.out.println("==================================================");

        } catch (Exception ex) {
            System.err.println("[FAIL] Terjadi kegagalan pengujian: " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError("[ASSERT FAILED] " + message);
        }
    }

    private static void assertEquals(Object expected, Object actual, String message) {
        if (!expected.equals(actual)) {
            throw new AssertionError("[ASSERT FAILED] " + message + " (Expected: " + expected + ", Actual: " + actual + ")");
        }
    }
}
