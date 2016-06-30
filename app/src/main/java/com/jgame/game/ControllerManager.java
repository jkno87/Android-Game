package com.jgame.game;

import android.util.Log;

import com.jgame.definitions.GameLevels;
import com.jgame.elements.GameButton;
import java.util.concurrent.BlockingQueue;

/**
 * Created by jose on 29/06/16.
 */
public class ControllerManager {

    public enum GameInput {
        LEFT, RIGHT, INPUT_A, INPUT_OFF
    }

    private final BlockingQueue<GameInput> inputs;
    private static final int NUMBER_OF_INPUTS = 4;
    private static final int INPUT_NONE = -1;
    public static final int INPUT_LEFT = 0;
    public static final int INPUT_RIGHT = 1;
    public static final int INPUT_A = 2;
    public static final int INPUT_B = 3;
    private final GameButton[] gameButtons;
    private int mainButtonPressed;
    private float inputX;
    private float inputY;

    public ControllerManager(BlockingQueue<GameInput> inputQueue){
        mainButtonPressed = INPUT_NONE;
        this.gameButtons = new GameButton[NUMBER_OF_INPUTS];
        this.inputs = inputQueue;
        gameButtons[INPUT_LEFT] = new GameButton(GameActivity.INPUT_LEFT_BOUNDS);
        gameButtons[INPUT_RIGHT] = new GameButton(GameActivity.INPUT_RIGHT_BOUNDS);
        gameButtons[INPUT_A] = new GameButton(GameActivity.INPUT_A_BOUNDS);
        gameButtons[INPUT_B] = new GameButton(GameActivity.INPUT_B_BOUNDS);
    }

    public void handleDown(float x, float y){
        try {
            inputX = GameLevels.FRUSTUM_WIDTH * x;
            inputY = GameLevels.FRUSTUM_HEIGHT * y;

            if (gameButtons[INPUT_LEFT].bounds.contains(inputX, inputY)) {
                inputs.put(GameInput.LEFT);
                mainButtonPressed = INPUT_LEFT;
            } else if (gameButtons[INPUT_RIGHT].bounds.contains(inputX, inputY)) {
                inputs.add(GameInput.RIGHT);
                mainButtonPressed = INPUT_RIGHT;
            } else if (gameButtons[INPUT_A].bounds.contains(inputX, inputY)) {
                inputs.add(GameInput.INPUT_A);
            }
        } catch (InterruptedException e){
            Thread.interrupted();
        }
    }

    public void handleDrag(float x, float y){
        try {
            inputX = GameLevels.FRUSTUM_WIDTH * x;
            inputY = GameLevels.FRUSTUM_HEIGHT * y;

            if (gameButtons[INPUT_LEFT].bounds.contains(inputX, inputY)) {
                inputs.put(GameInput.LEFT);
                mainButtonPressed = INPUT_LEFT;
            } else if (gameButtons[INPUT_RIGHT].bounds.contains(inputX, inputY)) {
                inputs.put(GameInput.RIGHT);
                mainButtonPressed = INPUT_RIGHT;
            } else if (gameButtons[INPUT_A].bounds.contains(inputX, inputY))
                inputs.put(GameInput.INPUT_A);
            else {
                mainButtonPressed = INPUT_NONE;
                inputs.put(GameInput.INPUT_OFF);
            }
        } catch (InterruptedException e){
            Thread.interrupted();
        }

    }

    public void handleUp(float x, float y){
        mainButtonPressed = INPUT_NONE;
        inputs.add(GameInput.INPUT_OFF);
    }

    public void handlePointerDown(float x, float y){
        try {
            inputX = GameLevels.FRUSTUM_WIDTH * x;
            inputY = GameLevels.FRUSTUM_HEIGHT * y;

            if (gameButtons[INPUT_LEFT].bounds.contains(inputX, inputY))
                inputs.put(GameInput.LEFT);
            else if (gameButtons[INPUT_RIGHT].bounds.contains(inputX, inputY))
                inputs.put(GameInput.RIGHT);
            else if (gameButtons[INPUT_A].bounds.contains(inputX, inputY))
                inputs.put(GameInput.INPUT_A);
        } catch (InterruptedException e){
            Thread.interrupted();
        }
    }

    public void handlePointerUp(float x, float y){
        try {
            inputX = GameLevels.FRUSTUM_HEIGHT * x;
            inputY = GameLevels.FRUSTUM_HEIGHT * y;

            if (mainButtonPressed == INPUT_NONE)
                return;
            else if (mainButtonPressed == INPUT_LEFT)
                inputs.put(GameInput.LEFT);
            else if (mainButtonPressed == INPUT_RIGHT)
                inputs.put(GameInput.RIGHT);


        } catch (InterruptedException e){
            Thread.interrupted();
        }

    }
}
