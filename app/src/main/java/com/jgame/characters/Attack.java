package com.jgame.characters;

import com.jgame.elements.GameElement;
import com.jgame.elements.Projectile;
import com.jgame.util.Vector2;

/**
 * Created by jose on 2/06/15.
 */
public abstract class Attack {
    public abstract Projectile createAttack(float sourceX, float sourceY, Vector2 characterPosition);
    public abstract Projectile createSpecialAttack(float sourceX, float sourceY);
}
