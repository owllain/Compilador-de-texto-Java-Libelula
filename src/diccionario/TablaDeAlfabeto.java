package diccionario;

/**
 * Esta clase representa una tabla de alfabeto que contiene patrones de
 * expresiones regulares para varios tipos de lexemas. Estos patrones se
 * utilizan para analizar el código fuente.
 */
public class TablaDeAlfabeto {

    /**
     * Enumeración que define los tipos de lexemas que se pueden encontrar en el
     * código fuente. Cada tipo de lexema tiene un patrón de expresión regular
     * asociado que se utiliza para identificar y analizar el lexema
     * correspondiente.
     */
    public enum Tipos {

        Libelula("(BEGIN|CHAR|ELSE|END|IF|INTEGER|MODULE|Read|ReadInt|ReadReal|REAL|REPEAT|RETURN|THEN|UNTIL|VAR|Write|WriteInt|WriteLn|WriteReal|WriteString)"),
        Modula2("(ABS|ABSTRACT|AND|ARRAY|AS|BEGIN|BITSET|BOOLEAN|BY|CAP|CARDINAL|CASE|CHAR|CHR|CLASS|CMPLX|COMPLEX|CONST|DEC|DEFINITION|DISPOSE|DIV|DO|ELSE|ELSIF|END|EXCEPT|EXCL|EXIT|EXPORT|FALSE|FINALLY|FLOAT|FOR|FORWARD|FROM|GENERIC|GUARD|HALT|HIGH|IF|IM|IMPLEMENTATION|IMPORT|IN|INC|INCL|INHERIT|INT|INTEGER|INTERRUPTIBLE|LENGTH|LFLOAT|LONGCOMPLEX|LONGREAL|LOOP|MAX|MIN|MOD|MODULE|NEW|NIL|NOT|ODD|OF|OR|ORD|OVERRIDE|PACKEDSET|POINTER|PROC|PROCEDURE|PROTECTION|QUALIFIED|RE|READONLY|REAL|RECORD|REM|REPEAT|RETRY|RETURN|REVEAL|SET|SIZE|THEN|TO|TRACED|TRUE|TRUNC|TYPE|UNINTERRUPTIBLE|UNSAFEGUARDED|UNTIL|VAL|VAR|WHILE|WITH)"),
        Identificador("[A-Za-z][A-Za-z0-9]*"),
        Numeros("[0-9]+"),
        Simbolo("[,;:%?'\\.]"),
        Cadena("\"[^\"]*\""),
        EspacioEnBlanco("\\s+"),
        Operadores("\\+|-|\\*|/|\\(|\\)|:=|<|>|<=|>=|<>|=|#"),
        Variable("(BOOLEAN|CHAR|INTEGER|REAL)"),
        Desconocido(".*");

        public final String Patron;

        Tipos(String lexema) {
            this.Patron = lexema;
        }
    }

}

