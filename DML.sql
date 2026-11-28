-- ============================================================
-- DML RESET & SEED DUMMY DATA: Pakem (pakem_db)
-- Target RDBMS: MySQL 8.0+
-- ============================================================

USE `pakem_db`;

-- ------------------------------------------------------------
-- 0. Kosongkan Semua Tabel & Reset AUTO_INCREMENT
-- ------------------------------------------------------------
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE `iuran`;
TRUNCATE TABLE `presensi`;
TRUNCATE TABLE `sesi_materi`;
TRUNCATE TABLE `jadwal_latihan`;
TRUNCATE TABLE `pemain_tim`;
TRUNCATE TABLE `tim_event`;
TRUNCATE TABLE `event`;
TRUNCATE TABLE `materi_tabuh`;
TRUNCATE TABLE `riwayat_tingkat`;
TRUNCATE TABLE `anggota`;
TRUNCATE TABLE `subkelompok`;
TRUNCATE TABLE `kategori_tingkat`;
TRUNCATE TABLE `settings`;
TRUNCATE TABLE `users`;

SET FOREIGN_KEY_CHECKS = 1;

-- ------------------------------------------------------------
-- 1. Tabel users (Password: pengurus123 & pelatih123)
-- ------------------------------------------------------------
INSERT INTO `users` (`id`, `username`, `password_hash`, `role`, `is_active`) VALUES
(1, 'pengurus', '10a7d7922e860efbde52cb12326e758446a97399b9a2d46135d62d43d6561743', 'PENGURUS', 1),
(2, 'pelatih',  '90da9876474dc0f3783baa0d3bc79fcfbb660dee7bdbfdfac193a07e813a2d33', 'PELATIH',  1);

-- ------------------------------------------------------------
-- 2. Tabel settings (Default Iuran 100.000)
-- ------------------------------------------------------------
INSERT INTO `settings` (`id`, `setting_key`, `setting_value`) VALUES
(1, 'nama_sanggar', 'Sanggar Seni Tabuh Tripitaka'),
(2, 'iuran_default', '100000'),
(3, 'batas_kuning', '15'),
(4, 'batas_merah', '25'),
(5, 'kapasitas_kelompok', '25');

-- ------------------------------------------------------------
-- 3. Tabel kategori_tingkat
-- ------------------------------------------------------------
INSERT INTO `kategori_tingkat` (`id`, `nama`, `urutan`) VALUES
(1, 'Dasar', 1),
(2, 'Madya', 2),
(3, 'Purna', 3),
(4, 'Inti',  4);

-- ------------------------------------------------------------
-- 4. Tabel subkelompok
-- ------------------------------------------------------------
INSERT INTO `subkelompok` (`id`, `kategori_tingkat_id`, `nama`) VALUES
(1, 1, 'Dasar A'),
(2, 1, 'Dasar B'),
(3, 2, 'Madya A'),
(4, 2, 'Madya B'),
(5, 3, 'Purna A'),
(6, 3, 'Purna B'),
(7, 4, 'Inti Sekaa Gong');

-- ------------------------------------------------------------
-- 5. Tabel anggota (105 Anggota - Nama Seniman Bali)
-- ------------------------------------------------------------
INSERT INTO `anggota` (`id`, `nama_lengkap`, `nomor_kontak`, `tanggal_bergabung`, `status`, `kategori_tingkat_id`, `subkelompok_id`, `catatan`) VALUES
-- Kategori 1: Dasar (ID 1 - 32)
(1,   'I Gede Manik Jagaraga',          '081236100101', '2026-07-05', 'AKTIF',    1, 1, 'Instrumen Gangsa Pemade'),
(2,   'I Ketut Merdana Kedis',          '081236100102', '2026-07-05', 'AKTIF',    1, 1, 'Instrumen Kantilan'),
(3,   'I Wayan Lotring Kuta',           '081236100103', '2026-07-08', 'AKTIF',    1, 1, 'Instrumen Reyong 1'),
(4,   'I Nyoman Kaler Pemecutan',       '081236100104', '2026-07-08', 'AKTIF',    1, 1, 'Instrumen Reyong 2'),
(5,   'I Wayan Beratha Belaluan',       '081236100105', '2026-07-10', 'AKTIF',    1, 1, 'Instrumen Calung'),
(6,   'I Made Bandem Singapadu',        '081236100106', '2026-07-10', 'AKTIF',    1, 1, 'Instrumen Jegogan'),
(7,   'I Nyoman Windha Banjar',         '081236100107', '2026-07-12', 'AKTIF',    1, 1, 'Instrumen Kendang Lanang dasar'),
(8,   'I Wayan Dibia Singapadu',        '081236100108', '2026-07-12', 'AKTIF',    1, 1, 'Instrumen Ceng-ceng Ricik'),
(9,   'I Ketut Mario Tabanan',          '081236100109', '2026-07-15', 'AKTIF',    1, 1, 'Instrumen Kajar'),
(10,  'Ni Ketut Reneng Kedaton',        '081236100110', '2026-07-15', 'AKTIF',    1, 1, 'Instrumen Kempli'),
(11,  'Ni Gusti Ayu Raka Rasmi',        '081236100111', '2026-07-18', 'AKTIF',    1, 1, 'Instrumen Gangsa Kantilan'),
(12,  'I Gusti Putu Made Geria',        '081236100112', '2026-07-18', 'AKTIF',    1, 1, 'Instrumen Gangsa Pemade'),
(13,  'I Wayan Suweca Kayumas',         '081236100113', '2026-07-20', 'AKTIF',    1, 1, 'Instrumen Reyong 3'),
(14,  'I Komang Astita Denpasar',       '081236100114', '2026-07-20', 'AKTIF',    1, 1, 'Instrumen Reyong 4'),
(15,  'I Made Subandi Batuyang',        '081236100115', '2026-07-22', 'AKTIF',    1, 1, 'Instrumen Gong & Kempul'),
(16,  'I Wayan Sadra Solo',             '081236100116', '2026-07-22', 'AKTIF',    1, 1, 'Instrumen Ugal dasar'),
(17,  'I Nyoman Rembang Sesetan',       '081236100117', '2026-08-01', 'AKTIF',    1, 2, 'Instrumen Gangsa Pemade'),
(18,  'Anak Agung Gde Mandera',         '081236100118', '2026-08-01', 'AKTIF',    1, 2, 'Instrumen Kantilan'),
(19,  'Cokorda Agung Mas Peliatan',     '081236100119', '2026-08-03', 'AKTIF',    1, 2, 'Instrumen Calung'),
(20,  'Ida Bagus Oka Wirjana',          '081236100120', '2026-08-03', 'AKTIF',    1, 2, 'Instrumen Jegogan'),
(21,  'I Made Lebah Peliatan',          '081236100121', '2026-08-05', 'AKTIF',    1, 2, 'Instrumen Reyong 1'),
(22,  'I Wayan Gandera Peliatan',       '081236100122', '2026-08-05', 'AKTIF',    1, 2, 'Instrumen Reyong 2'),
(23,  'I Ketut Gede Asnawa',            '081236100123', '2026-08-08', 'AKTIF',    1, 2, 'Instrumen Ceng-ceng Kopyak'),
(24,  'I Nyoman Catra Batuan',          '081236100124', '2026-08-08', 'AKTIF',    1, 2, 'Instrumen Kendang Wadon dasar'),
(25,  'Ni Luh Suasthi Widyastuti',      '081236100125', '2026-08-10', 'AKTIF',    1, 2, 'Instrumen Kantilan'),
(26,  'Desak Made Suarti Laksmi',       '081236100126', '2026-08-10', 'AKTIF',    1, 2, 'Instrumen Gangsa Pemade'),
(27,  'Ni Ketut Suryatini',             '081236100127', '2026-08-12', 'AKTIF',    1, 2, 'Instrumen Reyong 3'),
(28,  'I Wayan Rai Sukawati',           '081236100128', '2026-08-12', 'AKTIF',    1, 2, 'Instrumen Penyacah'),
(29,  'I Made Sidia Bona',              '081236100129', '2026-08-15', 'AKTIF',    1, 2, 'Instrumen Kajar'),
(30,  'I Ketut Kodi Singapadu',         '081236100130', '2026-08-15', 'AKTIF',    1, 2, 'Instrumen Gong'),
(31,  'I Wayan Wija Sukawati',          '081236100131', '2026-08-18', 'VAKUM',    1, NULL, 'Izin sementara persiapan lomba sekolah'),
(32,  'Ida Bagus Nyoman Mas',           '081236100132', '2026-08-18', 'VAKUM',    1, NULL, 'Vakum sementara karena kegiatan keluarga'),

