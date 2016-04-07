package com.jgame.game;

import com.jgame.definitions.GameLevels;
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
    public final Square inputLeft;
    public final Square inputRight;
    public final Square inputA;
    public final Square inputB;

    public FightingGameFlow(){
        gameFloor = new Square(0, 0, PLAYING_WIDTH, PLAYING_HEIGHT * 0.25f);
        inputLeft = new Square(20,INPUTS_HEIGHT, DIRECTION_WIDTH, DIRECTION_WIDTH);
        inputRight = new Square(20 + DIRECTION_WIDTH + 20, INPUTS_HEIGHT, DIRECTION_WIDTH, DIRECTION_WIDTH);
        inputA = new Square(PLAYING_WIDTH - BUTTONS_WIDTH * 2 - 50, INPUTS_HEIGHT, BUTTONS_WIDTH, BUTTONS_WIDTH);
        inputB = new Square(PLAYING_WIDTH - BUTTONS_WIDTH - 25, INPUTS_HEIGHT, BUTTONS_WIDTH, BUTTONS_WIDTH);
    }

    @Override
    public void handleDrag(float x, float y) {

    }

    @Override
    public void handleDown(float x, float y) {

    }

    @Override
    public void handleUp(float x, float y) {

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
