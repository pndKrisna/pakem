package com.pakem.model;

/**
 * Model Master Materi Tabuh (Repertoar / Gending).
 * Menerapkan prinsip Encapsulation.
 */
public class MateriTabuh {

    private int id;
    private String nama;
    private String deskripsi;

    public MateriTabuh() {
    }

    public MateriTabuh(int id, String nama, String deskripsi) {
        this.id = id;
        this.nama = nama;
        this.deskripsi = deskripsi;
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

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    @Override
    public String toString() {
        return nama;
    }
}
