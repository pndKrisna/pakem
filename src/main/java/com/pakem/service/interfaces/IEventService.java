package com.pakem.service.interfaces;

import com.pakem.model.Anggota;
import com.pakem.model.Event;
import com.pakem.model.PemainTim;
import com.pakem.model.TimEvent;
import com.pakem.model.User;
import com.pakem.model.enums.JenisAcara;
import com.pakem.model.enums.StatusHonor;
import com.pakem.model.enums.StatusKesediaan;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Kontrak Layanan Bisnis untuk Modul Pertunjukan / Event (Fase 6).
 */
public interface IEventService {

    // === Event / Acara ===
    List<Event> getDaftarEvent(JenisAcara filterJenis, LocalDate dariTanggal, LocalDate sampaiTanggal, String searchKeyword) throws Exception;
    Event getEventById(int id) throws Exception;
    void simpanEvent(Event event) throws Exception;
    void ubahEvent(Event event) throws Exception;
    void hapusEvent(int id) throws Exception;

    // === Tim dalam Event (Multi-Tim) ===
    List<TimEvent> getDaftarTimByEvent(int eventId) throws Exception;
    TimEvent getTimById(int id) throws Exception;
    void tambahTim(int eventId, String namaTim, String catatan) throws Exception;
    void ubahTim(int id, String namaTim, String catatan) throws Exception;
    void hapusTim(int id) throws Exception;

    // === Pemain Tim (Lintas Tingkat & Konfirmasi Kesediaan) ===
    List<PemainTim> getDaftarPemainByTim(int timEventId) throws Exception;
    List<PemainTim> getDaftarPemainByEvent(int eventId) throws Exception;
    PemainTim getPemainById(int id) throws Exception;
    void tambahPemain(int timEventId, int anggotaId, StatusKesediaan kesediaan, BigDecimal honor, StatusHonor statusHonor, User currentUser) throws Exception;
    void ubahKesediaanPemain(int pemainId, StatusKesediaan kesediaan) throws Exception;
    void ubahHonorPemain(int pemainId, BigDecimal honor, StatusHonor statusHonor, User currentUser) throws Exception;
    void hapusPemain(int pemainId) throws Exception;

    /**
     * Mengambil daftar anggota aktif yang tersedia untuk dipilih ke tim ini
     * (Isolasi status: hanya AKTIF, belum terdaftar di tim).
     */
    List<Anggota> getCalonPemainTersedia(int timEventId) throws Exception;

    /**
     * Memformat rincian jadwal acara dan susunan tim/pemain ke teks rapi untuk WhatsApp.
     */
    String formatPengumumanEvent(int eventId) throws Exception;
}
