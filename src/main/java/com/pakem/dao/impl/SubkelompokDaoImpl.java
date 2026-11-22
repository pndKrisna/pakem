package com.pakem.dao.impl;

import com.pakem.config.DatabaseConnection;
import com.pakem.dao.interfaces.ISubkelompokDao;
import com.pakem.model.Subkelompok;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementasi JDBC untuk ISubkelompokDao.
 */
public class SubkelompokDaoImpl implements ISubkelompokDao {

    @Override
    public List<Subkelompok> findByKategoriId(int kategoriId) throws SQLException {
        List<Subkelompok> list = new ArrayList<>();
        String sql = "SELECT s.id, s.kategori_tingkat_id, s.nama, k.nama AS nama_kategori " +
                     "FROM subkelompok s " +
                     "JOIN kategori_tingkat k ON s.kategori_tingkat_id = k.id " +
                     "WHERE s.kategori_tingkat_id = ? " +
                     "ORDER BY s.nama ASC";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, kategoriId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Subkelompok sub = new Subkelompok(
                            rs.getInt("id"),
                            rs.getInt("kategori_tingkat_id"),
                            rs.getString("nama")
                    );
                    sub.setNamaKategori(rs.getString("nama_kategori"));
                    list.add(sub);
                }
            }
        }
        return list;
    }

    @Override
    public List<Subkelompok> findAll() throws SQLException {
        List<Subkelompok> list = new ArrayList<>();
        String sql = "SELECT s.id, s.kategori_tingkat_id, s.nama, k.nama AS nama_kategori " +
                     "FROM subkelompok s " +
                     "JOIN kategori_tingkat k ON s.kategori_tingkat_id = k.id " +
                     "ORDER BY k.urutan ASC, s.nama ASC";
        Connection conn = DatabaseConnection.getConnection();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Subkelompok sub = new Subkelompok(
                        rs.getInt("id"),
                        rs.getInt("kategori_tingkat_id"),
                        rs.getString("nama")
                );
                sub.setNamaKategori(rs.getString("nama_kategori"));
                list.add(sub);
            }
        }
        return list;
    }

    @Override
    public Subkelompok findById(int id) throws SQLException {
        String sql = "SELECT s.id, s.kategori_tingkat_id, s.nama, k.nama AS nama_kategori " +
                     "FROM subkelompok s " +
                     "JOIN kategori_tingkat k ON s.kategori_tingkat_id = k.id " +
                     "WHERE s.id = ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Subkelompok sub = new Subkelompok(
                            rs.getInt("id"),
                            rs.getInt("kategori_tingkat_id"),
                            rs.getString("nama")
                    );
                    sub.setNamaKategori(rs.getString("nama_kategori"));
                    return sub;
                }
            }
        }
        return null;
    }

    @Override
    public boolean save(Subkelompok subkelompok) throws SQLException {
        String sql = "INSERT INTO subkelompok (kategori_tingkat_id, nama) VALUES (?, ?)";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, subkelompok.getKategoriTingkatId());
            stmt.setString(2, subkelompok.getNama());
            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        subkelompok.setId(keys.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean update(Subkelompok subkelompok) throws SQLException {
        String sql = "UPDATE subkelompok SET nama = ? WHERE id = ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, subkelompok.getNama());
            stmt.setInt(2, subkelompok.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM subkelompok WHERE id = ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public int countAnggota(int subkelompokId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM anggota WHERE subkelompok_id = ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, subkelompokId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    @Override
    public boolean existsByNamaInKategori(int kategoriId, String nama, Integer excludeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM subkelompok WHERE kategori_tingkat_id = ? AND LOWER(nama) = LOWER(?)" +
                     (excludeId != null ? " AND id <> ?" : "");
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, kategoriId);
            stmt.setString(2, nama.trim());
            if (excludeId != null) {
                stmt.setInt(3, excludeId);
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
