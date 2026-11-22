package com.pakem.dao.interfaces;

import com.pakem.model.User;
import java.sql.SQLException;
import java.util.List;

/**
 * Kontrak Abstraksi Akses Data (DAO) untuk entitas User.
 */
public interface IUserDao {

    /**
     * Mencari pengguna berdasarkan username unik.
     * @param username username login
     * @return User (instance polimorfik Pengurus atau Pelatih), atau null jika tidak ditemukan
     */
    User findByUsername(String username) throws SQLException;

    /**
     * Mencari pengguna berdasarkan ID unik.
     * @param id ID user
     * @return User (instance polimorfik Pengurus atau Pelatih), atau null jika tidak ditemukan
     */
    User findById(int id) throws SQLException;

    /**
     * Mengambil seluruh daftar pengguna.
     */
    List<User> findAll() throws SQLException;

    /**
     * Menyimpan pengguna baru ke dalam database.
     */
    boolean save(User user) throws SQLException;

    /**
     * Memperbarui password hash pengguna.
     */
    boolean updatePassword(int id, String newPasswordHash) throws SQLException;

    /**
     * Memperbarui status keaktifan akun pengguna.
     */
    boolean updateStatus(int id, boolean isActive) throws SQLException;
}
