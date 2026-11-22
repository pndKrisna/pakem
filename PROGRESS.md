# Progress Pengerjaan Aplikasi Pakem

Dokumen ini mencatat riwayat pengerjaan bertahap, class penting yang telah dibuat, status pengujian, dan rencana modul berikutnya.

---

## 📌 Status Terakhir: Fase 3 Selesai (Kategori Tingkat, Subkelompok, & Auto-Grouping)
- **Tanggal:** 22 November 2026
- **Status:** ✅ Selesai, Terintegrasi dengan Database MySQL, & Teruji 100%
- **Target Platform:** Java 25, Apache Maven, FlatLaf, MySQL JDBC

---

## 📁 Riwayat Pengerjaan Per Fase

### 🔹 Fase 0 — Fondasi Proyek (Selesai ✅)
1. **Dokumentasi Perancangan:**
   - Menyalin `rancangan_pakem.md` ke root repositori berdampingan dengan `prd_pakem_v1_0.md`.
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
1. **Model & Enums:** `RoleUser`, `User`, `Pengurus`, `Pelatih` dengan factory method polimorfik dan matriks hak akses.
2. **DAO Layer:** `IUserDao` dan `UserDaoImpl` (JDBC).
3. **Service Layer:** `IAuthService` dan `AuthServiceImpl` (hashing SHA-256 dan verifikasi password constant-time).
4. **UI Layer:** `LoginPanel` (card view modern FlatLaf) dan `MainFrame` (transisi card layout, topbar, dan navigasi dinamis berbasis peran).
5. **Pengujian:** `AuthTest` lulus 4 skenario pengujian 100%.

---

### 🔹 Fase 2 — Manajemen Data Anggota & Riwayat Tingkat (Selesai ✅)
1. **Model & Enums:** `StatusAnggota`, `Anggota`, `RiwayatTingkat`, `KategoriTingkat`, `Subkelompok`.
2. **DAO Layer:** `IAnggotaDao` & `AnggotaDaoImpl`, `IRiwayatTingkatDao` & `RiwayatTingkatDaoImpl`.
3. **Service Layer:** `IAnggotaService` & `AnggotaServiceImpl` (pencatatan otomatis ke `riwayat_tingkat` pada registrasi dan promosi tingkat, serta isolasi status).
4. **UI Layer:** `AnggotaListPanel`, `AnggotaFormDialog` (pembatasan hak akses pelatih), `RiwayatTingkatDialog`.
5. **Pengujian:** `AnggotaTest` lulus 7 skenario pengujian 100%.

---

### 🔹 Fase 3 — Kategori Tingkat, Subkelompok, & Auto-Grouping (Selesai ✅)

#### 1. Lapisan Akses Data (DAO & Abstraction)
- **`com.pakem.dao.interfaces.ISettingsDao`** & **`SettingsDaoImpl`**:
  - `get(key, defaultValue)` dan `set(key, value)`: Membaca dan memperbarui nilai pengaturan aplikasi (seperti kapasitas anjuran kelompok dari tabel `settings`).
- **`com.pakem.dao.interfaces.IKategoriTingkatDao`** & **`KategoriTingkatDaoImpl`**:
  - CRUD Kategori: `findAll()`, `findById(id)`, `save(kategori)`, `update(kategori)`, `delete(id)`.
  - Validasi: `existsByNama(nama, excludeId)`, `countAnggota(kategoriId)`.
  - Mengatur `kategori_tingkat_id = NULL` dan `subkelompok_id = NULL` pada anggota secara aman sebelum kategori dihapus.
- **`com.pakem.dao.interfaces.ISubkelompokDao`** & **`SubkelompokDaoImpl`**:
  - CRUD Subkelompok: `findByKategoriId(kategoriId)`, `findById(id)`, `save(subkelompok)`, `update(subkelompok)`, `delete(id)`.
  - Validasi: `existsByNamaInKategori(nama, kategoriId, excludeId)`, `countAnggota(subkelompokId)`.
  - Mengatur `subkelompok_id = NULL` pada anggota secara aman sebelum subkelompok dihapus.
