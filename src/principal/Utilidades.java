package principal;

import java.util.ArrayList;
import java.util.List;

public class Utilidades {

    public static int contadorModule = 0;
    public static int contadorVar = 0;
    public static int contadorEnd = 0;
    public static int contadorBegin = 0;
    public static int contadorRepeat = 0;
    public static int contadorUntil = 0;
    public static int contadorIf = 0;
    public static String[] revisionDeArchivo;
    public static List<String> variablesDeclaradas = new ArrayList<>();
    public static List<String> varTiposDeclaradas = new ArrayList<>();
    public static String nombreModule = "";
    public static String nombreEnd = "";
}
