package com.pakem.model.enums;

/**
 * Jenis acara / pertunjukan dalam sanggar seni tabuh.
 * Sesuai DDL tabel event: ENUM('JOB', 'NGAYAH', 'UJIAN').
 */
public enum JenisAcara {
    JOB("Pentas / Job"),
    NGAYAH("Ngayah"),
    UJIAN("Ujian Kenaikan Tingkat");

    private final String label;

    JenisAcara(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static JenisAcara fromString(String str) {
        if (str == null) return null;
        try {
            return JenisAcara.valueOf(str.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return label;
    }
}
