package com.jgame.elements;

import com.jgame.definitions.GameIds;
import com.jgame.util.Circle;
import com.jgame.util.GeometricElement;
import com.jgame.util.TimeCounter;
import com.jgame.util.Vector2;

import java.util.List;

/**
 * Created by jose on 3/09/15.
 */
public class Organism implements GameElement {

    public TimeCounter lifeTimer;
    public Circle interactionBox;
    public int hp;
    public final int foodPoints;

    public Organism (float timeToLive, Vector2 position, float size, int hp, int foodPoints){
        lifeTimer = new TimeCounter(timeToLive);
        interactionBox = new Circle(position, size);
        this.hp = hp;
        this.foodPoints = foodPoints;

    }

    @Override
    public void update(List<GameElement> others, float timeDifference){
        lifeTimer.accum(timeDifference);
    }

    @Override
    public void interact(GameElement e){

    }

    @Override
    public int getId(){
        return GameIds.FOOD_ORGANISM_ID;
    }

    @Override
    public float getSize(){
        return interactionBox.radius;
    }

    @Override
    public GeometricElement getBounds(){
        return interactionBox;
    }

    /**
     * Regresa un entero que representa la porcion de comida representada por foodPoints o por el hp disponible.
     * @return cantidad de comida disponible en un turno
     */
    public int takeFood(){
        int foodAvailable = hp >= foodPoints ? foodPoints : hp;
        hp -= foodAvailable;

        return foodAvailable;
    }

    public boolean vivo(){
        return !lifeTimer.completed() && hp > 0;
    }

    public Vector2 getPosition(){
        return interactionBox.position;
    }

    public float getPctAlive(){
        return 1.0f - lifeTimer.pctCharged();
    }
}
