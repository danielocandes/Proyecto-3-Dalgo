/*
Proyecto 3 Diseño y Análisis de Algoritmos
Trabajo realizado por Luisa Mora (202410347) y Daniel Ocampo (202410656)
*/

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

class MapaFocos {
    /**
     * Clase para representar los focos del campo. Tiene dos mapas, uno con llaves en coordenadas X y otro con llaves en
     * coordenadas Y. Esto permite guardar cada foco con otros focos que sean relevantes a él (son similares en alguna coordenada).
     * Permite ver todos los focos desde la perspectiva de una fila completa o de una columna completa.
     */
    public Map<Integer, ArrayList<Integer>> coordsX;
    public Map<Integer, ArrayList<Integer>> coordsY;

    public MapaFocos(ArrayList<Tuple> focos){
        coordsX = new HashMap<Integer, ArrayList<Integer>>();
        coordsY = new HashMap<Integer, ArrayList<Integer>>();

        for (Tuple tuple : focos){
            int x = tuple.x;
            int y = tuple.y;
            coordsX.putIfAbsent(x, new ArrayList<Integer>());
            coordsY.putIfAbsent(y, new ArrayList<Integer>());

            coordsX.get(x).add(y);
            coordsY.get(y).add(x);
        }
    }
}

class Tuple {

    /**
     * Tupla normalita de números que resulta útil ya que se trabaja mucho con coordenadas 2D en este proyecto.
     */
    public int x;
    public int y;

    public Tuple(int x, int y) {
        this.x = x;
        this.y = y;
    }

}

class LineMapTuple {

    /**
     * Clase que contiene dos mapas de líneas en uno. Sirve mucho para el retorno general del algoritmo.
     */
    public Map<Integer, ArrayList<Tuple>> a;
    public Map<Integer, ArrayList<Tuple>> b;

    public LineMapTuple(Map<Integer, ArrayList<Tuple>> a, Map<Integer, ArrayList<Tuple>> b) {
        this.a = a;
        this.b = b;
    }

}

public class ProblemaP3 {

    /**
     * Crea la mínima cantidad de líneas paralelas necesarias para cubrir todos los focos sin restricción de cómo se deben limitar las líneas.
     * @param coords Mapa que por cada fila/columna tiene una lista de donde se encuentran todos los focos bajo ese contexto
     * @return Mapa que representa las líneas, con la llave siendo la coordenada estática y el valor siendo una tupla que representa la
     * posición inicial y la posición final de la coordenada dinámica (por ejemplo, para una línea horizontal la llave sería la posición en 'y',
     * ya que este valor se mantiene estático a través de la trayectoria, y el valor sería la tupla [x1, x2] representando el cambio de 'x').
     */
    public Map<Integer, ArrayList<Tuple>> crearLineasSinRestriccion(Map<Integer, ArrayList<Integer>> coords){
        Map<Integer, ArrayList<Tuple>> lineas = new HashMap<Integer, ArrayList<Tuple>>();
        for (Integer c : coords.keySet()){
            lineas.put(c, new ArrayList<Tuple>());
            ArrayList<Integer> focos = coords.get(c);
            focos.sort(null);
            Tuple linea = new Tuple(focos.get(0), focos.getLast());
            lineas.get(c).add(linea);
        }

        return lineas;
    }

