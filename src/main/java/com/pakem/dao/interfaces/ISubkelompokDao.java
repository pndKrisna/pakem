package com.pakem.dao.interfaces;

import com.pakem.model.Subkelompok;
import java.sql.SQLException;
import java.util.List;

/**
 * Interface DAO untuk entitas Subkelompok.
 */
public interface ISubkelompokDao {
    List<Subkelompok> findByKategoriId(int kategoriId) throws SQLException;
    List<Subkelompok> findAll() throws SQLException;
    Subkelompok findById(int id) throws SQLException;
}
