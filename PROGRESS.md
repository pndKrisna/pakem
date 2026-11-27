# Progress Pengerjaan Aplikasi Pakem

Dokumen ini mencatat riwayat pengerjaan bertahap, class penting yang telah dibuat, status pengujian, dan rencana modul berikutnya.

---

## 📌 Status Terakhir: Fase 6 Selesai (Pertunjukan / Event)
- **Tanggal:** 24 November 2026
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

---

### 🔹 Fase 5 — Presensi Latihan & Rekapitulasi Kehadiran (Selesai ✅)

#### 1. Lapisan Model & Akses Data (DAO & Abstraction)
- **Model & Enums:**
  - `com.pakem.model.enums.StatusHadir`: Enum status kehadiran (`HADIR`, `IZIN`, `SAKIT`, `TANPA_KETERANGAN`).
  - `com.pakem.model.Presensi`: Model entitas presensi (id, jadwal_latihan_id, anggota_id, status_hadir, keterangan, serta field join nama, kontak, dan kategori/subkelompok).
  - `com.pakem.model.RekapPresensi`: Model DTO statistik kehadiran per anggota (total pertemuan, jumlah hadir, izin, sakit, alpa, serta persentase kehadiran terhitung).
- **DAO Interfaces & Implementations:**
  - `IPresensiDao` & `PresensiDaoImpl`:
    - `findByJadwalId(jadwalId)`: Mengambil daftar presensi per jadwal latihan terurut nama anggota.
    - `findByJadwalAndAnggota(jadwalId, anggotaId)`: Mengambil status presensi spesifik seorang anggota.
    - `save(presensi)`, `update(presensi)`, `saveOrUpdate(presensi)`: Operasi persistensi dengan dukungan klausa SQL `ON DUPLICATE KEY UPDATE` untuk menjaga integritas constraint `uq_presensi_jadwal_anggota`.
    - `saveBatch(presensiList)`: Penyimpanan lembar presensi banyak anggota dalam 1 transaksi atomik (`setAutoCommit(false)`).
    - `countByJadwalAndStatus(jadwalId, status)` & `countTotalByJadwal(jadwalId)`: Ringkasan cepat kehadiran sesi latihan.
    - `getRekapitulasi(kategoriId, subkelompokId, dariTanggal, sampaiTanggal)`: Agregasi query SQL multi-tabel (`anggota`, `kategori_tingkat`, `subkelompok`, `presensi`, `jadwal_latihan`) untuk menghitung total sesi, hadir, izin, sakit, alpa, dan tingkat keaktifan.
    - `delete(id)` & `deleteByJadwalId(jadwalId)`: Operasi penghapusan data presensi.

#### 2. Lapisan Logika Bisnis (Service Layer)
- **`com.pakem.service.interfaces.IPresensiService`** & **`PresensiServiceImpl`**:
  - `getLembarPresensiJadwal(jadwalId)`:
    - **Isolasi Status Teruji:** Hanya anggota aktif (`StatusAnggota.AKTIF`) yang dimasukkan ke lembar presensi. Anggota dengan status `VAKUM` (cuti) atau `NONAKTIF` (keluar) secara otomatis disaring keluar dari lembar kehadiran baru.
    - **Penyaringan Berdasarkan Target Jadwal:** Menyesuaikan peserta apakah jadwal ditujukan untuk subkelompok tertentu, kategori tingkat tertentu, atau seluruh anggota sanggar.
    - **Sinkronisasi Record Tersimpan:** Menggabungkan record presensi yang sudah tersimpan di database dengan anggota aktif yang berlaku (default status `HADIR` untuk entri baru).
  - `simpanDaftarPresensi(daftarPresensi)`: Menyimpan hasil absensi sesi secara batch dan atomik.
  - `simpanPresensi(presensi)`: Menyimpan/mengubah presensi per anggota secara individual.
  - `getRingkasanPresensiJadwal(jadwalId)`: Mengembalikan peta ringkasan kehadiran (TOTAL, HADIR, IZIN, SAKIT, ALPA) untuk visualisasi cepat.
  - `getRekapitulasi(kategoriId, subkelompokId, dariTanggal, sampaiTanggal)`: Mengembalikan data statistik keaktifan anggota aktif sanggar.

