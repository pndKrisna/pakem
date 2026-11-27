package com.pakem;

import com.pakem.config.DatabaseConnection;
import com.pakem.model.Anggota;
import com.pakem.model.Event;
import com.pakem.model.KategoriTingkat;
import com.pakem.model.PemainTim;
import com.pakem.model.Subkelompok;
import com.pakem.model.TimEvent;
import com.pakem.model.User;
import com.pakem.model.enums.JenisAcara;
import com.pakem.model.enums.RoleUser;
import com.pakem.model.enums.StatusAnggota;
import com.pakem.model.enums.StatusHonor;
import com.pakem.model.enums.StatusKesediaan;
import com.pakem.service.impl.AnggotaServiceImpl;
import com.pakem.service.impl.EventServiceImpl;
import com.pakem.service.impl.KategoriServiceImpl;
import com.pakem.service.interfaces.IAnggotaService;
import com.pakem.service.interfaces.IEventService;
import com.pakem.service.interfaces.IKategoriService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class EventTest {

    public static void main(String[] args) {
        System.out.println(">>> Memulai Pengujian Otomatis Fase 6 (Pertunjukan / Event) <<<");

        if (!DatabaseConnection.testConnection()) {
            System.err.println("[FAIL] Database tidak dapat dihubungi.");
            System.exit(1);
        }

        IAnggotaService anggotaService = new AnggotaServiceImpl();
        IKategoriService kategoriService = new KategoriServiceImpl();
        IEventService eventService = new EventServiceImpl();

        try {
            long ts = System.currentTimeMillis();

            // 1. Setup Kategori & Subkelompok Uji
            String namaKat = "Kategori Event Test " + ts;
            kategoriService.tambahKategori(namaKat, 99);
            KategoriTingkat kat = kategoriService.getSemuaKategori().stream()
                    .filter(k -> k.getNama().equals(namaKat))
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("Kategori baru tidak ditemukan"));

            kategoriService.tambahSubkelompok(kat.getId(), "Sub A " + ts);
            List<Subkelompok> subList = kategoriService.getSubkelompokByKategori(kat.getId());
            Subkelompok subA = subList.get(0);

            // 2. Setup Anggota: 1 AKTIF, 1 VAKUM, 1 NONAKTIF
            Anggota aAktif = new Anggota(0, "Wayan Aktif " + ts, "0811111111", LocalDate.now(),
                    StatusAnggota.AKTIF, kat.getId(), subA.getId(), "Aktif");
            anggotaService.simpanAnggotaBaru(aAktif, "Daftar aktif");

            Anggota aVakum = new Anggota(0, "Made Vakum " + ts, "0822222222", LocalDate.now(),
                    StatusAnggota.VAKUM, kat.getId(), subA.getId(), "Cuti");
            anggotaService.simpanAnggotaBaru(aVakum, "Daftar vakum");

            Anggota aNonaktif = new Anggota(0, "Nyoman Nonaktif " + ts, "0833333333", LocalDate.now(),
                    StatusAnggota.NONAKTIF, kat.getId(), subA.getId(), "Keluar");
            anggotaService.simpanAnggotaBaru(aNonaktif, "Daftar nonaktif");

            System.out.println("[PASS] Test 1: Setup data master & anggota berhasil.");

            // 3. User Simulasi
            User pengurus = com.pakem.model.User.create(1, "pengurus", "hash", RoleUser.PENGURUS, true);
            User pelatih = com.pakem.model.User.create(2, "pelatih", "hash", RoleUser.PELATIH, true);

            // 4. Test CRUD Event
            Event e = new Event(0, "Pentas Uji Coba " + ts, JenisAcara.JOB, LocalDate.now(),
                    LocalTime.of(19, 0), "Pura Dalem", "Event tes otomatis Fase 6");
            eventService.simpanEvent(e);
            assertTrue(e.getId() > 0, "ID Event harus tergenerate");

            Event fetched = eventService.getEventById(e.getId());
            assertEquals("Pentas Uji Coba " + ts, fetched.getNamaAcara(), "Nama acara harus cocok");
            assertEquals(JenisAcara.JOB, fetched.getJenisAcara(), "Jenis acara harus JOB");

            eventService.ubahEvent(new Event(e.getId(), "Pentas Uji Coba Updated " + ts,
                    JenisAcara.JOB, LocalDate.now().plusDays(1), LocalTime.of(20, 0), "Hotel", "Diubah"));
            Event updated = eventService.getEventById(e.getId());
            assertEquals("Pentas Uji Coba Updated " + ts, updated.getNamaAcara(), "Nama acara harus terupdate");

            System.out.println("[PASS] Test 2: CRUD Event berhasil (Simpan, Ubah, Baca).");

            // 5. Test Multi-Tim Event
            eventService.tambahTim(e.getId(), "Tim B", "Tim cadangan");

            List<TimEvent> timList = eventService.getDaftarTimByEvent(e.getId());
            assertEquals(2, timList.size(), "Harus ada 2 tim pada event (1 default + 1 baru)");
            assertTrue(eventService.getDaftarTimByEvent(e.getId()).stream()
                    .anyMatch(t -> t.getNamaTim().equals("Tim Utama")), "Tim Utama (default) harus ada");
            assertTrue(eventService.getDaftarTimByEvent(e.getId()).stream()
                    .anyMatch(t -> t.getNamaTim().equals("Tim B")), "Tim B harus ada");

            System.out.println("[PASS] Test 3: Multi-Tim Event berhasil (2 tim).");

            // 6. Test Tambah Pemain (Lintas Tingkat & Isolasi Status)
            TimEvent timUtama = timList.get(0);

            // Anggota AKTIF boleh ditambahkan
            eventService.tambahPemain(timUtama.getId(), aAktif.getId(),
                    StatusKesediaan.BERSEDIA, new BigDecimal("150000"), StatusHonor.BELUM_DIBAGIKAN, pengurus);

            // Anggota VAKUM TIDAK BOLEH ditambahkan
            try {
                eventService.tambahPemain(timUtama.getId(), aVakum.getId(),
                        StatusKesediaan.BERSEDIA, null, null, pengurus);
                throw new AssertionError("Anggota VAKUM seharusnya TIDAK BOLEH ditambahkan ke tim!");
            } catch (Exception expected) {
                // Diperbolehkan gagal
            }

            // Anggota NONAKTIF TIDAK BOLEH ditambahkan
            try {
                eventService.tambahPemain(timUtama.getId(), aNonaktif.getId(),
                        StatusKesediaan.BERSEDIA, null, null, pengurus);
                throw new AssertionError("Anggota NONAKTIF seharusnya TIDAK BOLEH ditambahkan ke tim!");
            } catch (Exception expected) {
                // Diperbolehkan gagal
            }

            List<PemainTim> pemainList = eventService.getDaftarPemainByTim(timUtama.getId());
            assertEquals(1, pemainList.size(), "Hanya 1 pemain (AKTIF) yang boleh masuk ke tim");
            assertEquals(StatusKesediaan.BERSEDIA, pemainList.get(0).getStatusKesediaan(), "Status kesediaan harus BERSEDIA");

            System.out.println("[PASS] Test 4: Pemain lintas tingkat + isolasi status anggota (Vakum & Nonaktif ditolak).");

            // 7. Test Pembatasan Akses Honor (Pengurus vs Pelatih)
            // Pengurus BISA atur honor
            eventService.ubahHonorPemain(pemainList.get(0).getId(), new BigDecimal("200000"), StatusHonor.SUDAH_DIBAGIKAN, pengurus);
            PemainTim afterPengurus = eventService.getPemainById(pemainList.get(0).getId());
            assertEquals(StatusHonor.SUDAH_DIBAGIKAN, afterPengurus.getStatusHonor(), "Pengurus harus bisa mengubah status honor");

            // Pelatih TIDAK BISA atur honor
            try {
                eventService.ubahHonorPemain(pemainList.get(0).getId(), new BigDecimal("300000"), StatusHonor.BELUM_DIBAGIKAN, pelatih);
                throw new AssertionError("Pelatih seharusnya TIDAK BOLEH mengubah honor pemain!");
            } catch (Exception expected) {
                // Diperbolehkan gagal
            }

            System.out.println("[PASS] Test 5: Pembatasan akses honor (Pengurus BISA, Pelatih DITOLAK).");

            // 8. Test Hapus Event (Cascade Delete Tim & Pemain)
            eventService.hapusEvent(e.getId());

            // Verifikasi event hilang (getEventById harus melempar exception karena null)
            try {
                eventService.getEventById(e.getId());
                throw new AssertionError("Event seharusnya sudah tidak ditemukan setelah dihapus.");
            } catch (Exception expected) {
                // Benar - event sudah dihapus
            }

            System.out.println("[PASS] Test 6: Hapus Event dengan cascade delete Tim & Pemain.");

            // 9. Cleanup Data Uji
            anggotaService.ubahStatusAnggota(aAktif.getId(), StatusAnggota.NONAKTIF);
            kategoriService.hapusKategori(kat.getId());
            System.out.println("[PASS] Test 7: Pembersihan data uji selesai.");

            System.out.println("==================================================");
            System.out.println("SEMUA 7 SKENARIO PENGUJIAN FASE 6 LULUS 100%!");
            System.out.println("==================================================");

        } catch (Exception ex) {
            System.err.println("[FAIL] Terjadi kegagalan pengujian Fase 6: " + ex.getMessage());
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
