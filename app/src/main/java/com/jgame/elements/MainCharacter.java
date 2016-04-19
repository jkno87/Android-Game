package com.jgame.elements;

import com.jgame.elements.GameButton.ButtonListener;
import com.jgame.util.GeometricElement;
import com.jgame.util.SimpleDrawer;
import com.jgame.util.Square;
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
    private final Square bounds;
    public GameState state;

    public MainCharacter(int id, Vector2 position, final GameButton inputLeft, final GameButton inputRight,
                         final GameButton inputA, final GameButton inputB){
        this.state = GameState.IDLE;
        this.id = id;
        bounds = new Square(position,CHARACTER_LENGTH,CHARACTER_HEIGHT,0);
        inputLeft.setButtonListener(new ButtonListener() {
            @Override
            public void pressAction() {
                setState(GameState.MOVING_BACKWARDS);
            }

            @Override
            public void releaseAction() {
                setState(GameState.IDLE);
            }
        });

        inputRight.setButtonListener(new ButtonListener() {
            @Override
            public void pressAction() {
                setState(GameState.MOVING_FORWARD);
            }

            @Override
            public void releaseAction() {
                setState(GameState.IDLE);
            }
        });

        inputA.setButtonListener(new ButtonListener() {
            @Override
            public void pressAction() {
                setState(GameState.INPUT_A);
            }

            @Override
            public void releaseAction() {
                if(inputLeft.pressed())
                    setState(GameState.MOVING_BACKWARDS);
                else if(inputRight.pressed())
                    setState(GameState.MOVING_FORWARD);
                else
                    setState(GameState.IDLE);
            }
        });

        inputB.setButtonListener(new ButtonListener() {
            @Override
            public void pressAction() {
                setState(GameState.INPUT_B);
            }

            @Override
            public void releaseAction() {
                if(inputLeft.pressed())
                    setState(GameState.MOVING_BACKWARDS);
                else if(inputRight.pressed())
                    setState(GameState.MOVING_FORWARD);
                else
                    setState(GameState.IDLE);
            }
        });
    }

    /**
     * Asigna un nuevo estado state al personaje.
     * @param state state en el que se encontrara el personaje.
     */
    private synchronized void setState(GameState state){
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
                bounds.getPosition().add(1, 0);
            if (state == GameState.MOVING_BACKWARDS)
                bounds.getPosition().add(-1, 0);
        }
    }

    @Override
    public boolean alive() {
        return false;
    }
}
