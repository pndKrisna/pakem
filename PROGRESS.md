# Progress Pengerjaan Aplikasi Pakem

Dokumen ini mencatat riwayat pengerjaan bertahap, class penting yang telah dibuat, status pengujian, dan rencana modul berikutnya.

---

## 📌 Status Terakhir: Fase 2 Selesai (Manajemen Data Anggota & Riwayat Tingkat)
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

#### 1. Penerapan PBO (Model & Enums)
- **`com.pakem.model.enums.StatusAnggota`**: Enum status keanggotaan:
  - `AKTIF`: Anggota aktif rutin berkegiatan (muncul pada presensi, calon tim event, dan tagihan iuran).
  - `VAKUM`: Cuti sementara (otomatis disembunyikan dari presensi baru dan tagihan iuran).
  - `NONAKTIF`: Berhenti permanen (diarsipkan, riwayat presensi/tingkat tetap abadi).
- **`com.pakem.model.Anggota`**: Model data anggota dengan enkapsulasi (ID, Nama Lengkap, Nomor Kontak, Tanggal Bergabung, Status, Kategori Tingkat ID, Subkelompok ID, Catatan, Timestamp).
- **`com.pakem.model.RiwayatTingkat`**: Model log riwayat perubahan tingkat kemampuan anggota (Anggota ID, Tingkat Lama ID, Tingkat Baru ID, Tanggal Perubahan, Keterangan).
- **`com.pakem.model.KategoriTingkat`** & **`com.pakem.model.Subkelompok`**: Model pendukung untuk struktur pengelompokan anggota.

#### 2. Lapisan Akses Data (DAO & Abstraction)
- **`com.pakem.dao.interfaces.IAnggotaDao`** & **`AnggotaDaoImpl`**:
  - `findAll(filterStatus, searchKeyword)`: Mengambil daftar anggota dengan filter status opsional dan pencarian wildcard (nama, kontak, catatan).
  - `findById(id)`, `save(anggota)`, `update(anggota)`, `updateStatus(id, newStatus)`.
  - `countByStatus(status)`, `countTotal()`: Untuk statistik ringkasan anggota.
- **`com.pakem.dao.interfaces.IRiwayatTingkatDao`** & **`RiwayatTingkatDaoImpl`**:
  - `save(riwayat)`: Mencatat mutasi tingkat anggota.
  - `findByAnggotaId(anggotaId)`: Mengambil seluruh riwayat tingkat anggota terurut waktu terbaru.
- **`com.pakem.dao.interfaces.IKategoriTingkatDao`** & **`ISubkelompokDao`** (beserta implementasinya): Menyediakan daftar master kategori dan subkelompok untuk komponen dropdown.

#### 3. Lapisan Logika Bisnis (Service Layer)
- **`com.pakem.service.interfaces.IAnggotaService`** & **`AnggotaServiceImpl`**:
  - Validasi ketat nama lengkap dan tanggal bergabung.
  - **Otomatisasi Log Riwayat Awal:** Saat anggota baru didaftarkan dengan kategori tingkat, sistem otomatis mencatat entri pertama ke `riwayat_tingkat`.
  - **Deteksi Perubahan Tingkat:** Saat data anggota diperbarui, jika nilai `kategoriTingkatId` berubah dari nilai lama, sistem otomatis mencatat log mutasi tingkat baru ke `riwayat_tingkat` dengan tanggal dan keterangan alasan perubahan.
  - **Isolasi Status Keanggotaan:** Pengubahan status ke `VAKUM` atau `NONAKTIF` mengisolasi anggota dari query berstatus `AKTIF` tanpa menghapus riwayat historisnya.

#### 4. Lapisan Antarmuka Pengguna (Swing + FlatLaf UI)
- **`com.pakem.ui.anggota.AnggotaListPanel`**:
  - Toolbar: Dropdown filter status (Semua / Aktif / Vakum / Nonaktif), text field pencarian cepat, tombol Refresh.
  - Tombol aksi: "+ Tambah Anggota", "Ubah Data", "Riwayat Tingkat", "Ubah Status...".
  - Tabel interaktif dengan **StatusBadgeRenderer**: Pill status berwarna hijau untuk Aktif, kuning/amber untuk Vakum, dan abu-abu untuk Nonaktif.
  - Bottom Summary Bar: Menampilkan total anggota dan rincian per status secara real-time.
