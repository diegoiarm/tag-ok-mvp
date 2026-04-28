package com.roony.infrastructure.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.Properties;

public final class DataBaseConfiguration
{
    private static final DataSource DATASOURCE = createDataSource();

    private DataBaseConfiguration() {}

    public static DataSource getDataSource()
    {
        return DATASOURCE;
    }

    private static DataSource createDataSource()
    {
        Properties props = loadProperties();

        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(
            props.getProperty("jdbc.url",
                "jdbc:postgresql://localhost:5432/tagok_db"));

        config.setUsername(
            props.getProperty("jdbc.username",
                "postgres"));

        config.setPassword(
            props.getProperty("jdbc.password",
                "postgres"));

        config.setMaximumPoolSize(8);
        config.setMinimumIdle(2);

        return new HikariDataSource(config);
    }

    private static Properties loadProperties()
    {
        Properties p = new Properties();

        try(InputStream in=DataBaseConfiguration.class
            .getClassLoader()
            .getResourceAsStream("application.properties"))
        {
            if(in != null)
                p.load(in);
        }
        catch(Exception ignored){}

        return p;
    }
}