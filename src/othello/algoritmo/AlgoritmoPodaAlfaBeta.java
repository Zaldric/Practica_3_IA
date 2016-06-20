/*
 */
package othello.algoritmo;

import othello.Utils.Casilla;
import othello.Utils.Heuristica;
import othello.Utils.Tablero;
import java.util.ArrayList;

/**
 *
 * @author gusamasan
 */
public class AlgoritmoPodaAlfaBeta extends Algoritmo {
// ----------------------------------------------------------------------

// ----------------------------------------------------------------------
    /**
     * Constructores *************************************************
     */
    private int playerColor;
    private final int blanco = 1;
    private final int negro = -1;

    private int profTotal;

    private int puntos;
    private Casilla mejor;

    /**
     * Constructor por defecto de Alfa-Beta.
     */
    public AlgoritmoPodaAlfaBeta() {
        puntos = 0;
        mejor = new Casilla();
    }

    /**
     * ****************************************************************
     */
    @Override
    public Tablero obtenerNuevaConfiguracionTablero(Tablero tablero, short turno) {
        this.playerColor = turno;
        Tablero tableroJugada = tablero.copiarTablero();
        try {
            int beta = Integer.MAX_VALUE;
            int alfa = Integer.MIN_VALUE;
            AlfaBeta(tableroJugada, this.getProfundidad(), playerColor, alfa, beta);
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (tableroJugada);
    }

    /**
     * Funcion que devuelve el jugador opuesto a uno dado.
     *
     * @param Jugador: el jugador a evaluar.
     * @return el jugador opuesto.
     */
    public int jugadorOpuesto(int Jugador) {
        if (Jugador == blanco) {
            return negro;
        } else {
            return blanco;
        }
    }

    /**
     * Crea los hijos de un nodo dado por parametro.
     *
     * @param nodo: el nodo padre.
     */
    public void crearHijos(Nodo nodo) {
        if (nodo.profundidad != profTotal) {
            Casilla casilla = new Casilla();
            Tablero tAux;

            if (nodo.jugador == blanco) {
                casilla.asignarFichaNegra();
            } else {
                casilla.asignarFichaBlanca();
            }

            for (int i = 0; i < nodo.getTablero().getCantidadFilas(); i++) {
                for (int j = 0; j < nodo.getTablero().getCanidadColumnas(); j++) {
                    casilla.fila = i;
                    casilla.col = j;
                    if (nodo.getTablero().movLegal(casilla)) {
                        tAux = nodo.getTablero().copiarTablero();
                        tAux.ponerFicha(casilla);
                        Nodo nodoAux = new Nodo(tAux, (nodo.profundidad + 1), jugadorOpuesto(nodo.getJugador()));
                        nodo.getHijos().add(nodoAux);
                        ++puntos;
                    }
                }
            }
            for (int i = 0; i < nodo.getHijos().size(); i++) {
                crearHijos(nodo.getHijos().get(i));
            }
        }
    }

    /**
     *
     * Éste es el método que tenemos que implementar.
     *
     * Algoritmo AlfaBeta para determinar cuál es el siguiente mejor movimiento
     *
     * @param tablero Configuración actual del tablero
     * @param prof Profundidad de búsqueda
     * @param jugadorActual Nos indica a qué jugador (FICHA_BLANCA ó
     * FICHA_NEGRA) le toca
     * @param alfa
     * @param beta Parámetros alfa y beta del algoritmo
     */
    public void AlfaBeta(Tablero tablero, int prof, int jugadorActual, int alfa, int beta) {
        profTotal = prof;
        Nodo raiz = new Nodo(tablero, jugadorActual);
        raiz.crearHijosRaiz();
        Casilla cas;
        for (int i = 0; i < raiz.getHijos().size(); i++) {
            crearHijos(raiz.getHijos().get(i));
        }
        asignaAlfaBeta(raiz);
        if (jugadorActual == this.playerColor) {
            cas = extraeMov(raiz);
            tablero.ponerFicha(cas);
        } else {
            cas = extraeMov(raiz);
            tablero.ponerFicha(cas);
        }
    }

    /**
     * Funcion que obtiene la casilla del siguiente movimiento que se quiere
     * realizar.
     *
     * @param raiz: el nodo raiz de nuestro arbol de estados.
     * @return el siguiente movimiento que se va a realizar.
     */
    public Casilla extraeMov(Nodo raiz) {
        Casilla ca = new Casilla();
        for (int i = 0; i < raiz.getHijos().size(); i++) {
            if (raiz.getHijos().get(i).valor == raiz.valor) {
                if (playerColor == blanco) {
                    ca.asignarFichaBlanca();
                } else {
                    ca.asignarFichaNegra();
                }
                Casilla[][] matriz = raiz.getTablero().getMatrizTablero();
                Casilla[][] matrizAux = raiz.getHijos().get(i).getTablero().getMatrizTablero();
                for (int ii = 0; ii < raiz.getTablero().getCantidadFilas(); ii++) {
                    for (int jj = 0; jj < raiz.getTablero().getCanidadColumnas(); jj++) {
                        if ((!matriz[ii][jj].esBlanca() && !matriz[ii][jj].esNegra()) && (matrizAux[ii][jj].esBlanca() || matrizAux[ii][jj].esNegra())) {
                            ca.fila = ii;
                            ca.col = jj;
                        }
                    }
                }
                return ca;
            }
        }
        return ca;
    }

    /**
     * Funcion que recorre el arbol de estados en profundidad y va asignando los
     * valores de alfa y beta asi como el valor de utilidad a los nodos, tambien
     * hace las podas
     *
     * @param raiz: el nodo raiz del arbol de estados.
     * @return valores de alfa y beta (recursividad)
     */
    public int asignaAlfaBeta(Nodo raiz) {
        if (raiz.getHijos().isEmpty()) {
            raiz.valor = Heuristica.h3(raiz.tablero, raiz.jugador);
            return raiz.valor;
        } else {
            for (int i = 0; i < raiz.hijos.size(); ++i) {
                if (!raiz.hijos.isEmpty()) {
                    if (playerColor == raiz.jugador) {
                        raiz.alfa = max(raiz.alfa, asignaAlfaBeta(raiz.hijos.get(0)));
                        raiz.valor = raiz.alfa;
                        if (raiz.beta <= raiz.alfa) {
                            break;
                        }
                        return raiz.alfa;
                    } else {
                        raiz.beta = min(raiz.beta, asignaAlfaBeta(raiz.hijos.get(0)));
                        raiz.valor = raiz.beta;
                        if (raiz.beta <= raiz.alfa) {
                            break;
                        }
                        return raiz.beta;
                    }
                }
            }
        }
        return 0;
    }

    /**
     * Devuelve el mayor valor de dos pasados como parametro
     *
     * @param a: valor que se quiere evaluar.
     * @param b: valor que se quiere evaluar.
     * @return el mayor valor de los dos.
     */
    public int max(int a, int b) {
        return (a > b) ? a : b;
    }

    /**
     * Devuelve el menor valor de dos pasados como parametro
     *
     * @param a: valor que se quiere evaluar.
     * @param b: valor que se quiere evaluar.
     * @return el menor valor de los dos.
     */
    public int min(int a, int b) {
        return (a < b) ? a : b;
    }

    public class Nodo {

        public Tablero tablero;
        public ArrayList<Nodo> hijos;
        public int profundidad;
        public int jugador;
        public int alfa;
        public int beta;
        public Casilla cas;
        public int valor;

        /**
         * Constructor parametrizado de la clase Nodo.
         *
         * @param tabl: el tablero asociado al nodo.
         * @param jugadorr: el jugador asociado al nodo.
         */
        public Nodo(Tablero tabl, int jugadorr) {
            tablero = tabl;
            hijos = new ArrayList<>();
            profundidad = 0;
            jugador = jugadorr;
            alfa = 0;
            beta = 100;
            cas = new Casilla();
        }

        /**
         * Constructor parametrizado de la clase Nodo.
         *
         * @param tabl: el tablero asociado al nodo.
         * @param prof: la profundidad asociada a un nodo.
         * @param jugadorr: el jugador asociado al nodo.
         */
        public Nodo(Tablero tabl, int prof, int jugadorr) {
            tablero = tabl;
            hijos = new ArrayList<>();
            profundidad = prof;
            jugador = jugadorr;
        }

        /**
         * Crea todos los hijos del nodo raiz.
         */
        public void crearHijosRaiz() {

            Casilla casilla = new Casilla();
            Tablero tAux;

            if (getJugador() == blanco) {
                casilla.asignarFichaBlanca();
            } else {
                casilla.asignarFichaNegra();
            }
            for (int i = 0; i < getTablero().getCantidadFilas(); i++) {
                for (int j = 0; j < getTablero().getCanidadColumnas(); j++) {
                    casilla.fila = i;
                    casilla.col = j;
                    if (getTablero().movLegal(casilla)) {
                        tAux = getTablero().copiarTablero();
                        tAux.ponerFicha(casilla);
                        getHijos().add(new Nodo(tAux, getProfundidad() + 1, jugadorOpuesto()));
                        ++puntos;
                    }
                }
            }
        }

        /**
         * Funcion que devuelve el jugador opuesto a uno dado.
         *
         * @param Jugador: el jugador a evaluar.
         * @return el jugador opuesto.
         */
        public int jugadorOpuesto() {
            if (getJugador() == blanco) {
                return negro;
            } else {
                return blanco;
            }
        }

        /**
         * @return the tablero
         */
        public Tablero getTablero() {
            return tablero;
        }

        /**
         * @return the hijos
         */
        public ArrayList<Nodo> getHijos() {
            return hijos;
        }

        /**
         * @return the profundidad
         */
        public int getProfundidad() {
            return profundidad;
        }

        /**
         * @return the jugador
         */
        public int getJugador() {
            return jugador;
        }

    }
}
