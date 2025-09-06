package com.miapp.web.servidor_web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import org.sqlite.SQLiteDataSource;

@Configuration
public class DataSourceConfig {

    @Bean
    public DataSource dataSource() {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        // Ruta absoluta de tu base de datos
        dataSource.setUrl("jdbc:sqlite:D:/Dev/Java/springBoot/RegistroAsistencia/base-datos/Registro.db");
        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}

