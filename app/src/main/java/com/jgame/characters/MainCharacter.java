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

    public enum CharacterState {
        NORMAL, STUNNED, SPECIAL
    }

    private boolean dragging;
    public CharacterState state;
    public final int stamina;
    private final Attack mainAttack;
    private MovementController movementController;

    public MainCharacter(MovementController movementController, int stamina, Attack mainAttack){
        this.stamina = stamina;
        state = CharacterState.NORMAL;
        this.mainAttack = mainAttack;
        this.movementController = movementController;
    }

    private void recoverCharacter(){
        state = CharacterState.NORMAL;
    }

    public void receiveInputDown(float sourceX, float sourceY){
        if(state == CharacterState.STUNNED)
            return;

        boolean withinCharacterRadius = movementController.containsPoint(sourceX, sourceY);
        dragging = withinCharacterRadius;
        movementController.updateDirection(sourceX, sourceY);

    }

    public void receiveInputDrag(float sourceX, float sourceY){
        movementController.updateDirection(sourceX, sourceY);

        if(dragging){
            movementController.move(sourceX, sourceY);
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
                created = mainAttack.createAttack(sourceX, sourceY, movementController.position);
                //created = new StaticProjectile(new Vector2(sourceX, sourceY),
                //    SPECIAL_PROJECTILE_SIZE, SPECIAL_LIFETIME, SPECIAL_PROJECTILE_LIFE);
                /*created = new SimpleProjectile(new Vector2(position), new Vector2(sourceX, sourceY)
                        .sub(position).nor(), PROYECTILE_SPEED, PROYECTILE_SIZE);*/

            movementController.updateDirection(sourceX, sourceY);
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
            if(movementController.collision(enemies.get(i))){
                Enemy e = enemies.get(i);
                e.hp--;
                movementController.stun(e.position, e.stunInfo);
            }
    }

    @Override
    public void update(GameLogic gameInstance, float timeDifference) {
        movementController.update(gameInstance, timeDifference);

        Projectile p = mainAttack.createTimedAttack(timeDifference, movementController.position);
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

    /**
     * Regresa un vector con la posicion del maincharacter
     * @return Vector2 con la posicion actual del personaje
     */
    public Vector2 getPosition(){
        return new Vector2(movementController.position);
    }

    /**
     * Regresa el angulo del personaje principal
     * @return float con el angulo de direccion
     */
    public float getAngle(){
        return movementController.angle;
    }

    public float getPctAlive(){
        return 0;
    }
}