#### 3. Lapisan Antarmuka Pengguna (Swing + FlatLaf UI)
- **`com.pakem.ui.presensi.PresensiPanel`**:
  - Selector jadwal latihan interaktif dengan tampilan ringkas (tanggal, jam, lokasi, target kelompok).
  - Tabel entri presensi per sesi dengan:
    - Kolom No, ID Anggota, Nama Anggota, Kategori, Subkelompok, Status Kehadiran (ComboBox editor/renderer dengan warna khusus), dan Keterangan.
  - **Fitur Percepat Absensi:** Tombol **"✅ Set Semua Hadir"** untuk mengisi seluruh baris menjadi hadir dalam satu klik sebelum pelatih menandai anggota yang izin/sakit/alpa.
  - Tombol **"💾 Simpan Presensi"** untuk menyimpan seluruh lembar kehadiran sekaligus.
  - Baris indikator ringkasan di bagian bawah: Total Peserta, Hadir (hijau), Izin (biru), Sakit (kuning), Alpa (merah) yang terupdate secara *live*.
- **`com.pakem.ui.presensi.RekapPresensiPanel`**:
  - Filter Kategori Tingkat, Subkelompok (dinamis), dan Rentang Tanggal (Dari - Sampai).
  - Tabel rekapitulasi keaktifan anggota dengan visual badge persentase (Hijau `>= 80%`, Kuning `50-79%`, Merah `< 50%`).
  - **Fitur "📋 Salin Rekap Teks"**: Menyalin ringkasan persentase keaktifan seluruh anggota ke sistem clipboard untuk laporan evaluasi atau dibagikan ke pengurus.
- **`com.pakem.ui.presensi.PresensiMainPanel`**:
  - Kontainer tabbed view modern FlatLaf yang menyatukan panel pencatatan presensi dan rekapitulasi kehadiran dengan auto-refresh saat tab berpindah.
- **Integrasi `MainFrame`**:
  - Menu sidebar "Presensi Latihan" kini langsung membuka `PresensiMainPanel` yang aktif dan terintegrasi penuh.

#### 4. Pengujian & Verifikasi
- Pengujian otomatis pada `src/test/java/com/pakem/PresensiTest.java` langsung ke database MySQL:
  - ✅ **Test 1:** Setup data master kategori, subkelompok, dan anggota uji (Aktif, Vakum, Nonaktif) berhasil.
  - ✅ **Test 2:** Isolasi status anggota teruji 100% (anggota Vakum dan Nonaktif otomatis diabaikan, hanya anggota Aktif yang muncul).
  - ✅ **Test 3:** Simpan presensi batch (multi-status: Hadir, Izin) berhasil disimpan dan terbaca akurat.
  - ✅ **Test 4:** Perhitungan ringkasan statistik sesi presensi akurat (Total, Hadir, Izin, Sakit, Alpa).
  - ✅ **Test 5:** Pembaruan status presensi berhasil (perubahan Izin -> Sakit).
  - ✅ **Test 6:** Akumulasi rekapitulasi lintas sesi & akurasi persentase keaktifan teruji valid (100% vs 0%).
  - ✅ **Test 7:** Cascade delete presensi pada penghapusan jadwal latihan teruji bersih.
  - ✅ **Test 8:** Pembersihan data uji selesai.
- Pengujian regresi modul sebelumnya: `LatihanTest` (9/9 PASS), `KategoriTest` (9/9 PASS), `AuthTest` (4/4 PASS).
- Kompilasi Maven: **`mvn clean test-compile` BUILD SUCCESS** (0 error, 0 warning).

---

