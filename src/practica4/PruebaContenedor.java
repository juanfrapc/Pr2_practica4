package practica4;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Clase para pruebas de lista de enteros
 *
 * @author Grupo 2-47-1
 */
public class PruebaContenedor {

    /**
     * Main del programa de pruebas. Contiene pruebas de funcionamiento y
     * eficiencia con los ficheros datos.dat y no_datos.dat
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException, Exception {

        ContenedorDeEnteros arbol = new ContenedorDeEnteros();
        arbol.crear("temp", 5);
        PruebaContenedor.pruebasFuncionamiento(arbol);
        arbol.cerrar();
        arbol.crear("temp2", 10);
        PruebaContenedor.pruebasFuncionamiento(arbol);
        arbol.cerrar();

        RandomAccessFile file = new RandomAccessFile("datos.dat", "r");
        BufferedWriter writer = new BufferedWriter(new FileWriter(new File("salida4.txt")));
        int[] tamaños = {5, 7, 9, 11, 20, 25, 55, 75, 105, 201, 301};
        System.out.println("Comienzan las pruebas de rendimiento");

        writer.write("Pruebas de inserción \r\n");
        writer.write("---------------------------------------------------------"
                + "---------------------------------------------------\r\n");

        for (int tamaño : tamaños) {
            arbol.crear("temp", tamaño);
            writer.write("Árbol orden: " + tamaño + "\r\n");
            for (int i = 0; i < 10; i++) {
                writer.write("\r\t");
                PruebaContenedor.pruebasRendimientoInsercion(arbol, file, writer, i);
            }
        }

        writer.write("#########################################################"
                + "###################################################\r\n");

        file = new RandomAccessFile("datos.dat", "r");
        writer.write("Pruebas de extraccción \r\n");
        writer.write("---------------------------------------------------------"
                + "---------------------------------------------------\r\n");

        for (int tamaño : tamaños) {
            arbol.crear("temp", tamaño);
            writer.write("Árbol orden: " + tamaño);
            for (int i = 0; i < 10; i++) {
                writer.write("\r\t");
                PruebaContenedor.pruebasRendimientoExtraccion(arbol, file, writer, i);
            }
        }
        writer.write("#########################################################"
                + "###################################################\r\n");
        arbol.vaciar();

        file = new RandomAccessFile("datos.dat", "r");
        writer.write("Pruebas de búsqueda Exitosa \r\n");
        writer.write("---------------------------------------------------------"
                + "---------------------------------------------------\r\n");

        for (int tamaño : tamaños) {
            arbol.crear("temp", tamaño);
            writer.write("Árbol orden: " + tamaño);
            for (int i = 0; i < 10; i++) {
                writer.write("\r\t");
                PruebaContenedor.pruebasRendimientoBusquedaExitosa(arbol, file, writer, i);
            }
        }

        writer.write("#########################################################"
                + "###################################################\r\n");
        arbol.vaciar();

        file = new RandomAccessFile("datos.dat", "r");
        writer.write("Pruebas de búsqueda infructuosa \r\n");
        writer.write("---------------------------------------------------------"
                + "---------------------------------------------------\r\n");

        RandomAccessFile noFile = new RandomAccessFile("datos_no.dat", "r");

        int[] vectorAux = new int[(int) noFile.length() / 4];

        for (int tamaño : tamaños) {
            arbol.crear("temp", tamaño);
            writer.write("Árbol orden: " + tamaño);
            for (int i = 0; i < vectorAux.length; i++) {
                writer.write("\r\t");
                vectorAux[i] = noFile.readInt();
            }
        }

        for (int i = 0; i < 10; i++) {
            PruebaContenedor.pruebasRendimientoBusquedainfructuosa(arbol, vectorAux,
                    file, writer, i);
        }
        writer.write("#########################################################"
                + "###################################################\r\n");

        System.out.println("Terminan las pruebas de rendimiento");

        writer.close();
    }

    /**
     * Pruebas de comprobación del buen funcionamiento. Muestra mensajes por
     * pantalla cuando se produce un fallo.
     */
    private static void pruebasFuncionamiento(ContenedorDeEnteros arbol) throws Exception {

        int[] v;
        System.out.println("Empiezan las Pruebas de funcionamiento");
        if (arbol.cardinal() != 0) {
            System.out.println("El contenedor a tiene " + arbol.cardinal()
                    + " elementos y deberia tener 0.");
        }
        for (int i = 0; i < 11; i += 2) {
            arbol.insertar(i);
        }

        for (int i = 1; i < 11; i += 2) {
            arbol.insertar(i);
        }

        if (arbol.cardinal() != 11) {
            System.out.println("El contenedor a tiene " + arbol.cardinal()
                    + " elementos y deberia tener 11.");
        }

        for (int i = 0; i < 21; i++) {
            if (!arbol.buscar(i) && i < 11) {
                System.out.println("No se ha encontrado el elemento " + i);
            }
            if (arbol.buscar(i) && i >= 11) {
                System.out.println("Se ha encontrado el elemento " + i);
            }
        }

        for (int i = 0; i < 11; i++) {
            if (arbol.insertar(i)) {
                System.out.println("error insertar repetidos: " + i);
            }
        }

        v = arbol.elementos();
        if (v.length != arbol.cardinal()) {
            System.out.println("Fallo en longitud");
        }
        for (int i = 0; i < v.length; i++) {
            if (v[i] != i) {
                System.out.println("error en el elemento " + v[i]);
            }
        }

        for (int i = 21; i > 0; i -= 5) {
            if (!arbol.extraer(i) && i < 11) {
                System.out.println("No se ha extraido el elemento " + i);
            }
            if (arbol.extraer(i) && i >= 11) {
                System.out.println("Se ha extraido el elemento " + i);
            }
        }
        if (arbol.cardinal() != 9) {
            System.out.println("No se ha extraido correctamente");
        }

        for (int i = 1; i < 11; i += 2) {
            arbol.insertar(i);
        }

        arbol.vaciar();
        if (arbol.cardinal() != 0) {
            System.out.println("No se ha vaciado al lista");
        }
        for (int i = 0; i < 100; i++) {
            arbol.insertar(i);
            arbol.extraer(i);
            if (arbol.cardinal() != 0) {
                System.out.println("error final " + i);
            }
        }
        arbol.vaciar();
        System.out.println("Terminan las Pruebas de funcionamiento");
    }

