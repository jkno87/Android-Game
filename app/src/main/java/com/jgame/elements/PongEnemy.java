package com.jgame.elements;

import com.jgame.game.GameData;
import com.jgame.util.CollisionObject;
import com.jgame.util.Decoration;
import com.jgame.util.FrameCounter;
import com.jgame.util.Square;
import com.jgame.util.TextureDrawer;
import com.jgame.util.Vector2;

import java.util.ArrayDeque;

/**
 * Created by Jose on 12/01/2018.
 */

public class PongEnemy extends GameCharacter {

    public class PongDecoration extends Decoration {

        private boolean finished;

        public PongDecoration (Square size, Vector2 position){
            this.size = size;
            this.size.position = position;
        }

        @Override
        public void terminate() {
            finished = true;
        }

        @Override
        public boolean drawable() {
            return !finished && currentState != EnemyState.PREATTACK;
        }

        @Override
        public void update(Vector2 backgroundMoveDelta) {
            if(currentState == EnemyState.DEAD)
                terminate();
        }

        @Override
        public boolean completed() {
            return finished;
        }

        @Override
        public TextureDrawer.TextureData getSprite() {
            return FIRE_SPRITE;
        }
    }

    private enum EnemyState {
        PREATTACK, ATTACK, DEAD, RECEIVING
    };

    private final static float IDLE_SIZE_X = 37;
    private final static float IDLE_SIZE_Y = 160;
    private final static float SPRITE_SIZE_X = 37;
    private final static float SPRITE_SIZE_Y = 160;
    private final static Vector2 INITIAL_POSITION = new Vector2(400, 0);
    private final static TextureDrawer.TextureData ATTACK_SPRITE =  new TextureDrawer.TextureData(0.5f,0.09375f, 0.5625f,0.1875f);
    private final static TextureDrawer.TextureData IDLE_SPRITE = new TextureDrawer.TextureData(0.4375f, 0, 0.46875f, 0.09375f);
    private EnemyState currentState;
    private final static TextureDrawer.TextureData FIRE_SPRITE = TextureDrawer.generarTextureData(12,0,14,2,32);
    private final FrameCounter attackStartup;
    private final Vector2 fireballSpeed;
    private final Vector2 fireballPosition;
    private final PongDecoration decoration;
    private boolean decorationAdded;

    public PongEnemy(float positionY, int id){
        super(SPRITE_SIZE_X, SPRITE_SIZE_Y, IDLE_SIZE_X, IDLE_SIZE_Y, new Vector2(0, positionY), id);
        baseX.x = -1;
        attackStartup = new FrameCounter(15);
        fireballPosition = new Vector2();
        fireballSpeed = new Vector2(-3,0);
        CollisionObject fireballCollision = new CollisionObject(new Square(fireballPosition, 80, 80), CollisionObject.TYPE_ATTACK);
        CollisionObject enemyCollision = new CollisionObject(new Square(fireballPosition,80,80), CollisionObject.TYPE_HITTABLE);
        collisionObjects = new CollisionObject[]{fireballCollision, enemyCollision};
        decoration = new PongDecoration(new Square(20,0, 80,80), fireballPosition);
    }

    @Override
    public void update(GameCharacter foe, ArrayDeque<Decoration> decorationData) {
        if (!completedTransition())
            return;

        if (currentState == EnemyState.ATTACK) {
            fireballPosition.add(fireballSpeed);
            if(detectCollision(foe, collisionObjects) == GameData.Event.HIT) {
                currentState = EnemyState.PREATTACK;
                attackStartup.reset();
            }
        } else if(currentState == EnemyState.RECEIVING) {
            if(fireballPosition.x > position.x - IDLE_SIZE_X){
                fireballSpeed.mul(-1);
                currentState = EnemyState.ATTACK;
            } else
                fireballPosition.add(fireballSpeed);
        } else if(currentState == EnemyState.PREATTACK) {
            attackStartup.updateFrame();
            if(attackStartup.completed()) {
                if (!decorationAdded) {
                    decorationData.add(decoration);
                    decorationAdded = true;
                }
                fireballPosition.set(position).add(-20, 50);
                currentState = EnemyState.ATTACK;
            }
        }
    }

    @Override
    public boolean completedTransition() {
        return position.x <= INITIAL_POSITION.x;
    }

    @Override
    public void reset(Vector2 positionOffset) {
        currentState = EnemyState.PREATTACK;
        moveTo(positionOffset, INITIAL_POSITION);
    }

    @Override
    public boolean hittable() {
        return currentState == EnemyState.ATTACK;
    }

    @Override
    public boolean alive() {
        return currentState != EnemyState.DEAD;
    }

    @Override
    public boolean attacking() {
        return currentState != EnemyState.PREATTACK;
    }

    @Override
    public TextureDrawer.TextureData getCurrentTexture() {
        if(currentState == EnemyState.PREATTACK)
            return IDLE_SPRITE;
        else
            return ATTACK_SPRITE;
    }

    @Override
    public void hit() {
        fireballSpeed.mul(-1);
        currentState = EnemyState.RECEIVING;
    }

}
