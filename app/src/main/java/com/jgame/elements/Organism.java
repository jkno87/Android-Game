package com.jgame.elements;

import com.jgame.definitions.GameIds;
import com.jgame.util.Circle;
import com.jgame.util.TimeCounter;
import com.jgame.util.Vector2;

import java.util.List;

/**
 * Created by jose on 3/09/15.
 */
public class Organism implements GameElement {

    public TimeCounter lifeTimer;
    public Circle interactionBox;

    public Organism (float timeToLive, Vector2 position, float size){
        lifeTimer = new TimeCounter(timeToLive);
        interactionBox = new Circle(position, size);
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

    public void decreaseLife(float time){
        lifeTimer.accum(time);
    }

    public boolean vivo(){
        return !lifeTimer.completed();
    }

    public Vector2 getPosition(){
        return interactionBox.position;
    }

    public float getPctAlive(){
        return 1.0f - lifeTimer.pctCharged();
    }
}
