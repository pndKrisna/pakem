package com.pakem.model;

import com.pakem.model.enums.StatusHadir;
import java.time.LocalDate;

/**
 * Model Presensi (Kehadiran Anggota per Sesi Jadwal Latihan).
 * Menerapkan prinsip Encapsulation.
 * Kolom tabel presensi: id, jadwal_latihan_id, anggota_id, status_hadir, keterangan.
 */
public class Presensi {

    private int id;
    private int jadwalLatihanId;
    private int anggotaId;
    private StatusHadir statusHadir;
    private String keterangan;

    // Helper display fields (hasil join SQL ke anggota & jadwal_latihan)
    private String namaAnggota;
    private String nomorKontak;
    private String namaKategori;
    private String namaSubkelompok;
    private LocalDate tanggalJadwal;
    private String jenisJadwal;

    public Presensi() {
        this.statusHadir = StatusHadir.HADIR;
    }

    public Presensi(int id, int jadwalLatihanId, int anggotaId, StatusHadir statusHadir, String keterangan) {
        this.id = id;
        this.jadwalLatihanId = jadwalLatihanId;
        this.anggotaId = anggotaId;
        this.statusHadir = statusHadir != null ? statusHadir : StatusHadir.HADIR;
        this.keterangan = keterangan;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getJadwalLatihanId() {
        return jadwalLatihanId;
    }

    public void setJadwalLatihanId(int jadwalLatihanId) {
        this.jadwalLatihanId = jadwalLatihanId;
    }

    public int getAnggotaId() {
        return anggotaId;
    }

    public void setAnggotaId(int anggotaId) {
        this.anggotaId = anggotaId;
    }

    public StatusHadir getStatusHadir() {
        return statusHadir;
    }

    public void setStatusHadir(StatusHadir statusHadir) {
        this.statusHadir = statusHadir;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getNamaAnggota() {
        return (namaAnggota != null && !namaAnggota.isEmpty()) ? namaAnggota : "-";
    }

    public void setNamaAnggota(String namaAnggota) {
        this.namaAnggota = namaAnggota;
    }

    public String getNomorKontak() {
        return (nomorKontak != null && !nomorKontak.isEmpty()) ? nomorKontak : "-";
    }

    public void setNomorKontak(String nomorKontak) {
        this.nomorKontak = nomorKontak;
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

    public LocalDate getTanggalJadwal() {
        return tanggalJadwal;
    }

    public void setTanggalJadwal(LocalDate tanggalJadwal) {
        this.tanggalJadwal = tanggalJadwal;
    }

    public String getJenisJadwal() {
        return (jenisJadwal != null && !jenisJadwal.isEmpty()) ? jenisJadwal : "-";
    }

    public void setJenisJadwal(String jenisJadwal) {
        this.jenisJadwal = jenisJadwal;
    }

    @Override
    public String toString() {
        return "Presensi{" +
                "id=" + id +
                ", jadwalLatihanId=" + jadwalLatihanId +
                ", anggotaId=" + anggotaId +
                ", statusHadir=" + statusHadir +
                ", namaAnggota='" + namaAnggota + '\'' +
                '}';
    }
}
