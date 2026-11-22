package com.pakem.model.enums;

/**
 * Status keanggotaan dalam sanggar seni Pakem.
 * 1. AKTIF: Anggota aktif rutin berkegiatan, otomatis muncul di presensi, iuran, calon pemain event.
 * 2. VAKUM: Cuti sementara, disembunyikan dari presensi baru dan tagihan iuran.
 * 3. NONAKTIF: Berhenti permanen, disembunyikan dari kegiatan baru, riwayat tetap diarsipkan.
 */
public enum StatusAnggota {
    AKTIF("Aktif"),
    VAKUM("Vakum (Cuti)"),
    NONAKTIF("Nonaktif (Keluar)");

    private final String label;

    StatusAnggota(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static StatusAnggota fromString(String str) {
        if (str == null) return null;
        try {
            return StatusAnggota.valueOf(str.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
