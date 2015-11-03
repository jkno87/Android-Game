package com.jgame.util;

/**
 * Created by jose on 2/11/15.
 */
public class Point extends GeometricElement{

    public final Vector2 position;

    public Point(float x, float y){
        position = new Vector2(x, y);
    }

    public Point(Vector2 position){
        this.position = new Vector2(position);
    }

    @Override
    public boolean intersectsX(float x){
        return x == position.x;
    }

    @Override
    public boolean intersectsY(float y){
        return y == position.y;
    }

    @Override
    public boolean contains(float x, float y) {
        return x == position.x && y == position.y;
    }
}
