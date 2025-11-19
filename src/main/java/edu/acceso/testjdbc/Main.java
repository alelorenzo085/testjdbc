package edu.acceso.testjdbc;

import java.nio.file.Path;
import java.sql.Connection;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;

public class Main {

    public static void main(String[] args) throws Exception {
        
        final
       String dbProtocol = "jdbc:sqlite:";
        Path dbPath = Path.of(System.getProperty("java.io.tmpdir"), "test.db");
        String dbUrl = String.format("%s%s", dbProtocol, dbPath);

        // Configuramos el acceso.
        HikariConfig hconfig = new HikariConfig();
        hconfig.setJdbcUrl(dbUrl);
        // En SQLite no hay credenciales de acceso.
        hconfig.setUsername(null);
        hconfig.setPassword(null);
        // Máximo y mínimo de conexiones
        hconfig.setMaximumPoolSize(10);  // Nunca se abrirán más de diez conexiones.
        hconfig.setMinimumIdle(1);       // Al menos habrá una conexión.

        HikariDataSource ds = new HikariDataSource(hconfig);
        HikariPoolMXBean stats = ds.getHikariPoolMXBean(); // Para consultar estadísticas.

        // Como el mínimo es una conexión, ya hay una conexión creada.
        System.out.println(String.format("Conexiones activas/totales: %d/%d", stats.getActiveConnections(), stats.getTotalConnections()));  // 0/1

        try(Connection conn1 = ds.getConnection()) {
        // ...
        System.out.println(String.format("activas/totales: %d/%d", stats.getActiveConnections(), stats.getTotalConnections()));  // 1/1
        }

        System.out.println(String.format("activas/totales: %d/%d", stats.getActiveConnections(), stats.getTotalConnections()));  // 0/1

        try(Connection conn1 = ds.getConnection()) {
        // ...
        System.out.println(String.format("activas/totales: %d/%d", stats.getActiveConnections(), stats.getTotalConnections()));  // 1/1
        try(Connection conn2 = ds.getConnection()) {  // Crea una conexión nueva.
            // ...
            System.out.println(String.format("activas/totales: %d/%d", stats.getActiveConnections(), stats.getTotalConnections()));  // 2/2
        }

        System.out.println(String.format("activas/totales: %d/%d", stats.getActiveConnections(), stats.getTotalConnections()));  // 1/2
        }

        System.out.println(String.format("activas/totales: %d/%d", stats.getActiveConnections(), stats.getTotalConnections()));  // 0/2

        ds.close();  // Se liberan recursos.
            }
        }
