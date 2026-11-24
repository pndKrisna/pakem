package com.pakem;

import com.pakem.config.DatabaseConnection;
import com.pakem.model.Anggota;
import com.pakem.model.JadwalLatihan;
import com.pakem.model.KategoriTingkat;
import com.pakem.model.Presensi;
import com.pakem.model.RekapPresensi;
import com.pakem.model.Subkelompok;
import com.pakem.model.enums.JenisJadwal;
import com.pakem.model.enums.StatusAnggota;
import com.pakem.model.enums.StatusHadir;
import com.pakem.service.impl.AnggotaServiceImpl;
import com.pakem.service.impl.KategoriServiceImpl;
import com.pakem.service.impl.LatihanServiceImpl;
import com.pakem.service.impl.PresensiServiceImpl;
import com.pakem.service.interfaces.IAnggotaService;
import com.pakem.service.interfaces.IKategoriService;
import com.pakem.service.interfaces.ILatihanService;
import com.pakem.service.interfaces.IPresensiService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public class PresensiTest {

    public static void main(String[] args) {
        System.out.println(">>> Memulai Pengujian Otomatis Fase 5 (Presensi Latihan & Rekapitulasi) <<<");

        if (!DatabaseConnection.testConnection()) {
            System.err.println("[FAIL] Database tidak dapat dihubungi.");
            System.exit(1);
        }

        IAnggotaService anggotaService = new AnggotaServiceImpl();
        IKategoriService kategoriService = new KategoriServiceImpl();
        ILatihanService latihanService = new LatihanServiceImpl();
        IPresensiService presensiService = new PresensiServiceImpl();

        try {
            long ts = System.currentTimeMillis();

            // 1. Setup Kategori & Subkelompok Uji
            String namaKat = "Kategori Presensi Test " + ts;
            kategoriService.tambahKategori(namaKat, 99);
            KategoriTingkat kat = kategoriService.getSemuaKategori().stream()
                    .filter(k -> k.getNama().equals(namaKat))
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("Kategori baru tidak ditemukan"));

            kategoriService.tambahSubkelompok(kat.getId(), "Sub A " + ts);
            List<Subkelompok> subList = kategoriService.getSubkelompokByKategori(kat.getId());
            Subkelompok subA = subList.get(0);

            // Setup 3 Anggota: 1 AKTIF, 1 VAKUM, 1 NONAKTIF
            Anggota aAktif = new Anggota(0, "Wayan Aktif " + ts, "0811111111", LocalDate.now(),
                    StatusAnggota.AKTIF, kat.getId(), subA.getId(), "Anggota aktif");
            anggotaService.simpanAnggotaBaru(aAktif, "Daftar aktif");

            Anggota aVakum = new Anggota(0, "Made Vakum " + ts, "0822222222", LocalDate.now(),
                    StatusAnggota.VAKUM, kat.getId(), subA.getId(), "Anggota cuti");
            anggotaService.simpanAnggotaBaru(aVakum, "Daftar vakum");

            Anggota aNonaktif = new Anggota(0, "Nyoman Nonaktif " + ts, "0833333333", LocalDate.now(),
                    StatusAnggota.NONAKTIF, kat.getId(), subA.getId(), "Anggota keluar");
            anggotaService.simpanAnggotaBaru(aNonaktif, "Daftar nonaktif");

            // Setup 1 Anggota Aktif kedua untuk variasi presensi
            Anggota aAktif2 = new Anggota(0, "Ketut Aktif " + ts, "0844444444", LocalDate.now(),
                    StatusAnggota.AKTIF, kat.getId(), subA.getId(), "Anggota aktif kedua");
            anggotaService.simpanAnggotaBaru(aAktif2, "Daftar aktif 2");

            System.out.println("[PASS] Test 1: Setup data master & anggota berhasil.");

            // 2. Setup Jadwal Latihan & Uji Isolasi Status pada Lembar Presensi
            JadwalLatihan jadwal1 = new JadwalLatihan(
                    0, JenisJadwal.RUTIN, LocalDate.now(), LocalTime.of(16, 0), LocalTime.of(18, 0),
                    "Balai Latihan Uji", kat.getId(), subA.getId(), null, "Sesi Uji Presensi 1"
            );
            latihanService.simpanJadwalLatihan(jadwal1);
            assertTrue(jadwal1.getId() > 0, "ID Jadwal Latihan harus tergenerate");

            List<Presensi> lembarPresensi = presensiService.getLembarPresensiJadwal(jadwal1.getId());

            // Verifikasi ISOLASI STATUS:
            // Hanya anggota AKTIF yang boleh masuk di lembar presensi
            boolean adaAktif1 = lembarPresensi.stream().anyMatch(p -> p.getAnggotaId() == aAktif.getId());
            boolean adaAktif2 = lembarPresensi.stream().anyMatch(p -> p.getAnggotaId() == aAktif2.getId());
            boolean adaVakum = lembarPresensi.stream().anyMatch(p -> p.getAnggotaId() == aVakum.getId());
            boolean adaNonaktif = lembarPresensi.stream().anyMatch(p -> p.getAnggotaId() == aNonaktif.getId());

            assertTrue(adaAktif1, "Anggota AKTIF 1 harus muncul di lembar presensi");
            assertTrue(adaAktif2, "Anggota AKTIF 2 harus muncul di lembar presensi");
            assertTrue(!adaVakum, "Anggota VAKUM TIDAK BOLEH muncul di lembar presensi!");
            assertTrue(!adaNonaktif, "Anggota NONAKTIF TIDAK BOLEH muncul di lembar presensi!");
            assertEquals(2, lembarPresensi.size(), "Jumlah peserta harus tepat 2 (hanya anggota AKTIF)");

            System.out.println("[PASS] Test 2: Isolasi status anggota teruji 100%! (Vakum dan Nonaktif otomatis diabaikan).");

            // 3. Test Simpan Presensi Batch
            Presensi p1 = lembarPresensi.stream().filter(p -> p.getAnggotaId() == aAktif.getId()).findFirst().get();
            p1.setStatusHadir(StatusHadir.HADIR);
            p1.setKeterangan("Hadir tepat waktu");

            Presensi p2 = lembarPresensi.stream().filter(p -> p.getAnggotaId() == aAktif2.getId()).findFirst().get();
            p2.setStatusHadir(StatusHadir.IZIN);
            p2.setKeterangan("Izin ada upacara keluarga");

            presensiService.simpanDaftarPresensi(lembarPresensi);

            List<Presensi> savedList = presensiService.getLembarPresensiJadwal(jadwal1.getId());
            assertEquals(2, savedList.size(), "Presensi tersimpan harus berjumlah 2");
            Presensi checkP1 = savedList.stream().filter(p -> p.getAnggotaId() == aAktif.getId()).findFirst().get();
            assertEquals(StatusHadir.HADIR, checkP1.getStatusHadir(), "Status hadir anggota 1 harus HADIR");
            assertEquals("Hadir tepat waktu", checkP1.getKeterangan(), "Keterangan harus tersimpan");

            Presensi checkP2 = savedList.stream().filter(p -> p.getAnggotaId() == aAktif2.getId()).findFirst().get();
            assertEquals(StatusHadir.IZIN, checkP2.getStatusHadir(), "Status hadir anggota 2 harus IZIN");

            System.out.println("[PASS] Test 3: Simpan presensi batch berhasil disimpan dan terbaca akurat.");

            // 4. Test Ringkasan Statistik Presensi Jadwal
            Map<String, Integer> stats = presensiService.getRingkasanPresensiJadwal(jadwal1.getId());
            assertEquals(2, (int) stats.get("TOTAL"), "Total presensi harus 2");
            assertEquals(1, (int) stats.get("HADIR"), "Jumlah Hadir harus 1");
            assertEquals(1, (int) stats.get("IZIN"), "Jumlah Izin harus 1");
            assertEquals(0, (int) stats.get("SAKIT"), "Jumlah Sakit harus 0");
            assertEquals(0, (int) stats.get("ALPA"), "Jumlah Alpa harus 0");
            System.out.println("[PASS] Test 4: Perhitungan ringkasan statistik sesi presensi akurat.");

            // 5. Test Update Status Kehadiran (Ubah Izin -> Sakit)
            checkP2.setStatusHadir(StatusHadir.SAKIT);
            checkP2.setKeterangan("Surat keterangan dokter menyusul");
            presensiService.simpanPresensi(checkP2);

            Map<String, Integer> statsUpdated = presensiService.getRingkasanPresensiJadwal(jadwal1.getId());
            assertEquals(0, (int) statsUpdated.get("IZIN"), "Jumlah Izin harus 0 setelah update");
            assertEquals(1, (int) statsUpdated.get("SAKIT"), "Jumlah Sakit harus 1 setelah update");
            System.out.println("[PASS] Test 5: Pembaruan status presensi berhasil.");

            // 6. Test Jadwal Sesi 2 & Perhitungan Akumulasi Rekapitulasi Presensi
            JadwalLatihan jadwal2 = new JadwalLatihan(
                    0, JenisJadwal.RUTIN, LocalDate.now().plusDays(1), LocalTime.of(16, 0), LocalTime.of(18, 0),
                    "Balai Latihan Uji", kat.getId(), subA.getId(), null, "Sesi Uji Presensi 2"
            );
            latihanService.simpanJadwalLatihan(jadwal2);

            List<Presensi> lembar2 = presensiService.getLembarPresensiJadwal(jadwal2.getId());
            for (Presensi p : lembar2) {
                if (p.getAnggotaId() == aAktif.getId()) {
                    p.setStatusHadir(StatusHadir.HADIR);
                } else if (p.getAnggotaId() == aAktif2.getId()) {
                    p.setStatusHadir(StatusHadir.TANPA_KETERANGAN);
                }
            }
            presensiService.simpanDaftarPresensi(lembar2);

            // Cek Rekapitulasi untuk Kategori ini
            List<RekapPresensi> rekap = presensiService.getRekapitulasi(kat.getId(), subA.getId(), null, null);
            assertTrue(!rekap.isEmpty(), "Data rekapitulasi tidak boleh kosong");

            RekapPresensi rAktif1 = rekap.stream().filter(r -> r.getAnggotaId() == aAktif.getId()).findFirst().get();
            assertEquals(2, rAktif1.getTotalPertemuan(), "Total sesi anggota 1 harus 2");
            assertEquals(2, rAktif1.getJumlahHadir(), "Hadir anggota 1 harus 2");
            assertEquals(100.0, rAktif1.getPersentaseKehadiran(), "Persentase kehadiran anggota 1 harus 100%");

            RekapPresensi rAktif2 = rekap.stream().filter(r -> r.getAnggotaId() == aAktif2.getId()).findFirst().get();
            assertEquals(2, rAktif2.getTotalPertemuan(), "Total sesi anggota 2 harus 2");
            assertEquals(0, rAktif2.getJumlahHadir(), "Hadir anggota 2 harus 0");
            assertEquals(1, rAktif2.getJumlahSakit(), "Sakit anggota 2 harus 1");
            assertEquals(1, rAktif2.getJumlahAlpa(), "Alpa anggota 2 harus 1");
            assertEquals(0.0, rAktif2.getPersentaseKehadiran(), "Persentase kehadiran anggota 2 harus 0%");

            System.out.println("[PASS] Test 6: Akumulasi rekapitulasi & persentase keaktifan teruji akurat.");

            // 7. Test Cascade Delete: Hapus jadwal latihan otomatis membersihkan record presensi
            latihanService.hapusJadwalLatihan(jadwal1.getId());
            latihanService.hapusJadwalLatihan(jadwal2.getId());

            int countP1 = presensiService.getRingkasanPresensiJadwal(jadwal1.getId()).get("TOTAL");
            int countP2 = presensiService.getRingkasanPresensiJadwal(jadwal2.getId()).get("TOTAL");
            assertEquals(0, countP1, "Presensi jadwal 1 harus terhapus cascade");
            assertEquals(0, countP2, "Presensi jadwal 2 harus terhapus cascade");
            System.out.println("[PASS] Test 7: Cascade delete presensi pada penghapusan jadwal berhasil.");

            // 8. Cleanup Data Uji Anggota & Kategori
            anggotaService.ubahStatusAnggota(aAktif.getId(), StatusAnggota.NONAKTIF);
            anggotaService.ubahStatusAnggota(aAktif2.getId(), StatusAnggota.NONAKTIF);
            kategoriService.hapusKategori(kat.getId());
            System.out.println("[PASS] Test 8: Pembersihan data uji selesai.");

            System.out.println("==================================================");
            System.out.println("SEMUA 8 SKENARIO PENGUJIAN FASE 5 LULUS 100%!");
            System.out.println("==================================================");

        } catch (Exception ex) {
            System.err.println("[FAIL] Terjadi kegagalan pengujian Fase 5: " + ex.getMessage());
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
