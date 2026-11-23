# Progress Pengerjaan Aplikasi Pakem

Dokumen ini mencatat riwayat pengerjaan bertahap, class penting yang telah dibuat, status pengujian, dan rencana modul berikutnya.

---

## 📌 Status Terakhir: Fase 4 Selesai (Jadwal Latihan & Master Materi Tabuh)
- **Tanggal:** 23 November 2026
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

### 🔹 Fase 4 — Jadwal Latihan & Master Materi Tabuh (Selesai ✅)

#### 1. Lapisan Model & Akses Data (DAO & Abstraction)
- **Model & Enums:**
  - `com.pakem.model.enums.JenisJadwal`: Enum `RUTIN` ("Latihan Rutin / Wajib") dan `KHUSUS_EVENT` ("Latihan Khusus Event").
  - `com.pakem.model.MateriTabuh`: Model master repertoar/gending (id, nama, deskripsi).
  - `com.pakem.model.JadwalLatihan`: Model jadwal latihan dengan helper field nama kategori, subkelompok, dan event.
  - `com.pakem.model.SesiMateri`: Model pencatatan materi latihan per sesi beserta catatan progres.
- **DAO Interfaces & Implementations:**
  - `IMateriTabuhDao` & `MateriTabuhDaoImpl`: CRUD master gending/materi tabuh, validasi nama unik, dan penghitungan pemakaian di sesi latihan (`countDigunakanDiSesi`).
  - `IJadwalLatihanDao` & `JadwalLatihanDaoImpl`: CRUD jadwal latihan, query berdasarkan filter kategori, subkelompok, dan tanggal latihan.
  - `ISesiMateriDao` & `SesiMateriDaoImpl`: CRUD sesi materi per jadwal latihan, dan query catatan progres terakhir (`findProgresTerakhirByMateri`) berdasarkan urutan tanggal jadwal terbaru.

#### 2. Lapisan Logika Bisnis (Service Layer)
- **`com.pakem.service.interfaces.ILatihanService`** & **`LatihanServiceImpl`**:
  - Mengelola CRUD Master Materi Tabuh dengan validasi nama unik dan pencegahan penghapusan jika materi sudah terikat sesi latihan.
  - Mengelola CRUD Jadwal Latihan (Rutin maupun Khusus Event) dengan validasi tanggal dan jenis jadwal.
  - Mengelola Sesi Materi Latihan (pencatatan materi yang dilatih per sesi beserta catatan progres).
  - **Fitur Kontinuitas Progres Tabuh (`getProgresTerakhirByMateri`)**: Menyajikan catatan progres sesi latihan terakhir untuk materi tabuh yang dipilih, memungkinkan pelatih menyambung progres latihan sebelumnya tanpa kehilangan konteks.

#### 3. Lapisan Antarmuka Pengguna (Swing + FlatLaf UI)
- **`com.pakem.ui.latihan.MateriTabuhPanel`**:
  - Tabel master materi/repertoar tabuh, tombol tambah, ubah, dan hapus dengan validasi pemakaian.
- **`com.pakem.ui.latihan.MateriTabuhFormDialog`**:
  - Modal form tambah/ubah materi gending dan deskripsinya.
- **`com.pakem.ui.latihan.JadwalLatihanPanel`**:
  - Filter interaktif berdasarkan Jenis Jadwal (Rutin/Khusus Event) dan Kategori Tingkat.
  - Tabel daftar jadwal latihan terurut waktu pelaksanaan.
  - Tombol aksi Tambah, Ubah, Hapus, dan tombol **"Lihat Sesi Materi"**.
- **`com.pakem.ui.latihan.JadwalLatihanFormDialog`**:
  - Form pembuatan/pengeditan jadwal latihan (tanggal, jam mulai/selesai dengan spinner time, lokasi, pemilihan kategori & subkelompok dinamis, catatan).
