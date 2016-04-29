package com.jgame.elements;

import com.jgame.elements.GameButton.ButtonListener;
import com.jgame.util.SimpleDrawer;
import com.jgame.util.TimeCounter;
import com.jgame.util.Vector2;
import java.util.List;
import com.jgame.elements.CollisionState.CollisionSt;

/**
 * Created by jose on 14/01/16.
 */
public class MainCharacter {

    public enum GameState {
        IDLE, MOVING_FORWARD, MOVING_BACKWARDS, INPUT_A, INPUT_B
    }

    public static final SimpleDrawer.ColorData PLAYER_COLOR = new SimpleDrawer.ColorData(0.65f,0.5f,0.85f,1);
    public static final SimpleDrawer.ColorData INPUT_A_COLOR = new SimpleDrawer.ColorData(0.65f,0.75f,0.85f,1);
    public static final SimpleDrawer.ColorData INPUT_B_COLOR = new SimpleDrawer.ColorData(0.65f,0.25f,0.60f,1);
    private final float MOVING_SPEED = 0.75f;
    private final Vector2 RIGHT_MOVE_SPEED = new Vector2(MOVING_SPEED, 0);
    private final Vector2 LEFT_MOVE_SPEED = new Vector2(-MOVING_SPEED, 0);
    public final int CHARACTER_LENGTH = 40;
    public final int CHARACTER_HEIGHT = 80;
    public final int LENGTH_MOVE_A = 35;
    public final int HEIGHT_MOVE_A = 85;
    private int id;
    private final TimeCounter MOVE_B_COUNTER = new TimeCounter(0.64f);
    public final GameObject mainObject;
    public final CollisionState moveA;
    public final CollisionObject collisionBox;
    private final GameButton inputLeft;
    private final GameButton inputRight;
    public GameState state;

    public MainCharacter(int id, Vector2 position, final GameButton inputLeft, final GameButton inputRight,
                         final GameButton inputA, final GameButton inputB){
        this.state = GameState.IDLE;
        this.id = id;
        mainObject = new GameObject(position,id);
        mainObject.baseX.set(-1, 0);
        moveA = new CollisionState(0.2f,0.15f,0.26f);
        initializeCollisionBoxes();
        collisionBox = new CollisionObject(new Vector2(), id, CHARACTER_LENGTH, CHARACTER_HEIGHT, mainObject);
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

    private void initializeCollisionBoxes(){
        CollisionObject [] startupA = new CollisionObject[1];
        startupA[0] = new CollisionObject(new Vector2(), id, LENGTH_MOVE_A, HEIGHT_MOVE_A, mainObject);
        CollisionObject [] activeA = new CollisionObject[2];
        activeA[0] = new CollisionObject(new Vector2(), id, LENGTH_MOVE_A, HEIGHT_MOVE_A, mainObject);
        activeA[1] = new CollisionObject(new Vector2(LENGTH_MOVE_A, HEIGHT_MOVE_A), id, 15, 10, mainObject);
        CollisionObject [] recoveryA = new CollisionObject[1];
        recoveryA[0] = new CollisionObject(new Vector2(), id, LENGTH_MOVE_A, HEIGHT_MOVE_A, mainObject);
        moveA.setStartupBoxes(startupA);
        moveA.setActiveBoxes(activeA);
        moveA.setRecoveryBoxes(recoveryA);
    }

    public synchronized void fillDrawer(SimpleDrawer d, Vector2 origin){
        if(state == GameState.INPUT_A){
            if(moveA.currentState == CollisionSt.STARTUP)
                for(CollisionObject o : moveA.startup)
                    d.addSquare(o.bounds, MainCharacter.INPUT_A_COLOR, origin, mainObject.baseX);
            else if(moveA.currentState == CollisionSt.ACTIVE)
                for(CollisionObject o : moveA.active)
                    d.addSquare(o.bounds, MainCharacter.INPUT_A_COLOR, origin, mainObject.baseX);
            else if(moveA.currentState == CollisionSt.RECOVERY)
                for(CollisionObject o : moveA.recovery)
                    d.addSquare(o.bounds, MainCharacter.INPUT_A_COLOR, origin, mainObject.baseX);
        } else
            d.addSquare(collisionBox.bounds, MainCharacter.INPUT_B_COLOR, origin, mainObject.baseX);
    }

    /**
     * Asigna un nuevo estado state al personaje. Esta funcion es para utilizarse por un boton para que no
     * interfiera con el manejo interno de los estados del personaje.
     * @param state state en el que se encontrara el personaje.
     */
    private synchronized void setStateFromButton(GameState state){
        if(this.state == GameState.INPUT_A || this.state == GameState.INPUT_B)
            return;

        if(state == GameState.INPUT_A) {
            moveA.reset();
        } else if(state == GameState.INPUT_B)
            MOVE_B_COUNTER.reset();

        this.state = state;
    }

    public void update(List<GameElement> others, float timeDifference) {
        synchronized (this) {
            if (state == GameState.IDLE)
                return;
            if (state == GameState.MOVING_FORWARD)
                mainObject.move(RIGHT_MOVE_SPEED);
            if (state == GameState.MOVING_BACKWARDS)
                mainObject.move(LEFT_MOVE_SPEED);
            if(state == GameState.INPUT_A){
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

            mainObject.update(others, timeDifference);
            collisionBox.update(others, timeDifference);

        }
    }
}