package analizador;

import diccionario.TablaDeErrores;
import diccionario.TablaDeAlfabeto;
import java.util.ArrayList;
import java.util.List;
import lexico.Token;

/**
 * Esta clase se encarga de analizar léxicamente un texto de entrada y generar
 * la lista de tokens correspondiente.
 */
public class AnalizadorLexico {

    public ArrayList<Token> tokenList;

    /**
     * Constructor de la clase AnalizadorLexico. Inicializa la lista de tokens.
     */
    public AnalizadorLexico() {
        tokenList = new ArrayList<>();
    }

    /**
     * Este método se encarga de analizar léxicamente el texto de entrada y
     * generar la lista de tokens correspondiente.
     *
     * @param textoEntrada Texto que se desea analizar léxicamente.
     * @return Lista de errores encontrados durante el análisis léxico.
     */
    public List<String> analizadorLexico(String textoEntrada) {
        List<String> errores = new ArrayList<>();
        String[] lexemas = textoEntrada.split("(?<=\\W)|(?=\\W)|\\n\\s*|\\s+");

        for (String lexema : lexemas) {

            if (!lexema.isEmpty()) {

                TablaDeAlfabeto.Tipos tipoLexema = TablaDeAlfabeto.Tipos.Desconocido; // Tipo por defecto
                // Recorremos los tipos en un orden específico para asignar un único tipo al lexema
                for (TablaDeAlfabeto.Tipos tipo : new TablaDeAlfabeto.Tipos[]{
                    TablaDeAlfabeto.Tipos.Variable, // variable tiene que ser el primer tipo para que detecte BOOLEAN|CHAR|INTEGER|REAL
                    TablaDeAlfabeto.Tipos.Libelula,
                    TablaDeAlfabeto.Tipos.Modula2,
                    TablaDeAlfabeto.Tipos.Cadena,
                    TablaDeAlfabeto.Tipos.EspacioEnBlanco,
                    TablaDeAlfabeto.Tipos.Numeros,
                    TablaDeAlfabeto.Tipos.Operadores,
                    TablaDeAlfabeto.Tipos.Simbolo,
                    TablaDeAlfabeto.Tipos.Identificador
                }) {
                    if (tipo == TablaDeAlfabeto.Tipos.Desconocido) {
                        continue;
                    }
                    if (tipo == TablaDeAlfabeto.Tipos.Modula2 && lexema.matches(TablaDeAlfabeto.Tipos.Libelula.Patron)) {
                        continue;
                    }
                    if (tipo == TablaDeAlfabeto.Tipos.Variable && (lexema.equals("BOOLEAN") || lexema.equals("CHAR") || lexema.equals("INTEGER") || lexema.equals("REAL"))) {
                        tipoLexema = TablaDeAlfabeto.Tipos.Variable;
                        break;
                    }
                    if (lexema.matches(tipo.Patron)) {
                        tipoLexema = tipo;
                        break;
                    }
                }

                Token token = new Token(tipoLexema, lexema);

                tokenList.add(token);

                List<String> analisisTokens = analizarToken(textoEntrada, token);
                if (!analisisTokens.isEmpty()) {
                    for (String erroresDetectados : analisisTokens) {
                        errores.add(erroresDetectados);
                    }
                }

                List<String> comandosTokens = comandoErroneoToken(token);
                if (!comandosTokens.isEmpty()) {
                    for (String erroresDetectados : comandosTokens) {
                        errores.add(erroresDetectados);
                    }
                }

            }

        }

        imprimirTokensAnalizados();
        return errores;
    }

    private void imprimirTokensAnalizados() {

        for (Token token : tokenList) {
            if (token.getTipo() != TablaDeAlfabeto.Tipos.EspacioEnBlanco) {
                System.out.println("Token: " + token.getValor() + " - Tipo: " + token.getTipo());
            }
        }

    }

