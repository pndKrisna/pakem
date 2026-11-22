package com.pakem.service.interfaces;

import com.pakem.model.Anggota;
import com.pakem.model.KategoriTingkat;
import com.pakem.model.RiwayatTingkat;
import com.pakem.model.Subkelompok;
import com.pakem.model.enums.StatusAnggota;

import java.util.List;
import java.util.Map;

/**
 * Kontrak Layanan Bisnis untuk Manajemen Data Anggota Sanggar.
 */
public interface IAnggotaService {

    /**
     * Mengambil daftar anggota dengan filter status dan pencarian kata kunci.
     */
    List<Anggota> getDaftarAnggota(StatusAnggota filterStatus, String searchKeyword) throws Exception;

    /**
     * Mengambil data anggota berdasarkan ID.
     */
    Anggota getAnggotaById(int id) throws Exception;

    /**
     * Mendaftarkan anggota baru.
     * Jika kategori tingkat diisi, otomatis mencatat entri riwayat tingkat awal.
     */
    void simpanAnggotaBaru(Anggota anggota, String keteranganAwal) throws Exception;

    /**
     * Memperbarui informasi anggota.
     * Jika terjadi perubahan kategori tingkat, otomatis mencatat perubahan ke riwayat_tingkat.
     */
    void perbaruiAnggota(Anggota anggota, Integer kategoriTingkatBaruId, String keteranganTingkat) throws Exception;

    /**
     * Mengubah status keanggotaan (Aktif, Vakum, Nonaktif).
     */
    void ubahStatusAnggota(int anggotaId, StatusAnggota statusBaru) throws Exception;

    /**
     * Mengambil seluruh catatan riwayat mutasi tingkat dari seorang anggota.
     */
    List<RiwayatTingkat> getRiwayatTingkatAnggota(int anggotaId) throws Exception;

    /**
     * Mengambil seluruh master kategori tingkat untuk dropdown form.
     */
    List<KategoriTingkat> getSemuaKategori() throws Exception;

    /**
     * Mengambil daftar subkelompok yang bernaung di bawah kategori tingkat tertentu.
     */
    List<Subkelompok> getSubkelompokByKategori(int kategoriId) throws Exception;

    /**
     * Mengambil ringkasan jumlah anggota berdasarkan status (Total, Aktif, Vakum, Nonaktif).
     */
    Map<String, Integer> getRingkasanJumlahAnggota() throws Exception;
}
