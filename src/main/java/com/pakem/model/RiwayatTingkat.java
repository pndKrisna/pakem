package com.pakem.model;

import java.time.LocalDate;

/**
 * Model Riwayat Perubahan Tingkat Kemampuan Anggota.
 */
public class RiwayatTingkat {

    private int id;
    private int anggotaId;
    private Integer kategoriTingkatIdLama;
    private Integer kategoriTingkatIdBaru;
    private LocalDate tanggalPerubahan;
    private String keterangan;

    // Helper display fields
    private String namaAnggota;
    private String namaTingkatLama;
    private String namaTingkatBaru;

    public RiwayatTingkat() {
        this.tanggalPerubahan = LocalDate.now();
    }

    public RiwayatTingkat(int id, int anggotaId, Integer kategoriTingkatIdLama,
                          Integer kategoriTingkatIdBaru, LocalDate tanggalPerubahan, String keterangan) {
        this.id = id;
        this.anggotaId = anggotaId;
        this.kategoriTingkatIdLama = kategoriTingkatIdLama;
        this.kategoriTingkatIdBaru = kategoriTingkatIdBaru;
        this.tanggalPerubahan = tanggalPerubahan;
        this.keterangan = keterangan;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAnggotaId() {
        return anggotaId;
    }

    public void setAnggotaId(int anggotaId) {
        this.anggotaId = anggotaId;
    }

    public Integer getKategoriTingkatIdLama() {
        return kategoriTingkatIdLama;
    }

    public void setKategoriTingkatIdLama(Integer kategoriTingkatIdLama) {
        this.kategoriTingkatIdLama = kategoriTingkatIdLama;
    }

    public Integer getKategoriTingkatIdBaru() {
        return kategoriTingkatIdBaru;
    }

    public void setKategoriTingkatIdBaru(Integer kategoriTingkatIdBaru) {
        this.kategoriTingkatIdBaru = kategoriTingkatIdBaru;
    }

    public LocalDate getTanggalPerubahan() {
        return tanggalPerubahan;
    }

    public void setTanggalPerubahan(LocalDate tanggalPerubahan) {
        this.tanggalPerubahan = tanggalPerubahan;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getNamaAnggota() {
        return namaAnggota;
    }

    public void setNamaAnggota(String namaAnggota) {
        this.namaAnggota = namaAnggota;
    }

    public String getNamaTingkatLama() {
        return (namaTingkatLama != null && !namaTingkatLama.isEmpty()) ? namaTingkatLama : "(Belum Ada / Awal)";
    }

    public void setNamaTingkatLama(String namaTingkatLama) {
        this.namaTingkatLama = namaTingkatLama;
    }

    public String getNamaTingkatBaru() {
        return (namaTingkatBaru != null && !namaTingkatBaru.isEmpty()) ? namaTingkatBaru : "-";
    }

    public void setNamaTingkatBaru(String namaTingkatBaru) {
        this.namaTingkatBaru = namaTingkatBaru;
    }
}
