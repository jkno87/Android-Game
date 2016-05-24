package com.jgame.game;

import com.jgame.definitions.GameLevels;
import com.jgame.elements.AttackData;
import com.jgame.elements.CollisionObject;
import com.jgame.elements.Character;
import com.jgame.elements.EmptyEnemy;
import com.jgame.elements.Enemy;
import com.jgame.elements.GameButton;
import com.jgame.elements.GameObject;
import com.jgame.elements.MainCharacter;
import com.jgame.util.IdGenerator;
import com.jgame.util.Square;
import com.jgame.util.TextureDrawer;
import com.jgame.util.TimeCounter;
import com.jgame.util.Vector2;

/**
 * Created by jose on 7/04/16.
 */
public class FightingGameFlow extends GameFlow {

    private final float SPAWN_TIME = 1.5f;
    private final int NUMBER_OF_INPUTS = 4;
    private final int MAX_WORLD_OBJECTS = 6;
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
    private final IdGenerator ID_GEN = new IdGenerator();
    public final Square gameFloor;
    public final GameButton[] gameButtons;
    private int mainButtonPressed;
    public final MainCharacter mainCharacter;
    public Enemy currentEnemy;
    public final EmptyEnemy enemySpawnInterval;
    public final Enemy[] availableEnemies;
    public int score;

    public FightingGameFlow(){
        gameFloor = new Square(0, 0, PLAYING_WIDTH, CONTROLS_HEIGHT);
        gameButtons = new GameButton[NUMBER_OF_INPUTS];
        mainButtonPressed = INPUT_NONE;
        gameButtons[INPUT_LEFT] = new GameButton(new Square(20,INPUTS_HEIGHT, DIRECTION_WIDTH, DIRECTION_WIDTH));
        gameButtons[INPUT_RIGHT] = new GameButton(new Square(20 + DIRECTION_WIDTH + 20, INPUTS_HEIGHT, DIRECTION_WIDTH, DIRECTION_WIDTH));
        gameButtons[INPUT_A] = new GameButton(new Square(PLAYING_WIDTH - BUTTONS_WIDTH * 2 - 50, INPUTS_HEIGHT, BUTTONS_WIDTH, BUTTONS_WIDTH));
        gameButtons[INPUT_B] = new GameButton(new Square(PLAYING_WIDTH - BUTTONS_WIDTH - 25, INPUTS_HEIGHT, BUTTONS_WIDTH, BUTTONS_WIDTH));
        mainCharacter = new MainCharacter(ID_GEN.getId(), new Vector2(15,ELEMENTS_HEIGHT), gameButtons[INPUT_LEFT],
                gameButtons[INPUT_RIGHT], gameButtons[INPUT_A], gameButtons[INPUT_B]);
        availableEnemies = new Enemy[MAX_WORLD_OBJECTS];
        enemySpawnInterval = new EmptyEnemy(ID_GEN.getId(), SPAWN_TIME);
        availableEnemies[0] = new Enemy(30,100,new Vector2(150, ELEMENTS_HEIGHT), ID_GEN.getId());
        currentEnemy = availableEnemies[0];
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
    public void update(UpdateInterval interval) {
        if(mainCharacter.alive())
            mainCharacter.update(currentEnemy, interval);

        currentEnemy.update(mainCharacter, interval);
        if(!currentEnemy.alive()){
            if(currentEnemy instanceof EmptyEnemy)
                currentEnemy = availableEnemies[0];
            else {
                currentEnemy = enemySpawnInterval;
                score++;
            }
            currentEnemy.reset();
        }
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
