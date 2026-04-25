package com.tagok.infrastructure;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DataBaseConfiguration 
{
    private static final String PROPERTIES_FILE = "application.properties";
    private static final String ENV_PREFIX = "DB_";

    // Claves típicas
    private static final String JDBC_URL = "jdbc.url";
    private static final String USERNAME = "jdbc.username";
    private static final String PASSWORD = "jdbc.password";
    private static final String MAX_POOL_SIZE = "jdbc.maxPoolSize";
    private static final String MIN_IDLE = "jdbc.minIdle";

    public static DataSource getDataSource() 
    {
        Properties props = loadProperties();

        String url = getValue(JDBC_URL, props, "DB_URL", "jdbc:postgresql://localhost:5432/tagok_db");
        String user = getValue(USERNAME, props, "DB_USER", "postgres");
        String pass = getValue(PASSWORD, props, "DB_PASS", "postgres");

        int maxPoolSize = Integer.parseInt(getValue(MAX_POOL_SIZE, props, "DB_MAX_POOL", "4"));
        int minIdle = Integer.parseInt(getValue(MIN_IDLE, props, "DB_MIN_IDLE", "2"));

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(user);
        config.setPassword(pass);
        config.setMaximumPoolSize(maxPoolSize);
        config.setMinimumIdle(minIdle);

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        return new HikariDataSource(config);
    }

    private static Properties loadProperties() 
    {
        Properties props = new Properties();
        try (InputStream is = DataBaseConfiguration.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) 
        {
            if (is != null) 
                props.load(is);

        } catch (IOException e) 
        {
            // No es grave, se usarán valores por defecto
        }
        return props;
    }

    private static String getValue(String propertyKey, Properties props, String envKey, String defaultValue) 
    {
        // Prioridad: archivo properties > variable entorno > defecto
        if (props.containsKey(propertyKey)) 
            return props.getProperty(propertyKey);

        String envValue = System.getenv(ENV_PREFIX + envKey);

        if (envValue != null && !envValue.isBlank()) 
            return envValue;
        
        return defaultValue;
    }
}