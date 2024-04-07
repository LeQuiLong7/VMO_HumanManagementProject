package com.lql.humanresourcedemo.config;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class FlywayConfig {
    @Autowired
    public void flywayConfiguration(DataSource dataSource) {
        Flyway.configure().baselineOnMigrate(true).baselineVersion("0").dataSource(dataSource).load().migrate();
    }
}
