package com.pakem.dao.interfaces;

import com.pakem.model.KategoriTingkat;
import java.sql.SQLException;
import java.util.List;

/**
 * Interface DAO untuk entitas Kategori Tingkat.
 */
public interface IKategoriTingkatDao {
    List<KategoriTingkat> findAll() throws SQLException;
    KategoriTingkat findById(int id) throws SQLException;
}
