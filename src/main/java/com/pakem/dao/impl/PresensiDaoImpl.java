package com.pakem.dao.impl;

import com.pakem.config.DatabaseConnection;
import com.pakem.dao.interfaces.IPresensiDao;
import com.pakem.model.Presensi;
import com.pakem.model.RekapPresensi;
import com.pakem.model.enums.StatusHadir;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementasi JDBC untuk IPresensiDao.
 * Semua kolom tabel 100% cocok dengan schema.sql (presensi, anggota, jadwal_latihan).
 */
public class PresensiDaoImpl implements IPresensiDao {

    @Override
    public List<Presensi> findByJadwalId(int jadwalId) throws SQLException {
        List<Presensi> list = new ArrayList<>();
        String sql = "SELECT p.id, p.jadwal_latihan_id, p.anggota_id, p.status_hadir, p.keterangan, " +
                     "a.nama_lengkap AS nama_anggota, a.nomor_kontak, " +
                     "k.nama AS nama_kategori, s.nama AS nama_subkelompok, " +
                     "j.tanggal AS tanggal_jadwal, j.jenis_jadwal " +
                     "FROM presensi p " +
                     "JOIN anggota a ON p.anggota_id = a.id " +
                     "JOIN jadwal_latihan j ON p.jadwal_latihan_id = j.id " +
                     "LEFT JOIN kategori_tingkat k ON a.kategori_tingkat_id = k.id " +
                     "LEFT JOIN subkelompok s ON a.subkelompok_id = s.id " +
                     "WHERE p.jadwal_latihan_id = ? " +
                     "ORDER BY a.nama_lengkap ASC";

        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, jadwalId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToPresensi(rs));
                }
            }
        }
        return list;
    }

    @Override
    public Presensi findByJadwalAndAnggota(int jadwalId, int anggotaId) throws SQLException {
        String sql = "SELECT p.id, p.jadwal_latihan_id, p.anggota_id, p.status_hadir, p.keterangan, " +
                     "a.nama_lengkap AS nama_anggota, a.nomor_kontak, " +
                     "k.nama AS nama_kategori, s.nama AS nama_subkelompok, " +
                     "j.tanggal AS tanggal_jadwal, j.jenis_jadwal " +
                     "FROM presensi p " +
                     "JOIN anggota a ON p.anggota_id = a.id " +
                     "JOIN jadwal_latihan j ON p.jadwal_latihan_id = j.id " +
                     "LEFT JOIN kategori_tingkat k ON a.kategori_tingkat_id = k.id " +
                     "LEFT JOIN subkelompok s ON a.subkelompok_id = s.id " +
                     "WHERE p.jadwal_latihan_id = ? AND p.anggota_id = ?";

        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, jadwalId);
            stmt.setInt(2, anggotaId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPresensi(rs);
                }
            }
        }
        return null;
    }

    @Override
    public Presensi findById(int id) throws SQLException {
        String sql = "SELECT p.id, p.jadwal_latihan_id, p.anggota_id, p.status_hadir, p.keterangan, " +
                     "a.nama_lengkap AS nama_anggota, a.nomor_kontak, " +
                     "k.nama AS nama_kategori, s.nama AS nama_subkelompok, " +
                     "j.tanggal AS tanggal_jadwal, j.jenis_jadwal " +
                     "FROM presensi p " +
                     "JOIN anggota a ON p.anggota_id = a.id " +
                     "JOIN jadwal_latihan j ON p.jadwal_latihan_id = j.id " +
                     "LEFT JOIN kategori_tingkat k ON a.kategori_tingkat_id = k.id " +
                     "LEFT JOIN subkelompok s ON a.subkelompok_id = s.id " +
                     "WHERE p.id = ?";

        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPresensi(rs);
                }
            }
        }
        return null;
    }

    @Override
    public boolean save(Presensi presensi) throws SQLException {
        String sql = "INSERT INTO presensi (jadwal_latihan_id, anggota_id, status_hadir, keterangan) " +
                     "VALUES (?, ?, ?, ?)";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, presensi.getJadwalLatihanId());
            stmt.setInt(2, presensi.getAnggotaId());
            stmt.setString(3, presensi.getStatusHadir().name());
            stmt.setString(4, presensi.getKeterangan());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        presensi.setId(keys.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean update(Presensi presensi) throws SQLException {
        String sql = "UPDATE presensi SET status_hadir = ?, keterangan = ? WHERE id = ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, presensi.getStatusHadir().name());
            stmt.setString(2, presensi.getKeterangan());
            stmt.setInt(3, presensi.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean saveOrUpdate(Presensi presensi) throws SQLException {
        String sql = "INSERT INTO presensi (jadwal_latihan_id, anggota_id, status_hadir, keterangan) " +
                     "VALUES (?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE status_hadir = VALUES(status_hadir), keterangan = VALUES(keterangan)";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, presensi.getJadwalLatihanId());
            stmt.setInt(2, presensi.getAnggotaId());
            stmt.setString(3, presensi.getStatusHadir().name());
            stmt.setString(4, presensi.getKeterangan());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        presensi.setId(keys.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean saveBatch(List<Presensi> list) throws SQLException {
        if (list == null || list.isEmpty()) return true;

        String sql = "INSERT INTO presensi (jadwal_latihan_id, anggota_id, status_hadir, keterangan) " +
                     "VALUES (?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE status_hadir = VALUES(status_hadir), keterangan = VALUES(keterangan)";

        Connection conn = DatabaseConnection.getConnection();
        boolean originalAutoCommit = conn.getAutoCommit();
        try {
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                for (Presensi p : list) {
                    stmt.setInt(1, p.getJadwalLatihanId());
                    stmt.setInt(2, p.getAnggotaId());
                    stmt.setString(3, p.getStatusHadir().name());
                    stmt.setString(4, p.getKeterangan());
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
            conn.commit();
            return true;
        } catch (SQLException ex) {
            conn.rollback();
            throw ex;
        } finally {
            conn.setAutoCommit(originalAutoCommit);
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM presensi WHERE id = ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteByJadwalId(int jadwalId) throws SQLException {
        String sql = "DELETE FROM presensi WHERE jadwal_latihan_id = ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, jadwalId);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public int countByJadwalAndStatus(int jadwalId, String statusHadir) throws SQLException {
        String sql = "SELECT COUNT(*) FROM presensi WHERE jadwal_latihan_id = ? AND status_hadir = ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, jadwalId);
            stmt.setString(2, statusHadir);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    @Override
    public int countTotalByJadwal(int jadwalId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM presensi WHERE jadwal_latihan_id = ?";
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

    @Override
    public List<RekapPresensi> getRekapitulasi(Integer kategoriId, Integer subkelompokId,
                                                LocalDate dariTanggal, LocalDate sampaiTanggal) throws SQLException {
        List<RekapPresensi> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT " +
                "a.id AS anggota_id, " +
                "a.nama_lengkap AS nama_anggota, " +
                "a.nomor_kontak, " +
                "k.nama AS nama_kategori, " +
                "s.nama AS nama_subkelompok, " +
                "COUNT(p.id) AS total_pertemuan, " +
                "COALESCE(SUM(CASE WHEN p.status_hadir = 'HADIR' THEN 1 ELSE 0 END), 0) AS jumlah_hadir, " +
                "COALESCE(SUM(CASE WHEN p.status_hadir = 'IZIN' THEN 1 ELSE 0 END), 0) AS jumlah_izin, " +
                "COALESCE(SUM(CASE WHEN p.status_hadir = 'SAKIT' THEN 1 ELSE 0 END), 0) AS jumlah_sakit, " +
                "COALESCE(SUM(CASE WHEN p.status_hadir = 'TANPA_KETERANGAN' THEN 1 ELSE 0 END), 0) AS jumlah_alpa " +
                "FROM anggota a " +
                "LEFT JOIN kategori_tingkat k ON a.kategori_tingkat_id = k.id " +
                "LEFT JOIN subkelompok s ON a.subkelompok_id = s.id " +
                "LEFT JOIN presensi p ON p.anggota_id = a.id " +
                "LEFT JOIN jadwal_latihan j ON p.jadwal_latihan_id = j.id " +
                "WHERE a.status = 'AKTIF' "
        );

        List<Object> params = new ArrayList<>();

        if (kategoriId != null && kategoriId > 0) {
            sql.append("AND a.kategori_tingkat_id = ? ");
            params.add(kategoriId);
        }

        if (subkelompokId != null && subkelompokId > 0) {
            sql.append("AND a.subkelompok_id = ? ");
            params.add(subkelompokId);
        }

        if (dariTanggal != null) {
            sql.append("AND (j.tanggal IS NULL OR j.tanggal >= ?) ");
            params.add(Date.valueOf(dariTanggal));
        }

        if (sampaiTanggal != null) {
            sql.append("AND (j.tanggal IS NULL OR j.tanggal <= ?) ");
            params.add(Date.valueOf(sampaiTanggal));
        }

        sql.append("GROUP BY a.id, a.nama_lengkap, a.nomor_kontak, k.nama, s.nama ");
        sql.append("ORDER BY a.nama_lengkap ASC");

        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    RekapPresensi r = new RekapPresensi();
                    r.setAnggotaId(rs.getInt("anggota_id"));
                    r.setNamaAnggota(rs.getString("nama_anggota"));
                    r.setNomorKontak(rs.getString("nomor_kontak"));
                    r.setNamaKategori(rs.getString("nama_kategori"));
                    r.setNamaSubkelompok(rs.getString("nama_subkelompok"));

                    r.setTotalPertemuan(rs.getInt("total_pertemuan"));
                    r.setJumlahHadir(rs.getInt("jumlah_hadir"));
                    r.setJumlahIzin(rs.getInt("jumlah_izin"));
                    r.setJumlahSakit(rs.getInt("jumlah_sakit"));
                    r.setJumlahAlpa(rs.getInt("jumlah_alpa"));

                    list.add(r);
                }
            }
        }
        return list;
    }

    private Presensi mapResultSetToPresensi(ResultSet rs) throws SQLException {
        Presensi p = new Presensi();
        p.setId(rs.getInt("id"));
        p.setJadwalLatihanId(rs.getInt("jadwal_latihan_id"));
        p.setAnggotaId(rs.getInt("anggota_id"));
        p.setStatusHadir(StatusHadir.valueOf(rs.getString("status_hadir").toUpperCase()));
        p.setKeterangan(rs.getString("keterangan"));

        p.setNamaAnggota(rs.getString("nama_anggota"));
        p.setNomorKontak(rs.getString("nomor_kontak"));
        p.setNamaKategori(rs.getString("nama_kategori"));
        p.setNamaSubkelompok(rs.getString("nama_subkelompok"));

        Date tgl = rs.getDate("tanggal_jadwal");
        if (tgl != null) {
            p.setTanggalJadwal(tgl.toLocalDate());
        }
        p.setJenisJadwal(rs.getString("jenis_jadwal"));

        return p;
    }
}