## 🚀 Cara Menjalankan Aplikasi untuk Pengujian Mandiri

Jalankan antarmuka desktop aplikasi Pakem melalui terminal:

```powershell
mvn compile exec:java
```

**Skenario Pengujian Fase 5 yang Dapat Dicoba:**
1. **Login sebagai Pengurus atau Pelatih:**
   - Buka menu **Presensi Latihan** di sidebar.
   - Pada tab **"📝 Catat Presensi Sesi"**, pilih salah satu sesi jadwal latihan di dropdown atas.
   - Amati daftar peserta hanya menampilkan anggota yang berstatus **Aktif**.
   - Klik tombol **"✅ Set Semua Hadir"** untuk menandai semua anggota hadir seketika.
   - Ubah salah satu anggota menjadi **Izin** atau **Sakit** dan ketik catatan pada kolom keterangan (misal: *"Izin ada acara keluarga"*).
   - Klik **"💾 Simpan Presensi"** dan perhatikan ringkasan kehadiran di bar bawah (Hadir, Izin, Sakit, Alpa) berubah secara akurat.
   - Berpindah ke tab **"📊 Rekapitulasi Kehadiran"**, klik **"Terapkan Filter"** untuk melihat statistik kehadiran dan badge persentase keaktifan anggota.
   - Klik **"📋 Salin Rekap Teks"** lalu lakukan *Paste (Ctrl+V)* di Notepad atau WhatsApp untuk melihat hasil laporan evaluasi kehadiran yang rapi.

---

---

### 🔹 Fase 6 — Pertunjukan / Event (Selesai ✅)

#### 1. Lapisan Model & Enums
- **Enums:**
  - `com.pakem.model.enums.JenisAcara`: `JOB` ("Pentas / Job"), `NGAYAH` ("Ngayah"), `UJIAN` ("Ujian Kenaikan Tingkat").
  - `com.pakem.model.enums.StatusKesediaan`: `BELUM_DITANYA`, `BERSEDIA`, `TIDAK_BERSEDIA`.
  - `com.pakem.model.enums.StatusHonor`: `BELUM_DIBAGIKAN`, `SUDAH_DIBAGIKAN`.
- **Model Entitas:**
  - `com.pakem.model.Event`: id, nama_acara, jenis_acara, tanggal, waktu, lokasi, catatan, plus helper jumlahTim & totalPemain.
  - `com.pakem.model.TimEvent`: id, event_id, nama_tim, catatan, plus helper namaEvent & jumlahPemain.
  - `com.pakem.model.PemainTim`: id, tim_event_id, anggota_id, status_kesediaan, nominal_honor, status_honor, plus helper join nama/kontak/kategori/subkelompok/tim/acara/jenis_acara.

#### 2. Lapisan Akses Data (DAO)
- **`IEventDao` & `EventDaoImpl`:** `findAll` (filter jenis, tanggal, kata kunci), `findById`, `save`, `update`, `delete`, `countTim`, `countPemain`. Semua kolom 100% cocok dengan `schema.sql` tabel `event`.
- **`ITimEventDao` & `TimEventDaoImpl`:** `findByEventId`, `findById`, `save`, `update`, `delete`, `countPemain`, `existsByNamaInEvent` (validasi nama unik per event).
- **`IPemainTimDao` & `PemainTimDaoImpl`:** `findByTimId`, `findByEventId`, `findById`, `findByTimAndAnggota`, `save`, `update`, `updateKesediaan`, `updateHonor`, `delete`, `deleteByTimId`, `existsByTimAndAnggota`, `countByKesediaan`.

