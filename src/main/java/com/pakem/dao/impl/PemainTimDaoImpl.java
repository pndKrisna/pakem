package com.pakem.dao.impl;

import com.pakem.config.DatabaseConnection;
import com.pakem.dao.interfaces.IPemainTimDao;
import com.pakem.model.PemainTim;
import com.pakem.model.enums.JenisAcara;
import com.pakem.model.enums.StatusHonor;
import com.pakem.model.enums.StatusKesediaan;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementasi JDBC untuk IPemainTimDao.
 * Semua kolom cocok 100% dengan schema.sql tabel pemain_tim.
 */
public class PemainTimDaoImpl implements IPemainTimDao {

    private static final String BASE_SELECT =
            "SELECT p.id, p.tim_event_id, p.anggota_id, p.status_kesediaan, p.nominal_honor, p.status_honor, " +
            "a.nama_lengkap AS nama_anggota, a.nomor_kontak, " +
            "k.nama AS nama_kategori, s.nama AS nama_subkelompok, " +
            "t.nama_tim, e.nama_acara, e.jenis_acara " +
            "FROM pemain_tim p " +
            "JOIN anggota a ON p.anggota_id = a.id " +
            "JOIN tim_event t ON p.tim_event_id = t.id " +
            "JOIN event e ON t.event_id = e.id " +
            "LEFT JOIN kategori_tingkat k ON a.kategori_tingkat_id = k.id " +
            "LEFT JOIN subkelompok s ON a.subkelompok_id = s.id ";

    @Override
    public List<PemainTim> findByTimId(int timEventId) throws SQLException {
        List<PemainTim> list = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE p.tim_event_id = ? ORDER BY a.nama_lengkap ASC";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, timEventId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToPemain(rs));
                }
            }
        }
        return list;
    }

    @Override
    public List<PemainTim> findByEventId(int eventId) throws SQLException {
        List<PemainTim> list = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE t.event_id = ? ORDER BY t.id ASC, a.nama_lengkap ASC";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToPemain(rs));
                }
            }
        }
        return list;
    }

    @Override
    public PemainTim findById(int id) throws SQLException {
        String sql = BASE_SELECT + "WHERE p.id = ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPemain(rs);
                }
            }
        }
        return null;
    }

    @Override
    public PemainTim findByTimAndAnggota(int timEventId, int anggotaId) throws SQLException {
        String sql = BASE_SELECT + "WHERE p.tim_event_id = ? AND p.anggota_id = ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, timEventId);
            stmt.setInt(2, anggotaId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPemain(rs);
                }
            }
        }
        return null;
    }

    @Override
    public boolean save(PemainTim pemain) throws SQLException {
        String sql = "INSERT INTO pemain_tim (tim_event_id, anggota_id, status_kesediaan, nominal_honor, status_honor) " +
                     "VALUES (?, ?, ?, ?, ?)";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, pemain.getTimEventId());
            stmt.setInt(2, pemain.getAnggotaId());
            stmt.setString(3, pemain.getStatusKesediaan().name());

            if (pemain.getNominalHonor() != null) {
                stmt.setBigDecimal(4, pemain.getNominalHonor());
            } else {
                stmt.setNull(4, Types.DECIMAL);
            }

            if (pemain.getStatusHonor() != null) {
                stmt.setString(5, pemain.getStatusHonor().name());
            } else {
                stmt.setNull(5, Types.VARCHAR);
            }

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        pemain.setId(keys.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean update(PemainTim pemain) throws SQLException {
        String sql = "UPDATE pemain_tim SET status_kesediaan = ?, nominal_honor = ?, status_honor = ? " +
                     "WHERE id = ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, pemain.getStatusKesediaan().name());
            if (pemain.getNominalHonor() != null) {
                stmt.setBigDecimal(2, pemain.getNominalHonor());
            } else {
                stmt.setNull(2, Types.DECIMAL);
            }
            if (pemain.getStatusHonor() != null) {
                stmt.setString(3, pemain.getStatusHonor().name());
            } else {
                stmt.setNull(3, Types.VARCHAR);
            }
            stmt.setInt(4, pemain.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateKesediaan(int id, StatusKesediaan statusKesediaan) throws SQLException {
        String sql = "UPDATE pemain_tim SET status_kesediaan = ? WHERE id = ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, statusKesediaan.name());
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateHonor(int id, BigDecimal nominalHonor, StatusHonor statusHonor) throws SQLException {
        String sql = "UPDATE pemain_tim SET nominal_honor = ?, status_honor = ? WHERE id = ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (nominalHonor != null) {
                stmt.setBigDecimal(1, nominalHonor);
            } else {
                stmt.setNull(1, Types.DECIMAL);
            }
            if (statusHonor != null) {
                stmt.setString(2, statusHonor.name());
            } else {
                stmt.setNull(2, Types.VARCHAR);
            }
            stmt.setInt(3, id);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM pemain_tim WHERE id = ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteByTimId(int timEventId) throws SQLException {
        String sql = "DELETE FROM pemain_tim WHERE tim_event_id = ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, timEventId);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean existsByTimAndAnggota(int timEventId, int anggotaId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM pemain_tim WHERE tim_event_id = ? AND anggota_id = ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, timEventId);
            stmt.setInt(2, anggotaId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    @Override
    public int countByKesediaan(int timEventId, StatusKesediaan statusKesediaan) throws SQLException {
        String sql = "SELECT COUNT(*) FROM pemain_tim WHERE tim_event_id = ? AND status_kesediaan = ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, timEventId);
            stmt.setString(2, statusKesediaan.name());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    private PemainTim mapResultSetToPemain(ResultSet rs) throws SQLException {
        PemainTim p = new PemainTim();
        p.setId(rs.getInt("id"));
        p.setTimEventId(rs.getInt("tim_event_id"));
        p.setAnggotaId(rs.getInt("anggota_id"));
        p.setStatusKesediaan(StatusKesediaan.valueOf(rs.getString("status_kesediaan").toUpperCase()));
        p.setNominalHonor(rs.getBigDecimal("nominal_honor"));

        String sh = rs.getString("status_honor");
        p.setStatusHonor(sh != null ? StatusHonor.valueOf(sh.toUpperCase()) : null);

        p.setNamaAnggota(rs.getString("nama_anggota"));
        p.setNomorKontak(rs.getString("nomor_kontak"));
        p.setNamaKategori(rs.getString("nama_kategori"));
        p.setNamaSubkelompok(rs.getString("nama_subkelompok"));
        p.setNamaTim(rs.getString("nama_tim"));
        p.setNamaAcara(rs.getString("nama_acara"));
        p.setJenisAcara(JenisAcara.valueOf(rs.getString("jenis_acara").toUpperCase()));

        return p;
    }
}
