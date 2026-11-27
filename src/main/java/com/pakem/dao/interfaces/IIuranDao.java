package com.pakem.dao.interfaces;

import com.pakem.model.Iuran;
import java.sql.SQLException;
import java.util.List;

public interface IIuranDao {
    List<Iuran> findByPeriode(int bulan, int tahun) throws SQLException;
    Iuran findByAnggotaAndPeriode(int anggotaId, int bulan, int tahun) throws SQLException;
    boolean save(Iuran iuran) throws SQLException;
    boolean update(Iuran iuran) throws SQLException;
    boolean saveOrUpdate(Iuran iuran) throws SQLException;
    boolean delete(int id) throws SQLException;
    boolean delete(int anggotaId, int bulan, int tahun) throws SQLException;
}
