package com.jgame.elements;

import com.jgame.game.GameLogic;
import com.jgame.util.AnimationData;
import com.jgame.util.TimeCounter;
import com.jgame.util.Vector2;

import java.util.List;

/**
 * Created by jose on 10/02/15.
 */
public class HomingEnemy extends Enemy {

    private final float speed;
    private final TimeCounter refreshCounter;

    public HomingEnemy(int hp, int damage, Vector2 position, float size, float speed, float refreshRate, int points) {
        super(hp, damage, position, new Vector2(), size, new AnimationData(2, 0.5f, 0, 0, refreshRate), points);
        this.speed = speed;
        this.refreshCounter = new TimeCounter(refreshRate);
    }

    public HomingEnemy(int hp, int damage, Vector2 position, float size, float speed, float[] color, float refreshRate, int points){
        this(hp, damage, position, size, speed, refreshRate, points);
        this.color = color;
    }

    @Override
    public void update(GameLogic gameInstance, float timeDifference) {
        if(refreshCounter.completed()) {
            Vector2 newDirection = new Vector2(gameInstance.mainCharacter.position)
                    .sub(position).nor().mul(speed);
            this.direction = newDirection;
            refreshCounter.reset();
        }

        refreshCounter.accum(timeDifference);
        position.add(direction);
    }
}
