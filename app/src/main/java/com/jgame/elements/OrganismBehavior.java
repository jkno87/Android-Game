package com.jgame.elements;

import com.jgame.util.GeometricElement;
import com.jgame.util.TimeCounter;
import com.jgame.util.Vector2;

import java.util.List;

/**
 * Clase que se representa el estado de un organismo en el tiempo. Tambien se encarga
 * de tomar decisiones sobre como interactua con otros organismos en caso de colisionar.
 * Created by jose on 12/11/15.
 */
public abstract class OrganismBehavior {

    private Vector2.RotationMatrix alsoTest = new Vector2.RotationMatrix(10);
    public TimeCounter timeRemaining;
    public GeometricElement bounds;
    public int hp;
    public int foodPoints;
    //Variable que representa a un organismo que trata de interactuar con otro
    public final boolean active;
    public Vector2 base;

    public OrganismBehavior(float timeToLive, GeometricElement bounds, int hp, int foodPoints, boolean active){
        timeRemaining = new TimeCounter(timeToLive);
        this.bounds = bounds;
        this.hp = hp;
        this.foodPoints = foodPoints;
        this.active = active;
    }

    public void setBase(Vector2 base){
        this.base = base;
    }

    /**
     * Determina si el organismo sigue vivo dependiendo de su hp y de su tiempo de vida
     * @return boolean con el estado del organismo
     */
    public boolean isAlive(){
        return !timeRemaining.completed() && hp > 0;
    }


    /**
     * Esto solo es un test para hacer una rotacion, se debe de ir.
     * @param target
     */
    public void testRotation(Vector2 target){
        target.rotate(alsoTest);
    }

    /**
     * Actualiza el estado del organismo en funcion al tiempo transcurrido
     * @param timeDifference diferencia de tiempo entre las actualizaciones
     */
    public abstract void age(float timeDifference);

    /**
     * Evalua si el organismo colisiona con otro elemento del juego
     * @param e GameElement que puede colisionar
     */
    public abstract void evaluateCollision(GameElement e);
}
