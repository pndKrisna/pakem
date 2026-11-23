package com.pakem.dao.interfaces;

import com.pakem.model.JadwalLatihan;
import com.pakem.model.MateriTabuh;
import com.pakem.model.SesiMateri;
import java.sql.SQLException;
import java.util.List;
import java.time.LocalDate;

/**
 * Kontrak Abstraksi Akses Data (DAO) untuk entitas Materi Tabuh.
 */
public interface IMateriTabuhDao {

    List<MateriTabuh> findAll() throws SQLException;
    MateriTabuh findById(int id) throws SQLException;
    boolean save(MateriTabuh materi) throws SQLException;
    boolean update(MateriTabuh materi) throws SQLException;
    boolean delete(int id) throws SQLException;
    boolean existsByNama(String nama, Integer excludeId) throws SQLException;
    int countDigunakanDiSesi(int materiId) throws SQLException;
}