- **`com.pakem.dao.interfaces.IAnggotaDao`** (Ekstensi):
  - `findByKategoriId(kategoriId, status)`: Mengambil anggota terfilter berdasarkan kategori tingkat dan status.
  - `updateSubkelompok(anggotaId, subkelompokId)`: Memperbarui penempatan subkelompok anggota.
  - `countHavingSubkelompokInKategori(kategoriId)`: Menghitung berapa banyak anggota aktif dalam kategori yang sudah memiliki subkelompok.

#### 2. Lapisan Logika Bisnis (Service Layer)
- **`com.pakem.service.interfaces.IKategoriService`** & **`KategoriServiceImpl`**:
  - CRUD Kategori Tingkat & Subkelompok dengan validasi nama unik dan nama tidak boleh kosong.
  - `getKapasitasAnjuran()`: Mengambil kapasitas maksimal kelompok anjuran (default 25 orang) dari database settings.
  - **Algoritma Auto-Grouping (`jalankanAutoGrouping`)**:
    - Mengambil seluruh anggota aktif dalam kategori yang dipilih.
    - Mengambil seluruh subkelompok dalam kategori tersebut.
    - Membagi anggota secara merata menggunakan algoritma Round-Robin (`i % jumlahKelompok`).
    - Validasi: Membutuhkan minimal 2 subkelompok dan minimal 1 anggota aktif.
  - `apakahAdaAnggotaSudahPunyaSubkelompok(kategoriId)`: Memeriksa apakah ada anggota aktif yang sudah terdaftar di subkelompok sebelum auto-grouping dijalankan, untuk memicu dialog konfirmasi penimpaan (overwrite confirmation).

#### 3. Lapisan Antarmuka Pengguna (Swing + FlatLaf UI)
- **`com.pakem.ui.kategori.KategoriTingkatPanel`**:
  - Tampilan *Split View*:
    - **Panel Kiri:** Tabel daftar kategori tingkat beserta urutannya, total anggota per kategori, tombol tambah, ubah, dan hapus.
    - **Panel Kanan:** 
      - Daftar subkelompok dalam bentuk tabulasi/accordion interaktif.
      - **Indikator Kapasitas Visual:** Badge modern penanda status kapasitas:
        - `EMPTY`: Kelompok belum memiliki anggota (warna abu-abu).
        - `OK`: Jumlah anggota dalam kapasitas anjuran (contoh `5 / 25 Anggota`, warna hijau).
        - `OVER`: Melebihi kapasitas anjuran (contoh `⚠️ 28 / 25 Anggota (Melebihi Kapasitas)`, warna oranye kemerahan). *Tidak memblokir sistem, hanya sebagai peringatan visual.*
      - **Tabel Anggota Kelompok:** Menampilkan daftar anggota dalam subkelompok dengan tombol "Pindah Kelompok".
      - **Tombol "⚡ Auto-Grouping":** Mengotomatisasi pembagian anggota merata ke subkelompok yang tersedia. Jika sudah ada anggota yang memiliki subkelompok, muncul dialog peringatan konfirmasi sebelum menimpa penempatan kelompok.
- **`com.pakem.ui.kategori.KategoriFormDialog`**:
  - Form modal tambah/ubah nama kategori dan nomor urutan tampilannya.
- **`com.pakem.ui.kategori.SubkelompokFormDialog`**:
  - Form modal tambah/ubah nama subkelompok pada kategori tingkat yang dipilih.
- **`com.pakem.ui.kategori.PindahSubkelompokDialog`**:
  - Dialog pemindahan manual anggota antar-subkelompok atau pengembalian ke status belum memiliki kelompok.
- **Integrasi `MainFrame`**:
  - Menu sidebar "Kategori Tingkat" kini langsung membuka `KategoriTingkatPanel` yang aktif dan terintegrasi dengan basis data.

