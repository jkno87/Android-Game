package com.jgame.elements;

import com.jgame.game.GameData;
import com.jgame.util.CollisionObject;
import com.jgame.util.Decoration;
import com.jgame.util.FrameCounter;
import com.jgame.util.Square;
import com.jgame.util.TextureDrawer;
import com.jgame.util.Vector2;

import java.util.ArrayDeque;
import java.util.Random;

/**
 * Created by Jose on 13/02/2018.
 */

public class EatingEnemy extends GameCharacter {

    private enum State {
        IDLE, EATING, ATTACKING
    }

    private final byte ID_TOOL = 0;
    private final byte ID_ENEMY = 1;
    private final byte ID_PROJECTILE = 2;
    private final static float IDLE_SIZE = 75;
    private final static TextureDrawer.TextureData IDLE_SPRITE = new TextureDrawer.TextureData(0.4375f, 0, 0.46875f, 0.09375f);
    private final static Vector2 INITIAL_POSITION = new Vector2(425, 0);
    private final int PROJECTILE_INITIAL_HP = 500;
    private final Vector2 PROJECTILE_SPEED = new Vector2(-0.65f,0);
    private final Vector2 ENEMY_PUSHBACK = new Vector2(-5,0);
    private final Vector2 artifactPosition = new Vector2();
    private final Vector2 hitboxPosition = new Vector2();
    private final Vector2 projectilePosition = new Vector2();
    private final TextureDrawer.ColorData EATING_COLOR = new TextureDrawer.ColorData(1,0,1,1);
    private final TextureDrawer.ColorData ATTACKING_COLOR = new TextureDrawer.ColorData(1,1,0,1);
    private final CollisionObject.IdCollisionObject coArtifact = new CollisionObject.IdCollisionObject(new Square(artifactPosition, 50, 50),
            CollisionObject.TYPE_HITTABLE, ID_TOOL);
    private final CollisionObject.IdCollisionObject coCharacter = new CollisionObject.IdCollisionObject(new Square(hitboxPosition, 50, 50),
            CollisionObject.TYPE_HITTABLE, ID_ENEMY);
    private final CollisionObject.IdCollisionObject coProjectile = new CollisionObject.IdCollisionObject(new Square(projectilePosition, 50, 50),
            CollisionObject.TYPE_MIXED, ID_PROJECTILE);
    private int projectileHp;
    private Random nextMove;
    private State currentState;
    private FrameCounter idleTime;
    private FrameCounter attackStartup;
    private boolean activeProjectile;

    public EatingEnemy() {
        super(new Square(new Vector2(), IDLE_SIZE, IDLE_SIZE));
        idleSizeX = IDLE_SIZE;
        baseX.x = -1;
        collisionObjects = new CollisionObject[]{coArtifact, coCharacter, coProjectile};
        idleTime = new FrameCounter(75);
        attackStartup = new FrameCounter(25);
        nextMove = new Random();
    }

    /**
     * Actualiza el color del personaje dependiendo del estado en el que se encuentre
     */
    private void updateColor(){
        if(currentState == State.EATING) {
            color.r = EATING_COLOR.r;
            color.g = EATING_COLOR.g;
            color.b = EATING_COLOR.b;
        } else if (currentState == State.ATTACKING){
            color.r = ATTACKING_COLOR.r;
            color.g = ATTACKING_COLOR.g;
            color.b = ATTACKING_COLOR.b;
        } else {
            color.r = 1;
            color.g = 1;
            color.b = 1;
        }

    }

    @Override
    public void update(GameCharacter foe, ArrayDeque<Decoration> decorationData) {
        if(activeProjectile){
            projectilePosition.add(PROJECTILE_SPEED);
            projectileHp--;

            if(projectileHp <= 0 || detectCollision(foe, collisionObjects) == GameData.Event.HIT) {
                activeProjectile = false;
                projectilePosition.set(0,0);
            }

            if(coProjectile.bounds.collides(coArtifact.bounds)) {
                artifactPosition.add(-3, 0);
                activeProjectile = false;
                projectilePosition.set(0,0);
            }

        } else {

            if (currentState == State.IDLE) {
                idleTime.updateFrame();
                if (idleTime.completed()) {
                    currentState = nextMove.nextInt(2) == 0 ? State.EATING : State.ATTACKING;
                    attackStartup.reset();
                    updateColor();
                }
            } else {
                attackStartup.updateFrame();
                if (!attackStartup.completed())
                    return;
                if (currentState == State.EATING) {
                    currentState = State.IDLE;
                }

                if (currentState == State.ATTACKING) {
                    projectilePosition.set(position);
                    projectilePosition.add(-IDLE_SIZE, 0);
                    activeProjectile = true;
                    projectileHp = PROJECTILE_INITIAL_HP;
                    currentState = State.IDLE;
                }

                updateColor();
                idleTime.reset();
            }
        }

    }

    @Override
    public boolean completedTransition() {
        return false;
    }

    @Override
    public void reset(Vector2 positionOffset) {
        moveTo(positionOffset, INITIAL_POSITION);
        hitboxPosition.set(position);
        hitboxPosition.add(-IDLE_SIZE, 0);
        artifactPosition.set(position);
        artifactPosition.add(-300, 0);
        idleTime.reset();
        attackStartup.reset();
        currentState = State.IDLE;
    }

    @Override
    public boolean hittable() {
        return true;
    }

    @Override
    public boolean alive() {
        return true;
    }

    @Override
    public TextureDrawer.TextureData getCurrentTexture() {
        return IDLE_SPRITE;
    }

    @Override
    public void hit(CollisionObject target) {
        if(coArtifact.equals(target)){
            artifactPosition.add(5,0);
        } else if(coCharacter.equals(target)){
            color.b = 1;
        } else if(coProjectile.equals(target)){
            activeProjectile = false;
            projectilePosition.set(0,0);
        }
    }
}