-- Kategori 2: Madya (ID 33 - 62)
(33,  'I Gusti Ngurah Padang',          '081236200201', '2026-01-10', 'AKTIF',    2, 3, 'Ugal Madya A'),
(34,  'I Made Terip Sukawati',          '081236200202', '2026-01-10', 'AKTIF',    2, 3, 'Gangsa Pemade polos'),
(35,  'I Nyoman Sumandhi Tunjuk',       '081236200203', '2026-01-12', 'AKTIF',    2, 3, 'Gangsa Pemade sangsih'),
(36,  'I Ketut Wirtawan Bongkasa',      '081236200204', '2026-01-12', 'AKTIF',    2, 3, 'Kantilan polos'),
(37,  'I Wayan Konolan Kayumas',        '081236200205', '2026-01-15', 'AKTIF',    2, 3, 'Kantilan sangsih'),
(38,  'I Made Regog Teges',             '081236200206', '2026-01-15', 'AKTIF',    2, 3, 'Reyong penyorog'),
(39,  'I Nyoman Kakul Batuan',          '081236200207', '2026-01-18', 'AKTIF',    2, 3, 'Reyong pengenter'),
(40,  'I Ketut Rinda Blahbatuh',        '081236200208', '2026-01-18', 'AKTIF',    2, 3, 'Kendang Lanang'),
(41,  'Ni Made Wiratini',               '081236200209', '2026-01-20', 'AKTIF',    2, 3, 'Gangsa Pemade'),
(42,  'Ni Komang Sri Wahyuni',          '081236200210', '2026-01-20', 'AKTIF',    2, 3, 'Kantilan'),
(43,  'Ni Putu Eka Yanti',              '081236200211', '2026-01-22', 'AKTIF',    2, 3, 'Calung'),
(44,  'Ni Wayan Sekarini',              '081236200212', '2026-01-22', 'AKTIF',    2, 3, 'Jegogan'),
(45,  'Ni Kadek Dwi Lestari',           '081236200213', '2026-01-25', 'AKTIF',    2, 3, 'Penyacah'),
(46,  'Ni Nyoman Triyana Sari',         '081236200214', '2026-01-25', 'AKTIF',    2, 3, 'Ceng-ceng Ricik'),
(47,  'Ni Ketut Sudiasih',              '081236200215', '2026-01-28', 'AKTIF',    2, 4, 'Ugal Madya B'),
(48,  'I Putu Gede Sukaryana',          '081236200216', '2026-02-02', 'AKTIF',    2, 4, 'Gangsa Pemade polos'),
(49,  'I Made Agus Wardana',            '081236200217', '2026-02-02', 'AKTIF',    2, 4, 'Gangsa Pemade sangsih'),
(50,  'I Komang Triyana Putra',         '081236200218', '2026-02-05', 'AKTIF',    2, 4, 'Kantilan polos'),
(51,  'I Ketut Budiasa Tejakula',       '081236200219', '2026-02-05', 'AKTIF',    2, 4, 'Kantilan sangsih'),
(52,  'I Gede Sudarmawan Kubutambahan', '081236200220', '2026-02-08', 'AKTIF',    2, 4, 'Reyong 1'),
(53,  'I Kadek Budi Suputra',           '081236200221', '2026-02-08', 'AKTIF',    2, 4, 'Reyong 2'),
(54,  'I Nyoman Darmawan Sawan',        '081236200222', '2026-02-10', 'AKTIF',    2, 4, 'Reyong 3'),
(55,  'I Wayan Artawan Sukasada',       '081236200223', '2026-02-10', 'AKTIF',    2, 4, 'Reyong 4'),
(56,  'I Made Suardana Banjar',         '081236200224', '2026-02-12', 'AKTIF',    2, 4, 'Kendang Wadon'),
(57,  'I Komang Yudiantara Busungbiu',  '081236200225', '2026-02-12', 'AKTIF',    2, 4, 'Ceng-ceng Kopyak'),
(58,  'I Ketut Agus Setiawan',          '081236200226', '2026-02-15', 'AKTIF',    2, 4, 'Calung'),
(59,  'I Putu Eka Saputra',             '081236200227', '2026-02-15', 'AKTIF',    2, 4, 'Jegogan'),
(60,  'I Made Dwi Payana',              '081236200228', '2026-02-18', 'VAKUM',    2, NULL, 'Cuti kuliah semester pendek'),
(61,  'Ida Ayu Made Gayatri',           '081236200229', '2026-02-18', 'VAKUM',    2, NULL, 'Pemulihan kesehatan'),
(62,  'Ida Bagus Gede Mantra',          '081236200230', '2026-02-20', 'NONAKTIF', 2, NULL, 'Pindah domisili kerja ke Denpasar'),

