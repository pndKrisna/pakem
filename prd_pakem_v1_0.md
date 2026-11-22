# Product Requirements Document (PRD)
## Aplikasi Manajemen Administrasi Sanggar Seni: **Pakem**

- **Versi Dokumen:** 1.0 (Final Scope)
- **Tanggal:** 22 November 2026
- **Target Platform:** Aplikasi Desktop (Java 25, Java Swing + FlatLaf, MySQL, Maven)
- **Tujuan Dokumen:** Menjadi acuan tunggal kebutuhan produk dan aturan bisnis bagi tim/AI pengembang dalam merancang arsitektur teknis (database, struktur *class* PBO, dan kode aplikasi).

---

## 1. Latar Belakang & Tujuan Produk

### 1.1 Ringkasan Produk
**Pakem** adalah aplikasi desktop internal untuk membantu pengurus dan pelatih dalam mengelola administrasi serta operasional kegiatan **tabuh** di sebuah sanggar seni tradisional. Aplikasi ini dirancang untuk menggantikan pencatatan manual dan penyebaran informasi yang tercecer di grup WhatsApp menjadi satu sistem terpusat yang rapi, fleksibel, dan tetap menghargai nilai kekeluargaan sanggar.

### 1.2 Tujuan Utama
1. Memusatkan data anggota sanggar beserta status keanggotaan (*Aktif, Vakum, Nonaktif*) dan riwayat tingkat kemampuannya.
2. Memudahkan pengelolaan kategori tingkat dan subkelompok secara fleksibel (manual maupun pembagian otomatis opsional).
3. Mendokumentasikan jadwal latihan rutin maupun khusus acara, daftar materi tabuh, serta catatan progres latihan terakhir agar pelatih mudah menyambung materi.
4. Mencatat presensi anggota secara teratur sebagai bahan pemantauan tanpa sanksi otomatis.
5. Mengelola jadwal pertunjukan (*Pentas/Job, Ngayah, Ujian Kenaikan Tingkat*), pembentukan tim lintas tingkat, status kesediaan pemain, serta catatan pembagian honor khusus *job*.
6. Memantau status pembayaran iuran bulanan anggota aktif melalui indikator warna administratif.

---

## 2. Batasan Ruang Lingkup (*Out of Scope*)

Untuk menjaga fokus aplikasi agar tetap realistis, sederhana, dan sesuai dengan kultur sanggar, fitur-fitur berikut **tidak dibuat** pada versi 1.0:
1. **Modul Tari:** Fokus versi awal 100% pada kegiatan **tabuh**. Tidak ada aturan atau rubrik penilaian tari.
2. **Manajemen Instrumen & Posisi Pemain:** Tidak ada modul konfigurasi instrumen gamelan, inventaris alat, maupun pemetaan posisi instrumen (*lanang/wadon*, dll.) pada anggota.
3. **Platform Multi-Sanggar:** Aplikasi hanya digunakan secara internal oleh satu sanggar.
4. **Akun Login Khusus Anggota / Aplikasi Mobile:** Anggota tidak melakukan login. Seluruh pencatatan dilakukan oleh Pengurus atau Pelatih.
5. **Bot WhatsApp Otomatis:** Tidak menggunakan integrasi API/bot WhatsApp. Digantikan dengan fitur tombol **Salin Format Pengumuman (Copy to Clipboard)**.
6. **Payment Gateway:** Pencatatan iuran dan honor dilakukan secara manual oleh pengurus tanpa integrasi pembayaran daring.
7. **Sanksi Otomatis:** Sistem tidak boleh memblokir anggota dari daftar absensi atau pertunjukan hanya karena belum membayar iuran atau jarang hadir.
8. **Modul Terpisah Ujian & Sertifikat:** Ujian kenaikan tingkat digabungkan sebagai salah satu jenis acara pada modul Pertunjukan/Event.

---

## 3. Peran Pengguna & Hak Akses

Aplikasi menggunakan sistem autentikasi (Login) dengan **2 peran pengguna**:

