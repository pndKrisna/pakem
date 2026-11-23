package com.pakem.model;

import com.pakem.model.enums.JenisJadwal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

/**
 * Model Jadwal Latihan (Rutin atau Khusus Event).
 * Menerapkan prinsip Encapsulation.
 */
public class JadwalLatihan {

    private int id;
    private JenisJadwal jenisJadwal;
    private LocalDate tanggal;
    private LocalTime jamMulai;
    private LocalTime jamSelesai;
    private String lokasi;
    private Integer kategoriTingkatId;
    private Integer subkelompokId;
    private Integer eventId;
    private String catatan;

    // Helper display fields (hasil join SQL)
    private String namaKategori;
    private String namaSubkelompok;
    private String namaEvent;

    public JadwalLatihan() {
        this.tanggal = java.time.LocalDate.now();
    }

    public JadwalLatihan(int id, JenisJadwal jenisJadwal, LocalDate tanggal,
                         LocalTime jamMulai, LocalTime jamSelesai, String lokasi,
                         Integer kategoriTingkatId, Integer subkelompokId, Integer eventId,
                         String catatan) {
        this.id = id;
        this.jenisJadwal = jenisJadwal;
        this.tanggal = tanggal;
        this.jamMulai = jamMulai;
        this.jamSelesai = jamSelesai;
        this.lokasi = lokasi;
        this.kategoriTingkatId = kategoriTingkatId;
        this.subkelompokId = subkelompokId;
        this.eventId = eventId;
        this.catatan = catatan;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public JenisJadwal getJenisJadwal() {
        return jenisJadwal;
    }

    public void setJenisJadwal(JenisJadwal jenisJadwal) {
        this.jenisJadwal = jenisJadwal;
    }

    public LocalDate getTanggal() {
        return tanggal;
    }

    public void setTanggal(LocalDate tanggal) {
        this.tanggal = tanggal;
    }

    public LocalTime getJamMulai() {
        return jamMulai;
    }

    public void setJamMulai(LocalTime jamMulai) {
        this.jamMulai = jamMulai;
    }

    public LocalTime getJamSelesai() {
        return jamSelesai;
    }

    public void setJamSelesai(LocalTime jamSelesai) {
        this.jamSelesai = jamSelesai;
    }

    public String getLokasi() {
        return lokasi;
    }

    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
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

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public String getCatatan() {
        return catatan;
    }

    public void setCatatan(String catatan) {
        this.catatan = catatan;
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

    public String getNamaEvent() {
        return (namaEvent != null && !namaEvent.isEmpty()) ? namaEvent : "-";
    }

    public void setNamaEvent(String namaEvent) {
        this.namaEvent = namaEvent;
    }

    @Override
    public String toString() {
        return (namaKategori != null ? namaKategori : "") +
               (namaSubkelompok != null ? " / " + namaSubkelompok : "") +
               " - " + tanggal;
    }
}
