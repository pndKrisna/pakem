package com.pakem.service.impl;

import com.pakem.dao.impl.AnggotaDaoImpl;
import com.pakem.dao.impl.JadwalLatihanDaoImpl;
import com.pakem.dao.impl.PresensiDaoImpl;
import com.pakem.dao.interfaces.IAnggotaDao;
import com.pakem.dao.interfaces.IJadwalLatihanDao;
import com.pakem.dao.interfaces.IPresensiDao;
import com.pakem.model.Anggota;
import com.pakem.model.JadwalLatihan;
import com.pakem.model.Presensi;
import com.pakem.model.RekapPresensi;
import com.pakem.model.enums.StatusAnggota;
import com.pakem.model.enums.StatusHadir;
import com.pakem.service.interfaces.IPresensiService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Implementasi Layanan Bisnis untuk Modul Presensi Latihan (Fase 5).
 */
public class PresensiServiceImpl implements IPresensiService {

    private final IPresensiDao presensiDao;
    private final IJadwalLatihanDao jadwalDao;
    private final IAnggotaDao anggotaDao;

    public PresensiServiceImpl() {
        this(new PresensiDaoImpl(), new JadwalLatihanDaoImpl(), new AnggotaDaoImpl());
    }

    public PresensiServiceImpl(IPresensiDao presensiDao, IJadwalLatihanDao jadwalDao, IAnggotaDao anggotaDao) {
        this.presensiDao = presensiDao;
        this.jadwalDao = jadwalDao;
        this.anggotaDao = anggotaDao;
    }

    @Override
    public List<Presensi> getLembarPresensiJadwal(int jadwalId) throws Exception {
        JadwalLatihan jadwal = jadwalDao.findById(jadwalId);
        if (jadwal == null) {
            throw new Exception("Jadwal latihan dengan ID " + jadwalId + " tidak ditemukan.");
        }

        // 1. Ambil data presensi yang telah tersimpan di database
        List<Presensi> existingList = presensiDao.findByJadwalId(jadwalId);
        Map<Integer, Presensi> existingMap = new HashMap<>();
        for (Presensi p : existingList) {
            existingMap.put(p.getAnggotaId(), p);
        }

        // 2. Ambil hanya anggota dengan status AKTIF (Isolasi status: Vakum & Nonaktif diabaikan)
        List<Anggota> anggotaAktifList = anggotaDao.findAll(StatusAnggota.AKTIF, null);

        // 3. Filter anggota yang berhak mengikuti jadwal latihan ini
        List<Presensi> lembarPresensi = new ArrayList<>();

        for (Anggota a : anggotaAktifList) {
            boolean eligible = true;

            // Jika jadwal spesifik ke subkelompok
            if (jadwal.getSubkelompokId() != null) {
                eligible = Objects.equals(a.getSubkelompokId(), jadwal.getSubkelompokId());
            }
            // Jika jadwal spesifik ke kategori tingkat umum (seluruh subkelompok dalam kategori)
            else if (jadwal.getKategoriTingkatId() != null) {
                eligible = Objects.equals(a.getKategoriTingkatId(), jadwal.getKategoriTingkatId());
            }

            if (eligible) {
                if (existingMap.containsKey(a.getId())) {
                    lembarPresensi.add(existingMap.get(a.getId()));
                } else {
                    // Buat baris presensi baru untuk anggota aktif ini (default HADIR)
                    Presensi p = new Presensi(0, jadwalId, a.getId(), StatusHadir.HADIR, null);
                    p.setNamaAnggota(a.getNamaLengkap());
                    p.setNomorKontak(a.getNomorKontak());
                    p.setNamaKategori(a.getNamaKategori());
                    p.setNamaSubkelompok(a.getNamaSubkelompok());
                    p.setTanggalJadwal(jadwal.getTanggal());
                    p.setJenisJadwal(jadwal.getJenisJadwal().getLabel());
                    lembarPresensi.add(p);
                }
            }
        }

        return lembarPresensi;
    }

    @Override
    public void simpanDaftarPresensi(List<Presensi> daftarPresensi) throws Exception {
        if (daftarPresensi == null || daftarPresensi.isEmpty()) {
            return;
        }

        for (Presensi p : daftarPresensi) {
            if (p.getStatusHadir() == null) {
                p.setStatusHadir(StatusHadir.HADIR);
            }
        }

        boolean success = presensiDao.saveBatch(daftarPresensi);
        if (!success) {
            throw new Exception("Gagal menyimpan daftar presensi ke database.");
        }
    }

    @Override
    public void simpanPresensi(Presensi presensi) throws Exception {
        if (presensi.getJadwalLatihanId() <= 0) {
            throw new IllegalArgumentException("ID Jadwal Latihan tidak valid.");
        }
        if (presensi.getAnggotaId() <= 0) {
            throw new IllegalArgumentException("ID Anggota tidak valid.");
        }
        if (presensi.getStatusHadir() == null) {
            presensi.setStatusHadir(StatusHadir.HADIR);
        }

        boolean success = presensiDao.saveOrUpdate(presensi);
        if (!success) {
            throw new Exception("Gagal menyimpan catatan presensi.");
        }
    }

    @Override
    public void hapusPresensi(int presensiId) throws Exception {
        boolean deleted = presensiDao.delete(presensiId);
        if (!deleted) {
            throw new Exception("Gagal menghapus data presensi dengan ID " + presensiId);
        }
    }

    @Override
    public void hapusPresensiByJadwal(int jadwalId) throws Exception {
        presensiDao.deleteByJadwalId(jadwalId);
    }

    @Override
    public Map<String, Integer> getRingkasanPresensiJadwal(int jadwalId) throws Exception {
        Map<String, Integer> map = new HashMap<>();
        map.put("TOTAL", presensiDao.countTotalByJadwal(jadwalId));
        map.put("HADIR", presensiDao.countByJadwalAndStatus(jadwalId, StatusHadir.HADIR.name()));
        map.put("IZIN", presensiDao.countByJadwalAndStatus(jadwalId, StatusHadir.IZIN.name()));
        map.put("SAKIT", presensiDao.countByJadwalAndStatus(jadwalId, StatusHadir.SAKIT.name()));
        map.put("ALPA", presensiDao.countByJadwalAndStatus(jadwalId, StatusHadir.TANPA_KETERANGAN.name()));
        return map;
    }

    @Override
    public List<RekapPresensi> getRekapitulasi(Integer kategoriId, Integer subkelompokId,
                                                LocalDate dariTanggal, LocalDate sampaiTanggal) throws Exception {
        return presensiDao.getRekapitulasi(kategoriId, subkelompokId, dariTanggal, sampaiTanggal);
    }
}
