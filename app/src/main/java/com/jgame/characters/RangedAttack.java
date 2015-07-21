package com.jgame.characters;

import com.jgame.elements.Projectile;
import com.jgame.elements.SimpleProjectile;
import com.jgame.elements.StaticProjectile;
import com.jgame.util.Vector2;

/**
 * Created by jose on 2/06/15.
 */
public class RangedAttack extends Attack{

    private final int SPECIAL_PROJECTILE_LIFE = 1;
    private final float SPECIAL_PROJECTILE_SIZE = 15f;
    private final float SPECIAL_LIFETIME = 1f;
    private final float PROJECTILE_SPEED = 5f;

    public RangedAttack(){
    }

    @Override
    public Projectile createAttack(float sourceX, float sourceY, Vector2 characterPosition) {
        return new SimpleProjectile(new Vector2(characterPosition),
                new Vector2(sourceX, sourceY).sub(characterPosition).nor(),
                SPECIAL_PROJECTILE_SIZE, PROJECTILE_SPEED);
    }

    public Projectile createSpecialAttack(float sourceX, float sourceY){
        return new StaticProjectile(new Vector2(sourceX, sourceY),
                SPECIAL_PROJECTILE_SIZE, SPECIAL_LIFETIME, SPECIAL_PROJECTILE_LIFE);
    }

    @Override
    public Projectile createTimedAttack(float timeDifference, Vector2 characterPosition) {
        return null;
    }
}
