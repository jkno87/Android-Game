package com.jgame.elements;

import com.jgame.util.Vector2;

/**
 * Created by jose on 27/01/15.
 */
public class SimpleProjectileSpeeder extends SimpleProjectile {

    public SimpleProjectileSpeeder(Vector2 position, Vector2 direction, float speed, float size) {
        super(position, direction, speed, size);
    }


    public void alterEnemy(Enemy e){
        vivo = false;
        e.direction = new Vector2(e.direction).mul(2f);
    }
}
