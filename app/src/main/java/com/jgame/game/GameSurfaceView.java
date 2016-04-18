package com.jgame.game;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class GameSurfaceView extends GLSurfaceView {

    private GameActivity gameActivity;
    private final GameRenderer gameRenderer;

    public GameSurfaceView(GameActivity context){
        super(context);
        this.gameActivity = context;
        gameRenderer = new GameRenderer(gameActivity);
        setRenderer(gameRenderer);
        gameRenderer.setSurfaceView(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        if(gameActivity.isPaused())
            return false;

        int action = MotionEventCompat.getActionMasked(event);
        int index = MotionEventCompat.getActionIndex(event);

        float x = (MotionEventCompat.getX(event, index) / (float) getWidth());
        float y = (((float) getHeight() - MotionEventCompat.getY(event, index)) / (float) getHeight());

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                gameActivity.getGameFlow().handleDown(x, y);
                break;

            case MotionEvent.ACTION_MOVE:
                gameActivity.getGameFlow().handleDrag(x, y);
                break;
            case MotionEvent.ACTION_UP:
                gameActivity.getGameFlow().handleUp(x, y);
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                gameActivity.getGameFlow().handlePointerDown(x,y);
                break;

            case MotionEvent.ACTION_POINTER_UP:
                gameActivity.getGameFlow().handlePointerUp(x,y);
                break;

        }

        return true;
    }
}