package com.jgame.characters;

import com.jgame.elements.Projectile;
import com.jgame.elements.StaticProjectile;
import com.jgame.elements.TimedProjectile;
import com.jgame.game.GameLogic;
import com.jgame.util.TimeCounter;
import com.jgame.util.Vector2;

/**
 * Created by jose on 2/06/15.
 */
public class DistanceAttack extends Attack {

    private final int SPECIAL_PROJECTILE_LIFE = 1;
    private final float SPECIAL_PROJECTILE_SIZE = 15f;
    private final float SPECIAL_LIFETIME = 1f;
    private final float SPECIAL_BURST_TIME = 3.5f;
    private final float PROYECTILE_SPEED = 5f;
    private final float PROYECTILE_SIZE = 2.5f;
    private final float PROJECTILE_LIFE = 0.45f;

    private TimeCounter specialAttackCounter;
    private Vector2 specialDirection;

    public DistanceAttack(){
        specialAttackCounter = new TimeCounter(SPECIAL_BURST_TIME);
    }

    @Override
    public Projectile createAttack(float sourceX, float sourceY, Vector2 position) {
        return new StaticProjectile(new Vector2(sourceX, sourceY),
                SPECIAL_PROJECTILE_SIZE, SPECIAL_LIFETIME, SPECIAL_PROJECTILE_LIFE);
    }

    public Projectile createSpecialAttack(float sourceX, float sourceY){
        specialDirection = new Vector2(sourceX, sourceY);
        return null;
        //return new StaticProjectile(new Vector2(sourceX, sourceY),
        //            SPECIAL_PROJECTILE_SIZE, SPECIAL_LIFETIME, SPECIAL_PROJECTILE_LIFE);
    }

    @Override
    public Projectile createTimedAttack(float timeDifference, Vector2 characterPosition) {
        Projectile p = null;

        if (specialDirection != null) {
            p = new TimedProjectile(new Vector2(characterPosition), new Vector2(specialDirection)
                    .sub(characterPosition).nor(), PROYECTILE_SPEED, PROYECTILE_SIZE, PROJECTILE_LIFE);
            specialAttackCounter.accum(timeDifference);
            if (specialAttackCounter.completed()) {
                specialDirection = null;
                specialAttackCounter.reset();
            }
        }

        return p;
    }
}
