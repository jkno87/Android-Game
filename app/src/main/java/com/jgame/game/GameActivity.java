package com.jgame.game;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

/**
 * Created by jose on 27/01/15.
 */
public class GameActivity extends Activity {

    private GLSurfaceView gameSurfaceView;
    GameLogic gameLogic;
    SoundManager soundManager;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        soundManager = GameResources.soundManager;
        gameLogic = GameResources.gameLogic;
        gameSurfaceView = new GameSurfaceView(this, gameLogic, GameResources.gameRenderer);
        setContentView(gameSurfaceView);
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
        soundManager.terminar();
        gameSurfaceView.onPause();
    }

    /**
     * Maneja el input de up cuando el juego se encuentra en el estado de PAUSED.
     * @param x coord X del evento
     * @param y coord Y del evento
     */
    private void handleUpPaused(float x, float y){
        if(gameLogic.continueButton.size.within(x, y))
            gameLogic.unpause();
        if(gameLogic.quitButton.size.within(x,y)) {
            finish();
        }
    }


    /**
     * Maneja los inputs cuando se encuentra el juego en game over
     * @param x coord X del evento
     * @param y coord Y del evento
     */
    private void handleGameOver(float x, float y){
        if(gameLogic.continueButton.size.within(x, y))
            startActivity(new Intent(this, GameActivity.class));
        if(gameLogic.quitButton.size.within(x, y))
            startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public boolean onTouchEvent(MotionEvent e){
        float x = (e.getX() / (float) gameSurfaceView.getWidth()) * gameLogic.FRUSTUM_WIDTH;
        float y = (((float) gameSurfaceView.getHeight() - e.getY()) / (float) gameSurfaceView.getHeight()) * gameLogic.FRUSTUM_HEIGHT;

        switch (e.getAction()) {
            case MotionEvent.ACTION_UP:
                if(gameLogic.state == GameLogic.GameState.PAUSED)
                    handleUpPaused(x, y);

                if(gameLogic.state == GameLogic.GameState.GAME_OVER && gameLogic.endGameDuration.completed())
                    handleGameOver(x, y);

        }

        return true;
    }

    @Override
    public boolean onKeyDown(int keycode, KeyEvent event){
        gameLogic.pause();
        return true;
    }


}
