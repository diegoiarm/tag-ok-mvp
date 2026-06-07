package com.tagok.routes_service.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tagok.routes_service.domain.uso.EventoUso;
import com.tagok.routes_service.domain.uso.TipoEventoUso;

@Repository
public interface EventoUsoRepository extends JpaRepository<EventoUso, Long>
{
    long countByTipo(TipoEventoUso tipo);

    long countByTipoAndFechaAfter(TipoEventoUso tipo, LocalDateTime desde);

    /** Conteo de eventos agrupado por año, mes y tipo (para la serie mensual de uso). */
    @Query("""
        SELECT YEAR(e.fecha) AS anio,
               MONTH(e.fecha) AS mes,
               e.tipo AS tipo,
               COUNT(e) AS total
        FROM EventoUso e
        GROUP BY YEAR(e.fecha), MONTH(e.fecha), e.tipo
        ORDER BY YEAR(e.fecha), MONTH(e.fecha)
        """)
    List<ConteoMensualUso> contarPorMes();

    interface ConteoMensualUso
    {
        int getAnio();
        int getMes();
        TipoEventoUso getTipo();
        long getTotal();
    }
}
