package com.jgame.elements;

import com.jgame.elements.GameButton.ButtonListener;
import com.jgame.util.GeometricElement;
import com.jgame.util.SimpleDrawer;
import com.jgame.util.Square;
import com.jgame.util.TimeCounter;
import com.jgame.util.Vector2;
import java.util.List;

/**
 * Created by jose on 14/01/16.
 */
public class MainCharacter implements GameElement {

    public enum GameState {
        IDLE, MOVING_FORWARD, MOVING_BACKWARDS, INPUT_A, INPUT_B
    }

    public static final SimpleDrawer.ColorData PLAYER_COLOR = new SimpleDrawer.ColorData(0.65f,0.5f,0.85f,1);
    public static final SimpleDrawer.ColorData INPUT_A_COLOR = new SimpleDrawer.ColorData(0.65f,0.75f,0.85f,1);
    public static final SimpleDrawer.ColorData INPUT_B_COLOR = new SimpleDrawer.ColorData(0.65f,0.25f,0.60f,1);
    public final int CHARACTER_LENGTH = 15;
    public final int CHARACTER_HEIGHT = 45;
    private int id;
    private final TimeCounter MOVE_A_COUNTER = new TimeCounter(0.33f);
    private final TimeCounter MOVE_B_COUNTER = new TimeCounter(0.64f);
    private final float MOVING_SPEED = 0.75f;
    private final Square bounds;
    public GameState state;
    private final GameButton inputLeft;
    private final GameButton inputRight;

    public MainCharacter(int id, Vector2 position, final GameButton inputLeft, final GameButton inputRight,
                         final GameButton inputA, final GameButton inputB){
        this.state = GameState.IDLE;
        this.id = id;
        bounds = new Square(position,CHARACTER_LENGTH,CHARACTER_HEIGHT,0);
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

    /**
     * Asigna un nuevo estado state al personaje. Esta funcion es para utilizarse por un boton para que no
     * interfiera con el manejo interno de los estados del personaje.
     * @param state state en el que se encontrara el personaje.
     */
    private synchronized void setStateFromButton(GameState state){
        if(this.state == GameState.INPUT_A || this.state == GameState.INPUT_B)
            return;

        if(state == GameState.INPUT_A)
            MOVE_A_COUNTER.reset();
        else if(state == GameState.INPUT_B)
            MOVE_B_COUNTER.reset();

        this.state = state;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public GeometricElement getBounds() {
        return bounds;
    }

    @Override
    public void update(List<GameElement> others, float timeDifference) {
        synchronized (this) {
            if (state == GameState.IDLE)
                return;
            if (state == GameState.MOVING_FORWARD)
                bounds.getPosition().add(MOVING_SPEED, 0);
            if (state == GameState.MOVING_BACKWARDS)
                bounds.getPosition().add(-MOVING_SPEED, 0);
            if(state == GameState.INPUT_A){
                MOVE_A_COUNTER.accum(timeDifference);
                if(MOVE_A_COUNTER.completed()){
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
        }
    }

    @Override
    public boolean alive() {
        return false;
    }
}
