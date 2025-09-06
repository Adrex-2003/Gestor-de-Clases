package com.miapp.web.servidor_web.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.miapp.web.servidor_web.models.Estudiante;
import com.miapp.web.servidor_web.repositories.EstudianteRepository;

@RestController
@RequestMapping("/api/estudiantes")
public class EstudianteController {

    private final EstudianteRepository estudianteRepo;

    public EstudianteController(EstudianteRepository estudianteRepo) {
        this.estudianteRepo = estudianteRepo;
    }

    // Registrar un nuevo estudiante
    @PostMapping
    public ResponseEntity<String> registrarEstudiante(@RequestBody Estudiante estudiante) {
        try {
            estudianteRepo.guardar(estudiante);
            return ResponseEntity.ok("Estudiante registrado correctamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    
}
