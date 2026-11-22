# Progress Pengerjaan Aplikasi Pakem

Dokumen ini mencatat riwayat pengerjaan bertahap, class penting yang telah dibuat, status pengujian, dan rencana modul berikutnya.

---

## 📌 Status Terakhir: Fase 1 Selesai (Autentikasi & Hak Akses Polimorfik)
- **Tanggal:** 22 November 2026
- **Status:** ✅ Selesai, Terintegrasi dengan Database MySQL, & Teruji 100%
- **Target Platform:** Java 25, Apache Maven, FlatLaf, MySQL JDBC

---

## 📁 Riwayat Pengerjaan Per Fase

### 🔹 Fase 0 — Fondasi Proyek (Selesai ✅)
1. **Dokumentasi Perancangan:**
   - Menyalin `rancangan_pakem.md` ke direktori utama berdampingan dengan `prd_pakem_v1_0.md`.
2. **Struktur Maven & Git:**
   - Membuat `pom.xml` untuk Java 25 dengan FlatLaf 3.5.4 dan MySQL Connector/J 8.3.0.
   - Membuat `.gitignore` standar Java/Maven/IDE.
3. **Skema Basis Data:**
   - Membuat `schema.sql` (14 tabel DDL, relasi foreign key, on update/delete rules, dan seed data default).
4. **Konfigurasi Koneksi:**
   - `src/main/resources/config.properties`: Kredensial MySQL `localhost:3306/pakem_db`.
   - `com.pakem.config.DatabaseConnection`: Kelas singleton JDBC dan pengujian koneksi.

---

### 🔹 Fase 1 — Autentikasi & Hak Akses (Selesai ✅)

#### 1. Penerapan PBO (Encapsulation, Inheritance, & Polymorphism)
- **`com.pakem.model.enums.RoleUser`**: Enum peran pengguna (`PENGURUS`, `PELATIH`).
- **`com.pakem.model.User`**: Abstract class dasar dengan prinsip enkapsulasi (semua field `private` dengan getter/setter), factory method `User.create(...)`, serta kontrak hak akses polimorfik:
  - `canEditAnggota()`
  - `canUpdateTingkatAnggota()`
  - `canManageKategoriTingkat()`
  - `canManageJadwalLatihan()`
  - `canManageMateriTabuh()`
  - `canRecordPresensi()`
  - `canManageEvent()`
  - `canRecordKesediaanPemain()`
  - `canAccessHonor()`
  - `canAccessIuran()`
  - `canAccessPengaturan()`
- **`com.pakem.model.Pengurus extends User`**: Mewarisi `User`, mengembalikan `true` untuk seluruh fitur administratif dan finansial.
- **`com.pakem.model.Pelatih extends User`**: Mewarisi `User`, mengimplementasikan isolasi hak akses (membatasi akses ke data honor, iuran bulanan, konfigurasi sanggar, dan pembatasan master kategori tingkat).

#### 2. Lapisan Akses Data (DAO & Abstraction)
- **`com.pakem.dao.interfaces.IUserDao`**: Kontrak interface DAO untuk entitas User (`findByUsername`, `findById`, `findAll`, `save`, `updatePassword`, `updateStatus`).
- **`com.pakem.dao.impl.UserDaoImpl`**: Implementasi JDBC murni menggunakan `PreparedStatement`, memetakan hasil query secara polimorfik ke instance `Pengurus` atau `Pelatih`.

#### 3. Lapisan Logika Bisnis (Service Layer)
- **`com.pakem.service.interfaces.IAuthService`**: Kontrak interface layanan autentikasi.
- **`com.pakem.service.impl.AuthServiceImpl`**:
  - Validasi username dan password tidak boleh kosong.
  - Verifikasi pengguna dan status keaktifan akun (`isActive`).
  - Hashing dan verifikasi password menggunakan algoritma **SHA-256** dengan perbandingan constant-time (`MessageDigest.isEqual`).
  - Pengelolaan sesi pengguna aktif (`currentUser`).

#### 4. Lapisan Antarmuka Pengguna (Swing + FlatLaf UI)
- **`com.pakem.ui.LoginPanel`**:
  - Desain modern card view dengan tema FlatLaf.
  - Form username dan password (dengan placeholder dan submit via tombol atau tombol Enter).
  - Indikator notifikasi error berwarna merah jika autentikasi gagal.
  - Hint informasi kredensial akun bawaan untuk memudahkan pengujian.
