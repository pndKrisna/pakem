package com.pakem.model;

import com.pakem.model.enums.RoleUser;

/**
 * Representasi Pelatih Sanggar.
 * Berfokus pada kegiatan operasional latihan, materi tabuh, presensi,
 * dan konfirmasi kesediaan pemain di acara. Tidak memiliki akses ke modul keuangan & pengaturan.
 */
public class Pelatih extends User {

    public Pelatih() {
        setRole(RoleUser.PELATIH);
    }

    public Pelatih(int id, String username, String passwordHash, boolean isActive) {
        super(id, username, passwordHash, RoleUser.PELATIH, isActive);
    }

    @Override
    public boolean canEditAnggota() {
        return false; // Hanya dapat melihat detail anggota
    }

    @Override
    public boolean canUpdateTingkatAnggota() {
        return true; // Diizinkan melakukan usulan / update tingkat kemampuan anggota
    }

    @Override
    public boolean canManageKategoriTingkat() {
        return false; // Hanya dapat melihat struktur kategori & subkelompok
    }

    @Override
    public boolean canManageJadwalLatihan() {
        return true; // Hak penuh jadwal latihan
    }

    @Override
    public boolean canManageMateriTabuh() {
        return true; // Hak penuh master materi & progres materi tabuh
    }

    @Override
    public boolean canRecordPresensi() {
        return true; // Hak penuh presensi latihan
    }

    @Override
    public boolean canManageEvent() {
        return false; // Hanya dapat melihat daftar acara dan susunan tim
    }

    @Override
    public boolean canRecordKesediaanPemain() {
        return true; // Diizinkan mencatat respons konfirmasi lisan calon pemain
    }

    @Override
    public boolean canAccessHonor() {
        return false; // Tidak memiliki akses data honor
    }

    @Override
    public boolean canAccessIuran() {
        return false; // Tidak memiliki akses modul iuran bulanan
    }

    @Override
    public boolean canAccessPengaturan() {
        return false; // Tidak memiliki akses ke konfigurasi sanggar
    }
}
