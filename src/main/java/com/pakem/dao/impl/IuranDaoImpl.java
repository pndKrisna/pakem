package com.pakem.dao.impl;

import com.pakem.config.DatabaseConnection;
import com.pakem.dao.interfaces.IIuranDao;
import com.pakem.model.Anggota;
import com.pakem.model.Iuran;
import com.pakem.model.enums.StatusBayar;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementasi JDBC untuk IIuranDao.
 * Semua nama tabel dan kolom 100% cocok dengan DDL tabel `iuran` dan `anggota` di schema.sql.
 */
public class IuranDaoImpl implements IIuranDao {

    @Override
    public List<Iuran> findByPeriode(int bulan, int tahun) throws SQLException {
        List<Iuran> list = new ArrayList<>();
        String sql = """
            SELECT i.id, i.anggota_id, i.bulan, i.tahun, i.nominal, i.tanggal_bayar, i.status_bayar,
                   a.nama_lengkap, a.nomor_kontak, a.status AS status_anggota,
                   k.nama AS nama_kategori, s.nama AS nama_subkelompok
            FROM iuran i
            JOIN anggota a ON i.anggota_id = a.id
            LEFT JOIN kategori_tingkat k ON a.kategori_tingkat_id = k.id
            LEFT JOIN subkelompok s ON a.subkelompok_id = s.id
            WHERE i.bulan = ? AND i.tahun = ?
              AND a.status = 'AKTIF'
            ORDER BY a.nama_lengkap ASC
        """;

        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bulan);
            stmt.setInt(2, tahun);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToIuran(rs));
                }
            }
        }
        return list;
    }

    @Override
    public Iuran findByAnggotaAndPeriode(int anggotaId, int bulan, int tahun) throws SQLException {
        String sql = """
            SELECT i.id, i.anggota_id, i.bulan, i.tahun, i.nominal, i.tanggal_bayar, i.status_bayar,
                   a.nama_lengkap, a.nomor_kontak, a.status AS status_anggota,
                   k.nama AS nama_kategori, s.nama AS nama_subkelompok
            FROM iuran i
            JOIN anggota a ON i.anggota_id = a.id
            LEFT JOIN kategori_tingkat k ON a.kategori_tingkat_id = k.id
            LEFT JOIN subkelompok s ON a.subkelompok_id = s.id
            WHERE i.anggota_id = ? AND i.bulan = ? AND i.tahun = ?
        """;

        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, anggotaId);
            stmt.setInt(2, bulan);
            stmt.setInt(3, tahun);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToIuran(rs);
                }
            }
        }
        return null;
    }

    @Override
    public boolean save(Iuran iuran) throws SQLException {
        String sql = "INSERT INTO iuran (anggota_id, bulan, tahun, nominal, tanggal_bayar, status_bayar) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, iuran.getAnggotaId());
            stmt.setInt(2, iuran.getBulan());
            stmt.setInt(3, iuran.getTahun());
            if (iuran.getNominal() != null) {
                stmt.setBigDecimal(4, iuran.getNominal());
            } else {
                stmt.setNull(4, Types.DECIMAL);
            }
            if (iuran.getTanggalBayar() != null) {
                stmt.setDate(5, Date.valueOf(iuran.getTanggalBayar()));
            } else {
                stmt.setNull(5, Types.DATE);
            }
            stmt.setString(6, iuran.getStatusBayar().name());
            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        iuran.setId(keys.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean update(Iuran iuran) throws SQLException {
        String sql = "UPDATE iuran SET nominal = ?, tanggal_bayar = ?, status_bayar = ? WHERE id = ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (iuran.getNominal() != null) {
                stmt.setBigDecimal(1, iuran.getNominal());
            } else {
                stmt.setNull(1, Types.DECIMAL);
            }
            if (iuran.getTanggalBayar() != null) {
                stmt.setDate(2, Date.valueOf(iuran.getTanggalBayar()));
            } else {
                stmt.setNull(2, Types.DATE);
            }
            stmt.setString(3, iuran.getStatusBayar().name());
            stmt.setInt(4, iuran.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean saveOrUpdate(Iuran iuran) throws SQLException {
        String sql = "INSERT INTO iuran (anggota_id, bulan, tahun, nominal, tanggal_bayar, status_bayar) " +
                     "VALUES (?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE nominal = VALUES(nominal), tanggal_bayar = VALUES(tanggal_bayar), status_bayar = VALUES(status_bayar)";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, iuran.getAnggotaId());
            stmt.setInt(2, iuran.getBulan());
            stmt.setInt(3, iuran.getTahun());
            if (iuran.getNominal() != null) {
                stmt.setBigDecimal(4, iuran.getNominal());
            } else {
                stmt.setNull(4, Types.DECIMAL);
            }
            if (iuran.getTanggalBayar() != null) {
                stmt.setDate(5, Date.valueOf(iuran.getTanggalBayar()));
            } else {
                stmt.setNull(5, Types.DATE);
            }
            stmt.setString(6, iuran.getStatusBayar().name());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM iuran WHERE id = ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int anggotaId, int bulan, int tahun) throws SQLException {
        String sql = "DELETE FROM iuran WHERE anggota_id = ? AND bulan = ? AND tahun = ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, anggotaId);
            stmt.setInt(2, bulan);
            stmt.setInt(3, tahun);
            return stmt.executeUpdate() > 0;
        }
    }

    private Iuran mapResultSetToIuran(ResultSet rs) throws SQLException {
        Iuran i = new Iuran();
        i.setId(rs.getInt("id"));
        i.setAnggotaId(rs.getInt("anggota_id"));
        i.setBulan(rs.getInt("bulan"));
        i.setTahun(rs.getInt("tahun"));

        java.sql.Date tglBayarSql = rs.getDate("tanggal_bayar");
        if (tglBayarSql != null) {
            i.setTanggalBayar(tglBayarSql.toLocalDate());
        }
        i.setStatusBayar(StatusBayar.fromString(rs.getString("status_bayar")));

        i.setNominal(rs.getBigDecimal("nominal"));

        i.setNamaAnggota(rs.getString("nama_lengkap"));
        i.setNomorKontak(rs.getString("nomor_kontak"));
        i.setKategoriTingkat(rs.getString("nama_kategori"));
        i.setSubkelompok(rs.getString("nama_subkelompok"));
        return i;
    }
}
