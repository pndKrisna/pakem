package com.pakem.service.impl;

import com.pakem.dao.impl.AnggotaDaoImpl;
import com.pakem.dao.impl.IuranDaoImpl;
import com.pakem.dao.impl.SettingsDaoImpl;
import com.pakem.dao.interfaces.IAnggotaDao;
import com.pakem.dao.interfaces.IIuranDao;
import com.pakem.dao.interfaces.ISettingsDao;
import com.pakem.model.Anggota;
import com.pakem.model.Iuran;
import com.pakem.model.SanggarSettings;
import com.pakem.model.enums.StatusAnggota;
import com.pakem.model.enums.StatusBayar;
import com.pakem.model.enums.StatusIuranWarna;
import com.pakem.service.interfaces.IIuranService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementasi Layanan Bisnis untuk Modul Iuran Bulanan (Fase 7).
 */
public class IuranServiceImpl implements IIuranService {

    private final IIuranDao iuranDao;
    private final IAnggotaDao anggotaDao;
    private final ISettingsDao settingsDao;

    public IuranServiceImpl() {
        this(new IuranDaoImpl(), new AnggotaDaoImpl(), new SettingsDaoImpl());
    }

    public IuranServiceImpl(IIuranDao iuranDao, IAnggotaDao anggotaDao, ISettingsDao settingsDao) {
        this.iuranDao = iuranDao;
        this.anggotaDao = anggotaDao;
        this.settingsDao = settingsDao;
    }

    @Override
    public List<Iuran> getDaftarIuranBulan(int bulan, int tahun) throws Exception {
        // 1. Ambil seluruh anggota aktif (Isolasi status: Vakum dan Nonaktif diabaikan)
        List<Anggota> anggotaAktif = anggotaDao.findAll(StatusAnggota.AKTIF, null);

        // 2. Ambil data iuran yang sudah tercatat di database untuk periode ini
        List<Iuran> iuranTercatat = iuranDao.findByPeriode(bulan, tahun);
        Map<Integer, Iuran> mapIuran = new HashMap<>();
        for (Iuran i : iuranTercatat) {
            mapIuran.put(i.getAnggotaId(), i);
        }

        // 3. Ambil pengaturan untuk kalkulasi warna
        SanggarSettings settings = getPengaturanSanggar();
        LocalDate today = LocalDate.now();

        List<Iuran> result = new ArrayList<>();
        for (Anggota a : anggotaAktif) {
            Iuran iuran = mapIuran.get(a.getId());

            if (iuran == null) {
                // Belum ada record di DB, buat objek memory-only sementara
                iuran = new Iuran();
                iuran.setAnggotaId(a.getId());
                iuran.setBulan(bulan);
                iuran.setTahun(tahun);
                iuran.setStatusBayar(StatusBayar.BELUM_BAYAR);
                iuran.setNominal(settings.getIuranDefault());
            }

            // Set helper fields untuk UI
            iuran.setNamaAnggota(a.getNamaLengkap());
            iuran.setNomorKontak(a.getNomorKontak());
            iuran.setKategoriTingkat(a.getNamaKategori());
            iuran.setSubkelompok(a.getNamaSubkelompok());

            // 4. Kalkulasi Indikator Warna
            iuran.setIndikatorWarna(hitungWarna(iuran, today, settings));

            result.add(iuran);
        }

        return result;
    }

    @Override
    public void catatPembayaran(int anggotaId, int bulan, int tahun, BigDecimal nominal, LocalDate tanggalBayar) throws Exception {
        if (nominal == null || nominal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Nominal pembayaran harus lebih dari 0.");
        }
        if (tanggalBayar == null) {
            tanggalBayar = LocalDate.now();
        }

        Iuran iuran = new Iuran();
        iuran.setAnggotaId(anggotaId);
        iuran.setBulan(bulan);
        iuran.setTahun(tahun);
        iuran.setNominal(nominal);
        iuran.setTanggalBayar(tanggalBayar);
        iuran.setStatusBayar(StatusBayar.LUNAS);

        boolean success = iuranDao.saveOrUpdate(iuran);
        if (!success) {
            throw new Exception("Gagal mencatat pembayaran iuran.");
        }
    }

    @Override
    public void batalkanPembayaran(int anggotaId, int bulan, int tahun) throws Exception {
        boolean success = iuranDao.delete(anggotaId, bulan, tahun);
        if (!success) {
            throw new Exception("Gagal membatalkan status pembayaran iuran.");
        }
    }

    @Override
    public SanggarSettings getPengaturanSanggar() throws Exception {
        SanggarSettings s = new SanggarSettings();
        s.setNamaSanggar(settingsDao.get("nama_sanggar", "Sanggar Seni Tabuh Pakem"));
        s.setIuranDefault(new BigDecimal(settingsDao.get("iuran_default", "50000")));
        s.setBatasKuning(Integer.parseInt(settingsDao.get("batas_kuning", "15")));
        s.setBatasMerah(Integer.parseInt(settingsDao.get("batas_merah", "25")));
        s.setKapasitasKelompok(Integer.parseInt(settingsDao.get("kapasitas_kelompok", "25")));
        return s;
    }

    @Override
    public void simpanPengaturanSanggar(SanggarSettings settings) throws Exception {
        settingsDao.set("nama_sanggar", settings.getNamaSanggar());
        settingsDao.set("iuran_default", settings.getIuranDefault().toString());
        settingsDao.set("batas_kuning", String.valueOf(settings.getBatasKuning()));
        settingsDao.set("batas_merah", String.valueOf(settings.getBatasMerah()));
        settingsDao.set("kapasitas_kelompok", String.valueOf(settings.getKapasitasKelompok()));
    }

    /**
     * Logika kalkulasi warna indikator iuran (PRD 4.7.130-134).
     */
    private StatusIuranWarna hitungWarna(Iuran iuran, LocalDate today, SanggarSettings settings) {
        // 1. Hijau - Lunas
        if (iuran.getStatusBayar() == StatusBayar.LUNAS) {
            return StatusIuranWarna.HIJAU;
        }

        // 2. Cek periode tagihan vs hari ini
        int tagihanTahun = iuran.getTahun();
        int tagihanBulan = iuran.getBulan();
        int currentTahun = today.getYear();
        int currentBulan = today.getMonthValue();
        int currentDay = today.getDayOfMonth();

        // 3. Merah - Menunggak (Sudah lewat bulan tagihan)
        if (currentTahun > tagihanTahun || (currentTahun == tagihanTahun && currentBulan > tagihanBulan)) {
            return StatusIuranWarna.MERAH;
        }

        // 4. Masih di bulan yang sama (atau bulan depan - tapi iuran biasanya bulan berjalan/lalu)
        if (currentTahun == tagihanTahun && currentBulan == tagihanBulan) {
            if (currentDay > settings.getBatasMerah()) {
                return StatusIuranWarna.MERAH;
            } else if (currentDay > settings.getBatasKuning()) {
                return StatusIuranWarna.KUNING;
            } else {
                return StatusIuranWarna.PUTIH;
            }
        }

        // 5. Belum masuk bulannya (Tagihan masa depan)
        return StatusIuranWarna.PUTIH;
    }
}
