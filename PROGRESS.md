# Progress Pengerjaan Aplikasi Pakem

Dokumen ini mencatat riwayat pengerjaan bertahap, class penting yang telah dibuat, status pengujian, dan rencana modul berikutnya.

---

## 📌 Status Terakhir: Fase 0 Selesai (Fondasi Proyek)
- **Tanggal:** 22 November 2026
- **Status:** ✅ Selesai & Terverifikasi
- **Target Platform:** Java 25, Apache Maven, FlatLaf, MySQL JDBC

---

## 📁 Ringkasan Pengerjaan Fase 0

### 1. Salinan Dokumen Perancangan
- File: `rancangan_pakem.md` telah disalin ke root repositori agar berdampingan dengan `prd_pakem_v1_0.md` sebagai acuan teknis resmi.

### 2. Struktur Proyek & Build Configuration
- `pom.xml`:
  - Target Java 25 (`<maven.compiler.release>25</maven.compiler.release>`).
  - Dependency UI: **FlatLaf** (`com.formdev:flatlaf:3.5.4`).
  - Dependency JDBC: **MySQL Connector/J** (`com.mysql:mysql-connector-j:8.3.0`).
  - Plugin: `maven-compiler-plugin` (3.13.0) & `exec-maven-plugin` (3.5.0).
- `.gitignore`: Mengabaikan build artifacts Maven (`target/`), binary bytecode (`*.class`), log, direktori IDE (VS Code, IntelliJ IDEA, Apache NetBeans, Eclipse), dan file sensitif lingkungan lokal.

### 3. Skema Basis Data MySQL (`schema.sql`)
- Skema DDL lengkap untuk 14 tabel:
  1. `users` (autentikasi & hak akses PENGURUS / PELATIH)
  2. `settings` (konfigurasi sanggar key-value)
  3. `kategori_tingkat` (master tingkat kemampuan fleksibel)
  4. `subkelompok` (pembagian kelompok di dalam kategori tingkat)
  5. `anggota` (data anggota sanggar dengan 3 status: AKTIF, VAKUM, NONAKTIF)
  6. `riwayat_tingkat` (log mutasi tingkat anggota)
  7. `materi_tabuh` (master repertoar gending tabuh)
  8. `event` (jadwal acara: JOB, NGAYAH, UJIAN)
  9. `tim_event` (pembagian tim dalam acara)
  10. `pemain_tim` (anggota terpilih, kesediaan, dan honor job)
  11. `jadwal_latihan` (latihan RUTIN maupun KHUSUS_EVENT)
  12. `sesi_materi` (materi tabuh dan catatan progres per pertemuan)
  13. `presensi` (kehadiran latihan: HADIR, IZIN, SAKIT, TANPA_KETERANGAN)
  14. `iuran` (pencatatan iuran bulanan anggota aktif)
- **Seed Data Awal:**
  - Akun Pengurus: `pengurus` / `pengurus123` (password ter-hash SHA-256)
  - Akun Pelatih: `pelatih` / `pelatih123` (password ter-hash SHA-256)
  - Pengaturan default: `nama_sanggar`, `iuran_default` (50000), `batas_kuning` (15), `batas_merah` (25), `kapasitas_kelompok` (25).
  - Kategori tingkat bawaan: Dasar, Madya, Purna, Inti.
  - Subkelompok contoh: Madya A, Madya B.

### 4. Konfigurasi Database & Entry Point
- `src/main/resources/config.properties`: Konfigurasi host, port, database, user, password, dan timezone.
- `src/main/java/com/pakem/config/DatabaseConnection.java`:
  - Singleton koneksi JDBC dengan driver MySQL (`com.mysql.cj.jdbc.Driver`).
  - Helper method `getConnection()`, `testConnection()`, `closeConnection()`.
- `src/main/java/com/pakem/Main.java`:
  - Entry point aplikasi.
  - Inisialisasi tema FlatLaf (`FlatLightLaf`).
  - Pengujian konektivitas awal database.

---

## 📋 Panduan untuk Pengembang Sebelum Melanjutkan
Sebelum memulai **Fase 1 (Autentikasi & Login)**, lakukan langkah berikut:
1. **Jalankan `schema.sql` di MySQL / DBeaver:**
   ```bash
   mysql -u root -p < schema.sql
   ```
   Atau buka file `schema.sql` di DBeaver / MySQL Workbench dan jalankan seluruh script untuk membuat database `pakem_db` dan tabel-tabelnya.
2. **Sesuaikan kredensial database (jika MySQL Anda menggunakan password):**
   - Buka `src/main/resources/config.properties`.
   - Ubah `db.password` sesuai password root MySQL Anda.
3. **Inisialisasi Git Repositori:**
   ```bash
   git init
   git add .
   git commit -m "feat: inisialisasi fondasi proyek Fase 0 (skema db, pom.xml, config)"
   ```

---

## ⏭️ Modul Berikutnya: Fase 1 (Autentikasi & Hak Akses)
1. Pembuatan POJO Model `User` dan hierarki peran PBO (`Pengurus` & `Pelatih` mewarisi `User`).
2. Pembuatan interface `IUserDao` dan implementasi `UserDaoImpl` (JDBC).
3. Pembuatan `IAuthService` dan `AuthServiceImpl` (validasi login dan verifikasi hash password SHA-256).
4. Pembuatan UI `LoginPanel` dan `MainFrame` (skeleton jendela utama dengan menu dinamis berbasis hak akses).
