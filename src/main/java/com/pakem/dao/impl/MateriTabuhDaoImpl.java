package com.pakem.dao.impl;

import com.pakem.config.DatabaseConnection;
import com.pakem.dao.interfaces.IMateriTabuhDao;
import com.pakem.model.MateriTabuh;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementasi JDBC untuk IMateriTabuhDao.
 */
public class MateriTabuhDaoImpl implements IMateriTabuhDao {

    @Override
    public List<MateriTabuh> findAll() throws SQLException {
        List<MateriTabuh> list = new ArrayList<>();
        String sql = "SELECT id, nama, deskripsi FROM materi_tabuh ORDER BY nama ASC";
        Connection conn = DatabaseConnection.getConnection();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new MateriTabuh(
                        rs.getInt("id"),
                        rs.getString("nama"),
                        rs.getString("deskripsi")
                ));
            }
        }
        return list;
    }

    @Override
    public MateriTabuh findById(int id) throws SQLException {
        String sql = "SELECT id, nama, deskripsi FROM materi_tabuh WHERE id = ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new MateriTabuh(
                            rs.getInt("id"),
                            rs.getString("nama"),
                            rs.getString("deskripsi")
                    );
                }
            }
        }
        return null;
    }

    @Override
    public boolean save(MateriTabuh materi) throws SQLException {
        String sql = "INSERT INTO materi_tabuh (nama, deskripsi) VALUES (?, ?)";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, materi.getNama());
            stmt.setString(2, materi.getDeskripsi());
            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        materi.setId(keys.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean update(MateriTabuh materi) throws SQLException {
        String sql = "UPDATE materi_tabuh SET nama = ?, deskripsi = ? WHERE id = ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, materi.getNama());
            stmt.setString(2, materi.getDeskripsi());
            stmt.setInt(3, materi.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM materi_tabuh WHERE id = ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean existsByNama(String nama, Integer excludeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM materi_tabuh WHERE LOWER(nama) = LOWER(?)" +
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

    @Override
    public int countDigunakanDiSesi(int materiId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM sesi_materi WHERE materi_tabuh_id = ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, materiId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
}
