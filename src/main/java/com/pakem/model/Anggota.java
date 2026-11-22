package com.pakem.model;

import com.pakem.model.enums.StatusAnggota;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Model Data Anggota Sanggar Pakem.
 * Menerapkan prinsip Encapsulation.
 */
public class Anggota {

    private int id;
    private String namaLengkap;
    private String nomorKontak;
    private LocalDate tanggalBergabung;
    private StatusAnggota status;
    private Integer kategoriTingkatId;
    private Integer subkelompokId;
    private String catatan;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Helper display fields (hasil join SQL)
    private String namaKategori;
    private String namaSubkelompok;

    public Anggota() {
        this.status = StatusAnggota.AKTIF;
        this.tanggalBergabung = LocalDate.now();
    }

    public Anggota(int id, String namaLengkap, String nomorKontak, LocalDate tanggalBergabung,
                   StatusAnggota status, Integer kategoriTingkatId, Integer subkelompokId, String catatan) {
        this.id = id;
        this.namaLengkap = namaLengkap;
        this.nomorKontak = nomorKontak;
        this.tanggalBergabung = tanggalBergabung;
        this.status = status;
        this.kategoriTingkatId = kategoriTingkatId;
        this.subkelompokId = subkelompokId;
        this.catatan = catatan;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNamaLengkap() {
        return namaLengkap;
    }

    public void setNamaLengkap(String namaLengkap) {
        this.namaLengkap = namaLengkap;
    }

    public String getNomorKontak() {
        return nomorKontak;
    }

    public void setNomorKontak(String nomorKontak) {
        this.nomorKontak = nomorKontak;
    }

    public LocalDate getTanggalBergabung() {
        return tanggalBergabung;
    }

    public void setTanggalBergabung(LocalDate tanggalBergabung) {
        this.tanggalBergabung = tanggalBergabung;
    }

    public StatusAnggota getStatus() {
        return status;
    }

    public void setStatus(StatusAnggota status) {
        this.status = status;
    }

    public Integer getKategoriTingkatId() {
        return kategoriTingkatId;
    }

    public void setKategoriTingkatId(Integer kategoriTingkatId) {
        this.kategoriTingkatId = kategoriTingkatId;
    }

    public Integer getSubkelompokId() {
        return subkelompokId;
    }

    public void setSubkelompokId(Integer subkelompokId) {
        this.subkelompokId = subkelompokId;
    }

    public String getCatatan() {
        return catatan;
    }

    public void setCatatan(String catatan) {
        this.catatan = catatan;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getNamaKategori() {
        return (namaKategori != null && !namaKategori.isEmpty()) ? namaKategori : "-";
    }

    public void setNamaKategori(String namaKategori) {
        this.namaKategori = namaKategori;
    }

    public String getNamaSubkelompok() {
        return (namaSubkelompok != null && !namaSubkelompok.isEmpty()) ? namaSubkelompok : "-";
    }

    public void setNamaSubkelompok(String namaSubkelompok) {
        this.namaSubkelompok = namaSubkelompok;
    }

    @Override
    public String toString() {
        return namaLengkap;
    }
}