-- Kategori 3: Purna (ID 63 - 86)
(63,  'Anak Agung Ngurah Oka',          '081236300301', '2025-06-10', 'AKTIF',    3, 5, 'Ugal Purna A'),
(64,  'Anak Agung Istri Mirah',         '081236300302', '2025-06-10', 'AKTIF',    3, 5, 'Kantilan cepat'),
(65,  'Cokorda Gde Raka Sukawati',      '081236300303', '2025-06-12', 'AKTIF',    3, 5, 'Gangsa Pemade sangsih'),
(66,  'Desak Nyoman Suarningsih',       '081236300304', '2025-06-12', 'AKTIF',    3, 5, 'Gangsa Pemade polos'),
(67,  'Dewa Gede Agung Darma',          '081236300305', '2025-06-15', 'AKTIF',    3, 5, 'Kendang Cedugan Lanang'),
(68,  'Sang Ayu Ketut Puspita',         '081236300306', '2025-06-15', 'AKTIF',    3, 5, 'Penyacah'),
(69,  'Gusti Bagus Wijaya Kusuma',      '081236300307', '2025-06-18', 'AKTIF',    3, 5, 'Reyong pengenter'),
(70,  'Ngakan Made Putra Wijaya',       '081236300308', '2025-06-18', 'AKTIF',    3, 5, 'Reyong penyorog'),
(71,  'Ni Luh Putu Indah Permata',      '081236300309', '2025-06-20', 'AKTIF',    3, 5, 'Kantilan polos'),
(72,  'Ni Made Ayu Ratna Dewi',         '081236300310', '2025-06-20', 'AKTIF',    3, 5, 'Kantilan sangsih'),
(73,  'Ni Komang Ayu Trisnawati',       '081236300311', '2025-06-22', 'AKTIF',    3, 5, 'Calung'),
(74,  'Ni Ketut Novianti',              '081236300312', '2025-06-22', 'AKTIF',    3, 6, 'Ugal Purna B'),
(75,  'Ni Wayan Santiari',              '081236300313', '2025-07-02', 'AKTIF',    3, 6, 'Gangsa Pemade polos'),
(76,  'Ni Kadek Meliani',               '081236300314', '2025-07-02', 'AKTIF',    3, 6, 'Gangsa Pemade sangsih'),
(77,  'Ni Nyoman Wulandari',            '081236300315', '2025-07-05', 'AKTIF',    3, 6, 'Kantilan polos'),
(78,  'Ni Putu Linda Cahyani',          '081236300316', '2025-07-05', 'AKTIF',    3, 6, 'Kantilan sangsih'),
(79,  'Ni Made Anggreni',               '081236300317', '2025-07-08', 'AKTIF',    3, 6, 'Jegogan'),
(80,  'Ni Komang Puspitasari',          '081236300318', '2025-07-08', 'AKTIF',    3, 6, 'Ceng-ceng Ricik'),
(81,  'I Wayan Eka Mahendra',           '081236300319', '2025-07-10', 'AKTIF',    3, 6, 'Kendang Cedugan Wadon'),
(82,  'I Made Yoga Pratama',            '081236300320', '2025-07-10', 'AKTIF',    3, 6, 'Reyong 3'),
(83,  'I Nyoman Bayu Permana',          '081236300321', '2025-07-12', 'AKTIF',    3, 6, 'Reyong 4'),
(84,  'I Ketut Hendra Gunawan',         '081236300322', '2025-07-12', 'AKTIF',    3, 6, 'Gong & Kempur'),
(85,  'I Putu Surya Dinata',            '081236300323', '2025-07-15', 'VAKUM',    3, NULL, 'Praktik kerja lapangan di luar daerah'),
(86,  'I Kadek Wahyu Pratama',          '081236300324', '2025-07-15', 'NONAKTIF', 3, NULL, 'Mengundurkan diri karena jadwal kerja'),

-- Kategori 4: Inti (ID 87 - 105)
(87,  'I Komang Arya Wiguna',           '081236400401', '2024-08-10', 'AKTIF',    4, 7, 'Juru Ugal utama Sekaa Gong'),
(88,  'I Gede Bagus Mahardika',         '081236400402', '2024-08-10', 'AKTIF',    4, 7, 'Juru Kendang Lanang utama'),
(89,  'I Wayan Raka Nugraha',           '081236400403', '2024-08-12', 'AKTIF',    4, 7, 'Juru Kendang Wadon utama'),
(90,  'I Made Oka Saputra',             '081236400404', '2024-08-12', 'AKTIF',    4, 7, 'Gangsa Pemade polos utama'),
(91,  'I Nyoman Triana Jaya',           '081236400405', '2024-08-15', 'AKTIF',    4, 7, 'Gangsa Pemade sangsih utama'),
(92,  'I Ketut Sujana Giri',            '081236400406', '2024-08-15', 'AKTIF',    4, 7, 'Kantilan polos utama'),
(93,  'I Putu Widiantara Sudaji',       '081236400407', '2024-08-18', 'AKTIF',    4, 7, 'Kantilan sangsih utama'),
(94,  'I Kadek Mertayasa Munduk',       '081236400408', '2024-08-18', 'AKTIF',    4, 7, 'Reyong pengenter utama'),
(95,  'I Komang Sudiarta Gobleg',       '081236400409', '2024-08-20', 'AKTIF',    4, 7, 'Reyong penyorog utama'),
(96,  'I Wayan Pastika Tamblang',       '081236400410', '2024-08-20', 'AKTIF',    4, 7, 'Reyong 3 utama'),
(97,  'I Made Sudarsana Menyali',       '081236400411', '2024-08-22', 'AKTIF',    4, 7, 'Reyong 4 utama'),
(98,  'I Nyoman Suastika Bebetin',      '081236400412', '2024-08-22', 'AKTIF',    4, 7, 'Calung utama'),
(99,  'I Ketut Subagia Sangsit',        '081236400413', '2024-08-25', 'AKTIF',    4, 7, 'Jegogan utama'),
(100, 'I Gede Wirawan Bungkulan',       '081236400414', '2024-08-25', 'AKTIF',    4, 7, 'Penyacah utama'),
(101, 'Ni Luh Gede Sariani',            '081236400415', '2024-09-01', 'AKTIF',    4, 7, 'Ceng-ceng Ricik & Kopyak'),
(102, 'Ni Made Sri Utami',              '081236400416', '2024-09-01', 'AKTIF',    4, 7, 'Kajar & Kempli utama'),
(103, 'Ni Komang Dewi Anggarini',       '081236400417', '2024-09-05', 'AKTIF',    4, 7, 'Gong Agung & Kempur'),
(104, 'Ni Ketut Ayu Lestari',           '081236400418', '2024-09-05', 'VAKUM',    4, NULL, 'Studi pertukaran pelajar'),
(105, 'Ida Bagus Putu Suryawan',        '081236400419', '2024-09-10', 'NONAKTIF', 4, NULL, 'Alumni penabuh senior');

