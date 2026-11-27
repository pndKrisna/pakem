package com.pakem.model;

import com.pakem.model.enums.JenisAcara;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Model Data Pertunjukan / Acara Sanggar (Fase 6).
 * Menerapkan prinsip Encapsulation.
 * Kolom tabel event: id, nama_acara, jenis_acara, tanggal, waktu, lokasi, catatan.
 */
public class Event {

    private int id;
    private String namaAcara;
    private JenisAcara jenisAcara;
    private LocalDate tanggal;
    private LocalTime waktu;
    private String lokasi;
    private String catatan;

    // Helper display fields (hasil agregasi/join)
    private int jumlahTim;
    private int totalPemain;

    public Event() {
        this.jenisAcara = JenisAcara.JOB;
        this.tanggal = LocalDate.now();
    }

    public Event(int id, String namaAcara, JenisAcara jenisAcara, LocalDate tanggal,
                 LocalTime waktu, String lokasi, String catatan) {
        this.id = id;
        this.namaAcara = namaAcara;
        this.jenisAcara = jenisAcara != null ? jenisAcara : JenisAcara.JOB;
        this.tanggal = tanggal;
        this.waktu = waktu;
        this.lokasi = lokasi;
        this.catatan = catatan;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNamaAcara() {
        return namaAcara;
    }

    public void setNamaAcara(String namaAcara) {
        this.namaAcara = namaAcara;
    }

    public JenisAcara getJenisAcara() {
        return jenisAcara;
    }

    public void setJenisAcara(JenisAcara jenisAcara) {
        this.jenisAcara = jenisAcara;
    }

    public LocalDate getTanggal() {
        return tanggal;
    }

    public void setTanggal(LocalDate tanggal) {
        this.tanggal = tanggal;
    }

    public LocalTime getWaktu() {
        return waktu;
    }

    public void setWaktu(LocalTime waktu) {
        this.waktu = waktu;
    }

    public String getLokasi() {
        return lokasi;
    }

    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }

    public String getCatatan() {
        return catatan;
    }

    public void setCatatan(String catatan) {
        this.catatan = catatan;
    }

    public int getJumlahTim() {
        return jumlahTim;
    }

    public void setJumlahTim(int jumlahTim) {
        this.jumlahTim = jumlahTim;
    }

    public int getTotalPemain() {
        return totalPemain;
    }

    public void setTotalPemain(int totalPemain) {
        this.totalPemain = totalPemain;
    }

    @Override
    public String toString() {
        return namaAcara + " (" + (tanggal != null ? tanggal.toString() : "-") + ")";
    }
}
