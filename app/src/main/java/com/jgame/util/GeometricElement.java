package com.jgame.util;

/**
 * Created by ej-jose on 19/08/15.
 */
public abstract class GeometricElement {
    public abstract Vector2 getPosition();
    public abstract boolean contains(float x, float y);
    public abstract boolean collides(GeometricElement e);
    public abstract void fillDrawRect(float[] drawArray);
}
