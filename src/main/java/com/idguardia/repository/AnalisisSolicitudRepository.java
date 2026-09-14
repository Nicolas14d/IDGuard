package com.idguardia.repository;

import com.idguardia.model.AnalisisSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AnalisisSolicitudRepository extends JpaRepository<AnalisisSolicitud, String> {
    @Query("SELECT s FROM AnalisisSolicitud s LEFT JOIN FETCH s.resultado r LEFT JOIN FETCH r.explicaciones ORDER BY s.fechaAnalisis DESC")
    List<AnalisisSolicitud> findAllWithDetails();
}