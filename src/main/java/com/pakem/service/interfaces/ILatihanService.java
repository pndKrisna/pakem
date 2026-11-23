package com.pakem.service.interfaces;

import com.pakem.model.JadwalLatihan;
import com.pakem.model.MateriTabuh;
import com.pakem.model.SesiMateri;

import java.time.LocalDate;
import java.util.List;

/**
 * Kontrak Layanan Bisnis untuk Jadwal Latihan & Materi Tabuh (Fase 4).
 */
public interface ILatihanService {

    // === Materi Tabuh ===
    List<MateriTabuh> getSemuaMateriTabuh() throws Exception;
    MateriTabuh getMateriTabuhById(int id) throws Exception;
    void tambahMateriTabuh(String nama, String deskripsi) throws Exception;
    void ubahMateriTabuh(int id, String nama, String deskripsi) throws Exception;
    void hapusMateriTabuh(int id) throws Exception;
    boolean apakahMateriSudahDigunakan(int materiId) throws Exception;

    // === Jadwal Latihan ===
    List<JadwalLatihan> getSemuaJadwalLatihan() throws Exception;
    List<JadwalLatihan> getJadwalByKategori(int kategoriId) throws Exception;
    List<JadwalLatihan> getJadwalBySubkelompok(int subkelompokId) throws Exception;
    List<JadwalLatihan> getJadwalByTanggal(LocalDate tanggal) throws Exception;
    JadwalLatihan getJadwalById(int id) throws Exception;
    void simpanJadwalLatihan(JadwalLatihan jadwal) throws Exception;
    void ubahJadwalLatihan(JadwalLatihan jadwal) throws Exception;
    void hapusJadwalLatihan(int id) throws Exception;

    // === Sesi Materi (Progres) ===
    List<SesiMateri> getSesiMateriByJadwal(int jadwalId) throws Exception;
    List<SesiMateri> getSesiMateriByMateri(int materiId) throws Exception;
    SesiMateri getSesiMateriById(int id) throws Exception;
    void simpanSesiMateri(SesiMateri sesi) throws Exception;
    void ubahSesiMateri(SesiMateri sesi) throws Exception;
    void hapusSesiMateri(int id) throws Exception;
    void hapusSesiMateriByJadwal(int jadwalId) throws Exception;

    /**
     * Mengambil catatan progres materi terakhir untuk suatu materi tabuh,
     * digunakan sebagai referensi pelatih saat menyambung materi latihan berikutnya.
     */
    SesiMateri getProgresTerakhirByMateri(int materiId) throws Exception;
}
