package com.pakem.model;

import java.time.LocalDateTime;

/**
 * Model Materi yang Dikerjakan + Catatan Progres per Sesi Latihan.
 * Menerapkan prinsip Encapsulation.
 */
public class SesiMateri {

    private int id;
    private int jadwalLatihanId;
    private int materiTabuhId;
    private String catatanProgres;

    // Helper display fields (hasil join SQL)
    private String namaMateri;
    private String tanggalJadwal;

    public SesiMateri() {
    }

    public SesiMateri(int id, int jadwalLatihanId, int materiTabuhId, String catatanProgres) {
        this.id = id;
        this.jadwalLatihanId = jadwalLatihanId;
        this.materiTabuhId = materiTabuhId;
        this.catatanProgres = catatanProgres;
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

    public int getMateriTabuhId() {
        return materiTabuhId;
    }

    public void setMateriTabuhId(int materiTabuhId) {
        this.materiTabuhId = materiTabuhId;
    }

    public String getCatatanProgres() {
        return catatanProgres;
    }

    public void setCatatanProgres(String catatanProgres) {
        this.catatanProgres = catatanProgres;
    }

    public String getNamaMateri() {
        return (namaMateri != null && !namaMateri.isEmpty()) ? namaMateri : "-";
    }

    public void setNamaMateri(String namaMateri) {
        this.namaMateri = namaMateri;
    }

    public String getTanggalJadwal() {
        return (tanggalJadwal != null && !tanggalJadwal.isEmpty()) ? tanggalJadwal : "-";
    }

    public void setTanggalJadwal(String tanggalJadwal) {
        this.tanggalJadwal = tanggalJadwal;
    }

    @Override
    public String toString() {
        return namaMateri + " (" + tanggalJadwal + ")";
    }
}
