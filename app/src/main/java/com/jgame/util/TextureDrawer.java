package com.jgame.util;

import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by jose on 3/12/15.
 */
public class TextureDrawer extends Drawer {

    public static class TextureData {
        public final float v1;
        public final float u1;
        public final float v2;
        public final float u2;

        public TextureData(float v1, float u1, float v2, float u2){
            this.v1 = v1;
            this.u1 = u1;
            this.v2 = v2;
            this.u2 = u2;
        }

    }

    private final static int MAX_TEXTURES = 35;

    public TextureDrawer(boolean withColor){
        super(true, withColor, MAX_TEXTURES);
    }

    public void addColoredSquare(Square s, TextureData tData, SimpleDrawer.ColorData cData){
        elementsAdded++;

        verticesBuffer[currentIndex++] = s.position.x;
        verticesBuffer[currentIndex++] = s.position.y;
        verticesBuffer[currentIndex++] = tData.v1;
        verticesBuffer[currentIndex++] = tData.u2;
        verticesBuffer[currentIndex++] = cData.r;
        verticesBuffer[currentIndex++] = cData.g;
        verticesBuffer[currentIndex++] = cData.b;
        verticesBuffer[currentIndex++] = cData.a;

        verticesBuffer[currentIndex++] = s.position.x + s.lenX;
        verticesBuffer[currentIndex++] = s.position.y;
        verticesBuffer[currentIndex++] = tData.v2;
        verticesBuffer[currentIndex++] = tData.u2;
        verticesBuffer[currentIndex++] = cData.r;
        verticesBuffer[currentIndex++] = cData.g;
        verticesBuffer[currentIndex++] = cData.b;
        verticesBuffer[currentIndex++] = cData.a;

        verticesBuffer[currentIndex++] = s.position.x + s.lenX;
        verticesBuffer[currentIndex++] = s.position.y + s.lenY;
        verticesBuffer[currentIndex++] = tData.v2;
        verticesBuffer[currentIndex++] = tData.u1;
        verticesBuffer[currentIndex++] = cData.r;
        verticesBuffer[currentIndex++] = cData.g;
        verticesBuffer[currentIndex++] = cData.b;
        verticesBuffer[currentIndex++] = cData.a;

        verticesBuffer[currentIndex++] = s.position.x;
        verticesBuffer[currentIndex++] = s.position.y + s.lenY;
        verticesBuffer[currentIndex++] = tData.v1;
        verticesBuffer[currentIndex++] = tData.u1;
        verticesBuffer[currentIndex++] = cData.r;
        verticesBuffer[currentIndex++] = cData.g;
        verticesBuffer[currentIndex++] = cData.b;
        verticesBuffer[currentIndex++] = cData.a;
    }

    /**
     * Agrega los vertices de un cuadrado con textura y color
     * @param x coordenada X del centro del cuadrado
     * @param y coordenada Y del centro de cuadrado
     * @param lenX longitud X del rectangulo
     * @param lenY longitud Y del rectangulo
     * @param tdata Informacion de la textura que se utilizara
     * @return Drawer que contiene los vertices del cuadrado
     */
    public void addTexturedSquare(float x, float y, float lenX, float lenY, TextureData tdata){
        elementsAdded++;

        float x2 = x + lenX;
        float y2 = y + lenY;

        verticesBuffer[currentIndex++] = x;
        verticesBuffer[currentIndex++] = y;
        verticesBuffer[currentIndex++] = tdata.v1;
        verticesBuffer[currentIndex++] = tdata.u2;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;

        verticesBuffer[currentIndex++] = x2;
        verticesBuffer[currentIndex++] = y;
        verticesBuffer[currentIndex++] = tdata.v2;
        verticesBuffer[currentIndex++] = tdata.u2;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;

        verticesBuffer[currentIndex++] = x2;
        verticesBuffer[currentIndex++] = y2;
        verticesBuffer[currentIndex++] = tdata.v2;
        verticesBuffer[currentIndex++] = tdata.u1;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;

        verticesBuffer[currentIndex++] = x;
        verticesBuffer[currentIndex++] = y2;
        verticesBuffer[currentIndex++] = tdata.v1;
        verticesBuffer[currentIndex++] = tdata.u1;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;

    }

    /**
     * Agrega los vertices de un cuadrado con textura y color
     * @param tData Informacion de textura que se utilizara
     * @return Drawer que contiene los vertices del cuadrado
     */
    public void addTexturedSquare(Square s, TextureData tData){
        elementsAdded++;

        verticesBuffer[currentIndex++] = s.position.x;
        verticesBuffer[currentIndex++] = s.position.y;
        verticesBuffer[currentIndex++] = tData.v1;
        verticesBuffer[currentIndex++] = tData.u2;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;

        verticesBuffer[currentIndex++] = s.position.x + s.lenX;
        verticesBuffer[currentIndex++] = s.position.y;
        verticesBuffer[currentIndex++] = tData.v2;
        verticesBuffer[currentIndex++] = tData.u2;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;

        verticesBuffer[currentIndex++] = s.position.x + s.lenX;
        verticesBuffer[currentIndex++] = s.position.y + s.lenY;
        verticesBuffer[currentIndex++] = tData.v2;
        verticesBuffer[currentIndex++] = tData.u1;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;

        verticesBuffer[currentIndex++] = s.position.x;
        verticesBuffer[currentIndex++] = s.position.y + s.lenY;
        verticesBuffer[currentIndex++] = tData.v1;
        verticesBuffer[currentIndex++] = tData.u1;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;
    }

    /**
     * Agrega los vertices de un cuadrado con textura y color
     * @param tData Informacion de textura que se utilizara
     * @return Drawer que contiene los vertices del cuadrado
     */
    public void addInvertedTexturedSquare(Square s, TextureData tData){
        elementsAdded++;

        verticesBuffer[currentIndex++] = s.position.x - s.lenX;
        verticesBuffer[currentIndex++] = s.position.y;
        verticesBuffer[currentIndex++] = tData.v2;
        verticesBuffer[currentIndex++] = tData.u2;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;

        verticesBuffer[currentIndex++] = s.position.x;
        verticesBuffer[currentIndex++] = s.position.y;
        verticesBuffer[currentIndex++] = tData.v1;
        verticesBuffer[currentIndex++] = tData.u2;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;

        verticesBuffer[currentIndex++] = s.position.x;
        verticesBuffer[currentIndex++] = s.position.y + s.lenY;
        verticesBuffer[currentIndex++] = tData.v1;
        verticesBuffer[currentIndex++] = tData.u1;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;

        verticesBuffer[currentIndex++] = s.position.x - s.lenX;
        verticesBuffer[currentIndex++] = s.position.y + s.lenY;
        verticesBuffer[currentIndex++] = tData.v2;
        verticesBuffer[currentIndex++] = tData.u1;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;
        verticesBuffer[currentIndex++] = 1;
    }


}
