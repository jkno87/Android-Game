package com.jgame.elements;

import android.util.Log;

import com.jgame.game.GameLogic;
import com.jgame.util.AnimationData;
import com.jgame.util.TimeCounter;
import com.jgame.util.Vector2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jose on 19/02/15.
 */
public class BossEnemy extends Enemy {

    enum BossState {
        COLORED, NORMAL
    }

    private final float[] PULSE_STATE = new float[]{1,0.5f,0.5f,1};
    private final float PULSE_INTERVAL = 3;
    private final float WARNING_DECORATION_TIME = 1.5f;
    private final float WARNING_SIZE = 60f;
    private static final StunInfo FREEZE_STUN = new StunInfo(2f, 0);
    public static final float SUB_ENEMY_SIZE = 25f;
    public static final float SECONDARY_ATTACK_SIZE = 35f;
    public static final float SECONDARY_ATTACK_INTERVAL = 2;
    public static final float SECONDARY_ATTACK_DURATION = 0.4f;


    private TimeCounter projectiles;
    private TimeCounter pulse;
    private TimeCounter pulseDuration;
    private TimeCounter secondaryAttack;
    private BossState state;
    private final float maxHp;
    private int lastHP;
    private boolean warningSent;
    private Vector2 freezeProjectileLocation;

    public BossEnemy(int hp, int damage, Vector2 position, Vector2 direction, float size, AnimationData textData, int points) {
        super(hp, damage, position, direction, size, textData, points);
        projectiles = new TimeCounter(1.5f);
        pulse = new TimeCounter(PULSE_INTERVAL);
        pulseDuration = new TimeCounter(0.5f);
        state = BossState.NORMAL;
        maxHp = hp;
        lastHP = hp;
        secondaryAttack = new TimeCounter(SECONDARY_ATTACK_INTERVAL);
        freezeProjectileLocation = new Vector2();
    }

    @Override
    public void update(GameLogic gameInstance, float timeDifference) {
        super.update(gameInstance, timeDifference);

        if(lastHP > hp) {
            pulse.changeInterval(PULSE_INTERVAL * (hp / maxHp));
            lastHP = hp;
            direction.x *= 1.05f;
        }

        if(!gameInstance.withinBounds(position.x, position.y))
            direction.x *= -1;

        textData.updateFrame(timeDifference);
        position.add(direction);
        projectiles.accum(timeDifference);


        if(projectiles.completed()) {
            projectiles.reset();
            gameInstance.addEnemy(new SimpleEnemy(1, 1, new Vector2(position), SUB_ENEMY_SIZE, new Vector2(0, -2), 0));
        }

        if(hp < 10) {
            secondaryAttack.accum(timeDifference);

            if(secondaryAttack.completed()) {
                if(warningSent){
                    Enemy e = new TimedEnemy(1, 1, new Vector2(freezeProjectileLocation), SECONDARY_ATTACK_SIZE,
                            SECONDARY_ATTACK_DURATION, 0);
                    e.stunInfo = FREEZE_STUN;
                    e.color = new float[]{0,1,0,1};
                    gameInstance.addEnemy(e);
                    warningSent = false;
                    secondaryAttack.changeInterval(SECONDARY_ATTACK_INTERVAL);
                } else {
                    gameInstance.addDecoration(new Decoration(gameInstance.mainCharacter.getPosition(), WARNING_SIZE, WARNING_DECORATION_TIME));
                    freezeProjectileLocation.set(gameInstance.mainCharacter.getPosition());
                    warningSent = true;
                    secondaryAttack.changeInterval(WARNING_DECORATION_TIME);
                }

            secondaryAttack.reset();
            }
        }


    }

    @Override
    public boolean vivo() {
        return hp > 0;
    }
}