    /**
     * Crea la mínima cantidad de líneas perpendiculares a las que ya se agregaron codiciosamente a la respuesta tal que se cubran todos
     * los focos sin que hayan intersecciones con las líneas ya agregadas en puntos fuera de los focos.
     * @param coords Mapa que por cada fila/columna tiene una lista de donde se encuentran todos los focos bajo ese contexto
     * @param restricciones Mapa que representa las lineas que ya se habían agregado anteriormente.
     * @return Mapa que representa las nuevas lineas que cubren los focos sin intersecciones innecesarias con las demás lineas.
     */
    public Map<Integer, ArrayList<Tuple>> crearLineasConRestriccion(Map<Integer, ArrayList<Integer>> coords, 
                                                                    Map<Integer, ArrayList<Tuple>> restricciones)
    {
        Map<Integer, ArrayList<Tuple>> lineas = new HashMap<Integer, ArrayList<Tuple>>();
        ArrayList<Integer> keysRestriccion = new ArrayList<Integer>();
        for (Integer key : restricciones.keySet()) {
            keysRestriccion.add(key);
        }
        keysRestriccion.sort(null);

        for (Integer c : coords.keySet()){
            lineas.put(c, new ArrayList<Tuple>());
            ArrayList<Integer> focos = coords.get(c);
            focos.sort(null);

            int start = focos.get(0);
            int i = 0;

            while (i < focos.size()){
                int tObjetivo = focos.get(i);
                int bloqueo = primerBloqueoEncontrado(c, start, tObjetivo, restricciones, coords);
                if (bloqueo != 0){
                    Tuple linea = new Tuple(start, focos.get(i-1));
                    lineas.get(c).add(linea);

                    start = focos.get(i);
                }
                i++;
            }
            Tuple linea = new Tuple(start, focos.getLast());
            lineas.get(c).add(linea);
        }

        return lineas;
    }

   
    /**
     * Dada una línea con su eje estático y su trayectoria en el eje dinámico, busca el primer bloqueo que se encuentre con alguna otra linea perpendicular.
     * @param pos Valor de coordenada estática de la línea (si es linea horizontal, es su valor en el eje y, para linea vertical es su valor en el eje x).
     * @param t1 Punto de inicio de la trayectoria en su coordenada dinámica
     * @param t2 Punto de fin de la trayectoria en su coordenada dinámica
     * @param restricciones Mapa de lineas que ya fueron agregadas a la respuesta; la función revisa si hay bloqueos con estas.
     * @param coords Mapa con los focos del problema.
     * @return Entero x tal que t1 <= x <= t2 donde se genere algún bloqueo con una linea perpendicular. Si no hay bloqueos, retorna 0.
     */
    public int primerBloqueoEncontrado(int pos, int t1, int t2, 
                                Map<Integer, ArrayList<Tuple>> restricciones, Map<Integer, ArrayList<Integer>> coords)
    {
        ArrayList<Integer> keysRestricciones = new ArrayList<Integer>();
        for (Integer k : restricciones.keySet()){
            keysRestricciones.add(k);
        }
        keysRestricciones.sort(null); // Se organizan las lineas que ya esten en la respuesta por su posición
        
        // Se pueden revisar solo los puntos donde podría haber intersección con la línea cuyos bloqueos estamos buscando en
        // vez de tener que revisar todos los ejes opuestos con focos uno a uno. Hacemos binary search para encontrar el punto inicial.
        int start = Collections.binarySearch(keysRestricciones, t1); 

        for (int i = start; i < keysRestricciones.size(); i++){ // Vamos desde la primera línea que puede haber intersección posible hasta la última
            int t = keysRestricciones.get(i);
            if (t > t2){ break;} // Si ya estamos revisando una línea que va después del final de la línea cuyos bloqueos estamos revisando, no hay necesidad de seguir.

            for (Tuple tuple : restricciones.get(t)){
                if (!coords.get(pos).contains(t) && tuple.x <= pos && pos <= tuple.y){ // Si se intersecan las líneas en un punto y no hay foco, es un bloqueo.
                    return t;
                }
            }
        }

        return 0;
    }

    /**
     * Retorna la cantidad de lineas en total dentro de un mapa de lineas
     * @param mapaLineas mapa de lineas
     * @return Tamaño del mapa de lineas
     */
    public int contarLineas(Map<Integer, ArrayList<Tuple>> mapaLineas){
        int count = 0;
        for (ArrayList<Tuple> lineasSeguidas : mapaLineas.values()){
            count+= lineasSeguidas.size();
        }
        return count;
    }

    /**
     * Ejecuta el algoritmo aproximado que comienza agregando las lineas horizontales de manera greedy
     * @param focos Lista de tuplas que representan las posiciones de los focos de infección
     * @return Los dos mapas de lineas horizontales y verticales respectivamente de la respuesta.
     */
    public LineMapTuple inicioHorizontales(ArrayList<Tuple> focos){
        MapaFocos mapasCoords = new MapaFocos(focos);
        Map<Integer, ArrayList<Tuple>> lineasHorizontales = crearLineasSinRestriccion(mapasCoords.coordsY);
        Map<Integer, ArrayList<Tuple>> lineasVerticales = crearLineasConRestriccion(mapasCoords.coordsX, lineasHorizontales);
        return new LineMapTuple(lineasHorizontales, lineasVerticales);
    }

    /**
     * Ejecuta el algoritmo aproximado que comienza agregando las lineas verticales de manera greedy
     * @param focos Lista de tuplas que representan las posiciones de los focos de infección
     * @return Los dos mapas de lineas horizontales y verticales respectivamente de la respuesta.
     */
    public LineMapTuple inicioVerticales(ArrayList<Tuple> focos){
        MapaFocos mapasCoords = new MapaFocos(focos);
        Map<Integer, ArrayList<Tuple>> lineasVerticales = crearLineasSinRestriccion(mapasCoords.coordsX);
        Map<Integer, ArrayList<Tuple>> lineasHorizontales = crearLineasConRestriccion(mapasCoords.coordsY, lineasVerticales);
        return new LineMapTuple(lineasHorizontales, lineasVerticales);
    }

    /**
     * Corre dos algoritmos aproximados casi iguales con la distinción que uno comienza poniendo líneas horizontales y otro
     * comienza poniendo líneas verticales. Compara el tamaño de ambas respuestas, y retorna la de menor respuesta.
     * @param focos Lista de tuplas que representan las posiciones de los focos de infección
     * @return Los dos mapas de lineas horizontales y verticales respectivamente de la respuesta optimizada.
     */
    public LineMapTuple greedyAlgorithm(ArrayList<Tuple> focos){
        LineMapTuple initHorizontal = inicioHorizontales(focos);
        LineMapTuple initVertical = inicioVerticales(focos);

        int total1 = contarLineas(initHorizontal.a) + contarLineas(initHorizontal.b);
        int total2 = contarLineas(initVertical.a) + contarLineas(initVertical.b);
        if (total1 < total2)
            return initHorizontal;
        else
            return initVertical;
    }

    public static void main(String[] args) throws Exception {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            int T = Integer.parseInt(br.readLine().trim());

            

        } catch (Exception e) {
            System.out.println(0);
        }
    }

}
