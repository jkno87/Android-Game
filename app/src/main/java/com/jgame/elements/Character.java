package com.jgame.elements;

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
    private final Vector2 positionOffset;
    public AttackData activeAttack;
    public CharacterState currentState;
    public final Square spriteContainer;

    public Character(float sizeX, float sizeY, Vector2 position, int id) {
        super(position, id);
        positionOffset = new Vector2(-sizeX/2,0);
        spriteContainer = new Square(new Vector2(), sizeX, sizeY, 0);
        idleCollisionBoxes = new CollisionObject[]{
                new CollisionObject(new Vector2(positionOffset), id, sizeX, sizeY, this, CollisionObject.TYPE_HITTABLE)
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
    }

    /**
     * Se agrega la funcionalidad de actualizar la posicion del spriteContainer
     */
    @Override
    public void updatePosition(){
        super.updatePosition();
        spriteContainer.position.set(position).add(positionOffset);
    }

    /**
     * Determina si el Character tiene un estado diferente a CharacterState.DEAD
     * @return boolean
     */
    public boolean alive(){
        return currentState != CharacterState.DEAD;
    }

    public abstract void update(GameObject[] objects, GameFlow.UpdateInterval interval);
    public abstract TextureData getCurrentTexture();
    public abstract void hit();
}
