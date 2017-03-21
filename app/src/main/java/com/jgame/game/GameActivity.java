package com.jgame.game;

import android.app.Activity;
import android.content.SharedPreferences;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.WindowManager;
import com.jgame.game.GameData.Event;
import com.jgame.elements.EmptyEnemy;
import com.jgame.elements.RobotEnemy;
import com.jgame.elements.GameCharacter;
import com.jgame.elements.MainCharacter;
import com.jgame.util.IdGenerator;
import com.jgame.util.LabelButton;
import com.jgame.util.Square;
import com.jgame.util.Vector2;
import com.jgame.game.GameData.GameState;
import java.util.ArrayDeque;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import com.jgame.util.Decoration;


/**
 * Actividad que se encarga de controlar lo referente al gameplay.
 * Created by jose on 27/01/15.
 */
public class GameActivity extends Activity {

    class GameRunnable implements Runnable {

        private final int QUAKE_FRAMES = 6;
        public final MainCharacter mainCharacter;
        public final GameCharacter enemySpawnInterval;
        public final GameCharacter[] availableEnemies;
        public int score;
        private ControllerManager.GameInput lastInput;
        private int currentEnemyCounter;
        private GameData.GameState currentState;
        private Difficulty initialDifficulty;
        private Difficulty currentDifficulty;
        private Vector2 backgroundModifier;
        private Event lastTriggeredEvent = Event.NONE;
        private int eventFrame = 0;
        private Random r = new Random();

        public GameRunnable(MainCharacter mainCharacter){
            this.mainCharacter = mainCharacter;
            availableEnemies = new GameCharacter[MAX_WORLD_OBJECTS];
            enemySpawnInterval = new EmptyEnemy(ID_GEN.getId(), SPAWN_TIME);
            backgroundModifier = new Vector2();
            //availableEnemies[1] = new TeleportEnemy(TELEPORT_SPRITE_LENGTH, TELEPORT_SPRITE_HEIGHT,
            //        TELEPORT_SPRITE_LENGTH - 50, TELEPORT_SPRITE_HEIGHT,ELEMENTS_HEIGHT, ID_GEN.getId(), mainCharacter);
            //availableEnemies[0] = new ChargingEnemy(CHARGING_SPRITE_LENGTH, MainCharacter.CHARACTER_HEIGHT,
            //        MainCharacter.CHARACTER_LENGTH, MainCharacter.CHARACTER_HEIGHT,ELEMENTS_HEIGHT, ID_GEN.getId(), mainCharacter);
            availableEnemies[0] = new RobotEnemy(TELEPORT_SPRITE_HEIGHT, TELEPORT_SPRITE_HEIGHT,
                    TELEPORT_SPRITE_LENGTH - 50, TELEPORT_SPRITE_HEIGHT, ELEMENTS_HEIGHT, ID_GEN.getId(), mainCharacter);
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
                        currentDifficulty = gameData.currentDifficulty;
                    }

                    if(currentState == GameState.MENU) {
                        if(lastInput == ControllerManager.GameInput.START_GAME)
                            currentState = GameState.STARTING;
                        else if(lastInput == ControllerManager.GameInput.CHANGE_SOUND_STATE) {
                            synchronized (gameData) {
                                gameData.soundEnabled = !gameData.soundEnabled;
                            }
                        } else if(lastInput == ControllerManager.GameInput.DIFFICULTY_EASY){
                            currentDifficulty = Difficulty.EASY;
                            initialDifficulty = currentDifficulty;
                        } else if(lastInput == ControllerManager.GameInput.DIFFICULTY_MEDIUM) {
                            currentDifficulty = Difficulty.MEDIUM;
                            initialDifficulty = currentDifficulty;
                        } else if(lastInput == ControllerManager.GameInput.DIFFICULTY_HARD){
                            currentDifficulty = Difficulty.HARD;
                            initialDifficulty = currentDifficulty;
                        }
                    } else if(currentState == GameState.STARTING) {
                        if(gameData.soundEnabled)
                            soundManager.startMusic();

                        synchronized (enemyLock) {
                            currentEnemy = enemySpawnInterval;
                        }

                        for(GameCharacter gc : availableEnemies)
                            gc.setCurrentDifficulty(currentDifficulty);

                        currentDifficulty = initialDifficulty;
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
                        if(score > EASY_DIFFICULTY_POINTS)
                            currentDifficulty = Difficulty.MEDIUM;

                        if(!mainCharacter.alive()) {
                            currentState = GameState.GAME_OVER;
                        }
                    } else if (currentState == GameState.RESTART_SCREEN) {
                        if(lastInput == ControllerManager.GameInput.START_GAME)
                            currentState = GameState.STARTING;
                        else if(lastInput == ControllerManager.GameInput.QUIT_GAME)
                            finish();
                    }

