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
    public final Square bounds;
    public final int type;

    public CollisionObject(Vector2 relativePosition, int id, float length, float height, GameObject parent, int type) {
        super(relativePosition, id);
        bounds = new Square(position, length, height, 0);
        this.type = type;
        setParent(parent);
    }
}
