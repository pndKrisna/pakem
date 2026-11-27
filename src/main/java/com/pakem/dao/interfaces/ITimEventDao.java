package com.pakem.dao.interfaces;

import com.pakem.model.TimEvent;

import java.sql.SQLException;
import java.util.List;

/**
 * Kontrak Abstraksi Akses Data (DAO) untuk entitas Tim Event.
 */
public interface ITimEventDao {

    List<TimEvent> findByEventId(int eventId) throws SQLException;
    TimEvent findById(int id) throws SQLException;
    boolean save(TimEvent tim) throws SQLException;
    boolean update(TimEvent tim) throws SQLException;
    boolean delete(int id) throws SQLException;
    int countPemain(int timId) throws SQLException;
    boolean existsByNamaInEvent(String namaTim, int eventId, Integer excludeId) throws SQLException;
}
