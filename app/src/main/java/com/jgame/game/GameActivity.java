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
    private SoundManager soundManager;
    private GameFlow gameFlow;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        soundManager = GameResources.soundManager;
        gameFlow = new CharacterSelectFlow(this);
        gameSurfaceView = new GameSurfaceView(this);
        setContentView(gameSurfaceView);
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
}
