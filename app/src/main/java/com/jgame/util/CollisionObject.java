package com.jgame.util;

import com.jgame.elements.GameCharacter;

/**
 * Created by jose on 9/01/18.
 */

public class CollisionObject {

    public static class IdCollisionObject extends CollisionObject {
        private final byte id;
        public IdCollisionObject(Square bounds, int type, byte id){
            super(bounds, type);
            this.id = id;
        }

        @Override
        public boolean equals(Object o){
            if(o instanceof IdCollisionObject){
                IdCollisionObject other = (IdCollisionObject)o;
                return other.id == id;
            }
            return false;
        }

        @Override
        public int hashCode(){
            return id;
        }
    }

    public static final int TYPE_ATTACK = 0;
    public static final int TYPE_HITTABLE = 1;
    public static final int TYPE_SMASHED = 2;
    public static final int TYPE_MIXED = 3;
    public final Square bounds;
    public int type;

    public CollisionObject(Square bounds, int type){
        this.bounds = bounds;
        this.type = type;
    }

    /**
     * Verifica si el CO actual colisiona con others. En caso de que ocurra una colision, regresa el CO que provocó la colision
     * @param others
     * @return
     */
    public CollisionObject checkCollision(CollisionObject[] others){
        //Si el objeto actual es hittable, no puede provocar un hit
        if(type == TYPE_HITTABLE)
            return null;

        for(CollisionObject c : others) {
            //Dos objetos de ataque no provocan colision
            if(c.type == TYPE_ATTACK)
                continue;
            if(bounds.collides(c.bounds))
                return c;
        }

        return null;
    }
}