-- ------------------------------------------------------------
-- 6. Tabel riwayat_tingkat (Arsip Kenaikan Tingkat)
-- ------------------------------------------------------------
INSERT INTO `riwayat_tingkat` (`id`, `anggota_id`, `kategori_tingkat_id_lama`, `kategori_tingkat_id_baru`, `tanggal_perubahan`, `keterangan`) VALUES
(1,  33, 1, 2, '2026-05-17', 'Lulus ujian evaluasi Tabuh Gilak & Tabuh Telu'),
(2,  34, 1, 2, '2026-05-17', 'Lulus ujian evaluasi teknik ubitan dasar'),
(3,  35, 1, 2, '2026-05-17', 'Lulus ujian evaluasi sangsih polos'),
(4,  40, 1, 2, '2026-05-17', 'Lulus ujian kendang gupekan dasar'),
(5,  63, 2, 3, '2026-05-17', 'Lulus ujian Tabuh Kreasi Kebyar Duduk'),
(6,  64, 2, 3, '2026-05-17', 'Lulus ujian kotekan cepat kantilan'),
(7,  67, 2, 3, '2026-05-17', 'Lulus ujian kendang cedugan'),
(8,  69, 2, 3, '2026-05-17', 'Lulus ujian norot & kotekan reyong'),
(9,  87, 3, 4, '2025-11-16', 'Dipromosikan ke Tim Inti Sekaa Gong'),
(10, 88, 3, 4, '2025-11-16', 'Dipromosikan sebagai Juru Kendang Utama'),
(11, 89, 3, 4, '2025-11-16', 'Dipromosikan sebagai Juru Kendang Wadon Utama'),
(12, 90, 3, 4, '2026-11-15', 'Lulus Ujian Kenaikan Tingkat Semester Ganjil 2026'),
(13, 91, 3, 4, '2026-11-15', 'Lulus Ujian Kenaikan Tingkat Semester Ganjil 2026'),
(14, 92, 3, 4, '2026-11-15', 'Lulus Ujian Kenaikan Tingkat Semester Ganjil 2026');

-- ------------------------------------------------------------
-- 7. Tabel materi_tabuh (Repertoar Gong Kebyar & Buleleng)
-- ------------------------------------------------------------
INSERT INTO `materi_tabuh` (`id`, `nama`, `deskripsi`) VALUES
(1,  'Tabuh Gilak Pengawak',        'Tabuh dasar berpola ritmis berulang untuk melatih ketepatan tempo dan pukulan gong.'),
(2,  'Tabuh Telu Gesing',           'Tabuh lelambatan tiga pukulan gong khas Bali Utara untuk pengiring upacara.'),
(3,  'Tabuh Kreasi Taruna Jaya',    'Karya monumental I Gede Manik asal Jagaraga Buleleng dengan dinamika angsel tajam.'),
(4,  'Tabuh Kreasi Wiranjaya',      'Karya I Gede Manik (Buleleng) yang menonjolkan keperkasaan gerak dan pukulan kendang.'),
(5,  'Tabuh Kreasi Palawakya',      'Gending kreasi I Gede Manik yang memadukan permainan instrumen trompong dan vokal.'),
(6,  'Tabuh Kreasi Nelayan',        'Karya maestro I Ketut Merdana dari Desa Kedis, Busungbiu, Buleleng bertema kehidupan pesisir.'),
(7,  'Tabuh Kebyar Buleleng (Legong)', 'Gaya klasik Kebyar Denbukit dengan karakter keras, lincah, dan penuh kejutan dinamika.'),
(8,  'Tabuh Kreasi Jaya Semara',    'Gending kebyar penuh nuansa liris dan megah karya I Wayan Beratha.'),
(9,  'Tabuh Kreasi Sekar Jepun',    'Gending tabuh kreasi maskot Kabupaten Badung dengan harmonisasi kotekan rapat.'),
(10, 'Tabuh Kreasi Kebyar Duduk',   'Karya I Ketut Mario dengan tempo berubah-ubah mengikuti ekspresi penari.'),
(11, 'Tabuh Kreasi Margapati',      'Gending pengiring tari kepahlawanan dengan pola kendang gupekan bertenaga.'),
(12, 'Tabuh Kreasi Panji Semirang', 'Gending pengiring tari karya I Nyoman Kaler dengan transisi tempo halus ke cepat.'),
(13, 'Tabuh Kreasi Manuk Rawa',     'Gending kreasi karya I Wayan Dibia & I Wayan Beratha dengan aksen ceng-ceng lincah.'),
(14, 'Tabuh Kreasi Gabor',          'Gending tari penyambutan tamu dengan melodi ceria dan pola ubitan standar.'),
(15, 'Tabuh Kreasi Puspanjali',     'Gending tari penyambutan karya I Nyoman Windha dengan nuansa sakral dan anggun.'),
(16, 'Tabuh Kreasi Cendrawasih',    'Gending pengiring tari berpasangan dengan melodi lembut dan aksen cepat.'),
(17, 'Tabuh Kreasi Teruna Jaya Jagaraga', 'Versi lengkap gaya Sekaa Gong Jagaraga Buleleng dengan variasi pengecet panjang.'),
(18, 'Tabuh Lelambatan Pegongan',   'Tabuh klasik bertempo lambat untuk mengiringi prosesi persembahyangan di pura.'),
(19, 'Tabuh Kreasi Kosalya Arini',  'Gending kebyar kontemporer karya I Wayan Suweca dengan pola ritmis sinkopasi.'),
(20, 'Tabuh Kreasi Bima Kroda',     'Gending berkarakter gagah dan heroik untuk pementasan pembuka festival.');

