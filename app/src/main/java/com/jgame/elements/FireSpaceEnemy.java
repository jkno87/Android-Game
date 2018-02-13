package com.jgame.elements;

import com.jgame.game.GameActivity;
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
 * Created by jose on 19/01/18.
 */

public class FireSpaceEnemy extends GameCharacter {

    private final static float IDLE_SIZE_X = 37;
    private final static float IDLE_SIZE_Y = 160;
    private final static float SPRITE_SIZE_X = 37;
    private final static float SPRITE_SIZE_Y = 160;
    private final static Vector2 INITIAL_POSITION = new Vector2(375, 0);
    private final static TextureDrawer.TextureData IDLE_SPRITE = new TextureDrawer.TextureData(0.4375f, 0, 0.46875f, 0.09375f);
    private final static TextureDrawer.TextureData FIRE_SPRITE = TextureDrawer.generarTextureData(12,0,14,2,32);
    private final TextureDrawer.ColorData ATTACK_A_COLOR = new TextureDrawer.ColorData(0,0,1,1);
    private final TextureDrawer.ColorData ATTACK_B_COLOR = new TextureDrawer.ColorData(1,0,0,1);
    private boolean targetSet;
    private Vector2 fireballOrigin;
    private Vector2 fireballSpeed;
    private Vector2 attack2Target;
    private byte hp;
    private Decoration.BoundedDecoration attackDecoration;
    private FrameCounter idleFrames;
    private FrameCounter preAttackFrames;
    private FrameCounter attack2Startup;
    private State currentState;
    private Random nextMove;
    private final Vector2 hitboxPosition = new Vector2();
    private final Square hitboxBounds = new Square(hitboxPosition, 35, 80);

    private enum State {
        IDLE, STARTING_ATTACK_A, STARTING_ATTACK_B, ATTACK_A, ATTACK_B, DEAD
    }

    public FireSpaceEnemy(int id) {
        super(SPRITE_SIZE_X, SPRITE_SIZE_Y, IDLE_SIZE_X, IDLE_SIZE_Y, new Vector2(0, GameActivity.ELEMENTS_HEIGHT), id);
        baseX.x = -1;
        fireballSpeed = new Vector2(-2,0);
        fireballOrigin = new Vector2(-65,0);
        idleFrames = new FrameCounter(30);
        preAttackFrames = new FrameCounter(15);
        attack2Startup = new FrameCounter(80);
        attack2Target = new Vector2();
        nextMove = new Random();
        attackDecoration = new Decoration.BoundedDecoration(hitboxBounds, hitboxPosition, FIRE_SPRITE);
        collisionObjects = new CollisionObject[]{new CollisionObject(hitboxBounds, CollisionObject.TYPE_HITTABLE),
                new CollisionObject(hitboxBounds, CollisionObject.TYPE_ATTACK)};
    }

    private void setColor(TextureDrawer.ColorData nColor){
        color.r = nColor.r;
        color.g = nColor.g;
        color.b = nColor.b;
    }

    private void resetAttackHitbox(){
        hitboxPosition.set(position).add(fireballOrigin);
    }

    @Override
    public void update(GameCharacter foe, ArrayDeque<Decoration> decorationData) {
        if(!completedTransition())
            return;

        if(currentState == State.IDLE){
            resetAttackHitbox();
            idleFrames.updateFrame();
            if (idleFrames.completed()) {
                currentState = nextMove.nextInt(2) == 0 ? State.STARTING_ATTACK_A : State.STARTING_ATTACK_B;
                preAttackFrames.reset();
                attack2Startup.reset();
            }
        } else if(currentState == State.STARTING_ATTACK_A){
            setColor(ATTACK_A_COLOR);
            preAttackFrames.updateFrame();
            if(preAttackFrames.completed()) {
                currentState = State.ATTACK_A;
                setColor(TextureDrawer.DEFAULT_COLOR);
                decorationData.add(attackDecoration);
                attackDecoration.reset();
            }
        } else if(currentState == State.STARTING_ATTACK_B){
            setColor(ATTACK_B_COLOR);
            attack2Startup.updateFrame();
            if(!targetSet) {
                attack2Target.set(foe.position);
                targetSet = true;
            }
            if(attack2Startup.completed()) {
                currentState = State.ATTACK_B;
                setColor(TextureDrawer.DEFAULT_COLOR);
                decorationData.add(attackDecoration);
            }
        } else if(currentState == State.ATTACK_A){
            hitboxPosition.add(fireballSpeed);
            if(detectCollision(foe, collisionObjects) == GameData.Event.HIT) {
                currentState = State.IDLE;
                idleFrames.reset();
                attackDecoration.terminate();
            }
        } else if(currentState == State.ATTACK_B){
            targetSet = false;
            hitboxPosition.set(attack2Target);
            preAttackFrames.updateFrame();
            attackDecoration.reset();
            if(detectCollision(foe, collisionObjects) == GameData.Event.HIT) {
                currentState = State.IDLE;
                idleFrames.reset();
                attackDecoration.terminate();
            }

            if(preAttackFrames.completed()){
                currentState = State.IDLE;
                idleFrames.reset();
                attackDecoration.terminate();
            }

        }


    }

    @Override
    public boolean completedTransition() {
        return position.x <= INITIAL_POSITION.x;
    }

    @Override
    public void reset(Vector2 positionOffset) {
        currentState = State.IDLE;
        setColor(TextureDrawer.DEFAULT_COLOR);
        moveTo(positionOffset, INITIAL_POSITION);
        hitboxPosition.set(position);
        idleFrames.reset();
        preAttackFrames.reset();
        attack2Startup.reset();
        targetSet = false;
        hp = 2;
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
    public TextureDrawer.TextureData getCurrentTexture() {
        return IDLE_SPRITE;
    }

    @Override
    public void hit() {
        if(currentState == State.ATTACK_B)
            hp = 0;
        else
            hp--;

        attackDecoration.terminate();

        if(hp == 0)
            currentState = State.DEAD;
        else {
            currentState = State.IDLE;
            idleFrames.reset();
        }
    }
}
