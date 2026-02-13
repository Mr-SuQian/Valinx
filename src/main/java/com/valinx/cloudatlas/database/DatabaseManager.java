package com.valinx.cloudatlas.database;

import com.valinx.kernel.config.AppConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseManager {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);
    private static HikariDataSource dataSource;

    public static void initialize() {
        if (dataSource != null)
            return;

        logger.info("Initializing Database Connection Pool...");
        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(AppConfig.get("db.url"));
            config.setUsername(AppConfig.get("db.user"));
            config.setPassword(AppConfig.get("db.password"));

            // Standard performance settings
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setIdleTimeout(30000);
            config.setMaxLifetime(1800000);

            dataSource = new HikariDataSource(config);
            logger.info("Database connected successfully.");
        } catch (Exception e) {
            logger.error("Failed to connect to database.", e);
            throw new RuntimeException("Database init failed", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null)
            initialize();
        return dataSource.getConnection();
    }

    public static void close() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}