-- ------------------------------------------------------------
-- 8. Tabel event (Pertunjukan, Ngayah, & Ujian)
-- ------------------------------------------------------------
INSERT INTO `event` (`id`, `nama_acara`, `jenis_acara`, `tanggal`, `waktu`, `lokasi`, `catatan`) VALUES
(1, 'Ngayah Odalan Pura Agung Jagatnatha',        'NGAYAH', '2026-10-18', '18:00:00', 'Pura Agung Jagatnatha, Singaraja',          'Mengiringi upacara piodalan & tari wali'),
(2, 'Pentas Pembukaan Lovina Festival 2026',      'JOB',    '2026-11-08', '19:30:00', 'Panggung Utama Pantai Binaria, Lovina',     'Durasi pentas 45 menit, honor per pemain'),
(3, 'Ujian Kenaikan Tingkat Semester Ganjil',     'UJIAN',  '2026-11-15', '09:00:00', 'Gedung Kesenian Gde Manik, Singaraja',      'Evaluasi kenaikan tingkat Dasar-Madya-Purna-Inti'),
(4, 'Ngayah Piodalan Pura Dalem Purwa',           'NGAYAH', '2026-12-06', '17:30:00', 'Pura Dalem Purwa, Kubutambahan, Buleleng',  'Membawakan Tabuh Telu Gesing & Tabuh Gabor'),
(5, 'Gelar Budaya Wisatawan Mancanegara',         'JOB',    '2026-12-14', '20:00:00', 'Amfiteater Puri Saron Baruna, Lovina',      'Pentas Taruna Jaya, Nelayan, & Kebyar Duduk'),
(6, 'Parade Tabuh Kebyar Denbukit Akhir Tahun',   'UJIAN',  '2026-12-21', '18:30:00', 'Wantilan Sasana Budaya, Singaraja',         'Unjuk karya seluruh subkelompok sanggar');

-- ------------------------------------------------------------
-- 9. Tabel tim_event
-- ------------------------------------------------------------
INSERT INTO `tim_event` (`id`, `event_id`, `nama_tim`, `catatan`) VALUES
(1, 1, 'Sekaa Gong Ngayah Utama',     'Gabungan anggota Inti dan Purna'),
(2, 2, 'Tim Kebyar Pembuka Festival', 'Membawakan Tabuh Kreasi Nelayan & Bima Kroda'),
(3, 2, 'Tim Pengiring Tari Buleleng', 'Pengiring Tari Taruna Jaya & Wiranjaya'),
(4, 3, 'Tim Evaluasi Ujian Tingkat',  'Kandidat kenaikan jenjang semester ganjil'),
(5, 4, 'Sekaa Ngayah Kubutambahan',   'Penabuh wajib hadir 1 jam sebelum upacara'),
(6, 5, 'Tim Pentas Utama Lovina',     'Formasi lengkap 17 penabuh inti');

-- ------------------------------------------------------------
-- 10. Tabel pemain_tim
-- ------------------------------------------------------------
INSERT INTO `pemain_tim` (`id`, `tim_event_id`, `anggota_id`, `status_kesediaan`, `nominal_honor`, `status_honor`) VALUES
-- Tim 1 (Ngayah 2026-10-18 - tanpa honor)
(1,  1, 87, 'BERSEDIA', NULL, NULL),
(2,  1, 88, 'BERSEDIA', NULL, NULL),
(3,  1, 89, 'BERSEDIA', NULL, NULL),
(4,  1, 90, 'BERSEDIA', NULL, NULL),
(5,  1, 91, 'BERSEDIA', NULL, NULL),
(6,  1, 92, 'BERSEDIA', NULL, NULL),
(7,  1, 93, 'BERSEDIA', NULL, NULL),
(8,  1, 94, 'BERSEDIA', NULL, NULL),

-- Tim 2 (Job Lovina 2026-11-08 - honor sudah dibagikan)
(9,  2, 87, 'BERSEDIA', 250000.00, 'SUDAH_DIBAGIKAN'),
(10, 2, 88, 'BERSEDIA', 250000.00, 'SUDAH_DIBAGIKAN'),
(11, 2, 89, 'BERSEDIA', 250000.00, 'SUDAH_DIBAGIKAN'),
(12, 2, 90, 'BERSEDIA', 250000.00, 'SUDAH_DIBAGIKAN'),
(13, 2, 91, 'BERSEDIA', 250000.00, 'SUDAH_DIBAGIKAN'),
(14, 2, 94, 'BERSEDIA', 250000.00, 'SUDAH_DIBAGIKAN'),
(15, 2, 95, 'BERSEDIA', 250000.00, 'SUDAH_DIBAGIKAN'),
(16, 2, 98, 'BERSEDIA', 250000.00, 'SUDAH_DIBAGIKAN'),
(17, 2, 99, 'BERSEDIA', 250000.00, 'SUDAH_DIBAGIKAN'),

-- Tim 3 (Job Lovina 2026-11-08 - honor belum dibagikan)
(18, 3, 63, 'BERSEDIA', 200000.00, 'BELUM_DIBAGIKAN'),
(19, 3, 64, 'BERSEDIA', 200000.00, 'BELUM_DIBAGIKAN'),
(20, 3, 65, 'BERSEDIA', 200000.00, 'BELUM_DIBAGIKAN'),
(21, 3, 66, 'BERSEDIA', 200000.00, 'BELUM_DIBAGIKAN'),
(22, 3, 67, 'BERSEDIA', 200000.00, 'BELUM_DIBAGIKAN'),
(23, 3, 69, 'BERSEDIA', 200000.00, 'BELUM_DIBAGIKAN'),

-- Tim 4 (Ujian 2026-11-15)
(24, 4, 1,  'BERSEDIA', NULL, NULL),
(25, 4, 2,  'BERSEDIA', NULL, NULL),
(26, 4, 33, 'BERSEDIA', NULL, NULL),
(27, 4, 34, 'BERSEDIA', NULL, NULL),
(28, 4, 90, 'BERSEDIA', NULL, NULL),
(29, 4, 91, 'BERSEDIA', NULL, NULL),

-- Tim 5 (Ngayah mendatang 2026-12-06)
(30, 5, 87, 'BERSEDIA',       NULL, NULL),
(31, 5, 88, 'BERSEDIA',       NULL, NULL),
(32, 5, 89, 'BELUM_DITANYA',  NULL, NULL),
(33, 5, 92, 'BERSEDIA',       NULL, NULL),
(34, 5, 93, 'TIDAK_BERSEDIA', NULL, NULL),
(35, 5, 101,'BELUM_DITANYA',  NULL, NULL),

