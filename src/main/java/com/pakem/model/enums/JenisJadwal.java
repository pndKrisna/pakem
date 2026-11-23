package com.pakem.model.enums;

/**
 * Jenis jadwal latihan sesuai PRD Modul 4.
 */
public enum JenisJadwal {
    RUTIN("Latihan Rutin / Wajib"),
    KHUSUS_EVENT("Latihan Khusus Event");

    private final String label;

    JenisJadwal(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static JenisJadwal fromString(String jenisStr) {
        if (jenisStr == null) return null;
        try {
            return JenisJadwal.valueOf(jenisStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