    /**
     * Pruebas de rendimiento para la inserción de 10000 elementos
     *
     * @param i
     * @throws IOException
     */
    private static void pruebasRendimientoInsercion(ContenedorDeEnteros arbol,
            RandomAccessFile file, BufferedWriter writer, int i)
            throws IOException, Exception {

        int[] vectorAux = new int[(int) file.length() / 40];

        for (int j = 0; j < file.length() / 40; j++) {
            vectorAux[j] = file.readInt();
        }

        double empieza = System.currentTimeMillis(); //Empieza a contar el tiempo
        for (int j = 0; j < file.length() / 40; j++) {
            arbol.insertar(vectorAux[j]);
        }
        double termina = System.currentTimeMillis(); //Termina de contar el tiempo
        double tiempoParcial = (termina - empieza);

        writer.write("tiempo promedio (ms/1000) para las inserciones desde el elemento "
                + (i * 10000 + 1) + " hasta el " + (i + 1) * 10000 + " = "
                + tiempoParcial / 10 + " ms \r\n");
    }

    /**
     * Pruebas de rendimiento para la extracción de 10000 elementos
     *
     * @param i
     * @throws IOException
     */
    private static void pruebasRendimientoExtraccion(ContenedorDeEnteros arbol,
            RandomAccessFile file, BufferedWriter writer, int i)
            throws IOException, Exception {

        int[] vectorAux = new int[(int) file.length() / 40];

        for (int j = 0; j < file.length() / 40; j++) {
            vectorAux[j] = file.readInt();
        }

        double empieza = System.currentTimeMillis(); //Empieza a contar el tiempo
        for (int j = 0; j < file.length() / 40; j++) {
            arbol.extraer(vectorAux[j]);
        }
        double termina = System.currentTimeMillis(); //Termina de contar el tiempo
        double tiempoParcial = (termina - empieza);

        writer.write("tiempo promedio (ms/1000) para las extracciones desde el elemento "
                + (i * 10000 + 1) + " hasta el " + (i + 1) * 10000 + " = "
                + tiempoParcial / 10 + " ms \r\n");
    }

    /**
     * Pruebas de busqueda exitosa
     *
     * @param i
     * @throws IOException
     */
    private static void pruebasRendimientoBusquedaExitosa(ContenedorDeEnteros arbol,
            RandomAccessFile file, BufferedWriter writer, int i)
            throws IOException, Exception {

        RandomAccessFile fileAux = new RandomAccessFile("datos.dat", "r");

        for (int j = 0; j < file.length() / 40; j++) {
            arbol.insertar(file.readInt());
        }

        long elementos = fileAux.length() / 40 * (i + 1);// numero de elementos
        //a buscar, correspondeinte a la iteración

        int[] vectorAux = new int[(int) elementos];
        for (int j = 0; j < vectorAux.length; j++) {
            vectorAux[j] = fileAux.readInt();
        }

        double empieza = System.currentTimeMillis(); //Empieza a contar el tiempo
        for (int j = 0; j < elementos; j++) {
            arbol.buscar(vectorAux[j]);
        }
        double termina = System.currentTimeMillis(); //Termina de contar el tiempo
        double tiempoParcial = (termina - empieza) / 1000;

        writer.write("tiempo promedio (ms/1000) para la busqueda exitosa desde el elemento"
                + 0 + " hasta el " + (i + 1) * 10000 + " = "
                + (double) tiempoParcial * 1000 / elementos + " ms \r\n");

    }

    /**
     * Pruebas de búsqueda infructuosa
     *
     * @param i
     * @throws IOException
     */
    private static void pruebasRendimientoBusquedainfructuosa(ContenedorDeEnteros arbol,
            int[] vectorAux, RandomAccessFile file, BufferedWriter writer, int i)
            throws IOException, Exception {

        for (int j = 0; j < file.length() / 40; j++) {
            arbol.insertar(file.readInt());
        }

        double empieza = System.currentTimeMillis(); //Empieza a contar el tiempo
        for (int j = 0; j < vectorAux.length; j++) {
            arbol.buscar(vectorAux[j]);
        }

        double termina = System.currentTimeMillis(); //Termina de contar el tiempo
        double tiempoParcial = (termina - empieza) / 1000;

        writer.write("tiempo promedio (ms/1000) para la busqueda infructuosa con "
                + (i + 1) * 10000 + " elementos = " + tiempoParcial / 20 + " ms \r\n");

    }
}
