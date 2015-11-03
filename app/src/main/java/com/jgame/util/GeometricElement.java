package com.jgame.util;

/**
 * Created by ej-jose on 19/08/15.
 */
public abstract class GeometricElement {
    public abstract boolean contains(float x, float y);
    public abstract boolean intersectsX(float x);
    public abstract boolean intersectsY(float y);
}
