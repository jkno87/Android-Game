package com.jgame.util;

/**
 * Created by jose on 2/11/15.
 */
public class Point extends GeometricElement{


    public final Vector2 position;
    //Esto solo se hace para que se vea el punto al dibujarse
    private final float POINT_SIZE = 0.01f;

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
    public void setPosition(Vector2 v){
        this.position.set(v);
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
    public void fillSimpleDrawer(SimpleDrawer d, SimpleDrawer.ColorData cd, Vector2 offset){
        d.addColoredRectangle(position.x - offset.x, position.y - offset.y, POINT_SIZE, POINT_SIZE, cd);
    }
}
