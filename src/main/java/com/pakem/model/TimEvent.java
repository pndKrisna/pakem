package com.pakem.model;

/**
 * Model Tim dalam suatu Acara Pertunjukan (Fase 6).
 * Satu event dapat memiliki satu atau lebih tim.
 * Kolom tabel tim_event: id, event_id, nama_tim, catatan.
 */
public class TimEvent {

    private int id;
    private int eventId;
    private String namaTim;
    private String catatan;

    // Helper display fields (hasil join SQL)
    private String namaEvent;
    private int jumlahPemain;

    public TimEvent() {
    }

    public TimEvent(int id, int eventId, String namaTim, String catatan) {
        this.id = id;
        this.eventId = eventId;
        this.namaTim = namaTim;
        this.catatan = catatan;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getNamaTim() {
        return namaTim;
    }

    public void setNamaTim(String namaTim) {
        this.namaTim = namaTim;
    }

    public String getCatatan() {
        return catatan;
    }

    public void setCatatan(String catatan) {
        this.catatan = catatan;
    }

    public String getNamaEvent() {
        return (namaEvent != null && !namaEvent.isEmpty()) ? namaEvent : "-";
    }

    public void setNamaEvent(String namaEvent) {
        this.namaEvent = namaEvent;
    }

    public int getJumlahPemain() {
        return jumlahPemain;
    }

    public void setJumlahPemain(int jumlahPemain) {
        this.jumlahPemain = jumlahPemain;
    }

    @Override
    public String toString() {
        return namaTim;
    }
}
