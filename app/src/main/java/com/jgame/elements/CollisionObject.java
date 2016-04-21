package com.jgame.elements;

import com.jgame.util.Square;
import com.jgame.util.Vector2;

/**
 * Created by jose on 21/04/16.
 */
public class CollisionObject extends GameObject {

    public final Square bounds;

    public CollisionObject(Vector2 relativePosition, int id, float length, float height) {
        super(relativePosition, id);
        bounds = new Square(position, length, height, 0);
    }
}
