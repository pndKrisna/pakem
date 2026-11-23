package com.pakem;

import com.pakem.config.DatabaseConnection;
import com.pakem.model.JadwalLatihan;
import com.pakem.model.KategoriTingkat;
import com.pakem.model.MateriTabuh;
import com.pakem.model.SesiMateri;
import com.pakem.model.enums.JenisJadwal;
import com.pakem.service.impl.KategoriServiceImpl;
import com.pakem.service.impl.LatihanServiceImpl;
import com.pakem.service.interfaces.IKategoriService;
import com.pakem.service.interfaces.ILatihanService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class LatihanTest {

    public static void main(String[] args) {
        System.out.println(">>> Memulai Pengujian Otomatis Fase 4 (Jadwal Latihan & Master Materi Tabuh) <<<");

        if (!DatabaseConnection.testConnection()) {
            System.err.println("[FAIL] Database tidak dapat dihubungi.");
            System.exit(1);
        }

        ILatihanService latihanService = new LatihanServiceImpl();
        IKategoriService kategoriService = new KategoriServiceImpl();

        try {
            // 1. Test Tambah Materi Tabuh Baru
            String namaGending = "Tabuh Gilak Test " + System.currentTimeMillis();
            String deskripsi = "Gending tabuh dasar untuk latihan ritme dan tempo.";
            latihanService.tambahMateriTabuh(namaGending, deskripsi);

            List<MateriTabuh> allMateri = latihanService.getSemuaMateriTabuh();
            MateriTabuh createdMateri = allMateri.stream()
                    .filter(m -> m.getNama().equals(namaGending))
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("Materi tabuh baru tidak ditemukan"));
            System.out.println("[PASS] Test 1: Tambah materi tabuh berhasil (ID #" + createdMateri.getId() + ")");

            // 2. Test Ubah Materi Tabuh
            String updatedDesc = deskripsi + " (Revisi Deskripsi)";
            latihanService.ubahMateriTabuh(createdMateri.getId(), namaGending, updatedDesc);
            MateriTabuh refreshedMateri = latihanService.getMateriTabuhById(createdMateri.getId());
            assertEquals(updatedDesc, refreshedMateri.getDeskripsi(), "Deskripsi materi harus terupdate");
            System.out.println("[PASS] Test 2: Ubah deskripsi materi tabuh berhasil.");

            // Ambil salah satu kategori untuk jadwal rutin
            List<KategoriTingkat> allKat = kategoriService.getSemuaKategori();
            int katId = allKat.isEmpty() ? 1 : allKat.get(0).getId();

            // 3. Test Buat Jadwal Latihan Sesi 1
            LocalDate tglSesi1 = LocalDate.now().minusDays(7);
            LocalTime jamMulai = LocalTime.of(16, 0);
            LocalTime jamSelesai = LocalTime.of(18, 0);
            String lokasi = "Bale Banjar Sanggar";

            JadwalLatihan j1 = new JadwalLatihan(
                    0, JenisJadwal.RUTIN, tglSesi1, jamMulai, jamSelesai, lokasi,
                    katId, null, null, "Latihan rutin mingguan sesi 1"
            );
            latihanService.simpanJadwalLatihan(j1);
            assertTrue(j1.getId() > 0, "ID Jadwal Sesi 1 harus tergenerate");
            System.out.println("[PASS] Test 3: Pembuatan Jadwal Latihan Sesi 1 berhasil (ID #" + j1.getId() + ")");

            // 4. Test Pencatatan Sesi Materi Sesi 1 + Catatan Progres
            String catatanProgres1 = "Baru selesai bagian kawitan, tempo masih agak lambat.";
            SesiMateri sm1 = new SesiMateri(0, j1.getId(), createdMateri.getId(), catatanProgres1);
            latihanService.simpanSesiMateri(sm1);
            assertTrue(sm1.getId() > 0, "ID Sesi Materi Sesi 1 harus tergenerate");

            List<SesiMateri> materiJadwal1 = latihanService.getSesiMateriByJadwal(j1.getId());
            assertEquals(1, materiJadwal1.size(), "Harus ada 1 sesi materi pada jadwal 1");
            assertEquals(catatanProgres1, materiJadwal1.get(0).getCatatanProgres(), "Catatan progres harus cocok");
            System.out.println("[PASS] Test 4: Pencatatan materi dan progres pada Sesi 1 berhasil.");

            // 5. Test Buat Jadwal Latihan Sesi 2 (Hari Ini)
            LocalDate tglSesi2 = LocalDate.now();
            JadwalLatihan j2 = new JadwalLatihan(
                    0, JenisJadwal.RUTIN, tglSesi2, jamMulai, jamSelesai, lokasi,
                    katId, null, null, "Latihan rutin mingguan sesi 2"
            );
            latihanService.simpanJadwalLatihan(j2);
            assertTrue(j2.getId() > 0, "ID Jadwal Sesi 2 harus tergenerate");
            System.out.println("[PASS] Test 5: Pembuatan Jadwal Latihan Sesi 2 berhasil (ID #" + j2.getId() + ")");

            // 6. Test Kontinuitas Progres Tabuh (Query Catatan Progres Terakhir untuk Referensi Pelatih)
            SesiMateri progresTerakhir = latihanService.getProgresTerakhirByMateri(createdMateri.getId());
            assertTrue(progresTerakhir != null, "Progres terakhir materi tabuh harus ditemukan");
            assertEquals(catatanProgres1, progresTerakhir.getCatatanProgres(), "Progres terakhir harus berasal dari Sesi 1");
            System.out.println("[PASS] Test 6: Kontinuitas progres berhasil! Catatan sesi 1 ('" + progresTerakhir.getCatatanProgres() + "') berhasil terbaca untuk referensi sesi 2.");

            // Catat progres sesi 2
            String catatanProgres2 = "Kawitan sudah lancar, mulai masuk bagian pengawak baris 1-4.";
            SesiMateri sm2 = new SesiMateri(0, j2.getId(), createdMateri.getId(), catatanProgres2);
            latihanService.simpanSesiMateri(sm2);

            SesiMateri progresTerbaru = latihanService.getProgresTerakhirByMateri(createdMateri.getId());
            assertEquals(catatanProgres2, progresTerbaru.getCatatanProgres(), "Progres terbaru harus berubah menjadi catatan sesi 2");
            System.out.println("[PASS] Test 7: Pembaruan riwayat progres sesi 2 berhasil (update otomatis referensi terbaru).");

            // 8. Test Validasi Materi Tidak Boleh Dihapus Jika Sudah Digunakan di Sesi
            boolean used = latihanService.apakahMateriSudahDigunakan(createdMateri.getId());
            assertTrue(used, "Materi harus terdeteksi sudah digunakan dalam sesi latihan");
            boolean deletePrevented = false;
            try {
                latihanService.hapusMateriTabuh(createdMateri.getId());
            } catch (Exception ex) {
                deletePrevented = true;
            }
            assertTrue(deletePrevented, "Hapus materi yang sudah digunakan di sesi harus ditolak");
            System.out.println("[PASS] Test 8: Validasi pencegahan hapus materi yang terikat sesi latihan berhasil.");

            // 9. Test Pembersihan / Hapus Jadwal (Cascade Sesi Materi)
            latihanService.hapusJadwalLatihan(j1.getId());
            latihanService.hapusJadwalLatihan(j2.getId());

            // Sekarang materi sudah tidak terikat sesi latihan, bisa dihapus
            latihanService.hapusMateriTabuh(createdMateri.getId());
            MateriTabuh deletedMateri = null;
            try {
                deletedMateri = latihanService.getMateriTabuhById(createdMateri.getId());
            } catch (Exception ignored) {
            }
            assertTrue(deletedMateri == null, "Materi harus berhasil dihapus setelah sesi dibersihkan");
            System.out.println("[PASS] Test 9: Penghapusan jadwal dan pembersihan materi berhasil.");

            System.out.println("==================================================");
            System.out.println("SEMUA 9 SKENARIO PENGUJIAN FASE 4 LULUS 100%!");
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
