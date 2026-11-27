package com.pakem.dao.interfaces;

import com.pakem.model.PemainTim;
import com.pakem.model.enums.StatusHonor;
import com.pakem.model.enums.StatusKesediaan;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

/**
 * Kontrak Abstraksi Akses Data (DAO) untuk entitas Pemain Tim Event.
 */
public interface IPemainTimDao {

    List<PemainTim> findByTimId(int timEventId) throws SQLException;
    List<PemainTim> findByEventId(int eventId) throws SQLException;
    PemainTim findById(int id) throws SQLException;
    PemainTim findByTimAndAnggota(int timEventId, int anggotaId) throws SQLException;
    boolean save(PemainTim pemain) throws SQLException;
    boolean update(PemainTim pemain) throws SQLException;
    boolean updateKesediaan(int id, StatusKesediaan statusKesediaan) throws SQLException;
    boolean updateHonor(int id, BigDecimal nominalHonor, StatusHonor statusHonor) throws SQLException;
    boolean delete(int id) throws SQLException;
    boolean deleteByTimId(int timEventId) throws SQLException;
    boolean existsByTimAndAnggota(int timEventId, int anggotaId) throws SQLException;
    int countByKesediaan(int timEventId, StatusKesediaan statusKesediaan) throws SQLException;
}
