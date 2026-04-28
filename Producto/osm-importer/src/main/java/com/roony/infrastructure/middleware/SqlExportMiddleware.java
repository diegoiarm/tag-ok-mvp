package com.roony.infrastructure.middleware;

import java.util.List;
import java.util.logging.Logger;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.stream.Collectors;

import com.roony.domain.model.Element;
import com.roony.domain.model.Geometry;
import com.roony.domain.model.MaxSpeed;
import com.roony.domain.model.Tags;

public class SqlExportMiddleware implements ElementMiddleware
{
    private static final Logger logger = Logger.getLogger(SqlExportMiddleware.class.getName());
    private static final String INSERT_SQL = """
        INSERT INTO EDGE (
            element_id,
            name,
            type,
            surface,
            lanes,
            maxspeed,
            maxspeed_bus,
            maxspeed_hgv,
            maxweight,
            oneway,
            geometry
        )
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ST_GeomFromText(?,4326))
        ON CONFLICT (element_id) DO NOTHING
    """;

    private static final int BATCH_SIZE = 500;

    private Connection conn;
    private PreparedStatement ps;
    private int batchCount = 0;
    
    public SqlExportMiddleware(DataSource dataSource)
    {
        try
        {
            this.conn = dataSource.getConnection();
            this.conn.setAutoCommit(false);

            this.ps = conn.prepareStatement(INSERT_SQL);
        }
        catch(SQLException e)
        {
            throw new RuntimeException("No se pudo inicializar SqlExportMiddleware", e);
        }
    }

 @Override
    public FilterResult process(
        Element element,
        int index,
        String fileName,
        Next next
    )
    {
        if (element == null)
            return FilterResult.ERROR;

        try
        {
            Tags tags = element.tags();
            MaxSpeed ms = tags != null ? tags.maxSpeed() : null;

            ps.setLong(1, element.id());

            ps.setString(2,
                tags != null ? tags.name() : null);

            ps.setString(3,
                tags != null ? tags.highway() : null);

            ps.setString(4,
                tags != null ? tags.surface() : null);

            Integer lanes =
                parseIntegerOrNull(
                    tags != null ? tags.lanes() : null
                );

            if(lanes != null)
                ps.setInt(5, lanes);
            else
                ps.setNull(5, java.sql.Types.INTEGER);

            if(ms != null && ms.defaultMaxSpeed() > 0)
                ps.setInt(6, ms.defaultMaxSpeed());
            else
                ps.setNull(6, java.sql.Types.INTEGER);

            if(ms != null && ms.bus() != null)
                ps.setInt(7, ms.bus());
            else
                ps.setNull(7, java.sql.Types.INTEGER);

            if(ms != null && ms.hgv() != null)
                ps.setInt(8, ms.hgv());
            else
                ps.setNull(8, java.sql.Types.INTEGER);

            if(tags != null && tags.maxweight()!=null)
                ps.setDouble(9, tags.maxweight());
            else
                ps.setNull(9, java.sql.Types.FLOAT);

            ps.setInt(
                10,
                (tags != null && tags.oneway()) ? 1 : 0
            );

            String wkt = buildLineString(
                element.geometry()
            );

            if(wkt != null)
                ps.setString(11, wkt);
            else
                ps.setNull(
                    11,
                    java.sql.Types.VARCHAR
                );

            ps.addBatch();
            batchCount++;

            if(batchCount >= BATCH_SIZE)
            {
                ps.executeBatch();
                conn.commit();
                batchCount = 0;
            }

        }
        catch(SQLException e)
        {
            logger.warning(
                "Error insertando "
                + element.id()
                + ": "
                + e.getMessage()
            );

            return FilterResult.ERROR;
        }

        return next.invoke(
            element,
            index,
            fileName);
    }


    public void flush()
    {
        try
        {
            if(batchCount > 0)
            {
                ps.executeBatch();
                conn.commit();
            }

            ps.close();
            conn.close();
        }
        catch(SQLException e)
        {
            logger.warning(
                "Error cerrando batch: "
                + e.getMessage()
            );
        }
    }


    private String buildLineString(
        List<Geometry> geomList
    )
    {
        if(geomList == null || geomList.isEmpty())
            return null;

        return geomList.stream()
            .map(g ->
                g.lon() + " " + g.lat()
            )
            .collect(
                Collectors.joining(
                    ", ",
                    "LINESTRING(",
                    ")"
                )
            );
    }


    private Integer parseIntegerOrNull(String value)
    {
        if(value != null)
        {
            value = value.trim().split(" ")[0];

            try
            {
                return Integer.parseInt(value);
            }
            catch(NumberFormatException ignored)
            {
            }
        }

        return null;
    }
}
