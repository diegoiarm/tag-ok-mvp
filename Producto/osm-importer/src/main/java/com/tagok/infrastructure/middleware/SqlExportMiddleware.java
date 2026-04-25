package com.tagok.infrastructure.middleware;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.tagok.domain.model.Element;
import com.tagok.domain.model.Geometry;
import com.tagok.domain.model.MaxSpeed;
import com.tagok.domain.model.Tags;

public class SqlExportMiddleware implements ElementMiddleware
{
    private static final Logger logger = Logger.getLogger(SqlExportMiddleware.class.getName());
    private final DataSource dataSource;
    
    public SqlExportMiddleware(DataSource dataSource)
    {
        this.dataSource = dataSource;
    }

    @Override
    public FilterResult process(JsonNode node, int index, String fileName, Map<String, Object> context, Next next) 
    {
        Element element = (Element) context.get(MapToDomainMiddleware.CURRENT_ELEMENT_KEY);

        if (element == null)
            return FilterResult.ERROR;

        final String sql = """
            INSERT INTO EDGE (element_id, name, 
                type, surface, lanes, maxspeed, maxspeed_bus, maxspeed_hgv, maxweight, oneway, geometry)  
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ST_GeomFromText(?, 4326))
            ON CONFLICT (element_id) DO NOTHING
        """;

        try (Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) 
            {
                Tags tags = element.tags();
                MaxSpeed ms = tags != null ? tags.maxSpeed() : null;

                ps.setLong(1, element.id());

                ps.setString(2, tags != null ? tags.name() : null);

                ps.setString(3, tags != null ? tags.highway() : null);

                ps.setString(4, tags != null ? tags.surface() : null);

                Integer lanes = parseIntegerOrNull(tags != null ? tags.lanes() : null);

                if (lanes != null) 
                    ps.setInt(5, lanes);
                else 
                    ps.setNull(5, java.sql.Types.INTEGER);

                if (ms != null && ms.defaultMaxSpeed() > 0) 
                    ps.setInt(6, ms.defaultMaxSpeed());
                else 
                    ps.setNull(6, java.sql.Types.INTEGER);


                if (ms != null && ms.bus() != null) 
                    ps.setInt(7, ms.bus());
                else 
                    ps.setNull(7, java.sql.Types.INTEGER);

                if (ms != null && ms.hgv() != null) 
                    ps.setInt(8, ms.hgv());
                else 
                    ps.setNull(8, java.sql.Types.INTEGER);

                if (tags != null && tags.maxWeight() != null) 
                    ps.setDouble(9, tags.maxWeight());
                else 
                    ps.setNull(9, java.sql.Types.FLOAT);

                boolean oneway = tags != null && tags.oneway();

                ps.setInt(10, oneway ? 1 : 0);

                List<Geometry> geomList = element.geometry();
                String wkt = null;
                if (geomList != null && !geomList.isEmpty()) 
                {
                    wkt = geomList.stream()
                            .map(g -> g.lon() + " " + g.lat())
                            .collect(Collectors.joining(", ", "LINESTRING(", ")"));
                }
                if (wkt != null)
                    ps.setString(11, wkt);
                else
                    ps.setNull(11, java.sql.Types.VARCHAR);

                ps.executeUpdate();
            } 
        catch (SQLException e) 
        {
            logger.warning("Error insertando elemento " + element.id() + " de " + fileName + ": " + e.getMessage());
            return FilterResult.ERROR;
        }

        return next.invoke(node, index, fileName, context);
    }

    private Integer parseIntegerOrNull(String value) 
    {
        if (value != null) 
        {
            value = value.trim().split(" ")[0];
            try 
            {
                return Integer.parseInt(value);
            } 
            catch (NumberFormatException ignored) 
            {
            }
        }
        return null;
    }
}