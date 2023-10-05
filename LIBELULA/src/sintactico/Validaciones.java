package sintactico;

import diccionario.TablaDeAlfabeto;
import diccionario.TablaDeAlfabeto.Tipos;
import diccionario.TablaDeErrores;
import diccionario.TablaDeSintaxis;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import principal.Utilidades;

public class Validaciones {

    public Validaciones() {
    }

    //*************************************
    //VALIDACIONES PARA CONFIGURACIÓN
    //*************************************
    
    //REVISA SI LOS COMANDOS SON PREVIOS A BEGIN 
    public String validarComandos(String linea, String lexema) {
        String error = "";
        boolean ignoreLine = false;

        if (lexema.equals("VAR")) {
            ignoreLine = true;
        }

        if (lexema.matches(TablaDeAlfabeto.Tipos.Libelula.Patron) || lexema.matches(TablaDeAlfabeto.Tipos.Modula2.Patron)) {
            if (!lexema.equals("BEGIN") || !lexema.equals("MODULE") || !lexema.equals("VAR")) {
                if (!linea.matches(TablaDeSintaxis.ReglasGramaticales.INICIO_PROGRAMA.getPatron())
                        && !linea.matches(TablaDeSintaxis.ReglasGramaticales.PALABRA_RESERVADA_BEGIN.getPatron())) {

                    if (Utilidades.contadorVar != 0 && (linea.matches(TablaDeSintaxis.ReglasGramaticales.DECLARACION_VARIABLE.getPatron())
                            || linea.matches(TablaDeSintaxis.ReglasGramaticales.MULTIPLES_DECLARACIONES.getPatron()))) {
                        ignoreLine = true;
                    }

                    if (ignoreLine == false) {
                        error = TablaDeErrores.Errores.ERROR_14.getDescripcion();
                    }
                }
            }
        }

        return error;
    }

    /**
     * Valida que la longitud de una línea no exceda los 100 caracteres.
     *
     * @param linea La línea a validar.
     * @return null si la línea es válida o un String con la descripción del
     * errorDeclaracion correspondiente.
     */
    public String validarLongitud(String linea) {
        if (linea.length() > 80) {
            return TablaDeErrores.Errores.ERROR_L1.getDescripcion();
        }
        return null; // devuelve null si la línea es válida
    }

    /**
     * Valida la declaración de una variable en una línea de código, verificando
     * si cumple con las reglas gramaticales definidas en la tabla de sintaxis.
     * Devuelve una lista de errores encontrados, si los hay.
     *
     * @param linea la línea de código a validar
     * @return una lista de cadenas de texto que representan los errores
     * encontrados, o una lista vacía si no hay errores
     */
    public List<String> validarDeclaracionVariables(String linea) {
        List<String> errores = new ArrayList<>();
        boolean declaracionLineal = false;
        boolean declaracionMultiple = false;

        if (linea.matches(TablaDeSintaxis.ReglasGramaticales.DECLARACION_VARIABLE.getPatron())) {
            declaracionLineal = true;
        }
        if (linea.matches(TablaDeSintaxis.ReglasGramaticales.MULTIPLES_DECLARACIONES.getPatron())) {
            declaracionMultiple = true;
        }

        if (declaracionLineal || declaracionMultiple) {
            String[] partes = linea.split(":");
            String nombreVariable = partes[0].trim();
            String tipoDato = partes[1].trim();
            String[] nombresVariables = nombreVariable.split(",");

            if (Utilidades.contadorVar >= 1 && Utilidades.contadorBegin >= 1) {

                String variables = "";
                for (String nombre : nombresVariables) {
                    if (nombre.matches("\\s*[a-zA-Z]+[a-zA-Z0-9]*\\s*")) {
                        variables += nombre + " ";
                    }
                }

                errores.add(TablaDeErrores.Errores.ERROR_VA5.getDescripcion() + " (" + variables + ")");
            } else {
                for (String nombre : nombresVariables) {
                    if (nombre.matches("\\s*[a-zA-Z]+[a-zA-Z0-9]*\\s*")) {
                        Utilidades.variablesDeclaradas.add(nombre);
                        Utilidades.varTiposDeclaradas.add(nombre + " : " + tipoDato);
                    }
                }
            }

        }

        if (declaracionLineal == false && declaracionMultiple == false) {

            // Ciclo 1: Verificar que la línea tenga dos puntos (:)
            if (!linea.contains(":")) {
                errores.add(TablaDeErrores.Errores.ERROR_08.getDescripcion());
            }

            // Ciclo 2: Verificar que la línea tenga punto y coma (;)
            if (!linea.contains(";")) {
                errores.add(TablaDeErrores.Errores.ERROR_04.getDescripcion());
            }

            // Ciclo 3: Verificar que la línea no tenga comas (,) antes del tipo de variable
            String[] partes = linea.split(":");
            String nombreVariable = partes[0].trim();

            if (nombreVariable.contains(",")) {
                String[] palabras = nombreVariable.split(",");
                for (String palabra : palabras) {
                    if (!palabra.matches("\\s*[a-zA-Z]+[a-zA-Z0-9]*\\s*")) {
                        errores.add(TablaDeErrores.Errores.ERROR_05.getDescripcion() + " Identificador: " + nombreVariable);
                    }
                }
            } else {
                // Verificar que la única palabra cumpla con el formato requerido
                if (!nombreVariable.matches("\\s*[a-zA-Z]+[a-zA-Z0-9]*\\s*")) {
                    errores.add(TablaDeErrores.Errores.ERROR_05.getDescripcion() + " Identificador: " + nombreVariable);
                }
            }

        }

        if (linea.contains(";")) {
            String[] partes = linea.split(";", 2); // Se separa la línea en dos partes a partir del punto y coma (;)
            if (partes.length > 1) {
                String despuesPuntoYComa = partes[1].trim();
                if (!despuesPuntoYComa.isEmpty()) {
                    if (!despuesPuntoYComa.matches("^\\s*$")) { // Expresión regular que verifica si hay espacios en blanco pero no hay lexemas
                        errores.add(TablaDeErrores.Errores.ERROR_07.getDescripcion());
                    }
                }
            }

            int index = linea.indexOf(";");
            if (linea.indexOf(";", index + 1) != -1) {
                errores.add(TablaDeErrores.Errores.ERROR_06.getDescripcion());
            }

        }

        return errores;
    }

    /**
     *
     * Valida si se han declarado las secciones previas necesarias antes de la
     * sección de declaraciones de variable.
     *
     * @param lexema contiene el tipo de asignación.
     * @return un String vacío si no hay errores, o una descripción del
     * errorDeclaracion si lo hay.
     */
    public List<String> validarDeclaracionesPrevias(String lexema) {
        List<String> errores = new ArrayList<>();

        if (Utilidades.contadorModule == 0) {
            errores.add(TablaDeErrores.Errores.ERROR_03.getDescripcion() + " Declaración de tipo ( " + lexema + ")");
        }
        if (Utilidades.contadorVar == 0) {
            errores.add(TablaDeErrores.Errores.ERROR_12.getDescripcion());

        }

        return errores;
    }

    /**
     * Esta función valida el tipo de dato de una asignación en base a un string
     * de entrada.
     *
     * @param asignacion El string que representa la asignación a validar.
     * @return El tipo de dato de la asignación, que puede ser "INTEGER",
     * "REAL", "CHAR", o "Null". Si no se encuentra un tipo de dato válido, se
     * retorna "Null".
     */
    public String validarTipo(String asignacion) {
        String tipoDato = "";
        boolean variableEncontrada = false;
        String entero = ("-?\\d+(?!\\.)");
        String decimal = ("\\d*\\.\\d+");
        String caracter = ("^[a-zA-Z]$");
        String identificador = ("[A-Za-z0-9]+");

        if (asignacion.matches(entero)) {
            tipoDato = "INTEGER";
        }
        if (asignacion.matches(decimal)) {
            tipoDato = "REAL";
        }
        if (asignacion.matches(caracter)) {

            if (asignacion.length() == 1 && Character.isLetter(asignacion.charAt(0))) {
                tipoDato = "CHAR";
            }
        }
        if (asignacion.matches(identificador)) {
            if ("".equals(tipoDato)) {
                if (!Utilidades.varTiposDeclaradas.isEmpty()) {
                    for (String asignacionesDeclaradas : Utilidades.varTiposDeclaradas) {
                        if (asignacionesDeclaradas.contains(asignacion)) {
                            String[] partes = asignacionesDeclaradas.split("\\s*:\\s*|\\s*;\\s*");
                            tipoDato = partes[1];
                            variableEncontrada = true;
                        }
                    }
                    if (variableEncontrada == false) {
                        if ("".equals(tipoDato)) {
                            tipoDato = "Null";
                        }
                    }
                }
            }
        }

        return tipoDato;
    }

