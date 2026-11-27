package com.pakem.model;

import com.pakem.model.enums.JenisAcara;
import com.pakem.model.enums.StatusHonor;
import com.pakem.model.enums.StatusKesediaan;

import java.math.BigDecimal;

/**
 * Model Pemain Terpilih dalam Tim Event (Fase 6).
 * Menerapkan prinsip Encapsulation.
 * Kolom tabel pemain_tim: id, tim_event_id, anggota_id, status_kesediaan, nominal_honor, status_honor.
 */
public class PemainTim {

    private int id;
    private int timEventId;
    private int anggotaId;
    private StatusKesediaan statusKesediaan;
    private BigDecimal nominalHonor;
    private StatusHonor statusHonor;

    // Helper display fields (hasil join SQL ke anggota & event)
    private String namaAnggota;
    private String nomorKontak;
    private String namaKategori;
    private String namaSubkelompok;
    private String namaTim;
    private String namaAcara;
    private JenisAcara jenisAcara;

    public PemainTim() {
        this.statusKesediaan = StatusKesediaan.BELUM_DITANYA;
    }

    public PemainTim(int id, int timEventId, int anggotaId, StatusKesediaan statusKesediaan,
                     BigDecimal nominalHonor, StatusHonor statusHonor) {
        this.id = id;
        this.timEventId = timEventId;
        this.anggotaId = anggotaId;
        this.statusKesediaan = statusKesediaan != null ? statusKesediaan : StatusKesediaan.BELUM_DITANYA;
        this.nominalHonor = nominalHonor;
        this.statusHonor = statusHonor;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTimEventId() {
        return timEventId;
    }

    public void setTimEventId(int timEventId) {
        this.timEventId = timEventId;
    }

    public int getAnggotaId() {
        return anggotaId;
    }

    public void setAnggotaId(int anggotaId) {
        this.anggotaId = anggotaId;
    }

    public StatusKesediaan getStatusKesediaan() {
        return statusKesediaan;
    }

    public void setStatusKesediaan(StatusKesediaan statusKesediaan) {
        this.statusKesediaan = statusKesediaan;
    }

    public BigDecimal getNominalHonor() {
        return nominalHonor;
    }

    public void setNominalHonor(BigDecimal nominalHonor) {
        this.nominalHonor = nominalHonor;
    }

    public StatusHonor getStatusHonor() {
        return statusHonor;
    }

    public void setStatusHonor(StatusHonor statusHonor) {
        this.statusHonor = statusHonor;
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

    public String getNamaTim() {
        return (namaTim != null && !namaTim.isEmpty()) ? namaTim : "-";
    }

    public void setNamaTim(String namaTim) {
        this.namaTim = namaTim;
    }

    public String getNamaAcara() {
        return (namaAcara != null && !namaAcara.isEmpty()) ? namaAcara : "-";
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

    @Override
    public String toString() {
        return namaAnggota + " (" + statusKesediaan.getLabel() + ")";
    }
}
