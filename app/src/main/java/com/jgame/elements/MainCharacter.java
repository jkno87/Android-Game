package com.jgame.elements;

import com.jgame.elements.GameButton.ButtonListener;
import com.jgame.game.GameFlow;
import com.jgame.game.LevelSelectFlow;
import com.jgame.util.TextureDrawer;
import com.jgame.util.TimeCounter;
import com.jgame.util.Vector2;
import java.util.List;
import com.jgame.elements.AttackData.CollisionState;

/**
 * Created by jose on 14/01/16.
 */
public class MainCharacter extends Character {

    public enum GameState {
        IDLE, MOVING_FORWARD, MOVING_BACKWARDS, INPUT_A, INPUT_B
    }

    public final static TextureDrawer.TextureData IDLE_TEXTURE = new TextureDrawer.TextureData(0,0,0.03125f,0.0625f);
    public final static TextureDrawer.TextureData INIT_MOV_A = new TextureDrawer.TextureData(0,0.0625f, 0.03125f,0.125f);
    public final static TextureDrawer.TextureData ACTIVE_MOV_A = new TextureDrawer.TextureData(0,0.125f,0.03125f, 0.1875f);
    public static final int CHARACTER_LENGTH = 40;
    public static final int CHARACTER_HEIGHT = 80;
    private final Vector2 CHARACTER_OFFSET = new Vector2(-CHARACTER_LENGTH/2,0);
    private final float MOVING_SPEED = 0.75f;
    private final Vector2 RIGHT_MOVE_SPEED = new Vector2(MOVING_SPEED, 0);
    private final Vector2 LEFT_MOVE_SPEED = new Vector2(-MOVING_SPEED, 0);
    public final int LENGTH_MOVE_A = 35;
    public final int HEIGHT_MOVE_A = 85;
    private final TimeCounter MOVE_B_COUNTER = new TimeCounter(0.64f);
    public final AttackData moveA;
    private final GameButton inputLeft;
    private final GameButton inputRight;
    public GameState state;

    public MainCharacter(int id, Vector2 position, final GameButton inputLeft, final GameButton inputRight,
                         final GameButton inputA, final GameButton inputB){
        super(CHARACTER_LENGTH, CHARACTER_HEIGHT, position, id);
        this.state = GameState.IDLE;
        //baseX.set(-1, 0);
        CollisionObject [] startupA = new CollisionObject[1];
        startupA[0] = new CollisionObject(new Vector2(CHARACTER_OFFSET), id, LENGTH_MOVE_A,
                HEIGHT_MOVE_A, this, CollisionObject.TYPE_HITTABLE);
        CollisionObject [] activeA = new CollisionObject[2];
        activeA[0] = new CollisionObject(new Vector2(CHARACTER_OFFSET), id,
                LENGTH_MOVE_A, HEIGHT_MOVE_A, this, CollisionObject.TYPE_HITTABLE);
        activeA[1] = new CollisionObject(new Vector2(CHARACTER_OFFSET).add(LENGTH_MOVE_A, HEIGHT_MOVE_A),
                id, 15, 10, this, CollisionObject.TYPE_ATTACK);
        CollisionObject [] recoveryA = new CollisionObject[1];
        recoveryA[0] = new CollisionObject(new Vector2(CHARACTER_OFFSET), id,
                LENGTH_MOVE_A, HEIGHT_MOVE_A, this, CollisionObject.TYPE_HITTABLE);

        moveA = new AttackData(0.12f,0.15f,0.1f, startupA, activeA, recoveryA);
        this.inputLeft = inputLeft;
        this.inputRight = inputRight;
        this.inputLeft.setButtonListener(new ButtonListener() {
            @Override
            public void pressAction() {
                setStateFromButton(GameState.MOVING_BACKWARDS);
            }

            @Override
            public void releaseAction() {
                setStateFromButton(GameState.IDLE);
            }
        });

        this.inputRight.setButtonListener(new ButtonListener() {
            @Override
            public void pressAction() {
                setStateFromButton(GameState.MOVING_FORWARD);
            }

            @Override
            public void releaseAction() {
                setStateFromButton(GameState.IDLE);
            }
        });

        inputA.setButtonListener(new ButtonListener() {
            @Override
            public void pressAction() {
                setStateFromButton(GameState.INPUT_A);
            }

            @Override
            public void releaseAction() {
            }
        });

        inputB.setButtonListener(new ButtonListener() {
            @Override
            public void pressAction() {
                setStateFromButton(GameState.INPUT_B);
            }

            @Override
            public void releaseAction() {
            }
        });
    }

    @Override
    public synchronized CollisionObject[] getActiveCollisionBoxes(){
        if(state == GameState.INPUT_A){
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
     * Asigna un nuevo estado state al personaje. Esta funcion es para utilizarse por un boton para que no
     * interfiera con el manejo interno de los estados del personaje.
     * @param state state en el que se encontrara el personaje.
     */
    private synchronized void setStateFromButton(GameState state){
        if(currentState == CharacterState.DEAD || this.state == GameState.INPUT_A || this.state == GameState.INPUT_B)
            return;

        if(state == GameState.INPUT_A) {
            moveA.reset();
        } else if(state == GameState.INPUT_B)
            MOVE_B_COUNTER.reset();

        this.state = state;
    }

    @Override
    public TextureDrawer.TextureData getCurrentTexture(){
        if(state != GameState.INPUT_A)
            return IDLE_TEXTURE;

        if(moveA.currentState == CollisionState.ACTIVE)
            return ACTIVE_MOV_A;
        else
            return INIT_MOV_A;
    }

    @Override
    public void update(Character foe, GameFlow.UpdateInterval timeDifference) {
        synchronized (this) {
            if (state == GameState.IDLE)
                return;
            if (state == GameState.MOVING_FORWARD) {
                move(RIGHT_MOVE_SPEED);
                updatePosition();
                //Esto esta horripilante, esto se va al diablo con cualquier cambio
                idleCollisionBoxes[0].updatePosition();
            } if (state == GameState.MOVING_BACKWARDS) {
                move(LEFT_MOVE_SPEED);
                updatePosition();
                //Esto esta horripilante, esto se va al diablo con cualquier cambio
                idleCollisionBoxes[0].updatePosition();
            } if(state == GameState.INPUT_A){
                moveA.update(timeDifference);
                if(moveA.completed()){
                    this.state = GameState.IDLE;
                    if(inputLeft.pressed())
                        this.state = GameState.MOVING_BACKWARDS;
                    if(inputRight.pressed())
                        this.state = GameState.MOVING_FORWARD;
                }
            }

            if(state == GameState.INPUT_B){
                MOVE_B_COUNTER.accum(timeDifference);
                if(MOVE_B_COUNTER.completed()){
                    this.state = GameState.IDLE;
                    if(inputLeft.pressed())
                        this.state = GameState.MOVING_BACKWARDS;
                    if(inputRight.pressed())
                        this.state = GameState.MOVING_FORWARD;
                }
            }

            for(CollisionObject co : getActiveCollisionBoxes())
                if(co.checkCollision(foe))
                    foe.hit();
        }
    }

    public void reset(float x, float y){
        relativePosition.set(x,y);
        updatePosition();
        idleCollisionBoxes[0].updatePosition();
        currentState = CharacterState.IDLE;
    }

    @Override
    public void hit(){
        currentState = CharacterState.DEAD;
    }
}