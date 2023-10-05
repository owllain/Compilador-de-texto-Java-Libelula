package diccionario;

/**
 * La clase TablaDeSintaxis contiene una serie de expresiones regulares que
 * representan las reglas gramaticales de un lenguaje de programación
 * específico.
 */
public class TablaDeSintaxis {

    public enum ReglasGramaticales {

        INICIO_PROGRAMA("^\\s*(?<!\\w)MODULE\\s+[A-Za-z][A-Za-z0-9]*\\s*;\\s*$"),
        FIN_PROGRAMA("^\s*END\s+(?!\s+$)([A-Za-z][A-Za-z0-9]*\s*)*[.]\s*$"),
        COMENTARIO_EN_LINEA("^.*\\(\\*.*\\*\\).*"),
        PALABRA_RESERVADA_BEGIN("^\\s*BEGIN\\s*"),
        PALABRA_RESERVADA_VAR("^\\s*VAR\\s*"),
        DECLARACION_VARIABLE("^\\s*[A-Za-z][A-Za-z0-9]*\\s*:\\s*(INTEGER|REAL|BOOLEAN|CHAR)\\s*;\\s*$"),
        MULTIPLES_DECLARACIONES("^\\s*([A-Za-z][A-Za-z0-9]*\\s*,\\s*)*[A-Za-z][A-Za-z0-9]*\\s*:\\s*(INTEGER|REAL|BOOLEAN|CHAR)\\s*;\\s*$"),
        INICIALIZACION_VARIABLES("^\\s*[A-Za-z0-9]+\\s*:=\\s*.*;\\s*$"),
        INSTRUCCION_CONDICIONAL("\\s*IF\\s*\\(\\s*(\\w+)\\s*((?:#|==|>|<|>=|<=))\\s*(\\w+)\\s*\\)\\s*THEN\\b"),
        PALABRA_RESERVADA_REPEAT("^\\s*REPEAT\\s*"),
        PALABRA_RESERVADA_UNTIL("^\\s*UNTIL\\s+\\w+\\s*(?:#|==|>|<|>=|<=)\\s+\\w+\\s*;\\s*$"),
        PALABRA_RESERVADA_READ("\\s*Read\\s*\\(\\s*([a-zA-Z]+[a-zA-Z0-9]*)\\s*\\)\\s*;\\s*"),
        PALABRA_RESERVADA_READINT("\\s*ReadInt\\s*\\(\\s*([a-zA-Z]+[a-zA-Z0-9]*)\\s*\\)\\s*;\\s*"),
        PALABRA_RESERVADA_READREAL("\\s*ReadReal\\s*\\(\\s*([a-zA-Z]+[a-zA-Z0-9]*)\\s*\\)\\s*;\\s*"),
        PALABRA_RESERVADA_WRITE("^\\s*Write\\s*\\(\\s*([a-zA-Z0-9]+)\\s*\\)\\s*;\\s*$"),
        PALABRA_RESERVADA_WRITEINT("^\\s*WriteInt\\s*\\(\\s*([a-zA-Z0-9]+)\\s*,\\s*(\\d+)\\s*\\)\\s*;\\s*$"),
        PALABRA_RESERVADA_WRITEREAL("^\\s*WriteReal\\s*\\(\\s*([a-zA-Z0-9]+)\\s*,\\s*(\\d+)\\s*\\)\\s*;\\s*$"),
        PALABRA_RESERVADA_WRITESTRING("^\\s*[a-zA-Z0-9\\s]*WriteString\\s*\\(\\s*\\'([^\\']*)\\'\\s*\\)\\s*;\\s*[a-zA-Z0-9\\s]*$"),
        PALABRA_RESERVADA_WRITELN("^\\s*WriteLn\\s*;\\s*[a-zA-Z0-9\\s]*$"),
        PALABRA_RESERVADA_RETURN("^\\s*RETURN\\s*;\\s*$");

        private final String patron;

        ReglasGramaticales(String patron) {
            this.patron = patron;
        }

        public String getPatron() {
            return patron;
        }
    }

}
