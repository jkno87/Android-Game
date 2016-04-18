package com.jgame.game;

/**
 * Created by jose on 11/08/15.
 */
public abstract class GameFlow {

    public abstract void handleDrag(float x, float y);
    public abstract void handleDown(float x, float y);
    public abstract void handleUp(float x, float y);
    public abstract void handlePointerUp(float x, float y);
    public abstract void handlePointerDown(float x, float y);
    public abstract void update(float interval);
    public abstract void pause();
    public abstract void resume();
}
