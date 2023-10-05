package analizador;

import principal.Utilidades;
import diccionario.TablaDeAlfabeto;
import diccionario.TablaDeErrores;
import diccionario.TablaDeSintaxis;
import lexico.Token;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sintactico.Validaciones;

public class Analizador {

    public Analizador() {

    }

    /**
     * Método que realiza el análisis sintáctico de una línea de código.
     *
     * @param linea la línea de código a analizar.
     *
     * @return una lista de strings que contienen los errores sintácticos
     * encontrados.
     */
    public List<String> analizadorSintactico(String linea) {
        Validaciones validarSintaxis = new Validaciones();
        List<String> errorSintaxis = new ArrayList<>();
        String[] lexemas = linea.split("(?<=\\W)|(?=\\W)|\\n\\s*|\\s+");
        boolean saltarLinea = false;
        String lineaRevisada = "";
        String longitud = validarSintaxis.validarLongitud(linea);

        if (longitud != null) {
            errorSintaxis.add(longitud);
        }

        //EN CASO DE DETECTAR FROM, IGNORARÁ EL RESTO DE LA LÍNEA
        for (String lexema : lexemas) {
            if ("FROM".equals(lexema) && lexema.matches(TablaDeAlfabeto.Tipos.Modula2.Patron)) {
                saltarLinea = true;
            }
        }

        //REVISA Y MANEJA COMENTARIOS EN LA LINEA
        lineaRevisada = validarSintaxis.revisionComentarios(linea);
        if (!"".equals(lineaRevisada)) {
            linea = lineaRevisada;
        }

        if (saltarLinea == false) {
            for (String lexema : lexemas) {
                if (lexema.isEmpty()) {
                    continue;
                }

                errorSintaxis.addAll(validarDeclaracionVariables(lexema, linea, validarSintaxis));
                errorSintaxis.addAll(validarPalabraReservada(lexema, linea, validarSintaxis));
                errorSintaxis.addAll(validarComentarios(lexema, linea, validarSintaxis));
                errorSintaxis.addAll(validarRead(lexema, linea, validarSintaxis));
                errorSintaxis.addAll(validarWrite(lexema, linea, validarSintaxis));
                errorSintaxis.addAll(validarReturn(lexema, linea, validarSintaxis));
                errorSintaxis.addAll(validarComandosRepeticion(lexema, linea, validarSintaxis));
                errorSintaxis.addAll(validarComandosIf(lexema, linea, validarSintaxis));

            }

            errorSintaxis.addAll(validarAsignacion(linea, validarSintaxis));
            errorSintaxis.addAll(validarComandosEnd(linea));
        }

        return errorSintaxis;
    }

    //VALIDACIONES PRINCIPALES
    private List<String> validarDeclaracionVariables(String lexema, String linea, Validaciones validarSintaxis) {
        List<String> errores = new ArrayList<>();

        if (lexema.matches(TablaDeAlfabeto.Tipos.Variable.Patron)) {
            if (lexema.equals("BOOLEAN") || lexema.equals("CHAR") || lexema.equals("INTEGER") || lexema.equals("REAL")) {
                List<String> errorDeclaracion = validarSintaxis.validarDeclaracionVariables(linea);
                List<String> errorDeclaracionPrevia = validarSintaxis.validarDeclaracionesPrevias(lexema);

                if (!errorDeclaracion.isEmpty()) {
                    for (String string : errorDeclaracion) {
                        errores.add(string);
                    }
                }

                if (!errorDeclaracionPrevia.isEmpty()) {
                    for (String string : errorDeclaracionPrevia) {
                        errores.add(string);
                    }
                }

            }
        }

        return errores;
    }

    private List<String> validarPalabraReservada(String lexema, String linea, Validaciones validarSintaxis) {
        List<String> errores = new ArrayList<>();
        String errorComando = "";

        if (Utilidades.contadorBegin == 0) {
            errorComando = validarSintaxis.validarComandos(linea, lexema);
            if (!"".equals(errorComando)) {
                errores.add(errorComando);
            }
        }

        if (lexema.matches(TablaDeAlfabeto.Tipos.Libelula.Patron)) {
            switch (lexema) {
                case "MODULE":
                    errores.addAll(validarModule(linea, validarSintaxis));
                    break;
                case "END":
                    errores.addAll(validarEnd(linea, validarSintaxis));
                    break;
                case "BEGIN":
                    errores.addAll(validarBegin(linea, validarSintaxis));
                    break;
                case "VAR":
                    errores.addAll(validarVar(linea, validarSintaxis));
                    break;
                default:
                    break;
            }
        }

        return errores;
    }

