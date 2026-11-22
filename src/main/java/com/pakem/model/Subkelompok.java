package com.pakem.model;

/**
 * Model Subkelompok di dalam Kategori Tingkat.
 */
public class Subkelompok {

    private int id;
    private int kategoriTingkatId;
    private String nama;
    private String namaKategori;

    public Subkelompok() {
    }

    public Subkelompok(int id, int kategoriTingkatId, String nama) {
        this.id = id;
        this.kategoriTingkatId = kategoriTingkatId;
        this.nama = nama;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getKategoriTingkatId() {
        return kategoriTingkatId;
    }

    public void setKategoriTingkatId(int kategoriTingkatId) {
        this.kategoriTingkatId = kategoriTingkatId;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNamaKategori() {
        return namaKategori;
    }

    public void setNamaKategori(String namaKategori) {
        this.namaKategori = namaKategori;
    }

    @Override
    public String toString() {
        return nama;
    }
}
