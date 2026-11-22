package com.pakem.config;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Singleton / Utility class untuk mengelola koneksi database MySQL melalui JDBC.
 */
public class DatabaseConnection {

    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_PORT = "3306";
    private static final String DEFAULT_DB = "pakem_db";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASS = "";
    private static final String DEFAULT_TIMEZONE = "Asia/Makassar";

    private static String host;
    private static String port;
    private static String dbName;
    private static String user;
    private static String password;
    private static String timezone;

    private static Connection connection = null;

    static {
        loadConfiguration();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("[DatabaseConnection] MySQL JDBC Driver tidak ditemukan: " + e.getMessage());
        }
    }

    private static void loadConfiguration() {
        Properties props = new Properties();
        try (InputStream is = DatabaseConnection.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (is != null) {
                props.load(is);
            }
        } catch (Exception e) {
            System.err.println("[DatabaseConnection] Gagal membaca config.properties, menggunakan default: " + e.getMessage());
        }

        host = props.getProperty("db.host", DEFAULT_HOST);
        port = props.getProperty("db.port", DEFAULT_PORT);
        dbName = props.getProperty("db.name", DEFAULT_DB);
        user = props.getProperty("db.user", DEFAULT_USER);
        password = props.getProperty("db.password", DEFAULT_PASS);
        timezone = props.getProperty("db.timezone", DEFAULT_TIMEZONE);
    }

    public static String getJdbcUrl() {
        return String.format("jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=%s",
                host, port, dbName, timezone);
    }

    /**
     * Mendapatkan koneksi aktif ke database.
     * Membuat koneksi baru jika koneksi belum ada atau sudah tertutup.
     */
    public static synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(getJdbcUrl(), user, password);
        }
        return connection;
    }

    /**
     * Menguji apakah koneksi ke database berhasil.
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("[DatabaseConnection] Gagal menguji koneksi: " + e.getMessage());
            return false;
        }
    }

    /**
     * Menutup koneksi database aktif jika ada.
     */
    public static synchronized void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println("[DatabaseConnection] Error saat menutup koneksi: " + e.getMessage());
            } finally {
                connection = null;
            }
        }
    }

    // Setter untuk fleksibilitas konfigurasi runtime jika diperlukan
    public static void setCredentials(String newHost, String newPort, String newDb, String newUser, String newPass) {
        host = newHost;
        port = newPort;
        dbName = newDb;
        user = newUser;
        password = newPass;
        closeConnection();
    }
}
