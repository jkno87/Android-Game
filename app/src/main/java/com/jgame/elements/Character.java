package com.jgame.elements;

import com.jgame.game.FightingGameFlow;
import com.jgame.game.GameFlow;
import com.jgame.util.Square;
import com.jgame.util.TextureDrawer.TextureData;
import com.jgame.util.Vector2;

/**
 * Objeto que representa los enemigos que apareceran en el juego
 * Created by jose on 2/05/16.
 */
public abstract class Character extends GameObject {

    public enum CharacterState {
        IDLE, ATTACKING, DEAD
    }

    public final static TextureData TEXTURE = new TextureData(0,0,0.03125f,0.0625f);
    public final CollisionObject[] idleCollisionBoxes;
    public AttackData activeAttack;
    public CharacterState currentState;
    public final Square spriteContainer;

    public Character(float sizeX, float sizeY, Vector2 position, int id) {
        super(position, id);
        spriteContainer = new Square(new Vector2(), sizeX, sizeY, 0);
        idleCollisionBoxes = new CollisionObject[]{
                new CollisionObject(new Vector2(), id, sizeX, sizeY, this, CollisionObject.TYPE_HITTABLE)
        };
        currentState = CharacterState.IDLE;
        updatePosition();
    }

    /**
     * Este constructor solo existe para representar los parametros que deben llevar los enemigos. Esta solia ser la clase enemigo y
     * solo se utiliza para poder hacer unos cuantos tests. Esto es temporal.
     * @param sizeX
     * @param sizeY
     * @param position
     * @param id
     * @param mainCharacter
     */
    public Character(float sizeX, float sizeY, Vector2 position, int id, MainCharacter mainCharacter){
        this(sizeX, sizeY, position, id);
    }

    public synchronized CollisionObject[] getActiveCollisionBoxes(){
        if(currentState == CharacterState.ATTACKING){
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
        moveX(baseX.x * -1 * spriteContainer.lenX);
        updatePosition();
    }

    /**
     * Se agrega la funcionalidad de actualizar la posicion del spriteContainer
     */
    @Override
    public void updatePosition(){
        super.updatePosition();
        spriteContainer.position.set(position);
    }

    /**
     * Sirve para checar la posicion en la que se encuentra el objeto foe y determina si es necesario realizar un cambio de direccion.
     * @param foe
     */
    public void adjustToFoePosition(Character foe){
        if(foe instanceof EmptyEnemy)
            return;

        if(baseX.x > 0) {
            if (position.x > foe.position.x)
                changeDirection();
        } else {
            if (position.x < foe.position.x)
                changeDirection();
        }
    }


    /**
     * Determina si el Character tiene un estado diferente a CharacterState.DEAD
     * @return boolean
     */
    public boolean alive(){
        return currentState != CharacterState.DEAD;
    }

    public abstract void update(Character foe, GameFlow.UpdateInterval interval, FightingGameFlow.WorldData worldData);
    public abstract TextureData getCurrentTexture();
    public abstract void hit();
}
