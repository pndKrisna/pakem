package com.pakem.service.impl;

import com.pakem.dao.impl.AnggotaDaoImpl;
import com.pakem.dao.impl.EventDaoImpl;
import com.pakem.dao.impl.PemainTimDaoImpl;
import com.pakem.dao.impl.TimEventDaoImpl;
import com.pakem.dao.interfaces.IAnggotaDao;
import com.pakem.dao.interfaces.IEventDao;
import com.pakem.dao.interfaces.IPemainTimDao;
import com.pakem.dao.interfaces.ITimEventDao;
import com.pakem.model.Anggota;
import com.pakem.model.Event;
import com.pakem.model.PemainTim;
import com.pakem.model.TimEvent;
import com.pakem.model.User;
import com.pakem.model.enums.JenisAcara;
import com.pakem.model.enums.StatusAnggota;
import com.pakem.model.enums.StatusHonor;
import com.pakem.model.enums.StatusKesediaan;
import com.pakem.service.interfaces.IEventService;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Implementasi Layanan Bisnis untuk Modul Pertunjukan / Event (Fase 6).
 */
public class EventServiceImpl implements IEventService {

    private final IEventDao eventDao;
    private final ITimEventDao timEventDao;
    private final IPemainTimDao pemainTimDao;
    private final IAnggotaDao anggotaDao;

    public EventServiceImpl() {
        this(new EventDaoImpl(), new TimEventDaoImpl(), new PemainTimDaoImpl(), new AnggotaDaoImpl());
    }

    public EventServiceImpl(IEventDao eventDao, ITimEventDao timEventDao,
                            IPemainTimDao pemainTimDao, IAnggotaDao anggotaDao) {
        this.eventDao = eventDao;
        this.timEventDao = timEventDao;
        this.pemainTimDao = pemainTimDao;
        this.anggotaDao = anggotaDao;
    }

    // =========================================================================
    // Event / Acara
    // =========================================================================

    @Override
    public List<Event> getDaftarEvent(JenisAcara filterJenis, LocalDate dariTanggal, LocalDate sampaiTanggal, String searchKeyword) throws Exception {
        return eventDao.findAll(filterJenis, dariTanggal, sampaiTanggal, searchKeyword);
    }

    @Override
    public Event getEventById(int id) throws Exception {
        Event e = eventDao.findById(id);
        if (e == null) {
            throw new Exception("Acara dengan ID " + id + " tidak ditemukan.");
        }
        return e;
    }

    @Override
    public void simpanEvent(Event event) throws Exception {
        validateEventInput(event);

        boolean saved = eventDao.save(event);
        if (!saved) {
            throw new Exception("Gagal menyimpan acara pertunjukan ke database.");
        }

        // Otomatis buat 1 tim awal (misal "Tim Utama") agar acara langsung siap diisi pemain
        TimEvent defaultTim = new TimEvent(0, event.getId(), "Tim Utama", "Tim utama acara " + event.getNamaAcara());
        timEventDao.save(defaultTim);
    }

    @Override
    public void ubahEvent(Event event) throws Exception {
        validateEventInput(event);

        boolean updated = eventDao.update(event);
        if (!updated) {
            throw new Exception("Gagal memperbarui acara pertunjukan.");
        }
    }

    @Override
    public void hapusEvent(int id) throws Exception {
        boolean deleted = eventDao.delete(id);
        if (!deleted) {
            throw new Exception("Gagal menghapus acara dengan ID " + id);
        }
    }

    private void validateEventInput(Event e) {
        if (e.getNamaAcara() == null || e.getNamaAcara().trim().isEmpty()) {
            throw new IllegalArgumentException("Nama acara wajib diisi.");
        }
        if (e.getJenisAcara() == null) {
            throw new IllegalArgumentException("Jenis acara wajib dipilih.");
        }
        if (e.getTanggal() == null) {
            throw new IllegalArgumentException("Tanggal acara wajib diisi.");
        }
    }

    // =========================================================================
    // Tim dalam Event (Multi-Tim)
    // =========================================================================

    @Override
    public List<TimEvent> getDaftarTimByEvent(int eventId) throws Exception {
        return timEventDao.findByEventId(eventId);
    }

    @Override
    public TimEvent getTimById(int id) throws Exception {
        TimEvent t = timEventDao.findById(id);
        if (t == null) {
            throw new Exception("Tim event dengan ID " + id + " tidak ditemukan.");
        }
        return t;
    }

    @Override
    public void tambahTim(int eventId, String namaTim, String catatan) throws Exception {
        if (namaTim == null || namaTim.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama tim tidak boleh kosong.");
        }
        if (timEventDao.existsByNamaInEvent(namaTim.trim(), eventId, null)) {
            throw new Exception("Tim dengan nama '" + namaTim.trim() + "' sudah ada pada acara ini.");
        }

        TimEvent t = new TimEvent(0, eventId, namaTim.trim(), catatan);
        if (!timEventDao.save(t)) {
            throw new Exception("Gagal menyimpan tim baru.");
        }
    }

