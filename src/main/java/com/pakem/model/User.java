package com.pakem.model;

import com.pakem.model.enums.RoleUser;

/**
 * Kelas abstrak dasar pengguna sistem Pakem.
 * Menerapkan prinsip Encapsulation dan dasar Polymorphism untuk hak akses peran.
 */
public abstract class User {

    private int id;
    private String username;
    private String passwordHash;
    private RoleUser role;
    private boolean isActive;

    public User() {
    }

    public User(int id, String username, String passwordHash, RoleUser role, boolean isActive) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.isActive = isActive;
    }

    /**
     * Factory method polimorfik untuk membuat instance Pengurus atau Pelatih
     * sesuai dengan nilai enum RoleUser.
     */
    public static User create(int id, String username, String passwordHash, RoleUser role, boolean isActive) {
        if (role == RoleUser.PENGURUS) {
            return new Pengurus(id, username, passwordHash, isActive);
        } else if (role == RoleUser.PELATIH) {
            return new Pelatih(id, username, passwordHash, isActive);
        }
        throw new IllegalArgumentException("Peran pengguna tidak dikenali: " + role);
    }

    // ==========================================
    // Kontrak Hak Akses Polimorfik (Polymorphism)
    // ==========================================

    /** Hak akses mengelola data anggota (tambah, ubah identitas, ubah status keaktifan). */
    public abstract boolean canEditAnggota();

    /** Hak akses mengubah atau mengusulkan kategori tingkat anggota. */
    public abstract boolean canUpdateTingkatAnggota();

    /** Hak akses mengelola master kategori tingkat dan subkelompok (tambah/hapus). */
    public abstract boolean canManageKategoriTingkat();

    /** Hak akses mengelola jadwal latihan rutin dan event. */
    public abstract boolean canManageJadwalLatihan();

    /** Hak akses mengelola master materi tabuh dan catatan progres materi. */
    public abstract boolean canManageMateriTabuh();

    /** Hak akses mencatat dan mengedit presensi latihan. */
    public abstract boolean canRecordPresensi();

    /** Hak akses mengelola acara/event dan susunan tim. */
    public abstract boolean canManageEvent();

    /** Hak akses mencatat status kesediaan pemain di event. */
    public abstract boolean canRecordKesediaanPemain();

    /** Hak akses melihat, mencatat, dan mengubah honor pertunjukan (Job). */
    public abstract boolean canAccessHonor();

    /** Hak akses melihat dan mengelola iuran bulanan sanggar. */
    public abstract boolean canAccessIuran();

    /** Hak akses membuka dan mengubah konfigurasi pengaturan sanggar. */
    public abstract boolean canAccessPengaturan();

    // ==========================================
    // Getter & Setter (Encapsulation)
    // ==========================================

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public RoleUser getRole() {
        return role;
    }

    public void setRole(RoleUser role) {
        this.role = role;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", role=" + role +
                ", isActive=" + isActive +
                '}';
    }
}
