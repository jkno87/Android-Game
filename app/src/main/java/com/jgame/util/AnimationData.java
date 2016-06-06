package com.jgame.util;

import com.jgame.util.TextureDrawer.TextureData;
import com.jgame.game.GameFlow.UpdateInterval;

/**
 * Objeto creado para realizar mejor las transiciones de texturas y representar una animacion.
 * Created by jose on 29/01/15.
 */
public class AnimationData {

    private final TextureData[] animationSprites;
    private final TimeCounter frameDuration;
    private int currentSprite;

    public AnimationData(float frameDuration, TextureData[] animationSprites){
        this.animationSprites = animationSprites;
        this.frameDuration = new TimeCounter(frameDuration);
    }

    /**
     * Regresa la animacion al estado inicial
     */
    public void reset(){
        this.frameDuration.reset();
        currentSprite = 0;
    }

    /**
     * Actualiza el estado de la animacion
     * @param interval intervalo de tiempo transcurrido desde la ultima actualizacion
     */
    public void update(UpdateInterval interval){
        frameDuration.accum(interval);
        if(frameDuration.completed()){
            currentSprite = (currentSprite < animationSprites.length - 1)
                    ? currentSprite + 1 : 0;
            frameDuration.reset();
        }
    }

    /**
     * Regresa el sprite actual en la animacion
     * @return
     */
    public TextureData getCurrentTexture(){
        return animationSprites[currentSprite];
    }

}
