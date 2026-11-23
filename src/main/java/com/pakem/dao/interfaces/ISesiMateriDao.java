package com.pakem.dao.interfaces;

import com.pakem.model.SesiMateri;
import java.sql.SQLException;
import java.util.List;

/**
 * Kontrak Abstraksi Akses Data (DAO) untuk entitas Sesi Materi.
 */
public interface ISesiMateriDao {

    List<SesiMateri> findByJadwalId(int jadwalId) throws SQLException;
    List<SesiMateri> findByMateriId(int materiId) throws SQLException;
    SesiMateri findById(int id) throws SQLException;
    boolean save(SesiMateri sesi) throws SQLException;
    boolean update(SesiMateri sesi) throws SQLException;
    boolean delete(int id) throws SQLException;
    boolean deleteByJadwalId(int jadwalId) throws SQLException;
    SesiMateri findProgresTerakhirByMateri(int materiId) throws SQLException;
}
