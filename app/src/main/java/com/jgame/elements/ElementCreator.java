package com.jgame.elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Objeto que se encarga de crear todos los GameElements en la vida de un nivel de juego.
 * Created by jose on 3/09/15.
 */
public class ElementCreator {

    /**
     * Clase que se encarga de generar GameElements en un intervalo de tiempo. Esto se hace para tener un control más específico
     * de una ola de elementos dependiendo de las necesidades del nivel.
     */
    public abstract static class ElementWave {
        /**
         * Función que se encarga de crear elementos del juego en un intervalo definido
         * @param interval intervalo transcurrido desde la última llamada a generate
         * @return Lista de GameElements producidos en el intervalo de tiempo.
         */
        public abstract List<GameElement> generate(float interval);

        /**
         * Inicializa el estado del objeto
         */
        public abstract void initialize();
    }


    private ElementWave[] waves;

    public ElementCreator(ElementWave[] waves){
        this.waves = waves;
    }

    /**
     * Produce GameElements en el intervalo de tiempo transcurrido. Es el conjunto que contiene todos los elementos producidos
     * individualmente por las ElementWaves.
     * @param interval intervalo de tiempo transcurrido
     * @return Lista total de elementos producidos.
     */
    public List<GameElement> createElements(float interval){
        List<GameElement> elements = new ArrayList<GameElement>();
        for(ElementWave e : waves){
            elements.addAll(e.generate(interval));
        }

        return elements;
    }

}
