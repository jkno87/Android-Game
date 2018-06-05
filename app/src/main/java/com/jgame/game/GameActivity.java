package com.jgame.game;

import android.app.Activity;
import android.content.SharedPreferences;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.jgame.elements.ChargingEnemy;
import com.jgame.elements.EatingEnemy;
import com.jgame.elements.FireEnemy;
import com.jgame.elements.FireSpaceEnemy;
import com.jgame.elements.PongEnemy;
import com.jgame.elements.ProjectileEnemy;
import com.jgame.game.GameData.Event;
import com.jgame.elements.RobotEnemy;
import com.jgame.elements.GameCharacter;
import com.jgame.elements.MainCharacter;
import com.jgame.util.IdGenerator;
import com.jgame.util.Square;
import com.jgame.util.Drawer;
import com.jgame.util.Vector2;
import com.jgame.game.GameData.GameState;
import java.util.ArrayDeque;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import com.jgame.util.Decoration;
import com.google.firebase.analytics.*;


/**
 * Actividad que se encarga de controlar lo referente al gameplay.
 * Created by jose on 27/01/15.
 */
public class GameActivity extends Activity {

    public enum Difficulty {
        EASY, MEDIUM, HARD
    }

    public static final long UPDATE_INTERVAL = 16L;
    public static final float FRAMES_PER_SECOND = 1000L / UPDATE_INTERVAL;
    public static final int MEDIUM_DIFFICULTY_POINTS = 4;
    public static final int HARD_DIFFICULTY_POINTS = 10;
    public static final float FRUSTUM_HEIGHT = 320f;
    public static final float FRUSTUM_WIDTH = 480f;
    public static final float MIN_X = 20;
    public static final float MAX_X = FRUSTUM_WIDTH - MIN_X;
    public static final float PLAYING_WIDTH = FRUSTUM_WIDTH;
    public static final float PLAYING_HEIGHT = FRUSTUM_HEIGHT;
    private static final float DIRECTION_WIDTH = 65;
    private static final float INPUT_SOUND_WIDTH = 55;
    private static final float BUTTONS_WIDTH = 65;
    private static final float INPUTS_HEIGHT = 5;
    public static final float CONTROLS_HEIGHT = PLAYING_HEIGHT * 0.25f;
    public static final float ELEMENTS_HEIGHT = CONTROLS_HEIGHT + 10;
    private static final IdGenerator ID_GEN = new IdGenerator();
    private static final int OFFSET_ADJ = 8;
    public static final Square FULL_SCREEN_BOUNDS = new Square(-OFFSET_ADJ,0,FRUSTUM_WIDTH + OFFSET_ADJ,FRUSTUM_HEIGHT + OFFSET_ADJ);
    public static final Square INPUT_SOUND_SPRITE = new Square(PLAYING_WIDTH - 100, PLAYING_HEIGHT - 100, INPUT_SOUND_WIDTH, INPUT_SOUND_WIDTH);
    public static final Square INPUT_SOUND_BOUNDS = new Square(PLAYING_WIDTH - 100, PLAYING_HEIGHT - 130, INPUT_SOUND_WIDTH, INPUT_SOUND_WIDTH + 40);
    public static final Square INPUT_LEFT_BOUNDS = new Square(5,INPUTS_HEIGHT, DIRECTION_WIDTH, DIRECTION_WIDTH);
    public static final Square INPUT_RIGHT_BOUNDS = new Square(5 + DIRECTION_WIDTH + 10, INPUTS_HEIGHT, DIRECTION_WIDTH, DIRECTION_WIDTH);
    public static final Square INPUT_A_BOUNDS = new Square(PLAYING_WIDTH - BUTTONS_WIDTH * 2 - 20, INPUTS_HEIGHT, BUTTONS_WIDTH, BUTTONS_WIDTH);
    public static final Square INPUT_B_BOUNDS = new Square(PLAYING_WIDTH - BUTTONS_WIDTH - 25, INPUTS_HEIGHT, BUTTONS_WIDTH, BUTTONS_WIDTH);
    public static final Square CONTINUE_BOUNDS = new Square(FRUSTUM_WIDTH / 2 - 100, FRUSTUM_HEIGHT/2 + 10, 200, 50);
    public static final Square RESTART_BOUNDS = new Square(FRUSTUM_WIDTH / 2 - 100, FRUSTUM_HEIGHT/2 - 40, 200, 50);
    public static final Square QUIT_BOUNDS = new Square(FRUSTUM_WIDTH / 2 - 100, FRUSTUM_HEIGHT/2 - 90, 200, 50);
    public static final Square START_BUTTON_BOUNDS = new Square(FRUSTUM_WIDTH/2 - 100, FRUSTUM_HEIGHT - 160, 200, 100);
    public static final Square SOUND_SWITCH = new Square(160, 40, 150, 40);
    public static final Square EASY_DIFF_BOUNDS = new Square(35, FRUSTUM_HEIGHT - 90, 80, 40);
    public static final Square MEDIUM_DIFF_BOUNDS = new Square(35, FRUSTUM_HEIGHT - 140, 80, 40);
    public static final Square HARD_DIFF_BOUNDS = new Square(35, FRUSTUM_HEIGHT - 190, 80, 40);
    public static final Square RETURN_BUTTON_BOUNDS = new Square(FRUSTUM_WIDTH - 115, FRUSTUM_HEIGHT - 90, 80, 40);
    public static final Square RECORDS_BUTTON_BOUNDS = new Square(FRUSTUM_WIDTH - 115, FRUSTUM_HEIGHT - 90, 80, 40);
    public static final Square HITBOXES_BUTTON_BOUNDS = new Square(FRUSTUM_WIDTH - 115, FRUSTUM_HEIGHT - 190, 80, 40);
    public static final Vector2 ADVANCE_SPEED = new Vector2(-2f, 0);
    public static final String HIGH_SCORE = "highScore";
    public static boolean renderHitboxes = false;
    //public static int ID_PUNCH;
    private GLSurfaceView gameSurfaceView;
    public SoundManager soundManager;
    public final GameData gameData = new GameData();
    public MainCharacter mainCharacter;
    public GameCharacter currentEnemy;
    public final Object enemyLock = new Object();
    public final BlockingQueue<ControllerManager.GameInput> inputQueue = new LinkedBlockingQueue<>(5);
    public final ControllerManager controllerManager = new ControllerManager(inputQueue, gameData);
    public final ArrayDeque<Decoration> decorationsBuffer = new ArrayDeque<>();
    public final Drawer.ColorData transitionOverlay = new Drawer.ColorData(0,0,0,0);
    public GameRunnable gameTask;
    private FirebaseAnalytics mFirebaseAnalytics;
    private InterstitialAd mInterstitialAd;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        soundManager = new SoundManager(this);
        setContentView(R.layout.activity_main);
        gameSurfaceView = (GameSurfaceView) findViewById(R.id.game_surface);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        gameData.state = GameState.LOADING_SCREEN;
        SharedPreferences settings = getPreferences(MODE_PRIVATE);
        mainCharacter = new MainCharacter(ELEMENTS_HEIGHT, MIN_X, MAX_X);
        gameData.highScore = settings.getInt(HIGH_SCORE, 0);
        gameTask = new GameRunnable();
        new Thread(gameTask).start();
    }

    /**
     * Se encarga de verificar el score del jugador y en caso de tener un nuevo highscore lo registra en shared preferences.
     * Tambien lo manda a firebase analytics en caso de que sea un highscore nuevo.
     * @param score
     */
    private void checkHighScore(int score){
        Log.d("game", "Ejecutando proceso de save");
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "NewHighScore");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

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
        if(gameData.state == GameState.GAME_OVER){
            if(mInterstitialAd.isLoaded())
                mInterstitialAd.show();

            synchronized (gameData){
                gameData.state = GameState.RESTART_SCREEN;
            }
            return true;
        }

        float x = (e.getX() / (float) gameSurfaceView.getWidth()) * FRUSTUM_WIDTH;
        float y = (((float) gameSurfaceView.getHeight() - e.getY()) / (float) gameSurfaceView.getHeight()) * FRUSTUM_HEIGHT;

        switch(e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (CONTINUE_BOUNDS.contains(x, y)) {
                    synchronized (gameData) {
                        gameData.paused = false;
                    }
                    if(gameData.soundEnabled && gameData.paused == false)
                        soundManager.startMusic();

                } else if (QUIT_BOUNDS.contains(x, y)) {
                    synchronized (gameData){
                        gameData.state = GameState.TERMINATING;
                    }
                    finish();
                } else if (RESTART_BOUNDS.contains(x,y)){
                    gameTask.startTransition(GameState.MENU);
                    synchronized (gameData){
                        gameData.paused = false;
                    }
                }

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
        if(gameData.state == GameState.PLAYING)
            gameData.paused = true;
    }

    @Override
    public boolean onKeyDown(int keycode, KeyEvent event){
        if(KeyEvent.KEYCODE_BACK == keycode){
            if(gameData.state == GameState.TITLE_SCREEN)
                return false;

            synchronized (gameData){
                gameData.paused = !gameData.paused;
            }


            if(gameData.soundEnabled) {
                if (gameData.paused)
                    soundManager.pauseMusic();
                else
                    soundManager.startMusic();
            }


            return true;
        }

        return false;
    }


    class GameRunnable implements Runnable {

        private final int MAX_WORLD_OBJECTS = 1;
        private final float TRANSITION_FRAMES = 20;
        private ControllerManager.GameInput lastInput;
        private int currentEnemyCounter;
        private GameState currentState;
        private Difficulty initialDifficulty;
        private Difficulty currentDifficulty;
        private boolean backgroundMoving;
        private boolean advancing = false;
        private final Vector2 positionOffset = new Vector2();
        private int transitionFrame;
        private boolean transitioning;
        private GameState stateAfterTransition;
        public final GameCharacter[] availableEnemies;
        public int score;

        public GameRunnable(){
            availableEnemies = new GameCharacter[MAX_WORLD_OBJECTS];
            availableEnemies[0] = new ProjectileEnemy();
           // availableEnemies[1] = new PongEnemy(ELEMENTS_HEIGHT, ID_GEN.getId());
            //availableEnemies[0] = new RobotEnemy(175, 215,
            //        135, 215, ELEMENTS_HEIGHT, ID_GEN.getId());
            //availableEnemies[2] = new ChargingEnemy(ELEMENTS_HEIGHT);
            currentEnemy = availableEnemies[0];
            initialDifficulty = Difficulty.EASY;
        }

        public void startTransition(GameState futureState){
            stateAfterTransition = futureState;
            transitioning = true;
            transitionFrame = 0;
        }

        @Override
        public void run() {
            try {
                synchronized (gameData){
                    gameData.state = GameState.TITLE_SCREEN;
                }

                while(true){
                    Thread. sleep(UPDATE_INTERVAL);
                    lastInput = inputQueue.poll();

                    synchronized (gameData){
                        if(gameData.paused)
                            continue;

                        currentState = gameData.state;
                        currentDifficulty = gameData.currentDifficulty;
                    }

                    //Se realizan los calculos de la transicion
                    if(transitioning){
                        transitionFrame++;
                        transitionOverlay.a = transitionFrame / TRANSITION_FRAMES;
                        if(transitionFrame == TRANSITION_FRAMES) {
                            currentState = stateAfterTransition;
                            transitionFrame = 0;
                            transitionOverlay.a = 0;
                            transitioning = false;
                        } else
                            continue;
                    }

                    if(currentState == GameState.TERMINATING || currentState == GameState.LOADING_SCREEN) {
                        continue;
                    } else if(currentState == GameState.MENU) {
                        if (lastInput == ControllerManager.GameInput.START_GAME)
                            startTransition(GameState.STARTING);
                        else if(lastInput == ControllerManager.GameInput.HITBOXES_SWITCH)
                            renderHitboxes = !renderHitboxes;
                        else if (lastInput == ControllerManager.GameInput.RECORDS_TRIGGER)
                            startTransition(GameState.RECORDS);
                        else if (lastInput == ControllerManager.GameInput.CHANGE_SOUND_STATE) {
                            synchronized (gameData) {
                                gameData.soundEnabled = !gameData.soundEnabled;
                            }
                        } else if (lastInput == ControllerManager.GameInput.DIFFICULTY_EASY) {
                            currentDifficulty = Difficulty.EASY;
                        } else if (lastInput == ControllerManager.GameInput.DIFFICULTY_MEDIUM) {
                            currentDifficulty = Difficulty.MEDIUM;
                        } else if (lastInput == ControllerManager.GameInput.DIFFICULTY_HARD) {
                            currentDifficulty = Difficulty.HARD;
                        }
                        initialDifficulty = currentDifficulty;
                    } else if(currentState == GameState.STARTING) {
                        if(gameData.soundEnabled)
                            soundManager.startMusic();

                        synchronized (enemyLock) {
                            currentEnemy = availableEnemies[0];
                            currentEnemy.setCurrentDifficulty(currentDifficulty);
                        }

                        currentDifficulty = initialDifficulty;
                        positionOffset.set(0,ELEMENTS_HEIGHT);
                        score = 0;
                        mainCharacter.reset(positionOffset);
                        currentEnemyCounter = 0;
                        currentEnemy.reset(positionOffset);
                        currentState = GameState.PLAYING;
                        advancing = false;

                    } else if(currentState == GameState.SAVING) {
                        checkHighScore(score);
                        currentState = GameState.GAME_OVER;
                    } else if(currentState == GameState.GAME_OVER){
                        advancing = false;
                    } else if (currentState == GameState.PLAYING){
                        gameData.score = score;
                        if(score > HARD_DIFFICULTY_POINTS)
                            currentDifficulty = Difficulty.HARD;
                        else if(score > MEDIUM_DIFFICULTY_POINTS)
                            currentDifficulty = Difficulty.MEDIUM;

                        if(!mainCharacter.alive()) {
                            currentState = GameState.SAVING;
                        }
                    } else if (currentState == GameState.RESTART_SCREEN) {
                        if(lastInput == ControllerManager.GameInput.START_GAME)
                            startTransition(GameState.STARTING);
                        else if(lastInput == ControllerManager.GameInput.QUIT_GAME)
                            finish();
                    } else if (currentState == GameState.TITLE_SCREEN){
                        if(lastInput == ControllerManager.GameInput.START_GAME)
                            startTransition(GameState.MENU);
                    } else if (currentState == GameState.RECORDS) {
                        if(lastInput == ControllerManager.GameInput.MAIN_MENU)
                            startTransition(GameState.MENU);
                    }

                    synchronized (gameData){
                        gameData.state = currentState;
                        gameData.currentDifficulty = currentDifficulty;
                        gameData.backgroundMoving = backgroundMoving;
                    }

                    //Se reinicia el modifier de cualquier cambio en el frame anterior.
                    backgroundMoving = false;

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
                        currentEnemy.update(mainCharacter, decorationsBuffer);
                    }


                    if(mainCharacter.state == MainCharacter.CharacterState.ADVANCING){
                        advancing = !(mainCharacter.completedTransition() && currentEnemy.completedTransition());
                        if(advancing) {
                            if(!mainCharacter.completedTransition())
                                mainCharacter.move(ADVANCE_SPEED);
                            if(!currentEnemy.completedTransition())
                                currentEnemy.move(ADVANCE_SPEED);

                        } else {
                            mainCharacter.state = MainCharacter.CharacterState.IDLE;
                        }
                    }

                    //Se realiza el cambio de enemigo en el caso de que el enemigo actual muera
                    if (!currentEnemy.alive() && currentState == GameState.PLAYING) {
                        positionOffset.set(currentEnemy.position);
                        currentEnemyCounter++;
                        if(currentEnemyCounter == availableEnemies.length)
                            currentEnemyCounter = 0;
                        synchronized (enemyLock) {
                            currentEnemy = availableEnemies[currentEnemyCounter];
                        }

                        //if(gameData.soundEnabled)
                        //    soundManager.playSound(ID_PUNCH);
                        //Se agrega un punto y se inicia con la transicion del personaje a la siguiente escena
                        score++;
                        //Se reinicia el enemigo para que se encuentre en su estado inicial en caso de algun cambio
                        currentEnemy.setCurrentDifficulty(currentDifficulty);
                        currentEnemy.reset(positionOffset);
                        mainCharacter.advance();
                    }

                    /*if(eventFrame > 0){
                        backgroundModifier.set(1 - r.nextInt(2) * 2, 0);
                        eventFrame--;
                    }*/
                    //En caso de que el juego este en el estado de advancing se manda el modificador al renderer
                    if(advancing)
                        backgroundMoving = true;
                }
            } catch (InterruptedException e){

            }

        }
    }
}
