package com.pakem.model;

/**
 * DTO untuk menampung seluruh data ringkasan dashboard (Fase 8).
 */
public class DashboardSummary {

    private int totalAnggota;
    private int jumlahAktif;
    private int jumlahVakum;
    private int jumlahNonaktif;
    private int totalJadwalTerdekat;
    private int totalEventTerdekat;
    private double persentaseKehadiranBulanIni;
    private int iuranLunas;
    private int iuranBelumBayar;
    private int iuranTotalAktif;

    // Getter / Setter
    public int getTotalAnggota() { return totalAnggota; }
    public void setTotalAnggota(int totalAnggota) { this.totalAnggota = totalAnggota; }

    public int getJumlahAktif() { return jumlahAktif; }
    public void setJumlahAktif(int jumlahAktif) { this.jumlahAktif = jumlahAktif; }

    public int getJumlahVakum() { return jumlahVakum; }
    public void setJumlahVakum(int jumlahVakum) { this.jumlahVakum = jumlahVakum; }

    public int getJumlahNonaktif() { return jumlahNonaktif; }
    public void setJumlahNonaktif(int jumlahNonaktif) { this.jumlahNonaktif = jumlahNonaktif; }

    public int getTotalJadwalTerdekat() { return totalJadwalTerdekat; }
    public void setTotalJadwalTerdekat(int totalJadwalTerdekat) { this.totalJadwalTerdekat = totalJadwalTerdekat; }

    public int getTotalEventTerdekat() { return totalEventTerdekat; }
    public void setTotalEventTerdekat(int totalEventTerdekat) { this.totalEventTerdekat = totalEventTerdekat; }

    public double getPersentaseKehadiranBulanIni() { return persentaseKehadiranBulanIni; }
    public void setPersentaseKehadiranBulanIni(double persentaseKehadiranBulanIni) { this.persentaseKehadiranBulanIni = persentaseKehadiranBulanIni; }

    public int getIuranLunas() { return iuranLunas; }
    public void setIuranLunas(int iuranLunas) { this.iuranLunas = iuranLunas; }

    public int getIuranBelumBayar() { return iuranBelumBayar; }
    public void setIuranBelumBayar(int iuranBelumBayar) { this.iuranBelumBayar = iuranBelumBayar; }

    public int getIuranTotalAktif() { return iuranTotalAktif; }
    public void setIuranTotalAktif(int iuranTotalAktif) { this.iuranTotalAktif = iuranTotalAktif; }

    public double getPersentaseIuranLunas() {
        if (iuranTotalAktif == 0) return 0.0;
        return (double) iuranLunas / iuranTotalAktif * 100.0;
    }
}