    private List<String> validarAsignacion(String linea, Validaciones validarSintaxis) {
        List<String> errores = new ArrayList<>();
        List<String> erroresAsignacion = new ArrayList<>();

        if (linea.contains(":=")) {
            erroresAsignacion = (validarSintaxis.asignacionVariables(linea));

            if (Utilidades.contadorBegin == 0) {
                errores.add(TablaDeErrores.Errores.ERROR_13.getDescripcion());
            }

            if (!linea.matches(TablaDeSintaxis.ReglasGramaticales.INICIALIZACION_VARIABLES.getPatron())) {

                errores.add(TablaDeErrores.Errores.ERROR_A1.getDescripcion());

                if (linea.contains(";")) {
                    int index = linea.indexOf(";");
                    if (linea.indexOf(";", index + 1) != -1) {
                        errores.add(TablaDeErrores.Errores.ERROR_06.getDescripcion());
                    }
                }
            }

            if (!linea.trim()
                    .matches(".*\\s*;\\s*$")) {
                errores.add(TablaDeErrores.Errores.ERROR_04.getDescripcion());
            }

        }

        if (!erroresAsignacion.isEmpty()) {
            for (String string : erroresAsignacion) {
                errores.add(string);
            }
        }

        return errores;
    }

    private List<String> validarComentarios(String lexema, String linea, Validaciones validarSintaxis) {
        List<String> errorSintaxis = new ArrayList<>();

        if (lexema.equals("(")) {
            if (linea.contains("(*")) {
                String errorDetectado = validarSintaxis.validarInicioComentarios(linea, Utilidades.revisionDeArchivo);
                if (!"".equals(errorDetectado)) {
                    Token token = new Token(TablaDeAlfabeto.Tipos.Operadores, lexema);
                    errorSintaxis.add(errorDetectado + " Token: " + token.getValor() + " Tipo: " + token.getTipo());
                }
            }
        }

        if (lexema.equals(")")) {
            if (linea.contains("*)")) {
                String errorDetectado = validarSintaxis.validarFinComentarios(linea, Utilidades.revisionDeArchivo);
                if (!"".equals(errorDetectado)) {
                    Token token = new Token(TablaDeAlfabeto.Tipos.Operadores, lexema);
                    errorSintaxis.add(errorDetectado + " Token: " + token.getValor() + " Tipo: " + token.getTipo());
                }
            }
        }

        return errorSintaxis;
    }

    private List<String> validarRead(String lexema, String linea, Validaciones validarSintaxis) {
        List<String> errores = new ArrayList<>();

        if (lexema.matches(TablaDeAlfabeto.Tipos.Libelula.Patron)) {
            List<String> erroresSintaxis = validarSintaxis.validarReadSintaxis(lexema, linea);
            if (!erroresSintaxis.isEmpty()) {
                for (String string : erroresSintaxis) {
                    errores.add(string);
                }
            }
        }

        return errores;
    }

    private List<String> validarWrite(String lexema, String linea, Validaciones validarSintaxis) {
        List<String> errores = new ArrayList<>();

        if ("WriteLn".equals(lexema) || "WriteInt".equals(lexema) || "WriteReal".equals(lexema) || "WriteString".equals(lexema) || "Write".equals(lexema)) {
            List<String> erroresSintaxis = validarSintaxis.validarWriteSintaxis(lexema, linea);
            if (!erroresSintaxis.isEmpty()) {
                for (String string : erroresSintaxis) {
                    errores.add(string);
                }
            }
        }

        return errores;
    }

    private List<String> validarReturn(String lexema, String linea, Validaciones validarSintaxis) {
        List<String> errores = new ArrayList<>();

        if ("RETURN".equals(lexema)) {
            List<String> erroresSintaxis = validarSintaxis.validarReturnSintaxis(lexema, linea);
            if (!erroresSintaxis.isEmpty()) {
                for (String string : erroresSintaxis) {
                    errores.add(string);
                }
            }
        }

        return errores;
    }

