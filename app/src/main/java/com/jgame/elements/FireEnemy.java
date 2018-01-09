package com.jgame.elements;

import com.jgame.util.Decoration;
import com.jgame.util.Square;
import com.jgame.util.TextureDrawer;
import com.jgame.util.Vector2;
import com.jgame.util.TextureDrawer.TextureData;
import com.jgame.util.CollisionObject;
import java.util.ArrayDeque;


/**
 * Created by Jose on 15/12/2017.
 */

public class FireEnemy extends GameCharacter {

    public static class FireDecoration extends Decoration {

        private boolean finished;

        public FireDecoration (Square size, Vector2 position){
            this.size = size;
            this.size.position = position;
        }

        @Override
        public void terminate() {
            finished = false;
        }

        @Override
        public boolean drawable() {
            return !finished;
        }

        @Override
        public void update(Vector2 backgroundMoveDelta) {
        }

        @Override
        public boolean completed() {
            return finished;
        }

        @Override
        public TextureData getSprite() {
            return FIRE_SPRITE;
        }
    }

    private enum EnemyState {
        PREATTACK, ATTACK
    };


    private final static Vector2.RotationMatrix ROTATION_MATRIX = new Vector2.RotationMatrix(2);
    private final static float IDLE_SIZE_X = 37;
    private final static float IDLE_SIZE_Y = 160;
    private final static float SPRITE_SIZE_X = 37;
    private final static float SPRITE_SIZE_Y = 160;
    private final static Vector2 INITIAL_POSITION = new Vector2(350, 0);
    private final static TextureData FIRE_SPRITE = TextureDrawer.generarTextureData(12,0,14,2,32);
    private final static TextureData IDLE_SPRITE = new TextureData(0.4375f, 0, 0.46875f, 0.09375f);
    private EnemyState currentState;
    private final Vector2 fireballOrigin;
    private final Vector2 fireballTransformation;
    private final Vector2 fireballPosition;

    public FireEnemy(float positionY, int id){
        super(SPRITE_SIZE_X, SPRITE_SIZE_Y, IDLE_SIZE_X, IDLE_SIZE_Y, new Vector2(0, positionY), id);
        baseX.x = -1;
        fireballPosition = new Vector2();
        fireballOrigin = new Vector2();
        fireballTransformation = new Vector2(50,0); //Posicion arbitraria
        CollisionObject fireballCollision = new CollisionObject(new Square(fireballPosition, 20, 20), CollisionObject.TYPE_ATTACK);
        CollisionObject enemyCollision = new CollisionObject(new Square(position, 50,50), CollisionObject.TYPE_HITTABLE);
        collisionObjects = new CollisionObject[]{fireballCollision, enemyCollision};
    }

    private void updateFireball(){
        fireballTransformation.rotate(ROTATION_MATRIX);
        fireballPosition.set(fireballOrigin);
        fireballPosition.add(fireballTransformation);
    }

    @Override
    public void reset(Vector2 positionOffset) {
        currentState = EnemyState.PREATTACK;
        moveTo(positionOffset, INITIAL_POSITION);
        fireballOrigin.set(position).add(-20, 50);
    }


    @Override
    public void update(GameCharacter foe, ArrayDeque<Decoration> decorationData) {
        if(currentState == EnemyState.PREATTACK) {
            decorationData.add(new FireDecoration(new Square(20, 0, 20, 20), fireballPosition));
            currentState = EnemyState.ATTACK;
        } else {
            updateFireball();
        }


    }

    @Override
    public boolean completedTransition() {
        return false;
    }

    @Override
    public boolean hittable() {
        return false;
    }

    @Override
    public boolean alive() {
        return true;
    }

    @Override
    public boolean attacking() {
        return false;
    }

    @Override
    public TextureData getCurrentTexture() {
        return IDLE_SPRITE;
    }

    @Override
    public void hit() {

    }
}
