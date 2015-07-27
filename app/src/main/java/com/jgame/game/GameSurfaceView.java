package com.jgame.game;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class GameSurfaceView extends GLSurfaceView {

    GameRenderer gameRenderer;
    GameLogic gameLogic;

    public GameSurfaceView(Context context, GameLogic gameLogic){
        super(context);
        this.gameLogic = gameLogic;
        gameRenderer = new GameRenderer(gameLogic, this);
        setRenderer(gameRenderer);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){

        synchronized(this){

            if(gameLogic.state != GameLogic.GameState.PLAYING)
                return false;

            float x = (event.getX() / (float) getWidth());
            float y = (((float) getHeight() - event.getY()) / (float) getHeight());

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    gameLogic.inputDown(x, y);
                    break;

                case MotionEvent.ACTION_MOVE:
                    gameLogic.drag(x, y);
                    break;
                case MotionEvent.ACTION_UP:
                    gameLogic.release(x, y);
                    break;

            }

            return true;

        }
    }
}