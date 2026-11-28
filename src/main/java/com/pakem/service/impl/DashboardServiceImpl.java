package com.pakem.service.impl;

import com.pakem.dao.impl.AnggotaDaoImpl;
import com.pakem.dao.impl.EventDaoImpl;
import com.pakem.dao.impl.IuranDaoImpl;
import com.pakem.dao.impl.JadwalLatihanDaoImpl;
import com.pakem.dao.impl.PresensiDaoImpl;
import com.pakem.dao.interfaces.IAnggotaDao;
import com.pakem.dao.interfaces.IEventDao;
import com.pakem.dao.interfaces.IIuranDao;
import com.pakem.dao.interfaces.IJadwalLatihanDao;
import com.pakem.dao.interfaces.IPresensiDao;
import com.pakem.model.DashboardSummary;
import com.pakem.model.Event;
import com.pakem.model.Iuran;
import com.pakem.model.JadwalLatihan;
import com.pakem.model.RekapPresensi;
import com.pakem.model.enums.StatusAnggota;
import com.pakem.model.enums.StatusBayar;
import com.pakem.service.interfaces.IDashboardService;

import java.time.LocalDate;
import java.util.List;

/**
 * Implementasi Layanan Bisnis untuk Dashboard Ringkasan (Fase 8).
 */
public class DashboardServiceImpl implements IDashboardService {

    private final IAnggotaDao anggotaDao;
    private final IJadwalLatihanDao jadwalLatihanDao;
    private final IPresensiDao presensiDao;
    private final IEventDao eventDao;
    private final IIuranDao iuranDao;

    public DashboardServiceImpl() {
        this(new AnggotaDaoImpl(), new JadwalLatihanDaoImpl(), new PresensiDaoImpl(), new EventDaoImpl(), new IuranDaoImpl());
    }

    public DashboardServiceImpl(IAnggotaDao anggotaDao, IJadwalLatihanDao jadwalLatihanDao,
                                IPresensiDao presensiDao, IEventDao eventDao, IIuranDao iuranDao) {
        this.anggotaDao = anggotaDao;
        this.jadwalLatihanDao = jadwalLatihanDao;
        this.presensiDao = presensiDao;
        this.eventDao = eventDao;
        this.iuranDao = iuranDao;
    }

    @Override
    public DashboardSummary getSummary() throws Exception {
        DashboardSummary sum = new DashboardSummary();

        // 1. Data Anggota
        sum.setTotalAnggota(anggotaDao.countTotal());
        sum.setJumlahAktif(anggotaDao.countByStatus(StatusAnggota.AKTIF));
        sum.setJumlahVakum(anggotaDao.countByStatus(StatusAnggota.VAKUM));
        sum.setJumlahNonaktif(anggotaDao.countByStatus(StatusAnggota.NONAKTIF));

        // 2. Jadwal & Event Terdekat
        LocalDate today = LocalDate.now();
        List<JadwalLatihan> allLatihan = jadwalLatihanDao.findAll();
        List<JadwalLatihan> upcomingLatihan = allLatihan.stream()
                .filter(j -> j.getTanggal() != null && !j.getTanggal().isBefore(today))
                .toList();
        sum.setTotalJadwalTerdekat(upcomingLatihan.size());

        List<Event> upcomingEvent = eventDao.findAll(null, today, null, null);
        sum.setTotalEventTerdekat(upcomingEvent.size());

        // 3. Rekap Kehadiran Bulan Berjalan
        LocalDate startOfMonth = today.withDayOfMonth(1);
        LocalDate endOfMonth = today.withDayOfMonth(today.lengthOfMonth());
        List<RekapPresensi> rekapList = presensiDao.getRekapitulasi(null, null, startOfMonth, endOfMonth);

        if (!rekapList.isEmpty()) {
            double totalPersen = 0;
            int countValid = 0;
            for (RekapPresensi rp : rekapList) {
                if (rp.getTotalPertemuan() > 0) {
                    totalPersen += rp.getPersentaseKehadiran();
                    countValid++;
                }
            }
            sum.setPersentaseKehadiranBulanIni(countValid > 0 ? (totalPersen / countValid) : 0.0);
        } else {
            sum.setPersentaseKehadiranBulanIni(0.0);
        }

        // 4. Rekap Iuran Bulan Berjalan
        int bulan = today.getMonthValue();
        int tahun = today.getYear();
        List<Iuran> iuranList = iuranDao.findByPeriode(bulan, tahun);

        int lunas = 0;
        for (Iuran i : iuranList) {
            if (i.getStatusBayar() == StatusBayar.LUNAS) {
                lunas++;
            }
        }
        sum.setIuranLunas(lunas);
        sum.setIuranTotalAktif(sum.getJumlahAktif());
        sum.setIuranBelumBayar(Math.max(0, sum.getJumlahAktif() - lunas));

        return sum;
    }

    @Override
    public List<JadwalLatihan> getJadwalLatihanTerdekat(int limit) throws Exception {
        LocalDate today = LocalDate.now();
        List<JadwalLatihan> all = jadwalLatihanDao.findAll();
        return all.stream()
                .filter(j -> j.getTanggal() != null && !j.getTanggal().isBefore(today))
                .limit(limit)
                .toList();
    }

    @Override
    public List<Event> getEventTerdekat(int limit) throws Exception {
        LocalDate today = LocalDate.now();
        List<Event> all = eventDao.findAll(null, today, null, null);
        return all.stream().limit(limit).toList();
    }
}
