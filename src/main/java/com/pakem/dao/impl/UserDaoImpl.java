package com.pakem.dao.impl;

import com.pakem.config.DatabaseConnection;
import com.pakem.dao.interfaces.IUserDao;
import com.pakem.model.User;
import com.pakem.model.enums.RoleUser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementasi JDBC untuk IUserDao.
 */
public class UserDaoImpl implements IUserDao {

    @Override
    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT id, username, password_hash, role, is_active FROM users WHERE username = ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }
        return null;
    }

    @Override
    public User findById(int id) throws SQLException {
        String sql = "SELECT id, username, password_hash, role, is_active FROM users WHERE id = ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<User> findAll() throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT id, username, password_hash, role, is_active FROM users ORDER BY username ASC";
        Connection conn = DatabaseConnection.getConnection();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSetToUser(rs));
            }
        }
        return list;
    }

    @Override
    public boolean save(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password_hash, role, is_active) VALUES (?, ?, ?, ?)";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, user.getRole().name());
            stmt.setInt(4, user.isActive() ? 1 : 0);
            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        user.setId(keys.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean updatePassword(int id, String newPasswordHash) throws SQLException {
        String sql = "UPDATE users SET password_hash = ? WHERE id = ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newPasswordHash);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateStatus(int id, boolean isActive) throws SQLException {
        String sql = "UPDATE users SET is_active = ? WHERE id = ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, isActive ? 1 : 0);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String username = rs.getString("username");
        String passwordHash = rs.getString("password_hash");
        String roleStr = rs.getString("role");
        boolean isActive = rs.getInt("is_active") == 1;

        RoleUser role = RoleUser.fromString(roleStr);
        return User.create(id, username, passwordHash, role, isActive);
    }
}
