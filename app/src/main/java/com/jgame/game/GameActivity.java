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
import com.jgame.elements.EmptyEnemy;
import com.jgame.elements.Enemy;
import com.jgame.elements.GameButton;
import com.jgame.elements.MainCharacter;
import com.jgame.util.IdGenerator;
import com.jgame.util.LabelButton;
import com.jgame.util.Square;
import com.jgame.util.Vector2;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by jose on 27/01/15.
 */
public class GameActivity extends Activity {

    public enum GameState {
        PLAYING, GAME_OVER
    }

    class GameRunnable implements Runnable {

        public final MainCharacter mainCharacter;
        public final EmptyEnemy enemySpawnInterval;
        public final Enemy[] availableEnemies;
        public int score;
        public GameState currentState;
        private final FightingGameFlow.WorldData worldData;
        private final GameActivity gameActivity;
        private final GameFlow.UpdateInterval updateInterval;

        public GameRunnable(GameActivity activity, GameFlow.UpdateInterval updateInterval, MainCharacter mainCharacter){
            this.gameActivity = activity;
            this.mainCharacter = mainCharacter;
            availableEnemies = new Enemy[MAX_WORLD_OBJECTS];
            enemySpawnInterval = new EmptyEnemy(ID_GEN.getId(), SPAWN_TIME);
            availableEnemies[0] = new Enemy(MainCharacter.SPRITE_LENGTH,MainCharacter.CHARACTER_HEIGHT,
                    MainCharacter.CHARACTER_LENGTH, MainCharacter.CHARACTER_HEIGHT,ELEMENTS_HEIGHT, ID_GEN.getId(), mainCharacter);
            worldData = new FightingGameFlow.WorldData(MIN_X, MAX_X);
            this.updateInterval = updateInterval;
            currentEnemy = enemySpawnInterval;
        }

        public void reset(){
            synchronized (criticalLock){
                score = 0;
                currentEnemy = enemySpawnInterval;
                currentEnemy.reset();
                mainCharacter.reset(INITIAL_CHARACTER_POSITION, ELEMENTS_HEIGHT);
                currentState = GameState.PLAYING;
            }
        }


        @Override
        public void run() {
            try {
                while(true){
                    Thread. sleep(16L);

                    synchronized (criticalLock) {
                        mainCharacter.receiveInput(inputQueue.poll());

                        if (mainCharacter.alive()) {
                            mainCharacter.update(currentEnemy, updateInterval, worldData);
                        } else {
                            currentState = GameState.GAME_OVER;
                            gameActivity.triggerGameOver(score);
                        }

                        currentEnemy.update(mainCharacter, updateInterval, worldData);

                        if (!currentEnemy.alive()) {
                            if (currentEnemy instanceof EmptyEnemy)
                                currentEnemy = availableEnemies[0];
                            else {
                                currentEnemy = enemySpawnInterval;
                                score++;
                            }
                            currentEnemy.reset();

                            if (score > 5)
                                currentEnemy.increaseDifficulty();
                        }
                    }

                }
            } catch (InterruptedException e){

            }

        }
    }


    public static final float MIN_X = 20;
    public static final float MAX_X = GameLevels.FRUSTUM_WIDTH - MIN_X;
    private final float SPAWN_TIME = 1.5f;
    private final int MAX_WORLD_OBJECTS = 6;
    public static final float PLAYING_WIDTH = GameLevels.FRUSTUM_WIDTH;
    public static final float PLAYING_HEIGHT = GameLevels.FRUSTUM_HEIGHT;
    private static final float DIRECTION_WIDTH = 45;
    private static final float BUTTONS_WIDTH = 50;
    private static final float INPUTS_HEIGHT = 15;
    public static final float CONTROLS_HEIGHT = PLAYING_HEIGHT * 0.25f;
    private static final float ELEMENTS_HEIGHT = CONTROLS_HEIGHT + 20;
    private static final float INITIAL_CHARACTER_POSITION = GameLevels.FRUSTUM_WIDTH / 2;
    private static final IdGenerator ID_GEN = new IdGenerator();
    public static final Square INPUT_LEFT_BOUNDS = new Square(20,INPUTS_HEIGHT, DIRECTION_WIDTH, DIRECTION_WIDTH);
    public static final Square INPUT_RIGHT_BOUNDS = new Square(20 + DIRECTION_WIDTH + 20, INPUTS_HEIGHT, DIRECTION_WIDTH, DIRECTION_WIDTH);
    public static final Square INPUT_A_BOUNDS = new Square(PLAYING_WIDTH - BUTTONS_WIDTH * 2 - 50, INPUTS_HEIGHT, BUTTONS_WIDTH, BUTTONS_WIDTH);
    public static final Square INPUT_B_BOUNDS = new Square(PLAYING_WIDTH - BUTTONS_WIDTH - 25, INPUTS_HEIGHT, BUTTONS_WIDTH, BUTTONS_WIDTH);


    public static final String HIGH_SCORE = "highScore";
    private GLSurfaceView gameSurfaceView;
    public SoundManager soundManager;
    public Boolean paused;
    public final LabelButton continueButton = new LabelButton(new Square(GameLevels.FRUSTUM_WIDTH / 2, GameLevels.FRUSTUM_HEIGHT/2, 150, 40), "continue");
    public final LabelButton quitButton = new LabelButton(new Square(GameLevels.FRUSTUM_WIDTH / 2, GameLevels.FRUSTUM_HEIGHT/2 - 100, 150, 40), "quit");
    public int highScore;
    public MainCharacter mainCharacter;
    public Enemy currentEnemy;
    public final Object criticalLock = new Object();
    public final BlockingQueue<ControllerManager.GameInput> inputQueue = new LinkedBlockingQueue<>(5);
    public final ControllerManager controllerManager = new ControllerManager(inputQueue);
    public GameRunnable gameTask;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        soundManager = GameResources.soundManager;
        gameSurfaceView = new GameSurfaceView(this);
        setContentView(gameSurfaceView);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        paused = false;
        SharedPreferences settings = getPreferences(MODE_PRIVATE);
        highScore = settings.getInt(HIGH_SCORE, 0);
        this.mainCharacter = new MainCharacter(ID_GEN.getId(), new Vector2());
        gameTask = new GameRunnable(this, new GameFlow.UpdateInterval(0.015384615f), mainCharacter);
        new Thread(gameTask).start();
        gameTask.reset();
    }

    public void triggerGameOver(int score){
        if(score <= highScore)
            return;

        highScore = score;
        SharedPreferences settings = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(HIGH_SCORE, highScore);

        editor.commit();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e){
        if(!paused)
            return false;

        float x = (e.getX() / (float) gameSurfaceView.getWidth()) * GameLevels.FRUSTUM_WIDTH;
        float y = (((float) gameSurfaceView.getHeight() - e.getY()) / (float) gameSurfaceView.getHeight()) * GameLevels.FRUSTUM_HEIGHT;

        if(continueButton.bounds.contains(x, y)) {
            synchronized (paused) {
                paused = false;
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
        paused = true;
    }

    @Override
    public boolean onKeyDown(int keycode, KeyEvent event){
        if(KeyEvent.KEYCODE_BACK == keycode){
            synchronized (paused){
                paused = !paused;
            }
            return true;
        }

        return false;
    }

}
