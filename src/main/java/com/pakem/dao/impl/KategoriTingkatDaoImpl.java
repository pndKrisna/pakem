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

    @Override
    public boolean save(KategoriTingkat kategori) throws SQLException {
        String sql = "INSERT INTO kategori_tingkat (nama, urutan) VALUES (?, ?)";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, kategori.getNama());
            stmt.setInt(2, kategori.getUrutan());
            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        kategori.setId(keys.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean update(KategoriTingkat kategori) throws SQLException {
        String sql = "UPDATE kategori_tingkat SET nama = ?, urutan = ? WHERE id = ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, kategori.getNama());
            stmt.setInt(2, kategori.getUrutan());
            stmt.setInt(3, kategori.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM kategori_tingkat WHERE id = ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public int countAnggota(int kategoriId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM anggota WHERE kategori_tingkat_id = ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, kategoriId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    @Override
    public boolean existsByNama(String nama, Integer excludeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM kategori_tingkat WHERE LOWER(nama) = LOWER(?)" +
                     (excludeId != null ? " AND id <> ?" : "");
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nama.trim());
            if (excludeId != null) {
                stmt.setInt(2, excludeId);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
}
