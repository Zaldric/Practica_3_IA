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
public class AlgoritmoMiniMax extends Algoritmo {

    private int playerColor;
    private final int blanco = 1;
    private final int negro = -1;

    private int max = 0;
    private int min = 0;

    private int profTotal;

    private int puntos;
    private Casilla mejor;
    
    private Nodo raizz;

    /** 
     * Constructor por defecto de Mini-Max.
     */
    public AlgoritmoMiniMax() {
        puntos = 0;
        mejor = new Casilla();
    }

    
    @Override
    public Tablero obtenerNuevaConfiguracionTablero(Tablero tablero, short turno) {

        System.out.println("analizando siguiente jugada con MINIMAX");
        this.playerColor = turno;
        Tablero tableroJugada = new Tablero();
        tableroJugada = tablero.copiarTablero();
        try {
            miniMax(tableroJugada, this.getProfundidad(), playerColor);
            Thread.sleep(1000);
        } catch (Exception e) {
        }

        return (tableroJugada);
    }

    /**
     *
     * Éste es el método que tenemos que implementar.
     *
     * Algoritmo Mini-Max para determinar cuál es el siguiente mejor movimiento
     *
     * @param tablero Configuración actual del tablero
     * @param prof Profundidad de búsqueda
     * @param jugadorActual Nos indica a qué jugador (FICHA_BLANCA ó
     * FICHA_NEGRA) le toca
     */
    public void miniMax(Tablero tablero, int prof, int jugadorActual) {

        profTotal = prof;

        if (jugadorActual == blanco) {
            max = blanco;
            min = negro;
        } else {
            max = negro;
            min = blanco;
        }

        Nodo raiz = new Nodo(tablero, jugadorActual);
        raiz.crearHijosRaiz();

        raizz=raiz;
        
        for (int i = 0; i < raiz.getHijos().size(); i++) {
            crearHijos(raiz.getHijos().get(i));
        }

        algoritmo(raiz);

        for (int i = 0; i < raiz.getHijos().size(); i++) {
            if (raiz.getHijos().get(i).getValor() == raiz.getValor()) {

                Casilla ca = new Casilla();
                if (jugadorActual == blanco) {
                    ca.asignarFichaBlanca();
                } else {
                    ca.asignarFichaNegra();
                }

                Casilla[][] matriz = tablero.getMatrizTablero();
                Casilla[][] matrizAux = raiz.getHijos().get(i).getTablero().getMatrizTablero();
                for (int ii = 0; ii < raiz.getTablero().getCantidadFilas(); ii++) {
                    for (int jj = 0; jj < raiz.getTablero().getCanidadColumnas(); jj++) {
                        if ((!matriz[ii][jj].esBlanca() && !matriz[ii][jj].esNegra()) && (matrizAux[ii][jj].esBlanca() || matrizAux[ii][jj].esNegra())) {
                            ca.fila = ii;
                            ca.col = jj;
                            tablero.ponerFicha(ca);
                            return;
                        }
                    }
                }
                return;
            }
        }
    }

    /**
     * Funcion del algoritmo Mini-Max que asigna un valor de utilidad a todos los nodos del arbol de estados.
     * @param nodo: el nodo de inicio. 
     */
    public void algoritmo(Nodo nodo) {
        if (nodo.getHijos().isEmpty()) { //Caso base de ser un nodo hoja
            nodo.setValor(Heuristica.h4(raizz.getTablero(),nodo.getTablero(), playerColor));
        } else {
            for (int i = 0; i < nodo.getHijos().size(); i++) {
                algoritmo(nodo.getHijos().get(i));
            }
            int nAux;

            if (nodo.jugador == max) {
                nAux=-99999;
                for (int i = 0; i < nodo.getHijos().size(); i++) {
                    if (nodo.getHijos().get(i).getValor() > nAux) {
                        nAux = nodo.getHijos().get(i).getValor();
                    }
                }
            }else{
                nAux=99999;
                for (int i = 0; i < nodo.getHijos().size(); i++) {
                    if (nodo.getHijos().get(i).getValor() < nAux) {
                        nAux = nodo.getHijos().get(i).getValor();
                    }
                }
            }
            nodo.setValor(nAux);
        }
    }

    /**
     * Crea los hijos de un nodo dado por parametro.
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
     * Funcion que devuelve el jugador opuesto a uno dado.
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

    public class Nodo {

        private Tablero tablero;
        private ArrayList<Nodo> hijos;
        private int profundidad;
        private int jugador;
        private int valor;

        /**
         * Constructor parametrizado de la clase Nodo.
         * @param tabl: el tablero asociado al nodo.
         * @param jugadorr: el jugador asociado al nodo.
         */
        public Nodo(Tablero tabl, int jugadorr) {
            tablero = tabl;
            hijos = new ArrayList<>();
            profundidad = 0;
            jugador = jugadorr;
            valor = -1;
        }

        /**
         * Constructor parametrizado de la clase Nodo.
         * @param tabl: el tablero asociado al nodo.
         * @param prof: la profundidad asociada a un nodo.
         * @param jugadorr: el jugador asociado al nodo. 
         */
        public Nodo(Tablero tabl, int prof, int jugadorr) {
            tablero = tabl;
            hijos = new ArrayList<>();
            profundidad = prof;
            jugador = jugadorr;
            valor = -1;
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
                    } else {
                      
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

        /**
         * @return the valor
         */
        public int getValor() {
            return valor;
        }

        /**
         * @param valor the valor to set
         */
        public void setValor(int valor) {
            this.valor = valor;
        }

    }
}
