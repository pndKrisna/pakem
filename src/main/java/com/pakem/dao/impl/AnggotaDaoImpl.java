package com.pakem.dao.impl;

import com.pakem.config.DatabaseConnection;
import com.pakem.dao.interfaces.IAnggotaDao;
import com.pakem.model.Anggota;
import com.pakem.model.enums.StatusAnggota;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementasi JDBC untuk IAnggotaDao.
 */
public class AnggotaDaoImpl implements IAnggotaDao {

    @Override
    public List<Anggota> findAll(StatusAnggota filterStatus, String searchKeyword) throws SQLException {
        List<Anggota> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT a.id, a.nama_lengkap, a.nomor_kontak, a.tanggal_bergabung, a.status, " +
                "a.kategori_tingkat_id, a.subkelompok_id, a.catatan, a.created_at, a.updated_at, " +
                "k.nama AS nama_kategori, s.nama AS nama_subkelompok " +
                "FROM anggota a " +
                "LEFT JOIN kategori_tingkat k ON a.kategori_tingkat_id = k.id " +
                "LEFT JOIN subkelompok s ON a.subkelompok_id = s.id " +
                "WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();

        if (filterStatus != null) {
            sql.append("AND a.status = ? ");
            params.add(filterStatus.name());
        }

        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            sql.append("AND (a.nama_lengkap LIKE ? OR a.nomor_kontak LIKE ? OR a.catatan LIKE ?) ");
            String wildcard = "%" + searchKeyword.trim() + "%";
            params.add(wildcard);
            params.add(wildcard);
            params.add(wildcard);
        }

        sql.append("ORDER BY a.nama_lengkap ASC");

        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToAnggota(rs));
                }
            }
        }
        return list;
    }

    @Override
    public Anggota findById(int id) throws SQLException {
        String sql = "SELECT a.id, a.nama_lengkap, a.nomor_kontak, a.tanggal_bergabung, a.status, " +
                     "a.kategori_tingkat_id, a.subkelompok_id, a.catatan, a.created_at, a.updated_at, " +
                     "k.nama AS nama_kategori, s.nama AS nama_subkelompok " +
                     "FROM anggota a " +
                     "LEFT JOIN kategori_tingkat k ON a.kategori_tingkat_id = k.id " +
                     "LEFT JOIN subkelompok s ON a.subkelompok_id = s.id " +
                     "WHERE a.id = ?";

        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAnggota(rs);
                }
            }
        }
        return null;
    }

    @Override
    public boolean save(Anggota anggota) throws SQLException {
        String sql = "INSERT INTO anggota (nama_lengkap, nomor_kontak, tanggal_bergabung, status, " +
                     "kategori_tingkat_id, subkelompok_id, catatan) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, anggota.getNamaLengkap());
            stmt.setString(2, anggota.getNomorKontak());
            stmt.setDate(3, Date.valueOf(anggota.getTanggalBergabung()));
            stmt.setString(4, anggota.getStatus().name());

            if (anggota.getKategoriTingkatId() != null) {
                stmt.setInt(5, anggota.getKategoriTingkatId());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }

            if (anggota.getSubkelompokId() != null) {
                stmt.setInt(6, anggota.getSubkelompokId());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }

            stmt.setString(7, anggota.getCatatan());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        anggota.setId(keys.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean update(Anggota anggota) throws SQLException {
        String sql = "UPDATE anggota SET nama_lengkap = ?, nomor_kontak = ?, tanggal_bergabung = ?, " +
                     "status = ?, kategori_tingkat_id = ?, subkelompok_id = ?, catatan = ? " +
                     "WHERE id = ?";

        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, anggota.getNamaLengkap());
            stmt.setString(2, anggota.getNomorKontak());
            stmt.setDate(3, Date.valueOf(anggota.getTanggalBergabung()));
            stmt.setString(4, anggota.getStatus().name());

            if (anggota.getKategoriTingkatId() != null) {
                stmt.setInt(5, anggota.getKategoriTingkatId());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }

            if (anggota.getSubkelompokId() != null) {
                stmt.setInt(6, anggota.getSubkelompokId());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }

            stmt.setString(7, anggota.getCatatan());
            stmt.setInt(8, anggota.getId());

            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateStatus(int id, StatusAnggota newStatus) throws SQLException {
        String sql = "UPDATE anggota SET status = ? WHERE id = ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newStatus.name());
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public int countByStatus(StatusAnggota status) throws SQLException {
        String sql = "SELECT COUNT(*) FROM anggota WHERE status = ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    @Override
    public int countTotal() throws SQLException {
        String sql = "SELECT COUNT(*) FROM anggota";
        Connection conn = DatabaseConnection.getConnection();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    private Anggota mapResultSetToAnggota(ResultSet rs) throws SQLException {
        Anggota a = new Anggota();
        a.setId(rs.getInt("id"));
        a.setNamaLengkap(rs.getString("nama_lengkap"));
        a.setNomorKontak(rs.getString("nomor_kontak"));

        Date tgl = rs.getDate("tanggal_bergabung");
        if (tgl != null) {
            a.setTanggalBergabung(tgl.toLocalDate());
        }

        a.setStatus(StatusAnggota.fromString(rs.getString("status")));

        int katId = rs.getInt("kategori_tingkat_id");
        a.setKategoriTingkatId(rs.wasNull() ? null : katId);

        int subId = rs.getInt("subkelompok_id");
        a.setSubkelompokId(rs.wasNull() ? null : subId);

        a.setCatatan(rs.getString("catatan"));

        Timestamp cAt = rs.getTimestamp("created_at");
        if (cAt != null) a.setCreatedAt(cAt.toLocalDateTime());

        Timestamp uAt = rs.getTimestamp("updated_at");
        if (uAt != null) a.setUpdatedAt(uAt.toLocalDateTime());

        a.setNamaKategori(rs.getString("nama_kategori"));
        a.setNamaSubkelompok(rs.getString("nama_subkelompok"));

        return a;
    }
}
