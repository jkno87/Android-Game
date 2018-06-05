package com.jgame.elements;

import com.jgame.util.Drawer.TextureData;

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

    /**
     * Reinicia la animacion hasta el primer frame
     */
    public void reset(){
        currentFrame = 0;
    }

    /**
     * Avanza un frame de animacion
     */
    public void updateFrame(){
        currentFrame++;
        if(loops && completed())
            reset();
    }

    /**
     * Termina la animacion en el momento en que se llama este metodo
     */
    public void terminate(){
        currentFrame = totalFrames;
    }

    /**
     * Determina si la animacion ya se termino
     * @return
     */
    public boolean completed(){
        return currentFrame >= totalFrames;
    }

    /**
     * Regresa el sprite en el que se encuentra la animacion
     * @return
     */
    public TextureData getCurrentSprite(){
        return sprites[currentFrame / framesPerSprite];
    }

}
