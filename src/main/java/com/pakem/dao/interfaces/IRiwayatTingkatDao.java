package com.pakem.dao.interfaces;

import com.pakem.model.RiwayatTingkat;
import java.sql.SQLException;
import java.util.List;

/**
 * Interface DAO untuk entitas Riwayat Perubahan Tingkat.
 */
public interface IRiwayatTingkatDao {
    boolean save(RiwayatTingkat riwayat) throws SQLException;
    List<RiwayatTingkat> findByAnggotaId(int anggotaId) throws SQLException;
}
