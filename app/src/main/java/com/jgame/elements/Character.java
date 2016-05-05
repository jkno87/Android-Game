package com.jgame.elements;

import com.jgame.util.TimeCounter;
import com.jgame.util.Vector2;
import java.util.List;

/**
 * Objeto que representa los enemigos que apareceran en el juego
 * Created by jose on 2/05/16.
 */
public class Character extends GameObject {

    enum State {
        IDLE, ATTACKING
    }

    public final CollisionObject[] idleCollisionBoxes;
    private final Vector2 positionOffset;
    public AttackData activeAttack;
    public final AttackData[] attacks;
    public TimeCounter idleTimer;
    public State currentState;

    public Character(float sizeX, float sizeY, Vector2 position, int id, int moves) {
        super(position, id);
        positionOffset = new Vector2(-sizeX/2,0);
        idleCollisionBoxes = new CollisionObject[]{
                new CollisionObject(new Vector2(positionOffset), id, sizeX, sizeY, this, CollisionObject.TYPE_HITTABLE)
        };
        idleTimer = new TimeCounter(1.5f);
        currentState = State.IDLE;
        attacks = new AttackData[moves];
    }

    /**
     * Este constructor solo existe para representar los parametros que deben llevar los enemigos. Esta solia ser la clase enemigo y
     * solo se utiliza para poder hacer unos cuantos tests. Esto es temporal.
     * @param sizeX
     * @param sizeY
     * @param position
     * @param id
     * @param mainCharacter
     * @param moves
     */
    public Character(float sizeX, float sizeY, Vector2 position, int id, MainCharacter mainCharacter, int moves){
        this(sizeX, sizeY, position, id, moves);
    }

    public synchronized CollisionObject[] getActiveCollisionBoxes(){
        if(currentState == State.ATTACKING){
            if(activeAttack.currentState == AttackData.CollisionState.STARTUP)
                return activeAttack.startup;
            else if(activeAttack.currentState == AttackData.CollisionState.ACTIVE)
                return activeAttack.active;
            else if(activeAttack.currentState == AttackData.CollisionState.RECOVERY)
                return activeAttack.recovery;
        }

        return idleCollisionBoxes;
    }

    public synchronized void changeDirection(){
        baseX.set(baseX.x * -1, 0);
    }

    @Override
    public void update(List<? extends GameObject> objects, float timeDifference){
        if(currentState == State.IDLE) {
            idleTimer.accum(timeDifference);
            if (idleTimer.completed()) {
                currentState = State.ATTACKING;
                activeAttack = attacks[0];
                activeAttack.reset();
            }
        }

        if(currentState == State.ATTACKING) {
            activeAttack.update(timeDifference);
            if(activeAttack.completed()){
                currentState = State.IDLE;
                idleTimer.reset();
            }
        }

        for(GameObject o : objects){
            if(o instanceof Character && o.id != id){
                for(CollisionObject co : getActiveCollisionBoxes())
                    co.checkCollision((Character)o);
            }
        }
    }
}
