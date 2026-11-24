package com.pakem.service.interfaces;

import com.pakem.model.JadwalLatihan;
import com.pakem.model.Presensi;
import com.pakem.model.RekapPresensi;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Kontrak Layanan Bisnis untuk Modul Presensi Latihan (Fase 5).
 */
public interface IPresensiService {

    /**
     * Mengambil daftar lembar presensi untuk suatu jadwal latihan.
     * Mengisolasi status: hanya anggota berstatus AKTIF yang dimunculkan.
     * Menggabungkan catatan presensi yang sudah tersimpan dengan anggota aktif yang berlaku.
     */
    List<Presensi> getLembarPresensiJadwal(int jadwalId) throws Exception;

    /**
     * Menyimpan atau memperbarui seluruh daftar presensi dalam satu sesi latihan.
     */
    void simpanDaftarPresensi(List<Presensi> daftarPresensi) throws Exception;

    /**
     * Menyimpan atau memperbarui satu record presensi anggota.
     */
    void simpanPresensi(Presensi presensi) throws Exception;

    /**
     * Menghapus record presensi berdasarkan ID.
     */
    void hapusPresensi(int presensiId) throws Exception;

    /**
     * Menghapus seluruh presensi untuk suatu jadwal latihan.
     */
    void hapusPresensiByJadwal(int jadwalId) throws Exception;

    /**
     * Mengambil ringkasan statistik kehadiran untuk satu jadwal latihan
     * (TOTAL, HADIR, IZIN, SAKIT, ALPA).
     */
    Map<String, Integer> getRingkasanPresensiJadwal(int jadwalId) throws Exception;

    /**
     * Mengambil rekapitulasi kehadiran seluruh anggota aktif berdasarkan filter.
     */
    List<RekapPresensi> getRekapitulasi(Integer kategoriId, Integer subkelompokId,
                                        LocalDate dariTanggal, LocalDate sampaiTanggal) throws Exception;
}
