package com.tagok.routes_service.repository;

import com.tagok.routes_service.domain.dto.RouteSegment;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RouteRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<RouteSegment> getRouteSegments(double lon1, double lat1, double lon2, double lat2) {
        // Buscar vértice de inicio
        Long startId = findNearestVertex(lon1, lat1)
                .orElseThrow(() -> new RuntimeException("No se encontró vértice cercano a inicio: " + lon1 + "," + lat1));
        System.out.println(">>> Start vertex ID: " + startId);

        // Buscar vértice de fin
        Long endId = findNearestVertex(lon2, lat2)
                .orElseThrow(() -> new RuntimeException("No se encontró vértice cercano a fin: " + lon2 + "," + lat2));
        System.out.println(">>> End vertex ID: " + endId);

        // Consulta de ruta
        String routeSql = """
            SELECT
                r.seq,
                r.edge,
                r.node,
                r.cost,
                r.agg_cost,
                e.name,
                e.type,
                ST_AsGeoJSON(e.geometry)::json AS geometry
            FROM pgr_dijkstra(
                'SELECT id, source, target, cost, reverse_cost FROM edge',
                ?, ?, directed := true
            ) r
            JOIN edge e ON r.edge = e.id
            ORDER BY r.seq
        """;

        List<RouteSegment> segments = jdbcTemplate.query(routeSql,
                (rs, rowNum) -> mapRowToRouteSegment(rs),
                startId, endId
        );
        System.out.println(">>> Total segmentos obtenidos: " + segments.size());
        return segments;
    }

    private Optional<Long> findNearestVertex(double lon, double lat) {
        String sql = """
            SELECT id FROM edge_vertices_pgr
            ORDER BY the_geom <-> ST_SetSRID(ST_Point(?, ?), 4326)
            LIMIT 1
        """;
        List<Long> ids = jdbcTemplate.query(sql,
                (rs, rowNum) -> rs.getLong("id"),
                lon, lat);
        return ids.isEmpty() ? Optional.empty() : Optional.of(ids.get(0));
    }

    private RouteSegment mapRowToRouteSegment(ResultSet rs) throws SQLException {
        return RouteSegment.builder()
                .seq(rs.getInt("seq"))
                .edgeId(rs.getLong("edge"))
                .node(rs.getLong("node"))
                .cost(rs.getDouble("cost"))
                .aggCost(rs.getDouble("agg_cost"))
                .name(rs.getString("name"))
                .geometry(rs.getString("geometry"))
                .build();
    }
}