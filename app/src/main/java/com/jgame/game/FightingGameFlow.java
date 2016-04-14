package com.jgame.game;

import android.util.Log;

import com.jgame.definitions.GameLevels;
import com.jgame.elements.GameButton;
import com.jgame.util.Square;

/**
 * Created by jose on 7/04/16.
 */
public class FightingGameFlow extends GameFlow {

    public static float PLAYING_WIDTH = GameLevels.FRUSTUM_WIDTH;
    public static float PLAYING_HEIGHT = GameLevels.FRUSTUM_HEIGHT;
    private final float DIRECTION_WIDTH = 45;
    private final float BUTTONS_WIDTH = 50;
    private final float INPUTS_HEIGHT = 15;
    public final Square gameFloor;
    public final GameButton inputLeft;
    public final GameButton inputRight;
    public final GameButton inputA;
    public final GameButton inputB;
    private GameButton mainButtonPressed;

    public FightingGameFlow(){
        gameFloor = new Square(0, 0, PLAYING_WIDTH, PLAYING_HEIGHT * 0.25f);
        inputLeft = new GameButton(new Square(20,INPUTS_HEIGHT, DIRECTION_WIDTH, DIRECTION_WIDTH));
        inputRight = new GameButton(new Square(20 + DIRECTION_WIDTH + 20, INPUTS_HEIGHT, DIRECTION_WIDTH, DIRECTION_WIDTH));
        inputA = new GameButton(new Square(PLAYING_WIDTH - BUTTONS_WIDTH * 2 - 50, INPUTS_HEIGHT, BUTTONS_WIDTH, BUTTONS_WIDTH));
        inputB = new GameButton(new Square(PLAYING_WIDTH - BUTTONS_WIDTH - 25, INPUTS_HEIGHT, BUTTONS_WIDTH, BUTTONS_WIDTH));
    }

    private void releaseInputs(){
        inputLeft.release();
        inputRight.release();
        inputA.release();
        inputB.release();
    }

    private void calculateMainInput(float gameX, float gameY){
        if(inputLeft.bounds.contains(gameX, gameY))
            mainButtonPressed = inputLeft;
        else if(inputRight.bounds.contains(gameX, gameY))
            mainButtonPressed = inputRight;
        else if(inputA.bounds.contains(gameX, gameY))
            mainButtonPressed = inputA;
        else if(inputB.bounds.contains(gameX, gameY))
            mainButtonPressed = inputB;

        mainButtonPressed.press();
    }

    @Override
    public void handleDrag(float x, float y) {

        float gameX = GameLevels.FRUSTUM_WIDTH * x;
        float gameY = GameLevels.FRUSTUM_HEIGHT * y;

        //Log.d("Game", "Drag " + gameX + ", " + gameY);

        if(!mainButtonPressed.pressed())
            calculateMainInput(gameX, gameY);
        else {
            if (!mainButtonPressed.bounds.contains(gameX, gameY))
                mainButtonPressed.release();
        }
    }

    @Override
    public void handleDown(float x, float y) {
        float gameX = GameLevels.FRUSTUM_WIDTH * x;
        float gameY = GameLevels.FRUSTUM_HEIGHT * y;

        //Log.d("Game", "Down " + gameX + ", " + gameY);
        calculateMainInput(gameX, gameY);
    }

    @Override
    public void handleUp(float x, float y) {
        mainButtonPressed.release();
    }

    @Override
    public void update(float interval) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }
}