-- Tim 6 (Job mendatang 2026-12-14)
(36, 6, 87, 'BERSEDIA',      300000.00, 'BELUM_DIBAGIKAN'),
(37, 6, 88, 'BERSEDIA',      300000.00, 'BELUM_DIBAGIKAN'),
(38, 6, 89, 'BERSEDIA',      300000.00, 'BELUM_DIBAGIKAN'),
(39, 6, 90, 'BELUM_DITANYA', 300000.00, 'BELUM_DIBAGIKAN'),
(40, 6, 91, 'BELUM_DITANYA', 300000.00, 'BELUM_DIBAGIKAN'),
(41, 6, 94, 'BERSEDIA',      300000.00, 'BELUM_DIBAGIKAN');

-- ------------------------------------------------------------
-- 11. Tabel jadwal_latihan (5 Lampau + 5 Mendatang)
-- ------------------------------------------------------------
INSERT INTO `jadwal_latihan` (`id`, `jenis_jadwal`, `tanggal`, `jam_mulai`, `jam_selesai`, `lokasi`, `kategori_tingkat_id`, `subkelompok_id`, `event_id`, `catatan`) VALUES
-- Jadwal Lampau (November 2026)
(1,  'RUTIN',        '2026-11-03', '16:00:00', '18:00:00', 'Wantilan Sanggar Tripitaka', 1, 1,    NULL, 'Latihan pukulan dasar & tempo Tabuh Gilak'),
(2,  'RUTIN',        '2026-11-05', '17:00:00', '19:30:00', 'Wantilan Sanggar Tripitaka', 2, 3,    NULL, 'Pendalaman kotekan Tabuh Kreasi Gabor'),
(3,  'KHUSUS_EVENT', '2026-11-06', '19:00:00', '21:30:00', 'Wantilan Sanggar Tripitaka', 4, 7,    2,    'Gladi bersih Lovina Festival 2026'),
(4,  'RUTIN',        '2026-11-12', '18:00:00', '20:30:00', 'Wantilan Sanggar Tripitaka', 3, 5,    NULL, 'Latihan dinamika angsel Tabuh Kebyar Duduk'),
(5,  'RUTIN',        '2026-11-21', '18:30:00', '21:00:00', 'Wantilan Sanggar Tripitaka', 4, 7,    NULL, 'Pemantapan repertoar Tabuh Kreasi Nelayan'),

-- Jadwal Mendatang (Akhir Nov - Des 2026 -> Muncul di Dashboard)
(6,  'RUTIN',        '2026-11-30', '16:30:00', '18:30:00', 'Wantilan Sanggar Tripitaka', 1, 2,    NULL, 'Latihan rutin Dasar B - pengenalan ubitan'),
(7,  'RUTIN',        '2026-12-02', '17:30:00', '19:30:00', 'Wantilan Sanggar Tripitaka', 2, 4,    NULL, 'Latihan rutin Madya B - Tabuh Puspanjali'),
(8,  'KHUSUS_EVENT', '2026-12-04', '18:30:00', '21:00:00', 'Wantilan Sanggar Tripitaka', 4, 7,    4,    'Persiapan Ngayah Pura Dalem Purwa Kubutambahan'),
(9,  'RUTIN',        '2026-12-08', '18:00:00', '20:30:00', 'Wantilan Sanggar Tripitaka', 3, 6,    NULL, 'Latihan rutin Purna B - Tabuh Jaya Semara'),
(10, 'KHUSUS_EVENT', '2026-12-12', '19:00:00', '21:30:00', 'Wantilan Sanggar Tripitaka', 4, 7,    5,    'Latihan gabungan pentas wisata Lovina');

-- ------------------------------------------------------------
-- 12. Tabel sesi_materi
-- ------------------------------------------------------------
INSERT INTO `sesi_materi` (`id`, `jadwal_latihan_id`, `materi_tabuh_id`, `catatan_progres`) VALUES
(1,  1, 1,  'Pukulan pokok dan ketepatan gong sudah stabil'),
(2,  1, 2,  'Pengenalan pola tabuh telu bagian pengawak'),
(3,  2, 14, 'Bagian pengawak & pengecet sudah lancar 85%'),
(4,  3, 6,  'Kompak dari kawitan hingga pekaad, siap pentas'),
(5,  3, 3,  'Perlu penegasan angsel ketiga sebelum pengecet'),
(6,  4, 10, 'Penyesuaian tempo kendang lanang-wadon sudah padu'),
(7,  5, 6,  'Dinamika keras-lembut khas Buleleng sudah matang'),
(8,  6, 1,  'Target penguasaan pola dasar gangsa & calung'),
(9,  7, 15, 'Target penguasaan bagian pengawak'),
(10, 8, 2,  'Penyelarasan gending lelambatan upacara'),
(11, 9, 8,  'Latihan ubitan cepat bagian pengecet'),
(12, 10, 4, 'Simulasi urutan repertoar pentas 45 menit');

-- ------------------------------------------------------------
-- 13. Tabel presensi (Untuk 5 Jadwal Latihan Lampau)
-- ------------------------------------------------------------
INSERT INTO `presensi` (`id`, `jadwal_latihan_id`, `anggota_id`, `status_hadir`, `keterangan`) VALUES
-- Jadwal 1: Dasar A (Anggota ID 1 - 16)
(1,  1, 1,  'HADIR', NULL),
(2,  1, 2,  'HADIR', NULL),
(3,  1, 3,  'HADIR', NULL),
(4,  1, 4,  'HADIR', NULL),
(5,  1, 5,  'HADIR', NULL),
(6,  1, 6,  'IZIN',  'Izin acara upacara keluarga di desa'),
(7,  1, 7,  'HADIR', NULL),
(8,  1, 8,  'HADIR', NULL),
(9,  1, 9,  'HADIR', NULL),
(10, 1, 10, 'HADIR', NULL),
(11, 1, 11, 'HADIR', NULL),
(12, 1, 12, 'SAKIT', 'Demam'),
(13, 1, 13, 'HADIR', NULL),
(14, 1, 14, 'HADIR', NULL),
(15, 1, 15, 'HADIR', NULL),
(16, 1, 16, 'HADIR', NULL),

-- Jadwal 2: Madya A (Anggota ID 33 - 46)
(17, 2, 33, 'HADIR', NULL),
(18, 2, 34, 'HADIR', NULL),
(19, 2, 35, 'HADIR', NULL),
(20, 2, 36, 'HADIR', NULL),
(21, 2, 37, 'HADIR', NULL),
(22, 2, 38, 'HADIR', NULL),
(23, 2, 39, 'TANPA_KETERANGAN', NULL),
(24, 2, 40, 'HADIR', NULL),
(25, 2, 41, 'HADIR', NULL),
(26, 2, 42, 'HADIR', NULL),
(27, 2, 43, 'HADIR', NULL),
(28, 2, 44, 'IZIN',  'Ujian praktik kampus'),
(29, 2, 45, 'HADIR', NULL),
(30, 2, 46, 'HADIR', NULL),