    /**
     *
     * Esta función realiza la asignación de variables del compilador.
     *
     * @param linea La línea de código que contiene la asignación de variables
     * en formato "variable := valor".
     *
     * @return Una lista de errores encontrados durante la asignación de
     * variables, si los hay. En caso contrario, la lista estará vacía.
     */
    public List<String> asignacionVariables(String linea) {
        List<String> errores = new ArrayList<>();
        ArrayList<String> identificadores = new ArrayList<>();
        String error = "";
        //REGISTRA SI ES LA VARIABLE ESTÁ DECLARADA Y EL TIPO SI LO ESTA
        boolean variableDeclarada = false;
        String variableTipo = "";

        // Patrones de regex para identificar enteros, decimales, caracteres y el identificador principal
        String entero = "-?\\d+(?!\\.)"; // El signo negativo (-) es opcional, y se excluye si se encuentra seguido de un punto decimal
        String decimal = ("\\d*\\.\\d+");
        String caracter = ("^[a-zA-Z]$");
        String identificador = ("[A-Za-z0-9]+");
        // Combinar los patrones en una sola expresión regular
        String patron = String.format("(%s|%s|%s|%s)\\b", entero, decimal, caracter, identificador);

        if (linea.matches(TablaDeSintaxis.ReglasGramaticales.INICIALIZACION_VARIABLES.getPatron())) {

            int posicionAsignacion = linea.indexOf(":=");
            String variable = linea.substring(0, posicionAsignacion).trim();

            //VALIDA QUE LA VARIABLE ESTE DECLARADA Y ESTE BIEN SU SINTAXIS
            if (!Utilidades.varTiposDeclaradas.isEmpty()) {
                for (String variablesDeclaradas : Utilidades.varTiposDeclaradas) {
                    if (variablesDeclaradas.contains(variable)) {
                        variableDeclarada = true;
                        String[] partes = variablesDeclaradas.split("\\s*:\\s*|\\s*;\\s*");
                        variableTipo = partes[1];
                    }
                }
            }

            if (variableDeclarada == false) {
                errores.add(TablaDeErrores.Errores.ERROR_A2.getDescripcion() + " Variable: (" + variable + ")");
            }

            if (variableDeclarada == true) {
                // Separar la línea después de ":="

                String lineaDespuesDeAsignacion = linea.split(":=")[1].trim();
                Pattern pattern = Pattern.compile(patron);
                Matcher matcher = pattern.matcher(lineaDespuesDeAsignacion);

                while (matcher.find()) {
                    String identificadorEncontrado = matcher.group(1);
                    identificadores.add(identificadorEncontrado);
                }

                for (String identificadoresDetectados : identificadores) {
                    String asignacion = "";

                    if (identificadoresDetectados.matches(entero)) {
                        asignacion = identificadoresDetectados;
                        identificadoresDetectados = identificadoresDetectados + " INTEGER";
                    } else if (identificadoresDetectados.matches(decimal)) {
                        asignacion = identificadoresDetectados;
                        identificadoresDetectados = identificadoresDetectados + " REAL";
                    } else if (identificadoresDetectados.matches(caracter)) {
                        asignacion = identificadoresDetectados;
                        identificadoresDetectados = identificadoresDetectados + " CHAR";
                    } else if (identificadoresDetectados.matches(identificador)) {
                        String asignacionTipo = "";

                        if (!identificadoresDetectados.matches(TablaDeAlfabeto.Tipos.Modula2.Patron)) {
                            if (!Utilidades.varTiposDeclaradas.isEmpty()) {
                                for (String variablesDeclaradas : Utilidades.varTiposDeclaradas) {
                                    if (variablesDeclaradas.contains(identificadoresDetectados)) {
                                        String[] partes = variablesDeclaradas.split("\\s*:\\s*|\\s*;\\s*");
                                        asignacionTipo = partes[1];
                                    }
                                }
                            }

                            if ("".equals(asignacionTipo)) {
                                asignacion = identificadoresDetectados;
                                identificadoresDetectados = identificadoresDetectados + " NO DECLARADO";
                            } else {

                                asignacion = identificadoresDetectados;
                                identificadoresDetectados = identificadoresDetectados + " " + asignacionTipo;
                            }

                        } else if (identificadoresDetectados.matches(TablaDeAlfabeto.Tipos.Modula2.Patron)) {
                            asignacion = identificadoresDetectados;
                            identificadoresDetectados = identificadoresDetectados + " MODULA";
                        }
                    }

                    if ((identificadoresDetectados.contains(variableTipo) || identificadoresDetectados.contains(" MODULA")) && !identificadoresDetectados.contains("NO DECLARADO")) {

                    } else {
                        if (identificadoresDetectados.contains("NO DECLARADO")) {
                            error += " El identificador (" + asignacion + ")" + " no ha sido declarado.";
                        }
                        if (!identificadoresDetectados.contains(variableTipo) && !identificadoresDetectados.contains("NO DECLARADO")) {
                            String tipo = "";

                            if (identificadoresDetectados.contains(" INTEGER")) {
                                tipo = "INTEGER";
                            }
                            if (identificadoresDetectados.contains(" REAL")) {
                                tipo = "REAL";
                            }
                            if (identificadoresDetectados.contains(" CHAR")) {
                                tipo = "CHAR";
                            }
                            error += " El identificador: (" + asignacion + ") de tipo (" + tipo + ") no es compatible con la variable.";
                        }
                    }
                }
                if (!"".equals(error)) {
                    errores.add(TablaDeErrores.Errores.ERROR_A1.getDescripcion() + error);
                }
            }
        } else {
            errores.add(TablaDeErrores.Errores.ERROR_A3.getDescripcion());
        }

        return errores;
    }

    /**
     *
     * Verifica si una línea de código tiene el inicio de un comentario y si la
     * pareja de cierre se encuentra en las diez líneas siguientes.
     *
     * @param linea la línea de código a verificar
     * @param lecturaLineas un array con todas las líneas de código
     * @return una cadena vacía si no se encontró ningún errorDeclaracion, de lo
     * contrario, devuelve la descripción del errorDeclaracion
     */
    public String validarInicioComentarios(String linea, String[] lecturaLineas) {
        String error = "";
        boolean parejaEncontrada = false;
        if (!linea.matches(TablaDeSintaxis.ReglasGramaticales.COMENTARIO_EN_LINEA.getPatron())) {

            // añade elementos a la lista
            String lineaBuscada = linea;
            int indiceInicio = -1;

            // buscar la línea y guardar su índice
            for (int i = 0; i < lecturaLineas.length; i++) {
                if (lecturaLineas[i].equals(lineaBuscada)) {
                    indiceInicio = i;
                    break;
                }
            }

            // recorrer las próximas 10 líneas desde el índice guardado
            if (indiceInicio != -1) {
                for (int i = indiceInicio + 1; i <= Math.min(indiceInicio + 10, lecturaLineas.length - 1); i++) {
                    if (lecturaLineas[i].contains("*)")) {
                        parejaEncontrada = true;
                    }
                }
            }

            if (parejaEncontrada == false) {
                error = TablaDeErrores.Errores.ERROR_C2.getDescripcion();
            }

        }

        return error;
    }

    /**
     *
     * Verifica si una línea de código tiene el fin de un comentario y si la
     * pareja de apertura se encuentra en las diez líneas anteriores.
     *
     * @param linea la línea de código a verificar
     * @param lecturaLineas un array con todas las líneas de código
     * @return una cadena vacía si no se encontró ningún errorDeclaracion, de lo
     * contrario, devuelve la descripción del errorDeclaracion
     */
    public String validarFinComentarios(String linea, String[] lecturaLineas) {
        String error = "";
        boolean parejaEncontrada = false;
        boolean parejaPrevia = false;
        if (!linea.matches(TablaDeSintaxis.ReglasGramaticales.COMENTARIO_EN_LINEA.getPatron())) {

            // añade elementos a la lista
            String lineaBuscada = linea;
            int indiceInicio = -1;

            // buscar la línea y guardar su índice
            for (int i = 0; i < lecturaLineas.length; i++) {
                if (lecturaLineas[i].equals(lineaBuscada)) {
                    indiceInicio = i;
                    break;
                }
            }

            // recorrer las diez líneas anteriores desde el índice guardado
            if (indiceInicio != -1) {
                for (int i = indiceInicio - 1; i >= Math.max(0, indiceInicio - 10); i--) {
                    if (lecturaLineas[i].contains("*)")) {
                        parejaPrevia = true;
                    }
                    if (parejaPrevia == false) {
                        if (lecturaLineas[i].contains("(*")) {
                            parejaEncontrada = true;
                            break;
                        }
                    }

                }
            }

            if (parejaEncontrada == false) {
                error = TablaDeErrores.Errores.ERROR_C1.getDescripcion();
            }

        }

        return error;
    }

    /**
     *
     * Esta función elimina los comentarios en línea de una línea de código. Los
     * comentarios en línea están delimitados por los caracteres (* y *) y
     * pueden contener cualquier carácter entre ellos.
     *
     * @param linea La línea de código en la que se eliminarán los comentarios
     * en línea.
     * @return La línea de código sin los comentarios en línea.
     */
    public String revisionComentarios(String linea) {
        String lineaSinComentarios = "";
        if (linea.contains("(")) {
            if (linea.contains("(*")) {
                if (linea.matches(TablaDeSintaxis.ReglasGramaticales.COMENTARIO_EN_LINEA.getPatron())) {
                    Pattern patron = Pattern.compile("\\(\\*.*?\\*\\)");
                    Matcher matcher = patron.matcher(linea);
                    lineaSinComentarios = matcher.replaceAll("");
                }
            }
        }
        return lineaSinComentarios;
    }

