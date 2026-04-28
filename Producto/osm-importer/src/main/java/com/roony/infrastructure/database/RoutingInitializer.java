package com.roony.infrastructure.database;

import javax.sql.DataSource;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

public final class RoutingInitializer
{
    private static final List<String> SCRIPTS = List.of(
        "database-scripts/04_create_topology.sql",
        "database-scripts/05_cost.sql"
    );

    private RoutingInitializer(){}

    public static void initialize(DataSource ds)
    {
        try(Connection conn = ds.getConnection();
            Statement st = conn.createStatement())
        {
            conn.setAutoCommit(false);

            for(String script : SCRIPTS)
            {
                String sql = loadSql(script);

                for(String statement : sql.split(";"))
                {
                    String trimmed =
                        statement.trim();

                    if(!trimmed.isEmpty())
                    {
                        st.execute(trimmed);
                    }
                }

                System.out.println("Ejecutado: " + script);
            }

            conn.commit();

            System.out.println("Red de rutas inicializada.");
        }
        catch(Exception e)
        {
            throw new RuntimeException("Error creando topología y costos", e);
        }
    }

    private static String loadSql(String path)
    {
        try(InputStream is =RoutingInitializer.class
            .getClassLoader()
            .getResourceAsStream(path))
        {
            if(is == null)
                throw new RuntimeException("No existe script: " + path);

            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
        catch(Exception e)
        {
            throw new RuntimeException("Error leyendo " + path,e);
        }
    }
}