package com.jgame.util;

/**
 * Created by ej-jose on 17/08/15.
 */
public class GameText {

    private final char[] texto;
    private float x;
    private float y;
    private float size;
    private final SimpleTextureData lettersData;


    public GameText(String texto, float x, float y, float size){
        this.texto = texto.toCharArray();
        this.x = x;
        this.y = y;
        this.size = size;
    }

}