- **`com.pakem.ui.latihan.SesiMateriDialog`**:
  - Split-view modern: panel kiri menampilkan daftar materi tabuh di sesi ini, panel kanan menampilkan form input catatan progres baru dan kotak referensi **Catatan Progres Terakhir**.
  - **Fitur "📋 Salin Pengumuman (WA)"**: Memformat secara otomatis teks pesan pengumuman jadwal latihan (hari, tanggal, jam, lokasi, kelompok, daftar materi gending beserta target progres) langsung ke sistem clipboard untuk dibagikan ke grup WhatsApp.
- **Integrasi `MainFrame`**:
  - Menu sidebar "Jadwal Latihan" dan "Materi Tabuh" telah terhubung penuh dan aktif.

#### 4. Pengujian & Verifikasi
- Pengujian otomatis pada `src/test/java/com/pakem/LatihanTest.java`:
  - ✅ **Test 1:** Tambah materi tabuh baru berhasil.
  - ✅ **Test 2:** Ubah deskripsi materi tabuh berhasil.
  - ✅ **Test 3:** Pembuatan Jadwal Latihan Sesi 1 berhasil.
  - ✅ **Test 4:** Pencatatan materi dan progres pada Sesi 1 berhasil.
  - ✅ **Test 5:** Pembuatan Jadwal Latihan Sesi 2 berhasil.
  - ✅ **Test 6:** Kontinuitas progres berhasil (catatan sesi 1 terbaca akurat saat persiapan sesi 2).
  - ✅ **Test 7:** Pembaruan riwayat progres sesi 2 berhasil mengupdate referensi progres terbaru.
  - ✅ **Test 8:** Pencegahan penghapusan materi yang terikat sesi latihan berhasil divalidasi.
  - ✅ **Test 9:** Penghapusan jadwal dan pembersihan materi cascade berhasil.
- Kompilasi Maven: **`mvn compile` BUILD SUCCESS** (0 error, 0 warning).

---

## 🚀 Cara Menjalankan Aplikasi untuk Pengujian Mandiri

Jalankan antarmuka desktop aplikasi Pakem melalui terminal:

```powershell
mvn compile exec:java
```

**Skenario Pengujian Fase 4 yang Dapat Dicoba:**
1. **Login sebagai Pengurus atau Pelatih:**
   - Buka menu **Materi Tabuh** di sidebar untuk menambah/mengubah daftar gending (misal: *Tabuh Gilak*, *Gending Pengalang*).
   - Buka menu **Jadwal Latihan**, klik **+ Tambah Jadwal** untuk membuat jadwal baru (pilih tanggal, jam, kategori, dan lokasi).
   - Pilih jadwal yang baru dibuat, lalu klik **Lihat Sesi Materi**.
   - Pada dialog sesi materi, pilih materi tabuh dan isi catatan progres (misal: *"Selesai bagian kawitan"*), lalu klik **+ Tambah ke Jadwal**.
   - Klik tombol **📋 Salin Pengumuman (WA)**, lalu lakukan *Paste (Ctrl+V)* di teks editor atau WhatsApp untuk melihat format pengumuman yang rapi.
   - Buat jadwal kedua dengan materi yang sama, amati kotak **Catatan Progres Terakhir** secara otomatis menampilkan catatan dari pertemuan sebelumnya.

---

## ⏭️ Modul Berikutnya: Fase 5 (Presensi Latihan)
1. **Model:** `Presensi`, `enums.StatusHadir` (`HADIR`, `IZIN`, `SAKIT`, `TANPA_KETERANGAN`).
2. **DAO Layer:** `IPresensiDao` & `PresensiDaoImpl` (pencatatan kehadiran anggota aktif per jadwal, rekap presensi per anggota/periode).
3. **Service Layer:** `IPresensiService` & `PresensiServiceImpl` (isolasi: anggota vakum/nonaktif tidak muncul di daftar absensi baru, perhitungan persentase kehadiran).
4. **UI Layer:**
   - Panel presensi per jadwal latihan (tabel absensi cepat dengan radio button/combo status hadir).
   - Panel rekapitulasi presensi (tabel statistik kehadiran anggota dengan persentase keaktifan).