| Fitur / Modul | Pengurus / Admin | Pelatih |
| :--- | :---: | :---: |
| Kelola Data Anggota (Tambah, Ubah, Ubah Status) | Penuh | Lihat Detail |
| Ubah Kategori Tingkat Anggota | Penuh | Diizinkan (Update/Usulan) |
| Kelola Master Kategori Tingkat & Subkelompok | Penuh | Lihat |
| Kelola Jadwal Latihan & Master Materi Tabuh | Penuh | Penuh |
| Catat Progres Materi Latihan | Penuh | Penuh |
| Catat & Edit Presensi Latihan | Penuh | Penuh |
| Kelola Pertunjukan/Event & Susunan Tim | Penuh | Lihat |
| Catat Status Kesediaan Pemain di Event | Penuh | Penuh |
| Catat Honor & Status Pembagian (*Pentas/Job*) | Penuh | Tidak Ada Akses |
| Kelola Iuran Bulanan & Pengaturan Sanggar | Penuh | Tidak Ada Akses |

---

## 4. Kebutuhan Fungsional & Aturan Bisnis

### Modul 1: Autentikasi & Dashboard Ringkas
- **Login:** Pengguna masuk menggunakan `username` dan `password`. Antarmuka dan menu menyesuaikan peran (*Pengurus* atau *Pelatih*).
- **Dashboard:** Menampilkan ringkasan informasi terkini:
  - Jumlah anggota berdasarkan status (*Aktif*, *Vakum*).
  - Jadwal latihan dan pertunjukan terdekat.
  - Ringkasan persentase kehadiran latihan bulan berjalan.
  - Ringkasan status iuran bulan berjalan (khusus *Pengurus*).

---

### Modul 2: Manajemen Data Anggota
- **Data Anggota:** Menyimpan ID Anggota, Nama Lengkap, Nomor Kontak/HP (opsional), Tanggal Bergabung, Status Keanggotaan, Kategori Tingkat saat ini, Subkelompok saat ini, dan Catatan Tambahan (teks bebas).
- **Aturan Status Keanggotaan (3 Status):**
  1. **Aktif:** Anggota rutin berkegiatan. Namanya otomatis muncul pada daftar presensi latihan baru, pilihan calon pemain pertunjukan, dan daftar tagihan iuran bulanan.
  2. **Vakum (Cuti Sementara):** Anggota berhenti sementara karena alasan tertentu. Namanya otomatis **disembunyikan** dari daftar presensi latihan baru dan **tidak dimunculkan** dalam tagihan iuran selama berstatus vakum. Dapat diubah kembali menjadi *Aktif* kapan saja.
  3. **Nonaktif (Keluar):** Anggota sudah berhenti permanen. Namanya tidak lagi muncul di kegiatan maupun tagihan baru, tetapi seluruh riwayat presensi, riwayat tingkat, dan pembayaran lamanya tetap tersimpan sebagai arsip.
- **Riwayat Perubahan Tingkat:** Setiap kali kategori tingkat anggota diubah (misalnya dari *Madya* ke *Purna*), sistem menyimpan catatan tanggal perubahan dan tingkat sebelumnya agar perkembangan anggota terdokumentasi.

---

### Modul 3: Kategori Tingkat & Subkelompok
- **Kustomisasi Kategori Tingkat:** Pengurus dapat menambah, mengubah nama, dan menghapus kategori tingkat sesuai kebiasaan sanggar (contoh inspirasi: *Dasar, Madya, Purna, Inti* — **tidak boleh di-hardcode**).
- **Pengelolaan Subkelompok:** Di dalam satu kategori tingkat, pengurus dapat membuat satu atau lebih subkelompok (contoh: *Madya A*, *Madya B*).
- **Penempatan Anggota:**
  - Pengurus dapat memindahkan anggota antar-subkelompok secara manual kapan saja.
  - **Fitur Bantuan Auto-Grouping (Opsional):** Pengurus dapat menekan tombol pembagian otomatis untuk membagi anggota dalam satu kategori tingkat secara merata ke subkelompok yang tersedia.
  - Jika anggota di kategori tersebut sudah memiliki subkelompok sebelumnya, sistem menampilkan dialog konfirmasi peringatan sebelum menimpa penempatan lama.
