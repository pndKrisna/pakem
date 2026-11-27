package com.pakem.dao.impl;

import com.pakem.config.DatabaseConnection;
import com.pakem.dao.interfaces.IEventDao;
import com.pakem.model.Event;
import com.pakem.model.enums.JenisAcara;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementasi JDBC untuk IEventDao.
 * Semua kolom cocok 100% dengan schema.sql tabel event.
 */
public class EventDaoImpl implements IEventDao {

    @Override
    public List<Event> findAll(JenisAcara filterJenis, LocalDate dariTanggal, LocalDate sampaiTanggal, String searchKeyword) throws SQLException {
        List<Event> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT e.id, e.nama_acara, e.jenis_acara, e.tanggal, e.waktu, e.lokasi, e.catatan, " +
                "COUNT(DISTINCT t.id) AS jumlah_tim, " +
                "COUNT(DISTINCT p.id) AS total_pemain " +
                "FROM event e " +
                "LEFT JOIN tim_event t ON t.event_id = e.id " +
                "LEFT JOIN pemain_tim p ON p.tim_event_id = t.id " +
                "WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();

        if (filterJenis != null) {
            sql.append("AND e.jenis_acara = ? ");
            params.add(filterJenis.name());
        }

        if (dariTanggal != null) {
            sql.append("AND e.tanggal >= ? ");
            params.add(Date.valueOf(dariTanggal));
        }

        if (sampaiTanggal != null) {
            sql.append("AND e.tanggal <= ? ");
            params.add(Date.valueOf(sampaiTanggal));
        }

        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            sql.append("AND (e.nama_acara LIKE ? OR e.lokasi LIKE ? OR e.catatan LIKE ?) ");
            String kw = "%" + searchKeyword.trim() + "%";
            params.add(kw);
            params.add(kw);
            params.add(kw);
        }

        sql.append("GROUP BY e.id, e.nama_acara, e.jenis_acara, e.tanggal, e.waktu, e.lokasi, e.catatan ");
        sql.append("ORDER BY e.tanggal DESC, e.waktu ASC");

        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToEvent(rs));
                }
            }
        }
        return list;
    }

    @Override
    public Event findById(int id) throws SQLException {
        String sql = "SELECT e.id, e.nama_acara, e.jenis_acara, e.tanggal, e.waktu, e.lokasi, e.catatan, " +
                     "COUNT(DISTINCT t.id) AS jumlah_tim, " +
                     "COUNT(DISTINCT p.id) AS total_pemain " +
                     "FROM event e " +
                     "LEFT JOIN tim_event t ON t.event_id = e.id " +
                     "LEFT JOIN pemain_tim p ON p.tim_event_id = t.id " +
                     "WHERE e.id = ? " +
                     "GROUP BY e.id, e.nama_acara, e.jenis_acara, e.tanggal, e.waktu, e.lokasi, e.catatan";

        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEvent(rs);
                }
            }
        }
        return null;
    }

    @Override
    public boolean save(Event event) throws SQLException {
        String sql = "INSERT INTO event (nama_acara, jenis_acara, tanggal, waktu, lokasi, catatan) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, event.getNamaAcara());
            stmt.setString(2, event.getJenisAcara().name());
            stmt.setDate(3, Date.valueOf(event.getTanggal()));
            if (event.getWaktu() != null) {
                stmt.setTime(4, Time.valueOf(event.getWaktu()));
            } else {
                stmt.setNull(4, java.sql.Types.TIME);
            }
            stmt.setString(5, event.getLokasi());
            stmt.setString(6, event.getCatatan());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        event.setId(keys.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean update(Event event) throws SQLException {
        String sql = "UPDATE event SET nama_acara = ?, jenis_acara = ?, tanggal = ?, waktu = ?, " +
                     "lokasi = ?, catatan = ? WHERE id = ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, event.getNamaAcara());
            stmt.setString(2, event.getJenisAcara().name());
            stmt.setDate(3, Date.valueOf(event.getTanggal()));
            if (event.getWaktu() != null) {
                stmt.setTime(4, Time.valueOf(event.getWaktu()));
            } else {
                stmt.setNull(4, java.sql.Types.TIME);
            }
            stmt.setString(5, event.getLokasi());
            stmt.setString(6, event.getCatatan());
            stmt.setInt(7, event.getId());

            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM event WHERE id = ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public int countTim(int eventId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM tim_event WHERE event_id = ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    @Override
    public int countPemain(int eventId) throws SQLException {
        String sql = "SELECT COUNT(p.id) FROM pemain_tim p " +
                     "JOIN tim_event t ON p.tim_event_id = t.id " +
                     "WHERE t.event_id = ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    private Event mapResultSetToEvent(ResultSet rs) throws SQLException {
        Event e = new Event();
        e.setId(rs.getInt("id"));
        e.setNamaAcara(rs.getString("nama_acara"));
        e.setJenisAcara(JenisAcara.valueOf(rs.getString("jenis_acara").toUpperCase()));

        Date tgl = rs.getDate("tanggal");
        if (tgl != null) {
            e.setTanggal(tgl.toLocalDate());
        }

        Time wkt = rs.getTime("waktu");
        if (wkt != null) {
            e.setWaktu(wkt.toLocalTime());
        }

        e.setLokasi(rs.getString("lokasi"));
        e.setCatatan(rs.getString("catatan"));

        e.setJumlahTim(rs.getInt("jumlah_tim"));
        e.setTotalPemain(rs.getInt("total_pemain"));

        return e;
    }
}
