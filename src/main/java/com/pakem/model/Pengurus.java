package com.pakem.model;

import com.pakem.model.enums.RoleUser;

/**
 * Representasi Pengurus / Admin Sanggar.
 * Memiliki hak akses penuh terhadap seluruh modul administrasi dan keuangan.
 */
public class Pengurus extends User {

    public Pengurus() {
        setRole(RoleUser.PENGURUS);
    }

    public Pengurus(int id, String username, String passwordHash, boolean isActive) {
        super(id, username, passwordHash, RoleUser.PENGURUS, isActive);
    }

    @Override
    public boolean canEditAnggota() {
        return true;
    }

    @Override
    public boolean canUpdateTingkatAnggota() {
        return true;
    }

    @Override
    public boolean canManageKategoriTingkat() {
        return true;
    }

    @Override
    public boolean canManageJadwalLatihan() {
        return true;
    }

    @Override
    public boolean canManageMateriTabuh() {
        return true;
    }

    @Override
    public boolean canRecordPresensi() {
        return true;
    }

    @Override
    public boolean canManageEvent() {
        return true;
    }

    @Override
    public boolean canRecordKesediaanPemain() {
        return true;
    }

    @Override
    public boolean canAccessHonor() {
        return true;
    }

    @Override
    public boolean canAccessIuran() {
        return true;
    }

    @Override
    public boolean canAccessPengaturan() {
        return true;
    }
}
