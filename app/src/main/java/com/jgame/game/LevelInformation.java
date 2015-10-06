package com.jgame.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Esta clase incluye la informacion requerida por el nivel actual. Ej. los organismos requeridos para completar el nivel.
 * Created by ej-jose on 2/10/15.
 */
public class LevelInformation {

    private final HashMap<Integer, Integer> requiredObjects;

    private LevelInformation(HashMap<Integer, Integer> requiredObjects){
        this.requiredObjects = new HashMap<>(requiredObjects);
    }

    /**
     * Genera una lista de LevelObjective que contiene los elementos del juego requeridos para terminar el nivel
     * @return List de LevelObjective con los requierimientos para pasar el nivel.
     */
    public List<LevelObjective> getObjectives(){
        Iterator<Integer> hashIterator = requiredObjects.keySet().iterator();
        ArrayList<LevelObjective> objectives = new ArrayList<>();
        while(hashIterator.hasNext()){
            int key = hashIterator.next();
            objectives.add(new LevelObjective(key, requiredObjects.get(key)));
        }

        return objectives;
    }

    /**
     *
     * @param collectedObjects
     * @return
     */
    public boolean objectivesMatched(HashMap<Integer, Integer> collectedObjects){
        Iterator<Integer> collectedIterator = collectedObjects.keySet().iterator();
        while(collectedIterator.hasNext()){

        }

        throw new UnsupportedOperationException("Implementar metodos");
    }

    public static class LevelObjective {
        public final int id;
        public int count;

        public LevelObjective (int id, int count){
            this.id = id;
            this.count = count;
        }
    }


    public static class LevelInfoCreator {

        private HashMap<Integer, Integer> elements;

        public LevelInfoCreator(){
            elements = new HashMap<Integer, Integer>();
        }

        /**
         * Agrega una llave al mapa interno del objeto. En caso de que se repita una llave, se lanza una excepcion.
         * @param key llave que representa al elemento requerido para pasar un nivel
         * @param quantity numero de objetos necesarios para cumplir el objetivo
         * @return Regresa el mismo LevelInfoCreator
         */
        public LevelInfoCreator addObjective(int key, int quantity){
            if(elements.containsKey(key))
                throw new UnsupportedOperationException("La llave ya se encuentra en el diccionario");

            elements.put(key, quantity);

            return this;
        }

        /**
         * Crea un objeto LevelInformation con los elementos definidos en elements.
         * @return Nuevo objeto LevelInformation.
         */
        public LevelInformation create(){
            return new LevelInformation(elements);
        }

    }
}