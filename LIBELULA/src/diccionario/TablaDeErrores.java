package diccionario;

/**
 * Clase que maneja una tabla de errores para un compilador. Esta tabla contiene
 * una serie de errores posibles que pueden ser generados durante el proceso de
 * compilación.
 */
public class TablaDeErrores {

    /**
     * Enumeración que define los distintos errores posibles que pueden ocurrir
     * durante el proceso de compilación. Cada error tiene una descripción
     * asociada que se puede obtener mediante el método getDescripcion().
     */
    public static enum Errores {
        ERROR_01("ERROR001: El tipo del TOKEN detectado es (DESCONOCIDO)."),
        ERROR_02("ERROR02: El TOKEN detectado no cumple la sintaxis."),
        ERROR_00("Advertencia: El TOKEN detectado no es soportado por esta versión."),
        ERROR_03("ERROR03: No se puede declarar variables antes de (MODULE)."),
        ERROR_04("ERROR04: Falta punto y coma (;)."),
        ERROR_05("ERROR05: El identificador contiene caracteres no permitidos."),
        ERROR_06("ERROR06: Existe más de un punto y coma (;) en la línea."),
        ERROR_07("ERROR07: Existe un lexema después de punto y coma (;)."),
        ERROR_08("ERROR08: A la sentencia le hace falta dos puntos (:)."),
        ERROR_09("ERROR09: El comando de lectura ejecutado despues de (END)."),
        ERROR_10("ERROR10: El comando de lectura ejecutado antes de (BEGIN)."),
        ERROR_11("ERROR11: El comando de lectura ejecutado antes de (VAR)."),
        ERROR_12("ERROR12: No se puede inicializar variables antes de (VAR)."),
        ERROR_13("ERROR13: El comando de asignación se ejecuto antes de (BEGIN)."),
        ERROR_14("ERROR14: Se detecto una palabra reservada antes de (BEGIN)."),
        ERROR_15("ERROR15: El comando es ínvalido ya que no cumple la sintaxis de: "),
        ERROR_M1("ERROR_M1: Falta el comando (MODULE) en el archivo."),
        ERROR_M2("ERROR_M2: El comando (MODULE) ya fue declarado."),
        ERROR_M3("ERROR_M3: El comando (MODULE) no se declaró correctamente."),
        ERROR_N1("ERROR_N1: El nombre del programa no coincide."),
        ERROR_N2("ERROR_N2: El nombre del programa no se declaró correctamente."),
        ERROR_N3("ERROR_N3: No se declaró el nombre del programa."),
        ERROR_VA1("ERROR_VA1: El comando (VAR) no se declaró correctamente."),
        ERROR_VA2("ERROR_VA2: El comando (BEGIN) se declaró antes del (VAR)."),
        ERROR_VA3("ERROR_VA3: El comando (VAR) detectado anteriormente."),
        ERROR_VA4("ERROR_VA4: El comando (MODULE) no se declaró antes del comando (VAR)."),
        ERROR_VA5("ERROR_VA5: La variable se declaró despues del cierre de (VAR): "),
        ERROR_A1("ERROR_A1: La asignación no se declaró correctamente."),
        ERROR_A2("ERROR_A2: No se declaró anteriormente la variable a inicializar."),
        ERROR_A3("ERROR_A3: La sintaxis de la asignación es erronea."),
        ERROR_B1("ERROR_B1: Falta el comando (BEGIN) en el archivo."),
        ERROR_B2("ERROR_B2: El comando (BEGIN) ya fue declarado."),
        ERROR_B3("ERROR_B3: El comando (BEGIN) no se declaró correctamente."),
        ERROR_B4("ERROR_B3: El comando (BEGIN) no se declaró antes de la asignación de variables."),
        ERROR_C1("ERROR_C1: No se encontró el inicio del comentario."),
        ERROR_C2("ERROR_C2: No se encontró el fin del comentario."),
        ERROR_C3("ERROR_C3: El comentario no se declaró correctamente."),
        ERROR_E1("ERROR_E1: El archivo no contiene la declaración (END nombre_programa.)"),
        ERROR_E2("ERROR_E2: Anteriormente se detectó el comando: (END)"),
        ERROR_E3("ERROR_E3: El comando (END) no se declaró correctamente."),
        ERROR_E4("ERROR_E3: Comando invalido, se declaró (END nombre_programa.) antes del comando."),
        ERROR_L1("ERROR_L1: La línea excede el tamaño permitido."),
        ERROR_R1("ERROR_R1: El comando (READ) no se declaró correctamente."),
        ERROR_R2("ERROR_R2: La variable no es compatible con el comando (READ)."),
        ERROR_R3("ERROR_R3: El comando (READINT) no se declaró correctamente."),
        ERROR_R4("ERROR_R4: La variable no es compatible con el comando (READINT)."),
        ERROR_R5("ERROR_R5: El comando (READREAL) no se declaró correctamente."),
        ERROR_R6("ERROR_R6: La variable no es compatible con el comando (READREAL)."),
        ERROR_R7("ERROR_R7: No se declaró anteriormente la variable a leer."),
        ERROR_R8("ERROR_R8: Error en la declaración: "),
        ERROR_W0("ERROR_W0: No se declaró anteriormente la variable a escribir."),
        ERROR_W1("ERROR_W1: Error en la sintaxis de la declaración, falta (;) luego de WriteLn"),
        ERROR_W2("ERROR_W2: El comando (WRITE) no se declaró correctamente."),
        ERROR_W3("ERROR_W3: La variable no es compatible con el comando (WRITE)."),
        ERROR_W4("ERROR_W4: El comando (WRITEINT) no se declaró correctamente."),
        ERROR_W5("ERROR_W5: La variable no es compatible con el comando (WRITEINT)."),
        ERROR_W6("ERROR_W6: El comando (WRITEREAL) no se declaró correctamente."),
        ERROR_W7("ERROR_W7: La variable no es compatible con el comando (WRITEREAL)."),
        ERROR_W8("ERROR_W8: Hace falta un componente de la sintaxis del comando (WriteString)."),
        ERROR_W9("ERROR_W9: El tamaño asignado es mayor a 20."),
        ERROR_W10("ERROR_W10: El tamaño asignado es menor a 0."),
        ERROR_W11("ERROR_W11: El tamaño del texto es mayor a 60 caracteres."),
        ERROR_RT1("ERROR_RT1: Error en la declaración de (RETURN). "),
        ERROR_RP1("ERROR_RP1: La declaración de (REPEAT) no es válida."),
        ERROR_RP2("ERROR_RP2: Ya existe un búcle (REPEAT)."),
        ERROR_RP3("ERROR_RP3: No se permite un comando (REPEAT) dentro de otro."),
        ERROR_UN1("ERROR_UN1: El comando (UNTIL) se declaró previo a (RETURN)."),
        ERROR_UN2("ERROR_UN2: La declaración de (UNTIL) no coindice con su sintaxis."),
        ERROR_UN3("ERROR_UN3: Un argumento de la condición (UNTIL) no está declarado."),
        ERROR_UN4("ERROR_UN4: El comando (UNTIL) se declaró anteriormente."),
        ERROR_UN5("ERROR_UN5: La declaración del comando (UNTIL) no coincide con el formato."),
        ERROR_UN6("ERROR_UN6: No hay comandos válidos entre (REPEAT) y (UNTIL)."),
        ERROR_IF1("ERROR_IF1: La declaración de (IF-THEN) no coindice con su sintaxis."), 
        ERROR_IF2("ERROR_IF2: Un argumento de la condición (IF-THEN) no está declarado."),
        ERROR_IF3("ERROR_IF3: No se encontró el final del bucle, falta (END;)."),
        ERROR_IF4("ERROR_IF4: No hay comandos válidos entre (IF) y (END)."),
        ERROR_IF5("ERROR_IF5: No hay comandos válidos entre (IF) y (ELSE)."),
        ERROR_IF6("ERROR_IF6: No hay comandos válidos entre (ELSE) y (END).");

        private final String descripcion;

        /**
         * Constructor privado de la enumeración Errores. Cada error se
         * inicializa con una descripción.
         */
        private Errores(String descripcion) {
            this.descripcion = descripcion;
        }

        /**
         * Método que devuelve la descripción del error.
         *
         * @return String con la descripción del error.
         */
        public String getDescripcion() {
            return descripcion;
        }
    }
}
