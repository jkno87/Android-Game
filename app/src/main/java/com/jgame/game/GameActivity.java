package com.jgame.game;

import android.app.Activity;
import android.content.SharedPreferences;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.WindowManager;
import com.jgame.definitions.GameLevels;
import com.jgame.elements.ChargingEnemy;
import com.jgame.elements.EmptyEnemy;
import com.jgame.elements.TeleportEnemy;
import com.jgame.elements.GameCharacter;
import com.jgame.elements.MainCharacter;
import com.jgame.util.IdGenerator;
import com.jgame.util.LabelButton;
import com.jgame.util.Square;
import com.jgame.util.Vector2;
import com.jgame.game.GameData.GameState;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Actividad que se encarga de manejar todo lo referente al gameplay.
 * Created by jose on 27/01/15.
 */
public class GameActivity extends Activity {

    public static class WorldData {
        public float minX;
        public float maxX;

        public WorldData(float minX, float maxX){
            this.minX = minX;
            this.maxX = maxX;
        }

    }

    class GameRunnable implements Runnable {

        private static final long UPDATE_INTERVAL = 16L;
        public final MainCharacter mainCharacter;
        public final GameCharacter enemySpawnInterval;
        public final GameCharacter[] availableEnemies;
        public int score;
        private final WorldData worldData;
        private final GameFlow.UpdateInterval updateInterval;
        private ControllerManager.GameInput lastInput;
        private int currentEnemyCounter;
        private GameData.GameState currentState;

        public GameRunnable(GameFlow.UpdateInterval updateInterval, MainCharacter mainCharacter){
            this.mainCharacter = mainCharacter;
            availableEnemies = new GameCharacter[MAX_WORLD_OBJECTS];
            enemySpawnInterval = new EmptyEnemy(ID_GEN.getId(), SPAWN_TIME);
            availableEnemies[1] = new TeleportEnemy(TELEPORT_SPRITE_LENGTH, TELEPORT_SPRITE_HEIGHT,
                    TELEPORT_SPRITE_LENGTH - 50, TELEPORT_SPRITE_HEIGHT,ELEMENTS_HEIGHT, ID_GEN.getId(), mainCharacter);
            availableEnemies[0] = new ChargingEnemy(CHARGING_SPRITE_LENGTH, MainCharacter.CHARACTER_HEIGHT,
                    MainCharacter.CHARACTER_LENGTH, MainCharacter.CHARACTER_HEIGHT,ELEMENTS_HEIGHT, ID_GEN.getId(), mainCharacter);
            worldData = new WorldData(MIN_X, MAX_X);
            this.updateInterval = updateInterval;
            currentEnemy = enemySpawnInterval;
        }

        @Override
        public void run() {
            try {
                while(true){
                    Thread. sleep(UPDATE_INTERVAL);
                    lastInput = inputQueue.poll();
                    synchronized (gameData){
                        if(gameData.paused)
                            continue;

                        currentState = gameData.state;
                    }

                    if(currentState == GameState.STARTING) {
                        synchronized (enemyLock) {
                            currentEnemy = enemySpawnInterval;
                        }
                        score = 0;
                        currentEnemyCounter = 0;
                        currentEnemy.reset(0,0);
                        mainCharacter.reset(INITIAL_CHARACTER_POSITION, ELEMENTS_HEIGHT);

                        currentState = GameState.PLAYING;

                    } else if(currentState == GameState.GAME_OVER){
                        triggerGameOver(score);
                        currentState = GameState.RESTART_SCREEN;
                    } else if (currentState == GameState.PLAYING){
                        gameData.score = score;
                        if(!mainCharacter.alive()) {
                            currentState = GameState.GAME_OVER;
                        }
                    } else if (currentState == GameState.RESTART_SCREEN) {
                        if(lastInput == ControllerManager.GameInput.RESTART_GAME)
                            currentState = GameState.STARTING;
                        else if(lastInput == ControllerManager.GameInput.QUIT_GAME)
                            finish();
                    }

                    synchronized (gameData){
                        gameData.state = currentState;
                    }

                    if(currentState == GameState.GAME_OVER)
                        continue;

                    if(lastInput == null)
                        mainCharacter.receiveInput(controllerManager.checkPressedButtons());
                    else
                        mainCharacter.receiveInput(lastInput);

                    mainCharacter.update(currentEnemy, updateInterval, worldData);
                    currentEnemy.update(mainCharacter, updateInterval, worldData);


                    if (!currentEnemy.alive()) {
                        if (currentEnemy instanceof EmptyEnemy) {
                            if(currentEnemyCounter == availableEnemies.length)
                                currentEnemyCounter = 0;
                            synchronized (enemyLock) {
                                currentEnemy = availableEnemies[currentEnemyCounter];
                            }
                            currentEnemyCounter++;
                        } else {
                            synchronized (enemyLock) {
                                currentEnemy = enemySpawnInterval;
                            }
                            score++;
                        }
                        currentEnemy.reset(0,0);
                    }
                }
            } catch (InterruptedException e){

            }

        }
    }