    //*************************************
    //VALIDACIONES PARA PALABRAS RESERVADAS
    //*************************************
    /**
     * Valida los comandos de repetición "REPEAT" y "UNTIL" en un código fuente
     * en lenguaje Modula-2.
     *
     * @param lexema El lexema a validar, que contiene la palabra reservada
     * "REPEAT" o "UNTIL".
     * @param linea La línea de código fuente que contiene el lexema a validar.
     * @return Una lista de errores encontrados durante la validación, si los
     * hubiera. Si no hay errores, la lista estará vacía.
     */
    public List<String> validarComandosRepeticion(String lexema, String linea) {
        List<String> errores = new ArrayList<>();
        String error = "";
        String primerIdentificador = "";
        String segundoIdentificador = "";
        String primerAsignacion = "";
        String segundaAsignacion = "";
        String lineaRepeat = "";
        String lineaUntil = "";
        boolean primerId = false;
        boolean segundoId = false;

        if (lexema.contains("REPEAT")) {
            if (linea.matches(TablaDeSintaxis.ReglasGramaticales.PALABRA_RESERVADA_REPEAT.getPatron())) {

                if (Utilidades.contadorRepeat > 1 && Utilidades.contadorUntil == 1) {
                    errores.add(TablaDeErrores.Errores.ERROR_RP2.getDescripcion());
                }

                if (Utilidades.contadorRepeat > 1 && Utilidades.contadorUntil == 0) {
                    errores.add(TablaDeErrores.Errores.ERROR_RP3.getDescripcion());
                }

                if (Utilidades.contadorRepeat == 0) {
                    Utilidades.contadorRepeat++;
                }

            } else if (!linea.matches(TablaDeSintaxis.ReglasGramaticales.PALABRA_RESERVADA_REPEAT.getPatron())) {
                errores.add(TablaDeErrores.Errores.ERROR_RP1.getDescripcion());
            }
        }

        if (lexema.contains("UNTIL")) {
            if (linea.matches(TablaDeSintaxis.ReglasGramaticales.PALABRA_RESERVADA_UNTIL.getPatron())) {
                String regex = "^\\s*UNTIL\\s+(\\w+)\\s*((?:#|==|>|<|>=|<=))\\s+((\\w+))\\s*;\\s*$";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(linea);

                if (matcher.matches()) {
                    primerIdentificador = matcher.group(1);
                    segundoIdentificador = matcher.group(3);
                }

                //VALIDA QUE LA VARIABLE ESTE DECLARADA Y ESTE BIEN SU SINTAXIS
                if (!Utilidades.varTiposDeclaradas.isEmpty()) {
                    for (String variablesDeclaradas : Utilidades.varTiposDeclaradas) {
                        if (variablesDeclaradas.contains(primerIdentificador)) {

                            String[] partes = variablesDeclaradas.split("\\s*:\\s*|\\s*;\\s*");
                            String variable = partes[0];

                            if (primerIdentificador.trim().equals(variable.trim())) {
                                primerId = true;
                            }
                        }

                        if (variablesDeclaradas.contains(segundoIdentificador)) {

                            String[] partes = variablesDeclaradas.split("\\s*:\\s*|\\s*;\\s*");
                            String variable = partes[0];

                            if (segundoIdentificador.trim().equals(variable.trim())) {
                                segundoId = true;
                            }
                        }
                    }
                }

                //VALIDA SI LA ASIGNACION DENTRO DE LA CONDICION ES UN NUMERO ENTERO/NUMERO DECIMAL/CARACTER
                primerAsignacion = validarTipo(primerIdentificador.trim());
                if (primerAsignacion.equals("INTEGER") || primerAsignacion.equals("REAL") || primerAsignacion.equals("CHAR")) {
                    primerId = true;
                }
                segundaAsignacion = validarTipo(segundoIdentificador.trim());
                if (segundaAsignacion.equals("INTEGER") || segundaAsignacion.equals("REAL") || segundaAsignacion.equals("CHAR")) {
                    segundoId = true;
                }

                //VALIDA SI EL IDENTIFICADOR ES UNA PALABRA RESERVADA
                if (primerIdentificador.matches(TablaDeAlfabeto.Tipos.Modula2.Patron)) {
                    primerId = true;
                }

                if (segundoIdentificador.matches(TablaDeAlfabeto.Tipos.Modula2.Patron)) {
                    segundoId = true;
                }

                if (primerId == true && segundoId == true) {

                    if (Utilidades.contadorUntil > 1) {
                        errores.add(TablaDeErrores.Errores.ERROR_UN4.getDescripcion());
                    } else if (Utilidades.contadorUntil > 1 && Utilidades.contadorRepeat == 0) {
                        errores.add(TablaDeErrores.Errores.ERROR_UN1.getDescripcion());
                    }

                    if (Utilidades.contadorUntil == 0) {
                        Utilidades.contadorUntil++;

                        if (Utilidades.contadorRepeat == 1 && Utilidades.contadorUntil == 1) {

                            String lineaRevisada = "";
                            String lineaActual = linea;
                            String regexRepeat = "^\\s*REPEAT\\s*";
                            boolean encontradaLineaBuscada = false;
                            boolean revisionDeComandos = false;

                            for (String lineasArreglo : Utilidades.revisionDeArchivo) {

                                lineaRevisada = revisionComentarios(lineasArreglo);
                                if (!"".equals(lineaRevisada)) {
                                    lineasArreglo = lineaRevisada;
                                }

                                if (Pattern.matches(regexRepeat, lineasArreglo)) {
                                    if (lineasArreglo.contains("REPEAT")) {
                                        encontradaLineaBuscada = true;
                                        lineaRepeat = lineasArreglo;
                                    }
                                } else if (encontradaLineaBuscada && Pattern.matches(lineaActual, lineasArreglo)) {
                                    lineaUntil = lineaActual;
                                }
                            }

                            if (!"".equals(lineaRepeat) && !"".equals(lineaUntil)) {
                                revisionDeComandos = validarLineasEntreComandos(lineaRepeat, lineaUntil);

                                if (revisionDeComandos == false) {
                                    errores.add(TablaDeErrores.Errores.ERROR_UN6.getDescripcion());
                                }
                            }

                        }
                    }
                }

                if (primerId == false) {
                    error += (" La variable: " + primerIdentificador + " no se encuentra declarada.");
                }
                if (segundoId == false) {
                    error += (" La variable: " + segundoIdentificador + " no se encuentra declarada.");
                }
                if (primerId == false || segundoId == false) {
                    errores.add(TablaDeErrores.Errores.ERROR_UN3.getDescripcion() + error);
                }

            } else {
                errores.add(TablaDeErrores.Errores.ERROR_UN5.getDescripcion());
            }
        }

        return errores;
    }

