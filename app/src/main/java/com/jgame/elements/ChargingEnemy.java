package com.jgame.elements;

import android.util.Log;

import com.jgame.util.Decoration;
import com.jgame.util.Square;
import com.jgame.util.Drawer;
import com.jgame.util.Drawer.TextureData;
import com.jgame.util.CollisionObject;
import com.jgame.game.GameData.Event;
import com.jgame.util.Vector2;

import java.util.ArrayDeque;

/**
 * Enemigo que tiene el objetivo de lanzarse hacia el personaje principal despues de realizar un periodo de carga.
 * Created by jose on 13/07/16.
 */
public class ChargingEnemy extends GameCharacter {

    public static class ProjectileDecoration extends Decoration {

        private ChargingEnemy parent;
        private boolean finished;

        public ProjectileDecoration(ChargingEnemy parent, Square size){
            this.parent = parent;
            this.inverted = true;
            this.size = size;
        }

        @Override
        public void terminate() {
            finished = true;
        }

        @Override
        public boolean drawable() {
            return !finished;
        }

        @Override
        public void update(Vector2 backgroundMoveDelta) {
            size.lenX -= ATTACK_SPEED.x;
            if(!parent.alive() || size.position.x - size.lenX < 0)
                terminate();
        }

        @Override
        public boolean completed() {
            return finished;
        }

        @Override
        public TextureData getSprite() {
            return PROJECTILE_TEXTURE;
        }
    }

    enum State {
        IDLE, ATTACKING, CHARGING, DEAD, DYING
    }

    private final float PROJECTILE_OFFSET = 50;
    private final static Vector2 INITIAL_POSITION = new Vector2(425,0);
    public final static TextureData PROJECTILE_TEXTURE = Drawer.generarTextureData(12,0,14,2,32);
    public final static TextureData IDLE_TEXTURE = Drawer.generarTextureData(20,0,22,2,32);
    public final static TextureData ATTACK_TEXTURE = Drawer.generarTextureData(26,0,28,2,32);
    public final static TextureData DEFEATED_TEXTURE = Drawer.generarTextureData(22,2,24,4,32);
    public final static TextureData[] STARTUP_TEXTURE = new TextureData[]{
            IDLE_TEXTURE, Drawer.generarTextureData(22,0,24,2,32),
            Drawer.generarTextureData(24,0,26,2,32), ATTACK_TEXTURE
    };
    private final static Vector2 ATTACK_SPEED = new Vector2(-5f, 0);
    private final static int IDLE_FRAMES = 120;
    private final static int CHARGE_FRAMES = 20;
    private final Vector2 PROJECTILE_INITIAL_POSITION = new Vector2(-idleSizeX, 50);
    private State currentState;
    private int idleFrame;
    private Vector2 projectilePosition;
    private final AnimationData attackStartup;

    public ChargingEnemy(float yPosition){
        super(new Square(new Vector2(0, yPosition), 85,85));
        this.baseX.x = -1;
        currentState = State.IDLE;
        idleFrame = IDLE_FRAMES;
        projectilePosition = new Vector2();
        collisionObjects = new CollisionObject[]{new CollisionObject(new Square(projectilePosition, 100,60), CollisionObject.TYPE_ATTACK),
                new CollisionObject(new Square(projectilePosition, 100, 60), CollisionObject.TYPE_HITTABLE)};
        attackStartup = new AnimationData(CHARGE_FRAMES, false, STARTUP_TEXTURE);
    }

    private void resetAttack(){
        projectilePosition.set(position).add(PROJECTILE_INITIAL_POSITION);
    }

    @Override
    public void reset(Vector2 positionOffset) {
        idleFrame = IDLE_FRAMES;
        currentState = State.IDLE;
        moveTo(positionOffset, INITIAL_POSITION);
        color.b = 0;
    }

    @Override
    public boolean hittable() {
        return currentState == State.ATTACKING;
    }

    @Override
    public boolean alive() {
        return currentState != State.DEAD;
    }

    @Override
    public boolean completedTransition(){
        return position.x <= INITIAL_POSITION.x;
    }

    @Override
    public void update(GameCharacter foe, ArrayDeque<Decoration> decorationData) {
        if(!completedTransition())
            return;

        if(currentState == State.IDLE){
            idleFrame -= 1;
            if(idleFrame <= 0) {
                attackStartup.reset();
                currentState = State.CHARGING;
                color.b = 0.5f;
            }
        } else if(currentState == State.CHARGING){
            attackStartup.updateFrame();
            if(attackStartup.completed()) {
                currentState = State.ATTACKING;
                color.b = 1;
                resetAttack();
                decorationData.add(new ProjectileDecoration(this,
                        new Square(new Vector2(position.x - PROJECTILE_OFFSET, position.y + 85),
                        50, 50)));
            }
        } else if (currentState == State.ATTACKING){
            projectilePosition.add(ATTACK_SPEED);
            //attackObject[0].move(ATTACK_SPEED);
            //attackObject[1].move(ATTACK_SPEED);
            if(Event.HIT == detectCollision(foe, collisionObjects)){
                //activeCollisionBoxes = idleCollisionBoxes;
                projectilePosition.set(PROJECTILE_INITIAL_POSITION);
                idleFrame = IDLE_FRAMES;
                currentState = State.IDLE;
                color.b = 0;
                resetAttack();
            }
        } else if(currentState == State.DYING){
            currentState = State.DEAD;
            color.b = 0;
            decorationData.add(new Decoration.TransitionDecoration(DEFEATED_TEXTURE,
                    new Square(this.spriteContainer), true, color));
        }

        return;
    }

    @Override
    public TextureData getCurrentTexture() {
        if(currentState == State.CHARGING)
            return attackStartup.getCurrentSprite();
        else if(currentState == State.ATTACKING)
            return ATTACK_TEXTURE;
        else
            return IDLE_TEXTURE;
    }

    @Override
    public void hit(CollisionObject o) {
        currentState = State.DYING;
    }
}
