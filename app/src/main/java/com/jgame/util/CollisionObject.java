package com.jgame.util;

import com.jgame.elements.GameCharacter;

/**
 * Created by jose on 9/01/18.
 */

public class CollisionObject {

    public static final int TYPE_ATTACK = 0;
    public static final int TYPE_HITTABLE = 1;
    public static final int TYPE_SMASHED = 2;
    public final Square bounds;
    public int type;

    public CollisionObject(Square bounds, int type){
        this.bounds = bounds;
        this.type = type;
    }

    public boolean checkCollision(CollisionObject[] others){
        //Si la collision box actual es hittable no puede provocar un hit
        if(type == TYPE_HITTABLE)
            return false;
        for(CollisionObject c : others){
            if(c.type != TYPE_HITTABLE)
                continue;
            if(bounds.collides(c.bounds))
                return true;
        }
        return false;
    }
}
