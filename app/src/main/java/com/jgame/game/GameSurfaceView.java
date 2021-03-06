package com.jgame.game;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class GameSurfaceView extends GLSurfaceView {

    private GameActivity gameActivity;
    private final GameRenderer gameRenderer;
    private final ControllerManager controllerManager;

    public GameSurfaceView(Context context, AttributeSet attrs){
        super(context, attrs);
        this.gameActivity = (GameActivity) context;
        gameRenderer = new GameRenderer(gameActivity);
        setRenderer(gameRenderer);
        gameRenderer.setSurfaceView(this);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        this.controllerManager = gameActivity.controllerManager;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        if (gameActivity.gameData.paused || gameActivity.gameData.state == GameData.GameState.GAME_OVER){
            return false;
        }

        int action = MotionEventCompat.getActionMasked(event);
        int index = MotionEventCompat.getActionIndex(event);

        float x = (MotionEventCompat.getX(event, index) / (float) getWidth());
        float y = (((float) getHeight() - MotionEventCompat.getY(event, index)) / (float) getHeight());

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                controllerManager.handleDown(x, y);
                break;

            case MotionEvent.ACTION_MOVE:
                controllerManager.handleDrag(x, y);
                break;
            case MotionEvent.ACTION_UP:
                controllerManager.handleUp(x, y);
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                controllerManager.handlePointerDown(x,y);
                break;

            case MotionEvent.ACTION_POINTER_UP:
                controllerManager.handlePointerUp(x,y);
                break;

        }

        return true;
    }
}