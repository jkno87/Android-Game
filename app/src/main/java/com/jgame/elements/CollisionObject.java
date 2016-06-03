package com.jgame.elements;

import com.jgame.util.Square;
import com.jgame.util.Vector2;

/**
 * Objeto que interactua con otros CollisionObject para desatar eventos en el juego.
 * Created by jose on 21/04/16.
 */
public class CollisionObject extends GameObject {

    public static final int TYPE_ATTACK = 0;
    public static final int TYPE_HITTABLE = 1;
    public static final int TYPE_SMASHED = 2;
    public final Square bounds;
    public int type;

    public CollisionObject(Vector2 relativePosition, int id, float length, float height, GameObject parent, int type) {
        super(relativePosition, id);
        bounds = new Square(position, length, height, 0);
        this.type = type;
        setParent(parent);
    }

    @Override
    public void updatePosition(){
        super.updatePosition();
        if(parent.baseX.x < 0)
            position.add(parent.baseX.x * bounds.lenX, 0);
    }

    /**
     * Checa si el otro enemigo colisiona con el objeto en caso de que sea del tipo TYPE_ATTACK
     * @param other otro objeto Character que puede colisionar
     * @return boolean que determina si existio una colision con el otro personaje.
     */
    public boolean checkCollision(Character other){
        if(type != TYPE_ATTACK)
            return false;

        for(CollisionObject c : other.getActiveCollisionBoxes()){
            if(c.type != TYPE_HITTABLE)
                continue;
            if(bounds.collides(c.bounds))
                return true;
        }

        return false;
    }
}
