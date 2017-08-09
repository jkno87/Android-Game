package com.jgame.elements;

import com.jgame.game.ControllerManager;
import com.jgame.game.GameActivity;
import com.jgame.game.GameData.Event;
import com.jgame.util.Decoration;
import com.jgame.util.TextureDrawer;
import com.jgame.util.TextureDrawer.TextureData;
import com.jgame.util.TimeCounter;
import com.jgame.util.Vector2;
import com.jgame.elements.AttackData.CollisionState;
import java.util.ArrayDeque;

/**
 * Objeto que representa al personaje del jugador.
 * Created by jose on 14/01/16.
 */
public class MainCharacter extends GameCharacter {

    public enum CharacterState {
        ABSORBING {
            @Override
            public boolean isCancellable() {
                return false;
            }
        }, IDLE {
            @Override
            public boolean isCancellable() {
                return true;
            }
        }, MOVING_FORWARD {
            @Override
            public boolean isCancellable() {
                return true;
            }
        }, MOVING_BACKWARDS {
            @Override
            public boolean isCancellable() {
                return true;
            }
        }, INPUT_A {
            @Override
            public boolean isCancellable() {
                return false;
            }
        }, INPUT_B {
            @Override
            public boolean isCancellable() {
                return false;
            }
        }, DEAD {
            @Override
            public boolean isCancellable() {
                return false;
            }
        }, ADVANCING {
            @Override
            public boolean isCancellable() {
                return false;
            }
        };

        public abstract boolean isCancellable();
    }

    public final static TextureData IDLE_TEXTURE = new TextureData(0.875f,0f,1,0.25f);
    public final static TextureData RECOVERY_SUCCESS_1 = new TextureData(0.25f, 0.75f, 0.375f, 1f);
    public final static TextureData RECOVERY_SUCCESS_2 = new TextureData(0.125f, 0.75f, 0.25f, 1f);
    public final static TextureData STARTUP_MOV_A = new TextureData(0.5f, 0.75f, 0.625f, 1f);
    public final static TextureData ACTIVE_MOV_A = new TextureData(0.375f, 0.75f, 0.5f, 1f);
    public final static TextureData MOVING_A = new TextureData(0.875f, 0.25f, 1, 0.5f);
    public final static TextureData MOVING_B = new TextureData(0.875f, 0.5f, 1, 0.75f);
    public final static TextureData MOVING_C = new TextureData(0.75f, 0.5f, 0.875f, 0.75f);
    public final static TextureData MOVING_D = new TextureData(0.625f, 0.75f, 0.75f, 1f);
    public final static TextureData MOVING_E = new TextureData(0.75f, 0.75f, 0.875f, 1f);
    public static final int SPRITE_LENGTH = 75;
    public static final int CHARACTER_LENGTH = 40;
    public static final int CHARACTER_HEIGHT = 160;
    public final int LENGTH_MOVE_A = CHARACTER_LENGTH + 10;
    public final int HEIGHT_MOVE_A = CHARACTER_HEIGHT;
    private final int KNOCKDOWN_FRAMES = 18;
    private final int INITIAL_HP = 1000;
    private final AnimationData WALKING_ANIMATION = new AnimationData(15, true, new TextureData[]{MOVING_A, MOVING_B, MOVING_C, MOVING_D, MOVING_E});
    private final float MOVING_SPEED = 0.75f;
    private final Vector2 RIGHT_MOVE_SPEED = new Vector2(MOVING_SPEED, 0);
    private final Vector2 LEFT_MOVE_SPEED = new Vector2(-MOVING_SPEED, 0);
    private final TimeCounter MOVE_B_COUNTER = new TimeCounter(0.64f);
    public final AttackData moveA;
    public CharacterState state;
    private int hp;
    private final float maxX;
    private final float minX;
    private final AnimationData ABSORBING_ANIMATION = new AnimationData(2, false, new TextureData[]{RECOVERY_SUCCESS_1, RECOVERY_SUCCESS_2});

