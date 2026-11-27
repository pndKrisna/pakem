package com.pakem.dao.interfaces;

import com.pakem.model.Event;
import com.pakem.model.enums.JenisAcara;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Kontrak Abstraksi Akses Data (DAO) untuk entitas Event.
 */
public interface IEventDao {

    List<Event> findAll(JenisAcara filterJenis, LocalDate dariTanggal, LocalDate sampaiTanggal, String searchKeyword) throws SQLException;
    Event findById(int id) throws SQLException;
    boolean save(Event event) throws SQLException;
    boolean update(Event event) throws SQLException;
    boolean delete(int id) throws SQLException;
    int countTim(int eventId) throws SQLException;
    int countPemain(int eventId) throws SQLException;
}
