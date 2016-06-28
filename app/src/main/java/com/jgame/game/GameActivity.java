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
import com.jgame.util.LabelButton;
import com.jgame.util.Square;

/**
 * Created by jose on 27/01/15.
 */
public class GameActivity extends Activity {

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
