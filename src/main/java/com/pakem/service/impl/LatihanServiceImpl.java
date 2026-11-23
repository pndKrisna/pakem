package com.pakem.service.impl;

import com.pakem.dao.interfaces.IMateriTabuhDao;
import com.pakem.dao.interfaces.IJadwalLatihanDao;
import com.pakem.dao.interfaces.ISesiMateriDao;
import com.pakem.dao.impl.MateriTabuhDaoImpl;
import com.pakem.dao.impl.JadwalLatihanDaoImpl;
import com.pakem.dao.impl.SesiMateriDaoImpl;
import com.pakem.model.JadwalLatihan;
import com.pakem.model.MateriTabuh;
import com.pakem.model.SesiMateri;
import com.pakem.service.interfaces.ILatihanService;

import java.time.LocalDate;
import java.util.List;

/**
 * Implementasi Layanan Bisnis untuk Jadwal Latihan & Materi Tabuh (Fase 4).
 */
public class LatihanServiceImpl implements ILatihanService {

    private final IMateriTabuhDao materiDao;
    private final IJadwalLatihanDao jadwalDao;
    private final ISesiMateriDao sesiDao;

    public LatihanServiceImpl() {
        this(new MateriTabuhDaoImpl(), new JadwalLatihanDaoImpl(), new SesiMateriDaoImpl());
    }

    public LatihanServiceImpl(IMateriTabuhDao materiDao, IJadwalLatihanDao jadwalDao,
                              ISesiMateriDao sesiDao) {
        this.materiDao = materiDao;
        this.jadwalDao = jadwalDao;
        this.sesiDao = sesiDao;
    }

    // ========================================================================
    // Materi Tabuh
    // ========================================================================

    @Override
    public List<MateriTabuh> getSemuaMateriTabuh() throws Exception {
        return materiDao.findAll();
    }

    @Override
    public MateriTabuh getMateriTabuhById(int id) throws Exception {
        MateriTabuh m = materiDao.findById(id);
        if (m == null) {
            throw new Exception("Materi tabuh dengan ID " + id + " tidak ditemukan.");
        }
        return m;
    }

    @Override
    public void tambahMateriTabuh(String nama, String deskripsi) throws Exception {
        if (nama == null || nama.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama materi tabuh tidak boleh kosong.");
        }
        if (materiDao.existsByNama(nama.trim(), null)) {
            throw new Exception("Materi tabuh dengan nama '" + nama.trim() + "' sudah ada.");
        }

        MateriTabuh m = new MateriTabuh(0, nama.trim(), deskripsi);
        if (!materiDao.save(m)) {
            throw new Exception("Gagal menyimpan materi tabuh ke database.");
        }
    }

    @Override
    public void ubahMateriTabuh(int id, String nama, String deskripsi) throws Exception {
        if (nama == null || nama.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama materi tabuh tidak boleh kosong.");
        }
        if (materiDao.existsByNama(nama.trim(), id)) {
            throw new Exception("Materi tabuh dengan nama '" + nama.trim() + "' sudah digunakan.");
        }

        MateriTabuh m = new MateriTabuh(id, nama.trim(), deskripsi);
        if (!materiDao.update(m)) {
            throw new Exception("Gagal memperbarui materi tabuh.");
        }
    }

    @Override
    public void hapusMateriTabuh(int id) throws Exception {
        if (materiDao.countDigunakanDiSesi(id) > 0) {
            throw new Exception("Materi tabuh ini sudah digunakan dalam sesi latihan. Tidak dapat dihapus.");
        }
        boolean deleted = materiDao.delete(id);
        if (!deleted) {
            throw new Exception("Gagal menghapus materi tabuh dengan ID " + id);
        }
    }

    @Override
    public boolean apakahMateriSudahDigunakan(int materiId) throws Exception {
        return materiDao.countDigunakanDiSesi(materiId) > 0;
    }

    // ========================================================================
    // Jadwal Latihan
    // ========================================================================

    @Override
    public List<JadwalLatihan> getSemuaJadwalLatihan() throws Exception {
        return jadwalDao.findAll();
    }

