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
    private final int totalFrames;

    public AnimationData(int frames, boolean loops,TextureData[] sprites){
        totalFrames = frames * sprites.length;
        this.sprites = sprites;
        framesPerSprite = frames;
        currentFrame = 0;
        this.loops = loops;
    }

    public AnimationData(int frames, boolean loops, TextureData sprite){
        totalFrames = frames;
        this.framesPerSprite = frames;
        this.sprites = new TextureData[]{sprite};
        currentFrame = 0;
        this.loops = loops;
    }

    public void updateFrameData(int framesPerSprite){
        this.framesPerSprite = framesPerSprite;
    }

    public void reset(){
        currentFrame = 0;
    }

    public void updateFrame(){
        if(!completed()) {
            currentFrame++;
            if(loops && completed())
                reset();
        }
    }

    public boolean completed(){
        return currentFrame + 1 >= totalFrames;
    }

    public TextureData getCurrentSprite(){
        return sprites[currentFrame / framesPerSprite];
    }

}
