package com.jgame.characters;

import com.jgame.elements.Enemy;
import com.jgame.util.Circle;
import com.jgame.util.Vector2;

/**
 * Esta clase se encarga de describir un objeto que funcionara como la estructura del personaje. Es la clase que se encargara
 * de detectar colisiones. Se hace en una clase independiente para que se pueda tener mayor flexibilidad en las formas del personaje.
 * Created by ej-jose on 31/08/15.
 */
public abstract class MovementController {

    public Vector2 position;
    public float angle;
    public abstract boolean stunned();
    public abstract void stun(Enemy.StunInfo stunInfo);
    public abstract boolean collision(Enemy enemy);
    public abstract boolean containsPoint(Vector2 position);
    public abstract boolean containsPoint(float x, float y);
    public abstract Vector2 difference(Vector2 other);
    public abstract void move(float x, float y);
    public abstract void update(float timeDifference);
}
