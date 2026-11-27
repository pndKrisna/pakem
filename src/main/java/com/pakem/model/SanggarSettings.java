package com.pakem.model;

import java.math.BigDecimal;

/**
 * DTO untuk menampung seluruh pengaturan sanggar dari tabel settings.
 */
public class SanggarSettings {
    private String namaSanggar;
    private BigDecimal iuranDefault;
    private int batasKuning;
    private int batasMerah;
    private int kapasitasKelompok;

    public SanggarSettings() {}

    public String getNamaSanggar() { return namaSanggar; }
    public void setNamaSanggar(String namaSanggar) { this.namaSanggar = namaSanggar; }

    public BigDecimal getIuranDefault() { return iuranDefault; }
    public void setIuranDefault(BigDecimal iuranDefault) { this.iuranDefault = iuranDefault; }

    public int getBatasKuning() { return batasKuning; }
    public void setBatasKuning(int batasKuning) { this.batasKuning = batasKuning; }

    public int getBatasMerah() { return batasMerah; }
    public void setBatasMerah(int batasMerah) { this.batasMerah = batasMerah; }

    public int getKapasitasKelompok() { return kapasitasKelompok; }
    public void setKapasitasKelompok(int kapasitasKelompok) { this.kapasitasKelompok = kapasitasKelompok; }
}
