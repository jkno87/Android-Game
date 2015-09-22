package com.jgame.elements;

import com.jgame.game.GameLogic;
import com.jgame.util.TimeCounter;
import com.jgame.util.Vector2;

import java.util.List;

/**
 * Created by jose on 3/09/15.
 */
public class Organism implements GameElement {

    public TimeCounter lifeTimer;
    private Vector2 position;

    public Organism (float timeToLive, Vector2 position){
        lifeTimer = new TimeCounter(timeToLive);
        this.position = new Vector2(position);
    }

    @Override
    public void updateDeprecated(GameLogic gameInstance, float timeDifference){
    }

    @Override
    public void update(List<GameElement> others, float timeDifference){
        lifeTimer.accum(timeDifference);
    }

    @Override
    public void interact(GameElement e){

    }

    public boolean vivo(){
        return !lifeTimer.completed();
    }

    public Vector2 getPosition(){
        return position;
    }

    public float getPctAlive(){
        return 1.0f - lifeTimer.pctCharged();
    }
}
