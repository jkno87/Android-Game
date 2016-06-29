package com.jgame.game;

import android.util.Log;

import com.jgame.definitions.GameLevels;
import com.jgame.elements.GameButton;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by jose on 29/06/16.
 */
public class ControllerTask implements Runnable {

    public enum GameInput {
        LEFT, RIGHT, INPUT_A, INPUT_OFF
    }

    private final BlockingQueue<GameInput> inputs;
    private static final int INPUT_NONE = -1;
    private final GameButton[] gameButtons;
    private int mainButtonPressed;

    public ControllerTask(GameButton[] gameButtons, BlockingQueue<GameInput> inputQueue){
        mainButtonPressed = INPUT_NONE;
        this.gameButtons = gameButtons;
        this.inputs = inputQueue;
    }

    public void handleDown(float x, float y){
        float gameX = GameLevels.FRUSTUM_WIDTH * x;
        float gameY = GameLevels.FRUSTUM_HEIGHT * y;

        Log.d("game", "Receiving input");

        if(gameButtons[GameActivity.INPUT_LEFT].bounds.contains(gameX,gameY))
            inputs.add(GameInput.LEFT);
        else if(gameButtons[GameActivity.INPUT_RIGHT].bounds.contains(gameX,gameY))
            inputs.add(GameInput.RIGHT);
    }

    public void handleDrag(float x, float y){

    }

    public void handleUp(float x, float y){
        inputs.add(GameInput.INPUT_OFF);
    }

    public void handlePointerDown(float x, float y){
        float gameX = GameLevels.FRUSTUM_WIDTH * x;
        float gameY = GameLevels.FRUSTUM_HEIGHT * y;

        Log.d("game", "Receiving input");

        if(gameButtons[GameActivity.INPUT_LEFT].bounds.contains(gameX,gameY))
            inputs.add(GameInput.LEFT);
        else if(gameButtons[GameActivity.INPUT_RIGHT].bounds.contains(gameX,gameY))
            inputs.add(GameInput.RIGHT);
    }

    public void handlePointerUp(float x, float y){

    }


    @Override
    public void run() {
        while(true){
            try {
                Thread.sleep(3L);
            } catch (InterruptedException e){
                Thread.interrupted();
            }
        }
    }
}