    /**
     *
     * Valida la sintaxis y semántica de un comando condicional IF. Verifica que
     * el comando IF esté correctamente declarado y que las variables
     * involucradas en la condición estén previamente declaradas y con la
     * sintaxis correcta. También valida si la asignación dentro de la condición
     * es un número entero, número decimal o caracter válido. Además, verifica
     * si el identificador es una palabra reservada. Verifica si la línea END o
     * ELSE se encuentran correctamente en el archivo después del IF. Valida las
     * líneas entre comandos para asegurarse de que no haya errores en ellas.
     *
     * @param lexema El lexema del comando IF.
     * @param linea La línea del archivo que contiene el comando IF.
     * @return errores Una lista de errores encontrados en la validación del
     * comando IF.
     */
    public List<String> validarComandoIf(String lexema, String linea) {
        List<String> errores = new ArrayList<>();
        String errorDeclaracion = "";
        String errorSintaxis = "";
        String primerIdentificador = "";
        String segundoIdentificador = "";
        String primerAsignacion = "";
        String segundaAsignacion = "";
        String lineaEnd = "";
        String lineaElse = "";

        boolean primerId = false;
        boolean segundoId = false;
        boolean lineaEntreComandos = false;
        boolean endEncontrado = false;
        boolean comandoErroneo = false;

        if (lexema.equals("IF") && linea.contains("THEN")) {
            if (linea.matches(TablaDeSintaxis.ReglasGramaticales.INSTRUCCION_CONDICIONAL.getPatron())) {

                String regex = "\\s*IF\\s*\\(\\s*(\\w+)\\s*((?:#|==|>|<|>=|<=))\\s*(\\w+)\\s*\\)\\s*THEN\\b";

                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(linea);

                if (matcher.matches()) {
                    primerIdentificador = matcher.group(1);
                    segundoIdentificador = matcher.group(3);
                }

                //VALIDA QUE LA VARIABLE ESTE DECLARADA Y ESTE BIEN SU SINTAXIS
                if (!Utilidades.varTiposDeclaradas.isEmpty()) {
                    for (String variablesDeclaradas : Utilidades.varTiposDeclaradas) {
                        if (variablesDeclaradas.contains(primerIdentificador)) {

                            String[] partes = variablesDeclaradas.split("\\s*:\\s*|\\s*;\\s*");
                            String variable = partes[0];

                            if (primerIdentificador.trim().equals(variable.trim())) {
                                primerId = true;
                            }
                        }

                        if (variablesDeclaradas.contains(segundoIdentificador)) {

                            String[] partes = variablesDeclaradas.split("\\s*:\\s*|\\s*;\\s*");
                            String variable = partes[0];

                            if (segundoIdentificador.trim().equals(variable.trim())) {
                                segundoId = true;
                            }
                        }
                    }
                }

                //VALIDA SI LA ASIGNACION DENTRO DE LA CONDICION ES UN NUMERO ENTERO/NUMERO DECIMAL/CARACTER
                primerAsignacion = validarTipo(primerIdentificador.trim());
                if (primerAsignacion.equals("INTEGER") || primerAsignacion.equals("REAL") || primerAsignacion.equals("CHAR")) {
                    primerId = true;
                }
                segundaAsignacion = validarTipo(segundoIdentificador.trim());
                if (segundaAsignacion.equals("INTEGER") || segundaAsignacion.equals("REAL") || segundaAsignacion.equals("CHAR")) {
                    segundoId = true;
                }

                //VALIDA SI EL IDENTIFICADOR ES UNA PALABRA RESERVADA
                if (primerIdentificador.matches(TablaDeAlfabeto.Tipos.Modula2.Patron)) {
                    primerId = true;
                }

                if (segundoIdentificador.matches(TablaDeAlfabeto.Tipos.Modula2.Patron)) {
                    segundoId = true;
                }

                if (primerId == true && segundoId == true) {
                    String lineaRevisada = "";
                    String lineaBuscada = "\\s*IF\\s*\\(\\s*(\\w+)\\s*((?:#|==|>|<|>=|<=))\\s*(\\w+)\\s*\\)\\s*THEN\\b";
                    boolean encontradaLineaBuscada = false;
                    String regexEnd = "\\bEND\\s*;";
                    String regexElse = "\\bELSE\\s*";

                    for (String lineasArreglo : Utilidades.revisionDeArchivo) {

                        lineaRevisada = revisionComentarios(lineasArreglo);
                        if (!"".equals(lineaRevisada)) {
                            lineasArreglo = lineaRevisada;
                        }

                        if (Pattern.matches(lineaBuscada, lineasArreglo)) {
                            if (lineasArreglo.equals(linea)) {
                                encontradaLineaBuscada = true;

                            }
                        } else if (encontradaLineaBuscada && Pattern.matches(regexElse, lineasArreglo)) {
                            lineaElse = lineasArreglo;
                        } else if (encontradaLineaBuscada && Pattern.matches(regexEnd, lineasArreglo)) {
                            lineaEnd = lineasArreglo;
                            endEncontrado = true;
                        }
                    }
                }

                if (endEncontrado == false) {
                    errores.add(TablaDeErrores.Errores.ERROR_IF3.getDescripcion());
                } else {
                    Utilidades.contadorIf++;
                }

                if (!"".equals(lineaEnd) && "".equals(lineaElse)) {
                    lineaEntreComandos = validarLineasEntreComandos(linea, lineaEnd);
                    if (lineaEntreComandos == false) {
                        errores.add(TablaDeErrores.Errores.ERROR_IF4.getDescripcion());
                    }
                }

                if (!"".equals(lineaElse)) {
                    lineaEntreComandos = validarLineasEntreComandos(linea, lineaElse);
                    if (lineaEntreComandos == false) {
                        errores.add(TablaDeErrores.Errores.ERROR_IF5.getDescripcion());
                    }

                    if (!"".equals(lineaEnd)) {
                        lineaEntreComandos = validarLineasEntreComandos(lineaElse, lineaEnd);
                        if (lineaEntreComandos == false) {
                            errores.add(TablaDeErrores.Errores.ERROR_IF6.getDescripcion());
                        }
                    }
                }

                //AGREGA ERRORES DE VARIABLES
                if (primerId == false) {
                    errorDeclaracion += (" La variable: " + primerIdentificador + " no se encuentra declarada.");
                }
                if (segundoId == false) {
                    errorDeclaracion += (" La variable: " + segundoIdentificador + " no se encuentra declarada.");
                }
                if (primerId == false || segundoId == false) {
                    errores.add(TablaDeErrores.Errores.ERROR_IF2.getDescripcion() + errorDeclaracion);
                }

            } else {

                //AGREGA ERRORES DE SINTAXIS DEL COMANDO IF-THEN
                if (linea.contains("IF") && !linea.contains("THEN")) {
                    errorSintaxis += " Falta {THEN}.";
                }
                if (linea.contains(";")) {
                    errorSintaxis += " La línea contiene {;}.";
                }
                errores.add(TablaDeErrores.Errores.ERROR_IF1.getDescripcion() + errorSintaxis);

            }

        }

        return errores;
    }

    /**
     * Valida si un comando específico se encuentra dentro de un rango definido
     * de líneas en un archivo, buscando por una línea de inicio y una línea de
     * fin.
     *
     * @param lineaInicio La línea de inicio del rango a buscar.
     * @param lineaFin La línea de fin del rango a buscar.
     * @param comando El comando a buscar dentro del rango de líneas.
     * @return true si el comando se encuentra dentro del rango de líneas, false
     * en caso contrario.
     */
    public boolean validarEntreComandos(String lineaInicio, String lineaFin, String comando) {
        boolean comandoEncontrado = false;
        boolean encontradaLineaInicio = false;
        String lineaRevisada = "";
        ArrayList<String> lineasEncontradas = new ArrayList<>();

        for (String lineasArreglo : Utilidades.revisionDeArchivo) {

            lineaRevisada = revisionComentarios(lineasArreglo);
            if (!"".equals(lineaRevisada)) {
                lineasArreglo = lineaRevisada;
            }

            if (!encontradaLineaInicio && lineasArreglo.contains(lineaInicio)) {
                encontradaLineaInicio = true;
            } else if (encontradaLineaInicio && !lineasArreglo.contains(lineaFin)) {
                lineasEncontradas.add(lineasArreglo);
            } else if (encontradaLineaInicio && lineasArreglo.contains(lineaFin)) {
                break;
            }
        }

        if (!lineasEncontradas.isEmpty()) {
            for (String lineaEncontrada : lineasEncontradas) {
                if (!"".equals(lineaEncontrada)) {
                    if (lineaEncontrada.contains(comando)) {
                        comandoEncontrado = true;
                    }

                }
            }
        }

        return comandoEncontrado;
    }

    /**
     * Valida si las líneas de un archivo, comprendidas entre una línea de
     * inicio y una línea de fin, cumplen con las reglas gramaticales definidas
     * en una tabla de sintaxis.
     *
     * @param lineaInicio La línea de inicio del rango de líneas a validar.
     * @param lineaFin La línea de fin del rango de líneas a validar.
     * @return true si todas las líneas del rango cumplen con las reglas
     * gramaticales, false en caso contrario.
     */
    public boolean validarLineasEntreComandos(String lineaInicio, String lineaFin) {
        boolean lineasValidas = false;
        boolean encontradaLineaInicio = false;
        String lineaRevisada = "";
        ArrayList<String> lineasEncontradas = new ArrayList<>();

        for (String lineasArreglo : Utilidades.revisionDeArchivo) {

            lineaRevisada = revisionComentarios(lineasArreglo);
            if (!"".equals(lineaRevisada)) {
                lineasArreglo = lineaRevisada;
            }

            if (!encontradaLineaInicio && lineasArreglo.contains(lineaInicio)) {
                encontradaLineaInicio = true;
            } else if (encontradaLineaInicio && !lineasArreglo.contains(lineaFin)) {
                lineasEncontradas.add(lineasArreglo);
            } else if (encontradaLineaInicio && lineasArreglo.contains(lineaFin)) {
                break;
            }
        }

        if (!lineasEncontradas.isEmpty()) {
            for (String lineaEncontrada : lineasEncontradas) {
                if (!"".equals(lineaEncontrada)) {

                    for (TablaDeSintaxis.ReglasGramaticales regla : TablaDeSintaxis.ReglasGramaticales.values()) {
                        String patron = regla.getPatron();
                        if (lineaEncontrada.matches(patron)) {
                            lineasValidas = true;
                        }
                    }
                }
            }
        }

        return lineasValidas;
    }

    /**
     *
     * Valida que no exista más de un módulo en el archivo.
     *
     * @param linea La línea a validar.
     * @return Un String con la descripción del errorDeclaracion correspondiente
     * si se detecta más de un módulo o una cadena vacía si no.
     */
    public String validarModuleRepetido(String linea) {
        String error = "";
        //Pensar si cambiar string por list para devolver más de un errorDeclaracion en caso de detectar repetido + mala declaracion
        if (linea.matches(TablaDeSintaxis.ReglasGramaticales.INICIO_PROGRAMA.getPatron())) {
            if (Utilidades.contadorModule != 0) {
                Utilidades.contadorModule++;
                error = TablaDeErrores.Errores.ERROR_M2.getDescripcion();
            }
        }

        return error;
    }

