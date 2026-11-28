package com.pakem.service.interfaces;

import com.pakem.model.DashboardSummary;
import com.pakem.model.Event;
import com.pakem.model.JadwalLatihan;

import java.util.List;

/**
 * Kontrak Layanan Bisnis untuk modul Dashboard Ringkasan (Fase 8).
 */
public interface IDashboardService {

    /**
     * Mengambil seluruh data statistik ringkasan dashboard terkini.
     */
    DashboardSummary getSummary() throws Exception;

    /**
     * Mengambil daftar jadwal latihan mendatang terdekat (maksimal limit item).
     */
    List<JadwalLatihan> getJadwalLatihanTerdekat(int limit) throws Exception;

    /**
     * Mengambil daftar event/pertunjukan mendatang terdekat (maksimal limit item).
     */
    List<Event> getEventTerdekat(int limit) throws Exception;
}
