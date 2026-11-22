package com.pakem.service.impl;

import com.pakem.dao.impl.AnggotaDaoImpl;
import com.pakem.dao.impl.KategoriTingkatDaoImpl;
import com.pakem.dao.impl.SettingsDaoImpl;
import com.pakem.dao.impl.SubkelompokDaoImpl;
import com.pakem.dao.interfaces.IAnggotaDao;
import com.pakem.dao.interfaces.IKategoriTingkatDao;
import com.pakem.dao.interfaces.ISettingsDao;
import com.pakem.dao.interfaces.ISubkelompokDao;
import com.pakem.model.Anggota;
import com.pakem.model.KategoriTingkat;
import com.pakem.model.Subkelompok;
import com.pakem.service.interfaces.IKategoriService;

import java.util.List;

/**
 * Implementasi Layanan Bisnis Manajemen Kategori Tingkat, Subkelompok, dan Auto-Grouping.
 */
public class KategoriServiceImpl implements IKategoriService {

    private final IKategoriTingkatDao kategoriDao;
    private final ISubkelompokDao subkelompokDao;
    private final IAnggotaDao anggotaDao;
    private final ISettingsDao settingsDao;

    public KategoriServiceImpl() {
        this(new KategoriTingkatDaoImpl(), new SubkelompokDaoImpl(), new AnggotaDaoImpl(), new SettingsDaoImpl());
    }

    public KategoriServiceImpl(IKategoriTingkatDao kategoriDao, ISubkelompokDao subkelompokDao,
                               IAnggotaDao anggotaDao, ISettingsDao settingsDao) {
        this.kategoriDao = kategoriDao;
        this.subkelompokDao = subkelompokDao;
        this.anggotaDao = anggotaDao;
        this.settingsDao = settingsDao;
    }

    @Override
    public List<KategoriTingkat> getSemuaKategori() throws Exception {
        return kategoriDao.findAll();
    }

    @Override
    public KategoriTingkat getKategoriById(int id) throws Exception {
        KategoriTingkat kat = kategoriDao.findById(id);
        if (kat == null) {
            throw new Exception("Kategori tingkat dengan ID " + id + " tidak ditemukan.");
        }
        return kat;
    }

    @Override
    public void tambahKategori(String nama, int urutan) throws Exception {
        if (nama == null || nama.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama kategori tingkat tidak boleh kosong.");
        }
        if (kategoriDao.existsByNama(nama.trim(), null)) {
            throw new Exception("Kategori tingkat dengan nama '" + nama.trim() + "' sudah ada.");
        }

        KategoriTingkat kat = new KategoriTingkat(0, nama.trim(), urutan);
        boolean saved = kategoriDao.save(kat);
        if (!saved) {
            throw new Exception("Gagal menyimpan kategori tingkat ke database.");
        }
    }

    @Override
    public void ubahKategori(int id, String nama, int urutan) throws Exception {
        if (nama == null || nama.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama kategori tingkat tidak boleh kosong.");
        }
        if (kategoriDao.existsByNama(nama.trim(), id)) {
            throw new Exception("Kategori tingkat dengan nama '" + nama.trim() + "' sudah digunakan.");
        }

        KategoriTingkat kat = new KategoriTingkat(id, nama.trim(), urutan);
        boolean updated = kategoriDao.update(kat);
        if (!updated) {
            throw new Exception("Gagal memperbarui kategori tingkat.");
        }
    }

    @Override
    public void hapusKategori(int id) throws Exception {
        boolean deleted = kategoriDao.delete(id);
        if (!deleted) {
            throw new Exception("Gagal menghapus kategori tingkat dengan ID " + id);
        }
    }

    @Override
    public int getJumlahAnggotaKategori(int kategoriId) throws Exception {
        return kategoriDao.countAnggota(kategoriId);
    }

    @Override
    public List<Subkelompok> getSubkelompokByKategori(int kategoriId) throws Exception {
        return subkelompokDao.findByKategoriId(kategoriId);
    }

