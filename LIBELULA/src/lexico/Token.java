package lexico;

import diccionario.TablaDeAlfabeto;

public class Token {

    private final TablaDeAlfabeto.Tipos tipo;
    private final String valor;

    /**
     * Constructor de la clase Token. Crea un nuevo token con el tipo y valor
     * especificados.
     *
     * @param tipo El tipo del token, una constante de la enumeración Tipos de
     * la clase TablaDeAlfabeto.
     * @param valor El valor del token, una cadena de texto que contiene la
     * representación textual del token.
     */
    public Token(TablaDeAlfabeto.Tipos tipo, String valor) {
        this.tipo = tipo;
        this.valor = valor;
    }

    /**
     * Método para obtener el tipo del token.
     *
     * @return El tipo del token, una constante de la enumeración Tipos de la
     * clase TablaDeAlfabeto.
     */
    public TablaDeAlfabeto.Tipos getTipo() {
        return tipo;
    }

    /**
     * Método para obtener el valor del token.
     *
     * @return El valor del token, una cadena de texto que contiene la
     * representación textual del token.
     */
    public String getValor() {
        return valor;

    }
}
