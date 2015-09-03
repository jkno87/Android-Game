package com.jgame.elements;

import com.jgame.game.GameLogic;
import com.jgame.util.TimeCounter;
import com.jgame.util.Vector2;

/**
 * Created by jose on 3/09/15.
 */
public class Organism implements GameElement {

    private TimeCounter lifeTimer;
    public Vector2 position;

    public Organism (float timeToLive, Vector2 position){
        lifeTimer = new TimeCounter(timeToLive);
        this.position = new Vector2(position);
    }

    public void update(GameLogic gameInstance, float timeDifference){
        lifeTimer.accum(timeDifference);
    }

    public boolean vivo(){
        return !lifeTimer.completed();
    }
}