    private List<String> analizarToken(String linea, Token tokenAnalizado) {
        List<String> errores = new ArrayList<>();
        List<String> analizarPalabraReservada = palabraReservadaToken(linea, tokenAnalizado);

        for (String revisionPalabraReservada : analizarPalabraReservada) {
            if (!"".equals(revisionPalabraReservada)) {
                errores.add(revisionPalabraReservada);
            }
        }

        if (tokenAnalizado.getTipo() == TablaDeAlfabeto.Tipos.Modula2) {
            errores.add(TablaDeErrores.Errores.ERROR_00.getDescripcion() + " Token: " + tokenAnalizado.getValor()
                    + " Tipo: " + tokenAnalizado.getTipo());
        }

        if (tokenAnalizado.getTipo() == TablaDeAlfabeto.Tipos.Desconocido) {
            String regexSimbolo = "[&%$!¡¿?]";
            String error = TablaDeErrores.Errores.ERROR_01.getDescripcion();
            if (tokenAnalizado.getValor().matches("^[^a-zA-Z0-9]*[a-zA-Z][a-zA-Z0-9]*$")) {
                error = error + " El lexema contiene simbolos no permitidos al comienzo.";
            }
            if (tokenAnalizado.getValor().matches("\\d+\\w+")) {
                error = error + " El lexema contiene números al comienzo.";
            }
            if (tokenAnalizado.getValor().matches(regexSimbolo)) {
                error = error + " El lexema es un simbolo no soportado por está versión.";
            }
            error = error + " Token: " + tokenAnalizado.getValor()
                    + " Tipo: " + tokenAnalizado.getTipo();
            errores.add(error);
        }

        if (tokenAnalizado.getTipo() == TablaDeAlfabeto.Tipos.Identificador) {

            int indice = linea.indexOf(tokenAnalizado.getValor());

            if (indice != -1) {  // si el lexema se encuentra en la línea
                char caracterAnterior = indice > 0 ? linea.charAt(indice - 1) : ' ';
                if (Character.isDigit(caracterAnterior)) {
                    errores.add(TablaDeErrores.Errores.ERROR_02.getDescripcion() + " El carácter anterior a ( " + tokenAnalizado.getValor() + " ) es un número.");
                }
                String regex = "[_&%!?¿]";
                if (!TablaDeAlfabeto.Tipos.Operadores.equals(caracterAnterior) && !"".equals(caracterAnterior) && regex.equals(caracterAnterior)) {
                    errores.add(TablaDeErrores.Errores.ERROR_02.getDescripcion() + " El carácter anterior a ( " + tokenAnalizado.getValor() + " ) es un símbolo que no es un operador.");
                }
            }

            if (tokenAnalizado.getValor().length() > 20) {
                errores.add(TablaDeErrores.Errores.ERROR_02.getDescripcion() + " El lexema: ( " + tokenAnalizado.getValor() + " ) tiene más de 20 cáracteres.");
            }

        }

        return errores;
    }

    private List<String> palabraReservadaToken(String linea, Token tokenAnalizado) {
        List<String> errores = new ArrayList<>();
        String regex = "^\\s*END\\s+[A-Za-z][A-Za-z0-9]*\\s*[.]\\s*$";
        boolean errorEnd = false;

        if (!tokenAnalizado.getValor().equals("END")) {
            if (tokenAnalizado.getValor().contains("END") || tokenAnalizado.getValor().contains("end")) {

                if (!linea.matches(regex)) {
                    if (!linea.equalsIgnoreCase("end") && !linea.equalsIgnoreCase("EnD")) {
                        errorEnd = true;
                    }
                }
            }
        }

        if (errorEnd == true) {
            errores.add("Advertencia! El identificador: " + tokenAnalizado.getValor() + " es una expresión erronea de la palabra reservada END.");
        }

        return errores;
    }

    private List<String> comandoErroneoToken(Token tokenAnalizado) {
        List<String> errores = new ArrayList<>();
        String comandoErroneo = "";
        String detectarChar = ("^[a-zA-Z]$");

        if ((tokenAnalizado.getTipo() == TablaDeAlfabeto.Tipos.Identificador && !tokenAnalizado.getValor().matches(detectarChar)) || tokenAnalizado.getTipo() == TablaDeAlfabeto.Tipos.Desconocido) {

            String patron = TablaDeAlfabeto.Tipos.Libelula.Patron;
            String[] comandos = patron.substring(1, patron.length() - 1).split("\\|");
            for (String comando : comandos) {
                if (tokenAnalizado.getValor().contains(comando) || comando.contains(tokenAnalizado.getValor())) {
                    comandoErroneo += (" (" + comando + ") ");
                }
                if (tokenAnalizado.getValor().toUpperCase().contains(comando) || comando.toUpperCase().contains(tokenAnalizado.getValor())) {
                    comandoErroneo += (" (" + comando + ") ");
                }

            }

            if (!"".equals(comandoErroneo)) {
                errores.add(TablaDeErrores.Errores.ERROR_15.getDescripcion() + comandoErroneo);
            }

        }

        return errores;
    }

}
