package com.pakem.service.interfaces;

import com.pakem.model.Anggota;
import com.pakem.model.KategoriTingkat;
import com.pakem.model.Subkelompok;

import java.util.List;

/**
 * Kontrak Layanan Bisnis untuk Kategori Tingkat, Subkelompok, dan Auto-Grouping.
 */
public interface IKategoriService {

    // === Kategori Tingkat ===
    List<KategoriTingkat> getSemuaKategori() throws Exception;
    KategoriTingkat getKategoriById(int id) throws Exception;
    void tambahKategori(String nama, int urutan) throws Exception;
    void ubahKategori(int id, String nama, int urutan) throws Exception;
    void hapusKategori(int id) throws Exception;
    int getJumlahAnggotaKategori(int kategoriId) throws Exception;

    // === Subkelompok ===
    List<Subkelompok> getSubkelompokByKategori(int kategoriId) throws Exception;
    Subkelompok getSubkelompokById(int id) throws Exception;
    void tambahSubkelompok(int kategoriId, String nama) throws Exception;
    void ubahSubkelompok(int id, int kategoriId, String nama) throws Exception;
    void hapusSubkelompok(int id) throws Exception;
    int getJumlahAnggotaSubkelompok(int subkelompokId) throws Exception;

    // === Penempatan & Pengelompokan Anggota ===
    List<Anggota> getAnggotaByKategori(int kategoriId, boolean aktifSaja) throws Exception;
    void pindahAnggotaSubkelompok(int anggotaId, Integer subkelompokId) throws Exception;
    boolean apakahAdaAnggotaSudahPunyaSubkelompok(int kategoriId) throws Exception;
    int jalankanAutoGrouping(int kategoriId) throws Exception;

    // === Kapasitas Kelompok ===
    int getKapasitasAnjuran() throws Exception;
}
