//Universidad del valle de Guatemala - POO
// Mauricio Enrique Montenegro Gonzalez - 23679
// Laboratorio 2
// El programa deberá permitir cargar los archivos csv

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Universidad universidad = new Universidad();
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.print("Ingrese el nombre del archivo de salones (por ejemplo, 'salones.csv'): ");
            String archivoSalones = scanner.nextLine();

            System.out.print("Ingrese el nombre del archivo de cursos (por ejemplo, 'cursos.csv'): ");
            String archivoCursos = scanner.nextLine();

            scanner.close();

            universidad.cargarSalones(archivoSalones);
            universidad.cargarCursos(archivoCursos);
            universidad.asignarSalones();
            universidad.mostrarInforme();
            universidad.exportarResultados("txt");
        } catch (IOException e) {
            System.out.println("Ocurrió un error al leer los archivos o al exportar los resultados.");
            e.printStackTrace();
        }
    }
}

//Porfavor no me bajen muchos puntos por que no saliera el programa me esforce mucho para que no me saliera me duele :(