    @Override
    public void ubahTim(int id, String namaTim, String catatan) throws Exception {
        if (namaTim == null || namaTim.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama tim tidak boleh kosong.");
        }
        TimEvent existing = timEventDao.findById(id);
        if (existing == null) {
            throw new Exception("Data tim tidak ditemukan.");
        }
        if (timEventDao.existsByNamaInEvent(namaTim.trim(), existing.getEventId(), id)) {
            throw new Exception("Tim dengan nama '" + namaTim.trim() + "' sudah digunakan pada acara ini.");
        }

        existing.setNamaTim(namaTim.trim());
        existing.setCatatan(catatan);
        if (!timEventDao.update(existing)) {
            throw new Exception("Gagal memperbarui tim.");
        }
    }

    @Override
    public void hapusTim(int id) throws Exception {
        boolean deleted = timEventDao.delete(id);
        if (!deleted) {
            throw new Exception("Gagal menghapus tim dengan ID " + id);
        }
    }

    // =========================================================================
    // Pemain Tim (Lintas Tingkat & Kesediaan)
    // =========================================================================

    @Override
    public List<PemainTim> getDaftarPemainByTim(int timEventId) throws Exception {
        return pemainTimDao.findByTimId(timEventId);
    }

    @Override
    public List<PemainTim> getDaftarPemainByEvent(int eventId) throws Exception {
        return pemainTimDao.findByEventId(eventId);
    }

    @Override
    public PemainTim getPemainById(int id) throws Exception {
        PemainTim p = pemainTimDao.findById(id);
        if (p == null) {
            throw new Exception("Data pemain tim dengan ID " + id + " tidak ditemukan.");
        }
        return p;
    }

    @Override
    public void tambahPemain(int timEventId, int anggotaId, StatusKesediaan kesediaan,
                             BigDecimal honor, StatusHonor statusHonor, User currentUser) throws Exception {
        TimEvent tim = timEventDao.findById(timEventId);
        if (tim == null) {
            throw new Exception("Tim event dengan ID " + timEventId + " tidak ditemukan.");
        }

        Event event = eventDao.findById(tim.getEventId());
        if (event == null) {
            throw new Exception("Acara pertunjukan tidak ditemukan.");
        }

        Anggota anggota = anggotaDao.findById(anggotaId);
        if (anggota == null) {
            throw new Exception("Data anggota tidak ditemukan.");
        }
        if (anggota.getStatus() != StatusAnggota.AKTIF) {
            throw new Exception("Hanya anggota dengan status AKTIF yang dapat dipilih menjadi pemain acara!");
        }

        if (pemainTimDao.existsByTimAndAnggota(timEventId, anggotaId)) {
            throw new Exception("Anggota '" + anggota.getNamaLengkap() + "' sudah terdaftar di tim " + tim.getNamaTim());
        }

        // Aturan Honor: Hanya relevan jika Jenis Acara = JOB dan currentUser punya hak akses honor
        BigDecimal finalHonor = null;
        StatusHonor finalStatusHonor = null;

        if (event.getJenisAcara() == JenisAcara.JOB) {
            if (currentUser != null && currentUser.canAccessHonor()) {
                finalHonor = honor;
                finalStatusHonor = (statusHonor != null) ? statusHonor : StatusHonor.BELUM_DIBAGIKAN;
            }
        }

        PemainTim pemain = new PemainTim(0, timEventId, anggotaId,
                kesediaan != null ? kesediaan : StatusKesediaan.BELUM_DITANYA,
                finalHonor, finalStatusHonor);

        if (!pemainTimDao.save(pemain)) {
            throw new Exception("Gagal menambahkan pemain ke dalam tim.");
        }
    }

    @Override
    public void ubahKesediaanPemain(int pemainId, StatusKesediaan kesediaan) throws Exception {
        if (kesediaan == null) {
            throw new IllegalArgumentException("Status kesediaan tidak boleh kosong.");
        }
        boolean updated = pemainTimDao.updateKesediaan(pemainId, kesediaan);
        if (!updated) {
            throw new Exception("Gagal memperbarui status kesediaan pemain.");
        }
    }

