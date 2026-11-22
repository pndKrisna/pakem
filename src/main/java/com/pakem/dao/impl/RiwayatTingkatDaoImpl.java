package com.pakem.dao.impl;

import com.pakem.config.DatabaseConnection;
import com.pakem.dao.interfaces.IRiwayatTingkatDao;
import com.pakem.model.RiwayatTingkat;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementasi JDBC untuk IRiwayatTingkatDao.
 */
public class RiwayatTingkatDaoImpl implements IRiwayatTingkatDao {

    @Override
    public boolean save(RiwayatTingkat riwayat) throws SQLException {
        String sql = "INSERT INTO riwayat_tingkat (anggota_id, kategori_tingkat_id_lama, kategori_tingkat_id_baru, tanggal_perubahan, keterangan) " +
                     "VALUES (?, ?, ?, ?, ?)";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, riwayat.getAnggotaId());

            if (riwayat.getKategoriTingkatIdLama() != null) {
                stmt.setInt(2, riwayat.getKategoriTingkatIdLama());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }

            if (riwayat.getKategoriTingkatIdBaru() != null) {
                stmt.setInt(3, riwayat.getKategoriTingkatIdBaru());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }

            stmt.setDate(4, Date.valueOf(riwayat.getTanggalPerubahan()));
            stmt.setString(5, riwayat.getKeterangan());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        riwayat.setId(keys.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public List<RiwayatTingkat> findByAnggotaId(int anggotaId) throws SQLException {
        List<RiwayatTingkat> list = new ArrayList<>();
        String sql = "SELECT r.id, r.anggota_id, r.kategori_tingkat_id_lama, r.kategori_tingkat_id_baru, " +
                     "r.tanggal_perubahan, r.keterangan, " +
                     "a.nama_lengkap AS nama_anggota, " +
                     "kl.nama AS nama_tingkat_lama, " +
                     "kb.nama AS nama_tingkat_baru " +
                     "FROM riwayat_tingkat r " +
                     "JOIN anggota a ON r.anggota_id = a.id " +
                     "LEFT JOIN kategori_tingkat kl ON r.kategori_tingkat_id_lama = kl.id " +
                     "LEFT JOIN kategori_tingkat kb ON r.kategori_tingkat_id_baru = kb.id " +
                     "WHERE r.anggota_id = ? " +
                     "ORDER BY r.tanggal_perubahan DESC, r.id DESC";

        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, anggotaId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    RiwayatTingkat item = new RiwayatTingkat();
                    item.setId(rs.getInt("id"));
                    item.setAnggotaId(rs.getInt("anggota_id"));

                    int lama = rs.getInt("kategori_tingkat_id_lama");
                    item.setKategoriTingkatIdLama(rs.wasNull() ? null : lama);

                    int baru = rs.getInt("kategori_tingkat_id_baru");
                    item.setKategoriTingkatIdBaru(rs.wasNull() ? null : baru);

                    Date tgl = rs.getDate("tanggal_perubahan");
                    if (tgl != null) {
                        item.setTanggalPerubahan(tgl.toLocalDate());
                    }

                    item.setKeterangan(rs.getString("keterangan"));
                    item.setNamaAnggota(rs.getString("nama_anggota"));
                    item.setNamaTingkatLama(rs.getString("nama_tingkat_lama"));
                    item.setNamaTingkatBaru(rs.getString("nama_tingkat_baru"));

                    list.add(item);
                }
            }
        }
        return list;
    }
}