- **`com.pakem.ui.anggota.AnggotaFormDialog`**:
  - Dialog pendaftaran dan edit data anggota.
  - Dropdown Kategori Tingkat terhubung secara dinamis dengan Dropdown Subkelompok.
  - Field alasan kenaikan tingkat yang otomatis tersorot saat kategori tingkat diubah dari nilai semula.
  - **Dukungan Polimorfisme Peran:** Jika login sebagai **Pelatih** (`!currentUser.canEditAnggota()`), form identitas utama (nama, kontak, tgl bergabung, status) otomatis menjadi read-only, namun pengubahan/usulan Kategori Tingkat dan Catatan tetap diizinkan sesuai PRD.
- **`com.pakem.ui.anggota.RiwayatTingkatDialog`**:
  - Modal dialog yang menampilkan tabel kronologis mutasi tingkat anggota beserta tanggal, tingkat lama, tingkat baru, dan keterangan alasan.
- **Integrasi `MainFrame`**:
  - Tab "Data Anggota" pada sidebar kini langsung membuka `AnggotaListPanel` aktif.

#### 5. Pengujian & Verifikasi
- Pengujian otomatis [AnggotaTest.java](file:///c:/Users/Acer/Documents/GitHub/pakem/src/test/java/com/pakem/AnggotaTest.java) dijalankan langsung ke database MySQL:
  - ✅ **Test 1:** Simpan anggota baru berhasil dengan auto-increment ID.
  - ✅ **Test 2:** Riwayat awal anggota otomatis tercatat di `riwayat_tingkat`.
  - ✅ **Test 3:** Anggota aktif tampil di filter AKTIF.
  - ✅ **Test 4:** Perubahan tingkat (Dasar ke Madya) berhasil & riwayat kenaikan tercatat lengkap.
  - ✅ **Test 5:** Isolasi status VAKUM berhasil (hilang dari filter aktif, tetap ada di arsip).
  - ✅ **Test 6:** Isolasi status NONAKTIF berhasil & seluruh riwayat tingkat tetap abadi.
  - ✅ **Test 7:** Ringkasan statistik jumlah anggota akurat.
- Kompilasi Maven: **`mvn compile` BUILD SUCCESS** (0 error, 0 warning).

---

## 🚀 Cara Menjalankan Aplikasi untuk Pengujian Mandiri

Jalankan antarmuka desktop aplikasi Pakem melalui terminal:

```powershell
mvn compile exec:java
```

**Skenario Pengujian yang Dapat Dicoba:**
1. **Login sebagai Pengurus (`pengurus` / `pengurus123`):**
   - Masuk ke menu **Data Anggota**.
   - Coba tombol **+ Tambah Anggota**, isi nama, pilih kategori (misal: *Dasar*), dan simpan.
   - Pilih anggota tersebut, klik **Ubah Data**, ubah kategorinya menjadi *Madya*, isi alasan perubahan tingkat, lalu simpan.
   - Klik **Riwayat Tingkat** untuk melihat riwayat kenaikan tingkat yang tercatat otomatis.
   - Coba tombol **Ubah Status...** menjadi *Vakum*, lalu amati badge status berubah warna kuning dan anggota hilang dari filter *Aktif Saja*.
2. **Login sebagai Pelatih (`pelatih` / `pelatih123`):**
   - Masuk ke menu **Data Anggota**.
   - Tombol *+ Tambah Anggota* dan *Ubah Status* otomatis tersembunyi.
   - Buka **Ubah Data**: field identitas menjadi disabled (read-only), namun kategori tingkat tetap dapat diusulkan/diubah.

---

## ⏭️ Modul Berikutnya: Fase 3 (Kategori Tingkat & Subkelompok)
1. **DAO Layer:** Operasi CRUD penuh untuk `kategori_tingkat` dan `subkelompok`.
2. **Service Layer:** `IKategoriService` (tambah/edit/hapus kategori, buat subkelompok, validasi kapasitas kelompok anjuran default 25 orang, dan algoritma **Auto-Grouping** pembagian merata anggota dengan dialog konfirmasi jika menimpa).
3. **UI Layer:**
   - `KategoriTingkatPanel`: Tampilan hierarki kategori tingkat dan daftar subkelompoknya, beserta indikator kapasitas anggota per kelompok.
   - `SubkelompokDialog`: Form tambah/ubah subkelompok.
   - Tombol dan dialog konfirmasi bantuan **Auto-Grouping**.
