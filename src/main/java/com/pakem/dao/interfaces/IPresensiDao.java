package com.pakem.dao.interfaces;

import com.pakem.model.Presensi;
import com.pakem.model.RekapPresensi;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Kontrak Abstraksi Akses Data (DAO) untuk entitas Presensi.
 * Semua query harus 100% cocok dengan kolom tabel presensi di schema.sql.
 */
public interface IPresensiDao {

    /**
     * Mengambil daftar presensi untuk suatu jadwal latihan, terurut nama anggota.
     */
    List<Presensi> findByJadwalId(int jadwalId) throws SQLException;

    /**
     * Mengambil record presensi spesifik berdasarkan jadwal dan anggota.
     */
    Presensi findByJadwalAndAnggota(int jadwalId, int anggotaId) throws SQLException;

    /**
     * Mengambil record presensi berdasarkan ID.
     */
    Presensi findById(int id) throws SQLException;

    /**
     * Menyimpan record presensi baru.
     */
    boolean save(Presensi presensi) throws SQLException;

    /**
     * Memperbarui record presensi.
     */
    boolean update(Presensi presensi) throws SQLException;

    /**
     * Upsert (Insert atau Update jika duplikat kunci jadwal_latihan_id & anggota_id).
     */
    boolean saveOrUpdate(Presensi presensi) throws SQLException;

    /**
     * Menyimpan atau memperbarui daftar presensi secara batch / transaksi.
     */
    boolean saveBatch(List<Presensi> list) throws SQLException;

    /**
     * Menghapus record presensi berdasarkan ID.
     */
    boolean delete(int id) throws SQLException;

    /**
     * Menghapus seluruh presensi untuk suatu jadwal latihan.
     */
    boolean deleteByJadwalId(int jadwalId) throws SQLException;

    /**
     * Menghitung total kehadiran anggota per status untuk satu jadwal latihan.
     */
    int countByJadwalAndStatus(int jadwalId, String statusHadir) throws SQLException;

    /**
     * Menghitung total presensi yang tercatat untuk satu jadwal latihan.
     */
    int countTotalByJadwal(int jadwalId) throws SQLException;

    /**
     * Mengambil data rekapitulasi kehadiran per anggota aktif dengan filter opsional.
     * @param kategoriId filter kategori tingkat (nullable)
     * @param subkelompokId filter subkelompok (nullable)
     * @param dariTanggal batas awal tanggal jadwal (nullable)
     * @param sampaiTanggal batas akhir tanggal jadwal (nullable)
     */
    List<RekapPresensi> getRekapitulasi(Integer kategoriId, Integer subkelompokId,
                                        LocalDate dariTanggal, LocalDate sampaiTanggal) throws SQLException;
}
