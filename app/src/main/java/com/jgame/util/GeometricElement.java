package com.jgame.util;

/**
 * Created by ej-jose on 19/08/15.
 */
public abstract class GeometricElement {
    public abstract Vector2 getPosition();
    public abstract void setPosition(Vector2 v);
    public abstract boolean contains(float x, float y);
    public abstract boolean collides(GeometricElement e);
    public abstract void fillSimpleDrawer(SimpleDrawer d, SimpleDrawer.ColorData cd, Vector2 offset);
}
