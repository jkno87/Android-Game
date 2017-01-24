package com.jgame.elements;

import com.jgame.game.GameActivity;
import com.jgame.util.Square;
import com.jgame.util.TextureDrawer.TextureData;
import com.jgame.game.GameActivity.Difficulty;
import com.jgame.util.Vector2;
import com.jgame.util.Decoration;

import java.util.ArrayDeque;
import java.util.Random;

/**
 * Objeto que representa los enemigos que apareceran en el juego
 * Created by jose on 2/05/16.
 */
public abstract class GameCharacter extends GameObject {

    abstract class EnemyAction {
        public abstract void act();
    }

    private static final int LEFT_TELEPORT = -1;
    private static final int RIGHT_TELEPORT = 1;
    private static final Random RANDOM_POSITION = new Random();
    public final CollisionObject[] idleCollisionBoxes;
    public AttackData activeAttack;
    public final Square spriteContainer;
    public float idleSizeX;
    public Difficulty currentDifficulty;

    public GameCharacter(float spriteSizeX, float spriteSizeY, float idleSizeX, float idleSizeY, Vector2 position, int id) {
        super(position, id);
        this.idleSizeX = idleSizeX;
        spriteContainer = new Square(new Vector2(), spriteSizeX, spriteSizeY, 0);
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
    public void setPosition(GameCharacter other, float distanceFromCharacter){
        float characterMid = other.position.x + other.idleSizeX * other.baseX.x;
        int modifier = 0;

        if(characterMid + distanceFromCharacter + idleSizeX> GameActivity.MAX_X)
            modifier = LEFT_TELEPORT;
        else if (characterMid - distanceFromCharacter - idleSizeX < GameActivity.MIN_X)
            modifier = RIGHT_TELEPORT;
        else
            modifier = 1 - 2*RANDOM_POSITION.nextInt(2);

        baseX.x = other.baseX.x * -1;
        moveTo(modifier != other.baseX.x ? other.position.x + modifier * distanceFromCharacter:
                other.position.x + modifier * (distanceFromCharacter + idleSizeX + other.idleSizeX), position.y);
        adjustToFoePosition(other);

    }

    /**
     * Realiza la unica accion en comun de todos los objetos GameCharacter, checar que colisione contra el objeto foe.
     * @param foe GameCharacter contra el que se podria provocar una colision.
     * @param decorationData Decoraciones que se encuentran en el juego actual
     */
    public void update(GameCharacter foe, ArrayDeque<Decoration> decorationData){
        if(foe.hittable()) {
            for (CollisionObject co : getActiveCollisionBoxes())
                if (co.checkCollision(foe))
                    foe.hit();
        }
    }

    /**
     * Actualiza la dificultad del personaje
     * @param diff Dificultad actual
     */
    public void setCurrentDifficulty(Difficulty diff){
        this.currentDifficulty = diff;
    }

    public abstract void trip();
    public abstract void reset(float x, float y);
    public abstract boolean hittable();
    public abstract boolean alive();
    public abstract boolean attacking();
    public abstract TextureData getCurrentTexture();
    public abstract void hit();
}