    private List<String> validarComandosRepeticion(String lexema, String linea, Validaciones validarSintaxis) {
        List<String> errores = new ArrayList<>();
        List<String> erroresDetectados = validarSintaxis.validarComandosRepeticion(lexema, linea);

        if (!erroresDetectados.isEmpty()) {

            for (String errorRepeticion : erroresDetectados) {
                if (!"".equals(errorRepeticion)) {
                    errores.add(errorRepeticion);
                }
            }
        }

        return errores;
    }

    private List<String> validarComandosIf(String lexema, String linea, Validaciones validarSintaxis) {
        List<String> errores = new ArrayList<>();
        List<String> erroresDetectados = validarSintaxis.validarComandoIf(lexema, linea);

        if (!erroresDetectados.isEmpty()) {

            for (String errorRepeticion : erroresDetectados) {
                if (!"".equals(errorRepeticion)) {
                    errores.add(errorRepeticion);
                }
            }
        }

        return errores;
    }

    private List<String> validarComandosEnd(String linea) {

        List<String> declaracion_postEnd = new ArrayList<>();

        if (Utilidades.contadorEnd >= 1) {
            if (!linea.matches(TablaDeSintaxis.ReglasGramaticales.FIN_PROGRAMA.getPatron())) {

                if (linea.contains(TablaDeAlfabeto.Tipos.Libelula.Patron)) {
                    declaracion_postEnd.add(TablaDeErrores.Errores.ERROR_E4.getDescripcion() + " Se detectó comando LIBELULA.");
                }
                if (linea.contains(TablaDeAlfabeto.Tipos.Modula2.Patron) && !linea.contains(TablaDeAlfabeto.Tipos.Libelula.Patron)) {
                    declaracion_postEnd.add(TablaDeErrores.Errores.ERROR_E4.getDescripcion() + " Se detectó comando MODULA.");
                }
                if (linea.contains(TablaDeAlfabeto.Tipos.Variable.Patron)) {
                    declaracion_postEnd.add(TablaDeErrores.Errores.ERROR_E4.getDescripcion() + " Se detectó comando Variable.");
                }
                if (linea.contains(":=")) {
                    declaracion_postEnd.add(TablaDeErrores.Errores.ERROR_E4.getDescripcion() + " Se detectó comando asignación.");
                }
            }
        }

        return declaracion_postEnd;
    }

    //VALIDACIONES SECUNDARIAS DE LA FUNCION ValidarPalabraReservada
    private List<String> validarModule(String linea, Validaciones validarSintaxis) {
        List<String> errores = new ArrayList<>();
        String errorRepetido = (validarSintaxis.validarModuleRepetido(linea));
        List<String> errorPatron = (validarSintaxis.validarModuleSintaxis(linea));

        if (!"".equals(errorRepetido)) {
            errores.add(errorRepetido);
        }

        if (!errorPatron.isEmpty()) {
            for (String string : errorPatron) {
                errores.add(string);
            }
        }

        return errores;
    }

    private List<String> validarBegin(String linea, Validaciones validarSintaxis) {
        List<String> errores = new ArrayList<>();
        String errorRepetido = (validarSintaxis.validarBeginRepetido(linea));
        String errorSintaxis = (validarSintaxis.validarBeginSintaxis(linea));

        if (!"".equals(errorRepetido)) {
            errores.add(errorRepetido);
        }

        if (!"".equals(errorSintaxis)) {
            errores.add(errorSintaxis);
        }
        return errores;
    }

    private List<String> validarEnd(String linea, Validaciones validarSintaxis) {
        List<String> errores = new ArrayList<>();
        String errorRepetido = (validarSintaxis.validarEndRepetido(linea));
        List<String> errorSintaxis = (validarSintaxis.validarEndSintaxis(linea));

        if (!"".equals(errorRepetido)) {
            errores.add(errorRepetido);
        }

        if (!errorSintaxis.isEmpty()) {
            for (String string : errorSintaxis) {
                errores.add(string);
            }
        }

        return errores;
    }

    private List<String> validarVar(String linea, Validaciones validarSintaxis) {
        List<String> errores = new ArrayList<>();
        String errorRepetido = (validarSintaxis.validarVarRepetido(linea));
        List<String> errorSintaxis = (validarSintaxis.validarVarSintaxis(linea));

        if (!"".equals(errorRepetido)) {
            errores.add(errorRepetido);
        }

        if (!errorSintaxis.isEmpty()) {
            for (String string : errorSintaxis) {
                errores.add(string);
            }
        }

        return errores;
    }

}
