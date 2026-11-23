package com.pakem.dao.interfaces;

import com.pakem.model.JadwalLatihan;
import java.sql.SQLException;
import java.util.List;
import java.time.LocalDate;

/**
 * Kontrak Abstraksi Akses Data (DAO) untuk entitas Jadwal Latihan.
 */
public interface IJadwalLatihanDao {

    List<JadwalLatihan> findAll() throws SQLException;
    List<JadwalLatihan> findByKategori(int kategoriId) throws SQLException;
    List<JadwalLatihan> findBySubkelompok(int subkelompokId) throws SQLException;
    List<JadwalLatihan> findByTanggal(LocalDate tanggal) throws SQLException;
    JadwalLatihan findById(int id) throws SQLException;
    boolean save(JadwalLatihan jadwal) throws SQLException;
    boolean update(JadwalLatihan jadwal) throws SQLException;
    boolean delete(int id) throws SQLException;
    int countSesiMateri(int jadwalId) throws SQLException;
}
