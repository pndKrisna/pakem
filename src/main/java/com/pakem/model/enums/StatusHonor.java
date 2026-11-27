package com.pakem.model.enums;

/**
 * Status pembagian uang honor acara (khusus acara JOB / Pentas).
 * Sesuai DDL tabel pemain_tim: ENUM('BELUM_DIBAGIKAN', 'SUDAH_DIBAGIKAN').
 */
public enum StatusHonor {
    BELUM_DIBAGIKAN("Belum Dibagikan"),
    SUDAH_DIBAGIKAN("Sudah Dibagikan");

    private final String label;

    StatusHonor(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static StatusHonor fromString(String str) {
        if (str == null) return null;
        try {
            return StatusHonor.valueOf(str.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return label;
    }
}