- **Kapasitas Kelompok:** Terdapat nilai kapasitas anjuran (default: **25 orang** per kelompok, dapat diubah di pengaturan). Jika jumlah anggota melebihi kapasitas, sistem hanya menampilkan teks/ikon informasi ringan tanpa memblokir penyimpanan atau memecah kelompok secara otomatis.

---

### Modul 4: Jadwal Latihan & Materi Tabuh
- **Jenis Jadwal Latihan:**
  1. **Latihan Rutin / Wajib:** Ditujukan untuk kategori tingkat atau subkelompok tertentu.
  2. **Latihan Khusus Event:** Ditujukan untuk persiapan acara tertentu (terhubung ke data Pertunjukan/Event dan daftar pemain yang dipilih pada acara tersebut).
- **Master Materi Tabuh:** Pengurus dan pelatih dapat mengelola daftar nama materi/repertoar tabuh yang dipelajari di sanggar.
- **Pencatatan Progres Materi:**
  - Setiap sesi jadwal latihan dapat diisi satu atau beberapa materi tabuh beserta **Catatan Progres Latihan** (contoh: *"Baru selesai bagian kawitan, pertemuan depan masuk pengawak"*).
  - Saat pelatih membuka halaman jadwal atau membuat jadwal baru untuk kelompok/materi yang sama, sistem menampilkan **info catatan progres dari pertemuan terakhir** agar latihan berikutnya langsung menyambung.
- **Salin Pengumuman WA:** Tersedia tombol **"Salin Pengumuman"** pada detail jadwal yang otomatis menyalin teks rapi berisi hari, tanggal, jam, kelompok, dan materi tabuh ke *clipboard* agar siap ditempel ke grup WhatsApp.

---

### Modul 5: Presensi Latihan
- **Pencatatan Kehadiran:** Pengurus atau pelatih memilih jadwal latihan yang berlangsung, lalu sistem menampilkan daftar anggota berstatus **Aktif** yang tergabung dalam kelompok/event jadwal tersebut.
- **Status Presensi:** Setiap anggota dicatat dengan salah satu status: **Hadir**, **Izin**, **Sakit**, atau **Tanpa Keterangan**.
- **Rekapitulasi:** Sistem menyediakan tampilan rekap jumlah dan persentase kehadiran per anggota dalam rentang waktu tertentu.

---

### Modul 6: Pertunjukan / Event
- **Data Acara:** Mencakup Nama Acara, Jenis Acara, Tanggal, Waktu, Lokasi, dan Catatan.
- **Jenis Acara (3 Pilihan):**
  1. **Pentas / Job**
  2. **Ngayah**
  3. **Ujian Kenaikan Tingkat**
- **Pembentukan Tim & Pemilihan Pemain:**
  - Satu acara dapat memiliki **satu atau lebih tim** (mendukung skenario memecah satu kelompok latihan menjadi dua tim untuk tampil di lokasi berbeda pada hari yang sama).
  - Pemilihan calon pemain bersifat fleksibel **lintas tingkat** (misalnya pemain *Inti* dapat dipilih untuk mendampingi tim *Madya*). Hanya anggota berstatus **Aktif** yang dapat dipilih.
- **Pencatatan Kesediaan Anggota:** Pengurus atau pelatih mencatat respons konfirmasi lisan setiap calon pemain dengan status: **Belum Ditanya**, **Bersedia**, atau **Tidak Bersedia**, sebelum menetapkan susunan pemain final.
- **Catatan Honor / Uang Job (Khusus Jenis Acara: Pentas / Job):**
  - Bagian ini **hanya tampil** apabila jenis acara yang dipilih adalah **Pentas / Job** (disembunyikan pada acara *Ngayah* dan *Ujian*).
  - Pengurus dapat mencatat nominal honor per pemain serta menandai status pembagiannya untuk setiap anggota yang ikut bermain (**Belum Dibagikan** atau **Sudah Dibagikan**).
- **Salin Pengumuman WA:** Tersedia tombol **"Salin Info Acara & Pemain"** untuk menyalin rincian jadwal acara beserta daftar pemain terpilih ke *clipboard*.

---

