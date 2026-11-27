package com.pakem.service.interfaces;

import com.pakem.model.Iuran;
import com.pakem.model.SanggarSettings;
import com.pakem.model.enums.StatusIuranWarna;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Kontrak layanan bisnis untuk modul iuran bulanan (Fase 7).
 */
public interface IIuranService {

    /**
     * Mengambil daftar semua anggota AKTIF beserta status warna iuran
     * untuk periode (bulan, tahun) yang dipilih.
     */
    List<Iuran> getDaftarIuranBulan(int bulan, int tahun) throws Exception;

    /**
     * Mencatat pembayaran iuran untuk seorang anggota pada periode tertentu.
     */
    void catatPembayaran(int anggotaId, int bulan, int tahun, BigDecimal nominal, LocalDate tanggalBayar) throws Exception;

    /**
     * Membatalkan status pembayaran iuran kembali ke BELUM_BAYAR.
     */
    void batalkanPembayaran(int anggotaId, int bulan, int tahun) throws Exception;

    /**
     * Mengambil seluruh pengaturan sanggar dari tabel settings.
     */
    SanggarSettings getPengaturanSanggar() throws Exception;

    /**
     * Menyimpan pengaturan sanggar ke tabel settings.
     */
    void simpanPengaturanSanggar(SanggarSettings settings) throws Exception;
}
