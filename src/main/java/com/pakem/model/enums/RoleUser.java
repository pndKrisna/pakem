package com.pakem.model.enums;

/**
 * Peran pengguna dalam sistem Pakem.
 */
public enum RoleUser {
    PENGURUS("Pengurus / Admin"),
    PELATIH("Pelatih");

    private final String label;

    RoleUser(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static RoleUser fromString(String roleStr) {
        if (roleStr == null) return null;
        try {
            return RoleUser.valueOf(roleStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
