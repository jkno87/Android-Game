package com.jgame.util;

/**
 * Created by ej-jose on 11/08/15.
 */
public class Circle extends GeometricElement {

    public final float radius;
    public final Vector2 position;

    public Circle(float x, float y, float radius){
        this.radius = radius;
        this.position = new Vector2(x, y);
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

}
