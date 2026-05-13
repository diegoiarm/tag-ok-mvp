package com.tagok.routes_service.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.tagok.routes_service.dto.PorticoRuta;
import com.tagok.routes_service.dto.RouteSegment;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RouteRepository 
{
    private final JdbcTemplate jdbcTemplate;

    public List<RouteSegment> getRouteSegments(long startVertexId, long endVertexId) 
    {
        String routeSql = """
            SELECT
                r.seq,
                r.edge,
                r.node,
                r.cost,
                r.agg_cost,
                e.name,
                e.type,
                ST_AsGeoJSON(e.geometry)::json AS geometry,
                ST_Length(e.geometry::geography) AS distance,
                e.maxspeed,
                p.id as portico_id
            FROM pgr_dijkstra(
                'SELECT id, source, target, cost, reverse_cost FROM edge',
                ?, ?, directed := true
            ) r
            JOIN edge e ON r.edge = e.id
            LEFT JOIN portico p ON ST_DWithin(
                e.geometry::geography,
                ST_SetSRID(ST_MakePoint(p.longitud, p.latitud), 4326)::geography,
                5
            )
            ORDER BY r.seq
        """;

        return jdbcTemplate.query(routeSql,
                (rs, rowNum) -> mapRowToRouteSegment(rs),
                startVertexId, endVertexId);
    }

    public Optional<Long> findNearestVertex(double lon, double lat) {
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

    public Optional<String> findMergedRouteGeometry(long startVertexId, long endVertexId) 
    {
        String sql = """
            SELECT ST_AsGeoJSON(ST_LineMerge(ST_Collect(e.geometry))) AS merged_geo
            FROM pgr_dijkstra(
                'SELECT id, source, target, cost, reverse_cost FROM edge',
                ?, ?, directed := true
            ) r
            JOIN edge e ON r.edge = e.id
            WHERE r.edge <> -1
        """;

        List<String> results = jdbcTemplate.query(sql,
                (rs, rowNum) -> rs.getString("merged_geo"),
                startVertexId, endVertexId);

        return results.stream()
                .filter(java.util.Objects::nonNull)
                .findFirst();
    }

    public List<RouteSegment> getAllRoads() 
    {

        String sql = """
            SELECT
                id,
                name,
                type,
                ST_AsGeoJSON(geometry) AS geometry
            FROM edge
            WHERE geometry IS NOT NULL
        """;

        return jdbcTemplate.query(sql, (rs, rowNum) ->
            RouteSegment.builder()
                .edgeId(rs.getLong("id"))
                .name(rs.getString("name"))
                .geometry(rs.getString("geometry"))
                .build()
        );
    }

    private RouteSegment mapRowToRouteSegment(ResultSet rs) throws SQLException
    {
        Long porticoId = rs.getObject("portico_id", Long.class);

        return RouteSegment.builder()
            .seq(rs.getInt("seq"))
            .edgeId(rs.getLong("edge"))
            .node(rs.getLong("node"))
            .cost(rs.getDouble("cost"))
            .aggCost(rs.getDouble("agg_cost"))
            .name(rs.getString("name"))
            .geometry(rs.getString("geometry"))
            .distance(rs.getDouble("distance"))
            .maxSpeed(rs.getDouble("maxspeed"))
            .portico(porticoId != null ? new PorticoRuta(porticoId) : null)
            .build();
    }
}