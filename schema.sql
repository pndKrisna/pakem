-- ============================================================
-- Skema Database: Pakem (Sistem Administrasi Sanggar Seni Tabuh)
-- Versi: 1.0
-- Target RDBMS: MySQL 8.0+
-- ============================================================

CREATE DATABASE IF NOT EXISTS `pakem_db` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `pakem_db`;

-- ------------------------------------------------------------
-- 0. Hapus Tabel Lama (Urutan terbalik dari dependensi foreign key)
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `iuran`;
DROP TABLE IF EXISTS `presensi`;
DROP TABLE IF EXISTS `sesi_materi`;
DROP TABLE IF EXISTS `jadwal_latihan`;
DROP TABLE IF EXISTS `pemain_tim`;
DROP TABLE IF EXISTS `tim_event`;
DROP TABLE IF EXISTS `event`;
DROP TABLE IF EXISTS `materi_tabuh`;
DROP TABLE IF EXISTS `riwayat_tingkat`;
DROP TABLE IF EXISTS `anggota`;
DROP TABLE IF EXISTS `subkelompok`;
DROP TABLE IF EXISTS `kategori_tingkat`;
DROP TABLE IF EXISTS `settings`;
DROP TABLE IF EXISTS `users`;

-- ------------------------------------------------------------
-- 1. Tabel users
-- ------------------------------------------------------------
CREATE TABLE `users` (
    `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(50) NOT NULL UNIQUE,
    `password_hash` VARCHAR(255) NOT NULL,
    `role` ENUM('PENGURUS', 'PELATIH') NOT NULL,
    `is_active` TINYINT(1) NOT NULL DEFAULT 1,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 2. Tabel settings (Konfigurasi Key-Value)
-- ------------------------------------------------------------
CREATE TABLE `settings` (
    `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `setting_key` VARCHAR(100) NOT NULL UNIQUE,
    `setting_value` TEXT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 3. Tabel kategori_tingkat (Master Tingkat Kemampuan)
-- ------------------------------------------------------------
CREATE TABLE `kategori_tingkat` (
    `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `nama` VARCHAR(100) NOT NULL UNIQUE,
    `urutan` INT NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 4. Tabel subkelompok (Sub-group dalam Kategori Tingkat)
-- ------------------------------------------------------------
CREATE TABLE `subkelompok` (
    `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `kategori_tingkat_id` INT UNSIGNED NOT NULL,
    `nama` VARCHAR(100) NOT NULL,
    UNIQUE KEY `uq_subkelompok_kategori` (`kategori_tingkat_id`, `nama`),
    CONSTRAINT `fk_subkelompok_kategori` 
        FOREIGN KEY (`kategori_tingkat_id`) 
        REFERENCES `kategori_tingkat` (`id`) 
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 5. Tabel anggota
-- ------------------------------------------------------------
CREATE TABLE `anggota` (
    `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `nama_lengkap` VARCHAR(150) NOT NULL,
    `nomor_kontak` VARCHAR(20) NULL,
    `tanggal_bergabung` DATE NOT NULL,
    `status` ENUM('AKTIF', 'VAKUM', 'NONAKTIF') NOT NULL DEFAULT 'AKTIF',
    `kategori_tingkat_id` INT UNSIGNED NULL,
    `subkelompok_id` INT UNSIGNED NULL,
    `catatan` TEXT NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_anggota_kategori` 
        FOREIGN KEY (`kategori_tingkat_id`) 
        REFERENCES `kategori_tingkat` (`id`) 
        ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT `fk_anggota_subkelompok` 
        FOREIGN KEY (`subkelompok_id`) 
        REFERENCES `subkelompok` (`id`) 
        ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 6. Tabel riwayat_tingkat (Arsip Kenaikan / Perubahan Tingkat)
-- ------------------------------------------------------------
CREATE TABLE `riwayat_tingkat` (
    `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `anggota_id` INT UNSIGNED NOT NULL,
    `kategori_tingkat_id_lama` INT UNSIGNED NULL,
    `kategori_tingkat_id_baru` INT UNSIGNED NULL,
    `tanggal_perubahan` DATE NOT NULL,
    `keterangan` TEXT NULL,
    CONSTRAINT `fk_riwayat_anggota` 
        FOREIGN KEY (`anggota_id`) 
        REFERENCES `anggota` (`id`) 
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_riwayat_tingkat_lama` 
        FOREIGN KEY (`kategori_tingkat_id_lama`) 
        REFERENCES `kategori_tingkat` (`id`) 
        ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT `fk_riwayat_tingkat_baru` 
        FOREIGN KEY (`kategori_tingkat_id_baru`) 
        REFERENCES `kategori_tingkat` (`id`) 
        ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 7. Tabel materi_tabuh (Master Repertoar Tabuh / Gending)
-- ------------------------------------------------------------
CREATE TABLE `materi_tabuh` (
    `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `nama` VARCHAR(200) NOT NULL UNIQUE,
    `deskripsi` TEXT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 8. Tabel event (Pertunjukan / Acara)
-- ------------------------------------------------------------
CREATE TABLE `event` (
    `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `nama_acara` VARCHAR(200) NOT NULL,
    `jenis_acara` ENUM('JOB', 'NGAYAH', 'UJIAN') NOT NULL,
    `tanggal` DATE NOT NULL,
    `waktu` TIME NULL,
    `lokasi` VARCHAR(255) NULL,
    `catatan` TEXT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 9. Tabel tim_event (Multi-Tim per Event)
-- ------------------------------------------------------------
CREATE TABLE `tim_event` (
    `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `event_id` INT UNSIGNED NOT NULL,
    `nama_tim` VARCHAR(100) NOT NULL,
    `catatan` TEXT NULL,
    CONSTRAINT `fk_tim_event` 
        FOREIGN KEY (`event_id`) 
        REFERENCES `event` (`id`) 
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 10. Tabel pemain_tim (Pemain Terpilih per Tim Event)
-- ------------------------------------------------------------
CREATE TABLE `pemain_tim` (
    `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `tim_event_id` INT UNSIGNED NOT NULL,
    `anggota_id` INT UNSIGNED NOT NULL,
    `status_kesediaan` ENUM('BELUM_DITANYA', 'BERSEDIA', 'TIDAK_BERSEDIA') NOT NULL DEFAULT 'BELUM_DITANYA',
    `nominal_honor` DECIMAL(12,2) NULL,
    `status_honor` ENUM('BELUM_DIBAGIKAN', 'SUDAH_DIBAGIKAN') NULL,
    UNIQUE KEY `uq_pemain_tim` (`tim_event_id`, `anggota_id`),
    CONSTRAINT `fk_pemain_tim_event` 
        FOREIGN KEY (`tim_event_id`) 
        REFERENCES `tim_event` (`id`) 
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_pemain_anggota` 
        FOREIGN KEY (`anggota_id`) 
        REFERENCES `anggota` (`id`) 
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 11. Tabel jadwal_latihan
-- ------------------------------------------------------------
CREATE TABLE `jadwal_latihan` (
    `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `jenis_jadwal` ENUM('RUTIN', 'KHUSUS_EVENT') NOT NULL,
    `tanggal` DATE NOT NULL,
    `jam_mulai` TIME NULL,
    `jam_selesai` TIME NULL,
    `lokasi` VARCHAR(255) NULL,
    `kategori_tingkat_id` INT UNSIGNED NULL,
    `subkelompok_id` INT UNSIGNED NULL,
    `event_id` INT UNSIGNED NULL,
    `catatan` TEXT NULL,
    CONSTRAINT `fk_jadwal_kategori` 
        FOREIGN KEY (`kategori_tingkat_id`) 
        REFERENCES `kategori_tingkat` (`id`) 
        ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT `fk_jadwal_subkelompok` 
        FOREIGN KEY (`subkelompok_id`) 
        REFERENCES `subkelompok` (`id`) 
        ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT `fk_jadwal_event` 
        FOREIGN KEY (`event_id`) 
        REFERENCES `event` (`id`) 
        ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 12. Tabel sesi_materi (Materi & Progres per Sesi Latihan)
-- ------------------------------------------------------------
CREATE TABLE `sesi_materi` (
    `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `jadwal_latihan_id` INT UNSIGNED NOT NULL,
    `materi_tabuh_id` INT UNSIGNED NOT NULL,
    `catatan_progres` TEXT NULL,
    CONSTRAINT `fk_sesi_jadwal` 
        FOREIGN KEY (`jadwal_latihan_id`) 
        REFERENCES `jadwal_latihan` (`id`) 
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_sesi_materi` 
        FOREIGN KEY (`materi_tabuh_id`) 
        REFERENCES `materi_tabuh` (`id`) 
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 13. Tabel presensi (Kehadiran Latihan Anggota)
-- ------------------------------------------------------------
CREATE TABLE `presensi` (
    `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `jadwal_latihan_id` INT UNSIGNED NOT NULL,
    `anggota_id` INT UNSIGNED NOT NULL,
    `status_hadir` ENUM('HADIR', 'IZIN', 'SAKIT', 'TANPA_KETERANGAN') NOT NULL,
    `keterangan` TEXT NULL,
    UNIQUE KEY `uq_presensi_jadwal_anggota` (`jadwal_latihan_id`, `anggota_id`),
    CONSTRAINT `fk_presensi_jadwal` 
        FOREIGN KEY (`jadwal_latihan_id`) 
        REFERENCES `jadwal_latihan` (`id`) 
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_presensi_anggota` 
        FOREIGN KEY (`anggota_id`) 
        REFERENCES `anggota` (`id`) 
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 14. Tabel iuran (Pencatatan Iuran Bulanan)
-- ------------------------------------------------------------
CREATE TABLE `iuran` (
    `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `anggota_id` INT UNSIGNED NOT NULL,
    `bulan` TINYINT UNSIGNED NOT NULL,
    `tahun` SMALLINT UNSIGNED NOT NULL,
    `nominal` DECIMAL(12,2) NULL,
    `tanggal_bayar` DATE NULL,
    `status_bayar` ENUM('BELUM_BAYAR', 'LUNAS') NOT NULL DEFAULT 'BELUM_BAYAR',
    UNIQUE KEY `uq_iuran_anggota_bulan_tahun` (`anggota_id`, `bulan`, `tahun`),
    CONSTRAINT `fk_iuran_anggota` 
        FOREIGN KEY (`anggota_id`) 
        REFERENCES `anggota` (`id`) 
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- SEED DATA (DATA AWAL)
-- ============================================================

-- Akun Pengguna Bawaan (Default Users)
-- Password pengurus: pengurus123 (SHA-256)
-- Password pelatih:  pelatih123  (SHA-256)
INSERT INTO `users` (`username`, `password_hash`, `role`, `is_active`) VALUES
('pengurus', '10a7d7922e860efbde52cb12326e758446a97399b9a2d46135d62d43d6561743', 'PENGURUS', 1),
('pelatih',  '90da9876474dc0f3783baa0d3bc79fcfbb660dee7bdbfdfac193a07e813a2d33', 'PELATIH',  1);

-- Pengaturan Awal Sanggar (Settings)
INSERT INTO `settings` (`setting_key`, `setting_value`) VALUES
('nama_sanggar', 'Sanggar Seni Tabuh Pakem'),
('iuran_default', '50000'),
('batas_kuning', '15'),
('batas_merah', '25'),
('kapasitas_kelompok', '25');

-- Kategori Tingkat Awal (Inspirasi Sanggar - Bisa Diubah Kapan Saja)
INSERT INTO `kategori_tingkat` (`nama`, `urutan`) VALUES
('Dasar', 1),
('Madya', 2),
('Purna', 3),
('Inti',  4);

-- Subkelompok Contoh untuk Kategori Madya
INSERT INTO `subkelompok` (`kategori_tingkat_id`, `nama`) VALUES
(2, 'Madya A'),
(2, 'Madya B');
