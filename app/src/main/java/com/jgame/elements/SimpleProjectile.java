package com.jgame.elements;

import java.util.List;

import com.jgame.game.GameLogic;
import com.jgame.util.Vector2;

public class SimpleProjectile extends Projectile {

    public final Vector2 direction;
    public boolean vivo;

    public SimpleProjectile(Vector2 position, Vector2 direction, float speed, float size){
        this.position = position;
        this.direction = direction.mul(speed);
        this.size = size;
        vivo = true;
        enemiesKilled = 0;
    }

    @Override
    public void update(GameLogic gameInstance, float timeDiff){
        position.add(direction);
    }

    @Override
    public void detectCollision(List<Enemy> e){
        for(int i = 0; i < e.size(); i++) {
            if (position.dist(e.get(i).position) < e.get(i).size + size)
                alterEnemy(e.get(i));
        }
    }

    private void alterEnemy(Enemy e) {
        vivo = false;
        e.hp--;
        e.hit();
        if(!e.vivo())
            enemiesKilled += e.getPoints();
    }

    @Override
    public boolean vivo(){
        return vivo;
    }
}