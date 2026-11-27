package com.pakem.model.enums;

/**
 * Status indikator warna iuran berdasarkan aturan bisnis dan tanggal berjalan.
 */
public enum StatusIuranWarna {
    PUTIH("Belum Bayar"),
    HIJAU("Lunas"),
    KUNING("Mendekati Batas"),
    MERAH("Menunggak");

    private final String label;

    StatusIuranWarna(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
