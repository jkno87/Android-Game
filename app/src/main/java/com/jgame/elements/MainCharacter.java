package com.jgame.elements;

import com.jgame.game.ControllerManager;
import com.jgame.game.GameFlow;
import com.jgame.util.AnimationData;
import com.jgame.util.TextureDrawer;
import com.jgame.util.TextureDrawer.TextureData;
import com.jgame.util.TimeCounter;
import com.jgame.util.Vector2;
import com.jgame.elements.AttackData.CollisionState;
import com.jgame.game.GameActivity.WorldData;

/**
 * Objeto que representa al personaje del jugador.
 * Created by jose on 14/01/16.
 */
public class MainCharacter extends GameCharacter {

    public enum CharacterState {
        IDLE, MOVING_FORWARD, MOVING_BACKWARDS, INPUT_A, INPUT_B, DEAD
    }

    public final static TextureData IDLE_TEXTURE = new TextureData(0,0.250f,0.125f,0.375f);
    public final static TextureData INIT_MOV_A = new TextureData(0,0,0.125f,0.125f);
    public final static TextureData ACTIVE_MOV_A = new TextureData(0,0.125f,0.125f, 0.250f);
    public final static TextureData MOVING_A = new TextureData(0,0.375f,0.125f, 0.5f);
    public final static TextureData MOVING_B = new TextureData(0,0.5f,0.125f, 0.625f);
    public static final int SPRITE_LENGTH = 75;
    public static final int CHARACTER_LENGTH = 40;
    public static final int CHARACTER_HEIGHT = 160;
    public final int LENGTH_MOVE_A = CHARACTER_LENGTH + 10;
    public final int HEIGHT_MOVE_A = CHARACTER_HEIGHT;
    private final AnimationData WALKING_ANIMATION = new AnimationData(0.55f, new TextureData[]{MOVING_A, MOVING_B});
    private final float MOVING_SPEED = 0.75f;
    private final Vector2 RIGHT_MOVE_SPEED = new Vector2(MOVING_SPEED, 0);
    private final Vector2 LEFT_MOVE_SPEED = new Vector2(-MOVING_SPEED, 0);
    private final TimeCounter MOVE_B_COUNTER = new TimeCounter(0.64f);
    public final AttackData moveA;
    public CharacterState state;

    public MainCharacter(int id, Vector2 position){
        super(SPRITE_LENGTH, CHARACTER_HEIGHT, CHARACTER_LENGTH, CHARACTER_HEIGHT, position, id);
        this.state = CharacterState.IDLE;
        CollisionObject [] startupA = new CollisionObject[1];
        startupA[0] = new CollisionObject(new Vector2(), id, LENGTH_MOVE_A,
                HEIGHT_MOVE_A, this, CollisionObject.TYPE_HITTABLE);
        CollisionObject [] activeA = new CollisionObject[2];
        activeA[0] = new CollisionObject(new Vector2(), id,
                LENGTH_MOVE_A, HEIGHT_MOVE_A, this, CollisionObject.TYPE_HITTABLE);
        activeA[1] = new CollisionObject(new Vector2(LENGTH_MOVE_A, HEIGHT_MOVE_A - 65),
                id, 10, 15, this, CollisionObject.TYPE_ATTACK);
        CollisionObject [] recoveryA = new CollisionObject[1];
        recoveryA[0] = new CollisionObject(new Vector2(), id,
                LENGTH_MOVE_A, HEIGHT_MOVE_A, this, CollisionObject.TYPE_HITTABLE);

        moveA = new AttackData(0.12f,0.15f,0.1f, startupA, activeA, recoveryA);
    }

    @Override
    public synchronized CollisionObject[] getActiveCollisionBoxes(){
        if(state == CharacterState.INPUT_A){
            if(moveA.currentState == CollisionState.STARTUP)
                return moveA.startup;
            else if(moveA.currentState == CollisionState.ACTIVE)
                return moveA.active;
            else if(moveA.currentState == CollisionState.RECOVERY)
                return moveA.recovery;
        }

        return idleCollisionBoxes;
    }

    public void receiveInput(ControllerManager.GameInput input){
        if(state == CharacterState.INPUT_A || input == ControllerManager.GameInput.NO_INPUT)
            return;

        if(input == ControllerManager.GameInput.INPUT_OFF)
            this.state = CharacterState.IDLE;
        else if(input == ControllerManager.GameInput.RIGHT)
            this.state = CharacterState.MOVING_FORWARD;
        else if(input == ControllerManager.GameInput.LEFT)
            this.state = CharacterState.MOVING_BACKWARDS;
        else if(input == ControllerManager.GameInput.INPUT_A) {
            moveA.reset();
            this.state = CharacterState.INPUT_A;
        }

    }

    /**
     * Asigna un nuevo estado state al personaje. Esta funcion es para utilizarse por un boton para que no
     * interfiera con el manejo interno de los estados del personaje.
     * @param state state en el que se encontrara el personaje.
     */
    private synchronized void setStateFromButton(CharacterState state){
        if(this.state == CharacterState.DEAD || this.state == CharacterState.INPUT_A || this.state == CharacterState.INPUT_B)
            return;

        if(state == CharacterState.INPUT_A) {
            moveA.reset();
        } else if(state == CharacterState.INPUT_B)
            MOVE_B_COUNTER.reset();

        this.state = state;
    }

    @Override
    public TextureDrawer.TextureData getCurrentTexture(){
        if(state == CharacterState.MOVING_FORWARD || state == CharacterState.MOVING_BACKWARDS)
            return WALKING_ANIMATION.getCurrentTexture();

        if(state != CharacterState.INPUT_A)
            return IDLE_TEXTURE;

        if(moveA.currentState == CollisionState.ACTIVE)
            return ACTIVE_MOV_A;
        else
            return INIT_MOV_A;
    }

    @Override
    public void update(GameCharacter foe, GameFlow.UpdateInterval timeDifference, WorldData worldData) {
        super.update(foe, timeDifference, worldData);
        if (state == CharacterState.IDLE) {
            adjustToFoePosition(foe);
            WALKING_ANIMATION.reset();
            return;
        } if (state == CharacterState.MOVING_FORWARD) {
            adjustToFoePosition(foe);
            if(position.x + MOVING_SPEED < worldData.maxX) {
                move(RIGHT_MOVE_SPEED);
                WALKING_ANIMATION.update(timeDifference);
            }
        } if (state == CharacterState.MOVING_BACKWARDS) {
            adjustToFoePosition(foe);
            if(position.x - MOVING_SPEED > worldData.minX) {
                move(LEFT_MOVE_SPEED);
                WALKING_ANIMATION.update(timeDifference);
            }
        } if(state == CharacterState.INPUT_A){
            moveA.update(timeDifference);
            if(moveA.completed()){
                this.state = CharacterState.IDLE;
            }
        }

        if(state == CharacterState.INPUT_B){
            MOVE_B_COUNTER.accum(timeDifference);
            if(MOVE_B_COUNTER.completed()){
                this.state = CharacterState.IDLE;
            }
        }
    }

    public void reset(float x, float y){
        relativePosition.set(x, y);
        baseX.set(1,0);
        updatePosition();
        idleCollisionBoxes[0].updatePosition();
        state = CharacterState.IDLE;
    }

    @Override
    public boolean hittable(){
        //en este momento el personaje principal siempre puede ser golpeado
        return true;
    }

    @Override
    public boolean attacking(){
        return state == CharacterState.INPUT_A;
    }

    @Override
    public boolean alive(){
        return state != CharacterState.DEAD;
    }

    @Override
    public void hit(){
        state = CharacterState.DEAD;
    }
}