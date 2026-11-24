package com.pakem.model.enums;

/**
 * Status kehadiran anggota dalam sesi latihan.
 * Kolom status_hadir pada tabel presensi: ENUM('HADIR', 'IZIN', 'SAKIT', 'TANPA_KETERANGAN').
 */
public enum StatusHadir {
    HADIR("Hadir"),
    IZIN("Izin"),
    SAKIT("Sakit"),
    TANPA_KETERANGAN("Tanpa Keterangan (Alpa)");

    private final String label;

    StatusHadir(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static StatusHadir fromString(String str) {
        if (str == null) return null;
        try {
            return StatusHadir.valueOf(str.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return label;
    }
}
