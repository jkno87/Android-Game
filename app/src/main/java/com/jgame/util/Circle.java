package com.jgame.util;

import android.util.Log;

/**
 * Created by ej-jose on 11/08/15.
 */
public class Circle extends GeometricElement {

    public float radius;
    public final Vector2 position;

    public Circle(float x, float y, float radius){
        this.radius = radius;
        this.position = new Vector2(x, y);
    }

    public Circle(Vector2 position, float radius){
        this.position = position;
        this.radius = radius;
    }

    @Override
    public Vector2 getPosition(){
        return position;
    }

    /**
     * Determina si el punto x,y se encuentra dentro del circulo
     * @param x coordenada x
     * @param y coordenada y
     * @return boolean determinando si el punto esta dentro del circulo
     */
    @Override
    public boolean contains(float x, float y){
        return position.dist(x, y) <= radius;
    }

    @Override
    public boolean collides(GeometricElement e) {
        if(e instanceof Circle)
            return containsCircle((Circle) e);
        else
            throw new UnsupportedOperationException();
    }

    @Override
    public void fillSimpleDrawer(SimpleDrawer d, SimpleDrawer.ColorData cd, Vector2 offset){
        d.addColoredRectangle(position.x - offset.x, position.y - offset.y, radius, radius, cd);
    }

    /**
     * Determina si el círculo toca a otro círculo
     * @param c Circle que se pretende comprarar
     * @return boolean que determina si el objeto toca a otro Circle
     */
    public boolean containsCircle(Circle c){
        return position.dist(c.position) <= radius + c.radius;
    }

    /**
     * Determina si el vector apunta dentro del círculo
     * @param position
     * @return boolean
     */
    public boolean contains(Vector2 position){
        return contains(position.x, position.y);
    }

}