    /**
     *
     * Valida que la sintaxis de la declaración de un módulo sea correcta.
     *
     * @param linea La línea a validar.
     * @return Un String con la descripción del errorDeclaracion correspondiente
     * si la sintaxis no es correcta o una cadena vacía si lo es.
     */
    public List<String> validarModuleSintaxis(String linea) {
        List<String> errores = new ArrayList<>();

        if (linea.matches(TablaDeSintaxis.ReglasGramaticales.INICIO_PROGRAMA.getPatron())) {
            // MODULE DETECTADO
            Utilidades.contadorModule++;

            String regex = "^\\s*(?<!\\w)MODULE\\s+([A-Za-z][A-Za-z0-9]*)\\s*;\\s*$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(linea);

            if (matcher.find()) {
                String nombrePrograma = matcher.group(1);
                Utilidades.nombreModule = nombrePrograma;
            }
        }

        if (!linea.matches(TablaDeSintaxis.ReglasGramaticales.INICIO_PROGRAMA.getPatron())) {

            // Verificar que la palabra "MODULE" esté escrita correctamente y que haya un espacio después de ella
            if (linea.matches("(?i)^.*\\bMODULE\\s+.*$")) {
                String[] partes = linea.split("\\s*;\\s*");
                String nombreModulo = partes[0].split("\\bMODULE\\s+")[1].trim().replaceAll("\\s+", " ");

                // Verificar que el nombre del módulo esté escrito correctamente
                if (!nombreModulo.matches("^[A-Za-z][A-Za-z0-9]*$")) {
                    errores.add(TablaDeErrores.Errores.ERROR_N2.getDescripcion()
                            + " El identificador del nombre no es valido. " + (nombreModulo));
                }

                // Verificar que el nombre del módulo no contenga caracteres especiales
                if (nombreModulo.matches("^.*[\\W_+/&%\\-\\*].*$")) {
                    errores.add(TablaDeErrores.Errores.ERROR_05.getDescripcion() + " Identificador: " + nombreModulo);
                }

                // Verificar que la línea termine con ";" y que no haya ningún lexema después del ";"
                String[] partesLinea = linea.split(";");
                if (partesLinea.length < 1 || partesLinea.length > 2) {
                    errores.add(TablaDeErrores.Errores.ERROR_04.getDescripcion());
                } else if (partesLinea.length == 2 && partesLinea[1].trim().length() > 0) {
                    errores.add(TablaDeErrores.Errores.ERROR_07.getDescripcion());
                } else if (partesLinea.length == 1) {
                    errores.add(TablaDeErrores.Errores.ERROR_04.getDescripcion());
                }

            } else if (linea.matches("(?i)^.*\\bmo[dD][uU][lL][eE]\\b.*$")) {
                // Si la palabra "MODULE" está mal escrita
                errores.add(TablaDeErrores.Errores.ERROR_M3.getDescripcion());
            }
        }

        return errores;
    }

    /**
     * Valida que no exista más de una declaración de BEGIN en el archivo.
     *
     * @param linea La línea a validar.
     * @return Un String con la descripción del errorDeclaracion correspondiente
     * si se detecta más de una declaración de BEGIN o una cadena vacía si no.
     */
    public String validarBeginRepetido(String linea) {
        String error = "";

        if (linea.matches(TablaDeSintaxis.ReglasGramaticales.PALABRA_RESERVADA_BEGIN.getPatron())) {
            if (Utilidades.contadorBegin != 0) {
                Utilidades.contadorBegin++;
                error = TablaDeErrores.Errores.ERROR_B2.getDescripcion();
            }
        }

        return error;
    }

    /**
     * Valida que la sintaxis de la declaración de BEGIN sea correcta.
     *
     * @param linea La línea a validar.
     * @return Un String con la descripción del errorDeclaracion correspondiente
     * si la sintaxis no es correcta o una cadena vacía si lo es.
     */
    public String validarBeginSintaxis(String linea) {
        String error = "";

        if (linea.matches(TablaDeSintaxis.ReglasGramaticales.PALABRA_RESERVADA_BEGIN.getPatron())) {
            //MODULE DETECTADO
            Utilidades.contadorBegin++;
        }

        if (!linea.matches(TablaDeSintaxis.ReglasGramaticales.PALABRA_RESERVADA_BEGIN.getPatron())) {

            // Separar la oración en un array de palabras
            String[] palabras = linea.split("\\s+");

            // Encontrar el índice de "BEGIN"
            int indiceBegin = Arrays.asList(palabras).indexOf("BEGIN");

            // Verificar si hay más lexemas antes o después de "BEGIN"
            if (indiceBegin > 0 || indiceBegin == palabras.length - 1) {
                error = (TablaDeErrores.Errores.ERROR_B3.getDescripcion() + " La palabra BEGIN debe ser la única palabra en la línea.");
            } else {
                error = TablaDeErrores.Errores.ERROR_B3.getDescripcion();
            }

        }

        return error;
    }

    /**
     * Valida que no exista más de una declaración de END en el archivo.
     *
     * @param linea La línea a validar.
     * @return Un String con la descripción del errorDeclaracion correspondiente
     * si se detecta más de una declaración de END o una cadena vacía si no.
     */
    public String validarEndRepetido(String linea) {
        String error = "";
        //Pensar si cambiar string por list para devolver más de un errorDeclaracion en caso de detectar repetido + mala declaracion
        if (Utilidades.contadorEnd != 0) {
            error = TablaDeErrores.Errores.ERROR_E2.getDescripcion();
        }

        return error;
    }

    /**
     *
     * Valida si la sintaxis de la línea cumple con la regla gramatical de fin
     * de programa o fin de ciclo. Si la línea cumple con la regla gramatical de
     * fin de programa, se aumenta el contador de fin de programa. Si la línea
     * no cumple con la regla gramatical de fin de ciclo o fin de programa, se
     * retorna el mensaje de error correspondiente.
     *
     * @param linea la línea que se desea validar.
     * @return un mensaje de errorDeclaracion si la línea no cumple con la regla
     * gramatical de fin de ciclo o fin de programa, de lo contrario una cadena
     * vacía.
     */
    public List<String> validarEndSintaxis(String linea) {
        List<String> errores = new ArrayList<>();

        if (linea.matches(TablaDeSintaxis.ReglasGramaticales.FIN_PROGRAMA.getPatron())) {
            //END programa DETECTADO
            boolean declaracionCorrecta = false;
            String nombrePrograma = "";
            String regex = "^\\s*END\\s*([A-Za-z][A-Za-z0-9]*)?\\s*[.]\\s*$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(linea);

            if (matcher.find()) {
                nombrePrograma = matcher.group(1);
            }

            if (!"".equals(nombrePrograma) && nombrePrograma != null) {
                if (!"".equals(Utilidades.nombreModule)) {
                    if (!nombrePrograma.trim().equals(Utilidades.nombreModule.trim())) {
                        errores.add(TablaDeErrores.Errores.ERROR_N1.getDescripcion() + " El nombre: " + nombrePrograma + " no coincide con: " + Utilidades.nombreModule);
                    }
                } else if ("".equals(Utilidades.nombreModule)) {
                    errores.add(TablaDeErrores.Errores.ERROR_N1.getDescripcion() + " No se declaró nombre del programa en MODULE.");
                }
            }

            if ("".equals(nombrePrograma) || nombrePrograma == null) {
                errores.add(TablaDeErrores.Errores.ERROR_N1.getDescripcion() + " Falta el nombre del programa.");
            }

            if (nombrePrograma != null) {
                if (nombrePrograma.trim().equals(Utilidades.nombreModule.trim())) {
                    declaracionCorrecta = true;
                }
            }

            if (declaracionCorrecta == true) {
                Utilidades.contadorEnd++;
                nombrePrograma = Utilidades.nombreEnd;
            }

        }

        if (!linea.matches(TablaDeSintaxis.ReglasGramaticales.FIN_PROGRAMA.getPatron())) {

            String mensajeError = "";
            String regexEnd = "\\bEND\\s*;";
            boolean puntoFinal = false;

            String[] partes = linea.split("\\s*END\\s+");
            for (String parte : partes) {

                if (!"".equals(parte)) {

                    if (linea.matches(regexEnd)) {
                        if (Utilidades.contadorIf == 0) {
                            mensajeError += "La declaración END corresponde a un bucle IF. (IF) No detectado. ";
                        }
                    } else {

                        if (parte.contains(";")) {
                            mensajeError += "Punto y coma no es válido en la declaración de END. ";
                        }

                        if (parte.trim().endsWith(".")) {
                            puntoFinal = true;
                        }

                        if (!parte.contains("END")) {
                            if (!parte.trim().matches(TablaDeAlfabeto.Tipos.Identificador.Patron)) {
                                mensajeError += ("[ " + parte.trim() + " ]" + " No es un nombre válido. ");
                            }
                            if (!parte.trim().contains(Utilidades.nombreModule)) {
                                mensajeError += ("[ " + parte.trim() + " ]" + " No coincide con el nombre en (MODULE). ");
                            }
                            
                        } else {
                            mensajeError += "Falta el nombre del programa. ";
                        }

                        if (puntoFinal == false) {
                            mensajeError += "Falta punto al final. ";
                        }
                        
                        
                    }

                }

            }

            if (!"".equals(mensajeError)) {
                errores.add(TablaDeErrores.Errores.ERROR_E3.getDescripcion() + " " + mensajeError);
            }

        }

        return errores;
    }

    /**
     * Valida si la palabra reservada "VAR" ha sido repetida en el código.
     *
     * @param linea la línea actual de código que se está validando
     * @return un String con el mensaje de errorDeclaracion si la palabra "VAR"
     * ha sido repetida, en caso contrario retorna una cadena vacía
     */
    public String validarVarRepetido(String linea) {
        String error = "";
        //Pensar si cambiar string por list para devolver más de un errorDeclaracion en caso de detectar repetido + mala declaracion
        if (Utilidades.contadorVar != 0) {
            error = TablaDeErrores.Errores.ERROR_VA3.getDescripcion();
        }
        return error;
    }

