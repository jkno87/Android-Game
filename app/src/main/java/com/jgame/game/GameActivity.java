package com.jgame.game;

import android.app.Activity;
import android.content.Intent;
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

/**
 * Created by jose on 27/01/15.
 */
public class GameActivity extends Activity {

    class GameRunnable implements Runnable {

        private final IdGenerator ID_GEN = new IdGenerator();
        public final GameButton[] gameButtons;
        private int mainButtonPressed;
        public final MainCharacter mainCharacter;
        public Enemy currentEnemy;
        public final EmptyEnemy enemySpawnInterval;
        public final Enemy[] availableEnemies;
        public int score;
        public FightingGameFlow.GameState currentState;
        private final FightingGameFlow.WorldData worldData;
        private final GameActivity gameActivity;
        private final int punchSoundId;
        private final int hitSoundId;

        public GameRunnable(GameActivity activity){
            this.gameActivity = activity;
            gameButtons = new GameButton[NUMBER_OF_INPUTS];
            mainButtonPressed = INPUT_NONE;
            gameButtons[INPUT_LEFT] = new GameButton(new Square(20,INPUTS_HEIGHT, DIRECTION_WIDTH, DIRECTION_WIDTH));
            gameButtons[INPUT_RIGHT] = new GameButton(new Square(20 + DIRECTION_WIDTH + 20, INPUTS_HEIGHT, DIRECTION_WIDTH, DIRECTION_WIDTH));
            gameButtons[INPUT_A] = new GameButton(new Square(PLAYING_WIDTH - BUTTONS_WIDTH * 2 - 50, INPUTS_HEIGHT, BUTTONS_WIDTH, BUTTONS_WIDTH));
            gameButtons[INPUT_B] = new GameButton(new Square(PLAYING_WIDTH - BUTTONS_WIDTH - 25, INPUTS_HEIGHT, BUTTONS_WIDTH, BUTTONS_WIDTH));
            mainCharacter = new MainCharacter(ID_GEN.getId(), new Vector2(), gameButtons[INPUT_LEFT],
                    gameButtons[INPUT_RIGHT], gameButtons[INPUT_A], gameButtons[INPUT_B], this);
            availableEnemies = new Enemy[MAX_WORLD_OBJECTS];
            enemySpawnInterval = new EmptyEnemy(ID_GEN.getId(), SPAWN_TIME);
            availableEnemies[0] = new Enemy(MainCharacter.SPRITE_LENGTH,MainCharacter.CHARACTER_HEIGHT,
                    MainCharacter.CHARACTER_LENGTH, MainCharacter.CHARACTER_HEIGHT,ELEMENTS_HEIGHT, ID_GEN.getId(), mainCharacter);
            worldData = new FightingGameFlow.WorldData(MIN_X, MAX_X);
            punchSoundId = activity.soundManager.loadSound(activity, R.raw.punch);
            hitSoundId = activity.soundManager.loadSound(activity, R.raw.sound);
        }


        @Override
        public void run() {
            try {
                while(true){
                    Thread.sleep(16L);
                    if(mainCharacter.alive()) {
                        mainCharacter.update(currentEnemy, interval, worldData);
                    } else {
                        currentState = GameState.GAME_OVER;
                        gameActivity.triggerGameOver(score);
                    }

                    currentEnemy.update(mainCharacter, interval, worldData);
                    if(!currentEnemy.alive()){
                        if(currentEnemy instanceof EmptyEnemy)
                            currentEnemy = availableEnemies[0];
                        else {
                            currentEnemy = enemySpawnInterval;
                            score++;
                            triggerHitSound();
                        }
                        currentEnemy.reset();

                        if(score > 5)
                            currentEnemy.increaseDifficulty();
                    }

                }
            } catch (InterruptedException e){

            }

        }
    }


    public static final float MIN_X = 20;
    public static final float MAX_X = GameLevels.FRUSTUM_WIDTH - MIN_X;
    private final float SPAWN_TIME = 1.5f;
    private final int NUMBER_OF_INPUTS = 4;
    private final int MAX_WORLD_OBJECTS = 6;
    public static final int INPUT_LEFT = 0;
    public static final int INPUT_RIGHT = 1;
    public static final int INPUT_A = 2;
    private static final int INPUT_B = 3;
    private static final int INPUT_NONE = -1;

    public static float PLAYING_WIDTH = GameLevels.FRUSTUM_WIDTH;
    public static float PLAYING_HEIGHT = GameLevels.FRUSTUM_HEIGHT;
    public static final float CONTROLS_HEIGHT = PLAYING_HEIGHT * 0.25f;
    private static final float ELEMENTS_HEIGHT = CONTROLS_HEIGHT + 20;
    private static final float DIRECTION_WIDTH = 45;
    private static final float BUTTONS_WIDTH = 50;
    private static final float INPUTS_HEIGHT = 15;
    private static final float INITIAL_CHARACTER_POSITION = GameLevels.FRUSTUM_WIDTH / 2;



    public static final String HIGH_SCORE = "highScore";
    private GLSurfaceView gameSurfaceView;
    public SoundManager soundManager;
    private GameFlow gameFlow;
    public Boolean paused;
    public final LabelButton continueButton = new LabelButton(new Square(GameLevels.FRUSTUM_WIDTH / 2, GameLevels.FRUSTUM_HEIGHT/2, 150, 40), "continue");
    public final LabelButton quitButton = new LabelButton(new Square(GameLevels.FRUSTUM_WIDTH / 2, GameLevels.FRUSTUM_HEIGHT/2 - 100, 150, 40), "quit");
    public int highScore;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        soundManager = GameResources.soundManager;
        gameFlow = new MenuFlow(this);
        gameSurfaceView = new GameSurfaceView(this);
        setContentView(gameSurfaceView);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        paused = false;
        SharedPreferences settings = getPreferences(MODE_PRIVATE);
        highScore = settings.getInt(HIGH_SCORE, 0);
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

    /**
     * Regresa el GameFlow actual de la actividad.
     * @return
     */
    public GameFlow getGameFlow(){
        return gameFlow;
    }

    /**
     * Asigna un nuevo GameFlow a la actividad
     * @param gameFlow
     */
    public void setGameFlow(GameFlow gameFlow){
        this.gameFlow = gameFlow;
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
        gameFlow.resume();
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.d("Game", "onPause");
        soundManager.terminar();
        gameSurfaceView.onPause();
        gameFlow.pause();
        paused = true;
    }

    @Override
    public boolean onKeyDown(int keycode, KeyEvent event){
        if(KeyEvent.KEYCODE_BACK == keycode){
            gameFlow.pause();
            synchronized (paused){
                paused = !paused;
            }
            return true;
        }

        return false;
    }

}
