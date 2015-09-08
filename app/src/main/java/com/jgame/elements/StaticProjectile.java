package com.jgame.elements;

import com.jgame.game.GameLogic;
import com.jgame.util.TimeCounter;
import com.jgame.util.Vector2;

import java.util.List;

/**
 * Created by jose on 29/01/15.
 */
public class StaticProjectile extends Projectile {
    private final TimeCounter timeToLive;
    private int hp;

    public StaticProjectile(Vector2 position, float size, float timeAlive, int hp){
        this.size = size;
        this.position = position;
        timeToLive = new TimeCounter(timeAlive);
        this.hp = hp;
    }

    private void alterEnemy(Enemy e) {
        e.hp--;
    }

    @Override
    public void update(GameLogic gameInstance, float timeDifference) {
        timeToLive.accum(timeDifference);
    }

    @Override
    public boolean vivo(){
        return !timeToLive.completed() && hp > 0;
    }


    @Override
    public void detectCollision(List<Enemy> e) {
        for(int i = 0; i < e.size(); i++)
            if(position.dist(e.get(i).position) < e.get(i).size + size) {
                alterEnemy(e.get(i));
                e.get(i).hit();
                hp--;
            }
    }

    public Vector2 getPosition(){
        return new Vector2();
    }

    public float getPctAlive(){
        return 0;
    }
}
