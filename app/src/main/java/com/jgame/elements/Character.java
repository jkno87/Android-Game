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

    public final CollisionObject[] idleCollisionBoxes;
    public AttackData activeAttack;
    public final Square spriteContainer;

    public Character(float sizeX, float sizeY, float idleSizeX, float idleSizeY, Vector2 position, int id) {
        super(position, id);
        spriteContainer = new Square(new Vector2(), sizeX, sizeY, 0);
        idleCollisionBoxes = new CollisionObject[]{
                new CollisionObject(new Vector2(), id, idleSizeX, idleSizeY, this, CollisionObject.TYPE_HITTABLE)
        };
        updatePosition();
    }

    public synchronized CollisionObject[] getActiveCollisionBoxes(){
        if(attacking()){
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

    public abstract boolean alive();
    public abstract boolean attacking();
    public abstract void update(Character foe, GameFlow.UpdateInterval interval, FightingGameFlow.WorldData worldData);
    public abstract TextureData getCurrentTexture();
    public abstract void hit();
}
