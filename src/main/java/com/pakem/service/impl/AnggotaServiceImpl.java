package com.pakem.service.impl;

import com.pakem.dao.impl.AnggotaDaoImpl;
import com.pakem.dao.impl.KategoriTingkatDaoImpl;
import com.pakem.dao.impl.RiwayatTingkatDaoImpl;
import com.pakem.dao.impl.SubkelompokDaoImpl;
import com.pakem.dao.interfaces.IAnggotaDao;
import com.pakem.dao.interfaces.IKategoriTingkatDao;
import com.pakem.dao.interfaces.IRiwayatTingkatDao;
import com.pakem.dao.interfaces.ISubkelompokDao;
import com.pakem.model.Anggota;
import com.pakem.model.KategoriTingkat;
import com.pakem.model.RiwayatTingkat;
import com.pakem.model.Subkelompok;
import com.pakem.model.enums.StatusAnggota;
import com.pakem.service.interfaces.IAnggotaService;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Implementasi Layanan Bisnis Manajemen Data Anggota.
 */
public class AnggotaServiceImpl implements IAnggotaService {

    private final IAnggotaDao anggotaDao;
    private final IKategoriTingkatDao kategoriDao;
    private final ISubkelompokDao subkelompokDao;
    private final IRiwayatTingkatDao riwayatTingkatDao;

    public AnggotaServiceImpl() {
        this(new AnggotaDaoImpl(), new KategoriTingkatDaoImpl(), new SubkelompokDaoImpl(), new RiwayatTingkatDaoImpl());
    }

    public AnggotaServiceImpl(IAnggotaDao anggotaDao, IKategoriTingkatDao kategoriDao,
                              ISubkelompokDao subkelompokDao, IRiwayatTingkatDao riwayatTingkatDao) {
        this.anggotaDao = anggotaDao;
        this.kategoriDao = kategoriDao;
        this.subkelompokDao = subkelompokDao;
        this.riwayatTingkatDao = riwayatTingkatDao;
    }

    @Override
    public List<Anggota> getDaftarAnggota(StatusAnggota filterStatus, String searchKeyword) throws Exception {
        return anggotaDao.findAll(filterStatus, searchKeyword);
    }

    @Override
    public Anggota getAnggotaById(int id) throws Exception {
        Anggota a = anggotaDao.findById(id);
        if (a == null) {
            throw new Exception("Data anggota dengan ID " + id + " tidak ditemukan.");
        }
        return a;
    }

    @Override
    public void simpanAnggotaBaru(Anggota anggota, String keteranganAwal) throws Exception {
        validateAnggotaInput(anggota);

        boolean saved = anggotaDao.save(anggota);
        if (!saved) {
            throw new Exception("Gagal menyimpan data anggota baru ke database.");
        }

        // Catat riwayat tingkat awal jika kategori tingkat diisi
        if (anggota.getKategoriTingkatId() != null) {
            RiwayatTingkat riwayat = new RiwayatTingkat();
            riwayat.setAnggotaId(anggota.getId());
            riwayat.setKategoriTingkatIdLama(null);
            riwayat.setKategoriTingkatIdBaru(anggota.getKategoriTingkatId());
            riwayat.setTanggalPerubahan(anggota.getTanggalBergabung() != null ? anggota.getTanggalBergabung() : LocalDate.now());
            riwayat.setKeterangan((keteranganAwal != null && !keteranganAwal.trim().isEmpty())
                    ? keteranganAwal.trim()
                    : "Pendaftaran awal anggota baru");

            riwayatTingkatDao.save(riwayat);
        }
    }

    @Override
    public void perbaruiAnggota(Anggota anggota, Integer kategoriTingkatBaruId, String keteranganTingkat) throws Exception {
        validateAnggotaInput(anggota);

        Anggota existing = anggotaDao.findById(anggota.getId());
        if (existing == null) {
            throw new Exception("Data anggota yang akan diperbarui tidak ditemukan.");
        }

        // Deteksi apakah terjadi perubahan kategori tingkat
        Integer oldKatId = existing.getKategoriTingkatId();
        if (!Objects.equals(oldKatId, kategoriTingkatBaruId)) {
            // Perbarui kategori di objek anggota
            anggota.setKategoriTingkatId(kategoriTingkatBaruId);

            // Simpan riwayat perubahan tingkat
            RiwayatTingkat riwayat = new RiwayatTingkat();
            riwayat.setAnggotaId(anggota.getId());
            riwayat.setKategoriTingkatIdLama(oldKatId);
            riwayat.setKategoriTingkatIdBaru(kategoriTingkatBaruId);
            riwayat.setTanggalPerubahan(LocalDate.now());
            riwayat.setKeterangan((keteranganTingkat != null && !keteranganTingkat.trim().isEmpty())
                    ? keteranganTingkat.trim()
                    : "Perubahan / usulan kenaikan tingkat kemampuan");

            riwayatTingkatDao.save(riwayat);
        }

        boolean updated = anggotaDao.update(anggota);
        if (!updated) {
            throw new Exception("Gagal memperbarui data anggota di database.");
        }
    }

    @Override
    public void ubahStatusAnggota(int anggotaId, StatusAnggota statusBaru) throws Exception {
        if (statusBaru == null) {
            throw new IllegalArgumentException("Status baru tidak boleh kosong.");
        }

        boolean updated = anggotaDao.updateStatus(anggotaId, statusBaru);
        if (!updated) {
            throw new Exception("Gagal mengubah status anggota dengan ID " + anggotaId);
        }
    }

    @Override
    public List<RiwayatTingkat> getRiwayatTingkatAnggota(int anggotaId) throws Exception {
        return riwayatTingkatDao.findByAnggotaId(anggotaId);
    }

    @Override
    public List<KategoriTingkat> getSemuaKategori() throws Exception {
        return kategoriDao.findAll();
    }

    @Override
    public List<Subkelompok> getSubkelompokByKategori(int kategoriId) throws Exception {
        return subkelompokDao.findByKategoriId(kategoriId);
    }

    @Override
    public Map<String, Integer> getRingkasanJumlahAnggota() throws Exception {
        Map<String, Integer> summary = new HashMap<>();
        summary.put("TOTAL", anggotaDao.countTotal());
        summary.put("AKTIF", anggotaDao.countByStatus(StatusAnggota.AKTIF));
        summary.put("VAKUM", anggotaDao.countByStatus(StatusAnggota.VAKUM));
        summary.put("NONAKTIF", anggotaDao.countByStatus(StatusAnggota.NONAKTIF));
        return summary;
    }

    private void validateAnggotaInput(Anggota a) {
        if (a.getNamaLengkap() == null || a.getNamaLengkap().trim().isEmpty()) {
            throw new IllegalArgumentException("Nama lengkap anggota wajib diisi.");
        }
        if (a.getTanggalBergabung() == null) {
            throw new IllegalArgumentException("Tanggal bergabung wajib diisi.");
        }
        if (a.getStatus() == null) {
            a.setStatus(StatusAnggota.AKTIF);
        }
    }
}