- **`com.pakem.ui.MainFrame`**:
  - Container tunggal berbasis `CardLayout` (`CARD_LOGIN` dan `CARD_APP`) yang bebas kedipan (*flicker-free*).
  - Header (Top Bar): Nama sanggar, username aktif, badge peran polimorfik (`[Pengurus]` / `[Pelatih]`), serta tombol konfirmasi Logout.
  - Sidebar Navigasi Dinamis:
    - Menampilkan modul operasional umum (Dashboard, Data Anggota, Kategori, Jadwal, Presensi, Event).
    - **Menu Iuran Bulanan** dan **Pengaturan Sanggar** otomatis disembunyikan jika login sebagai **Pelatih** (`user.canAccessIuran()` & `user.canAccessPengaturan()`).
  - Halaman Dashboard: Menyajikan greeting pengguna dan tabel **Matriks Hak Akses Peran Aktif** secara transparan.
  - Placeholder Pages: Panel informasi estetik untuk modul-modul yang akan dikerjakan pada fase berikutnya.
- **`com.pakem.Main`**: Entry point utama yang menginisialisasi FlatLaf Look and Feel, menguji database, dan menampilkan `MainFrame`.

#### 5. Pengujian & Verifikasi
- Kelas pengujian: `src/test/java/com/pakem/AuthTest.java`.
- Hasil pengujian terhadap database aktual `pakem_db`:
  - ✅ **Test 1:** Login Pengurus (`pengurus`/`pengurus123`) berhasil, instance polimorfik `Pengurus` valid, hak akses honor/iuran/pengaturan = `true`.
  - ✅ **Test 2:** Login Pelatih (`pelatih`/`pelatih123`) berhasil, instance polimorfik `Pelatih` valid, hak akses honor/iuran/pengaturan = `false`, hak akses latihan/presensi = `true`.
  - ✅ **Test 3:** Penolakan password salah berhasil ditolak dengan pesan yang sesuai.
  - ✅ **Test 4:** Penolakan username tidak terdaftar berhasil ditolak dengan pesan yang sesuai.
- Status Kompilasi: **`mvn compile` BUILD SUCCESS** (0 error, 0 warning).

---

## 🚀 Cara Menjalankan Aplikasi untuk Pengujian Mandiri

Anda dapat menjalankan antarmuka desktop aplikasi Pakem melalui terminal dengan perintah:

```bash
# Menggunakan Maven Exec Plugin
mvn compile exec:java

# Atau menggunakan java langsung
java -cp "target/classes;C:\Users\Acer\.m2\repository\com\mysql\mysql-connector-j\8.3.0\mysql-connector-j-8.3.0.jar;C:\Users\Acer\.m2\repository\com\formdev\flatlaf\3.5.4\flatlaf-3.5.4.jar" com.pakem.Main
```

**Kredensial untuk dicoba:**
- **Pengurus:** `username: pengurus`, `password: pengurus123` (Menu Iuran & Pengaturan muncul di sidebar).
- **Pelatih:** `username: pelatih`, `password: pelatih123` (Menu Iuran & Pengaturan otomatis hilang dari sidebar).

---

## ⏭️ Modul Berikutnya: Fase 2 (Manajemen Data Anggota)
1. **Model & Enums:** `StatusAnggota` (`AKTIF`, `VAKUM`, `NONAKTIF`), POJO `Anggota`, dan `RiwayatTingkat`.
2. **DAO Layer:** Interface `IAnggotaDao` dan implementasi `AnggotaDaoImpl` (CRUD anggota, filter status, pencatatan mutasi tingkat ke `riwayat_tingkat`).
3. **Service Layer:** `IAnggotaService` dan `AnggotaServiceImpl` (aturan bisnis isolasi status keanggotaan dan riwayat tingkat).
4. **UI Layer:**
   - `AnggotaListPanel`: Tabel data anggota, filter status (Aktif/Vakum/Nonaktif), tombol Tambah/Ubah/Ubah Status (dinonaktifkan untuk Pelatih).
   - `AnggotaFormDialog`: Dialog form input anggota baru dan edit.
   - `RiwayatTingkatDialog`: Dialog riwayat perubahan tingkat anggota.
