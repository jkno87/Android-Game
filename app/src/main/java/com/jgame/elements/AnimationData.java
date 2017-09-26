package com.jgame.elements;

import android.util.Log;

import com.jgame.util.TextureDrawer.TextureData;

/**
 * Clase que sirve para representar la informacion de una animacion en el juego
 * Created by jose on 20/10/16.
 */
public class AnimationData {

    public int framesPerSprite;
    private int currentFrame;
    private TextureData[] sprites;
    private final boolean loops;
    private int totalFrames;

    public AnimationData(int totalFrames, boolean loops,TextureData[] sprites){
        this.totalFrames = totalFrames - 1;
        this.sprites = sprites;
        framesPerSprite = totalFrames / sprites.length;
        currentFrame = 0;
        this.loops = loops;
    }

    public AnimationData(int totalFrames, boolean loops, TextureData sprite){
        this.totalFrames = totalFrames - 1;
        this.framesPerSprite = totalFrames;
        this.sprites = new TextureData[]{sprite};
        currentFrame = 0;
        this.loops = loops;
    }

    public void updateFrameData(int framesPerSprite){
        this.framesPerSprite = framesPerSprite;
        totalFrames = framesPerSprite * sprites.length - 1;
    }

    public void reset(){
        currentFrame = 0;
    }

    public void updateFrame(){
        currentFrame++;
        if(loops && completed())
            reset();
    }

    public boolean completed(){
        return currentFrame >= totalFrames;
    }

    public TextureData getCurrentSprite(){
        return sprites[currentFrame / framesPerSprite];
    }

}
