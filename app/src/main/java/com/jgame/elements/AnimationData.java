package com.jgame.elements;

import com.jgame.util.TextureDrawer.TextureData;

/**
 * Clase que sirve para representar la informacion de una animacion en el juego
 * Created by jose on 20/10/16.
 */
public class AnimationData {

    public int framesPerSprite;
    private int currentFrame;
    private int currentSprite;
    private TextureData[] sprites;
    private final boolean loops;

    public AnimationData(AnimationData other){
        this.framesPerSprite = other.framesPerSprite;
        currentFrame = 0;
        currentSprite = 0;
        this.sprites = other.sprites;
        this.loops = other.loops;
    }

    public AnimationData(int frames, boolean loops,TextureData[] sprites){
        this.sprites = sprites;
        framesPerSprite = frames;
        currentFrame = 0;
        currentSprite = 0;
        this.loops = loops;
    }

    public AnimationData(int frames, boolean loops, TextureData sprite){
        this.framesPerSprite = frames;
        this.sprites = new TextureData[]{sprite};
        currentFrame = 0;
        currentSprite = 0;
        this.loops = loops;
    }

    public void updateFrameData(int framesPerSprite){
        this.framesPerSprite = framesPerSprite;
    }

    public void reset(){
        currentFrame = 0;
        currentSprite = 0;
    }

    public void updateFrame(){
        if(!completed()) {
            currentFrame += 1;
            if (currentFrame > framesPerSprite) {
                currentSprite++;
                currentFrame = 0;
            }
        }
        if(loops && completed())
            reset();
    }

    public boolean completed(){
        return currentSprite >= sprites.length;
    }

    public TextureData getCurrentSprite(){
        return sprites[currentSprite];
    }

}
