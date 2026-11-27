package com.pakem.model.enums;

/**
 * Status kesediaan calon pemain untuk tampil pada acara.
 * Sesuai DDL tabel pemain_tim: ENUM('BELUM_DITANYA', 'BERSEDIA', 'TIDAK_BERSEDIA').
 */
public enum StatusKesediaan {
    BELUM_DITANYA("Belum Ditanya"),
    BERSEDIA("Bersedia"),
    TIDAK_BERSEDIA("Tidak Bersedia");

    private final String label;

    StatusKesediaan(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static StatusKesediaan fromString(String str) {
        if (str == null) return null;
        try {
            return StatusKesediaan.valueOf(str.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return label;
    }
}
