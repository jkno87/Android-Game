package com.jgame.util;

/**
 * Objeto que sirve como timer para realizar ciertas operaciones de accum
 * Created by jose on 29/01/15.
 */
public class FrameCounter {

    private int currentFrame;
    public final int totalFrames;

    public FrameCounter(int totalFrames){
        this.totalFrames = totalFrames;
    }

    public boolean completed(){
        return currentFrame == totalFrames;
    }

    public void reset(){
        currentFrame = 0;
    }

    public void updateFrame(){
        currentFrame++;
    }

    public float pctCharged(){
        return (float) currentFrame / totalFrames;
    }

}
