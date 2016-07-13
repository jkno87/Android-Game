package com.jgame.elements;

import com.jgame.game.GameActivity;
import com.jgame.game.GameFlow;
import com.jgame.util.Square;
import com.jgame.util.TextureDrawer.TextureData;
import com.jgame.util.TimeCounter;
import com.jgame.util.Vector2;
import com.jgame.game.GameActivity.WorldData;

import java.util.Random;

/**
 * Objeto que representa los enemigos que apareceran en el juego
 * Created by jose on 2/05/16.
 */
public abstract class GameCharacter extends GameObject {

    abstract class EnemyAction {
        public abstract void act();
    }

    class EnemyParameters {
        float distanceFromCharacter;
        /*float startInterval;
        float activeInterval;
        float recoveryInterval;
        TimeCounter teleportInterval;
        TimeCounter idleTimer;*/
    }

    private static final int LEFT_TELEPORT = -1;
    private static final int RIGHT_TELEPORT = 1;
    private static final Random RANDOM_POSITION = new Random();
    public final CollisionObject[] idleCollisionBoxes;
    public AttackData activeAttack;
    public final Square spriteContainer;
    public float idleSizeX;

    public GameCharacter(float sizeX, float sizeY, float idleSizeX, float idleSizeY, Vector2 position, int id) {
        super(position, id);
        this.idleSizeX = idleSizeX;
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
        moveX(baseX.x * -1 * idleSizeX);
    }

    /**
     * Se agrega la funcionalidad de actualizar la posicion del spriteContainer
     */
    @Override
    public void updatePosition(){
        super.updatePosition();
        spriteContainer.position.set(position);
        idleCollisionBoxes[0].updatePosition();
    }

    /**
     * Sirve para checar la posicion en la que se encuentra el objeto foe y determina si es necesario realizar un cambio de direccion.
     * @param foe
     */
    public void adjustToFoePosition(GameCharacter foe){
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
     * Reinicia la posicion del objeto tomando en cuenta la posicion de mainCharacter
     */
    public void setPosition(GameCharacter other, EnemyParameters currentParameters){
        float characterMid = other.position.x + other.idleSizeX * other.baseX.x;
        int modifier = 0;

        if(characterMid + currentParameters.distanceFromCharacter + idleSizeX> GameActivity.MAX_X)
            modifier = LEFT_TELEPORT;
        else if (characterMid - currentParameters.distanceFromCharacter - idleSizeX < GameActivity.MIN_X)
            modifier = RIGHT_TELEPORT;
        else
            modifier = 1 - 2*RANDOM_POSITION.nextInt(2);

        baseX.x = other.baseX.x * -1;
        moveTo(modifier != other.baseX.x ? other.position.x + modifier * (currentParameters.distanceFromCharacter) :
                other.position.x + modifier * (currentParameters.distanceFromCharacter + idleSizeX + other.idleSizeX), position.y);
        adjustToFoePosition(other);

    }

    public abstract void reset(float x, float y);
    public abstract boolean hittable();
    public abstract boolean alive();
    public abstract boolean attacking();
    public abstract void update(GameCharacter foe, GameFlow.UpdateInterval interval, WorldData worldData);
    public abstract TextureData getCurrentTexture();
    public abstract void hit();
}
