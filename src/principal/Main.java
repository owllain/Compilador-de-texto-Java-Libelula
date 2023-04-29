/**
 * Este paquete contiene la clase principal del compilador.
 */
package principal;

/**
 * La clase Main es la entrada al compilador. Toma un archivo .lid como
 * argumento, lee su contenido y llama al método principal para analizar el
 * código.
 */
public class Main {

    /**
     * El método main recibe un argumento que debe ser el archivo .lid que se
     * desea compilar. Pasa el nombre a la clase Archivo para ser procesado.
     *
     * @param args Un array de String que debe tener un solo elemento, el nombre
     * del archivo .lid a compilar.
     */
    public static void main(String[] args) {

        //Declaración de variables básicas.
        String archivoLid;

        // Validación de args
        if (args.length == 0) {
            System.out.println("Debe indicar la ruta de un archivo valido como argumento.");
            return;
        } else {
            archivoLid = args[0];
        }

        if (!"".equals(archivoLid)) {
            Archivo procesarArchivo = new Archivo();
            procesarArchivo.aperturaArchivo(archivoLid);
        }
        
    }

}