-- Jadwal 3: Gladi Bersih Inti Sekaa Gong (Anggota ID 87 - 103)
(31, 3, 87,  'HADIR', NULL),
(32, 3, 88,  'HADIR', NULL),
(33, 3, 89,  'HADIR', NULL),
(34, 3, 90,  'HADIR', NULL),
(35, 3, 91,  'HADIR', NULL),
(36, 3, 92,  'HADIR', NULL),
(37, 3, 93,  'HADIR', NULL),
(38, 3, 94,  'HADIR', NULL),
(39, 3, 95,  'HADIR', NULL),
(40, 3, 96,  'HADIR', NULL),
(41, 3, 97,  'HADIR', NULL),
(42, 3, 98,  'HADIR', NULL),
(43, 3, 99,  'HADIR', NULL),
(44, 3, 100, 'HADIR', NULL),
(45, 3, 101, 'HADIR', NULL),
(46, 3, 102, 'HADIR', NULL),
(47, 3, 103, 'HADIR', NULL),

-- Jadwal 4: Purna A (Anggota ID 63 - 73)
(48, 4, 63, 'HADIR', NULL),
(49, 4, 64, 'HADIR', NULL),
(50, 4, 65, 'HADIR', NULL),
(51, 4, 66, 'SAKIT', 'Flu berat'),
(52, 4, 67, 'HADIR', NULL),
(53, 4, 68, 'HADIR', NULL),
(54, 4, 69, 'HADIR', NULL),
(55, 4, 70, 'HADIR', NULL),
(56, 4, 71, 'HADIR', NULL),
(57, 4, 72, 'HADIR', NULL),
(58, 4, 73, 'HADIR', NULL),

-- Jadwal 5: Rutin Inti Sekaa Gong (Anggota ID 87 - 103)
(59, 5, 87,  'HADIR', NULL),
(60, 5, 88,  'HADIR', NULL),
(61, 5, 89,  'HADIR', NULL),
(62, 5, 90,  'HADIR', NULL),
(63, 5, 91,  'HADIR', NULL),
(64, 5, 92,  'IZIN',  'Menghadiri upacara mepandes keluarga'),
(65, 5, 93,  'HADIR', NULL),
(66, 5, 94,  'HADIR', NULL),
(67, 5, 95,  'HADIR', NULL),
(68, 5, 96,  'HADIR', NULL),
(69, 5, 97,  'HADIR', NULL),
(70, 5, 98,  'HADIR', NULL),
(71, 5, 99,  'HADIR', NULL),
(72, 5, 100, 'TANPA_KETERANGAN', NULL),
(73, 5, 101, 'HADIR', NULL),
(74, 5, 102, 'HADIR', NULL),
(75, 5, 103, 'HADIR', NULL);

