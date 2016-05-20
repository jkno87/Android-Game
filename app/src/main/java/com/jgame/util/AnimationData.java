package com.jgame.util;

/**
 * Created by jose on 29/01/15.
 */
public class AnimationData extends TextureData {

    private final int animationFrames;
    private int currentFrame;
    private float offsetX;
    private float offsetY;
    private float lengthX;
    private final TimeCounter frameTimer;

    public AnimationData(int animationFrames, float lengthX, float offsetX, float offsetY, float frameInterval){
        this.animationFrames = animationFrames;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.lengthX = lengthX;
        currentFrame = 0;
        frameTimer = new TimeCounter(frameInterval);
    }

    public synchronized void updateFrame(float interval){

        frameTimer.accum(null);
        if(!frameTimer.completed())
            return;

        currentFrame++;
        if(currentFrame >= animationFrames)
            currentFrame = 0;

        frameTimer.reset();
    }

    @Override
    public synchronized float[] getTextCoords() {

        return new float[]{offsetX + lengthX * currentFrame, offsetY + lengthX,
                           offsetX + lengthX * (currentFrame + 1), offsetY + lengthX,
                           offsetX + lengthX * (currentFrame + 1), offsetY,
                           offsetX + lengthX * currentFrame, offsetY};
    }
}
