package com.jgame.game;

/**
 * Created by jose on 11/08/15.
 */
public abstract class GameFlow {

    public static class UpdateInterval {
        public float delta;

        public UpdateInterval(){

        }

        public UpdateInterval(float delta){
            this.delta = delta;
        }
    }

    public abstract void handleDrag(float x, float y);
    public abstract void handleDown(float x, float y);
    public abstract void handleUp(float x, float y);
    public abstract void handlePointerUp(float x, float y);
    public abstract void handlePointerDown(float x, float y);
    public abstract void update(UpdateInterval interval);
    public abstract void pause();
    public abstract void resume();
}
