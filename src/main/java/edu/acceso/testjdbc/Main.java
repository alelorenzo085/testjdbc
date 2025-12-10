package edu.acceso.testjdbc;

import java.io.IOException;
import java.time.LocalDate;

import javax.sql.DataSource;

import edu.acceso.testjdbc.backend.Conexion;
import edu.acceso.testjdbc.backend.dao.CentroDao;
import edu.acceso.testjdbc.backend.dao.EstudianteDao;
import edu.acceso.testjdbc.domain.Centro;
import edu.acceso.testjdbc.domain.Estudiante;
import edu.acceso.testjdbc.domain.Titularidad;

import edu.acceso.sqlutils.errors.DataAccessException;

public class Main {

    public static void hacerTransaccion() throws DataAccessException {
        DataSource ds = Conexion.get();

        Conexion.transaction(ds, (cDao,eDao) -> {
            eDao.remove(1);
            cDao.insert(new Centro(11004866, "xxxx", Titularidad.PUBLICA));
        });
    }

    public static void main(String[] args) {
        String url = "file::memory:?cache=shared";

        DataSource ds = null;
        try {
            ds = Conexion.create(url, "resources:/centros.sql");
            System.out.println("Hemos logrado conectar a la base de datos");
        } catch (IOException e){
            System.err.println("Es imposible acceder a la base de datos");
        } catch (DataAccessException e) {
            System.err.println("Error al iniciar la base de datos. "+ e.getMessage());
        }

        Centro[] centros = new Centro[] {
            new Centro(11004866, "IES Castillo de Luna", Titularidad.PUBLICA),
            new Centro(11700602, "IES Pintor Juan Lara", Titularidad.PUBLICA),
            new Centro(11004039, "IES SIDON" , Titularidad.PUBLICA),
            new Centro(21002100, "IES Pade José Miravent", Titularidad.PUBLICA)
        };

        try {
            CentroDao centroDao = new CentroDao(ds);
            EstudianteDao estudianteDao = new EstudianteDao(ds);

            // Agrego los centros a la base de datos
            centroDao.insert(centros);

            System.out.println("--- LISTA DE CENTROS ---");
            centroDao.get().forEach(System.out::println);
            System.out.println("---- ************** ----");

            System.out.println("---- **** ------");
            System.out.println(centroDao.get(11004866));
            System.out.println("---- **** ------");

            Estudiante[] estudiantes = new Estudiante[] {
                new Estudiante(null, "Perico de los Palotes", LocalDate.of(2000, 01, 01), centros[0]),
                new Estudiante(null, "Segismundo", LocalDate.of(2002, 02, 02), null)
            };

            estudianteDao.insert(estudiantes);

            System.out.println("--- LISTA DE ESTUDIANTES ---");
            for(Estudiante estudiante: estudianteDao.get()) {
                System.out.printf("Estudiante %d: %s.\n", estudiante.getId(), estudiante);
            }
            estudianteDao.get().forEach(System.out::println);
            System.out.println("---- ************** ----");

            try {
                hacerTransaccion();
            } catch (DataAccessException e) {
                System.err.println("Error en la transacción: " + e.getMessage());
            }

            System.out.println("--- Estudiante que hay en la base de daatos ---");
            estudianteDao.get().forEach(System.out::println);
        }
        catch(DataAccessException err) {
            err.printStackTrace();
            System.err.println("Error de conexión. " + err.getMessage());
        }
    }
}