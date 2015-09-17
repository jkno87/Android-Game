package com.jgame.elements;

import com.jgame.game.GameLogic;
import com.jgame.util.TimeCounter;
import com.jgame.util.Vector2;

/**
 * Created by jose on 26/02/15.
 */
public class TimedEnemy extends SimpleEnemy {

    private final TimeCounter lifeCounter;

    public TimedEnemy(int hp, int damage, Vector2 position, float size, float timeAlive, int points) {
        super(hp, damage, position, size, new Vector2(), points);
        lifeCounter = new TimeCounter(timeAlive);
    }

    @Override
    public void updateDeprecated(GameLogic gameInstance, float timeDiff){
        lifeCounter.accum(timeDiff);
    }

    @Override
    public boolean vivo(){
        return hp > 0 && !lifeCounter.completed();
    }
}
