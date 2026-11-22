package com.pakem.model;

/**
 * Model Master Kategori Tingkat (Kemampuan Tabuh).
 */
public class KategoriTingkat {

    private int id;
    private String nama;
    private int urutan;

    public KategoriTingkat() {
    }

    public KategoriTingkat(int id, String nama, int urutan) {
        this.id = id;
        this.nama = nama;
        this.urutan = urutan;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public int getUrutan() {
        return urutan;
    }

    public void setUrutan(int urutan) {
        this.urutan = urutan;
    }

    @Override
    public String toString() {
        return nama;
    }
}