-- ------------------------------------------------------------
-- 14. Tabel iuran (Nominal 100.000 - November & Oktober 2026)
-- ------------------------------------------------------------
INSERT INTO `iuran` (`anggota_id`, `bulan`, `tahun`, `nominal`, `tanggal_bayar`, `status_bayar`) VALUES
-- Bulan November 2026 (Seluruh 96 Anggota Aktif)
(1,  11, 2026, 100000.00, '2026-11-03', 'LUNAS'),
(2,  11, 2026, 100000.00, '2026-11-03', 'LUNAS'),
(3,  11, 2026, 100000.00, '2026-11-05', 'LUNAS'),
(4,  11, 2026, 100000.00, '2026-11-05', 'LUNAS'),
(5,  11, 2026, 100000.00, '2026-11-07', 'LUNAS'),
(6,  11, 2026, 100000.00, '2026-11-08', 'LUNAS'),
(7,  11, 2026, 100000.00, '2026-11-10', 'LUNAS'),
(8,  11, 2026, 100000.00, '2026-11-10', 'LUNAS'),
(9,  11, 2026, 100000.00, '2026-11-12', 'LUNAS'),
(10, 11, 2026, 100000.00, '2026-11-12', 'LUNAS'),
(11, 11, 2026, 100000.00, '2026-11-14', 'LUNAS'),
(12, 11, 2026, 100000.00, '2026-11-14', 'LUNAS'),
(13, 11, 2026, 100000.00, '2026-11-15', 'LUNAS'),
(14, 11, 2026, 100000.00, '2026-11-18', 'LUNAS'),
(15, 11, 2026, 100000.00, '2026-11-20', 'LUNAS'),
(16, 11, 2026, 100000.00, NULL,         'BELUM_BAYAR'),
(17, 11, 2026, 100000.00, '2026-11-04', 'LUNAS'),
(18, 11, 2026, 100000.00, '2026-11-04', 'LUNAS'),
(19, 11, 2026, 100000.00, '2026-11-06', 'LUNAS'),
(20, 11, 2026, 100000.00, '2026-11-09', 'LUNAS'),
(21, 11, 2026, 100000.00, '2026-11-11', 'LUNAS'),
(22, 11, 2026, 100000.00, '2026-11-13', 'LUNAS'),
(23, 11, 2026, 100000.00, NULL,         'BELUM_BAYAR'),
(24, 11, 2026, 100000.00, NULL,         'BELUM_BAYAR'),
(25, 11, 2026, 100000.00, '2026-11-16', 'LUNAS'),
(26, 11, 2026, 100000.00, '2026-11-19', 'LUNAS'),
(27, 11, 2026, 100000.00, NULL,         'BELUM_BAYAR'),
(28, 11, 2026, 100000.00, NULL,         'BELUM_BAYAR'),
(29, 11, 2026, 100000.00, '2026-11-22', 'LUNAS'),
(30, 11, 2026, 100000.00, NULL,         'BELUM_BAYAR'),
(33, 11, 2026, 100000.00, '2026-11-02', 'LUNAS'),
(34, 11, 2026, 100000.00, '2026-11-02', 'LUNAS'),
(35, 11, 2026, 100000.00, '2026-11-05', 'LUNAS'),
(36, 11, 2026, 100000.00, '2026-11-05', 'LUNAS'),
(37, 11, 2026, 100000.00, '2026-11-08', 'LUNAS'),
(38, 11, 2026, 100000.00, '2026-11-10', 'LUNAS'),
(39, 11, 2026, 100000.00, NULL,         'BELUM_BAYAR'),
(40, 11, 2026, 100000.00, '2026-11-12', 'LUNAS'),
(41, 11, 2026, 100000.00, '2026-11-14', 'LUNAS'),
(42, 11, 2026, 100000.00, '2026-11-15', 'LUNAS'),
(43, 11, 2026, 100000.00, NULL,         'BELUM_BAYAR'),
(44, 11, 2026, 100000.00, NULL,         'BELUM_BAYAR'),
(45, 11, 2026, 100000.00, '2026-11-21', 'LUNAS'),
(46, 11, 2026, 100000.00, '2026-11-24', 'LUNAS'),
(47, 11, 2026, 100000.00, '2026-11-03', 'LUNAS'),
(48, 11, 2026, 100000.00, '2026-11-06', 'LUNAS'),
(49, 11, 2026, 100000.00, NULL,         'BELUM_BAYAR'),
(50, 11, 2026, 100000.00, NULL,         'BELUM_BAYAR'),
(51, 11, 2026, 100000.00, '2026-11-11', 'LUNAS'),
(52, 11, 2026, 100000.00, '2026-11-13', 'LUNAS'),
(53, 11, 2026, 100000.00, NULL,         'BELUM_BAYAR'),
(54, 11, 2026, 100000.00, '2026-11-17', 'LUNAS'),
(55, 11, 2026, 100000.00, NULL,         'BELUM_BAYAR'),
(56, 11, 2026, 100000.00, '2026-11-23', 'LUNAS'),
(57, 11, 2026, 100000.00, NULL,         'BELUM_BAYAR'),
(58, 11, 2026, 100000.00, NULL,         'BELUM_BAYAR'),
(59, 11, 2026, 100000.00, '2026-11-26', 'LUNAS'),
(63, 11, 2026, 100000.00, '2026-11-02', 'LUNAS'),
(64, 11, 2026, 100000.00, '2026-11-02', 'LUNAS'),
(65, 11, 2026, 100000.00, '2026-11-04', 'LUNAS'),
(66, 11, 2026, 100000.00, '2026-11-07', 'LUNAS'),
(67, 11, 2026, 100000.00, '2026-11-09', 'LUNAS'),
(68, 11, 2026, 100000.00, NULL,         'BELUM_BAYAR'),
(69, 11, 2026, 100000.00, '2026-11-12', 'LUNAS'),
(70, 11, 2026, 100000.00, '2026-11-14', 'LUNAS'),
(71, 11, 2026, 100000.00, NULL,         'BELUM_BAYAR'),
(72, 11, 2026, 100000.00, '2026-11-18', 'LUNAS'),
(73, 11, 2026, 100000.00, NULL,         'BELUM_BAYAR'),
(74, 11, 2026, 100000.00, '2026-11-05', 'LUNAS'),
(75, 11, 2026, 100000.00, '2026-11-08', 'LUNAS'),
(76, 11, 2026, 100000.00, NULL,         'BELUM_BAYAR'),
(77, 11, 2026, 100000.00, '2026-11-11', 'LUNAS'),
(78, 11, 2026, 100000.00, NULL,         'BELUM_BAYAR'),
(79, 11, 2026, 100000.00, '2026-11-15', 'LUNAS'),
(80, 11, 2026, 100000.00, NULL,         'BELUM_BAYAR'),
(81, 11, 2026, 100000.00, '2026-11-19', 'LUNAS'),
(82, 11, 2026, 100000.00, NULL,         'BELUM_BAYAR'),
(83, 11, 2026, 100000.00, NULL,         'BELUM_BAYAR'),
(84, 11, 2026, 100000.00, '2026-11-25', 'LUNAS'),
(87, 11, 2026, 100000.00, '2026-11-01', 'LUNAS'),
(88, 11, 2026, 100000.00, '2026-11-01', 'LUNAS'),
(89, 11, 2026, 100000.00, '2026-11-02', 'LUNAS'),
(90, 11, 2026, 100000.00, '2026-11-03', 'LUNAS'),
(91, 11, 2026, 100000.00, '2026-11-04', 'LUNAS'),
(92, 11, 2026, 100000.00, '2026-11-05', 'LUNAS'),
(93, 11, 2026, 100000.00, '2026-11-06', 'LUNAS'),
(94, 11, 2026, 100000.00, '2026-11-07', 'LUNAS'),
(95, 11, 2026, 100000.00, '2026-11-08', 'LUNAS'),
(96, 11, 2026, 100000.00, '2026-11-09', 'LUNAS'),
(97, 11, 2026, 100000.00, NULL,         'BELUM_BAYAR'),
(98, 11, 2026, 100000.00, '2026-11-12', 'LUNAS'),
(99, 11, 2026, 100000.00, NULL,         'BELUM_BAYAR'),
(100,11, 2026, 100000.00, '2026-11-14', 'LUNAS'),
(101,11, 2026, 100000.00, '2026-11-15', 'LUNAS'),
(102,11, 2026, 100000.00, NULL,         'BELUM_BAYAR'),
(103,11, 2026, 100000.00, '2026-11-20', 'LUNAS'),

-- Bulan Oktober 2026 (Sampel Riwayat Bulan Sebelumnya)
(87, 10, 2026, 100000.00, '2026-10-03', 'LUNAS'),
(88, 10, 2026, 100000.00, '2026-10-04', 'LUNAS'),
(89, 10, 2026, 100000.00, '2026-10-05', 'LUNAS'),
(90, 10, 2026, 100000.00, '2026-10-08', 'LUNAS'),
(91, 10, 2026, 100000.00, '2026-10-10', 'LUNAS'),
(92, 10, 2026, 100000.00, '2026-10-12', 'LUNAS'),
(93, 10, 2026, 100000.00, '2026-10-14', 'LUNAS'),
(94, 10, 2026, 100000.00, '2026-10-18', 'LUNAS'),
(95, 10, 2026, 100000.00, '2026-10-21', 'LUNAS'),
(96, 10, 2026, 100000.00, NULL,         'BELUM_BAYAR'),
(97, 10, 2026, 100000.00, NULL,         'BELUM_BAYAR'),
(98, 10, 2026, 100000.00, '2026-10-11', 'LUNAS'),
(99, 10, 2026, 100000.00, '2026-10-13', 'LUNAS'),
(100,10, 2026, 100000.00, '2026-10-15', 'LUNAS'),
(101,10, 2026, 100000.00, '2026-10-19', 'LUNAS'),
(102,10, 2026, 100000.00, '2026-10-22', 'LUNAS'),
(103,10, 2026, 100000.00, '2026-10-24', 'LUNAS');