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
    public Vector2 getPosition() {
        return position;
    }

    @Override
    public boolean contains(float x, float y) {
        return x == position.x && y == position.y;
    }

    @Override
    public boolean collides(GeometricElement e) {
        return false;
    }

    @Override
    public void fillDrawRect(float[] drawArray) {
        drawArray[0] = position.x;
        drawArray[1] = position.y;
        //Esto solo se hace para que se pueda ver un punto al dibujarse
        drawArray[2] = 0.01f;
        drawArray[3] = 0.01f;
    }
}