                    synchronized (gameData){
                        gameData.state = currentState;
                        gameData.currentDifficulty = currentDifficulty;
                        gameData.backgroundModifier.set(backgroundModifier);
                    }

                    if(currentState != GameState.PLAYING && currentState != GameState.RESTART_SCREEN)
                        continue;

                    if(lastInput == null)
                        mainCharacter.receiveInput(controllerManager.checkPressedButtons());
                    else
                        mainCharacter.receiveInput(lastInput);

                    //Se realiza el update del MainCharacter
                    mainCharacter.update(currentEnemy, decorationsBuffer);

                    //Se realiza el update del enemigo
                    synchronized (enemyLock) {
                        lastTriggeredEvent = currentEnemy.update(mainCharacter, decorationsBuffer);
                        if(lastTriggeredEvent == Event.QUAKE)
                            eventFrame = QUAKE_FRAMES;
                    }


                    if (!currentEnemy.alive() && currentState == GameState.PLAYING) {
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
                            if(gameData.soundEnabled)
                                soundManager.playSound(ID_PUNCH);
                        }
                        currentEnemy.setCurrentDifficulty(currentDifficulty);
                        currentEnemy.reset(0,0);
                    }

                    if(eventFrame > 0){
                        backgroundModifier.set(1 - r.nextInt(2) * 2, 0);
                        eventFrame--;
                    }
                }
            } catch (InterruptedException e){

            }

        }
    }


    public static enum Difficulty {
        EASY, MEDIUM, HARD
    }

    public static final long UPDATE_INTERVAL = 16L;
    public static final float FRAMES_PER_SECOND = UPDATE_INTERVAL / 1000L;
    public static final int EASY_DIFFICULTY_POINTS = 4;
    public static final float FRUSTUM_HEIGHT = 320f;
    public static final float FRUSTUM_WIDTH = 480f;
    public static final float MIN_X = 20;
    public static final float MAX_X = FRUSTUM_WIDTH - MIN_X;
    private final float SPAWN_TIME = 1.5f;
    private final int MAX_WORLD_OBJECTS = 1;
    public static final float PLAYING_WIDTH = FRUSTUM_WIDTH;
    public static final float PLAYING_HEIGHT = FRUSTUM_HEIGHT;
    private static final float DIRECTION_WIDTH = 45;
    private static final float INPUT_SOUND_WIDTH = 55;
    private static final float BUTTONS_WIDTH = 50;
    private static final float INPUTS_HEIGHT = 15;
    public static final float CONTROLS_HEIGHT = PLAYING_HEIGHT * 0.25f;
    private static final float ELEMENTS_HEIGHT = CONTROLS_HEIGHT + 10;
    private static final float INITIAL_CHARACTER_POSITION = FRUSTUM_WIDTH / 2;
    private static final IdGenerator ID_GEN = new IdGenerator();
    public static final int TELEPORT_SPRITE_LENGTH = 115;
    public static final int TELEPORT_SPRITE_HEIGHT = 145;
    public static final int CHARGING_SPRITE_LENGTH = 115;
    public static final Square INPUT_SOUND_SPRITE = new Square(PLAYING_WIDTH - 100, PLAYING_HEIGHT - 100, INPUT_SOUND_WIDTH, INPUT_SOUND_WIDTH);
    public static final Square INPUT_SOUND_BOUNDS = new Square(PLAYING_WIDTH - 100, PLAYING_HEIGHT - 130, INPUT_SOUND_WIDTH, INPUT_SOUND_WIDTH + 40);
    public static final Square INPUT_LEFT_BOUNDS = new Square(20,INPUTS_HEIGHT, DIRECTION_WIDTH, DIRECTION_WIDTH);
    public static final Square INPUT_RIGHT_BOUNDS = new Square(20 + DIRECTION_WIDTH + 20, INPUTS_HEIGHT, DIRECTION_WIDTH, DIRECTION_WIDTH);
    public static final Square INPUT_A_BOUNDS = new Square(PLAYING_WIDTH - BUTTONS_WIDTH * 2 - 50, INPUTS_HEIGHT, BUTTONS_WIDTH, BUTTONS_WIDTH);
    public static final Square INPUT_B_BOUNDS = new Square(PLAYING_WIDTH - BUTTONS_WIDTH - 25, INPUTS_HEIGHT, BUTTONS_WIDTH, BUTTONS_WIDTH);
    public final LabelButton continueButton = new LabelButton(new Square(FRUSTUM_WIDTH / 2 - 100, FRUSTUM_HEIGHT/2 + 10, 200, 50), "continue");
    public static final Square QUIT_BOUNDS = new Square(FRUSTUM_WIDTH / 2 - 100, FRUSTUM_HEIGHT/2 - 60, 200, 50);
    public static final Square RESTART_BOUNDS = new Square(FRUSTUM_WIDTH / 2 - 100, FRUSTUM_HEIGHT/2 + 10, 200, 50);
    public static final Square START_BUTTON_BOUNDS = new Square(FRUSTUM_WIDTH/2 - 100, FRUSTUM_HEIGHT - 160, 200, 100);
    public static final Square SOUND_SWITCH = new Square(160, 40, 150, 40);
    public static final Square EASY_DIFF_BOUNDS = new Square(35, FRUSTUM_HEIGHT - 90, 80, 40);
    public static final Square MEDIUM_DIFF_BOUNDS = new Square(35, FRUSTUM_HEIGHT - 140, 80, 40);
    public static final Square HARD_DIFF_BOUNDS = new Square(35, FRUSTUM_HEIGHT - 190, 80, 40);
    public static final String HIGH_SCORE = "highScore";
    public static int ID_PUNCH;
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
    public final ArrayDeque<Decoration> decorationsBuffer = new ArrayDeque<>();
    public GameRunnable gameTask;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        soundManager = GameResources.soundManager;
        ID_PUNCH = soundManager.loadSound(this, R.raw.punch);
        gameSurfaceView = new GameSurfaceView(this);
        setContentView(gameSurfaceView);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        SharedPreferences settings = getPreferences(MODE_PRIVATE);
        gameData.highScore = settings.getInt(HIGH_SCORE, 0);
        gameData.state = GameState.MENU;
        this.mainCharacter = new MainCharacter(ID_GEN.getId(), new Vector2(), MIN_X, MAX_X);
        gameTask = new GameRunnable(mainCharacter);
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

        float x = (e.getX() / (float) gameSurfaceView.getWidth()) * FRUSTUM_WIDTH;
        float y = (((float) gameSurfaceView.getHeight() - e.getY()) / (float) gameSurfaceView.getHeight()) * FRUSTUM_HEIGHT;

        switch(e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (continueButton.bounds.contains(x, y)) {
                    synchronized (gameData) {
                        gameData.paused = false;
                    }
                    if(gameData.paused == false)
                        soundManager.startMusic();

                } else if (quitButton.bounds.contains(x, y))
                    finish();

                break;

            case MotionEvent.ACTION_UP:
                if (INPUT_SOUND_BOUNDS.contains(x, y)) {
                    synchronized (gameData) {
                        gameData.soundEnabled = !gameData.soundEnabled;
                    }
                }
                break;
        }

        return true;
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d("Game", "Resumiendo juego");
        new Thread(soundManager).start();
        soundManager.iniciar();
        gameSurfaceView.onResume();
    }

    @Override
    public void onPause() {
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

            if(gameData.paused)
                soundManager.pauseMusic();
            else
                soundManager.startMusic();


            return true;
        }

        return false;
    }

}
