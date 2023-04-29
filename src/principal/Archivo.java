package principal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import analizador.AnalizadorLexico;
import analizador.Analizador;
import sintactico.Validaciones;

public class Archivo {

    public void aperturaArchivo(String archivoLid) {

        Validaciones validarNombre = new Validaciones();
        ArrayList<String> lineasArchivo = new ArrayList<>();
        archivoLid = validarNombre.validarNombreArchivo(archivoLid); //Valida que posea el nombre y la extensión .lid, y en caso de no tenerla, la agrega.
        String nombreArchivo = archivoLid.substring(0, archivoLid.length() - 4); // Elimina la extensión .lid para crear el archivo de errores.
        String nombreArchivoErrores = nombreArchivo + "-errores.LID"; // Agrega la extensión -errores
        File archivoErrores = new File(nombreArchivoErrores); //Crea el archivo de errores.

        // Elimina archivo de errores existente.
        if (archivoErrores.exists()) {
            archivoErrores.delete();
        }

        try (FileReader scanFile = new FileReader(archivoLid); BufferedReader br = new BufferedReader(scanFile)) {
            String linea;
            while ((linea = br.readLine()) != null) {
                lineasArchivo.add(linea);
                System.out.println(linea);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        procesarArchivo(lineasArchivo.toArray(new String[0]), archivoErrores);

    }

    public void procesarArchivo(String[] readerStrings, File archivoErrores) {
        int contadorErrores = 0;
        StringBuilder erroresConcatenados = new StringBuilder();

        try (FileWriter writer = new FileWriter("temp.lid")) {

            for (int i = 0; i < readerStrings.length; i++) {
                Utilidades.revisionDeArchivo = readerStrings;
                String linea = readerStrings[i];

                //IMPLEMENTACIÓN DEL ANALISIS LÉXICO
                List<String> erroresLexicos = analizadorLexico(linea);

                //IMPLEMENTACIÓN DEL ANALISIS SINTACTICO
                List<String> erroresSintacticos = analizadorSintactico(linea);

                StringBuilder erroresDetectados = new StringBuilder();

                //LEXICO
                if (!erroresLexicos.isEmpty()) {
                    for (String error : erroresLexicos) {
                        erroresDetectados.append("\n        ").append(error);
                        contadorErrores++;
                    }
                }

                //SINTACTICO
                if (!erroresSintacticos.isEmpty()) {
                    for (String error : erroresSintacticos) {
                        erroresDetectados.append("\n        ").append(error);
                        contadorErrores++;
                    }
                }

                //Se llama la función write para almacenar la linea ya revisada y sus errores en caso de tenerlos.
                writer.write(String.format("%05d", i + 1) + " " + linea + erroresDetectados.toString() + "\n");
                erroresConcatenados.append(erroresDetectados.toString());
            }

            //MANEJO DE ERRORES GLOBALES
            Validaciones validacionSintactica = new Validaciones();
            String erroresGlobalesModule = validacionSintactica.validarArchivoModule(readerStrings);
            if (!"".equals(erroresGlobalesModule)) {
                writer.write("        " + erroresGlobalesModule + "\n");
                erroresConcatenados.append("\n        ").append(erroresGlobalesModule);
                contadorErrores++;
            }

            String erroresGlobalesBegin = validacionSintactica.validarArchivoBegin(readerStrings);
            if (!"".equals(erroresGlobalesBegin)) {
                writer.write("        " + erroresGlobalesBegin + "\n");
                erroresConcatenados.append("\n        ").append(erroresGlobalesBegin);
                contadorErrores++;
            }

            String erroresGlobalesEnd = validacionSintactica.validarArchivoEnd(readerStrings);
            if (!"".equals(erroresGlobalesEnd)) {
                writer.write("        " + erroresGlobalesEnd + "\n");
                erroresConcatenados.append("\n        ").append(erroresGlobalesEnd);
                contadorErrores++;
            }

            String erroresGlobalesNombre = validacionSintactica.validarNombrePrograma(readerStrings);
            if (!"".equals(erroresGlobalesNombre)) {
                writer.write("        " + erroresGlobalesNombre + "\n");
                erroresConcatenados.append("\n        ").append(erroresGlobalesNombre);
                contadorErrores++;
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("Se encontraron " + contadorErrores + " errores.");

        File archivoTemporal = new File("temp.lid");

        if (contadorErrores > 0) {
            archivoTemporal.renameTo(archivoErrores);
        } else {
            archivoTemporal.delete();
        }

    }

    private List<String> analizadorLexico(String linea) {
        AnalizadorLexico analisisLexico = new AnalizadorLexico();
        return analisisLexico.analizadorLexico(linea);
    }

    private List<String> analizadorSintactico(String linea) {
        Analizador analisisSintactico = new Analizador();
        return analisisSintactico.analizadorSintactico(linea);
    }

}
