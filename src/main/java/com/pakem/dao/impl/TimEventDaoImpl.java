package com.pakem.dao.impl;

import com.pakem.config.DatabaseConnection;
import com.pakem.dao.interfaces.ITimEventDao;
import com.pakem.model.TimEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementasi JDBC untuk ITimEventDao.
 * Semua kolom cocok 100% dengan schema.sql tabel tim_event.
 */
public class TimEventDaoImpl implements ITimEventDao {

    @Override
    public List<TimEvent> findByEventId(int eventId) throws SQLException {
        List<TimEvent> list = new ArrayList<>();
        String sql = "SELECT t.id, t.event_id, t.nama_tim, t.catatan, " +
                     "e.nama_acara AS nama_event, " +
                     "COUNT(p.id) AS jumlah_pemain " +
                     "FROM tim_event t " +
                     "JOIN event e ON t.event_id = e.id " +
                     "LEFT JOIN pemain_tim p ON p.tim_event_id = t.id " +
                     "WHERE t.event_id = ? " +
                     "GROUP BY t.id, t.event_id, t.nama_tim, t.catatan, e.nama_acara " +
                     "ORDER BY t.id ASC";

        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToTim(rs));
                }
            }
        }
        return list;
    }

    @Override
    public TimEvent findById(int id) throws SQLException {
        String sql = "SELECT t.id, t.event_id, t.nama_tim, t.catatan, " +
                     "e.nama_acara AS nama_event, " +
                     "COUNT(p.id) AS jumlah_pemain " +
                     "FROM tim_event t " +
                     "JOIN event e ON t.event_id = e.id " +
                     "LEFT JOIN pemain_tim p ON p.tim_event_id = t.id " +
                     "WHERE t.id = ? " +
                     "GROUP BY t.id, t.event_id, t.nama_tim, t.catatan, e.nama_acara";

        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTim(rs);
                }
            }
        }
        return null;
    }

    @Override
    public boolean save(TimEvent tim) throws SQLException {
        String sql = "INSERT INTO tim_event (event_id, nama_tim, catatan) VALUES (?, ?, ?)";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, tim.getEventId());
            stmt.setString(2, tim.getNamaTim());
            stmt.setString(3, tim.getCatatan());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        tim.setId(keys.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean update(TimEvent tim) throws SQLException {
        String sql = "UPDATE tim_event SET nama_tim = ?, catatan = ? WHERE id = ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tim.getNamaTim());
            stmt.setString(2, tim.getCatatan());
            stmt.setInt(3, tim.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM tim_event WHERE id = ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public int countPemain(int timId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM pemain_tim WHERE tim_event_id = ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, timId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    @Override
    public boolean existsByNamaInEvent(String namaTim, int eventId, Integer excludeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM tim_event WHERE event_id = ? AND LOWER(nama_tim) = LOWER(?)" +
                     (excludeId != null ? " AND id <> ?" : "");
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            stmt.setString(2, namaTim.trim());
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

    private TimEvent mapResultSetToTim(ResultSet rs) throws SQLException {
        TimEvent t = new TimEvent();
        t.setId(rs.getInt("id"));
        t.setEventId(rs.getInt("event_id"));
        t.setNamaTim(rs.getString("nama_tim"));
        t.setCatatan(rs.getString("catatan"));
        t.setNamaEvent(rs.getString("nama_event"));
        t.setJumlahPemain(rs.getInt("jumlah_pemain"));
        return t;
    }
}
