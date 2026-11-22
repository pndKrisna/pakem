package com.pakem.dao.interfaces;

import java.sql.SQLException;

/**
 * Interface DAO untuk mengelola pengaturan sistem sanggar (key-value).
 */
public interface ISettingsDao {
    String get(String key, String defaultValue) throws SQLException;
    boolean set(String key, String value) throws SQLException;
}
