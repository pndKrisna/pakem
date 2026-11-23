package com.pakem.dao.impl;

import com.pakem.config.DatabaseConnection;
import com.pakem.dao.interfaces.IJadwalLatihanDao;
import com.pakem.model.JadwalLatihan;
import com.pakem.model.enums.JenisJadwal;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementasi JDBC untuk IJadwalLatihanDao.
 */
public class JadwalLatihanDaoImpl implements IJadwalLatihanDao {

    @Override
    public List<JadwalLatihan> findAll() throws SQLException {
        List<JadwalLatihan> list = new ArrayList<>();
        String sql = "SELECT j.id, j.jenis_jadwal, j.tanggal, j.jam_mulai, j.jam_selesai, " +
                     "j.lokasi, j.kategori_tingkat_id, j.subkelompok_id, j.event_id, j.catatan, " +
                     "k.nama AS nama_kategori, s.nama AS nama_subkelompok, e.nama_acara AS nama_event " +
                     "FROM jadwal_latihan j " +
                     "LEFT JOIN kategori_tingkat k ON j.kategori_tingkat_id = k.id " +
                     "LEFT JOIN subkelompok s ON j.subkelompok_id = s.id " +
                     "LEFT JOIN event e ON j.event_id = e.id " +
                     "ORDER BY j.tanggal ASC, j.jam_mulai ASC";
        Connection conn = DatabaseConnection.getConnection();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSetToJadwal(rs));
            }
        }
        return list;
    }

    @Override
    public List<JadwalLatihan> findByKategori(int kategoriId) throws SQLException {
        List<JadwalLatihan> list = new ArrayList<>();
        String sql = "SELECT j.id, j.jenis_jadwal, j.tanggal, j.jam_mulai, j.jam_selesai, " +
                     "j.lokasi, j.kategori_tingkat_id, j.subkelompok_id, j.event_id, j.catatan, " +
                     "k.nama AS nama_kategori, s.nama AS nama_subkelompok, e.nama_acara AS nama_event " +
                     "FROM jadwal_latihan j " +
                     "LEFT JOIN kategori_tingkat k ON j.kategori_tingkat_id = k.id " +
                     "LEFT JOIN subkelompok s ON j.subkelompok_id = s.id " +
                     "LEFT JOIN event e ON j.event_id = e.id " +
                     "WHERE j.kategori_tingkat_id = ? " +
                     "ORDER BY j.tanggal ASC, j.jam_mulai ASC";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, kategoriId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToJadwal(rs));
                }
            }
        }
        return list;
    }

    @Override
    public List<JadwalLatihan> findBySubkelompok(int subkelompokId) throws SQLException {
        List<JadwalLatihan> list = new ArrayList<>();
        String sql = "SELECT j.id, j.jenis_jadwal, j.tanggal, j.jam_mulai, j.jam_selesai, " +
                     "j.lokasi, j.kategori_tingkat_id, j.subkelompok_id, j.event_id, j.catatan, " +
                     "k.nama AS nama_kategori, s.nama AS nama_subkelompok, e.nama_acara AS nama_event " +
                     "FROM jadwal_latihan j " +
                     "LEFT JOIN kategori_tingkat k ON j.kategori_tingkat_id = k.id " +
                     "LEFT JOIN subkelompok s ON j.subkelompok_id = s.id " +
                     "LEFT JOIN event e ON j.event_id = e.id " +
                     "WHERE j.subkelompok_id = ? " +
                     "ORDER BY j.tanggal ASC, j.jam_mulai ASC";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, subkelompokId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToJadwal(rs));
                }
            }
        }
        return list;
    }

    @Override
    public List<JadwalLatihan> findByTanggal(LocalDate tanggal) throws SQLException {
        List<JadwalLatihan> list = new ArrayList<>();
        String sql = "SELECT j.id, j.jenis_jadwal, j.tanggal, j.jam_mulai, j.jam_selesai, " +
                     "j.lokasi, j.kategori_tingkat_id, j.subkelompok_id, j.event_id, j.catatan, " +
                     "k.nama AS nama_kategori, s.nama AS nama_subkelompok, e.nama_acara AS nama_event " +
                     "FROM jadwal_latihan j " +
                     "LEFT JOIN kategori_tingkat k ON j.kategori_tingkat_id = k.id " +
                     "LEFT JOIN subkelompok s ON j.subkelompok_id = s.id " +
                     "LEFT JOIN event e ON j.event_id = e.id " +
                     "WHERE j.tanggal = ? " +
                     "ORDER BY j.jam_mulai ASC";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(tanggal));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToJadwal(rs));
                }
            }
        }
        return list;
    }

    @Override
    public JadwalLatihan findById(int id) throws SQLException {
        String sql = "SELECT j.id, j.jenis_jadwal, j.tanggal, j.jam_mulai, j.jam_selesai, " +
                     "j.lokasi, j.kategori_tingkat_id, j.subkelompok_id, j.event_id, j.catatan, " +
                     "k.nama AS nama_kategori, s.nama AS nama_subkelompok, e.nama_acara AS nama_event " +
                     "FROM jadwal_latihan j " +
                     "LEFT JOIN kategori_tingkat k ON j.kategori_tingkat_id = k.id " +
                     "LEFT JOIN subkelompok s ON j.subkelompok_id = s.id " +
                     "LEFT JOIN event e ON j.event_id = e.id " +
                     "WHERE j.id = ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToJadwal(rs);
                }
            }
        }
        return null;
    }

    @Override
    public boolean save(JadwalLatihan jadwal) throws SQLException {
        String sql = "INSERT INTO jadwal_latihan (jenis_jadwal, tanggal, jam_mulai, jam_selesai, " +
                     "lokasi, kategori_tingkat_id, subkelompok_id, event_id, catatan) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, jadwal.getJenisJadwal().name());
            stmt.setDate(2, Date.valueOf(jadwal.getTanggal()));
            if (jadwal.getJamMulai() != null) {
                stmt.setTime(3, java.sql.Time.valueOf(jadwal.getJamMulai()));
            } else {
                stmt.setNull(3, java.sql.Types.TIME);
            }
            if (jadwal.getJamSelesai() != null) {
                stmt.setTime(4, java.sql.Time.valueOf(jadwal.getJamSelesai()));
            } else {
                stmt.setNull(4, java.sql.Types.TIME);
            }
            stmt.setString(5, jadwal.getLokasi());

            if (jadwal.getKategoriTingkatId() != null) {
                stmt.setInt(6, jadwal.getKategoriTingkatId());
            } else {
                stmt.setNull(6, java.sql.Types.INTEGER);
            }
            if (jadwal.getSubkelompokId() != null) {
                stmt.setInt(7, jadwal.getSubkelompokId());
            } else {
                stmt.setNull(7, java.sql.Types.INTEGER);
            }
            if (jadwal.getEventId() != null) {
                stmt.setInt(8, jadwal.getEventId());
            } else {
                stmt.setNull(8, java.sql.Types.INTEGER);
            }
            stmt.setString(9, jadwal.getCatatan());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        jadwal.setId(keys.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean update(JadwalLatihan jadwal) throws SQLException {
        String sql = "UPDATE jadwal_latihan SET jenis_jadwal = ?, tanggal = ?, jam_mulai = ?, " +
                     "jam_selesai = ?, lokasi = ?, kategori_tingkat_id = ?, subkelompok_id = ?, " +
                     "event_id = ?, catatan = ? WHERE id = ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, jadwal.getJenisJadwal().name());
            stmt.setDate(2, Date.valueOf(jadwal.getTanggal()));
            if (jadwal.getJamMulai() != null) {
                stmt.setTime(3, java.sql.Time.valueOf(jadwal.getJamMulai()));
            } else {
                stmt.setNull(3, java.sql.Types.TIME);
            }
            if (jadwal.getJamSelesai() != null) {
                stmt.setTime(4, java.sql.Time.valueOf(jadwal.getJamSelesai()));
            } else {
                stmt.setNull(4, java.sql.Types.TIME);
            }
            stmt.setString(5, jadwal.getLokasi());
            if (jadwal.getKategoriTingkatId() != null) {
                stmt.setInt(6, jadwal.getKategoriTingkatId());
            } else {
                stmt.setNull(6, java.sql.Types.INTEGER);
            }
            if (jadwal.getSubkelompokId() != null) {
                stmt.setInt(7, jadwal.getSubkelompokId());
            } else {
                stmt.setNull(7, java.sql.Types.INTEGER);
            }
            if (jadwal.getEventId() != null) {
                stmt.setInt(8, jadwal.getEventId());
            } else {
                stmt.setNull(8, java.sql.Types.INTEGER);
            }
            stmt.setString(9, jadwal.getCatatan());
            stmt.setInt(10, jadwal.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM jadwal_latihan WHERE id = ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public int countSesiMateri(int jadwalId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM sesi_materi WHERE jadwal_latihan_id = ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, jadwalId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    private JadwalLatihan mapResultSetToJadwal(ResultSet rs) throws SQLException {
        JadwalLatihan j = new JadwalLatihan();
        j.setId(rs.getInt("id"));
        j.setJenisJadwal(JenisJadwal.valueOf(rs.getString("jenis_jadwal").toUpperCase()));

        Date tgl = rs.getDate("tanggal");
        if (tgl != null) {
            j.setTanggal(tgl.toLocalDate());
        }

        java.sql.Time jm = rs.getTime("jam_mulai");
        if (jm != null) {
            j.setJamMulai(jm.toLocalTime());
        }

        java.sql.Time js = rs.getTime("jam_selesai");
        if (js != null) {
            j.setJamSelesai(js.toLocalTime());
        }

        j.setLokasi(rs.getString("lokasi"));

        int katId = rs.getInt("kategori_tingkat_id");
        j.setKategoriTingkatId(rs.wasNull() ? null : katId);

        int subId = rs.getInt("subkelompok_id");
        j.setSubkelompokId(rs.wasNull() ? null : subId);

        int evId = rs.getInt("event_id");
        j.setEventId(rs.wasNull() ? null : evId);

        j.setCatatan(rs.getString("catatan"));

        j.setNamaKategori(rs.getString("nama_kategori"));
        j.setNamaSubkelompok(rs.getString("nama_subkelompok"));
        j.setNamaEvent(rs.getString("nama_event"));

        return j;
    }
}
