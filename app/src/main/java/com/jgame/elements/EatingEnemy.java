package com.jgame.elements;

import com.jgame.game.GameData;
import com.jgame.util.CollisionObject;
import com.jgame.util.Decoration;
import com.jgame.util.FrameCounter;
import com.jgame.util.Square;
import com.jgame.util.Drawer;
import com.jgame.util.Vector2;
import java.util.ArrayDeque;

/**
 * Created by Jose on 13/02/2018.
 */

public class EatingEnemy extends GameCharacter {

    private enum State {
        IDLE, ATTACKING, DEAD
    }

    private final byte ID_TOOL = 0;
    private final byte ID_ENEMY = 1;
    private final byte ID_PROJECTILE = 2;
    private final static float IDLE_SIZE = 75;
    private final static Drawer.TextureData IDLE_SPRITE = new Drawer.TextureData(0.4375f, 0, 0.46875f, 0.09375f);
    private final static Drawer.TextureData FIREBALL_SPRITE = Drawer.generarTextureData(12,0,14,2,32);
    private final static Drawer.TextureData WALL_SPRITE = Drawer.generarTextureData(12,2,14,4,32);
    private final static Vector2 INITIAL_POSITION = new Vector2(425, 0);
    private final int PROJECTILE_INITIAL_HP = 500;
    private final float ARTIFACT_SPEED_MAGNITUDE = 8;
    private final Vector2 PROJECTILE_SPEED = new Vector2(-0.65f,0);
    private final Vector2 ENEMY_PUSHBACK = new Vector2(-5,0);
    private final Vector2 artifactPosition = new Vector2();
    private final Vector2 hitboxPosition = new Vector2();
    private final Vector2 projectilePosition = new Vector2();
    private final Vector2 artifactForceMagnitude = new Vector2();
    private final Drawer.ColorData EATING_COLOR = new Drawer.ColorData(1,0,1,1);
    private final Drawer.ColorData ATTACKING_COLOR = new Drawer.ColorData(1,1,0,1);
    private final CollisionObject.IdCollisionObject coArtifact = new CollisionObject.IdCollisionObject(new Square(artifactPosition, 65, 50),
            CollisionObject.TYPE_HITTABLE, ID_TOOL);
    private final CollisionObject.IdCollisionObject coCharacter = new CollisionObject.IdCollisionObject(new Square(hitboxPosition, 50, 50),
            CollisionObject.TYPE_HITTABLE, ID_ENEMY);
    private final CollisionObject.IdCollisionObject coProjectile = new CollisionObject.IdCollisionObject(new Square(projectilePosition, 50, 50),
            CollisionObject.TYPE_MIXED, ID_PROJECTILE);
    private int projectileHp;
    private State currentState;
    private FrameCounter idleTime;
    private FrameCounter attackStartup;
    private boolean activeProjectile;
    private final Decoration fireballDecoration;
    private final Decoration wallDecoration;
    private boolean addedDecorations;

    public EatingEnemy() {
        super(new Square(new Vector2(), IDLE_SIZE, IDLE_SIZE));
        idleSizeX = IDLE_SIZE;
        baseX.x = -1;
        collisionObjects = new CollisionObject[]{coArtifact, coCharacter, coProjectile};
        idleTime = new FrameCounter(75);
        attackStartup = new FrameCounter(25);
        fireballDecoration = new Decoration.BoundedDecoration(coProjectile.bounds, projectilePosition, FIREBALL_SPRITE);
        wallDecoration = new Decoration.BoundedDecoration(coArtifact.bounds, artifactPosition, WALL_SPRITE);
    }

    /**
     * Actualiza el color del personaje dependiendo del estado en el que se encuentre
     */
    private void updateColor(){
        if (currentState == State.ATTACKING){
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
        if(!addedDecorations){
            addedDecorations = true;
            decorationData.add(fireballDecoration);
            decorationData.add(wallDecoration);
        }

        if(!completedTransition()) {
            artifactPosition.set(position);
            artifactPosition.add(-300, 0);
            hitboxPosition.set(position);
            hitboxPosition.add(-IDLE_SIZE, 0);
            return;
        }

        if(artifactForceMagnitude.x != 0){
            artifactForceMagnitude.x *= 0.85;
            if(artifactForceMagnitude.x < 0.05f && artifactForceMagnitude.x > -0.05f)
                artifactForceMagnitude.x = 0;
            artifactPosition.add(artifactForceMagnitude);
        }

        if(coArtifact.bounds.collides(coCharacter.bounds)){
            currentState = State.DEAD;
            return;
        }

        if(activeProjectile){
            projectilePosition.add(PROJECTILE_SPEED);
            projectileHp--;

            if(projectileHp <= 0 || detectCollision(foe, collisionObjects) == GameData.Event.HIT) {
                activeProjectile = false;
                projectilePosition.set(0,0);
            }

            if(coProjectile.bounds.collides(coArtifact.bounds)) {
                artifactForceMagnitude.add(-3, 0);
                activeProjectile = false;
                projectilePosition.set(0,0);
            }

        } else {

            if (currentState == State.IDLE) {
                idleTime.updateFrame();
                if (idleTime.completed()) {
                    currentState = State.ATTACKING;
                    attackStartup.reset();
                    updateColor();
                }
            } else {
                attackStartup.updateFrame();
                if (!attackStartup.completed())
                    return;

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
        return position.x <= INITIAL_POSITION.x;
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
        updateColor();
    }

    @Override
    public boolean hittable() {
        return true;
    }

    @Override
    public boolean alive() {
        return currentState != State.DEAD;
    }

    @Override
    public Drawer.TextureData getCurrentTexture() {
        return IDLE_SPRITE;
    }

    @Override
    public void hit(CollisionObject target) {
        if(artifactForceMagnitude.x <= 0 && coArtifact.equals(target)){
            artifactForceMagnitude.set(ARTIFACT_SPEED_MAGNITUDE, 0);
        } else if(coCharacter.equals(target)){
            color.b = 1;
        } else if(coProjectile.equals(target)){
            activeProjectile = false;
            projectilePosition.set(0,0);
        }
    }
}