    /**
     * Valida la sintaxis de la declaración de variable en una línea de código.
     *
     * @param linea la línea actual de código que se está validando
     *
     * @return una lista de Strings con los mensajes de errorDeclaracion si hay
     * problemas con la sintaxis de la declaración de variable, en caso
     * contrario retorna una lista vacía
     */
    public List<String> validarVarSintaxis(String linea) {
        List<String> errores = new ArrayList<>();
        String sobranteVar = "";

        if (linea.matches(TablaDeSintaxis.ReglasGramaticales.PALABRA_RESERVADA_VAR.getPatron())) {
            //VAR DETECTADO
            Utilidades.contadorVar++;
        }

        if (!linea.matches(TablaDeSintaxis.ReglasGramaticales.PALABRA_RESERVADA_VAR.getPatron())) {

            // Separar la oración en un array de palabras
            String[] palabras = linea.split("\\s+");
            for (String palabra : palabras) {
                if (!palabra.equals("VAR")) {
                    sobranteVar += palabra + " ";
                }
            }

            int indiceVar = palabras.length;

            if (indiceVar > 0) {
                if (!sobranteVar.contains("VAR")) {
                    errores.add(TablaDeErrores.Errores.ERROR_VA1.getDescripcion() + " La palabra VAR debe ser la única palabra en la línea. (" + sobranteVar.trim() + ")");
                }
            }
        }

        if (Utilidades.contadorBegin != 0) {
            errores.add(TablaDeErrores.Errores.ERROR_VA2.getDescripcion());
        }
        if (Utilidades.contadorModule == 0) {
            errores.add(TablaDeErrores.Errores.ERROR_VA4.getDescripcion());
        }
        return errores;
    }

    /**
     * Valida la sintaxis de una instrucción de lectura de datos (read, readInt
     * o readReal).
     *
     * @param lexema el lexema correspondiente a la instrucción de lectura
     * (read, readInt o readReal)
     *
     * @param linea la línea de código a validar
     *
     * @return una lista de errores (en caso de haberlos) encontrados al validar
     * la sintaxis de la línea.
     */
    public List<String> validarReadSintaxis(String lexema, String linea) {
        List<String> errores = new ArrayList<>();
        boolean declaracionVariable = false;
        String nombreVariable = "";
        String tipoVariable = "";

        if (lexema.equals("Read")) {
            if (linea.matches(TablaDeSintaxis.ReglasGramaticales.PALABRA_RESERVADA_READ.getPatron())) {

                Pattern p = Pattern.compile(TablaDeSintaxis.ReglasGramaticales.PALABRA_RESERVADA_READ.getPatron());
                Matcher m = p.matcher(linea);

                if (m.find()) {
                    nombreVariable = m.group(1);
                }

                if (!nombreVariable.equals("")) {

                    if (!nombreVariable.matches("\\s*[a-zA-Z]+[a-zA-Z0-9]*\\s*")) {
                        errores.add(TablaDeErrores.Errores.ERROR_05.getDescripcion() + " PALABRA: " + nombreVariable);
                    }

                    if (!Utilidades.varTiposDeclaradas.isEmpty()) {
                        for (String variablesDeclaradas : Utilidades.varTiposDeclaradas) {
                            if (variablesDeclaradas.contains(nombreVariable)) {
                                declaracionVariable = true;
                                String[] partes = variablesDeclaradas.split("\\s*:\\s*|\\s*;\\s*");
                                tipoVariable = partes[1];
                            }
                        }
                    }
                }

                if (!tipoVariable.equals("")) {
                    if (!tipoVariable.equals("CHAR")) {
                        errores.add(TablaDeErrores.Errores.ERROR_R2.getDescripcion() + " Es de tipo: " + tipoVariable);
                    }
                }

                if (declaracionVariable == false) {
                    errores.add(TablaDeErrores.Errores.ERROR_R7.getDescripcion());
                }

            } else {

                if (!linea.trim().matches(".*\\s*;\\s*$")) {
                    errores.add(TablaDeErrores.Errores.ERROR_04.getDescripcion());
                }

                if (!linea.matches(TablaDeSintaxis.ReglasGramaticales.PALABRA_RESERVADA_READ.getPatron())) {
                    errores.add(TablaDeErrores.Errores.ERROR_R1.getDescripcion());
                }
            }
        }

        if (lexema.equals("ReadInt")) {
            if (linea.matches(TablaDeSintaxis.ReglasGramaticales.PALABRA_RESERVADA_READINT.getPatron())) {

                Pattern p = Pattern.compile(TablaDeSintaxis.ReglasGramaticales.PALABRA_RESERVADA_READINT.getPatron());
                Matcher m = p.matcher(linea);

                if (m.find()) {
                    nombreVariable = m.group(1);
                }

                if (!nombreVariable.equals("")) {

                    if (!nombreVariable.matches("\\s*[a-zA-Z]+[a-zA-Z0-9]*\\s*")) {
                        errores.add(TablaDeErrores.Errores.ERROR_05.getDescripcion() + " PALABRA: " + nombreVariable);
                    }

                    if (!Utilidades.varTiposDeclaradas.isEmpty()) {
                        for (String variablesDeclaradas : Utilidades.varTiposDeclaradas) {
                            if (variablesDeclaradas.contains(nombreVariable)) {
                                declaracionVariable = true;
                                String[] partes = variablesDeclaradas.split("\\s*:\\s*|\\s*;\\s*");
                                tipoVariable = partes[1];
                            }
                        }
                    }
                }

                if (!tipoVariable.equals("")) {
                    if (!tipoVariable.equals("INTEGER")) {
                        errores.add(TablaDeErrores.Errores.ERROR_R4.getDescripcion());
                    }
                }

                if (declaracionVariable == false) {
                    errores.add(TablaDeErrores.Errores.ERROR_R7.getDescripcion());
                }

            } else {

                if (!linea.trim().matches(".*\\s*;\\s*$")) {
                    errores.add(TablaDeErrores.Errores.ERROR_04.getDescripcion());
                }

                if (!linea.matches(TablaDeSintaxis.ReglasGramaticales.PALABRA_RESERVADA_READINT.getPatron())) {
                    errores.add(TablaDeErrores.Errores.ERROR_R3.getDescripcion());
                }

            }
        }

        if (lexema.equals("ReadReal")) {
            if (linea.matches(TablaDeSintaxis.ReglasGramaticales.PALABRA_RESERVADA_READREAL.getPatron())) {

                Pattern p = Pattern.compile(TablaDeSintaxis.ReglasGramaticales.PALABRA_RESERVADA_READREAL.getPatron());
                Matcher m = p.matcher(linea);

                if (m.find()) {
                    nombreVariable = m.group(1);
                }

                if (!nombreVariable.equals("")) {

                    if (!nombreVariable.matches("\\s*[a-zA-Z]+[a-zA-Z0-9]*\\s*")) {
                        errores.add(TablaDeErrores.Errores.ERROR_05.getDescripcion() + " PALABRA: " + nombreVariable);
                    }

                    if (!Utilidades.varTiposDeclaradas.isEmpty()) {
                        for (String variablesDeclaradas : Utilidades.varTiposDeclaradas) {
                            if (variablesDeclaradas.contains(nombreVariable)) {
                                declaracionVariable = true;
                                String[] partes = variablesDeclaradas.split("\\s*:\\s*|\\s*;\\s*");
                                tipoVariable = partes[1];
                            }
                        }
                    }
                }

                if (!tipoVariable.equals("")) {
                    if (!tipoVariable.equals("REAL")) {
                        errores.add(TablaDeErrores.Errores.ERROR_R6.getDescripcion());
                    }
                }

                if (declaracionVariable == false) {
                    errores.add(TablaDeErrores.Errores.ERROR_R7.getDescripcion());
                }

            } else {

                if (!linea.trim().matches(".*\\s*;\\s*$")) {
                    errores.add(TablaDeErrores.Errores.ERROR_04.getDescripcion());
                }

                if (!linea.matches(TablaDeSintaxis.ReglasGramaticales.PALABRA_RESERVADA_READREAL.getPatron())) {
                    errores.add(TablaDeErrores.Errores.ERROR_R5.getDescripcion());
                }

            }
        }

        if (lexema.equals("Read") || lexema.equals("ReadInt") || lexema.equals("ReadReal")) {
            if (Utilidades.contadorEnd != 0) {
                errores.add(TablaDeErrores.Errores.ERROR_09.getDescripcion());
            }
            if (Utilidades.contadorBegin == 0) {
                errores.add(TablaDeErrores.Errores.ERROR_10.getDescripcion());
            }
            if (Utilidades.contadorVar == 0) {
                errores.add(TablaDeErrores.Errores.ERROR_11.getDescripcion());
            }

            int contadorParentesis = 0;
            for (int i = 0; i < linea.length(); i++) {
                char caracter = linea.charAt(i);
                if (caracter == '(') {
                    contadorParentesis++;
                } else if (caracter == ')') {
                    contadorParentesis--;
                    if (contadorParentesis < 0) {
                        errores.add(TablaDeErrores.Errores.ERROR_R8.getDescripcion() + "se encontró un paréntesis cerrado sin haber uno abierto previamente.");
                        break;
                    }
                }
            }

            if (contadorParentesis != 0) {
                errores.add(TablaDeErrores.Errores.ERROR_R8.getDescripcion() + "falta cerrar uno o más paréntesis");
            }

            if (linea.contains("(") && linea.contains(")")) {
                // Buscar texto entre paréntesis
                String parentesis_contenido = linea.substring(linea.indexOf("(") + 1, linea.indexOf(")"));
                // Eliminar espacios en blanco alrededor del contenido del paréntesis
                parentesis_contenido = parentesis_contenido.trim();

                // Validar si el contenido del paréntesis está vacío
                if (parentesis_contenido.equals("")) {
                    errores.add(TablaDeErrores.Errores.ERROR_R8.getDescripcion() + "el contenido entre paréntesis está vacío.");
                }
            }

        }

        return errores;
    }

