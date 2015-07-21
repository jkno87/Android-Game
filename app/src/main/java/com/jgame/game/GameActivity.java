package com.jgame.game;

import android.app.Activity;
import android.content.Context;
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
        soundManager = new SoundManager(this);
        gameLogic = new GameLogic(soundManager);
        gameSurfaceView = new GameSurfaceView(this, gameLogic);
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

    @Override
    public boolean onTouchEvent(MotionEvent e){
        float x = (e.getX() / (float) gameSurfaceView.getWidth());
        float y = (((float) gameSurfaceView.getHeight() - e.getY()) / (float) gameSurfaceView.getHeight());

        switch (e.getAction()) {
            case MotionEvent.ACTION_UP:
                if(gameLogic.receivePauseEvent(x,y))
                    super.onBackPressed();
                break;

        }

        return true;
    }

    @Override
    public boolean onKeyDown(int keycode, KeyEvent event){
        gameLogic.pause();
        return true;
    }


}
