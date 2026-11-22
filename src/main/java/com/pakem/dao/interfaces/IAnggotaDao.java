package com.pakem.dao.interfaces;

import com.pakem.model.Anggota;
import com.pakem.model.enums.StatusAnggota;
import java.sql.SQLException;
import java.util.List;

/**
 * Kontrak Abstraksi Akses Data (DAO) untuk entitas Anggota.
 */
public interface IAnggotaDao {

    /**
     * Mengambil daftar anggota dengan filter status opsional dan pencarian kata kunci.
     * @param filterStatus status anggota (bisa null untuk mengambil semua status)
     * @param searchKeyword pencarian nama atau kontak (bisa null atau kosong)
     */
    List<Anggota> findAll(StatusAnggota filterStatus, String searchKeyword) throws SQLException;

    /**
     * Mengambil data anggota berdasarkan ID.
     */
    Anggota findById(int id) throws SQLException;

    /**
     * Mengambil seluruh anggota di dalam kategori tertentu.
     */
    List<Anggota> findByKategoriId(int kategoriId, boolean aktifSaja) throws SQLException;

    /**
     * Menyimpan data anggota baru ke dalam database.
     */
    boolean save(Anggota anggota) throws SQLException;

    /**
     * Memperbarui informasi data anggota.
     */
    boolean update(Anggota anggota) throws SQLException;

    /**
     * Memperbarui status keanggotaan (AKTIF, VAKUM, NONAKTIF).
     */
    boolean updateStatus(int id, StatusAnggota newStatus) throws SQLException;

    /**
     * Memperbarui penempatan subkelompok seorang anggota.
     */
    boolean updateSubkelompok(int id, Integer subkelompokId) throws SQLException;

    /**
     * Menghitung berapa anggota aktif di kategori ini yang sudah memiliki subkelompok.
     */
    int countHavingSubkelompokInKategori(int kategoriId) throws SQLException;

    /**
     * Menghitung jumlah anggota berdasarkan status.
     */
    int countByStatus(StatusAnggota status) throws SQLException;

    /**
     * Menghitung total keseluruhan anggota.
     */
    int countTotal() throws SQLException;
}
