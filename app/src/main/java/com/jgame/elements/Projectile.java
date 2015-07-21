package com.jgame.elements;

import com.jgame.util.Vector2;

import java.util.List;

/**
 * Created by jose on 29/01/15.
 */
public abstract class Projectile implements GameElement, EnemyModifier {
    public Vector2 position;
    public float size;
    public int enemiesKilled;
}
