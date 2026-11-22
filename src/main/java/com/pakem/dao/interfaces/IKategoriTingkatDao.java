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
    boolean save(KategoriTingkat kategori) throws SQLException;
    boolean update(KategoriTingkat kategori) throws SQLException;
    boolean delete(int id) throws SQLException;
    int countAnggota(int kategoriId) throws SQLException;
    boolean existsByNama(String nama, Integer excludeId) throws SQLException;
}