    /**
     * Realiza la validación de la sintaxis del comando de escritura de
     * variable.
     *
     * @param lexema String que indica el tipo de comando de escritura (Write,
     * WriteInt, WriteReal)
     *
     * @param linea String con la línea a validar
     *
     * @return Lista de Strings con los errores encontrados durante la
     * validación, si los hay.
     */
    public List<String> validarWriteSintaxis(String lexema, String linea) {
        List<String> errores = new ArrayList<>();
        boolean declaracionVariable = false;
        String nombreVariable = "";
        String tipoVariable = "";
        int tamanoVariable = 0;

        if ("Write".equals(lexema)) {

            if (linea.matches(TablaDeSintaxis.ReglasGramaticales.PALABRA_RESERVADA_WRITE.getPatron())) {

                Pattern p = Pattern.compile(TablaDeSintaxis.ReglasGramaticales.PALABRA_RESERVADA_WRITE.getPatron());
                Matcher m = p.matcher(linea);

                if (m.find()) {
                    nombreVariable = m.group(1);
                }

                if (!nombreVariable.equals("")) {

                    if (!nombreVariable.matches("\\s*[a-zA-Z]+[a-zA-Z0-9]*\\s*")) {
                        errores.add(TablaDeErrores.Errores.ERROR_05.getDescripcion() + " PALABRA: " + nombreVariable);
                    }

                    if (!Utilidades.varTiposDeclaradas.isEmpty()) {
                        for (String variablesDeclaradas : Utilidades.varTiposDeclaradas) {
                            if (variablesDeclaradas.contains(nombreVariable)) {
                                declaracionVariable = true;
                                String[] partes = variablesDeclaradas.split("\\s*:\\s*|\\s*;\\s*");
                                tipoVariable = partes[1];
                            }
                        }
                    }
                }

                if (!tipoVariable.equals("")) {
                    if (!tipoVariable.equals("CHAR")) {
                        errores.add(TablaDeErrores.Errores.ERROR_W3.getDescripcion() + " La variable usada es de tipo: " + tipoVariable);
                    }
                }

                if (declaracionVariable == false) {
                    errores.add(TablaDeErrores.Errores.ERROR_W0.getDescripcion());
                }

            } else {

                String mensajeError = "";
                if (!linea.trim().matches(".*\\s*;\\s*$")) {
                    mensajeError += " Falta punto y coma (;)."; 
                }
                if(!linea.contains("(")){
                    mensajeError += " Falta el parentesis `(`."; 
                }
                 if(!linea.contains("(")){
                    mensajeError += " Falta el parentesis `)`."; 
                }

                if (!linea.matches(TablaDeSintaxis.ReglasGramaticales.PALABRA_RESERVADA_WRITE.getPatron())) {
                    errores.add(TablaDeErrores.Errores.ERROR_W2.getDescripcion() + mensajeError);
                }
            }
        }

        if ("WriteInt".equals(lexema)) {
            if (linea.matches(TablaDeSintaxis.ReglasGramaticales.PALABRA_RESERVADA_WRITEINT.getPatron())) {

                Pattern p = Pattern.compile(TablaDeSintaxis.ReglasGramaticales.PALABRA_RESERVADA_WRITEINT.getPatron());
                Matcher m = p.matcher(linea);

                if (m.find()) {
                    nombreVariable = m.group(1);
                    String tamanoVariableStr = m.group(2);

                    if (tamanoVariableStr.matches("\\d+")) {
                        tamanoVariable = Integer.parseInt(tamanoVariableStr);
                    }

                }

                if (tamanoVariable > 20) {
                    errores.add(TablaDeErrores.Errores.ERROR_W9.getDescripcion() + " Tamaño: " + tamanoVariable);
                }
                if (tamanoVariable < 0) {
                    errores.add(TablaDeErrores.Errores.ERROR_W10.getDescripcion() + " Tamaño: " + tamanoVariable);
                }

                if (!nombreVariable.equals("")) {

                    if (!nombreVariable.matches("\\s*[a-zA-Z]+[a-zA-Z0-9]*\\s*")) {
                        errores.add(TablaDeErrores.Errores.ERROR_05.getDescripcion() + " PALABRA: " + nombreVariable);
                    }

                    if (!Utilidades.varTiposDeclaradas.isEmpty()) {
                        for (String variablesDeclaradas : Utilidades.varTiposDeclaradas) {
                            if (variablesDeclaradas.contains(nombreVariable)) {
                                declaracionVariable = true;
                                String[] partes = variablesDeclaradas.split("\\s*:\\s*|\\s*;\\s*");
                                tipoVariable = partes[1];
                            }
                        }
                    }
                }

                if (!tipoVariable.equals("")) {
                    if (!tipoVariable.equals("INTEGER")) {
                        errores.add(TablaDeErrores.Errores.ERROR_W5.getDescripcion() + " La variable usada es de tipo: " + tipoVariable);
                    }
                }

                if (declaracionVariable == false) {
                    errores.add(TablaDeErrores.Errores.ERROR_W0.getDescripcion());
                }

            } else {
                String mensajeError = "";
                if (!linea.trim().matches(".*\\s*;\\s*$")) {
                    mensajeError += " Falta punto y coma (;)."; 
                }
                if(!linea.contains("(")){
                    mensajeError += " Falta el parentesis `(`."; 
                }
                 if(!linea.contains("(")){
                    mensajeError += " Falta el parentesis `)`."; 
                }

                if (!linea.matches(TablaDeSintaxis.ReglasGramaticales.PALABRA_RESERVADA_WRITEINT.getPatron())) {
                    errores.add(TablaDeErrores.Errores.ERROR_W4.getDescripcion() + mensajeError);
                }
            }
        }

        if ("WriteReal".equals(lexema)) {

            if (linea.matches(TablaDeSintaxis.ReglasGramaticales.PALABRA_RESERVADA_WRITEREAL.getPatron())) {

                Pattern p = Pattern.compile(TablaDeSintaxis.ReglasGramaticales.PALABRA_RESERVADA_WRITEREAL.getPatron());
                Matcher m = p.matcher(linea);

                if (m.find()) {
                    nombreVariable = m.group(1);
                    String tamanoVariableStr = m.group(2);
                    if (tamanoVariableStr.matches("\\d+")) {
                        tamanoVariable = Integer.parseInt(tamanoVariableStr);
                    }

                }

                if (tamanoVariable > 20) {
                    errores.add(TablaDeErrores.Errores.ERROR_W9.getDescripcion() + " Tamaño: " + tamanoVariable);
                }
                if (tamanoVariable < 0) {
                    errores.add(TablaDeErrores.Errores.ERROR_W10.getDescripcion() + " Tamaño: " + tamanoVariable);
                }

                if (!nombreVariable.equals("")) {

                    if (!nombreVariable.matches("\\s*[a-zA-Z]+[a-zA-Z0-9]*\\s*")) {
                        errores.add(TablaDeErrores.Errores.ERROR_05.getDescripcion() + " PALABRA: " + nombreVariable);
                    }

                    if (!Utilidades.varTiposDeclaradas.isEmpty()) {
                        for (String variablesDeclaradas : Utilidades.varTiposDeclaradas) {
                            if (variablesDeclaradas.contains(nombreVariable)) {
                                declaracionVariable = true;
                                String[] partes = variablesDeclaradas.split("\\s*:\\s*|\\s*;\\s*");
                                tipoVariable = partes[1];
                            }
                        }
                    }
                }

                if (!tipoVariable.equals("")) {
                    if (!tipoVariable.equals("REAL")) {
                        errores.add(TablaDeErrores.Errores.ERROR_W7.getDescripcion() + " La variable usada es de tipo: " + tipoVariable);
                    }
                }

                if (declaracionVariable == false) {
                    errores.add(TablaDeErrores.Errores.ERROR_W0.getDescripcion());
                }

            } else {
                
                String mensajeError = "";
                if (!linea.trim().matches(".*\\s*;\\s*$")) {
                    mensajeError += " Falta punto y coma (;)."; 
                }
                if(!linea.contains("(")){
                    mensajeError += " Falta el parentesis `(`."; 
                }
                 if(!linea.contains("(")){
                    mensajeError += " Falta el parentesis `)`."; 
                }


                if (!linea.matches(TablaDeSintaxis.ReglasGramaticales.PALABRA_RESERVADA_WRITEREAL.getPatron())) {
                    errores.add(TablaDeErrores.Errores.ERROR_W6.getDescripcion() + mensajeError);
                }

            }

        }

        if ("WriteString".equals(lexema)) {

            String mensajeError = TablaDeErrores.Errores.ERROR_W8.getDescripcion();

            if (linea.matches(TablaDeSintaxis.ReglasGramaticales.PALABRA_RESERVADA_WRITESTRING.getPatron())) {
                Pattern p = Pattern.compile(TablaDeSintaxis.ReglasGramaticales.PALABRA_RESERVADA_WRITESTRING.getPatron());
                Matcher m = p.matcher(linea);

                if (m.find()) {
                    String texto = m.group(1);
                    if (texto.length() > 60) {
                        errores.add(TablaDeErrores.Errores.ERROR_W11.getDescripcion());
                    }
                }
            }

            if (!linea.matches(TablaDeSintaxis.ReglasGramaticales.PALABRA_RESERVADA_WRITESTRING.getPatron())) {
                int pos = linea.indexOf("WriteString");

                if (pos == -1) {
                    // No se encontró la palabra WriteString
                } else {
                    pos = linea.indexOf('(', pos);

                    if (pos == -1) {
                        errores.add(mensajeError + " Falta el paréntesis '(' después de WriteString.");
                    } else {
                        pos = linea.indexOf('\'', pos);

                        if (pos == -1) {
                            errores.add(mensajeError + " Falta la comilla simple después del paréntesis '('.");
                        } else {
                            pos = linea.indexOf('\'', pos + 1);

                            if (pos == -1) {
                                errores.add(mensajeError + " Falta la segunda comilla simple.");
                            } else {
                                pos = linea.indexOf(')', pos + 1);

                                if (pos == -1) {
                                    errores.add(mensajeError + " Falta el paréntesis ')' después de la segunda comilla simple.");
                                } else {
                                    pos = linea.indexOf(';', pos + 1);

                                    if (pos == -1) {
                                        errores.add(mensajeError + " falta el punto y coma ';'.");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if ("WriteLn".equals(lexema)) {

            if (!linea.matches(TablaDeSintaxis.ReglasGramaticales.PALABRA_RESERVADA_WRITELN.getPatron())) {

                if (!linea.contains(";")) {
                    errores.add(TablaDeErrores.Errores.ERROR_W1.getDescripcion());
                }

                if (linea.contains(";")) {
                    int index = linea.indexOf("WriteLn");
                    if (index != -1 && index + "WriteLn".length() < linea.length() && linea.charAt(index + "WriteLn".length()) == ';') {

                    } else {

                        errores.add(TablaDeErrores.Errores.ERROR_W1.getDescripcion());
                    }
                }
            }
        }

        return errores;
    }

    /**
     * Método que valida la sintaxis del retorno en una línea de código.
     *
     * @param lexema el lexema a validar
     * @param linea la línea de código a analizar
     * @return una lista de errores encontrados en la validación de la sintaxis
     */
    public List<String> validarReturnSintaxis(String lexema, String linea) {
        List<String> errores = new ArrayList<>();
        String error = "";
        if ("RETURN".equals(lexema)) {

            if (!linea.matches(TablaDeSintaxis.ReglasGramaticales.PALABRA_RESERVADA_RETURN.getPatron())) {

                int index = linea.indexOf("RETURN");
                if (index != -1 && index + "RETURN".length() < linea.length() && linea.charAt(index + "RETURN".length()) == ';') {

                } else {
                    error += " Falta (;) en la declaración.";
                }

                if (linea.contains(";")) {
                    Pattern p = Pattern.compile("^\\s*RETURN\\s*;\\s*\\S.*$");
                    Matcher m = p.matcher(linea);

                    if (m.matches()) {
                        error += " Existe más de un punto y coma (;) en la línea.";

                        if (linea.indexOf(";") != linea.lastIndexOf(";")) {
                            error += " Existe un lexema después de punto y coma (;)";
                        }
                    }
                }

                errores.add(TablaDeErrores.Errores.ERROR_RT1.getDescripcion() + error + " (" + lexema + ")");
            }

        }
        return errores;
    }

    //*************************************
    //VALIDACIONES PARA EL ARCHIVO COMPLETO
    //*************************************
    /**
     * Valida si el archivo contiene la palabra clave "MODULE". Si el archivo
     * contiene la palabra clave "MODULE", se marca la banderaModule como
     * verdadera. Si el archivo no contiene la palabra clave "MODULE", se
     * retorna el mensaje de error correspondiente.
     *
     * @param listaLineas un arreglo que contiene todas las líneas del código.
     * @return un mensaje de errorDeclaracion si el archivo no contiene la
     * palabra clave "MODULE", de lo contrario una cadena vacía.
     */
    public String validarArchivoModule(String[] listaLineas) {
        String error = "";
        boolean banderaModule = false;

        for (String linea : listaLineas) {
            if (linea.contains("MODULE")) {
                banderaModule = true;
            }
        }

        if (banderaModule == false) {
            if (Utilidades.contadorModule != 1) {
                error = TablaDeErrores.Errores.ERROR_M1.getDescripcion();
            }
        }
        return error;
    }

    /**
     * Valida si el archivo contiene una línea con la palabra reservada "END" y
     * si cumple con la regla gramatical para el fin del programa. Si no cumple
     * con estas condiciones, se retorna un mensaje de error.
     *
     * @param listaLineas arreglo de strings con las líneas del archivo a
     * validar
     * @return un string vacío si no hay errores o un mensaje de
     * errorDeclaracion si no se encuentra la palabra reservada "END" o no
     * cumple con la regla gramatical para el fin del programa.
     */
    public String validarArchivoEnd(String[] listaLineas) {
        String error = "";
        boolean banderaEnd = false;

        if (Utilidades.contadorEnd == 1) {
            banderaEnd = true;
        }

        if (banderaEnd == false) {
            error = TablaDeErrores.Errores.ERROR_E1.getDescripcion();
        }
        return error;
    }

    /**
     * Valida si el archivo contiene una línea con la palabra reservada "BEGIN"
     * y si cumple con la regla gramatical para esta palabra reservada. Si no
     * cumple con estas condiciones, se retorna un mensaje de error.
     *
     * @param listaLineas arreglo de strings con las líneas del archivo a
     * validar
     * @return un string vacío si no hay errores o un mensaje de
     * errorDeclaracion si no se encuentra la palabra reservada "BEGIN" o no
     * cumple con la regla gramatical para esta palabra reservada.
     */
    public String validarArchivoBegin(String[] listaLineas) {
        String error = "";

        boolean banderaBegin = false;

        for (String linea : listaLineas) {
            if (linea.contains("BEGIN")) {
                if (linea.matches(TablaDeSintaxis.ReglasGramaticales.PALABRA_RESERVADA_BEGIN.getPatron())) {

                    if (Utilidades.contadorBegin == 1) {
                        banderaBegin = true;
                    }
                }
            }
        }

        if (banderaBegin == false) {
            error = TablaDeErrores.Errores.ERROR_B1.getDescripcion();
        }
        return error;
    }

    /**
     *
     * Valida el nombre del programa. Debe tener un módulo y un final correctos
     * con el mismo nombre.
     *
     * @param listaLineas un array de strings con las líneas del programa.
     *
     * @return una cadena vacía si el nombre es válido, o una descripción del
     * errorDeclaracion si no lo es.
     */
    public String validarNombrePrograma(String[] listaLineas) {
        String error = "";
        String validarModule = "";
        String validarEnd = "";

        if (Utilidades.contadorModule != 1 && Utilidades.contadorEnd != 1) {

            if (Utilidades.nombreModule == null || "".equals(Utilidades.nombreModule)) {
                validarModule = (" Falta el nombre del programa en la declaración de MODULE. ");
                if (Utilidades.contadorModule == 0) {
                    validarModule = (" Falta la declaración de MODULE. ");
                }
            }
            if (Utilidades.nombreEnd == null || "".equals(Utilidades.nombreEnd)) {
                validarEnd = (" Falta el nombre del programa en la declaración de END. ");
                if (Utilidades.contadorModule == 0) {
                    validarEnd = (" Falta la declaración de END. ");
                }
            }

            error = (TablaDeErrores.Errores.ERROR_N1.getDescripcion() + validarModule + validarEnd);

        }

        /* // String errorDeclaracion = "";
        String lineaModule = "";
        String lineaEnd = "";
        boolean nombrePrograma = false;
        boolean banderaModuleEnd = false;

        if (Utilidades.contadorModule > 0 && Utilidades.contadorEnd > 0) {
            banderaModuleEnd = true;
        }

        if (banderaModuleEnd == true) {
            for (String linea : listaLineas) {
                if (linea.contains("MODULE") && linea.matches(TablaDeSintaxis.ReglasGramaticales.INICIO_PROGRAMA.getPatron())) {
                    lineaModule = linea;
                }
                if (linea.contains("END") && linea.matches(TablaDeSintaxis.ReglasGramaticales.FIN_PROGRAMA.getPatron())) {
                    lineaEnd = linea;
                }
            }

            int moduleIndex = lineaModule.indexOf(";");
            String moduleNombre = lineaModule.substring(7, moduleIndex);
            int endIndex = lineaEnd.indexOf(".");
            String endNombre = lineaEnd.substring(4, endIndex);
            nombrePrograma = moduleNombre.equals(endNombre);

        }

        if (nombrePrograma == false) {
            errorDeclaracion = TablaDeErrores.Errores.ERROR_N1.getDescripcion();
        }

        if (banderaModuleEnd == false) {
            errorDeclaracion = TablaDeErrores.Errores.ERROR_N3.getDescripcion();
        }
         */
        return error;
    }

    /**
     *
     * Valida si el nombre del archivo es válido.
     *
     * @param nombreArchivo el nombre del archivo a validar.
     * @return el nombre del archivo si es válido, o null si no lo es.
     */
    public String validarNombreArchivo(String nombreArchivo) {
        if (nombreArchivo.length() > 20) {
            return null;
        }
        if (!Character.isLetter(nombreArchivo.charAt(0))) {
            return null;
        }
        String regex = "^[a-zA-Z][a-zA-Z0-9]{0,18}\\.[lL][iI][dD]$";
        if (!Pattern.matches(regex, nombreArchivo)) {
            return null;
        }
        if (!nombreArchivo.toLowerCase().endsWith(".lid")) {
            nombreArchivo += ".lid";
        }
        return nombreArchivo;
    }

}
