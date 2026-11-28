# Pakem — Sistem Administrasi Sanggar Seni Tabuh

**Pakem** adalah aplikasi desktop berbasis Java yang dirancang untuk mengelola administrasi operasional, keanggotaan, latihan, pertunjukan (event), serta pencatatan iuran bulanan pada sanggar seni tabuh tradisional Bali.

## Fitur Utama

- **Autentikasi & Kontrol Akses Berbasis Peran (RBAC)**:
  - **Pengurus**: Akses penuh ke seluruh modul administrasi anggota, penjadwalan, pertunjukan, iuran bulanan, dan pengaturan konfigurasi sanggar.
  - **Pelatih**: Akses terfokus pada operasional latihan, materi repertoar tabuh, pencatatan presensi, dan konfirmasi kesediaan pemain event (modul keuangan dan pengaturan disembunyikan secara otomatis).
- **Manajemen Anggota & Riwayat Tingkat**: Pengelolaan data anggota dengan status aktif (`AKTIF`, `VAKUM`, `NONAKTIF`) serta pencatatan riwayat kenaikan tingkat kemampuan secara otomatis.
- **Kategori Tingkat & Auto-Grouping**: Pembagian anggota aktif ke dalam subkelompok latihan secara merata menggunakan algoritma *round-robin*, dilengkapi fitur perpindahan anggota antar-subkelompok.
- **Jadwal Latihan, Materi Tabuh & Presensi**: Pencatatan sesi latihan rutin maupun khusus event, pelacakan progres materi tabuh Buleleng antar-sesi, pengisian presensi massal (*batch*), serta rekapitulasi persentase kehadiran.
- **Manajemen Pertunjukan (Event)**: Pengelolaan agenda pentas (*Job*, *Ngayah*, *Ujian*), pembagian multi-tim, penugasan pemain lintas tingkat, konfirmasi kesediaan, hingga pencatatan honorarium.
- **Iuran Bulanan & Pengaturan Sanggar**: Pelacakan status pembayaran iuran bulanan dengan indikator visual status pembayaran serta konfigurasi parameter sanggar secara dinamis.

## Arsitektur & Teknologi

Aplikasi dibangun menggunakan pola **Layered Architecture** (`Config` → `Model` → `DAO` → `Service` → `UI`) dengan penerapan prinsip Pemrograman Berorientasi Objek (Encapsulation, Inheritance, Polymorphism, dan Abstraction).

- **Bahasa Pemrograman**: Java 25
- **Build Tool**: Apache Maven
- **Antarmuka Grafis (GUI)**: Java Swing dengan tema modern **FlatLaf 3.5.4**
- **Basis Data**: MySQL 8.0+ (InnoDB, `utf8mb4`)
- **Konektivitas Data**: JDBC (`MySQL Connector/J 8.3.0`) dengan `PreparedStatement`

## Struktur Direktori

```text
pakem/
├── src/
│   ├── main/
│   │   ├── java/com/pakem/
│   │   │   ├── config/          # Manajemen koneksi tunggal JDBC (DatabaseConnection)
│   │   │   ├── dao/             # Antarmuka & implementasi akses data SQL
│   │   │   ├── model/           # Entitas domain, DTO, enum, dan hierarki User
│   │   │   ├── service/         # Antarmuka & implementasi logika bisnis
│   │   │   ├── ui/              # Komponen antarmuka Swing (Panel & Dialog)
│   │   │   └── Main.java        # Titik masuk utama aplikasi
│   │   └── resources/
│   │       └── config.properties
│   └── test/
│       └── java/com/pakem/      # 8 rangkaian pengujian otomatis (53 skenario uji)
├── schema.sql                   # Skema DDL basis data pakem_db
└── pom.xml                      # Konfigurasi proyek dan dependensi Maven
```

## Cara Menjalankan Aplikasi

1. **Siapkan Basis Data**:
   - Pastikan layanan MySQL Server 8.0+ sudah berjalan.
   - Buat database baru bernama `pakem_db` dan impor struktur tabel dari file `schema.sql`.
2. **Sesuaikan Konfigurasi Koneksi**:
   - Periksa kredensial koneksi database di `src/main/resources/config.properties` (default: host `localhost`, port `3306`, database `pakem_db`, user `root`).
3. **Kompilasi dan Jalankan via Maven**:
   ```bash
   mvn clean compile exec:java -Dexec.mainClass="com.pakem.Main"
   ```

## Akun Default Pengujian

| Peran | Username | Password |
| :--- | :--- | :--- |
| **Pengurus** | `pengurus` | `pengurus123` |
| **Pelatih** | `pelatih` | `pelatih123` |
