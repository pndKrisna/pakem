package com.pakem.model;

/**
 * Data Transfer Object (DTO) untuk Rekapitulasi Presensi Kehadiran Anggota.
 * Menerapkan prinsip Encapsulation.
 */
public class RekapPresensi {

    private int anggotaId;
    private String namaAnggota;
    private String nomorKontak;
    private String namaKategori;
    private String namaSubkelompok;

    private int totalPertemuan;
    private int jumlahHadir;
    private int jumlahIzin;
    private int jumlahSakit;
    private int jumlahAlpa;

    public RekapPresensi() {
    }

    public RekapPresensi(int anggotaId, String namaAnggota, String nomorKontak,
                         String namaKategori, String namaSubkelompok,
                         int totalPertemuan, int jumlahHadir, int jumlahIzin,
                         int jumlahSakit, int jumlahAlpa) {
        this.anggotaId = anggotaId;
        this.namaAnggota = namaAnggota;
        this.nomorKontak = nomorKontak;
        this.namaKategori = namaKategori;
        this.namaSubkelompok = namaSubkelompok;
        this.totalPertemuan = totalPertemuan;
        this.jumlahHadir = jumlahHadir;
        this.jumlahIzin = jumlahIzin;
        this.jumlahSakit = jumlahSakit;
        this.jumlahAlpa = jumlahAlpa;
    }

    public int getAnggotaId() {
        return anggotaId;
    }

    public void setAnggotaId(int anggotaId) {
        this.anggotaId = anggotaId;
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

    public int getTotalPertemuan() {
        return totalPertemuan;
    }

    public void setTotalPertemuan(int totalPertemuan) {
        this.totalPertemuan = totalPertemuan;
    }

    public int getJumlahHadir() {
        return jumlahHadir;
    }

    public void setJumlahHadir(int jumlahHadir) {
        this.jumlahHadir = jumlahHadir;
    }

    public int getJumlahIzin() {
        return jumlahIzin;
    }

    public void setJumlahIzin(int jumlahIzin) {
        this.jumlahIzin = jumlahIzin;
    }

    public int getJumlahSakit() {
        return jumlahSakit;
    }

    public void setJumlahSakit(int jumlahSakit) {
        this.jumlahSakit = jumlahSakit;
    }

    public int getJumlahAlpa() {
        return jumlahAlpa;
    }

    public void setJumlahAlpa(int jumlahAlpa) {
        this.jumlahAlpa = jumlahAlpa;
    }

    /**
     * Menghitung persentase kehadiran:
     * (jumlahHadir / totalPertemuan) * 100%
     */
    public double getPersentaseKehadiran() {
        if (totalPertemuan <= 0) return 0.0;
        return (jumlahHadir * 100.0) / totalPertemuan;
    }

    public String getFormattedPersentase() {
        return String.format("%.1f%%", getPersentaseKehadiran());
    }
}