#### 3. Lapisan Logika Bisnis (Service Layer)
- **`IEventService` & `EventServiceImpl`:**
  - CRUD Event & Tim Event (Multi-Tim).
  - Validasi: nama acara wajib diisi; tanggal acara wajib diisi; nama tim harus unik per event.
  - `simpanEvent` secara otomatis membuat 1 tim default ("Tim Utama") agar acara siap diisi pemain.
  - **Pemain Tim & Isolasi Status:** `tambahPemain` hanya menerima anggota berstatus `AKTIF`; anggota `VAKUM` dan `NONAKTIF` secara otomatis ditolak. Mendukung pemilihan pemain lintas tingkat (cross-category).
  - **Pembatasan Akses Honor:** `ubahHonorPemain` hanya dapat dipanggil oleh pengguna dengan `canAccessHonor() == true` (Pengurus) dan hanya berlaku jika event berjenis `JOB`. Kolom honor disembunyikan untuk acara `NGAYAH` dan `UJIAN` serta untuk peran Pelatih.
  - `getCalonPemainTersedia(timEventId)`: Mengambil anggota aktif yang belum terdaftar di tim tersebut.
  - `formatPengumumanEvent(int eventId)`: Menghasilkan teks WhatsApp rapi dengan rincian acara, daftar tim, dan daftar pemain beserta status kesediaan.

#### 4. Lapisan Antarmuka Pengguna (Swing + FlatLaf UI)
- **`com.pakem.ui.event.EventListPanel`:** Tabel daftar acara dengan filter Jenis Acara, pencarian kata kunci, rentang tanggal. Tombol Tambah/Ubah/Hapus Acara (terbatas untuk `canManageEvent()`), Kelola Tim & Pemain, dan Salin WA. Double-click baris membuka manajemen tim.
- **`com.pakem.ui.event.EventFormDialog`:** Form tambah/ubah acara (nama, jenis acara, tanggal, waktu, lokasi, catatan).
- **`com.pakem.ui.event.TimEventDialog`:** Manajemen multi-tim dan susunan pemain. Tabel pemain menampilkan kolom Honor & Status Honor **hanya jika** `event.getJenisAcara() == JenisAcara.JOB` **dan** `currentUser.canAccessHonor() == true`. Aksi: tambah pemain, ubah kesediaan, atur honor (khusus Pengurus di JOB), hapus pemain, dan "📋 Salin Info Acara & Pemain (WA)".
- **`com.pakem.ui.event.PilihPemainDialog`:** Dialog pemilihan anggota aktif lintas tingkat dengan filter kategori tingkat dan input status kesediaan serta nominal honor.
- **Integrasi `MainFrame`:** Menu sidebar "Pertunjukan / Event" kini membuka `EventListPanel` yang aktif dan terintegrasi penuh.

#### 5. Pengujian & Verifikasi
- Pengujian otomatis pada `src/test/java/com/pakem/EventTest.java` langsung ke database MySQL:
  - ✅ **Test 1:** Setup data master & anggota berhasil.
  - ✅ **Test 2:** CRUD Event (Simpan, Ubah, Baca) berhasil.
  - ✅ **Test 3:** Multi-Tim Event berhasil (2 tim, termasuk default Tim Utama).
  - ✅ **Test 4:** Pemain lintas tingkat + isolasi status anggota (Vakum & Nonaktif ditolak).
  - ✅ **Test 5:** Pembatasan akses honor (Pengurus BISA, Pelatih DITOLAK).
  - ✅ **Test 6:** Hapus Event dengan cascade delete Tim & Pemain berhasil.
  - ✅ **Test 7:** Pembersihan data uji selesai.
- Kompilasi Maven: **`mvn clean test-compile` BUILD SUCCESS** (0 error).

---

## ⏭️ Modul Berikutnya: Fase 7 (Iuran Bulanan)
1. **DAO:** `IIuranDao` + impl.
2. **Service:** `IIuranService` + impl (hitung status warna berdasarkan settings: Hijau/Kuning/Merah).
3. **UI:** `IuranPanel` (pilih bulan/tahun → daftar anggota Aktif + warna indikator + catat bayar).
4. **Aturan:** Daftar tagihan hanya anggota Aktif (Vakum dan Nonaktif diabaikan); warna indikator berubah dinamis sesuai tanggal dan pengaturan.