    @Override
    public void ubahHonorPemain(int pemainId, BigDecimal honor, StatusHonor statusHonor, User currentUser) throws Exception {
        if (currentUser != null && !currentUser.canAccessHonor()) {
            throw new Exception("Akses ditolak: Hanya Pengurus yang memiliki izin mengelola honor pemain.");
        }

        PemainTim p = pemainTimDao.findById(pemainId);
        if (p == null) {
            throw new Exception("Data pemain tidak ditemukan.");
        }

        if (p.getJenisAcara() != JenisAcara.JOB) {
            throw new Exception("Pencatatan honor hanya berlaku untuk acara berjenis Pentas / Job.");
        }

        boolean updated = pemainTimDao.updateHonor(pemainId, honor,
                statusHonor != null ? statusHonor : StatusHonor.BELUM_DIBAGIKAN);
        if (!updated) {
            throw new Exception("Gagal memperbarui data honor pemain.");
        }
    }

    @Override
    public void hapusPemain(int pemainId) throws Exception {
        boolean deleted = pemainTimDao.delete(pemainId);
        if (!deleted) {
            throw new Exception("Gagal menghapus pemain dari tim.");
        }
    }

    @Override
    public List<Anggota> getCalonPemainTersedia(int timEventId) throws Exception {
        // 1. Ambil ID pemain yang sudah ada di tim ini
        List<PemainTim> existingPemain = pemainTimDao.findByTimId(timEventId);
        Set<Integer> existingIds = new HashSet<>();
        for (PemainTim p : existingPemain) {
            existingIds.add(p.getAnggotaId());
        }

        // 2. Ambil seluruh anggota berstatus AKTIF (Isolasi status: Vakum & Nonaktif diabaikan)
        List<Anggota> anggotaAktif = anggotaDao.findAll(StatusAnggota.AKTIF, null);

        // 3. Filter anggota yang belum ada di tim ini
        List<Anggota> available = new ArrayList<>();
        for (Anggota a : anggotaAktif) {
            if (!existingIds.contains(a.getId())) {
                available.add(a);
            }
        }
        return available;
    }

    @Override
    public String formatPengumumanEvent(int eventId) throws Exception {
        Event event = getEventById(eventId);
        List<TimEvent> timList = getDaftarTimByEvent(eventId);

        Locale indoLocale = Locale.of("id", "ID");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", indoLocale);
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");

        StringBuilder sb = new StringBuilder();
        sb.append("📢 *PENGUMUMAN ACARA SANGGAR SENI TABUH PAKEM*\n");
        sb.append("=========================================\n\n");

        sb.append("🎭 *Nama Acara:* ").append(event.getNamaAcara()).append("\n");
        sb.append("🏷️ *Jenis Acara:* ").append(event.getJenisAcara().getLabel()).append("\n");
        sb.append("📅 *Hari/Tanggal:* ").append(event.getTanggal() != null ? event.getTanggal().format(dtf) : "-").append("\n");
        sb.append("⏰ *Waktu:* ").append(event.getWaktu() != null ? event.getWaktu().format(timeFmt) + " Wita" : "-").append("\n");
        sb.append("📍 *Lokasi:* ").append(event.getLokasi() != null ? event.getLokasi() : "-").append("\n");

        if (event.getCatatan() != null && !event.getCatatan().trim().isEmpty()) {
            sb.append("📝 *Catatan:* ").append(event.getCatatan().trim()).append("\n");
        }

        sb.append("\n-----------------------------------------\n");
        sb.append("👥 *SUSUNAN TIM & PEMAIN:*\n");
        sb.append("-----------------------------------------\n");

        if (timList.isEmpty()) {
            sb.append("_(Belum ada tim yang dibentuk)_\n");
        } else {
            for (TimEvent tim : timList) {
                sb.append("\n🚩 *").append(tim.getNamaTim()).append("*\n");
                if (tim.getCatatan() != null && !tim.getCatatan().trim().isEmpty()) {
                    sb.append("   Catatan Tim: ").append(tim.getCatatan().trim()).append("\n");
                }

                List<PemainTim> pemainList = pemainTimDao.findByTimId(tim.getId());
                if (pemainList.isEmpty()) {
                    sb.append("   _(Belum ada pemain terpilih)_\n");
                } else {
                    int no = 1;
                    for (PemainTim p : pemainList) {
                        String statusBadge = switch (p.getStatusKesediaan()) {
                            case BERSEDIA -> "✅ [Bersedia]";
                            case TIDAK_BERSEDIA -> "❌ [Tidak Bersedia]";
                            case BELUM_DITANYA -> "⏳ [Belum Ditanya]";
                        };

                        sb.append(String.format("   %d. %s (%s / %s) - %s\n",
                                no++, p.getNamaAnggota(), p.getNamaKategori(), p.getNamaSubkelompok(), statusBadge));
                    }
                }
            }
        }

        sb.append("\n=========================================\n");
        sb.append("Mohon konfirmasi kesediaan kepada pengurus/pelatih. Matur suksma 🙏");

        return sb.toString();
    }
}
