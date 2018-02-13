package com.jgame.elements;

import com.jgame.util.Square;
import com.jgame.util.TextureDrawer.TextureData;
import com.jgame.game.GameActivity.Difficulty;
import com.jgame.util.Vector2;
import com.jgame.util.Decoration;
import com.jgame.game.GameData.Event;
import com.jgame.util.TextureDrawer.ColorData;
import java.util.ArrayDeque;
import com.jgame.util.CollisionObject;

/**
 * Objeto que representa los enemigos que apareceran en el juego
 * Created by jose on 2/05/16.
 */
public abstract class GameCharacter extends GameObject {

    abstract class EnemyAction {
        public abstract void act();
    }
    
    public final ColorData color = new ColorData(1,1,1,1);
    public CollisionObject[] collisionObjects;
    public final Square spriteContainer;
    public float idleSizeX;
    public Difficulty currentDifficulty;

    public GameCharacter(Square spriteContainer){
        super(spriteContainer.position);
        this.spriteContainer = spriteContainer;
        this.idleSizeX = spriteContainer.lenX;
        updatePosition();
    }

    public GameCharacter(float spriteSizeX, float spriteSizeY, float idleSizeX, float idleSizeY, Vector2 position) {
        super(position);
        this.idleSizeX = idleSizeX;
        spriteContainer = new Square(new Vector2(), spriteSizeX, spriteSizeY);
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
     * Verifica si el objeto actual colisiona con foe
     * @param foe
     * @param collisionObjects
     * @return
     */
    public Event detectCollision(GameCharacter foe, CollisionObject[] collisionObjects){
        if(foe.hittable() && foe.alive()) {
            for (CollisionObject co : collisionObjects)
                if (co.checkCollision(foe.collisionObjects)) {
                    foe.hit(co);
                    return Event.HIT;
                }
        }
        return Event.NONE;
    }

    /**
     * Actualiza la dificultad del personaje
     * @param diff Dificultad actual
     */
    public void setCurrentDifficulty(Difficulty diff){
        this.currentDifficulty = diff;
    }
    public abstract void update(GameCharacter foe, ArrayDeque<Decoration> decorationData);
    public abstract boolean completedTransition();
    public abstract void reset(Vector2 positionOffset);
    public abstract boolean hittable();
    public abstract boolean alive();
    public abstract TextureData getCurrentTexture();
    public abstract void hit(CollisionObject target);
}
