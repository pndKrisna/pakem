package com.pakem.model;

import com.pakem.model.enums.StatusBayar;
import com.pakem.model.enums.StatusIuranWarna;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Model untuk merepresentasikan data iuran bulanan anggota.
 */
public class Iuran {
    private int id;
    private int anggotaId;
    private int bulan;
    private int tahun;
    private BigDecimal nominal;
    private LocalDate tanggalBayar;
    private StatusBayar statusBayar;

    // Helper fields untuk join/UI
    private String namaAnggota;
    private String nomorKontak;
    private String kategoriTingkat;
    private String subkelompok;
    private StatusIuranWarna indikatorWarna;

    public Iuran() {
        this.statusBayar = StatusBayar.BELUM_BAYAR;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getAnggotaId() { return anggotaId; }
    public void setAnggotaId(int anggotaId) { this.anggotaId = anggotaId; }

    public int getBulan() { return bulan; }
    public void setBulan(int bulan) { this.bulan = bulan; }

    public int getTahun() { return tahun; }
    public void setTahun(int tahun) { this.tahun = tahun; }

    public BigDecimal getNominal() { return nominal; }
    public void setNominal(BigDecimal nominal) { this.nominal = nominal; }

    public LocalDate getTanggalBayar() { return tanggalBayar; }
    public void setTanggalBayar(LocalDate tanggalBayar) { this.tanggalBayar = tanggalBayar; }

    public StatusBayar getStatusBayar() { return statusBayar; }
    public void setStatusBayar(StatusBayar statusBayar) { this.statusBayar = statusBayar; }

    public String getNamaAnggota() { return namaAnggota; }
    public void setNamaAnggota(String namaAnggota) { this.namaAnggota = namaAnggota; }

    public String getNomorKontak() { return nomorKontak; }
    public void setNomorKontak(String nomorKontak) { this.nomorKontak = nomorKontak; }

    public String getKategoriTingkat() { return kategoriTingkat; }
    public void setKategoriTingkat(String kategoriTingkat) { this.kategoriTingkat = kategoriTingkat; }

    public String getSubkelompok() { return subkelompok; }
    public void setSubkelompok(String subkelompok) { this.subkelompok = subkelompok; }

    public StatusIuranWarna getIndikatorWarna() { return indikatorWarna; }
    public void setIndikatorWarna(StatusIuranWarna indikatorWarna) { this.indikatorWarna = indikatorWarna; }
}