    @Override
    public List<JadwalLatihan> getJadwalByKategori(int kategoriId) throws Exception {
        return jadwalDao.findByKategori(kategoriId);
    }

    @Override
    public List<JadwalLatihan> getJadwalBySubkelompok(int subkelompokId) throws Exception {
        return jadwalDao.findBySubkelompok(subkelompokId);
    }

    @Override
    public List<JadwalLatihan> getJadwalByTanggal(LocalDate tanggal) throws Exception {
        return jadwalDao.findByTanggal(tanggal);
    }

    @Override
    public JadwalLatihan getJadwalById(int id) throws Exception {
        JadwalLatihan j = jadwalDao.findById(id);
        if (j == null) {
            throw new Exception("Jadwal latihan dengan ID " + id + " tidak ditemukan.");
        }
        return j;
    }

    @Override
    public void simpanJadwalLatihan(JadwalLatihan jadwal) throws Exception {
        if (jadwal.getTanggal() == null) {
            throw new IllegalArgumentException("Tanggal jadwal latihan tidak boleh kosong.");
        }

        if (jadwal.getJenisJadwal() == null) {
            throw new IllegalArgumentException("Jenis jadwal latihan tidak boleh kosong.");
        }

        if (!jadwalDao.save(jadwal)) {
            throw new Exception("Gagal menyimpan jadwal latihan ke database.");
        }
    }

    @Override
    public void ubahJadwalLatihan(JadwalLatihan jadwal) throws Exception {
        if (jadwal.getTanggal() == null) {
            throw new IllegalArgumentException("Tanggal jadwal latihan tidak boleh kosong.");
        }
        if (jadwal.getJenisJadwal() == null) {
            throw new IllegalArgumentException("Jenis jadwal latihan tidak boleh kosong.");
        }
        if (!jadwalDao.update(jadwal)) {
            throw new Exception("Gagal memperbarui jadwal latihan.");
        }
    }

    @Override
    public void hapusJadwalLatihan(int id) throws Exception {
        jadwalDao.delete(id);
    }

    // ========================================================================
    // Sesi Materi (Progres)
    // ========================================================================

    @Override
    public List<SesiMateri> getSesiMateriByJadwal(int jadwalId) throws Exception {
        return sesiDao.findByJadwalId(jadwalId);
    }

    @Override
    public List<SesiMateri> getSesiMateriByMateri(int materiId) throws Exception {
        return sesiDao.findByMateriId(materiId);
    }

    @Override
    public SesiMateri getSesiMateriById(int id) throws Exception {
        SesiMateri s = sesiDao.findById(id);
        if (s == null) {
            throw new Exception("Sesi materi dengan ID " + id + " tidak ditemukan.");
        }
        return s;
    }

    @Override
    public void simpanSesiMateri(SesiMateri sesi) throws Exception {
        if (sesi.getJadwalLatihanId() <= 0) {
            throw new IllegalArgumentException("Jadwal latihan harus dipilih.");
        }
        if (sesi.getMateriTabuhId() <= 0) {
            throw new IllegalArgumentException("Materi tabuh harus dipilih.");
        }
        if (!sesiDao.save(sesi)) {
            throw new Exception("Gagal menyimpan sesi materi ke database.");
        }
    }

    @Override
    public void ubahSesiMateri(SesiMateri sesi) throws Exception {
        if (!sesiDao.update(sesi)) {
            throw new Exception("Gagal memperbarui catatan progres sesi materi.");
        }
    }

    @Override
    public void hapusSesiMateri(int id) throws Exception {
        boolean deleted = sesiDao.delete(id);
        if (!deleted) {
            throw new Exception("Gagal menghapus sesi materi dengan ID " + id);
        }
    }

    @Override
    public void hapusSesiMateriByJadwal(int jadwalId) throws Exception {
        sesiDao.deleteByJadwalId(jadwalId);
    }

    @Override
    public SesiMateri getProgresTerakhirByMateri(int materiId) throws Exception {
        return sesiDao.findProgresTerakhirByMateri(materiId);
    }
}