    public MainCharacter(int id, Vector2 position, float minX, float maxX){
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

        moveA = new AttackData(startupA, activeA, recoveryA);
        moveA.setStartupAnimation(new AnimationData(5, false, STARTUP_MOV_A));
        moveA.setActiveAnimation(new AnimationData(10, false, ACTIVE_MOV_A));
        moveA.setRecoveryAnimation(new AnimationData(10, false, STARTUP_MOV_A));

        this.maxX = maxX;
        this.minX = minX;
        this.hp = INITIAL_HP;
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

    /**
     * Funcion que se encarga de recibir el ultimo input del ControllerManager.
     * Solo realiza una accion si el estado en el que se encuentra el personaje
     * puede ser cancelable
     * @param input Ultimo GameInput recibido
     */
    public void receiveInput(ControllerManager.GameInput input){
        if (!state.isCancellable())
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
            return WALKING_ANIMATION.getCurrentSprite();

        if(state == CharacterState.ABSORBING)
            return ABSORBING_ANIMATION.getCurrentSprite();

        if(state != CharacterState.INPUT_A)
            return IDLE_TEXTURE;

        return moveA.getCurrentAnimation().getCurrentSprite();
    }

    @Override
    public Event update(GameCharacter foe, ArrayDeque<Decoration> decorationData) {
        Event e = super.update(foe, decorationData);
        if (state == CharacterState.IDLE) {
            adjustToFoePosition(foe);
            WALKING_ANIMATION.reset();
            return Event.NONE;
        } if (state == CharacterState.MOVING_FORWARD) {
            adjustToFoePosition(foe);
            if(position.x + MOVING_SPEED < maxX) {
                move(RIGHT_MOVE_SPEED);
                WALKING_ANIMATION.updateFrame();
            }
        } if (state == CharacterState.MOVING_BACKWARDS) {
            adjustToFoePosition(foe);
            if(position.x - MOVING_SPEED > minX) {
                move(LEFT_MOVE_SPEED);
                WALKING_ANIMATION.updateFrame();
            }
        } if(state == CharacterState.INPUT_A){
            //Si se detecta colision con el input, significa que absorbio energia
            if (e == Event.HIT){
                hp = INITIAL_HP;
                state = CharacterState.ABSORBING;
                ABSORBING_ANIMATION.reset();
                return Event.NONE;
            }

            moveA.update();
            if(moveA.completed()){
                this.state = CharacterState.IDLE;
            }
        } if (state == CharacterState.ABSORBING){
            ABSORBING_ANIMATION.updateFrame();
            if(ABSORBING_ANIMATION.completed()) {
                state = CharacterState.ADVANCING;
                baseX.set(1,0);
            }
        }

        if(state == CharacterState.INPUT_B){
            /*MOVE_B_COUNTER.accum(timeDifference);
            if(MOVE_B_COUNTER.completed()){
                this.state = CharacterState.IDLE;
            }*/
        }

        if(hp < 0)
            state = CharacterState.DEAD;
        else {
            hp -= 1;
            color.a = (float) hp / INITIAL_HP;
        }

        return Event.NONE;
    }

    public void reset(float x, float y){
        relativePosition.set(x, y);
        baseX.set(1,0);
        updatePosition();
        idleCollisionBoxes[0].updatePosition();
        state = CharacterState.IDLE;
        hp = INITIAL_HP;
    }

    public boolean completedTransition(){
        return this.state == CharacterState.ADVANCING && this.position.x < GameActivity.INITIAL_CHARACTER_POSITION;
    }

    /**
     * Accion que sirve para que el personaje principal se caiga. Esto tiene la funcion de crear espacio entre el jugador y
     * el enemigo, tambien para controlar el ritmo de juego.
     */
    public void trip(){
        moveBackwards(25);
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