package com.pakem;

import com.pakem.config.DatabaseConnection;
import com.pakem.model.Anggota;
import com.pakem.model.KategoriTingkat;
import com.pakem.model.Subkelompok;
import com.pakem.model.enums.StatusAnggota;
import com.pakem.service.impl.AnggotaServiceImpl;
import com.pakem.service.impl.KategoriServiceImpl;
import com.pakem.service.interfaces.IAnggotaService;
import com.pakem.service.interfaces.IKategoriService;

import java.time.LocalDate;
import java.util.List;

public class KategoriTest {

    public static void main(String[] args) {
        System.out.println(">>> Memulai Pengujian Otomatis Fase 3 (Kategori Tingkat, Subkelompok, & Auto-Grouping) <<<");

        if (!DatabaseConnection.testConnection()) {
            System.err.println("[FAIL] Database tidak dapat dihubungi.");
            System.exit(1);
        }

        IKategoriService kategoriService = new KategoriServiceImpl();
        IAnggotaService anggotaService = new AnggotaServiceImpl();

        try {
            // 1. Test Tambah Kategori Baru
            String namaKat = "Tingkat Eksperimen " + System.currentTimeMillis();
            kategoriService.tambahKategori(namaKat, 99);
            List<KategoriTingkat> allKat = kategoriService.getSemuaKategori();
            KategoriTingkat createdKat = allKat.stream()
                    .filter(k -> k.getNama().equals(namaKat))
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("Kategori baru tidak ditemukan"));
            System.out.println("[PASS] Test 1: Tambah kategori tingkat berhasil (ID #" + createdKat.getId() + ")");

            // 2. Test Ubah Kategori
            String namaKatBaru = namaKat + " Updated";
            kategoriService.ubahKategori(createdKat.getId(), namaKatBaru, 100);
            KategoriTingkat updatedKat = kategoriService.getKategoriById(createdKat.getId());
            assertEquals(namaKatBaru, updatedKat.getNama(), "Nama kategori harus terupdate");
            assertEquals(100, updatedKat.getUrutan(), "Urutan kategori harus 100");
            System.out.println("[PASS] Test 2: Ubah nama dan urutan kategori berhasil.");

            // 3. Test Buat 2 Subkelompok
            kategoriService.tambahSubkelompok(createdKat.getId(), "Kelompok Alpha");
            kategoriService.tambahSubkelompok(createdKat.getId(), "Kelompok Beta");
            List<Subkelompok> subs = kategoriService.getSubkelompokByKategori(createdKat.getId());
            assertEquals(2, subs.size(), "Harus ada 2 subkelompok yang dibuat");
            Subkelompok subAlpha = subs.get(0);
            Subkelompok subBeta = subs.get(1);
            System.out.println("[PASS] Test 3: Pembuatan 2 subkelompok berhasil (" + subAlpha.getNama() + ", " + subBeta.getNama() + ")");

            // 4. Test Pendaftaran 5 Anggota ke Kategori Ini
            int anggotaCount = 5;
            for (int i = 1; i <= anggotaCount; i++) {
                Anggota a = new Anggota();
                a.setNamaLengkap("Anggota AutoGroup " + i + " " + System.currentTimeMillis());
                a.setTanggalBergabung(LocalDate.now());
                a.setStatus(StatusAnggota.AKTIF);
                a.setKategoriTingkatId(createdKat.getId());
                // Belum punya subkelompok
                a.setSubkelompokId(null);
                anggotaService.simpanAnggotaBaru(a, "Pendaftaran test auto-grouping");
            }

            List<Anggota> anggotaKategori = kategoriService.getAnggotaByKategori(createdKat.getId(), true);
            assertEquals(5, anggotaKategori.size(), "Harus ada 5 anggota aktif di kategori ini");
            System.out.println("[PASS] Test 4: 5 anggota aktif berhasil didaftarkan ke kategori ini.");

            // 5. Test Auto-Grouping (Bagi Rata 5 anggota ke 2 subkelompok: 3 dan 2)
            int distributed = kategoriService.jalankanAutoGrouping(createdKat.getId());
            assertEquals(5, distributed, "Harus membagi 5 anggota");

            int countAlpha = kategoriService.getJumlahAnggotaSubkelompok(subAlpha.getId());
            int countBeta = kategoriService.getJumlahAnggotaSubkelompok(subBeta.getId());
            assertTrue((countAlpha == 3 && countBeta == 2) || (countAlpha == 2 && countBeta == 3),
                    "Pembagian harus seimbang (3 dan 2)");
            System.out.println("[PASS] Test 5: Auto-Grouping merata berhasil! (Alpha: " + countAlpha + ", Beta: " + countBeta + ")");

            // 6. Test Deteksi Anggota Sudah Memiliki Subkelompok
            boolean adaPunya = kategoriService.apakahAdaAnggotaSudahPunyaSubkelompok(createdKat.getId());
            assertTrue(adaPunya, "Harus mendeteksi bahwa anggota sudah punya subkelompok (untuk trigger dialog konfirmasi)");
            System.out.println("[PASS] Test 6: Deteksi penempatan subkelompok untuk dialog konfirmasi penimpaan valid.");

            // 7. Test Pemindahan Manual Antar-Subkelompok
            List<Anggota> anggotaSetelahGroup = kategoriService.getAnggotaByKategori(createdKat.getId(), true);
            Anggota anggotaPindah = anggotaSetelahGroup.get(0);
            Integer subAwal = anggotaPindah.getSubkelompokId();
            assertTrue(subAwal != null, "Subkelompok anggota hasil auto-grouping tidak boleh null");
            Integer subTujuan = subAwal.equals(subAlpha.getId()) ? subBeta.getId() : subAlpha.getId();

            kategoriService.pindahAnggotaSubkelompok(anggotaPindah.getId(), subTujuan);
            Anggota refreshed = anggotaService.getAnggotaById(anggotaPindah.getId());
            assertEquals(subTujuan, refreshed.getSubkelompokId(), "Subkelompok anggota harus berpindah ke subkelompok tujuan");
            System.out.println("[PASS] Test 7: Pemindahan manual anggota antar-subkelompok berhasil.");

            // 8. Test Kapasitas Anjuran
            int kap = kategoriService.getKapasitasAnjuran();
            assertEquals(25, kap, "Kapasitas anjuran default dari settings harus 25");
            System.out.println("[PASS] Test 8: Kapasitas anjuran kelompok terbaca dari settings: " + kap + " orang.");

            // 9. Test Hapus Kategori (Verifikasi ON DELETE SET NULL pada anggota)
            kategoriService.hapusKategori(createdKat.getId());
            Anggota anggotaSetelahHapus = anggotaService.getAnggotaById(anggotaPindah.getId());
            assertTrue(anggotaSetelahHapus.getKategoriTingkatId() == null,
                    "Kategori anggota harus menjadi NULL (ON DELETE SET NULL), anggota tidak boleh terhapus!");
            System.out.println("[PASS] Test 9: Hapus kategori berhasil tanpa menghapus anggota.");

            System.out.println("==================================================");
            System.out.println("SEMUA 9 SKENARIO PENGUJIAN FASE 3 LULUS 100%!");
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
