package com.jgame.game;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class GameSurfaceView extends GLSurfaceView {

    private GameActivity gameActivity;

    public GameSurfaceView(GameActivity context){
        super(context);
        this.gameActivity = context;
        GameRenderer gameRenderer = new GameRenderer(gameActivity);
        setRenderer(gameRenderer);
        gameRenderer.setSurfaceView(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){

            if(gameActivity.isPaused())
                return false;

            float x = (event.getX() / (float) getWidth());
            float y = (((float) getHeight() - event.getY()) / (float) getHeight());

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    gameActivity.getGameFlow().handleDown(x, y);
                    break;

                case MotionEvent.ACTION_MOVE:
                    gameActivity.getGameFlow().handleDrag(x, y);
                    break;
                case MotionEvent.ACTION_UP:
                    gameActivity.getGameFlow().handleUp(x, y);
                    break;

            }

            return true;
    }
}