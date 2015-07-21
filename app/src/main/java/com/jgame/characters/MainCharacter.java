package com.jgame.characters;

import android.util.Log;

import com.jgame.elements.Enemy;
import com.jgame.elements.EnemyModifier;
import com.jgame.elements.GameElement;
import com.jgame.elements.Projectile;
import com.jgame.elements.StaticProjectile;
import com.jgame.elements.TimedProjectile;
import com.jgame.game.GameLogic;
import com.jgame.util.TimeCounter;
import com.jgame.util.Vector2;
import com.jgame.elements.Enemy.StunInfo;

import java.util.List;

/**
 * Created by jose on 10/02/15.
 */
public class MainCharacter implements GameElement, EnemyModifier {

    //public static final float[] STUNNED_COLOR = new float[]{0,1,1,0.75f};
    //public static final float[] NORMAL_COLOR = new float[]{1,1,1,1};



    public enum CharacterState {
        NORMAL, STUNNED, SPECIAL
    }

    private final float STUN_RELEASE = 0.25f;
    /*private final float PROYECTILE_SPEED = 5f;
    private final float PROYECTILE_SIZE = 2.5f;
    private final int SPECIAL_PROJECTILES = 3;
    private final int SPECIAL_PROJECTILE_LIFE = 1;
    private final float SPECIAL_PROJECTILE_SIZE = 15f;
    private final float SPECIAL_LIFETIME = 1f;
    private final float STUN_RELEASE = 0.25f;
    private final float PROJECTILE_LIFE = 0.45f;
    private final float SPECIAL_BURST_TIME = 3.5f;*/

    private final Vector2 startPosition;
    public Vector2 position;
    private Vector2 stunDirection;
    //private TimeCounter specialAttackCounter;
    private TimeCounter stunCounter;
    private TimeCounter stunRelease;
    private boolean dragging;
    private float size;
    public float angle;
    public float stun;
    public CharacterState state;
    public int remainingSpecials;
    public final int stamina;
    //public Vector2 specialDirection;
    private final Attack mainAttack;

    public MainCharacter(Vector2 position, float size, int stamina, Attack mainAttack){
        this.startPosition = position;
        this.position = new Vector2(position);
        this.size = size;
        //specialAttackCounter = new TimeCounter(specialCharge);
        //specialAttackCounter = new TimeCounter(SPECIAL_BURST_TIME);
        this.stamina = stamina;
        state = CharacterState.NORMAL;
        this.mainAttack = mainAttack;
        //remainingSpecials = SPECIAL_PROJECTILES;
        stunRelease = new TimeCounter(STUN_RELEASE);
    }

    private void stunCharacter(Vector2 stunPosition, StunInfo stunInfo){
        stunCounter = new TimeCounter(stunInfo.time);
        state = CharacterState.STUNNED;
        stun += 1;
        dragging = false;
        stunDirection = new Vector2(position).sub(stunPosition).nor().mul(stunInfo.force);
    }

    private void recoverCharacter(){
        state = CharacterState.NORMAL;
    }

    public float pctStunned(){
        return stun / stamina;
    }

    public void receiveInputDown(float sourceX, float sourceY){
        if(state == CharacterState.STUNNED)
            return;

        boolean withinCharacterRadius = position.dist(sourceX, sourceY) <= size;
        //charging = !withinCharacterRadius;
        dragging = withinCharacterRadius;
        angle = new Vector2(sourceX, sourceY).sub(position).angle();

    }

    public void receiveInputDrag(float sourceX, float sourceY){

        angle = new Vector2(sourceX, sourceY).sub(position).angle();

        if(dragging){
            position.x = sourceX;
            position.y = sourceY;
        }
    }

    public void changeState(){
        if(state == CharacterState.NORMAL)
            this.state = CharacterState.SPECIAL;
        else if(state == CharacterState.SPECIAL)
            this.state = CharacterState.NORMAL;
    }

    public Projectile receiveInputUp(float sourceX, float sourceY){
        Projectile created = null;

        if(!dragging && state != CharacterState.STUNNED) {
            if(state == CharacterState.SPECIAL)
                created = mainAttack.createSpecialAttack(sourceX, sourceY);
            else
                created = mainAttack.createAttack(sourceX, sourceY, position);
                //created = new StaticProjectile(new Vector2(sourceX, sourceY),
                //    SPECIAL_PROJECTILE_SIZE, SPECIAL_LIFETIME, SPECIAL_PROJECTILE_LIFE);
                /*created = new SimpleProjectile(new Vector2(position), new Vector2(sourceX, sourceY)
                        .sub(position).nor(), PROYECTILE_SPEED, PROYECTILE_SIZE);*/

            angle = new Vector2(sourceX, sourceY).sub(position).angle();
        }

        dragging = false;
        //charging = false;
        //specialAttackCounter.reset();



        return created;
    }

    @Override
    public void detectCollision(List<Enemy> enemies){
        if(state == CharacterState.STUNNED)
            return;

        for(int i = 0; i < enemies.size(); i++)
            if(position.dist(enemies.get(i).position) < enemies.get(i).size + size) {
                Enemy e = enemies.get(i);
                e.hp--;
                stunCharacter(e.position, e.stunInfo);
            }
    }

    @Override
    public void update(GameLogic gameInstance, float timeDifference) {
        if(state == CharacterState.STUNNED){
            stunCounter.accum(timeDifference);
            if(stunCounter.completed())
                recoverCharacter();

            if(!gameInstance.withinXBounds(position.x, size))
                stunDirection.x *= -1;

            if(!gameInstance.withinYBounds(position.y, size))
                stunDirection.y *= -1;

            position.add(stunDirection);

            return;
        }

        if(stun > 0) {

            if(stun >= stamina) {
                position.set(startPosition);
                stun = 0;
                stunRelease.reset();
                return;
            }

            stunRelease.accum(timeDifference);
            if (stunRelease.completed()) {
                stun -= 1;
                stunRelease.reset();
            }
        }

        Projectile p = mainAttack.createTimedAttack(timeDifference, position);
        if(p != null)
            gameInstance.addProjectile(p);

        /*if(specialDirection != null){
            gameInstance.addProjectile(new TimedProjectile(new Vector2(position), new Vector2(specialDirection)
                    .sub(position).nor(), PROYECTILE_SPEED, PROYECTILE_SIZE, PROJECTILE_LIFE));
            specialAttackCounter.accum(timeDifference);
            if(specialAttackCounter.completed()) {
                specialDirection = null;
                state = CharacterState.NORMAL;
                specialAttackCounter.reset();
            }
        }

        /*if(remainingSpecials > 0 && charging) {
            specialAttackCounter.accum(timeDifference);
            NORMAL_COLOR[1] -= specialAttackCounter.pctCharged();
            NORMAL_COLOR[2] -= specialAttackCounter.pctCharged();
        }*/

    }

    @Override
    public boolean vivo() {
        return false;
    }
}
