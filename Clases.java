//Universidad del valle de Guatemala - POO
// Mauricio Enrique Montenegro Gonzalez - 23679
// Laboratorio 2
// El programa deberá permitir cargar los archivos csv
import java.util.*;
import java.io.*;

enum DiaSemana {
    LUNES, MARTES, MIERCOLES, JUEVES, VIERNES, SABADO, DOMINGO
}

class Sede {
    int id_sede;
    Map<String, Edificio> edificios;
    Map<Integer, Clase> cursos;

    public Sede(int id_sede) {
        this.id_sede = id_sede;
        this.edificios = new HashMap<>();
        this.cursos = new HashMap<>();
    }

    public Edificio getEdificio(String idEdificio) {
        return edificios.get(idEdificio);
    }

    public void agregarEdificio(Edificio edificio) {
        this.edificios.put(edificio.id_edificio, edificio);
    }

    public void agregarCurso(Clase curso) {
        this.cursos.put(curso.id_curso, curso);
    }
}

class Edificio {
    String id_edificio;
    Map<String, Salon> salones;

    public Edificio(String id_edificio) {
        this.id_edificio = id_edificio;
        this.salones = new HashMap<>();
    }

    public Salon getSalon(String idSalon) {
        return salones.get(idSalon);
    }

    public void agregarSalon(Salon salon) {
        this.salones.put(salon.id_salon, salon);
    }
}

class Salon {
    String id_salon;
    int nivel;
    int capacidad;
    Map<Integer, Clase> horario;

    public Salon(String id_salon, int nivel, int capacidad) {
        this.id_salon = id_salon;
        this.nivel = nivel;
        this.capacidad = capacidad;
        this.horario = new HashMap<>();
        for (int i = 7; i <= 21; i++) {
            this.horario.put(i, null);
        }
    }

    public boolean estaDisponible(int hora, int duracion) {
        for (int i = hora; i < hora + duracion; i++) {
            if (this.horario.get(i) != null) {
                return false;
            }
        }
        return true;
    }

    public void asignarClase(Clase clase) {
        if (estaDisponible(clase.horario, clase.duracion) && clase.cantidad_estudiantes <= this.capacidad) {
            for (int i = clase.horario; i < clase.horario + clase.duracion; i++) {
                this.horario.put(i, clase);
            }
        }
    }

    public void mostrarHorario() {
        for (int i = 7; i <= 21; i++) {
            Clase clase = this.horario.get(i);
            if (clase == null) {
                System.out.println("Hora: " + i + ":00 - Libre");
            } else {
                System.out.println("Hora: " + i + ":00 - Ocupado por la clase " + clase.nombre_clase);
            }
        }
    }
}

class Clase {
    int id_curso;
    String nombre_clase;
    int horario;
    int duracion;
    List<DiaSemana> dias;
    int cantidad_estudiantes;

    public Clase(int id_curso, String nombre_clase, int horario, int duracion, List<DiaSemana> dias, int cantidad_estudiantes) {
        this.id_curso = id_curso;
        this.nombre_clase = nombre_clase;
        this.horario = horario;
        this.duracion = duracion;
        this.dias = dias;
        this.cantidad_estudiantes = cantidad_estudiantes;
    }

    public int getIdCurso() {
        return id_curso;
    }

    public String getNombreClase() {
        return nombre_clase;
    }

    public int getHorario() {
        return horario;
    }

    public int getDuracion() {
        return duracion;
    }

    public List<DiaSemana> getDias() {
        return dias;
    }

    public int getCantidadEstudiantes() {
        return cantidad_estudiantes;
    }
}

class Universidad {
    Map<Integer, Sede> sedes;

    public Universidad() {
        this.sedes = new HashMap<>();
    }

    public void cargarCursos(String archivoCursos) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(archivoCursos))) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int id_curso = Integer.parseInt(parts[0].trim());
                int id_sede = Integer.parseInt(parts[1].trim());
                String nombre_curso = parts[2].trim();

                String[] diasArray = parts[3].trim().split(",");
                List<DiaSemana> dias = new ArrayList<>();
                for (String dia : diasArray) {
                    try {
                        dias.add(DiaSemana.valueOf(dia.trim().toUpperCase()));
                    } catch (IllegalArgumentException e) {
                        System.out.println("Error al convertir el día: " + dia);
                        throw e;
                    }
                }

                int horario = Integer.parseInt(parts[4].trim());
                int duracion = Integer.parseInt(parts[5].trim());
                int cantidad_estudiantes = Integer.parseInt(parts[6].trim());

                Sede sede = getSede(id_sede);
                if (sede != null) {
                    Clase clase = new Clase(id_curso, nombre_curso, horario, duracion, dias, cantidad_estudiantes);
                    sede.agregarCurso(clase);
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public void cargarSalones(String archivoSalones) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(archivoSalones));
        String line;
        boolean primeraLinea = true;
        while ((line = reader.readLine()) != null) {
            if (primeraLinea) {
                primeraLinea = false;
                continue;
            }
            String[] parts = line.split(",");
            int id_sede = Integer.parseInt(parts[0].trim());
            String id_edificio = parts[1].trim();
            int nivel = Integer.parseInt(parts[2].trim());
            String id_salon = parts[3].trim();
            int capacidad = Integer.parseInt(parts[4].trim());
            Salon salon = new Salon(id_salon, nivel, capacidad);
            Sede sede = getSede(id_sede);
            if (sede != null) {
                Edificio edificio = sede.getEdificio(id_edificio);
                if (edificio != null) {
                    edificio.salones.put(id_salon, salon);
                }
            }
        }
        reader.close();
    }

    public Sede getSede(int idSede) {
        return sedes.get(idSede);
    }

    public void asignarSalones() {
        for (Sede sede : sedes.values()) {
            for (Edificio edificio : sede.edificios.values()) {
                for (Salon salon : edificio.salones.values()) {
                    for (Clase clase : sede.cursos.values()) {
                        if (salon.estaDisponible(clase.horario, clase.duracion) && clase.cantidad_estudiantes <= salon.capacidad) {
                            salon.asignarClase(clase);
                            break;
                        }
                    }
                }
            }
        }
    }

    public void mostrarInforme() {
        for (Sede sede : sedes.values()) {
            for (Edificio edificio : sede.edificios.values()) {
                for (Salon salon : edificio.salones.values()) {
                    System.out.println("Sede: " + sede.id_sede + ", Edificio: " + edificio.id_edificio + ", Salon: " + salon.id_salon);
                    salon.mostrarHorario();
                }
            }
        }
    }

    public void exportarResultados(String formato) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("resultados." + formato))) {
            for (Sede sede : sedes.values()) {
                for (Edificio edificio : sede.edificios.values()) {
                    for (Salon salon : edificio.salones.values()) {
                        writer.write("Sede: " + sede.id_sede + ", Edificio: " + edificio.id_edificio + ", Salon: " + salon.id_salon + "\n");
                        for (Map.Entry<Integer, Clase> entry : salon.horario.entrySet()) {
                            Clase clase = entry.getValue();
                            if (clase != null) {
                                writer.write("Hora: " + entry.getKey() + ":00 - Ocupado por la clase " + clase.nombre_clase + "\n");
                            } else {
                                writer.write("Hora: " + entry.getKey() + ":00 - Libre\n");
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error al exportar resultados: " + e.getMessage());
        }
    }
}