    @Override
    public Subkelompok getSubkelompokById(int id) throws Exception {
        Subkelompok sub = subkelompokDao.findById(id);
        if (sub == null) {
            throw new Exception("Subkelompok dengan ID " + id + " tidak ditemukan.");
        }
        return sub;
    }

    @Override
    public void tambahSubkelompok(int kategoriId, String nama) throws Exception {
        if (nama == null || nama.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama subkelompok tidak boleh kosong.");
        }
        if (subkelompokDao.existsByNamaInKategori(kategoriId, nama.trim(), null)) {
            throw new Exception("Subkelompok '" + nama.trim() + "' sudah ada di dalam kategori ini.");
        }

        Subkelompok sub = new Subkelompok(0, kategoriId, nama.trim());
        boolean saved = subkelompokDao.save(sub);
        if (!saved) {
            throw new Exception("Gagal menyimpan subkelompok ke database.");
        }
    }

    @Override
    public void ubahSubkelompok(int id, int kategoriId, String nama) throws Exception {
        if (nama == null || nama.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama subkelompok tidak boleh kosong.");
        }
        if (subkelompokDao.existsByNamaInKategori(kategoriId, nama.trim(), id)) {
            throw new Exception("Subkelompok '" + nama.trim() + "' sudah ada di dalam kategori ini.");
        }

        Subkelompok sub = new Subkelompok(id, kategoriId, nama.trim());
        boolean updated = subkelompokDao.update(sub);
        if (!updated) {
            throw new Exception("Gagal memperbarui data subkelompok.");
        }
    }

    @Override
    public void hapusSubkelompok(int id) throws Exception {
        boolean deleted = subkelompokDao.delete(id);
        if (!deleted) {
            throw new Exception("Gagal menghapus subkelompok dengan ID " + id);
        }
    }

    @Override
    public int getJumlahAnggotaSubkelompok(int subkelompokId) throws Exception {
        return subkelompokDao.countAnggota(subkelompokId);
    }

    @Override
    public List<Anggota> getAnggotaByKategori(int kategoriId, boolean aktifSaja) throws Exception {
        return anggotaDao.findByKategoriId(kategoriId, aktifSaja);
    }

    @Override
    public void pindahAnggotaSubkelompok(int anggotaId, Integer subkelompokId) throws Exception {
        boolean updated = anggotaDao.updateSubkelompok(anggotaId, subkelompokId);
        if (!updated) {
            throw new Exception("Gagal memindahkan anggota ke subkelompok.");
        }
    }

    @Override
    public boolean apakahAdaAnggotaSudahPunyaSubkelompok(int kategoriId) throws Exception {
        return anggotaDao.countHavingSubkelompokInKategori(kategoriId) > 0;
    }

    @Override
    public int jalankanAutoGrouping(int kategoriId) throws Exception {
        List<Subkelompok> subs = subkelompokDao.findByKategoriId(kategoriId);
        if (subs == null || subs.isEmpty()) {
            throw new Exception("Kategori ini belum memiliki subkelompok. Silakan tambahkan minimal 1 subkelompok terlebih dahulu.");
        }

        List<Anggota> aktifAnggota = anggotaDao.findByKategoriId(kategoriId, true);
        if (aktifAnggota == null || aktifAnggota.isEmpty()) {
            throw new Exception("Tidak ada anggota aktif dalam kategori ini untuk dibagi ke dalam subkelompok.");
        }

        // Pembagian merata secara round-robin
        int numSubs = subs.size();
        for (int i = 0; i < aktifAnggota.size(); i++) {
            Subkelompok target = subs.get(i % numSubs);
            anggotaDao.updateSubkelompok(aktifAnggota.get(i).getId(), target.getId());
        }

        return aktifAnggota.size();
    }

    @Override
    public int getKapasitasAnjuran() throws Exception {
        String val = settingsDao.get("kapasitas_kelompok", "25");
        try {
            return Integer.parseInt(val.trim());
        } catch (NumberFormatException e) {
            return 25;
        }
    }
}
