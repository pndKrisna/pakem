package com.pakem.dao.impl;

import com.pakem.config.DatabaseConnection;
import com.pakem.dao.interfaces.ISesiMateriDao;
import com.pakem.model.SesiMateri;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementasi JDBC untuk ISesiMateriDao.
 */
public class SesiMateriDaoImpl implements ISesiMateriDao {

    @Override
    public List<SesiMateri> findByJadwalId(int jadwalId) throws SQLException {
        List<SesiMateri> list = new ArrayList<>();
        String sql = "SELECT s.id, s.jadwal_latihan_id, s.materi_tabuh_id, s.catatan_progres, " +
                     "m.nama AS nama_materi, j.tanggal AS tanggal_jadwal " +
                     "FROM sesi_materi s " +
                     "JOIN materi_tabuh m ON s.materi_tabuh_id = m.id " +
                     "JOIN jadwal_latihan j ON s.jadwal_latihan_id = j.id " +
                     "WHERE s.jadwal_latihan_id = ? " +
                     "ORDER BY s.id ASC";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, jadwalId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToSesi(rs));
                }
            }
        }
        return list;
    }

    @Override
    public List<SesiMateri> findByMateriId(int materiId) throws SQLException {
        List<SesiMateri> list = new ArrayList<>();
        String sql = "SELECT s.id, s.jadwal_latihan_id, s.materi_tabuh_id, s.catatan_progres, " +
                     "m.nama AS nama_materi, j.tanggal AS tanggal_jadwal " +
                     "FROM sesi_materi s " +
                     "JOIN materi_tabuh m ON s.materi_tabuh_id = m.id " +
                     "JOIN jadwal_latihan j ON s.jadwal_latihan_id = j.id " +
                     "WHERE s.materi_tabuh_id = ? " +
                     "ORDER BY j.tanggal DESC, s.id DESC";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, materiId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToSesi(rs));
                }
            }
        }
        return list;
    }

    @Override
    public SesiMateri findById(int id) throws SQLException {
        String sql = "SELECT s.id, s.jadwal_latihan_id, s.materi_tabuh_id, s.catatan_progres, " +
                     "m.nama AS nama_materi, j.tanggal AS tanggal_jadwal " +
                     "FROM sesi_materi s " +
                     "JOIN materi_tabuh m ON s.materi_tabuh_id = m.id " +
                     "JOIN jadwal_latihan j ON s.jadwal_latihan_id = j.id " +
                     "WHERE s.id = ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSesi(rs);
                }
            }
        }
        return null;
    }

    @Override
    public boolean save(SesiMateri sesi) throws SQLException {
        String sql = "INSERT INTO sesi_materi (jadwal_latihan_id, materi_tabuh_id, catatan_progres) " +
                     "VALUES (?, ?, ?)";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, sesi.getJadwalLatihanId());
            stmt.setInt(2, sesi.getMateriTabuhId());
            stmt.setString(3, sesi.getCatatanProgres());
            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        sesi.setId(keys.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean update(SesiMateri sesi) throws SQLException {
        String sql = "UPDATE sesi_materi SET materi_tabuh_id = ?, catatan_progres = ? WHERE id = ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sesi.getMateriTabuhId());
            stmt.setString(2, sesi.getCatatanProgres());
            stmt.setInt(3, sesi.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM sesi_materi WHERE id = ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteByJadwalId(int jadwalId) throws SQLException {
        String sql = "DELETE FROM sesi_materi WHERE jadwal_latihan_id = ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, jadwalId);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public SesiMateri findProgresTerakhirByMateri(int materiId) throws SQLException {
        String sql = "SELECT s.id, s.jadwal_latihan_id, s.materi_tabuh_id, s.catatan_progres, " +
                     "m.nama AS nama_materi, j.tanggal AS tanggal_jadwal " +
                     "FROM sesi_materi s " +
                     "JOIN materi_tabuh m ON s.materi_tabuh_id = m.id " +
                     "JOIN jadwal_latihan j ON s.jadwal_latihan_id = j.id " +
                     "WHERE s.materi_tabuh_id = ? " +
                     "ORDER BY j.tanggal DESC, j.jam_mulai DESC, s.id DESC " +
                     "LIMIT 1";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, materiId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSesi(rs);
                }
            }
        }
        return null;
    }

    private SesiMateri mapResultSetToSesi(ResultSet rs) throws SQLException {
        SesiMateri s = new SesiMateri();
        s.setId(rs.getInt("id"));
        s.setJadwalLatihanId(rs.getInt("jadwal_latihan_id"));
        s.setMateriTabuhId(rs.getInt("materi_tabuh_id"));
        s.setCatatanProgres(rs.getString("catatan_progres"));

        s.setNamaMateri(rs.getString("nama_materi"));

        java.sql.Date tgl = rs.getDate("tanggal_jadwal");
        s.setTanggalJadwal(tgl != null ? tgl.toLocalDate().toString() : "-");

        return s;
    }
}
