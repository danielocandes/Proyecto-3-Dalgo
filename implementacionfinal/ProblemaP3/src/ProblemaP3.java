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

public class ProblemaP3 {

    /**
     * Crea la mínima cantidad de líneas paralelas necesarias para cubrir todos los focos sin restricción de cómo se deben limitar las líneas.
     * @param coords Mapa que por cada fila/columna tiene una lista de donde se encuentran todos los focos bajo ese contexto
     * @return Mapa que representa las líneas, con la llave siendo el eje estático y el valor siendo una tupla que representa la
     * posición inicial y la posición final del eje dinámico (por ejemplo, para una línea horizontal la llave sería la posición en 'y',
     * ya que este valor se mantiene estático a través de la trayectoria, y el valor sería la tupla [x1, x2] representando el cambio de 'x').
     */
    public Map<Integer, ArrayList<int[]>> crearLineasSinRestriccion(Map<Integer, ArrayList<Integer>> coords){
        Map<Integer, ArrayList<int[]>> lineas = new HashMap<Integer, ArrayList<int[]>>();
        for (Integer c : coords.keySet()){
            lineas.put(c, new ArrayList<int[]>());
            ArrayList<Integer> focos = coords.get(c);
            focos.sort(null);
            int[] linea = {focos.get(0), focos.getLast()};
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
    public Map<Integer, ArrayList<int[]>> crearLineasConRestriccion(Map<Integer, ArrayList<Integer>> coords, 
                                                                    Map<Integer, ArrayList<int[]>> restricciones)
    {
        Map<Integer, ArrayList<int[]>> lineas = new HashMap<Integer, ArrayList<int[]>>();
        ArrayList<Integer> keysRestriccion = new ArrayList<Integer>();
        for (Integer key : restricciones.keySet()) {
            keysRestriccion.add(key);
        }
        keysRestriccion.sort(null);

        for (Integer c : coords.keySet()){
            lineas.put(c, new ArrayList<int[]>());
            ArrayList<Integer> focos = coords.get(c);
            focos.sort(null);

            int start = focos.get(0);
            int i = 0;

            while (i < focos.size()){
                int tObjetivo = focos.get(i);
                int bloqueo = primerBloqueoEncontrado(c, start, tObjetivo, restricciones, coords);
                if (bloqueo != 0){
                    int[] linea = {start, focos.get(i-1)};
                    lineas.get(c).add(linea);

                    start = focos.get(i);
                }
                i++;
            }
            int[] linea = {start, focos.getLast()};
            lineas.get(c).add(linea);
        }

        return lineas;
    }

   
    /**
     *  Busca la posicion del primer bloqueo que se encuentre al intentar comenzar una linea
     *  en la coordenada 'pos' (sea en el eje x o eje y) y extender esa trayectoria desde t1 a t2
     *  (por ejemplo, si se esta revisando una linea horizontal, pos sería la posición 'y' de
     *  la trayectoria, t1 sería la posición x inicial, t2 sería la posición x final. Si se esta intentando
     *  revisar una linea vertical, sería lo mismo pero con las coordenadas invertidas). Restricciones es el mapa
     *  de lineas que ya se agregaron a la respuesta. Coords son los focos que se dan en cada una de las filas o columnas.
     */
    public int primerBloqueoEncontrado(int pos, int t1, int t2, 
                                Map<Integer, ArrayList<int[]>> restricciones, Map<Integer, ArrayList<Integer>> coords)
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

            for (int[] tuple : restricciones.get(t)){
                if (!coords.get(pos).contains(t) && tuple[0] <= pos && pos <= tuple[1]){ // Si se intersecan las líneas en un punto y no hay foco, es un bloqueo.
                    return t;
                }
            }
        }

        return 0;
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
