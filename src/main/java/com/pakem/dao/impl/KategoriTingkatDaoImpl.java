package com.pakem.dao.impl;

import com.pakem.config.DatabaseConnection;
import com.pakem.dao.interfaces.IKategoriTingkatDao;
import com.pakem.model.KategoriTingkat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementasi JDBC untuk IKategoriTingkatDao.
 */
public class KategoriTingkatDaoImpl implements IKategoriTingkatDao {

    @Override
    public List<KategoriTingkat> findAll() throws SQLException {
        List<KategoriTingkat> list = new ArrayList<>();
        String sql = "SELECT id, nama, urutan FROM kategori_tingkat ORDER BY urutan ASC, nama ASC";
        Connection conn = DatabaseConnection.getConnection();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new KategoriTingkat(
                        rs.getInt("id"),
                        rs.getString("nama"),
                        rs.getInt("urutan")
                ));
            }
        }
        return list;
    }

    @Override
    public KategoriTingkat findById(int id) throws SQLException {
        String sql = "SELECT id, nama, urutan FROM kategori_tingkat WHERE id = ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new KategoriTingkat(
                            rs.getInt("id"),
                            rs.getString("nama"),
                            rs.getInt("urutan")
                    );
                }
            }
        }
        return null;
    }
}
