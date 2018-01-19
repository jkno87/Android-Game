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

    public class FireDecoration extends Decoration.BoundedDecoration {


        public FireDecoration (Square size, Vector2 position){
            super(size, position);
        }

        @Override
        public void update(Vector2 backgroundMoveDelta) {
            if(currentState == EnemyState.DEAD)
                terminate();
        }

        @Override
        public TextureData getSprite() {
            return FIRE_SPRITE;
        }
    }

    private enum EnemyState {
        PREATTACK, ATTACK, DEAD
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
    private final Vector2 hitboxPosition;

    public FireEnemy(float positionY, int id){
        super(SPRITE_SIZE_X, SPRITE_SIZE_Y, IDLE_SIZE_X, IDLE_SIZE_Y, new Vector2(0, positionY), id);
        baseX.x = -1;
        hitboxPosition = new Vector2();
        fireballPosition = new Vector2();
        fireballOrigin = new Vector2();
        fireballTransformation = new Vector2(50,0); //Posicion arbitraria
        CollisionObject fireballCollision = new CollisionObject(new Square(fireballPosition, 20, 20), CollisionObject.TYPE_ATTACK);
        CollisionObject enemyCollision = new CollisionObject(new Square(hitboxPosition, 50,50), CollisionObject.TYPE_HITTABLE);
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
    }


    @Override
    public void update(GameCharacter foe, ArrayDeque<Decoration> decorationData) {
        if(!completedTransition())
            return;

        if(currentState == EnemyState.PREATTACK) {
            fireballOrigin.set(position).add(-20, 50);
            hitboxPosition.set(position).add(-IDLE_SIZE_X, 0);
            decorationData.add(new FireDecoration(new Square(20, 0, 20, 20), fireballPosition));
            currentState = EnemyState.ATTACK;
        } else {
            updateFireball();
            detectCollision(foe, collisionObjects);
        }
    }

    @Override
    public boolean completedTransition() {
        return position.x <= INITIAL_POSITION.x;
    }

    @Override
    public boolean hittable() {
        return true;
    }

    @Override
    public boolean alive() {
        return currentState != EnemyState.DEAD;
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
        currentState = EnemyState.DEAD;
    }
}
