package com.jgame.game;

import com.jgame.definitions.GameLevels;
import com.jgame.elements.AttackData;
import com.jgame.elements.CollisionObject;
import com.jgame.elements.Enemy;
import com.jgame.elements.GameButton;
import com.jgame.elements.MainCharacter;
import com.jgame.util.Square;
import com.jgame.util.Vector2;

/**
 * Created by jose on 7/04/16.
 */
public class FightingGameFlow extends GameFlow {

    private final int INPUT_LEFT = 0;
    private final int INPUT_RIGHT = 1;
    private final int INPUT_A = 2;
    private final int INPUT_B = 3;
    private final int INPUT_NONE = -1;
    public static float PLAYING_WIDTH = GameLevels.FRUSTUM_WIDTH;
    public static float PLAYING_HEIGHT = GameLevels.FRUSTUM_HEIGHT;
    private final float CONTROLS_HEIGHT = PLAYING_HEIGHT * 0.25f;
    private final float ELEMENTS_HEIGHT = CONTROLS_HEIGHT + 20;
    private final float DIRECTION_WIDTH = 45;
    private final float BUTTONS_WIDTH = 50;
    private final float INPUTS_HEIGHT = 15;
    public final Square gameFloor;
    public final GameButton[] gameButtons;
    private int mainButtonPressed;
    public final MainCharacter mainCharacter;
    public final Enemy sampleEnemy;


    public FightingGameFlow(){
        gameFloor = new Square(0, 0, PLAYING_WIDTH, CONTROLS_HEIGHT);
        gameButtons = new GameButton[4];
        mainButtonPressed = INPUT_NONE;
        gameButtons[INPUT_LEFT] = new GameButton(new Square(20,INPUTS_HEIGHT, DIRECTION_WIDTH, DIRECTION_WIDTH));
        gameButtons[INPUT_RIGHT] = new GameButton(new Square(20 + DIRECTION_WIDTH + 20, INPUTS_HEIGHT, DIRECTION_WIDTH, DIRECTION_WIDTH));
        gameButtons[INPUT_A] = new GameButton(new Square(PLAYING_WIDTH - BUTTONS_WIDTH * 2 - 50, INPUTS_HEIGHT, BUTTONS_WIDTH, BUTTONS_WIDTH));
        gameButtons[INPUT_B] = new GameButton(new Square(PLAYING_WIDTH - BUTTONS_WIDTH - 25, INPUTS_HEIGHT, BUTTONS_WIDTH, BUTTONS_WIDTH));
        mainCharacter = new MainCharacter(0, new Vector2(15,ELEMENTS_HEIGHT), gameButtons[INPUT_LEFT],
                gameButtons[INPUT_RIGHT], gameButtons[INPUT_A], gameButtons[INPUT_B]);
        sampleEnemy = new Enemy(30,50,new Vector2(150,ELEMENTS_HEIGHT),0,mainCharacter, 1);
        setupSampleEnemy();
    }

    private void setupSampleEnemy(){
        AttackData sample = new AttackData(0.33f,0.1f,0.45f);
        CollisionObject[] startupA = new CollisionObject[]{sampleEnemy.idleCollisionBox};
        CollisionObject[] activeA = new CollisionObject[2];
        activeA[0] = sampleEnemy.idleCollisionBox;
        activeA[1] = new CollisionObject(new Vector2(15,25),0,10,5,sampleEnemy);
        CollisionObject[] recoveryA = new CollisionObject[]{sampleEnemy.idleCollisionBox};
        sample.setStartupBoxes(startupA);
        sample.setActiveBoxes(activeA);
        sample.setRecoveryBoxes(recoveryA);

        sampleEnemy.attacks[0] = sample;
    }


    private void calculateMainInput(float gameX, float gameY){
        if(gameButtons[INPUT_LEFT].bounds.contains(gameX, gameY))
            mainButtonPressed = INPUT_LEFT;
        else if(gameButtons[INPUT_RIGHT].bounds.contains(gameX, gameY))
            mainButtonPressed = INPUT_RIGHT;
        else if(gameButtons[INPUT_A].bounds.contains(gameX, gameY))
            mainButtonPressed = INPUT_A;
        else if(gameButtons[INPUT_B].bounds.contains(gameX, gameY))
            mainButtonPressed = INPUT_B;
        else
            mainButtonPressed = INPUT_NONE;

        if(mainButtonPressed != INPUT_NONE)
            gameButtons[mainButtonPressed].press();
    }

    @Override
    public void handleDrag(float x, float y) {
        if(mainButtonPressed == INPUT_NONE)
            return;

        float gameX = GameLevels.FRUSTUM_WIDTH * x;
        float gameY = GameLevels.FRUSTUM_HEIGHT * y;

        if(!gameButtons[mainButtonPressed].pressed())
            calculateMainInput(gameX, gameY);
        else {
            if (!gameButtons[mainButtonPressed].bounds.contains(gameX, gameY))
                gameButtons[mainButtonPressed].release();
        }
    }

    @Override
    public void handleDown(float x, float y) {
        float gameX = GameLevels.FRUSTUM_WIDTH * x;
        float gameY = GameLevels.FRUSTUM_HEIGHT * y;
        calculateMainInput(gameX, gameY);
    }

    @Override
    public void handleUp(float x, float y) {
        if(mainButtonPressed != INPUT_NONE)
            gameButtons[mainButtonPressed].release();
    }

    @Override
    public void update(float interval) {
        mainCharacter.update(null, interval);
        sampleEnemy.update(null, interval);
    }

    @Override
    public void handlePointerDown(float x, float y){
        float gameX = GameLevels.FRUSTUM_WIDTH * x;
        float gameY = GameLevels.FRUSTUM_HEIGHT * y;

        if(gameButtons[INPUT_LEFT].bounds.contains(gameX,gameY))
            gameButtons[INPUT_LEFT].press();
        else if(gameButtons[INPUT_RIGHT].bounds.contains(gameX,gameY))
            gameButtons[INPUT_RIGHT].press();
        else if(gameButtons[INPUT_A].bounds.contains(gameX,gameY))
            gameButtons[INPUT_A].press();
        else if(gameButtons[INPUT_B].bounds.contains(gameX,gameY))
            gameButtons[INPUT_B].press();
    }

    @Override
    public void handlePointerUp(float x, float y){
        for(int i = 0; i < gameButtons.length; i++)
            if(i != mainButtonPressed)
                gameButtons[i].release();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }
}