### Modul 7: Pencatatan Iuran Bulanan & Pengaturan Sanggar
- **Daftar Tagihan Bulanan Otomatis:** Ketika pengurus memilih bulan dan tahun tertentu, sistem menampilkan daftar seluruh anggota yang berstatus **Aktif** (anggota *Vakum* dan *Nonaktif* tidak ditampilkan).
- **Indikator Status Pembayaran (Teks + Warna):**
  1. **Putih / Abu-abu — Belum Bayar:** Anggota belum membayar pada periode awal bulan (tanggal `1` s.d. `Batas Kuning`).
  2. **Hijau — Lunas:** Anggota sudah membayar iuran bulan tersebut (mencatat tanggal bayar dan nominal).
  3. **Kuning — Mendekati Batas:** Anggota belum membayar dan tanggal berjalan sudah melewati `Batas Kuning` (default: tanggal **15**).
  4. **Merah — Menunggak:** Anggota belum membayar dan tanggal berjalan sudah melewati `Batas Merah` (default: tanggal **25**) atau sudah melewati bulan tagihan.
- **Pengaturan Sanggar:** Halaman konfigurasi bagi pengurus untuk mengubah:
  - Nama sanggar.
  - Nominal default iuran bulanan.
  - Tanggal ambang batas indikator Kuning (default: 15) dan Merah (default: 25).
  - Kapasitas anjuran anggota per kelompok (default: 25).

---

## 5. Kriteria Penerimaan Utama (*Acceptance Criteria*)

Rancangan sistem dinyatakan valid apabila memenuhi kondisi pengujian berikut:
1. **Isolasi Status Anggota:** Mengubah status anggota menjadi *Vakum* atau *Nonaktif* langsung menghilangkan nama anggota tersebut dari form presensi sesi baru dan daftar iuran bulan berikutnya, namun riwayat kehadirannya di bulan-bulan lalu tetap tampil di laporan riwayat.
2. **Fleksibilitas Tingkat & Auto-Grouping:** Pengurus dapat menghapus seluruh tingkat bawaan, membuat nama tingkat baru, membuat 3 subkelompok sekaligus, menjalankan pembagian otomatis secara merata, lalu memindahkan 1–2 anggota secara manual setelahnya.
3. **Kontinuitas Progres Tabuh:** Catatan progres yang diisi pada jadwal latihan minggu ini muncul sebagai referensi pada sesi latihan berikutnya untuk kelompok/materi yang bersangkutan.
4. **Multi-Tim & Honor Job:** Satu acara berjenis *Pentas / Job* dapat menampung 2 tim berbeda dengan pemain lintas tingkat, dan menampilkan kolom pencatatan status pembagian honor (*Sudah/Belum Dibagikan*). Jika jenis acara diubah menjadi *Ngayah*, kolom honor tidak muncul.
5. **Dinamika Warna Iuran:** Mengubah batas tanggal kuning/merah di menu Pengaturan langsung memperbarui tampilan indikator status warna pada anggota yang belum melunasi iuran bulan berjalan.

---

## 6. Spesifikasi & Batasan Teknis (Untuk Perancangan Arsitektur)

- **Lingkungan Pengembangan:** Java 25, dibangun menggunakan struktur proyek **Maven** (`pom.xml`) agar kompatibel penuh dengan VS Code.
- **Antarmuka Pengguna (UI):** **Java Swing** dengan tema **FlatLaf** (tampilan modern, bersih, menggunakan kombinasi teks dan warna untuk setiap indikator status).
- **Basis Data:** **MySQL** diakses menggunakan **JDBC** murni (tanpa ORM berat).
- **Penerapan Pemrograman Berorientasi Objek (PBO):**
  - Arsitektur dipisahkan secara jelas antara lapisan **UI (Swing Views)**, **Service (Business Logic)**, dan **DAO/Repository (Database Access)**.
  - Menerapkan prinsip PBO secara wajar dan kontekstual (*Encapsulation* pada model data, *Inheritance* dan *Polymorphism* pada hierarki peran pengguna `Pengurus` dan `Pelatih`, serta *Abstraction* pada kontrak akses data/layanan).