#### 4. Pengujian & Verifikasi
- Pengujian otomatis [KategoriTest.java](file:///c:/Users/Acer/Documents/GitHub/pakem/src/test/java/com/pakem/KategoriTest.java) dijalankan langsung ke database MySQL:
  - ✅ **Test 1:** Tambah kategori tingkat baru berhasil.
  - ✅ **Test 2:** Ubah nama dan urutan kategori berhasil.
  - ✅ **Test 3:** Pembuatan 2 subkelompok berhasil.
  - ✅ **Test 4:** Pendaftaran anggota aktif ke kategori berhasil.
  - ✅ **Test 5:** Auto-Grouping merata berhasil (Round-Robin).
  - ✅ **Test 6:** Deteksi penempatan subkelompok untuk dialog konfirmasi penimpaan valid.
  - ✅ **Test 7:** Pemindahan manual anggota antar-subkelompok berhasil.
  - ✅ **Test 8:** Pembacaan kapasitas anjuran kelompok dari settings (25 orang) akurat.
  - ✅ **Test 9:** Hapus kategori aman (anggota tidak terhapus, foreign key di-null-kan).
- Kompilasi Maven: **`mvn compile` BUILD SUCCESS** (0 error, 0 warning).

---

## 🚀 Cara Menjalankan Aplikasi untuk Pengujian Mandiri

Jalankan antarmuka desktop aplikasi Pakem melalui terminal:

```powershell
mvn compile exec:java
```

**Skenario Pengujian Fase 3 yang Dapat Dicoba:**
1. **Login sebagai Pengurus (`pengurus` / `pengurus123`):**
   - Buka menu **Kategori Tingkat** di sidebar.
   - Pilih salah satu kategori di panel kiri (misal: *Dasar* atau *Lanjutan*).
   - Di panel kanan, klik **+ Subkelompok** untuk membuat kelompok baru (misal: *Kelompok A* dan *Kelompok B*).
   - Klik tombol **⚡ Auto-Grouping**: amati anggota aktif terbagi merata secara otomatis ke kelompok A dan B.
   - Jika tombol **⚡ Auto-Grouping** diklik kembali, perhatikan sistem menampilkan dialog konfirmasi peringatan penimpaan kelompok yang sudah ada.
   - Pilih salah satu anggota di tabel, klik tombol **Pindah Kelompok...** untuk memindahkan anggota secara manual ke kelompok lain.
   - Amati badge kapasitas kelompok menampilkan status jumlah anggota (misal `12 / 25 Anggota`).
2. **Login sebagai Pelatih (`pelatih` / `pelatih123`):**
   - Pelatih dapat melihat struktur kategori dan pembagian subkelompok secara transparan.

---

## ⏭️ Modul Berikutnya: Fase 4 (Jadwal Latihan & Master Materi Tabuh)
1. **Model:** `MateriTabuh`, `JadwalLatihan`, `SesiMateri`, `enums.JenisJadwal` (`RUTIN`, `KHUSUS_EVENT`).
2. **DAO Layer:**
   - `IMateriTabuhDao` & `MateriTabuhDaoImpl` (CRUD gending/materi).
   - `IJadwalLatihanDao` & `JadwalLatihanDaoImpl` (CRUD jadwal rutin per kategori/subkelompok dan jadwal insidental event).
   - `ISesiMateriDao` & `SesiMateriDaoImpl` (pencatatan materi latihan per sesi beserta catatan progres).
3. **Service Layer:** `ILatihanService` (pencarian riwayat progres materi terakhir kelompok untuk referensi pelatih).
4. **UI Layer:**
   - Panel jadwal latihan (kalender/daftar jadwal terurut waktu, filter jenis dan kategori).
   - Dialog pembuatan/edit jadwal latihan.
   - Dialog sesi materi latihan (menampilkan catatan progres sesi sebelumnya dan input catatan baru).
   - Fitur **"Salin Pengumuman"** (format teks pesan WhatsApp ke clipboard).
