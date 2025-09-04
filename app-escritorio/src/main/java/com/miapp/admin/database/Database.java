package com.miapp.admin.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    private static final Logger logger = LoggerFactory.getLogger(Database.class);

    // Por defecto usa archivo relativo a la carpeta desde la cual inicias la JVM:
    // ejemplo:
    // D:\Dev\Java\springBoot\RegistroAsistencia\database\registro_asistencia.db
    private static final String DB_PATH = System.getProperty("db.path",
            "D:\\Dev\\Java\\springBoot\\RegistroAsistencia\\base-datos\\Registro.db");
    private static final String URL = "jdbc:sqlite:" + DB_PATH;

    public static Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection(URL);
            logger.info("Conexión a SQLite exitosa ({}).", DB_PATH);
            crearTablasSiNoExisten(conn);
            return conn;
        } catch (SQLException e) {
            logger.error("Error al conectar a la base de datos SQLite en {}: {}", DB_PATH, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private static void crearTablasSiNoExisten(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            logger.info("Verificando/creando tablas...");

            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS Estudiantes (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "ru TEXT UNIQUE NOT NULL," +
                            "ci TEXT UNIQUE NOT NULL," +
                            "aPaterno TEXT NOT NULL," +
                            "aMaterno TEXT NOT NULL," +
                            "nombre TEXT NOT NULL" +
                            ");");

            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS Sesiones (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "descripcion TEXT NOT NULL," +
                            "token_qr TEXT UNIQUE NOT NULL," +
                            "fecha TEXT NOT NULL," +
                            "fecha_expiracion_qr TEXT NOT NULL" +
                            ");");

            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS Asistencias (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "id_estudiante INTEGER NOT NULL," +
                            "id_sesion INTEGER NOT NULL," +
                            "presente INTEGER NOT NULL DEFAULT 0," +
                            "hora_registro TEXT," +
                            "FOREIGN KEY(id_estudiante) REFERENCES Estudiantes(id)," +
                            "FOREIGN KEY(id_sesion) REFERENCES Sesiones(id)," +
                            "UNIQUE(id_estudiante, id_sesion)" +
                            ");");

            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS Participaciones (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "id_estudiante INTEGER NOT NULL," +
                            "descripcion TEXT NOT NULL," +
                            "puntos REAL NOT NULL," +
                            "fecha TEXT NOT NULL," +
                            "FOREIGN KEY(id_estudiante) REFERENCES Estudiantes(id)" +
                            ");");
            // ===========================
            // Tabla: Practicas
            // ===========================
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS Practicas (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "titulo TEXT NOT NULL," +
                        "descripcion TEXT," +
                        "fecha_inicio TEXT NOT NULL," +
                        "fecha_fin TEXT NOT NULL," +
                        "tipo TEXT NOT NULL" + // 'auxiliar' o 'docente'
                        ");");

            // ===========================
            // Tabla: NotasPracticas
            // ===========================
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS NotasPracticas (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "practica_id INTEGER NOT NULL," +
                        "estudiante_id INTEGER NOT NULL," +
                        "nota REAL," +
                        "FOREIGN KEY(practica_id) REFERENCES Practicas(id) ON DELETE CASCADE," +
                        "FOREIGN KEY(estudiante_id) REFERENCES Estudiantes(id) ON DELETE CASCADE" +
                        ");");

            logger.info("Tablas verificadas/creadas correctamente.");
        } catch (SQLException e) {
            logger.error("Error al crear/verificar tablas: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
