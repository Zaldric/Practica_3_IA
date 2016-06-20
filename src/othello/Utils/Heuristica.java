/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package othello.Utils;

import java.io.IOException;

/**
 *
 * @author user
 */
public class Heuristica {

    public static int h1(Tablero tablero) {
        return 0;
    }

    //Una heuristica posible a usar
    public static int h2(Tablero tablero, int playerColor) throws IOException {
        int score = Puntos(playerColor, tablero) - Puntos(-playerColor, tablero);

        // If the game is over
        if (tablero.EsFinalDeJuego()) {
            // if player has won
            if (score > 0) {
                return 100;
            } // if player has lost (or tied)
            else {
                return -100;
            }
        }

        // if game isn't over, return relative advatage
        return score;
    }

    public static int Puntos(int playerColor, Tablero tablero) throws IOException {
        int points = 0;

        for (int x = 0; x < Tablero.CANTIDAD_FILAS_DEFECTO; x++) {
            for (int y = 0; y < Tablero.CANTIDAD_COLUMNAS_DEFECTO; y++) {
                if (tablero.getMatrizTablero()[x][y].obtenerColorFicha() == playerColor) {
                    points++;
                }
            }
        }

        return points;
    }

    /** 
     * Funcion de apollo para la euristica que nos cuenta el numero de fichas de nuestro color del tablero.
     * @param tablero: el tablero actual.
     * @param color: el color del jugador.
     * @return el numero de fichas del color del jugador.
     */
    public static int h3(Tablero tablero, int color) {
        int cont = 0;
        Casilla[][] matriz = tablero.getMatrizTablero();
        if (color == 1) {
            for (int i = 0; i < tablero.getCantidadFilas(); i++) {
                for (int j = 0; j < tablero.getCanidadColumnas(); j++) {
                    if (matriz[i][j].esBlanca()) {
                        ++cont;
                    }
                }
            }
        } else {
            for (int i = 0; i < tablero.getCantidadFilas(); i++) {
                for (int j = 0; j < tablero.getCanidadColumnas(); j++) {
                    if (matriz[i][j].esNegra()) {
                        ++cont;
                    }
                }
            }
        }
        return cont;
    }

    /**
     * Funcion heuristica para nuestros algoritmos, que intenta priorizar el mejor movimiento.
     * @param tablero: el tablero actual.
     * @param hoja: el tablero resultante de la hoja que se quiere evaluar.
     * @param color: el color.
     * @return el valor heuristico para el nodo hoja.
     */
    public static int h4(Tablero tablero, Tablero hoja, int color) {
        int cont;
        int f = tablero.getCantidadFilas();
        int c = tablero.getCanidadColumnas();
        Casilla[][] matrizRaiz = tablero.getMatrizTablero();
        Casilla[][] matrizHoja = hoja.getMatrizTablero();

        cont = h3(hoja, color);

        for (int i = 0; i < f; i++) {
            for (int j = 0; j < c; j++) {
                if (matrizRaiz[i][j].estaVacia()) {
                    if (matrizHoja[i][j].estaVacia()) {
                        //Comprobacion de esquinas
                        if ((i == 0 && j == 0) || (i == 0 && j == (c - 1)) || (i == (f - 1) && j == 0) || (i == (f - 1) && j == (c - 1))) {
                            if (matrizHoja[i][j].obtenerColorFicha() == color) {
                                cont += 500;
                            } else {
                                cont -= 500;
                            }
                            break;
                        }
                        //lateral
                        if (i == 0 || j == 0 || i == (f - 1) || j == (c - 1)) {
                            if (matrizHoja[i][j].obtenerColorFicha() == color) {
                                cont += 10;
                            } else {
                                cont -= 10;
                            }
                        }

                    }
                }
            }
        }
        return cont;
    }
}