    public static final float MIN_X = 20;
    public static final float MAX_X = GameLevels.FRUSTUM_WIDTH - MIN_X;
    private final float SPAWN_TIME = 1.5f;
    private final int MAX_WORLD_OBJECTS = 2;
    public static final float PLAYING_WIDTH = GameLevels.FRUSTUM_WIDTH;
    public static final float PLAYING_HEIGHT = GameLevels.FRUSTUM_HEIGHT;
    private static final float DIRECTION_WIDTH = 45;
    private static final float BUTTONS_WIDTH = 50;
    private static final float INPUTS_HEIGHT = 15;
    public static final float CONTROLS_HEIGHT = PLAYING_HEIGHT * 0.25f;
    private static final float ELEMENTS_HEIGHT = CONTROLS_HEIGHT + 20;
    private static final float INITIAL_CHARACTER_POSITION = GameLevels.FRUSTUM_WIDTH / 2;
    private static final IdGenerator ID_GEN = new IdGenerator();
    public static final int TELEPORT_SPRITE_LENGTH = 115;
    public static final int TELEPORT_SPRITE_HEIGHT = 145;
    public static final int CHARGING_SPRITE_LENGTH = 115;
    public static final Square INPUT_SOUND_BOUNDS = new Square(PLAYING_WIDTH - 85, PLAYING_HEIGHT - 40, DIRECTION_WIDTH, DIRECTION_WIDTH);
    public static final Square INPUT_LEFT_BOUNDS = new Square(20,INPUTS_HEIGHT, DIRECTION_WIDTH, DIRECTION_WIDTH);
    public static final Square INPUT_RIGHT_BOUNDS = new Square(20 + DIRECTION_WIDTH + 20, INPUTS_HEIGHT, DIRECTION_WIDTH, DIRECTION_WIDTH);
    public static final Square INPUT_A_BOUNDS = new Square(PLAYING_WIDTH - BUTTONS_WIDTH * 2 - 50, INPUTS_HEIGHT, BUTTONS_WIDTH, BUTTONS_WIDTH);
    public static final Square INPUT_B_BOUNDS = new Square(PLAYING_WIDTH - BUTTONS_WIDTH - 25, INPUTS_HEIGHT, BUTTONS_WIDTH, BUTTONS_WIDTH);
    public final LabelButton continueButton = new LabelButton(new Square(GameLevels.FRUSTUM_WIDTH / 2 - 50, GameLevels.FRUSTUM_HEIGHT/2, 150, 40), "continue");
    public static final Square QUIT_BOUNDS = new Square(GameLevels.FRUSTUM_WIDTH / 2 - 50, GameLevels.FRUSTUM_HEIGHT/2 - 100, 150, 40);
    public static final Square RESTART_BOUNDS = new Square(GameLevels.FRUSTUM_WIDTH / 2 - 50, GameLevels.FRUSTUM_HEIGHT / 2, 150, 40);
    public static final String HIGH_SCORE = "highScore";
    private GLSurfaceView gameSurfaceView;
    public SoundManager soundManager;
    public final GameData gameData = new GameData();
    public final LabelButton quitButton = new LabelButton(QUIT_BOUNDS, "quit");
    public final LabelButton restartButton = new LabelButton(RESTART_BOUNDS, "restart");
    public MainCharacter mainCharacter;
    public GameCharacter currentEnemy;
    public final Object enemyLock = new Object();
    public final BlockingQueue<ControllerManager.GameInput> inputQueue = new LinkedBlockingQueue<>(5);
    public final ControllerManager controllerManager = new ControllerManager(inputQueue, gameData);
    public GameRunnable gameTask;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        soundManager = GameResources.soundManager;
        gameSurfaceView = new GameSurfaceView(this);
        setContentView(gameSurfaceView);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        SharedPreferences settings = getPreferences(MODE_PRIVATE);
        gameData.highScore = settings.getInt(HIGH_SCORE, 0);
        gameData.state = GameState.STARTING;
        this.mainCharacter = new MainCharacter(ID_GEN.getId(), new Vector2());
        gameTask = new GameRunnable(new GameFlow.UpdateInterval(0.015384615f), mainCharacter);
        new Thread(gameTask).start();
    }

    private void triggerGameOver(int score){
        if (score <= gameData.highScore)
            return;

        gameData.highScore = score;
        SharedPreferences settings = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(HIGH_SCORE, gameData.highScore);

        editor.commit();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e){
        if(!gameData.paused)
            return false;

        float x = (e.getX() / (float) gameSurfaceView.getWidth()) * GameLevels.FRUSTUM_WIDTH;
        float y = (((float) gameSurfaceView.getHeight() - e.getY()) / (float) gameSurfaceView.getHeight()) * GameLevels.FRUSTUM_HEIGHT;

        if(continueButton.bounds.contains(x, y)) {
            synchronized (gameData) {
                gameData.paused = false;
            }
        } else if(quitButton.bounds.contains(x, y))
            finish();

        return true;
    }

    @Override
    public void onResume(){
        super.onResume();
        soundManager.iniciar();
        new Thread(soundManager).start();
        gameSurfaceView.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.d("Game", "onPause");
        soundManager.terminar();
        gameSurfaceView.onPause();
        gameData.paused = true;
    }

    @Override
    public boolean onKeyDown(int keycode, KeyEvent event){
        if(KeyEvent.KEYCODE_BACK == keycode){
            synchronized (gameData){
                gameData.paused = !gameData.paused;
            }
            return true;
        }

        return false;
    }

}
