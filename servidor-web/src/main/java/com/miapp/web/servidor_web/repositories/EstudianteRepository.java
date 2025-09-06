package com.miapp.web.servidor_web.repositories;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.miapp.web.servidor_web.models.Estudiante;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class EstudianteRepository {

    private final JdbcTemplate jdbcTemplate;

    public EstudianteRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Registrar un nuevo estudiante
    public void guardar(Estudiante estudiante) {
        String sql = "INSERT INTO Estudiantes (ru, ci, aPaterno, aMaterno, nombre) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                estudiante.getRu(),
                estudiante.getCi(),
                estudiante.getaPaterno(),
                estudiante.getaMaterno(),
                estudiante.getNombre());
    }

    // Listar todos los estudiantes
    public List<Estudiante> listarTodos() {
        String sql = "SELECT * FROM Estudiantes";
        return jdbcTemplate.query(sql, new RowMapper<>() {
            @Override
            public Estudiante mapRow(ResultSet rs, int rowNum) throws SQLException {
                Estudiante e = new Estudiante();
                e.setId(rs.getInt("id"));
                e.setRu(rs.getString("ru"));
                e.setCi(rs.getString("ci"));
                e.setaPaterno(rs.getString("aPaterno"));
                e.setaMaterno(rs.getString("aMaterno"));
                e.setNombre(rs.getString("nombre"));
                return e;
            }
        });
    }
}

