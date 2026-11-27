package com.pakem.model.enums;

/**
 * Status pembayaran iuran bulanan yang tersimpan di database.
 */
public enum StatusBayar {
    BELUM_BAYAR("Belum Bayar"),
    LUNAS("Lunas");

    private final String label;

    StatusBayar(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static StatusBayar fromString(String str) {
        if (str == null) return null;
        try {
            return StatusBayar.valueOf(str.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
